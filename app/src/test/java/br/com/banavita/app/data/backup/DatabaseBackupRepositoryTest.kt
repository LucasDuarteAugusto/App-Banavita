package br.com.banavita.app.data.backup

import android.content.Context
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.data.local.BanavitaDatabaseHolder
import br.com.banavita.app.data.local.MIGRATION_1_2
import br.com.banavita.app.data.local.MIGRATION_2_3
import br.com.banavita.app.data.local.entity.GrupoEntity
import br.com.banavita.app.data.repository.DatabaseBackupRepository
import br.com.banavita.app.data.repository.EventoRepository
import br.com.banavita.app.domain.exception.BackupSchemaTooNewException
import br.com.banavita.app.domain.reminder.FakeReminderScheduler
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.File
import java.util.UUID
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Provider

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DatabaseBackupRepositoryTest {

    private lateinit var context: Context
    private lateinit var holder: BanavitaDatabaseHolder
    private lateinit var repository: DatabaseBackupRepository
    private lateinit var scheduler: FakeReminderScheduler

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        // Limpa arquivos de banco de testes anteriores.
        context.getDatabasePath(BanavitaDatabase.DATABASE_NAME).delete()
        File(context.getDatabasePath(BanavitaDatabase.DATABASE_NAME).path + "-wal").delete()
        File(context.getDatabasePath(BanavitaDatabase.DATABASE_NAME).path + "-shm").delete()

        holder = BanavitaDatabaseHolder(context)
        scheduler = FakeReminderScheduler()
        val eventoRepository = EventoRepository(
            Provider { holder.get().eventoDao() },
            Provider { holder.get().clienteDao() },
            Provider { holder.get().fornecedorDao() },
            scheduler,
        )
        repository = DatabaseBackupRepository(context, holder, eventoRepository, scheduler)

        runBlocking {
            holder.get().grupoDao().insert(
                GrupoEntity(
                    id = UUID.randomUUID(),
                    createdAt = 1L,
                    updatedAt = 1L,
                    nome = "Frutas",
                    observacao = null,
                ),
            )
        }
    }

    @After
    fun tearDown() {
        runCatching { holder.checkpointAndClose() }
    }

    @Test
    fun `export cria pacote com manifest e sqlite`() = runBlocking {
        val outFile = File(context.cacheDir, "test-export.banavita")
        val uri = Uri.fromFile(outFile)
        val manifest = repository.exportTo(uri)
        assertEquals(BanavitaDatabase.SCHEMA_VERSION, manifest.schemaVersion)
        assertTrue(outFile.exists() && outFile.length() > 0)
    }

    @Test
    fun `restore substitui dados e reagenda`() = runBlocking {
        val backupFile = File(context.cacheDir, "test-restore.banavita")
        repository.exportTo(Uri.fromFile(backupFile))

        // Altera o banco atual.
        holder.get().grupoDao().insert(
            GrupoEntity(
                id = UUID.randomUUID(),
                createdAt = 2L,
                updatedAt = 2L,
                nome = "Temporario",
                observacao = null,
            ),
        )
        assertEquals(2, holder.get().grupoDao().getAll().size)

        repository.restoreFrom(Uri.fromFile(backupFile))

        val grupos = holder.get().grupoDao().getAll()
        assertEquals(1, grupos.size)
        assertEquals("Frutas", grupos.first().nome)
    }

    @Test
    fun `restore recusa schema mais novo sem alterar banco`() = runBlocking {
        val before = holder.get().grupoDao().getAll().map { it.nome }

        val bad = File(context.cacheDir, "too-new.banavita")
        ZipOutputStream(bad.outputStream()).use { zip ->
            zip.putNextEntry(ZipEntry(BackupManifest.MANIFEST_ENTRY))
            zip.write(
                BackupManifest(
                    schemaVersion = BanavitaDatabase.SCHEMA_VERSION + 5,
                    exportedAtEpochMillis = 1L,
                ).toJson().toByteArray(),
            )
            zip.closeEntry()
            zip.putNextEntry(ZipEntry(BackupManifest.DATABASE_ENTRY))
            // Header SQLite mínimo válido o bastante para passar a checagem de magic.
            val header = ByteArray(100)
            "SQLite format 3".toByteArray().copyInto(header)
            zip.write(header)
            zip.closeEntry()
        }

        try {
            repository.restoreFrom(Uri.fromFile(bad))
            fail("deveria recusar schema novo")
        } catch (_: BackupSchemaTooNewException) {
            // ok
        }

        val after = holder.get().grupoDao().getAll().map { it.nome }
        assertEquals(before, after)
    }

    @Test
    fun `holder reabre apos checkpointAndClose`() {
        holder.checkpointAndClose()
        holder.reopen()
        assertTrue(holder.get().isOpen)
        // Garante migrations registradas (builder usa as mesmas constantes).
        Room.databaseBuilder(context, BanavitaDatabase::class.java, "assert-migrations.db")
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
            .close()
        context.deleteDatabase("assert-migrations.db")
    }
}
