# Voz: segurar para gravar, intensidade e modelo

Troca o toque de 8 s fixos por pressionar-e-segurar (timer + barras de RMS). Ao soltar, o overlay mostra só o spinner de transcrição. O modelo Whisper continua nos assets do APK e é extraído no armazenamento privado na primeira tela com mic.

## Criado

| Caminho |
|---------|
| `app/src/main/java/br/com/banavita/app/core/speech/AmplitudeMeter.kt` |
| `app/src/main/java/br/com/banavita/app/ui/voice/VoiceRecordingBar.kt` |
| `app/src/test/java/br/com/banavita/app/core/speech/AmplitudeMeterTest.kt` |
| `docs/ui/mockups/voice-recording.png` |
| `docs/ui/mockups/voice-transcribing.png` |

## Alterado

| Caminho |
|---------|
| `app/src/main/java/br/com/banavita/app/core/speech/AudioCapture.kt` |
| `app/src/main/java/br/com/banavita/app/core/speech/SpeechModelManager.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/SpeechToTextRepository.kt` |
| `app/src/main/java/br/com/banavita/app/ui/voice/VoiceCaptureViewModel.kt` |
| `app/src/main/java/br/com/banavita/app/ui/voice/VoiceCaptureUiState.kt` |
| `app/src/main/java/br/com/banavita/app/ui/voice/VoiceEnabledScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/voice/VoiceFabLayout.kt` |
| `app/src/main/res/values/strings.xml` |
| `app/src/test/java/br/com/banavita/app/data/repository/SpeechToTextRepositoryTest.kt` |
| `docs/speech/README.md` (extração no aparelho, USB `data` vazia, `adb` no Git Bash) |
| `docs/android/README.md` |
| `docs/ui/README.md` |
| `tools/speech-model/README.md` |
| `README.md` |

## Removido

| Caminho |
|---------|
| `SpeechToTextRepository.transcribeMicrophone` (janela fixa) |
| Overlay central “Gravando… fale agora” / `RecordingPulse` |

## O que conferir na IDE

1. Telas com mic (Início, listas, formulários novos): **segurar** o botão verde, ver ponto vermelho + timer + barras; **soltar** e ver spinner **Transcrevendo…**.
2. Toque curto: banner “Segure o botão para falar”.
3. Primeira abertura após instalar: Logcat `BanavitaSpeech` com o path em `files/models/` (Device File Explorer, não a pasta USB `data`).
4. Sync não é obrigatório (sem mudança Gradle). Rodar no Galaxy A17 de novo.
