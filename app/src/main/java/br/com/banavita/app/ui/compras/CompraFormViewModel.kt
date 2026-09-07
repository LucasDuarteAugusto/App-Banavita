package br.com.banavita.app.ui.compras

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.CompraRepository
import br.com.banavita.app.data.repository.FornecedorRepository
import br.com.banavita.app.data.repository.ProdutoRepository
import br.com.banavita.app.domain.exception.BanavitaDomainException
import br.com.banavita.app.domain.model.DocumentoItemInput
import br.com.banavita.app.ui.common.DateInputParser
import br.com.banavita.app.ui.common.DocumentLineItemFormState
import br.com.banavita.app.ui.common.MoneyInputParser
import br.com.banavita.app.ui.common.QuantityInputParser
import br.com.banavita.app.ui.navigation.BanavitaRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CompraFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val compraRepository: CompraRepository,
    private val fornecedorRepository: FornecedorRepository,
    private val produtoRepository: ProdutoRepository,
) : ViewModel() {

    private val compraId: UUID? = savedStateHandle.get<String>(BanavitaRoutes.ARG_COMPRA_ID)
        ?.let(UUID::fromString)

    private val _uiState = MutableStateFlow(
        CompraFormUiState(isEditMode = compraId != null, isLoading = true),
    )
    val uiState: StateFlow<CompraFormUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            fornecedorRepository.observeAll().collect { fornecedores ->
                _uiState.update {
                    it.copy(fornecedores = fornecedores.sortedBy { f -> f.nome.lowercase() })
                }
            }
        }
        viewModelScope.launch {
            produtoRepository.observeAll().collect { produtos ->
                _uiState.update {
                    it.copy(produtos = produtos.sortedBy { p -> p.descricao.lowercase() })
                }
            }
        }
        if (compraId != null) {
            loadCompra(compraId)
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadCompra(id: UUID) {
        viewModelScope.launch {
            runCatching { compraRepository.getById(id) }
                .onSuccess { compra ->
                    if (compra == null) {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "Compra não encontrada.")
                        }
                        return@launch
                    }
                    val itens = compra.itens.map { item ->
                        DocumentLineItemFormState(
                            produtoId = item.produtoId,
                            quantidadeInput = formatQuantityInput(item.quantidade),
                            precoInput = MoneyInputParser.formatForInput(item.valorUnitarioCentavos),
                        )
                    }
                    _uiState.update {
                        it.copy(
                            dataInput = DateInputParser.formatForInput(compra.data),
                            fornecedorId = compra.fornecedorId,
                            aPrazo = compra.aPrazo,
                            itens = itens.ifEmpty { listOf(DocumentLineItemFormState()) },
                            isLoading = false,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao carregar compra.",
                        )
                    }
                }
        }
    }

    fun onDataChange(value: String) {
        _uiState.update { it.copy(dataInput = value, errorMessage = null) }
    }

    fun onFornecedorSelected(fornecedorId: UUID?) {
        _uiState.update { it.copy(fornecedorId = fornecedorId, errorMessage = null) }
    }

    fun onAPrazoChange(value: Boolean) {
        _uiState.update { it.copy(aPrazo = value, errorMessage = null) }
    }

    fun onPagoChange(isPago: Boolean) {
        _uiState.update { it.copy(aPrazo = !isPago, errorMessage = null) }
    }

    fun onProdutoSelected(index: Int, produtoId: UUID?) {
        val produto = _uiState.value.produtos.find { it.id == produtoId }
        updateItem(index) { item ->
            item.copy(
                produtoId = produtoId,
                precoInput = produto?.let { MoneyInputParser.formatForInput(it.precoCentavos) } ?: "",
            )
        }
    }

    fun onQuantidadeChange(index: Int, value: String) {
        updateItem(index) { it.copy(quantidadeInput = value) }
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onPrecoChange(index: Int, value: String) {
        updateItem(index) { it.copy(precoInput = value) }
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun onAddItem() {
        _uiState.update {
            it.copy(itens = it.itens + DocumentLineItemFormState(), errorMessage = null)
        }
    }

    fun onRemoveItem(index: Int) {
        _uiState.update { state ->
            if (state.itens.size <= 1) return@update state
            state.copy(
                itens = state.itens.filterIndexed { i, _ -> i != index },
                errorMessage = null,
            )
        }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun save(onSuccess: () -> Unit) {
        val state = _uiState.value
        val data = DateInputParser.parseToEpochMillis(state.dataInput)
        if (data == null) {
            _uiState.update { it.copy(errorMessage = "Informe uma data válida (dd/MM/aaaa).") }
            return
        }

        val inputsResult = buildInputs(state)
        if (inputsResult.error != null) {
            _uiState.update { it.copy(errorMessage = inputsResult.error) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                if (compraId == null) {
                    compraRepository.create(data, state.fornecedorId, inputsResult.inputs, state.aPrazo)
                } else {
                    compraRepository.update(compraId, data, state.fornecedorId, inputsResult.inputs)
                }
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = (error as? BanavitaDomainException)?.message
                            ?: error.message
                            ?: "Erro ao salvar compra.",
                    )
                }
            }
        }
    }

    fun delete(onSuccess: () -> Unit) {
        val id = compraId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, errorMessage = null) }
            runCatching { compraRepository.delete(id) }
                .onSuccess {
                    _uiState.update { it.copy(isDeleting = false, showDeleteDialog = false) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            showDeleteDialog = false,
                            errorMessage = (error as? BanavitaDomainException)?.message
                                ?: error.message
                                ?: "Erro ao excluir compra.",
                        )
                    }
                }
        }
    }

    private fun updateItem(index: Int, transform: (DocumentLineItemFormState) -> DocumentLineItemFormState) {
        _uiState.update { state ->
            if (index !in state.itens.indices) return@update state
            val itens = state.itens.toMutableList()
            itens[index] = transform(itens[index])
            state.copy(itens = itens, errorMessage = null)
        }
    }

    private fun buildInputs(state: CompraFormUiState): InputsResult {
        if (state.produtos.isEmpty()) {
            return InputsResult(error = "Cadastre ao menos um produto antes de registrar a compra.")
        }
        if (state.fornecedores.isEmpty()) {
            return InputsResult(error = "Cadastre ao menos um fornecedor antes de registrar a compra.")
        }
        if (state.fornecedorId == null) {
            return InputsResult(error = "Selecione um fornecedor cadastrado.")
        }
        val filled = state.itens.filter {
            it.produtoId != null || it.quantidadeInput.isNotBlank() || it.precoInput.isNotBlank()
        }
        if (filled.isEmpty()) {
            return InputsResult(error = "Adicione ao menos um item à compra.")
        }

        val inputs = mutableListOf<DocumentoItemInput>()
        filled.forEachIndexed { index, item ->
            val produtoId = item.produtoId
                ?: return InputsResult(error = "Selecione o produto do item ${index + 1}.")
            val quantidade = QuantityInputParser.parse(item.quantidadeInput)
                ?: return InputsResult(error = "Informe uma quantidade válida no item ${index + 1}.")
            val precoCentavos = MoneyInputParser.parseToCentavos(item.precoInput)
                ?: return InputsResult(error = "Informe o preço unitário do item ${index + 1}.")
            inputs += DocumentoItemInput(
                produtoId = produtoId,
                quantidade = quantidade,
                valorUnitarioCentavos = precoCentavos,
            )
        }
        return InputsResult(inputs = inputs)
    }

    private fun formatQuantityInput(value: Double): String =
        if (value % 1.0 == 0.0) value.toLong().toString() else value.toString().replace(".", ",")

    private data class InputsResult(
        val inputs: List<DocumentoItemInput> = emptyList(),
        val error: String? = null,
    )
}
