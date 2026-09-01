package br.com.banavita.app.ui.cadastros.produtos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.GrupoRepository
import br.com.banavita.app.data.repository.ProdutoRepository
import br.com.banavita.app.domain.exception.BanavitaDomainException
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

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                if (produtoId == null) {
                    produtoRepository.create(descricao, precoCentavos, state.grupoId, unidade)
                } else {
                    produtoRepository.update(produtoId, descricao, precoCentavos, state.grupoId, unidade)
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
