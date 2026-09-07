package br.com.banavita.app.domain.model

enum class TranscriptionProvider {
    /** Whisper.cpp embutido no aparelho (offline, mais lento em hardware fraco). */
    LOCAL,

    /** Groq Cloud — whisper-large-v3-turbo (requer internet e chave de API). */
    GROQ,
}
