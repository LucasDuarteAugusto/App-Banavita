# Modificação: Remoção do Modelo Whisper Tiny dos Assets e Validação do Build

## Resumo
Removeu fisicamente o arquivo binário do modelo Whisper Tiny (`ggml-tiny-q5_1.bin`) da pasta de assets (`app/src/main/assets/models/`) e atualizou o script do Gradle (`app/build.gradle.kts`) para remover a checagem obrigatória e cópia automática do Tiny antes da compilação do APK de debug/release.

## Arquivos Modificados/Removidos
| Ação | Arquivo |
| :--- | :--- |
| **Removido** | `app/src/main/assets/models/ggml-tiny-q5_1.bin` |
| **Alterado** | `app/build.gradle.kts` |

## Instruções de Conferência na IDE
1. Verifique se a compilação final é executada perfeitamente via `./gradlew assembleDebug` sem requerer o arquivo Tiny.
2. Certifique-se de que a pasta de assets gerada contém exclusivamente o arquivo do modelo Base (`ggml-base-q5_1.bin`).
