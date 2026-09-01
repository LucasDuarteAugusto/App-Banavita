package br.com.banavita.app.core.speech

import android.content.Context
import br.com.banavita.app.domain.model.ModelStatus
import br.com.banavita.app.domain.model.SpeechModelSpec
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class SpeechModelManager @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    fun modelsDirectory(): File =
        File(context.filesDir, SpeechConstants.MODELS_DIR_NAME).also { it.mkdirs() }

    fun activeSpec(): SpeechModelSpec = SpeechModelCatalog.activeModel()

    fun modelFile(spec: SpeechModelSpec = activeSpec()): File =
        File(modelsDirectory(), spec.fileName)

    open fun ensureModelReady(spec: SpeechModelSpec = activeSpec()): ModelStatus {
        var status = validateModelFile(spec)
        if (status != ModelStatus.READY) {
            installBundledModelFromAssets(spec)
            status = validateModelFile(spec)
        }
        return status
    }

    /**
     * Copia o modelo embutido em [assets][SpeechConstants.MODELS_DIR_NAME] para [filesDir]
     * na primeira execução (ou quando o arquivo local está ausente/corrompido).
     */
    open fun installBundledModelFromAssets(spec: SpeechModelSpec = activeSpec()): Boolean {
        val target = modelFile(spec)
        val assetPath = "${SpeechConstants.MODELS_DIR_NAME}/${spec.fileName}"
        val temp = File(modelsDirectory(), "${spec.fileName}.tmp")

        if (target.exists()) {
            target.delete()
        }
        temp.delete()

        return try {
            context.assets.open(assetPath).use { input ->
                temp.outputStream().buffered().use { output ->
                    input.copyTo(output)
                }
            }
            if (!temp.renameTo(target)) {
                temp.inputStream().use { input ->
                    target.outputStream().buffered().use { output ->
                        input.copyTo(output)
                    }
                }
                temp.delete()
            }
            validateModelFile(spec) == ModelStatus.READY
        } catch (_: IOException) {
            temp.delete()
            target.delete()
            false
        }
    }

    private fun validateModelFile(spec: SpeechModelSpec): ModelStatus {
        val file = modelFile(spec)
        if (!file.exists()) {
            return ModelStatus.MISSING
        }
        if (file.length() < MIN_BYTES) {
            return ModelStatus.TOO_SMALL
        }
        if (spec.sha256.isNotBlank()) {
            val actual = sha256Hex(file)
            if (!actual.equals(spec.sha256, ignoreCase = true)) {
                return ModelStatus.INVALID_CHECKSUM
            }
        }
        return ModelStatus.READY
    }

    open fun modelPathOrNull(spec: SpeechModelSpec = activeSpec()): String? {
        return if (ensureModelReady(spec) == ModelStatus.READY) {
            modelFile(spec).absolutePath
        } else {
            null
        }
    }

    fun sha256Hex(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        FileInputStream(file).use { input ->
            val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
            while (true) {
                val read = input.read(buffer)
                if (read <= 0) break
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { byte -> "%02x".format(byte) }
    }

    companion object {
        private const val MIN_BYTES = 1_000_000L
    }
}
