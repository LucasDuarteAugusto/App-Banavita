package br.com.banavita.app.core.speech

import br.com.banavita.app.domain.model.DeviceSpeechProfile
import br.com.banavita.app.domain.model.SpeechModelSpec

object SpeechModelCatalog {
    val models: List<SpeechModelSpec> = listOf(
        SpeechModelSpec(
            id = SpeechConstants.TINY_MODEL_ID,
            fileName = SpeechConstants.TINY_MODEL_FILE,
            sha256 = SpeechConstants.TINY_MODEL_SHA256,
            profile = DeviceSpeechProfile.MINIMUM,
            active = true, // Default
            approximateSizeMb = SpeechConstants.TINY_MODEL_SIZE,
        ),
        SpeechModelSpec(
            id = SpeechConstants.BASE_MODEL_ID,
            fileName = SpeechConstants.BASE_MODEL_FILE,
            sha256 = SpeechConstants.BASE_MODEL_SHA256,
            profile = DeviceSpeechProfile.INTERMEDIATE,
            active = false,
            approximateSizeMb = SpeechConstants.BASE_MODEL_SIZE,
        )
    )

    fun activeModel(): SpeechModelSpec =
        models.first { it.active }

    fun getSpec(id: String): SpeechModelSpec? =
        models.find { it.id == id }
}
