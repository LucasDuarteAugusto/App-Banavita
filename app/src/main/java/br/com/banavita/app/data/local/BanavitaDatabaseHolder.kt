package br.com.banavita.app.data.local

import android.content.Context
import androidx.room.Room
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Possui a instância Room e permite fechar/reabrir após restaurar backup,
 * para os DAOs/Providers não ficarem apontando para arquivo morto.
 */
@Singleton
class BanavitaDatabaseHolder @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    @Volatile
    private var instance: BanavitaDatabase = buildDatabase()

    fun get(): BanavitaDatabase = instance

    fun databaseFile(): File = context.getDatabasePath(BanavitaDatabase.DATABASE_NAME)

    @Synchronized
    fun checkpointAndClose() {
        val db = instance
        runCatching {
            db.openHelper.writableDatabase.query("PRAGMA wal_checkpoint(FULL)").use { cursor ->
                cursor.moveToFirst()
            }
        }
        db.close()
    }

    @Synchronized
    fun reopen() {
        if (instance.isOpen) {
            instance.close()
        }
        instance = buildDatabase()
    }

    private fun buildDatabase(): BanavitaDatabase =
        Room.databaseBuilder(
            context,
            BanavitaDatabase::class.java,
            BanavitaDatabase.DATABASE_NAME,
        )
            .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
            .build()
}
