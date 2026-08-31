package br.com.banavita.app.core.speech

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PtBrPromptHintsTest {

    @Test
    fun `normalize colapsa espacos e aplica correcao caipira comum`() {
        val raw = "  vendeu uma   caxinha de banana  "
        val normalized = PtBrPromptHints.normalizeTranscript(raw)
        assertEquals("vendeu uma caixinha de banana", normalized)
    }

    @Test
    fun `initial prompt menciona vocabulario do negocio`() {
        val prompt = PtBrPromptHints.initialPrompt.lowercase()
        assertTrue(prompt.contains("banana"))
        assertTrue(prompt.contains("venda") || prompt.contains("compra"))
        assertTrue(prompt.contains("são paulo") || prompt.contains("sao paulo") || prompt.contains("paulista") || prompt.contains("brasil"))
    }
}
