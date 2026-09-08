package br.com.banavita.app.data.repository

import android.content.Context
import android.net.Uri
import br.com.banavita.app.core.time.nowEpochMillis
import br.com.banavita.app.data.backup.BackupManifest
import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.data.local.BanavitaDatabaseHolder
import br.com.banavita.app.domain.exception.BackupInvalidException
import br.com.banavita.app.domain.exception.BackupSchemaTooNewException
import br.com.banavita.app.domain.reminder.ReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DatabaseBackupRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val databaseHolder: BanavitaDatabaseHolder,
    private val eventoRepository: EventoRepository,
    private val reminderScheduler: ReminderScheduler,
) {
    suspend fun exportTo(uri: Uri): BackupManifest = withContext(Dispatchers.IO) {
        val manifest = BackupManifest(
            schemaVersion = BanavitaDatabase.SCHEMA_VERSION,
            exportedAtEpochMillis = nowEpochMillis(),
        )
        val db = databaseHolder.get()
        // Checkpoint com o banco aberto para consolidar WAL no arquivo principal.
        db.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").use { cursor ->
            cursor.moveToFirst()
        }

        val dbFile = databaseHolder.databaseFile()
        if (!dbFile.exists() || dbFile.length() == 0L) {
            throw BackupInvalidException("Banco de dados local não encontrado.")
        }

        context.contentResolver.openOutputStream(uri)?.use { output ->
            ZipOutputStream(BufferedOutputStream(output)).use { zip ->
                zip.putNextEntry(ZipEntry(BackupManifest.MANIFEST_ENTRY))
                zip.write(manifest.toJson().toByteArray(Charsets.UTF_8))
                zip.closeEntry()

                zip.putNextEntry(ZipEntry(BackupManifest.DATABASE_ENTRY))
                dbFile.inputStream().buffered().use { input -> input.copyTo(zip) }
                zip.closeEntry()
            }
        } ?: throw BackupInvalidException("Não foi possível gravar o arquivo escolhido.")

        manifest
    }

    /**
     * Substitui o banco atual pelo do pacote. Não mescla.
     * Após sucesso: reabre Room e reagenda lembretes da agenda.
     */
    suspend fun restoreFrom(uri: Uri) = withContext(Dispatchers.IO) {
        val stagingDir = File(context.cacheDir, "banavita-restore-${System.currentTimeMillis()}")
        stagingDir.mkdirs()
        try {
            val (manifest, stagedDb) = unpackBackup(uri, stagingDir)
            validateManifest(manifest)
            validateSqliteHeader(stagedDb)

            val target = databaseHolder.databaseFile()
            val wal = File(target.path + "-wal")
            val shm = File(target.path + "-shm")

            databaseHolder.checkpointAndClose()

            // Apaga o banco atual e sidecars; depois copia o restaurado.
            if (target.exists() && !target.delete()) {
                throw BackupInvalidException("Não foi possível substituir o banco atual.")
            }
            wal.delete()
            shm.delete()

            stagedDb.copyTo(target, overwrite = true)

            databaseHolder.reopen()
            // Força abertura e migrações (schema antigo → atual).
            databaseHolder.get().openHelper.writableDatabase
            reminderScheduler.rescheduleAll(eventoRepository.getPendingReminders())
        } catch (error: Exception) {
            runCatching {
                // Se o banco foi fechado e a restauração falhou, tenta reabrir o que restou.
                databaseHolder.reopen()
            }
            when (error) {
                is BackupSchemaTooNewException, is BackupInvalidException -> throw error
                else -> throw BackupInvalidException(
                    error.message ?: "Não foi possível restaurar o arquivo escolhido.",
                )
            }
        } finally {
            stagingDir.deleteRecursively()
        }
    }

    private fun unpackBackup(uri: Uri, stagingDir: File): Pair<BackupManifest, File> {
        var manifest: BackupManifest? = null
        val stagedDb = File(stagingDir, BackupManifest.DATABASE_ENTRY)

        context.contentResolver.openInputStream(uri)?.use { input ->
            ZipInputStream(BufferedInputStream(input)).use { zip ->
                var entry = zip.nextEntry
                while (entry != null) {
                    when (entry.name) {
                        BackupManifest.MANIFEST_ENTRY -> {
                            val raw = zip.readBytes().toString(Charsets.UTF_8)
                            manifest = runCatching { BackupManifest.fromJson(raw) }
                                .getOrElse {
                                    throw BackupInvalidException(
                                        it.message ?: "Metadados do backup inválidos.",
                                    )
                                }
                        }
                        BackupManifest.DATABASE_ENTRY -> {
                            stagedDb.outputStream().buffered().use { out -> zip.copyTo(out) }
                        }
                    }
                    zip.closeEntry()
                    entry = zip.nextEntry
                }
            }
        } ?: throw BackupInvalidException("Não foi possível abrir o arquivo escolhido.")

        val resolvedManifest = manifest
            ?: throw BackupInvalidException("Arquivo sem metadados de backup Banavita.")
        if (!stagedDb.exists() || stagedDb.length() == 0L) {
            throw BackupInvalidException("Arquivo de backup sem banco de dados.")
        }
        return resolvedManifest to stagedDb
    }

    private fun validateManifest(manifest: BackupManifest) {
        if (manifest.schemaVersion > BanavitaDatabase.SCHEMA_VERSION) {
            throw BackupSchemaTooNewException(
                backupSchemaVersion = manifest.schemaVersion,
                appSchemaVersion = BanavitaDatabase.SCHEMA_VERSION,
            )
        }
        if (manifest.schemaVersion < 1) {
            throw BackupInvalidException("Versão de schema do backup inválida.")
        }
    }

    private fun validateSqliteHeader(file: File) {
        val header = ByteArray(16)
        file.inputStream().use { input ->
            val read = input.read(header)
            if (read < 16) {
                throw BackupInvalidException("Arquivo de banco incompleto.")
            }
        }
        val magic = header.decodeToString()
        if (!magic.startsWith("SQLite format 3")) {
            throw BackupInvalidException("O arquivo não contém um banco SQLite válido.")
        }
    }
}
