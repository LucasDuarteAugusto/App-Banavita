package br.com.banavita.app.data.remote.groq

import br.com.banavita.app.core.speech.GroqSpeechConstants
import br.com.banavita.app.core.speech.PtBrPromptHints
import br.com.banavita.app.core.speech.SpeechConstants
import br.com.banavita.app.core.speech.WavWriter
import br.com.banavita.app.domain.exception.SpeechRemoteException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroqWhisperClient @Inject constructor(
    private val httpClient: OkHttpClient,
    private val groqConfig: GroqConfig,
) {
    suspend fun transcribe(
        samples: FloatArray,
        language: String = SpeechConstants.LANGUAGE_PT,
        initialPrompt: String? = PtBrPromptHints.initialPrompt,
    ): String = withContext(Dispatchers.IO) {
        if (!groqConfig.isConfigured()) {
            throw SpeechRemoteException(
                "Chave GROQ_API_KEY ausente. Configure o arquivo .env na raiz do projeto e gere o APK novamente.",
            )
        }

        val wavBytes: ByteArray = WavWriter.floatMonoToWavBytes(samples)
        val audioMediaType: MediaType = "audio/wav".toMediaType()
        val fileBody: RequestBody = wavBytes.toRequestBody(audioMediaType)

        val multipartBuilder = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart("file", "speech.wav", fileBody)
            .addFormDataPart(
                "model",
                groqConfig.modelId.ifBlank { GroqSpeechConstants.MODEL_ID },
            )
            .addFormDataPart("language", language)
            .addFormDataPart("response_format", GroqSpeechConstants.RESPONSE_FORMAT_JSON)

        if (!initialPrompt.isNullOrBlank()) {
            multipartBuilder.addFormDataPart("prompt", initialPrompt)
        }

        val multipart: MultipartBody = multipartBuilder.build()
        val request: Request = Request.Builder()
            .url(GroqSpeechConstants.BASE_URL + GroqSpeechConstants.TRANSCRIPTIONS_PATH)
            .header("Authorization", "Bearer ${groqConfig.apiKey}")
            .post(multipart)
            .build()

        val response: Response = httpClient.newCall(request).execute()
        response.use { resp ->
            val body: String = resp.body?.string().orEmpty()
            if (!resp.isSuccessful) {
                throw SpeechRemoteException(parseErrorMessage(resp.code, body))
            }
            parseTranscriptText(body)
        }
    }

    companion object {
        fun parseTranscriptText(jsonBody: String): String {
            if (jsonBody.isBlank()) {
                throw SpeechRemoteException("Resposta vazia da API Groq")
            }
            val text: String = runCatching {
                JSONObject(jsonBody).optString("text", "")
            }.getOrElse {
                throw SpeechRemoteException("Resposta inválida da API Groq")
            }
            if (text.isBlank()) {
                throw SpeechRemoteException("Transcrição vazia retornada pela API Groq")
            }
            return text
        }

        fun parseErrorMessage(code: Int, body: String): String {
            val detail: String? = runCatching {
                JSONObject(body)
                    .optJSONObject("error")
                    ?.optString("message")
                    ?.takeIf { message -> message.isNotBlank() }
            }.getOrNull()
            return detail ?: "Falha na transcrição remota (HTTP $code)"
        }

        fun createDefaultHttpClient(): OkHttpClient =
            OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build()
    }
}
