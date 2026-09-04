package br.com.banavita.app.data.repository

import br.com.banavita.app.core.speech.PcmConverter
import br.com.banavita.app.core.speech.PtBrPromptHints
import br.com.banavita.app.core.speech.SpeechConstants
import br.com.banavita.app.core.speech.SpeechModelManager
import br.com.banavita.app.core.speech.WavReader
import br.com.banavita.app.core.speech.WhisperEngine
import br.com.banavita.app.domain.exception.SpeechAudioException
import br.com.banavita.app.domain.exception.SpeechModelException
import br.com.banavita.app.domain.model.ModelStatus
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
) {
    private val prepareMutex = Mutex()

    fun ensureModelReady(): ModelStatus =
        speechModelManager.ensureModelReady()

    suspend fun prepareEngine() = prepareMutex.withLock {
        withContext(Dispatchers.IO) {
            val status = speechModelManager.ensureModelReady()
            if (status != ModelStatus.READY) {
                throw SpeechModelException(
                    "Modelo ASR indisponível ($status). " +
                        "Reinstale o app ou gere o APK com o modelo embutido " +
                        "(${speechModelManager.activeSpec().fileName}).",
                )
            }
            if (!whisperEngine.isLoaded) {
                val path = speechModelManager.modelPathOrNull()
                    ?: throw SpeechModelException("Caminho do modelo não encontrado")
                whisperEngine.load(path)
            }
        }
    }

    suspend fun transcribe(pcm16kMono: FloatArray): TranscriptionResult {
        prepareEngine()
        if (pcm16kMono.isEmpty()) {
            throw SpeechAudioException("Áudio vazio")
        }
        val samples = PcmConverter.clampToMaxDuration(pcm16kMono)
        lateinit var text: String
        val elapsed = measureTimeMillis {
            text = whisperEngine.transcribe(
                samples = samples,
                language = SpeechConstants.LANGUAGE_PT,
                initialPrompt = PtBrPromptHints.initialPrompt,
            )
        }
        return TranscriptionResult(
            text = PtBrPromptHints.normalizeTranscript(text),
            language = SpeechConstants.LANGUAGE_PT,
            durationMillis = elapsed,
            modelId = speechModelManager.activeSpec().id,
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
}
