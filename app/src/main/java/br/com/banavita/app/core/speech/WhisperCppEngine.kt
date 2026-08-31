package br.com.banavita.app.core.speech

import br.com.banavita.app.domain.exception.SpeechEngineException
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WhisperCppEngine @Inject constructor() : WhisperEngine {

    private val inferenceDispatcher = Executors.newSingleThreadExecutor { runnable ->
        Thread(runnable, "whisper-inference").apply { isDaemon = true }
    }.asCoroutineDispatcher()

    @Volatile
    private var contextPtr: Long = 0L

    override val isLoaded: Boolean
        get() = contextPtr != 0L

    override suspend fun load(modelPath: String) = withContext(inferenceDispatcher) {
        if (contextPtr != 0L) {
            WhisperLib.freeContext(contextPtr)
            contextPtr = 0L
        }
        val ptr = WhisperLib.initContext(modelPath)
        if (ptr == 0L) {
            throw SpeechEngineException("Falha ao carregar modelo Whisper em $modelPath")
        }
        contextPtr = ptr
    }

    override suspend fun transcribe(
        samples: FloatArray,
        language: String,
        initialPrompt: String?,
    ): String = withContext(inferenceDispatcher) {
        check(contextPtr != 0L) { "WhisperContext não carregado" }
        val threads = (Runtime.getRuntime().availableProcessors() - 1).coerceAtLeast(1)
        val code = WhisperLib.fullTranscribe(
            contextPtr = contextPtr,
            numThreads = threads,
            audioData = samples,
            language = language,
            initialPrompt = initialPrompt,
        )
        if (code != 0) {
            throw SpeechEngineException("whisper_full falhou com código $code")
        }
        buildString {
            val count = WhisperLib.getTextSegmentCount(contextPtr)
            for (i in 0 until count) {
                append(WhisperLib.getTextSegment(contextPtr, i))
            }
        }.trim()
    }

    override fun release() {
        val ptr = contextPtr
        contextPtr = 0L
        if (ptr != 0L) {
            try {
                WhisperLib.freeContext(ptr)
            } catch (_: UnsatisfiedLinkError) {
                // Native lib may be unavailable in unit tests.
            }
        }
    }
}
