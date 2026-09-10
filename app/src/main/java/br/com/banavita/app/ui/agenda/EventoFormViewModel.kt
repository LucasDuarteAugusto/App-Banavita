package br.com.banavita.app.ui.agenda

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.ClienteRepository
import br.com.banavita.app.data.repository.EventoRepository
import br.com.banavita.app.data.repository.FornecedorRepository
import br.com.banavita.app.domain.exception.BanavitaDomainException
import br.com.banavita.app.ui.common.DateTimeInputParser
import br.com.banavita.app.ui.navigation.BanavitaRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class EventoFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val eventoRepository: EventoRepository,
    private val clienteRepository: ClienteRepository,
    private val fornecedorRepository: FornecedorRepository,
) : ViewModel() {

    private val eventoId: UUID? = savedStateHandle.get<String>(BanavitaRoutes.ARG_EVENTO_ID)
        ?.let(UUID::fromString)

    private val _uiState = MutableStateFlow(
        EventoFormUiState(isEditMode = eventoId != null, isLoading = eventoId != null),
    )
    val uiState: StateFlow<EventoFormUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            clienteRepository.observeAll().collect { clientes ->
                _uiState.update { it.copy(clientes = clientes.sortedBy { c -> c.nome.lowercase() }) }
            }
        }
        viewModelScope.launch {
            fornecedorRepository.observeAll().collect { fornecedores ->
                _uiState.update { it.copy(fornecedores = fornecedores.sortedBy { f -> f.nome.lowercase() }) }
            }
        }
        if (eventoId != null) {
            viewModelScope.launch {
                val evento = eventoRepository.getById(eventoId)
                if (evento == null) {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Evento não encontrado.") }
                    return@launch
                }
                _uiState.update {
                    it.copy(
                        titulo = evento.titulo,
                        dataInput = DateTimeInputParser.formatDate(evento.dataHora),
                        horaInput = DateTimeInputParser.formatTime(evento.dataHora),
                        observacao = evento.observacao.orEmpty(),
                        clienteId = evento.clienteId,
                        fornecedorId = evento.fornecedorId,
                        notificar = evento.notificar,
                        antecipacaoMinutos = evento.antecipacaoMinutos,
                        notifTitulo = evento.notifTitulo.orEmpty(),
                        notifCorpo = evento.notifCorpo.orEmpty(),
                        isLoading = false,
                    )
                }
            }
        } else {
            val epochDay = savedStateHandle.get<String>(BanavitaRoutes.ARG_AGENDA_DATE)?.toLongOrNull()
            if (epochDay != null) {
                _uiState.update {
                    it.copy(dataInput = DateTimeInputParser.formatDate(LocalDate.ofEpochDay(epochDay)))
                }
            }
        }
    }

    fun onTitulo(value: String) = _uiState.update { it.copy(titulo = value, errorMessage = null) }
    fun onData(value: String) = _uiState.update { it.copy(dataInput = value, errorMessage = null) }
    fun onHora(value: String) = _uiState.update { it.copy(horaInput = value, errorMessage = null) }
    fun onObservacao(value: String) = _uiState.update { it.copy(observacao = value) }
    fun onCliente(id: UUID?) = _uiState.update { it.copy(clienteId = id, fornecedorId = if (id != null) null else it.fornecedorId) }
    fun onFornecedor(id: UUID?) = _uiState.update { it.copy(fornecedorId = id, clienteId = if (id != null) null else it.clienteId) }
    fun onNotificar(value: Boolean) = _uiState.update { it.copy(notificar = value) }
    fun onAntecipacao(value: Int?) = _uiState.update { it.copy(antecipacaoMinutos = value) }
    fun onNotifTitulo(value: String) = _uiState.update { it.copy(notifTitulo = value) }
    fun onNotifCorpo(value: String) = _uiState.update { it.copy(notifCorpo = value) }
    fun onDeleteClick() = _uiState.update { it.copy(showDeleteDialog = true) }
    fun onDismissDelete() = _uiState.update { it.copy(showDeleteDialog = false) }
    fun save(onSuccess: () -> Unit) {
        saveInternal(onSuccess)
    }

    private fun saveInternal(onSuccess: () -> Unit) {
        val state = _uiState.value
        val dataHora = DateTimeInputParser.parseToEpochMillis(state.dataInput, state.horaInput)
        if (dataHora == null) {
            _uiState.update { it.copy(errorMessage = "Informe data e hora válidas.") }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true, errorMessage = null) }
            runCatching {
                if (eventoId == null) {
                    eventoRepository.create(
                        titulo = state.titulo,
                        dataHora = dataHora,
                        observacao = state.observacao,
                        clienteId = state.clienteId,
                        fornecedorId = state.fornecedorId,
                        notificar = state.notificar,
                        antecipacaoMinutos = state.antecipacaoMinutos,
                        notifTitulo = state.notifTitulo,
                        notifCorpo = state.notifCorpo,
                    )
                } else {
                    eventoRepository.update(
                        id = eventoId,
                        titulo = state.titulo,
                        dataHora = dataHora,
                        observacao = state.observacao,
                        clienteId = state.clienteId,
                        fornecedorId = state.fornecedorId,
                        notificar = state.notificar,
                        antecipacaoMinutos = state.antecipacaoMinutos,
                        notifTitulo = state.notifTitulo,
                        notifCorpo = state.notifCorpo,
                    )
                }
            }.onSuccess {
                _uiState.update { it.copy(isSaving = false) }
                onSuccess()
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isSaving = false,
                        errorMessage = (error as? BanavitaDomainException)?.message ?: error.message,
                    )
                }
            }
        }
    }

    fun delete(onSuccess: () -> Unit) {
        val id = eventoId ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(isDeleting = true) }
            runCatching { eventoRepository.delete(id) }
                .onSuccess {
                    _uiState.update { it.copy(isDeleting = false, showDeleteDialog = false) }
                    onSuccess()
                }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            showDeleteDialog = false,
                            errorMessage = error.message,
                        )
                    }
                }
        }
    }
}
