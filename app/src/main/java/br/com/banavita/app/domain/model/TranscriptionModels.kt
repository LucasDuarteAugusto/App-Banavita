package br.com.banavita.app.domain.model

enum class DeviceSpeechProfile {
    MINIMUM,
    INTERMEDIATE,
    PREMIUM,
}

enum class ModelStatus {
    READY,
    MISSING,
    INVALID_CHECKSUM,
    TOO_SMALL,
}

data class SpeechModelSpec(
    val id: String,
    val fileName: String,
    val sha256: String,
    val profile: DeviceSpeechProfile,
    val active: Boolean,
    val approximateSizeMb: Int,
)

data class TranscriptionResult(
    val text: String,
    val language: String,
    val durationMillis: Long,
    val modelId: String,
)
