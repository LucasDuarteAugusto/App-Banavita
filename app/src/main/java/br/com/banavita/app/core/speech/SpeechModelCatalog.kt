package br.com.banavita.app.core.speech

import br.com.banavita.app.domain.model.DeviceSpeechProfile
import br.com.banavita.app.domain.model.SpeechModelSpec

object SpeechModelCatalog {
    val models: List<SpeechModelSpec> = listOf(
        SpeechModelSpec(
            id = SpeechConstants.ACTIVE_MODEL_ID,
            fileName = SpeechConstants.ACTIVE_MODEL_FILE,
            sha256 = SpeechConstants.ACTIVE_MODEL_SHA256,
            profile = DeviceSpeechProfile.MINIMUM,
            active = true,
            approximateSizeMb = 57,
        ),
        SpeechModelSpec(
            id = "ggml-base-q8_0",
            fileName = "ggml-base-q8_0.bin",
            sha256 = "",
            profile = DeviceSpeechProfile.INTERMEDIATE,
            active = false,
            approximateSizeMb = 78,
        ),
        SpeechModelSpec(
            id = "ggml-small-q5_1",
            fileName = "ggml-small-q5_1.bin",
            sha256 = "",
            profile = DeviceSpeechProfile.INTERMEDIATE,
            active = false,
            approximateSizeMb = 190,
        ),
        SpeechModelSpec(
            id = "ggml-small-q8_0",
            fileName = "ggml-small-q8_0.bin",
            sha256 = "",
            profile = DeviceSpeechProfile.PREMIUM,
            active = false,
            approximateSizeMb = 250,
        ),
    )

    fun activeModel(): SpeechModelSpec =
        models.first { it.active }
}
