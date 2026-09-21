package br.com.banavita.app.ui.voice

import br.com.banavita.app.R
import br.com.banavita.app.domain.model.extraction.VoiceScreenContext

data class VoicePatternInfo(
    val pattern: String,
    val example: String,
    val hintRes: Int
)

object VoiceHints {
    fun getInfo(context: VoiceScreenContext): VoicePatternInfo = when (context) {
        VoiceScreenContext.CLIENTE -> VoicePatternInfo(
            pattern = "[nome] [telefone]",
            example = "Armando 14936586721",
            hintRes = R.string.voice_hint_cliente
        )
        VoiceScreenContext.FORNECEDOR -> VoicePatternInfo(
            pattern = "[nome] [telefone]",
            example = "Sítio Verde 14936586721",
            hintRes = R.string.voice_hint_fornecedor
        )
        VoiceScreenContext.PRODUTO -> VoicePatternInfo(
            pattern = "[nome] [preço] [unidade]",
            example = "Nanica 8 reais quilo",
            hintRes = R.string.voice_hint_produto
        )
        VoiceScreenContext.GRUPO -> VoicePatternInfo(
            pattern = "[nome] obs [texto]",
            example = "Banana obs frutas",
            hintRes = R.string.voice_hint_grupo
        )
        VoiceScreenContext.VENDA -> VoicePatternInfo(
            pattern = "[qtde] [unidade] [produto] [estado] pra [cliente] [preço] [fiado|pago]",
            example = "20 quilos nanica amarela pra padaria 8 reais fiado",
            hintRes = R.string.voice_hint_venda
        )
        VoiceScreenContext.COMPRA -> VoicePatternInfo(
            pattern = "[qtde] [unidade] [produto] do [fornecedor] [preço] [prazo]",
            example = "10 caixas nanica do Sítio Verde 6 reais",
            hintRes = R.string.voice_hint_compra
        )
        VoiceScreenContext.CONSULTAS -> VoicePatternInfo(
            pattern = "[modo] [período] [produto]",
            example = "estoque nanica",
            hintRes = R.string.voice_hint_consultas
        )
        VoiceScreenContext.RECEBER -> VoicePatternInfo(
            pattern = "[valor] reais da [cliente]",
            example = "50 reais da Maria",
            hintRes = R.string.voice_hint_receber
        )
        VoiceScreenContext.PAGAR -> VoicePatternInfo(
            pattern = "[valor] reais do [fornecedor]",
            example = "200 reais do Sítio Verde",
            hintRes = R.string.voice_hint_pagar
        )
        VoiceScreenContext.AGENDA -> VoicePatternInfo(
            pattern = "[título] [dia] às [hora] me avisa [tempo]",
            example = "visita João amanhã às 15 me avisa 1 hora",
            hintRes = R.string.voice_hint_agenda
        )
        VoiceScreenContext.INICIO -> VoicePatternInfo(
            pattern = "[ação] ...",
            example = "vendas hoje",
            hintRes = R.string.voice_hint_inicio
        )
    }
}
