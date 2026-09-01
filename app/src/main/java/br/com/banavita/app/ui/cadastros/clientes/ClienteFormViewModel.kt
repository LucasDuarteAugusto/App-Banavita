package br.com.banavita.app.ui.cadastros.clientes

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.ClienteRepository
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

data class ClienteFormUiState(
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
class ClienteFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val clienteRepository: ClienteRepository,
) : ViewModel() {

    private val clienteId: UUID? = savedStateHandle.get<String>(BanavitaRoutes.ARG_CLIENTE_ID)
        ?.let(UUID::fromString)

    private val _uiState = MutableStateFlow(
        ClienteFormUiState(isEditMode = clienteId != null, isLoading = clienteId != null),
    )
    val uiState: StateFlow<ClienteFormUiState> = _uiState.asStateFlow()

    init {
        clienteId?.let { loadCliente(it) }
    }

    private fun loadCliente(id: UUID) {
        viewModelScope.launch {
            runCatching { clienteRepository.getById(id) }
                .onSuccess { cliente ->
                    if (cliente == null) {
                        _uiState.update { it.copy(isLoading = false, errorMessage = "Cliente não encontrado.") }
                    } else {
                        _uiState.update {
                            it.copy(
                                nome = cliente.nome,
                                telefone = cliente.telefone.orEmpty(),
                                isLoading = false,
                            )
                        }
                    }
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Erro ao carregar cliente.")
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
            _uiState.update { it.copy(errorMessage = "Informe o nome do cliente.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            val telefone = state.telefone.trim().ifBlank { null }
            runCatching {
                if (clienteId == null) {
                    clienteRepository.create(nome, telefone)
                } else {
                    clienteRepository.update(clienteId, nome, telefone)
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
                            ?: "Erro ao salvar cliente.",
                    )
                }
            }
        }
    }

    fun delete(onSuccess: () -> Unit) {
        val id = clienteId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true, errorMessage = null) }
            runCatching { clienteRepository.delete(id) }
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
                                ?: "Erro ao excluir cliente.",
                        )
                    }
                }
        }
    }
}
