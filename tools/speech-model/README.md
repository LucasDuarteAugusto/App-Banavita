# Download do modelo Whisper GGML (Banavita)

Ferramenta de PC para obter `ggml-base-q5_1.bin` e instalar como **sidecar** no aparelho.

## Pré-requisitos

- Node.js 14+
- `adb` (Android platform-tools) para enviar ao celular

## Baixar o modelo

```bash
cd tools/speech-model
npm run download
```

Isso grava:

- `models/ggml-base-q5_1.bin` (~57 MB, gitignored)
- `checksums.json` com SHA-256

SHA-256 esperado:

```
422f1ae452ade6f30a004d7e5c6a43195e4433bc370bf23fac9cc591f01a8898
```

Deve coincidir com `SpeechConstants.ACTIVE_MODEL_SHA256` no app.

## Instalar no aparelho (sidecar)

O APK **não** embute o modelo. Path no app:

```
/data/data/br.com.banavita.app/files/models/ggml-base-q5_1.bin
```

Fluxo típico (build debug):

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
