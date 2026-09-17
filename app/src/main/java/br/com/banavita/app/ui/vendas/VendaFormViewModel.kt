package br.com.banavita.app.ui.vendas

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.ClienteRepository
import br.com.banavita.app.data.repository.EstoqueRepository
import br.com.banavita.app.data.repository.LoteRepository
import br.com.banavita.app.data.repository.ProdutoRepository
import br.com.banavita.app.data.repository.VendaRepository
import br.com.banavita.app.domain.exception.BanavitaDomainException
import br.com.banavita.app.domain.exception.InsufficientStockException
import br.com.banavita.app.domain.model.DocumentoItemInput
import br.com.banavita.app.domain.model.MaturacaoEstado
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
class VendaFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val vendaRepository: VendaRepository,
    private val clienteRepository: ClienteRepository,
    private val produtoRepository: ProdutoRepository,
    private val estoqueRepository: EstoqueRepository,
    private val loteRepository: LoteRepository,
) : ViewModel() {

    private val vendaId: UUID? = savedStateHandle.get<String>(BanavitaRoutes.ARG_VENDA_ID)
        ?.let(UUID::fromString)

    private val _uiState = MutableStateFlow(
        VendaFormUiState(isEditMode = vendaId != null, isLoading = true),
    )
    val uiState: StateFlow<VendaFormUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            clienteRepository.observeAll().collect { clientes ->
                _uiState.update {
                    it.copy(clientes = clientes.sortedBy { c -> c.nome.lowercase() })
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
        if (vendaId != null) {
            loadVenda(vendaId)
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadVenda(id: UUID) {
        viewModelScope.launch {
            runCatching { vendaRepository.getById(id) }
                .onSuccess { venda ->
                    if (venda == null) {
                        _uiState.update {
                            it.copy(isLoading = false, errorMessage = "Venda não encontrada.")
                        }
                        return@launch
                    }
                    val itens = venda.itens.map { item ->
                        DocumentLineItemFormState(
                            produtoId = item.produtoId,
                            quantidadeInput = formatQuantityInput(item.quantidade),
                            precoInput = MoneyInputParser.formatForInput(item.valorUnitarioCentavos),
                            estoqueDisponivel = item.estado?.let { estado ->
                                loteRepository.saldoPorEstado(item.produtoId, estado)
                            } ?: estoqueRepository.getSaldo(item.produtoId),
                            estado = item.estado,
                        )
                    }
                    _uiState.update {
                        it.copy(
                            dataInput = DateInputParser.formatForInput(venda.data),
                            clienteId = venda.clienteId,
                            aPrazo = venda.aPrazo,
                            itens = itens.ifEmpty { listOf(DocumentLineItemFormState()) },
                            isLoading = false,
                        )
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Erro ao carregar venda.",
                        )
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
            val itens = state.itens.map { item ->
                val produto = state.produtos.find { it.id == item.produtoId }
                if (produto?.perecivel == true && item.estado == null && ponto != null) {
                    item.copy(estado = ponto)
                } else {
                    item
                }
            }
            state.copy(clienteId = clienteId, itens = itens, errorMessage = null)
        }
        _uiState.value.itens.forEachIndexed { index, item ->
            if (item.estado != null && item.produtoId != null) {
                refreshItemPricing(index, item.produtoId, item.estado)
            }
        }
    }

    fun onAPrazoChange(value: Boolean) {
        _uiState.update { it.copy(aPrazo = value, errorMessage = null) }
    }

    fun onPagoChange(isPago: Boolean) {
        _uiState.update { it.copy(aPrazo = !isPago, errorMessage = null) }
    }

    fun onProdutoSelected(index: Int, produtoId: UUID?) {
        viewModelScope.launch {
            val state = _uiState.value
            val produto = state.produtos.find { it.id == produtoId }
            val ponto = state.clientes.find { it.id == state.clienteId }?.pontoPreferido
            val estado = if (produto?.perecivel == true) ponto else null
            val saldo = when {
                produtoId == null -> null
                produto?.perecivel == true && estado != null -> loteRepository.saldoPorEstado(produtoId, estado)
                produtoId != null -> estoqueRepository.getSaldo(produtoId)
                else -> null
            }
            val preco = when {
                produto == null -> ""
                produto.perecivel && estado != null ->
                    MoneyInputParser.formatForInput(loteRepository.precoVenda(produto.id, estado))
                else -> MoneyInputParser.formatForInput(produto.precoCentavos)
            }
            updateItem(index) { item ->
                item.copy(
                    produtoId = produtoId,
                    precoInput = preco,
                    estoqueDisponivel = saldo,
                    estado = estado,
                )
            }
        }
    }

    fun onEstadoSelected(index: Int, estado: MaturacaoEstado?) {
        val produtoId = _uiState.value.itens.getOrNull(index)?.produtoId
        updateItem(index) { it.copy(estado = estado) }
        if (produtoId != null && estado != null) {
            refreshItemPricing(index, produtoId, estado)
        }
    }

    private fun refreshItemPricing(index: Int, produtoId: UUID, estado: MaturacaoEstado) {
        viewModelScope.launch {
            val saldo = loteRepository.saldoPorEstado(produtoId, estado)
            val preco = loteRepository.precoVenda(produtoId, estado)
            updateItem(index) { item ->
                item.copy(
                    estoqueDisponivel = saldo,
                    precoInput = MoneyInputParser.formatForInput(preco),
                )
            }
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
                if (vendaId == null) {
                    vendaRepository.create(data, state.clienteId, inputsResult.inputs, state.aPrazo)
                } else {
                    vendaRepository.update(vendaId, data, state.clienteId, inputsResult.inputs)
                }
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isSaving = false, errorMessage = mapError(error, state))
                }
            }
        }
    }

    fun delete(onSuccess: () -> Unit) {
        val id = vendaId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, errorMessage = null) }
            runCatching { vendaRepository.delete(id) }
                .onSuccess {
                    _uiState.update { it.copy(isDeleting = false, showDeleteDialog = false) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            showDeleteDialog = false,
                            errorMessage = mapError(error, _uiState.value),
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

    private fun buildInputs(state: VendaFormUiState): InputsResult {
        if (state.produtos.isEmpty()) {
            return InputsResult(error = "Cadastre ao menos um produto antes de registrar a venda.")
        }
        if (state.clientes.isEmpty()) {
            return InputsResult(error = "Cadastre ao menos um cliente antes de registrar a venda.")
        }
        if (state.clienteId == null) {
            return InputsResult(error = "Selecione um cliente cadastrado.")
        }
        val filled = state.itens.filter {
            it.produtoId != null || it.quantidadeInput.isNotBlank() || it.precoInput.isNotBlank()
        }
        if (filled.isEmpty()) {
            return InputsResult(error = "Adicione ao menos um item à venda.")
        }

        val inputs = mutableListOf<DocumentoItemInput>()
        filled.forEachIndexed { index, item ->
            val produtoId = item.produtoId
                ?: return InputsResult(error = "Selecione o produto do item ${index + 1}.")
            val quantidade = QuantityInputParser.parse(item.quantidadeInput)
                ?: return InputsResult(error = "Informe uma quantidade válida no item ${index + 1}.")
            val precoCentavos = item.precoInput.trim().takeIf { it.isNotEmpty() }
                ?.let { MoneyInputParser.parseToCentavos(it) }
            if (item.precoInput.trim().isNotEmpty() && precoCentavos == null) {
                return InputsResult(error = "Informe um preço válido no item ${index + 1}.")
            }
            val produto = state.produtos.find { it.id == produtoId }
            if (produto?.perecivel == true && item.estado == null) {
                return InputsResult(error = "Informe o estado da banana no item ${index + 1}.")
            }
            if (produto?.perecivel == true && item.estado == MaturacaoEstado.PODRE) {
                return InputsResult(error = "Podre não se vende. Lance como perda.")
            }
            inputs += DocumentoItemInput(
                produtoId = produtoId,
                quantidade = quantidade,
                valorUnitarioCentavos = precoCentavos,
                estado = item.estado,
                unidade = produto?.unidade,
            )
        }
        return InputsResult(inputs = inputs)
    }

    private fun mapError(error: Throwable, state: VendaFormUiState): String {
        if (error is InsufficientStockException) {
            val produto = state.produtos.find { it.id.toString() == error.produtoId }
            val nome = produto?.descricao ?: error.produtoId
            return "Estoque insuficiente para \"$nome\". Disponível: ${error.disponivel}, solicitado: ${error.solicitado}."
        }
        return (error as? BanavitaDomainException)?.message
            ?: error.message
            ?: "Erro ao salvar venda."
    }

    private fun formatQuantityInput(value: Double): String =
        if (value % 1.0 == 0.0) value.toLong().toString() else value.toString().replace(".", ",")

    private data class InputsResult(
        val inputs: List<DocumentoItemInput> = emptyList(),
        val error: String? = null,
    )
}
