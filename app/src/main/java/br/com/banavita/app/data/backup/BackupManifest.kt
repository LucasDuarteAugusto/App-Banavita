package br.com.banavita.app.data.backup

import org.json.JSONObject
import java.time.Instant

/**
 * Metadados do pacote `.banavita` (ZIP: manifest.json + banavita.db).
 */
data class BackupManifest(
    val app: String = APP_NAME,
    val schemaVersion: Int,
    val exportedAtEpochMillis: Long,
) {
    fun toJson(): String =
        JSONObject()
            .put("app", app)
            .put("schemaVersion", schemaVersion)
            .put("exportedAtEpochMillis", exportedAtEpochMillis)
            .put("exportedAt", Instant.ofEpochMilli(exportedAtEpochMillis).toString())
            .toString()

    companion object {
        const val APP_NAME = "Banavita"
        const val MANIFEST_ENTRY = "manifest.json"
        const val DATABASE_ENTRY = "banavita.db"
        const val FILE_EXTENSION = "banavita"

        fun fromJson(raw: String): BackupManifest {
            val json = JSONObject(raw)
            val app = json.optString("app", "")
            if (app != APP_NAME) {
                throw IllegalArgumentException("Arquivo não é um backup Banavita.")
            }
            if (!json.has("schemaVersion")) {
                throw IllegalArgumentException("Backup sem versão de schema.")
            }
            return BackupManifest(
                app = app,
                schemaVersion = json.getInt("schemaVersion"),
                exportedAtEpochMillis = json.optLong(
                    "exportedAtEpochMillis",
                    System.currentTimeMillis(),
                ),
            )
        }

        fun suggestedFileName(nowEpochMillis: Long = System.currentTimeMillis()): String {
            val day = Instant.ofEpochMilli(nowEpochMillis)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate()
            return "Banavita-backup-$day.$FILE_EXTENSION"
        }
    }
}
