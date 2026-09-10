# Otimização Whisper Tiny offline

## Resumo

O ASR local passou a limitar threads (2, teto 4), cortar silêncio nas pontas, usar `single_segment` e `audio_ctx` proporcional ao áudio, logar tempos/RTF (sem telefone completo) e compilar o nativo com `-O3` também em Debug. O modelo permanece carregado: transcrições seguintes não recalculam SHA-256 nem recarregam o GGML.

## Arquivos

| Status | Caminho |
|--------|---------|
| Criado | `app/src/main/java/br/com/banavita/app/core/speech/SilenceTrimmer.kt` |
| Criado | `app/src/main/java/br/com/banavita/app/core/speech/WhisperDecodeParams.kt` |
| Criado | `app/src/main/java/br/com/banavita/app/core/speech/SttDiagnostics.kt` |
| Criado | `app/src/test/java/br/com/banavita/app/core/speech/SilenceTrimmerTest.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/core/speech/SpeechConstants.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/core/speech/WhisperCppEngine.kt` |
| Alterado | `app/src/main/java/br/com/banavita/app/core/speech/WhisperLib.kt` |
| Alterado | `app/src/main/cpp/jni.c` |
| Alterado | `app/src/main/cpp/CMakeLists.txt` |
| Alterado | `app/build.gradle.kts` |
| Alterado | `app/src/main/java/br/com/banavita/app/data/repository/SpeechToTextRepository.kt` |
| Alterado | `app/src/test/java/br/com/banavita/app/data/repository/SpeechToTextRepositoryTest.kt` |

## O que conferir na IDE

1. Sync Gradle / rebuild nativo (`libbanavita_whisper.so` com `-O3`).
2. Rodar `SilenceTrimmerTest`, `WhisperDecodeParamsTest` (no mesmo arquivo) e `SpeechToTextRepositoryTest`.
3. Configurações: provedor **Local**. Segurar o mic ~3 s de fala num hold mais longo: transcrição deve ficar mais rápida que o buffer cheio.
4. Logcat filtro `BanavitaStt`: JSON com `audio_seconds`, `speech_seconds`, `inference_ms`, `rtf`, `threads` (telefone mascarado).
5. Segunda transcrição na mesma tela: `load_model_ms` baixo (modelo já em memória).
