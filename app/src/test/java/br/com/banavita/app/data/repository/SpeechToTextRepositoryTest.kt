package br.com.banavita.app.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import br.com.banavita.app.core.speech.SpeechConstants
import br.com.banavita.app.core.speech.SpeechModelManager
import br.com.banavita.app.core.speech.WhisperEngine
import br.com.banavita.app.data.local.settings.AppSettingsLocalDataSource
import br.com.banavita.app.data.remote.groq.GroqConfig
import br.com.banavita.app.data.remote.groq.GroqWhisperClient
import br.com.banavita.app.domain.model.ModelStatus
import br.com.banavita.app.domain.model.SpeechModelSpec
import br.com.banavita.app.domain.model.TranscriptionProvider
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
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
    private lateinit var settingsRepository: AppSettingsRepository
    private lateinit var groqClient: GroqWhisperClient

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        fakeEngine = FakeWhisperEngine(transcript = "vendeu uma caxinha de banana")
        settingsRepository = AppSettingsRepository(
            localDataSource = AppSettingsLocalDataSource(context),
            groqConfig = object : GroqConfig {
                override val apiKey: String = "test-key"
                override val modelId: String = "whisper-large-v3-turbo"
            },
        )
        settingsRepository.setTranscriptionProvider(TranscriptionProvider.LOCAL)
        groqClient = mockk(relaxed = true)

        val modelManager = object : SpeechModelManager(context) {
            override fun ensureModelReady(spec: SpeechModelSpec): ModelStatus = ModelStatus.READY

            override fun modelPathOrNull(spec: SpeechModelSpec): String? =
                "/tmp/fake-model.bin"
        }

        repository = SpeechToTextRepository(
            speechModelManager = modelManager,
            whisperEngine = fakeEngine,
            groqWhisperClient = groqClient,
            appSettingsRepository = settingsRepository,
        )
    }

    @Test
    fun `transcribe local usa engine e normaliza texto`() = runBlocking {
        val result = repository.transcribe(FloatArray(1600) { 0.01f })
        assertEquals("vendeu uma caixinha de banana", result.text)
        assertEquals(SpeechConstants.LANGUAGE_PT, result.language)
        assertEquals(SpeechConstants.ACTIVE_MODEL_ID, result.modelId)
        assertTrue(result.durationMillis >= 0)
        assertTrue(fakeEngine.loadCalled)
        assertTrue(fakeEngine.transcribeCalled)
        assertEquals(SpeechConstants.LANGUAGE_PT, fakeEngine.lastLanguage)
    }

    @Test
    fun `transcribe local corta silencio antes da inferencia`() = runBlocking {
        val rate = SpeechConstants.SAMPLE_RATE_HZ
        val samples = FloatArray(rate) { 0f } + FloatArray(rate) { 0.3f } + FloatArray(rate) { 0f }
        repository.transcribe(samples)
        assertTrue(fakeEngine.lastSampleCount < samples.size)
        assertTrue(fakeEngine.lastSampleCount >= rate)
    }

    @Test
    fun `transcribe groq usa cliente remoto`() = runBlocking {
        settingsRepository.setTranscriptionProvider(TranscriptionProvider.GROQ)
        coEvery {
            groqClient.transcribe(any(), any(), any())
        } returns "vendeu uma caxinha de banana"

        val result = repository.transcribe(FloatArray(1600) { 0.01f })
        assertEquals("vendeu uma caixinha de banana", result.text)
        assertEquals("whisper-large-v3-turbo", result.modelId)
        coVerify(exactly = 1) { groqClient.transcribe(any(), any(), any()) }
        assertTrue(!fakeEngine.transcribeCalled)
    }

    private class FakeWhisperEngine(
        private val transcript: String,
    ) : WhisperEngine {
        var loadCalled = false
        var transcribeCalled = false
        var lastLanguage: String? = null
        var lastSampleCount: Int = 0
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
            lastSampleCount = samples.size
            return transcript
        }

        override fun release() {
            loaded = false
        }
    }
}
