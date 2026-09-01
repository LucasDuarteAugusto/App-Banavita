package br.com.banavita.app.ui.cadastros.fornecedores

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.FornecedorRepository
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

data class FornecedorFormUiState(
    val nome: String = "",
    val telefone: String = "",
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val showDeleteDialog: Boolean = false,
    val isEditMode: Boolean = false,
    val errorMessage: String? = null,
)

@HiltViewModel
class FornecedorFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val fornecedorRepository: FornecedorRepository,
) : ViewModel() {

    private val fornecedorId: UUID? = savedStateHandle.get<String>(BanavitaRoutes.ARG_FORNECEDOR_ID)
        ?.let(UUID::fromString)

    private val _uiState = MutableStateFlow(
        FornecedorFormUiState(isEditMode = fornecedorId != null, isLoading = fornecedorId != null),
    )
    val uiState: StateFlow<FornecedorFormUiState> = _uiState.asStateFlow()

    init {
        fornecedorId?.let { loadFornecedor(it) }
    }

    private fun loadFornecedor(id: UUID) {
        viewModelScope.launch {
            runCatching { fornecedorRepository.getById(id) }
                .onSuccess { fornecedor ->
                    if (fornecedor == null) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Fornecedor não encontrado.") }
                    } else {
                        _uiState.update {
                            it.copy(
                                nome = fornecedor.nome,
                                telefone = fornecedor.telefone.orEmpty(),
                                isLoading = false,
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Erro ao carregar fornecedor.")
                    }
                }
        }
    }

    fun onNomeChange(value: String) {
        _uiState.update { it.copy(nome = value, errorMessage = null) }
    }

    fun onTelefoneChange(value: String) {
        _uiState.update { it.copy(telefone = value, errorMessage = null) }
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
            _uiState.update { it.copy(errorMessage = "Informe o nome do fornecedor.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val telefone = state.telefone.trim().ifBlank { null }
            runCatching {
                if (fornecedorId == null) {
                    fornecedorRepository.create(nome, telefone)
                } else {
                    fornecedorRepository.update(fornecedorId, nome, telefone)
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
                            ?: "Erro ao salvar fornecedor.",
                    )
                }
            }
        }
    }

    fun delete(onSuccess: () -> Unit) {
        val id = fornecedorId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, errorMessage = null) }
            runCatching { fornecedorRepository.delete(id) }
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
                                ?: "Erro ao excluir fornecedor.",
                        )
                    }
                }
        }
    }
}
