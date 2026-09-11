package br.com.banavita.app.data.repository

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import br.com.banavita.app.data.local.settings.AppSettingsLocalDataSource
import br.com.banavita.app.data.remote.groq.GroqConfig
import br.com.banavita.app.domain.model.ThemeMode
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AppSettingsRepositoryTest {

    private lateinit var repository: AppSettingsRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        repository = AppSettingsRepository(
            localDataSource = AppSettingsLocalDataSource(context),
            groqConfig = object : GroqConfig {
                override val apiKey: String = ""
                override val modelId: String = "test"
            },
        )
    }

    @Test
    fun `tema padrao e claro`() {
        assertEquals(ThemeMode.LIGHT, repository.getThemeMode())
    }

    @Test
    fun `setThemeMode persiste e emite`() {
        repository.setThemeMode(ThemeMode.DARK)
        assertEquals(ThemeMode.DARK, repository.getThemeMode())
        assertEquals(ThemeMode.DARK, repository.themeMode.value)
        repository.setThemeMode(ThemeMode.LIGHT)
        assertEquals(ThemeMode.LIGHT, repository.getThemeMode())
    }
}
