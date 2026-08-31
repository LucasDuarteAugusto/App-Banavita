package br.com.banavita.app.core.speech

/**
 * JNI bridge for libbanavita_whisper.so (whisper.cpp).
 * Not thread-safe — call only from WhisperCppEngine's dedicated dispatcher.
 */
object WhisperLib {
    init {
        System.loadLibrary("banavita_whisper")
    }

    external fun initContext(modelPath: String): Long

    external fun freeContext(contextPtr: Long)

    external fun fullTranscribe(
        contextPtr: Long,
        numThreads: Int,
        audioData: FloatArray,
        language: String,
        initialPrompt: String?,
    ): Int

    external fun getTextSegmentCount(contextPtr: Long): Int

    external fun getTextSegment(contextPtr: Long, index: Int): String
}
