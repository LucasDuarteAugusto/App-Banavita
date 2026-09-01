package br.com.banavita.app.ui.cadastros.fornecedores

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.FornecedorRepository
import br.com.banavita.app.domain.model.Fornecedor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class FornecedorListUiState(
    val items: List<Fornecedor> = emptyList(),
    val isLoading: Boolean = true,
)

@HiltViewModel
class FornecedorListViewModel @Inject constructor(
    fornecedorRepository: FornecedorRepository,
) : ViewModel() {

    val uiState: StateFlow<FornecedorListUiState> = fornecedorRepository.observeAll()
        .map { fornecedores ->
            FornecedorListUiState(items = fornecedores, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = FornecedorListUiState(),
        )
}
