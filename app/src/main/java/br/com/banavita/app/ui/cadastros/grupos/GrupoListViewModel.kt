package br.com.banavita.app.ui.cadastros.grupos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.GrupoRepository
import br.com.banavita.app.domain.model.Grupo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class GrupoListUiState(
    val items: List<Grupo> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class GrupoListViewModel @Inject constructor(
    grupoRepository: GrupoRepository,
) : ViewModel() {

    val uiState: StateFlow<GrupoListUiState> = grupoRepository.observeAll()
        .map { grupos ->
            GrupoListUiState(items = grupos, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = GrupoListUiState(),
        )
}
