package br.com.banavita.app.ui.cadastros.clientes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.ClienteRepository
import br.com.banavita.app.domain.model.Cliente
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ClienteListUiState(
    val items: List<Cliente> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class ClienteListViewModel @Inject constructor(
    clienteRepository: ClienteRepository,
) : ViewModel() {

    val uiState: StateFlow<ClienteListUiState> = clienteRepository.observeAll()
        .map { clientes ->
            ClienteListUiState(items = clientes, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ClienteListUiState(),
        )
}
