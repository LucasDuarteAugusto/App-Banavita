package br.com.banavita.app.data.backup

import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.domain.exception.BackupInvalidException
import br.com.banavita.app.domain.exception.BackupSchemaTooNewException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class BackupManifestTest {

    @Test
    fun `roundtrip json preserva schemaVersion`() {
        val original = BackupManifest(
            schemaVersion = BanavitaDatabase.SCHEMA_VERSION,
            exportedAtEpochMillis = 1_700_000_000_000L,
        )
        val parsed = BackupManifest.fromJson(original.toJson())
        assertEquals(original.app, parsed.app)
        assertEquals(original.schemaVersion, parsed.schemaVersion)
        assertEquals(original.exportedAtEpochMillis, parsed.exportedAtEpochMillis)
    }

    @Test
    fun `suggestedFileName usa extensao banavita e data`() {
        val name = BackupManifest.suggestedFileName(1_725_747_200_000L) // 2024-09-08 UTC-ish
        assertTrue(name.startsWith("Banavita-backup-"))
        assertTrue(name.endsWith(".banavita"))
    }

    @Test
    fun `fromJson rejeita app diferente`() {
        try {
            BackupManifest.fromJson("""{"app":"Outro","schemaVersion":3}""")
            fail("deveria falhar")
        } catch (error: IllegalArgumentException) {
            assertTrue(error.message!!.contains("Banavita"))
        }
    }

    @Test
    fun `fromJson rejeita sem schemaVersion`() {
        try {
            BackupManifest.fromJson("""{"app":"Banavita"}""")
            fail("deveria falhar")
        } catch (error: IllegalArgumentException) {
            assertTrue(error.message!!.contains("schema"))
        }
    }

    @Test
    fun `schema mais novo que o app e recusado`() {
        val backupVersion = BanavitaDatabase.SCHEMA_VERSION + 1
        try {
            rejectIfTooNew(backupVersion)
            fail("deveria falhar")
        } catch (error: BackupSchemaTooNewException) {
            assertEquals(backupVersion, error.backupSchemaVersion)
            assertEquals(BanavitaDatabase.SCHEMA_VERSION, error.appSchemaVersion)
        }
    }

    @Test
    fun `schema igual ou mais antigo e aceito`() {
        rejectIfTooNew(BanavitaDatabase.SCHEMA_VERSION)
        rejectIfTooNew(1)
    }

    @Test
    fun `pacote zip contem manifest e db`() {
        val manifest = BackupManifest(
            schemaVersion = 3,
            exportedAtEpochMillis = 123L,
        )
        val fakeDb = "SQLite format 3\u0000fake".toByteArray()
        val bytes = ByteArrayOutputStream().use { baos ->
            ZipOutputStream(baos).use { zip ->
                zip.putNextEntry(ZipEntry(BackupManifest.MANIFEST_ENTRY))
                zip.write(manifest.toJson().toByteArray())
                zip.closeEntry()
                zip.putNextEntry(ZipEntry(BackupManifest.DATABASE_ENTRY))
                zip.write(fakeDb)
                zip.closeEntry()
            }
            baos.toByteArray()
        }

        var foundManifest = false
        var foundDb = false
        ZipInputStream(ByteArrayInputStream(bytes)).use { zip ->
            var entry = zip.nextEntry
            while (entry != null) {
                when (entry.name) {
                    BackupManifest.MANIFEST_ENTRY -> {
                        foundManifest = true
                        val parsed = BackupManifest.fromJson(zip.readBytes().toString(Charsets.UTF_8))
                        assertEquals(3, parsed.schemaVersion)
                    }
                    BackupManifest.DATABASE_ENTRY -> {
                        foundDb = true
                        val header = ByteArray(16)
                        val read = zip.read(header)
                        assertTrue(read >= 15)
                        assertTrue(header.decodeToString().startsWith("SQLite format 3"))
                    }
                }
                zip.closeEntry()
                entry = zip.nextEntry
            }
        }
        assertTrue(foundManifest)
        assertTrue(foundDb)
    }

    private fun rejectIfTooNew(backupSchemaVersion: Int) {
        if (backupSchemaVersion > BanavitaDatabase.SCHEMA_VERSION) {
            throw BackupSchemaTooNewException(
                backupSchemaVersion = backupSchemaVersion,
                appSchemaVersion = BanavitaDatabase.SCHEMA_VERSION,
            )
        }
        if (backupSchemaVersion < 1) {
            throw BackupInvalidException("Versão de schema do backup inválida.")
        }
    }
}
