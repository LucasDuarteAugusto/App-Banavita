# Configurações: transcrição Groq ou Whisper local

Adiciona tela de Configurações para escolher entre transcrição remota (Groq `whisper-large-v3-turbo`) e o modelo Whisper.cpp local. A chave `GROQ_API_KEY` do `.env` gera `GroqBuildFields.kt` no preBuild. Padrão: Groq quando a chave existe.

## Criado

| Caminho |
|---------|
| `app/src/main/java/br/com/banavita/app/domain/model/TranscriptionProvider.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/settings/AppSettingsLocalDataSource.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/AppSettingsRepository.kt` |
| `app/src/main/java/br/com/banavita/app/data/remote/groq/GroqConfig.kt` |
| `app/src/main/java/br/com/banavita/app/data/remote/groq/GroqWhisperClient.kt` |
| `app/src/main/java/br/com/banavita/app/core/speech/WavWriter.kt` |
| `app/src/main/java/br/com/banavita/app/core/speech/GroqSpeechConstants.kt` |
| `app/src/main/java/br/com/banavita/app/di/NetworkModule.kt` |
| `app/src/main/java/br/com/banavita/app/ui/settings/SettingsScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/settings/SettingsViewModel.kt` |
| `app/src/main/java/br/com/banavita/app/ui/settings/SettingsUiState.kt` |
| `app/src/main/java/br/com/banavita/app/GroqBuildFields.kt.example` |
| `app/src/main/res/drawable/ic_settings.xml` |
| `app/src/test/java/br/com/banavita/app/core/speech/WavWriterTest.kt` |
| `app/src/test/java/br/com/banavita/app/data/remote/groq/GroqWhisperClientTest.kt` |
| `docs/ui/mockups/settings.png` |

## Alterado

| Caminho |
|---------|
| `app/build.gradle.kts` (OkHttp explícito + `generateGroqBuildFields`) |
| `gradle/libs.versions.toml` |
| `app/src/main/AndroidManifest.xml` (`INTERNET`) |
| `.gitignore` (`.env`, `GroqBuildFields.kt`) |
| `.env` (`GROQ_API_KEY`) |
| `app/src/main/java/br/com/banavita/app/domain/exception/SpeechException.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/SpeechToTextRepository.kt` |
| `app/src/main/java/br/com/banavita/app/di/SpeechModule.kt` |
| `app/src/main/java/br/com/banavita/app/ui/navigation/BanavitaRoutes.kt` |
| `app/src/main/java/br/com/banavita/app/ui/navigation/MainNavHost.kt` |
| `app/src/main/java/br/com/banavita/app/ui/shell/MainAppShell.kt` |
| `app/src/main/java/br/com/banavita/app/ui/common/BanavitaIcons.kt` |
| `app/src/main/res/values/strings.xml` |
| `app/src/test/java/br/com/banavita/app/data/repository/SpeechToTextRepositoryTest.kt` |
| `docs/ui/README.md` |
| `docs/speech/README.md` |

## Removido

| Caminho |
|---------|
| `app/src/main/java/br/com/banavita/app/di/SettingsModule.kt` (órfão com refs inválidas) |

## O que conferir na IDE

1. Sync Gradle (baixa OkHttp e regenera `GroqBuildFields.kt` no preBuild).
2. Confirmar que `GroqWhisperClient` / `NetworkModule` resolvem `okhttp3.*`.
3. Confirmar que `GroqConfig` resolve `GroqBuildFields` (arquivo gerado; se faltar, Sync ou `./gradlew generateGroqBuildFields`).
4. Início → engrenagem → Configurações; gravar com Groq.
5. Não versionar `.env` nem `GroqBuildFields.kt`.
