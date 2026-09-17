package br.com.banavita.app.ui.voice

import br.com.banavita.app.domain.model.Cliente
import br.com.banavita.app.domain.model.Fornecedor
import br.com.banavita.app.domain.model.Produto
import br.com.banavita.app.domain.model.extraction.ExtractionDraft

enum class VoiceCapturePhase {
    IDLE,
    RECORDING,
    PROCESSING,
    REVIEW,
}

data class VoiceCaptureUiState(
    val phase: VoiceCapturePhase = VoiceCapturePhase.IDLE,
    val draft: ExtractionDraft? = null,
    val errorMessage: String? = null,
    val statusMessage: String? = null,
    val elapsedMs: Long = 0L,
    val amplitudeBars: List<Float> = emptyList(),
    // Listas para dropdowns de revisão
    val produtos: List<Produto> = emptyList(),
    val clientes: List<Cliente> = emptyList(),
    val fornecedores: List<Fornecedor> = emptyList(),
)
