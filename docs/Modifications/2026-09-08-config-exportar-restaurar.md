# Configurações: exportar / restaurar banco

## Resumo

A tela Configurações passou a ter duas seções: **Modelo de transcrição** (inalterada) e **Banco de dados** (exportar/restaurar via SAF). O backup é um pacote `.banavita` (ZIP com `manifest.json` + `banavita.db`), sem licença/Groq/modelo. Restore substitui o Room, aplica migrações se o schema do arquivo for mais antigo, recusa schema mais novo e reagenda lembretes da agenda. Repositórios Room passaram a usar `Provider` para não ficarem com DAO morto após reabrir o banco.

## Arquivos

| Status | Caminho |
|--------|---------|
| Criado | `app/src/main/java/br/com/banavita/app/data/local/BanavitaDatabaseHolder.kt` |
| Criado | `app/src/main/java/br/com/banavita/app/data/backup/BackupManifest.kt` |
| Criado | `app/src/main/java/br/com/banavita/app/data/repository/DatabaseBackupRepository.kt` |
| Criado | `app/src/main/java/br/com/banavita/app/domain/exception/BackupExceptions.kt` |
| Criado | `app/src/test/java/br/com/banavita/app/data/backup/BackupManifestTest.kt` |
| Criado | `app/src/test/java/br/com/banavita/app/data/backup/DatabaseBackupRepositoryTest.kt` |
| Criado | `docs/Modifications/2026-09-08-config-exportar-restaurar.md` |
| Alterado | `app/src/main/java/br/com/banavita/app/di/DatabaseModule.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/local/BanavitaDatabase.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/settings/SettingsScreen.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/settings/SettingsViewModel.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/settings/SettingsUiState.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/ui/common/FormComponents.kt` |
| Alterado | `app/src/main/res/values/strings.xml` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/repository/*Repository.kt` (Provider Room) |
| Alterado | `app/src/main/java/br/com/banavita/app/data/repository/DocumentoSupport.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/core/extraction/EntityResolver.kt` |
| Alterado | testes Room/Extraction/Financeiro/Evento |
| Alterado | `docs/ui/mockups/settings.png` |
| Alterado | `docs/ui/README.md` |

## O que conferir na IDE

1. Sync Gradle / rebuild.
2. Abrir **Configurações** (ícone no Início): seções Transcrição e Banco de dados.
3. **Exportar dados** → escolher pasta → arquivo `Banavita-backup-AAAA-MM-DD.banavita` → mensagem de sucesso.
4. Alterar algum cadastro → **Restaurar** → cancelar no diálogo (nada muda) → confirmar (dados voltam ao backup).
5. Conferir agenda futura ainda notifica após restore.
6. Licença e chave Groq permanecem as do aparelho (não vêm no arquivo).
7. Rodar testes: `BackupManifestTest`, `DatabaseBackupRepositoryTest`.
