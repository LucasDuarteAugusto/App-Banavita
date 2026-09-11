package br.com.banavita.app.ui.pereciveis.encomenda

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.ClienteRepository
import br.com.banavita.app.data.repository.EncomendaRepository
import br.com.banavita.app.data.repository.ProdutoRepository
import br.com.banavita.app.domain.exception.BanavitaDomainException
import br.com.banavita.app.domain.exception.InsufficientStockException
import br.com.banavita.app.domain.model.Cliente
import br.com.banavita.app.domain.model.DocumentoItemInput
import br.com.banavita.app.domain.model.EncomendaStatus
import br.com.banavita.app.domain.model.MaturacaoEstado
import br.com.banavita.app.domain.model.Produto
import br.com.banavita.app.ui.common.DateInputParser
import br.com.banavita.app.ui.common.DocumentLineItemFormState
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

data class EncomendaFormUiState(
    val dataInput: String = DateInputParser.todayInput(),
    val clienteId: UUID? = null,
    val clientes: List<Cliente> = emptyList(),
    val produtos: List<Produto> = emptyList(),
    val itens: List<DocumentLineItemFormState> = listOf(DocumentLineItemFormState()),
    val aPrazo: Boolean = false,
    val status: EncomendaStatus = EncomendaStatus.ABERTO,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isAtendendo: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val showParcialDialog: Boolean = false,
    val isEditMode: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class EncomendaFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val encomendaRepository: EncomendaRepository,
    private val clienteRepository: ClienteRepository,
    private val produtoRepository: ProdutoRepository,
) : ViewModel() {

    private val encomendaId: UUID? = savedStateHandle.get<String>(BanavitaRoutes.ARG_ENCOMENDA_ID)
        ?.let(UUID::fromString)

    private val _uiState = MutableStateFlow(
        EncomendaFormUiState(isEditMode = encomendaId != null, isLoading = encomendaId != null),
    )
    val uiState: StateFlow<EncomendaFormUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            clienteRepository.observeAll().collect { clientes ->
                _uiState.update { it.copy(clientes = clientes.sortedBy { c -> c.nome.lowercase() }) }
            }
        }
        viewModelScope.launch {
            produtoRepository.observeAll().collect { produtos ->
                _uiState.update {
                    it.copy(produtos = produtos.filter { p -> p.perecivel }.sortedBy { p -> p.descricao.lowercase() })
                }
            }
        }
        encomendaId?.let { load(it) }
    }

    private fun load(id: UUID) {
        viewModelScope.launch {
            runCatching { encomendaRepository.getById(id) }
                .onSuccess { enc ->
                    if (enc == null) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Encomenda não encontrada.") }
                        return@launch
                    }
                    _uiState.update {
                        it.copy(
                            dataInput = DateInputParser.formatForInput(enc.dataEntrega),
                            clienteId = enc.clienteId,
                            status = enc.status,
                            itens = enc.itens.map { item ->
                                DocumentLineItemFormState(
                                    produtoId = item.produtoId,
                                    quantidadeInput = if (item.quantidade % 1.0 == 0.0) {
                                        item.quantidade.toLong().toString()
                                    } else {
                                        item.quantidade.toString().replace('.', ',')
                                    },
                                    estado = item.estado,
                                )
                            }.ifEmpty { listOf(DocumentLineItemFormState()) },
                            isLoading = false,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Erro ao carregar.")
                    }
                }
        }
    }

    fun onDataChange(value: String) {
        _uiState.update { it.copy(dataInput = value, errorMessage = null) }
    }

    fun onClienteSelected(clienteId: UUID?) {
        val ponto = _uiState.value.clientes.find { it.id == clienteId }?.pontoPreferido
        _uiState.update { state ->
            state.copy(
                clienteId = clienteId,
                itens = state.itens.map { item ->
                    if (item.estado == null && ponto != null) item.copy(estado = ponto) else item
                },
                errorMessage = null,
            )
        }
    }

    fun onAPrazoChange(value: Boolean) {
        _uiState.update { it.copy(aPrazo = value) }
    }

    fun onProdutoSelected(index: Int, produtoId: UUID?) {
        val ponto = _uiState.value.clientes.find { it.id == _uiState.value.clienteId }?.pontoPreferido
        updateItem(index) { it.copy(produtoId = produtoId, estado = ponto) }
    }

    fun onEstadoSelected(index: Int, estado: MaturacaoEstado?) {
        updateItem(index) { it.copy(estado = estado) }
    }

    fun onQuantidadeChange(index: Int, value: String) {
        updateItem(index) { it.copy(quantidadeInput = value) }
    }

    fun onAddItem() {
        _uiState.update { it.copy(itens = it.itens + DocumentLineItemFormState()) }
    }

    fun onRemoveItem(index: Int) {
        _uiState.update { state ->
            if (state.itens.size <= 1) state else state.copy(itens = state.itens.filterIndexed { i, _ -> i != index })
        }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun onDismissParcial() {
        _uiState.update { it.copy(showParcialDialog = false) }
    }

    fun save(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.status != EncomendaStatus.ABERTO && state.isEditMode) {
            _uiState.update { it.copy(errorMessage = "Encomenda já atendida não pode ser alterada.") }
            return
        }
        val data = DateInputParser.parseToEpochMillis(state.dataInput)
        if (data == null) {
            _uiState.update { it.copy(errorMessage = "Informe uma data válida (dd/MM/aaaa).") }
            return
        }
        val clienteId = state.clienteId
        if (clienteId == null) {
            _uiState.update { it.copy(errorMessage = "Selecione um cliente.") }
            return
        }
        val inputs = buildInputs(state) ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                if (encomendaId == null) {
                    encomendaRepository.create(clienteId, data, inputs)
                } else {
                    encomendaRepository.update(encomendaId, clienteId, data, inputs)
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
                            ?: "Erro ao salvar encomenda.",
                    )
                }
            }
        }
    }

    fun atender(onSuccess: () -> Unit, parcial: Boolean = false) {
        val id = encomendaId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isAtendendo = true, showParcialDialog = false, errorMessage = null) }
            runCatching { encomendaRepository.atender(id, _uiState.value.aPrazo, parcial) }
                .onSuccess {
                    _uiState.update { it.copy(isAtendendo = false) }
                    onSuccess()
                }
                .onFailure { error ->
                    if (error is InsufficientStockException && !parcial) {
                        _uiState.update { it.copy(isAtendendo = false, showParcialDialog = true) }
                    } else {
                        _uiState.update {
                            it.copy(
                                isAtendendo = false,
                                errorMessage = (error as? BanavitaDomainException)?.message
                                    ?: error.message
                                    ?: "Não foi possível atender.",
                            )
                        }
                    }
                }
        }
    }

    fun delete(onSuccess: () -> Unit) {
        val id = encomendaId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            runCatching { encomendaRepository.delete(id) }
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
                                ?: "Erro ao excluir.",
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

    private fun buildInputs(state: EncomendaFormUiState): List<DocumentoItemInput>? {
        val filled = state.itens.filter { it.produtoId != null || it.quantidadeInput.isNotBlank() }
        if (filled.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Adicione ao menos um item.") }
            return null
        }
        val inputs = mutableListOf<DocumentoItemInput>()
        filled.forEachIndexed { index, item ->
            val produtoId = item.produtoId
            if (produtoId == null) {
                _uiState.update { it.copy(errorMessage = "Selecione o produto do item ${index + 1}.") }
                return null
            }
            val qtde = QuantityInputParser.parse(item.quantidadeInput)
            if (qtde == null || qtde <= 0) {
                _uiState.update { it.copy(errorMessage = "Informe a quantidade do item ${index + 1}.") }
                return null
            }
            val estado = item.estado
            if (estado == null) {
                _uiState.update { it.copy(errorMessage = "Informe o estado do item ${index + 1}.") }
                return null
            }
            inputs += DocumentoItemInput(produtoId = produtoId, quantidade = qtde, estado = estado)
        }
        return inputs
    }
}
