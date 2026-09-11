package br.com.banavita.app.data.repository

import br.com.banavita.app.core.speech.GroqSpeechConstants
import br.com.banavita.app.core.speech.PcmConverter
import br.com.banavita.app.core.speech.PtBrPromptHints
import br.com.banavita.app.core.speech.SilenceTrimmer
import br.com.banavita.app.core.speech.SpeechConstants
import br.com.banavita.app.core.speech.SpeechModelManager
import br.com.banavita.app.core.speech.SttDiagnostics
import br.com.banavita.app.core.speech.TrimResult
import br.com.banavita.app.core.speech.WavReader
import br.com.banavita.app.core.speech.WhisperDecodeParams
import br.com.banavita.app.core.speech.WhisperEngine
import br.com.banavita.app.data.remote.groq.GroqWhisperClient
import br.com.banavita.app.domain.exception.SpeechAudioException
import br.com.banavita.app.domain.exception.SpeechModelException
import br.com.banavita.app.domain.exception.SpeechRemoteException
import br.com.banavita.app.domain.model.ModelStatus
import br.com.banavita.app.domain.model.TranscriptionProvider
import br.com.banavita.app.domain.model.TranscriptionResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.system.measureTimeMillis

@Singleton
class SpeechToTextRepository @Inject constructor(
    private val speechModelManager: SpeechModelManager,
    private val whisperEngine: WhisperEngine,
    private val groqWhisperClient: GroqWhisperClient,
    private val appSettingsRepository: AppSettingsRepository,
) {
    private val prepareMutex = Mutex()

    fun currentProvider(): TranscriptionProvider =
        appSettingsRepository.getTranscriptionProvider()

    fun ensureModelReady(): ModelStatus =
        when (currentProvider()) {
            TranscriptionProvider.LOCAL -> speechModelManager.ensureModelReady()
            TranscriptionProvider.GROQ -> {
                if (appSettingsRepository.isGroqApiKeyConfigured()) {
                    ModelStatus.READY
                } else {
                    ModelStatus.MISSING
                }
            }
        }

    suspend fun prepareEngine() = prepareMutex.withLock {
        when (currentProvider()) {
            TranscriptionProvider.GROQ -> {
                if (!appSettingsRepository.isGroqApiKeyConfigured()) {
                    throw SpeechRemoteException(
                        "Chave GROQ_API_KEY ausente. Configure o .env e gere o APK novamente, " +
                            "ou escolha o modelo local em Configurações.",
                    )
                }
            }
            TranscriptionProvider.LOCAL -> withContext(Dispatchers.IO) {
                if (whisperEngine.isLoaded) return@withContext
                val status = speechModelManager.ensureModelReady()
                if (status != ModelStatus.READY) {
                    throw SpeechModelException(
                        "Modelo ASR indisponível ($status). " +
                            "Reinstale o app ou gere o APK com o modelo embutido " +
                            "(${speechModelManager.activeSpec().fileName}).",
                    )
                }
                val path = speechModelManager.modelPathOrNull()
                    ?: throw SpeechModelException("Caminho do modelo não encontrado")
                whisperEngine.load(path)
            }
        }
    }

    suspend fun transcribe(pcm16kMono: FloatArray): TranscriptionResult {
        if (pcm16kMono.isEmpty()) {
            throw SpeechAudioException("Áudio vazio")
        }
        val provider = currentProvider()
        val loadMs = measureTimeMillis { prepareEngine() }

        lateinit var trimmed: TrimResult
        val preprocessMs = measureTimeMillis {
            trimmed = SilenceTrimmer.trim(PcmConverter.clampToMaxDuration(pcm16kMono))
        }
        if (trimmed.samples.isEmpty()) {
            throw SpeechAudioException("Áudio vazio")
        }

        lateinit var rawText: String
        val inferenceMs = measureTimeMillis {
            rawText = when (provider) {
                TranscriptionProvider.LOCAL -> whisperEngine.transcribe(
                    samples = trimmed.samples,
                    language = SpeechConstants.LANGUAGE_PT,
                    initialPrompt = PtBrPromptHints.initialPrompt,
                )
                TranscriptionProvider.GROQ -> groqWhisperClient.transcribe(
                    samples = trimmed.samples,
                    language = SpeechConstants.LANGUAGE_PT,
                    initialPrompt = PtBrPromptHints.initialPrompt,
                )
            }
        }
        lateinit var text: String
        val postprocessMs = measureTimeMillis {
            text = PtBrPromptHints.normalizeTranscript(rawText)
        }
        val totalMs = loadMs + preprocessMs + inferenceMs + postprocessMs
        val threads = if (provider == TranscriptionProvider.LOCAL) {
            WhisperDecodeParams.threadCount()
        } else {
            0
        }
        SttDiagnostics.logTiming(
            modelId = modelIdFor(provider),
            threads = threads,
            audioSeconds = trimmed.originalSeconds,
            speechSeconds = trimmed.speechSeconds,
            loadMs = loadMs,
            preprocessMs = preprocessMs,
            inferenceMs = inferenceMs,
            postprocessMs = postprocessMs,
            totalMs = totalMs,
            maskedResult = SttDiagnostics.maskSensitive(text),
        )
        return TranscriptionResult(
            text = text,
            language = SpeechConstants.LANGUAGE_PT,
            durationMillis = totalMs,
            modelId = modelIdFor(provider),
        )
    }

    suspend fun transcribeWav(file: File): TranscriptionResult {
        if (!file.exists()) {
            throw SpeechAudioException("Arquivo WAV não encontrado: ${file.absolutePath}")
        }
        val samples = WavReader.readPcm16MonoAsFloat(file)
        return transcribe(samples)
    }

    fun release() {
        whisperEngine.release()
    }

    private fun modelIdFor(provider: TranscriptionProvider): String =
        when (provider) {
            TranscriptionProvider.LOCAL -> speechModelManager.activeSpec().id
            TranscriptionProvider.GROQ -> GroqSpeechConstants.MODEL_ID
        }
}
