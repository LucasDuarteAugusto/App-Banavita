package br.com.banavita.app.domain.exception

class BackupException(message: String) : BanavitaDomainException(message)

class BackupSchemaTooNewException(
    val backupSchemaVersion: Int,
    val appSchemaVersion: Int,
) : BackupException(
    "Este backup é da versão $backupSchemaVersion do banco. " +
        "Atualize o aplicativo (versão do banco $appSchemaVersion) antes de restaurar.",
)

class BackupInvalidException(message: String) : BackupException(message)
