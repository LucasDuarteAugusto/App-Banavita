package br.com.banavita.app.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import br.com.banavita.app.core.speech.AudioCapture
import br.com.banavita.app.core.speech.SpeechConstants
import br.com.banavita.app.core.speech.SpeechModelManager
import br.com.banavita.app.core.speech.WhisperEngine
import br.com.banavita.app.domain.model.ModelStatus
import br.com.banavita.app.domain.model.SpeechModelSpec
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SpeechToTextRepositoryTest {

    private lateinit var repository: SpeechToTextRepository
    private lateinit var fakeEngine: FakeWhisperEngine

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        fakeEngine = FakeWhisperEngine(transcript = "vendeu uma caxinha de banana")

        val modelManager = object : SpeechModelManager(context) {
            override fun ensureModelReady(spec: SpeechModelSpec): ModelStatus = ModelStatus.READY

            override fun modelPathOrNull(spec: SpeechModelSpec): String? =
                "/tmp/fake-model.bin"
        }

        repository = SpeechToTextRepository(
            speechModelManager = modelManager,
            whisperEngine = fakeEngine,
            audioCapture = AudioCapture(context),
        )
    }

    @Test
    fun `transcribe usa engine e normaliza texto`() = runBlocking {
        val result = repository.transcribe(FloatArray(1600) { 0.01f })
        assertEquals("vendeu uma caixinha de banana", result.text)
        assertEquals(SpeechConstants.LANGUAGE_PT, result.language)
        assertEquals(SpeechConstants.ACTIVE_MODEL_ID, result.modelId)
        assertTrue(result.durationMillis >= 0)
        assertTrue(fakeEngine.loadCalled)
        assertTrue(fakeEngine.transcribeCalled)
        assertEquals(SpeechConstants.LANGUAGE_PT, fakeEngine.lastLanguage)
    }

    private class FakeWhisperEngine(
        private val transcript: String,
    ) : WhisperEngine {
        var loadCalled = false
        var transcribeCalled = false
        var lastLanguage: String? = null
        private var loaded = false

        override val isLoaded: Boolean
            get() = loaded

        override suspend fun load(modelPath: String) {
            loadCalled = true
            loaded = true
        }

        override suspend fun transcribe(
            samples: FloatArray,
            language: String,
            initialPrompt: String?,
        ): String {
            transcribeCalled = true
            lastLanguage = language
            return transcript
        }

        override fun release() {
            loaded = false
        }
    }
}
