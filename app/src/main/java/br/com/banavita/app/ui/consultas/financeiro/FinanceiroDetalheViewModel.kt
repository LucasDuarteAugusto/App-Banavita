package br.com.banavita.app.ui.consultas.financeiro

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.ClienteRepository
import br.com.banavita.app.data.repository.FinanceiroRepository
import br.com.banavita.app.data.repository.FornecedorRepository
import br.com.banavita.app.domain.exception.BanavitaDomainException
import br.com.banavita.app.domain.model.DocumentoAberto
import br.com.banavita.app.ui.common.MoneyInputParser
import br.com.banavita.app.ui.navigation.BanavitaRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class FinanceiroDetalheUiState(
    val nome: String = "",
    val documentos: List<DocumentoAberto> = emptyList(),
    val saldoTotal: Long = 0,
    val isLoading: Boolean = true,
    val showBaixaDialog: Boolean = false,
    val valorInput: String = "",
    val documentoAlvo: UUID? = null,
    val isSaving: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class FinanceiroDetalheViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val financeiroRepository: FinanceiroRepository,
    clienteRepository: ClienteRepository,
    fornecedorRepository: FornecedorRepository,
) : ViewModel() {

    private val clienteId: UUID? = savedStateHandle.get<String>(BanavitaRoutes.ARG_CLIENTE_ID)
        ?.let(UUID::fromString)
    private val fornecedorId: UUID? = savedStateHandle.get<String>(BanavitaRoutes.ARG_FORNECEDOR_ID)
        ?.let(UUID::fromString)

    private val dialog = MutableStateFlow(DialogState())

    private val documentos = when {
        clienteId != null -> financeiroRepository.observeDocumentosAbertosCliente(clienteId)
        fornecedorId != null -> financeiroRepository.observeDocumentosAbertosFornecedor(fornecedorId)
        else -> kotlinx.coroutines.flow.flowOf(emptyList())
    }

    val uiState: StateFlow<FinanceiroDetalheUiState> = combine(documentos, dialog) { docs, dlg ->
        val nome = when {
            clienteId != null -> clienteRepository.getById(clienteId)?.nome ?: ""
            fornecedorId != null -> fornecedorRepository.getById(fornecedorId)?.nome ?: ""
            else -> ""
        }
        FinanceiroDetalheUiState(
            nome = nome,
            documentos = docs,
            saldoTotal = docs.sumOf { it.saldoCentavos },
            isLoading = false,
            showBaixaDialog = dlg.visible,
            valorInput = dlg.valor,
            documentoAlvo = dlg.documentoId,
            isSaving = dlg.saving,
            errorMessage = dlg.error,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FinanceiroDetalheUiState())

    fun onBaixaDocumento(documentoId: UUID) {
        val doc = uiState.value.documentos.find { it.documentoId == documentoId } ?: return
        dialog.value = DialogState(
            visible = true,
            documentoId = documentoId,
            valor = MoneyInputParser.formatForInput(doc.saldoCentavos),
        )
    }

    fun onBaixaTotalClick() {
        dialog.value = DialogState(
            visible = true,
            documentoId = null,
            valor = MoneyInputParser.formatForInput(uiState.value.saldoTotal),
        )
    }

    fun onValorChange(value: String) {
        dialog.update { it.copy(valor = value, error = null) }
    }

    fun dismissBaixa() {
        dialog.value = DialogState()
    }

    fun confirmBaixa() {
        val cents = MoneyInputParser.parseToCentavos(dialog.value.valor)
        if (cents == null || cents <= 0) {
            dialog.update { it.copy(error = "Informe um valor válido.") }
            return
        }
        viewModelScope.launch {
            dialog.update { it.copy(saving = true, error = null) }
            runCatching {
                val alvo = dialog.value.documentoId
                when {
                    alvo != null && clienteId != null -> financeiroRepository.registrarRecebimento(alvo, cents)
                    alvo != null && fornecedorId != null -> financeiroRepository.registrarPagamento(alvo, cents)
                    clienteId != null -> financeiroRepository.registrarRecebimentoCliente(clienteId, cents)
                    fornecedorId != null -> financeiroRepository.registrarPagamentoFornecedor(fornecedorId, cents)
                }
            }.onSuccess {
                dialog.value = DialogState()
            }.onFailure { error ->
                dialog.update {
                    it.copy(
                        saving = false,
                        error = (error as? BanavitaDomainException)?.message ?: error.message,
                    )
                }
            }
        }
    }

    private data class DialogState(
        val visible: Boolean = false,
        val documentoId: UUID? = null,
        val valor: String = "",
        val saving: Boolean = false,
        val error: String? = null,
    )
}
