package br.com.banavita.app.data.remote.groq

import br.com.banavita.app.domain.exception.SpeechRemoteException
import org.junit.Assert.assertEquals
import org.junit.Test

class GroqWhisperClientTest {
    @Test
    fun `parseTranscriptText extrai campo text`() {
        val text = GroqWhisperClient.parseTranscriptText("""{"text":"vendeu banana"}""")
        assertEquals("vendeu banana", text)
    }

    @Test(expected = SpeechRemoteException::class)
    fun `parseTranscriptText rejeita corpo vazio`() {
        GroqWhisperClient.parseTranscriptText("")
    }

    @Test
    fun `parseErrorMessage usa mensagem da API quando presente`() {
        val message = GroqWhisperClient.parseErrorMessage(
            401,
            """{"error":{"message":"Invalid API Key"}}""",
        )
        assertEquals("Invalid API Key", message)
    }
}
