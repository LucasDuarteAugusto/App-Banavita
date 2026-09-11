package br.com.banavita.app.ui.pereciveis.etileno

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.banavita.app.data.repository.LoteRepository
import br.com.banavita.app.domain.exception.BanavitaDomainException
import br.com.banavita.app.domain.model.EtilenoCapacidadeSnapshot
import br.com.banavita.app.domain.model.Lote
import br.com.banavita.app.ui.common.QuantityInputParser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class EtilenoFilaUiState(
    val lotes: List<Lote> = emptyList(),
    val capacidade: EtilenoCapacidadeSnapshot? = null,
    val quantidadePorLote: Map<UUID, String> = emptyMap(),
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val statusMessage: String? = null,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class EtilenoFilaViewModel @Inject constructor(
    private val loteRepository: LoteRepository,
) : ViewModel() {

    private val quantidades = MutableStateFlow<Map<UUID, String>>(emptyMap())
    private val messages = MutableStateFlow(Pair<String?, String?>(null, null))

    val uiState: StateFlow<EtilenoFilaUiState> = combine(
        loteRepository.observeFilaFria().flatMapLatest { lotes ->
            flow { emit(lotes to loteRepository.capacidadeSnapshot()) }
        },
        quantidades,
        messages,
    ) { (lotes, cap), qtde, msg ->
        EtilenoFilaUiState(
            lotes = lotes,
            capacidade = cap,
            quantidadePorLote = qtde,
            isLoading = false,
            errorMessage = msg.first,
            statusMessage = msg.second,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EtilenoFilaUiState())

    fun onQuantidadeChange(loteId: UUID, value: String) {
        quantidades.update { it + (loteId to value) }
        messages.value = null to null
    }

    fun soltar(loteId: UUID) {
        val raw = quantidades.value[loteId]
        val qtde = QuantityInputParser.parse(raw.orEmpty())
        if (qtde == null || qtde <= 0) {
            messages.value = "Informe a quantidade a soltar." to null
            return
        }
        viewModelScope.launch {
            runCatching { loteRepository.transferirParaEtileno(loteId, qtde) }
                .onSuccess {
                    quantidades.update { it - loteId }
                    messages.value = null to "Lote solto para a etileno."
                }
                .onFailure { error ->
                    messages.value = ((error as? BanavitaDomainException)?.message
                        ?: error.message
                        ?: "Não foi possível soltar o lote.") to null
                }
        }
    }
}
