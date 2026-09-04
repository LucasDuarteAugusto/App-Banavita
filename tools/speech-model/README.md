# Download do modelo Whisper GGML (Banavita)

Ferramenta de PC para obter `ggml-base-q5_1.bin` e prepará-lo para **embutir no APK**.

## Pré-requisitos

- Node.js 14+
- JDK 17+ e Android SDK (para gerar o APK)

## Baixar e copiar para assets

```bash
cd tools/speech-model
npm run download
```

Isso grava:

- `models/ggml-base-q5_1.bin` (~57 MB, gitignored)
- `checksums.json` com SHA-256
- cópia em `app/src/main/assets/models/ggml-base-q5_1.bin` (gitignored)

SHA-256 esperado:

```
422f1ae452ade6f30a004d7e5c6a43195e4433bc370bf23fac9cc591f01a8898
```

Deve coincidir com `SpeechConstants.ACTIVE_MODEL_SHA256` no app.

## Gerar o APK

No Android Studio: **Build → Make Project** ou **Build APK**.

Via Gradle:

```bash
./gradlew assembleDebug
```

O task `preBuild` copia o modelo de `tools/speech-model/models/` para assets (se ainda não estiver lá) e falha com instruções claras se o `.bin` estiver ausente.

Para build **sem** modelo (UI sem voz):

```bash
./gradlew assembleDebug -PskipSpeechModel
```

## No aparelho

Na **primeira tela com microfone**, o app extrai o modelo para o armazenamento **privado**:

```
/data/data/br.com.banavita.app/files/models/ggml-base-q5_1.bin
```

O explorador MTP (cabo USB) não mostra esse diretório; pasta `data` vazia no PC é esperado. Como conferir (Device File Explorer, Logcat, `adb` no Git Bash se o comando não for reconhecido): [docs/speech/README.md](../../docs/speech/README.md).

Logcat: `BanavitaSpeech`. Não é necessário `adb push` em instalações normais.

### Sidecar manual (legado / debug)

Útil se você buildou com `-PskipSpeechModel` ou quer testar um `.bin` alternativo:

```bash
adb push models/ggml-base-q5_1.bin /data/local/tmp/ggml-base-q5_1.bin
adb shell run-as br.com.banavita.app mkdir -p files/models
adb shell run-as br.com.banavita.app cp /data/local/tmp/ggml-base-q5_1.bin files/models/ggml-base-q5_1.bin
```

## Relação com tiers de hardware

| Perfil | Modelo | Status |
|--------|--------|--------|
| Mínimo | `ggml-base-q5_1` | Ativo (este script) |
| Intermediário | `base-q8` / `small-q5` | Reservado |
| Premium | `small-q8` / `medium` | Reservado |

Ver [docs/speech/README.md](../../docs/speech/README.md).
