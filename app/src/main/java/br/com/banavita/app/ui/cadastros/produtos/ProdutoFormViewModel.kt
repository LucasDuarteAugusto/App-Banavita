package br.com.banavita.app.ui.cadastros.produtos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.GrupoRepository
import br.com.banavita.app.data.repository.ProdutoRepository
import br.com.banavita.app.domain.exception.BanavitaDomainException
import br.com.banavita.app.domain.model.PrecosPorEstado
import br.com.banavita.app.domain.model.RipeningCurve
import br.com.banavita.app.ui.common.MoneyInputParser
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
class ProdutoFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val produtoRepository: ProdutoRepository,
    private val grupoRepository: GrupoRepository,
) : ViewModel() {

    private val produtoId: UUID? = savedStateHandle.get<String>(BanavitaRoutes.ARG_PRODUTO_ID)
        ?.let(UUID::fromString)

    private val _uiState = MutableStateFlow(
        ProdutoFormUiState(isEditMode = produtoId != null, isLoading = true),
    )
    val uiState: StateFlow<ProdutoFormUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            grupoRepository.observeAll().collect { grupos ->
                _uiState.update { it.copy(grupos = grupos.sortedBy { g -> g.nome.lowercase() }) }
            }
        }
        if (produtoId != null) {
            loadProduto(produtoId)
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun loadProduto(id: UUID) {
        viewModelScope.launch {
            runCatching { produtoRepository.getById(id) }
                .onSuccess { produto ->
                    if (produto == null) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Produto não encontrado.") }
                    } else {
                        _uiState.update {
                            it.copy(
                                descricao = produto.descricao,
                                precoInput = MoneyInputParser.formatForInput(produto.precoCentavos),
                                unidade = produto.unidade,
                                grupoId = produto.grupoId,
                                perecivel = produto.perecivel,
                                diasEmMaturacao = produto.curva.diasEmMaturacao.toString(),
                                diasVerde = produto.curva.diasVerde.toString(),
                                diasVerdolenga = produto.curva.diasVerdolenga.toString(),
                                diasAmarela = produto.curva.diasAmarela.toString(),
                                diasPintada = produto.curva.diasPintada.toString(),
                                precoCruaInput = MoneyInputParser.formatForInput(produto.precosEstado.cruaCentavos),
                                precoMaturacaoInput = MoneyInputParser.formatForInput(produto.precosEstado.emMaturacaoCentavos),
                                precoVerdeInput = MoneyInputParser.formatForInput(produto.precosEstado.verdeCentavos),
                                precoVerdolengaInput = MoneyInputParser.formatForInput(produto.precosEstado.verdolengaCentavos),
                                precoAmarelaInput = MoneyInputParser.formatForInput(produto.precosEstado.amarelaCentavos),
                                precoPintadaInput = MoneyInputParser.formatForInput(produto.precosEstado.pintadaCentavos),
                                isLoading = false,
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Erro ao carregar produto.")
                    }
                }
        }
    }

    fun onDescricaoChange(value: String) {
        _uiState.update { it.copy(descricao = value, errorMessage = null) }
    }

    fun onPrecoChange(value: String) {
        _uiState.update { it.copy(precoInput = value, errorMessage = null) }
    }

    fun onUnidadeChange(value: String) {
        _uiState.update { it.copy(unidade = value.uppercase(), errorMessage = null) }
    }

    fun onGrupoSelected(grupoId: UUID?) {
        _uiState.update { it.copy(grupoId = grupoId, errorMessage = null) }
    }

    fun onPerecivelChange(value: Boolean) {
        _uiState.update { state ->
            val tabela = MoneyInputParser.parseToCentavos(state.precoInput)
            val fill = tabela?.let { MoneyInputParser.formatForInput(it) }.orEmpty()
            state.copy(
                perecivel = value,
                errorMessage = null,
                precoCruaInput = if (value && state.precoCruaInput.isBlank()) fill else state.precoCruaInput,
                precoMaturacaoInput = if (value && state.precoMaturacaoInput.isBlank()) fill else state.precoMaturacaoInput,
                precoVerdeInput = if (value && state.precoVerdeInput.isBlank()) fill else state.precoVerdeInput,
                precoVerdolengaInput = if (value && state.precoVerdolengaInput.isBlank()) fill else state.precoVerdolengaInput,
                precoAmarelaInput = if (value && state.precoAmarelaInput.isBlank()) fill else state.precoAmarelaInput,
                precoPintadaInput = if (value && state.precoPintadaInput.isBlank()) fill else state.precoPintadaInput,
            )
        }
    }

    fun onDiasEmMaturacaoChange(value: String) {
        _uiState.update { it.copy(diasEmMaturacao = value, errorMessage = null) }
    }

    fun onDiasVerdeChange(value: String) {
        _uiState.update { it.copy(diasVerde = value, errorMessage = null) }
    }

    fun onDiasVerdolengaChange(value: String) {
        _uiState.update { it.copy(diasVerdolenga = value, errorMessage = null) }
    }

    fun onDiasAmarelaChange(value: String) {
        _uiState.update { it.copy(diasAmarela = value, errorMessage = null) }
    }

    fun onDiasPintadaChange(value: String) {
        _uiState.update { it.copy(diasPintada = value, errorMessage = null) }
    }

    fun onPrecoCruaChange(value: String) {
        _uiState.update { it.copy(precoCruaInput = value, errorMessage = null) }
    }

    fun onPrecoMaturacaoChange(value: String) {
        _uiState.update { it.copy(precoMaturacaoInput = value, errorMessage = null) }
    }

    fun onPrecoVerdeChange(value: String) {
        _uiState.update { it.copy(precoVerdeInput = value, errorMessage = null) }
    }

    fun onPrecoVerdolengaChange(value: String) {
        _uiState.update { it.copy(precoVerdolengaInput = value, errorMessage = null) }
    }

    fun onPrecoAmarelaChange(value: String) {
        _uiState.update { it.copy(precoAmarelaInput = value, errorMessage = null) }
    }

    fun onPrecoPintadaChange(value: String) {
        _uiState.update { it.copy(precoPintadaInput = value, errorMessage = null) }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun save(onSuccess: () -> Unit) {
        val state = _uiState.value
        val descricao = state.descricao.trim()
        if (descricao.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Informe a descrição do produto.") }
            return
        }

        val precoCentavos = MoneyInputParser.parseToCentavos(state.precoInput)
        if (precoCentavos == null || precoCentavos < 0) {
            _uiState.update { it.copy(errorMessage = "Informe um preço válido.") }
            return
        }

        val unidade = state.unidade.trim().uppercase()
        if (unidade.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Informe a unidade do produto.") }
            return
        }

        var curva = RipeningCurve.DEFAULT
        var precosEstado: PrecosPorEstado? = null
        if (state.perecivel) {
            val dMaturacao = state.diasEmMaturacao.trim().toIntOrNull()
            val dVerde = state.diasVerde.trim().toIntOrNull()
            val dVerdolenga = state.diasVerdolenga.trim().toIntOrNull()
            val dAmarela = state.diasAmarela.trim().toIntOrNull()
            val dPintada = state.diasPintada.trim().toIntOrNull()
            if (dMaturacao == null || dVerde == null || dVerdolenga == null || dAmarela == null || dPintada == null) {
                _uiState.update { it.copy(errorMessage = "Informe dias maiores que zero em cada etapa da curva.") }
                return
            }
            curva = RipeningCurve(dMaturacao, dVerde, dVerdolenga, dAmarela, dPintada)
            if (!curva.isValid()) {
                _uiState.update { it.copy(errorMessage = "Informe dias maiores que zero em cada etapa da curva.") }
                return
            }
            val pCrua = MoneyInputParser.parseToCentavos(state.precoCruaInput)
            val pMaturacao = MoneyInputParser.parseToCentavos(state.precoMaturacaoInput)
            val pVerde = MoneyInputParser.parseToCentavos(state.precoVerdeInput)
            val pVerdolenga = MoneyInputParser.parseToCentavos(state.precoVerdolengaInput)
            val pAmarela = MoneyInputParser.parseToCentavos(state.precoAmarelaInput)
            val pPintada = MoneyInputParser.parseToCentavos(state.precoPintadaInput)
            if (pCrua == null || pMaturacao == null || pVerde == null || pVerdolenga == null || pAmarela == null || pPintada == null ||
                pCrua < 0 || pMaturacao < 0 || pVerde < 0 || pVerdolenga < 0 || pAmarela < 0 || pPintada < 0
            ) {
                _uiState.update { it.copy(errorMessage = "Informe um preço válido para cada estado.") }
                return
            }
            precosEstado = PrecosPorEstado(
                cruaCentavos = pCrua,
                emMaturacaoCentavos = pMaturacao,
                verdeCentavos = pVerde,
                verdolengaCentavos = pVerdolenga,
                amarelaCentavos = pAmarela,
                pintadaCentavos = pPintada,
                podreCentavos = 0L,
            )
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                if (produtoId == null) {
                    produtoRepository.create(
                        descricao,
                        precoCentavos,
                        state.grupoId,
                        unidade,
                        state.perecivel,
                        curva,
                        precosEstado,
                    )
                } else {
                    produtoRepository.update(
                        produtoId,
                        descricao,
                        precoCentavos,
                        state.grupoId,
                        unidade,
                        state.perecivel,
                        curva,
                        precosEstado,
                    )
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
                            ?: "Erro ao salvar produto.",
                    )
                }
            }
        }
    }

    fun delete(onSuccess: () -> Unit) {
        val id = produtoId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, errorMessage = null) }
            runCatching { produtoRepository.delete(id) }
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
                                ?: "Erro ao excluir produto.",
                        )
                    }
                }
        }
    }
}
