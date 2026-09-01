package br.com.banavita.app.ui.cadastros.grupos

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.GrupoRepository
import br.com.banavita.app.domain.exception.BanavitaDomainException
import br.com.banavita.app.ui.navigation.BanavitaRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class GrupoFormUiState(
    val nome: String = "",
    val observacao: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isEditMode: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class GrupoFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val grupoRepository: GrupoRepository,
) : ViewModel() {

    private val grupoId: UUID? = savedStateHandle.get<String>(BanavitaRoutes.ARG_ENTITY_ID)
        ?.let(UUID::fromString)

    private val _uiState = MutableStateFlow(
        GrupoFormUiState(isEditMode = grupoId != null, isLoading = grupoId != null),
    )
    val uiState: StateFlow<GrupoFormUiState> = _uiState.asStateFlow()

    init {
        grupoId?.let { loadGrupo(it) }
    }

    private fun loadGrupo(id: UUID) {
        viewModelScope.launch {
            runCatching {
                grupoRepository.getById(id)
            }.onSuccess { grupo ->
                if (grupo == null) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Grupo não encontrado.") }
                } else {
                    _uiState.update {
                        it.copy(
                            nome = grupo.nome,
                            observacao = grupo.observacao.orEmpty(),
                            isLoading = false,
                        )
                    }
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = error.message ?: "Erro ao carregar grupo.")
                }
            }
        }
    }

    fun onNomeChange(value: String) {
        _uiState.update { it.copy(nome = value, errorMessage = null) }
    }

    fun onObservacaoChange(value: String) {
        _uiState.update { it.copy(observacao = value, errorMessage = null) }
    }

    fun onDeleteClick() {
        _uiState.update { it.copy(showDeleteDialog = true) }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = false) }
    }

    fun save(onSuccess: () -> Unit) {
        val state = _uiState.value
        val nome = state.nome.trim()
        if (nome.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Informe o nome do grupo.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val observacao = state.observacao.trim().ifBlank { null }
            runCatching {
                if (grupoId == null) {
                    grupoRepository.create(nome, observacao)
                } else {
                    grupoRepository.update(grupoId, nome, observacao)
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
                            ?: "Erro ao salvar grupo.",
                    )
                }
            }
        }
    }

    fun delete(onSuccess: () -> Unit) {
        val id = grupoId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, errorMessage = null) }
            runCatching {
                grupoRepository.delete(id)
            }.onSuccess {
                _uiState.update { it.copy(isDeleting = false, showDeleteDialog = false) }
                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isDeleting = false,
                        showDeleteDialog = false,
                        errorMessage = (error as? BanavitaDomainException)?.message
                            ?: error.message
                            ?: "Erro ao excluir grupo.",
                    )
                }
            }
        }
    }
}
