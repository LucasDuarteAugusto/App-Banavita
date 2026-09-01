package br.com.banavita.app.core.speech

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import br.com.banavita.app.domain.model.ModelStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SpeechModelManagerTest {

    private lateinit var manager: SpeechModelManager
    private lateinit var modelsDir: File

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        manager = SpeechModelManager(context)
        modelsDir = manager.modelsDirectory()
        modelsDir.listFiles()?.forEach { it.delete() }
    }

    @Test
    fun `modelo ausente retorna MISSING`() {
        assertEquals(ModelStatus.MISSING, manager.ensureModelReady())
    }

    @Test
    fun `arquivo muito pequeno tenta reinstalar e retorna MISSING sem asset`() {
        val file = manager.modelFile()
        file.writeBytes(ByteArray(100))
        assertEquals(ModelStatus.MISSING, manager.ensureModelReady())
    }

    @Test
    fun `checksum invalido tenta reinstalar e retorna MISSING sem asset`() {
        val file = manager.modelFile()
        file.writeBytes(ByteArray(2_000_000) { 1 })
        assertEquals(ModelStatus.MISSING, manager.ensureModelReady())
    }

    @Test
    fun `installBundledModelFromAssets retorna false quando asset ausente`() {
        assertTrue(!manager.installBundledModelFromAssets())
        assertEquals(ModelStatus.MISSING, manager.ensureModelReady())
    }

    @Test
    fun `sha256Hex e deterministico`() {
        val file = File(modelsDir, "sample.bin")
        file.writeBytes(byteArrayOf(1, 2, 3, 4))
        val a = manager.sha256Hex(file)
        val b = manager.sha256Hex(file)
        assertEquals(a, b)
        assertEquals(64, a.length)
        assertTrue(a.matches(Regex("[0-9a-f]+")))
    }
}
