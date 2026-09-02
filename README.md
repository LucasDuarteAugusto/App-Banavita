# App-Banavita

Aplicativo para controle offline do negócio Banavita.

## Stack

- Kotlin + Jetpack Compose
- Room (SQLite) com KSP
- Hilt + Coroutines/Flow
- Arquitetura MVVM/Clean
- ASR offline: whisper.cpp + GGML quantizado (PT-BR)

## Requisitos de build

- JDK 17+
- Android SDK (API 35)
- Android NDK (para whisper.cpp / arm64-v8a)
- Android Studio Ladybug ou superior (recomendado; este projeto é usado no **Quail**)
- Modelo ASR: `cd tools/speech-model && npm run download` (obrigatório antes do primeiro build com voz)

## Requisitos de dispositivo (IA local)

O app usa speech-to-text **offline**. Aparelhos com **4 GB de RAM não são suportados** para ASR. Só **arm64-v8a**. Espaço livre recomendado ≥ **500 MB** (modelo extraído ~57 MB + áudio temporário + banco). Permissão `RECORD_AUDIO` necessária para captura.

| Perfil | Exemplos reais | Hardware | Modelo ASR |
|--------|----------------|----------|------------|
| **Mínimo** (adotado agora) | Galaxy A17 8 GB, Galaxy A35 8 GB, mid-entry equivalentes | 8 GB RAM, 128 GB armazenamento, CPU ~Exynos 1330 / Helio G99 | **`ggml-base-q5_1`** (~57 MB no APK) |
| **Intermediário** (reservado) | Galaxy A54 / A55, Galaxy M54, Redmi Note 13 Pro | 8 GB+ RAM, UFS melhor | Futuro: `ggml-base-q8_0` ou `ggml-small-q5_1` |
| **Premium** (reservado) | Galaxy S23 / S24, A56 high-end, Pixel 7+ | 8–12 GB RAM, SoC forte | Futuro: `ggml-small-q8_0` ou `medium` quantizado |

Nesta versão só o perfil **mínimo** carrega modelo (`base-q5`). Intermediário/Premium estão no catálogo para versionamento futuro.

Ver [docs/speech/README.md](docs/speech/README.md) e [tools/speech-model/README.md](tools/speech-model/README.md).

## Estrutura

```
app/src/main/java/br/com/banavita/app/
  data/local/          # Room: entities, DAOs, database, view de estoque
  data/repository/     # Repositórios transacionais + speech
  domain/              # Modelos e exceções de domínio
  core/                # Money, tempo, segurança/ativação, speech (ASR)
  ui/                  # Compose: telas, navegação, voz
docs/database/         # Schema SQLite
docs/activation/       # Controle de acesso offline
docs/speech/           # ASR whisper.cpp PT-BR
docs/extraction/       # Extração heurística do transcript
docs/ui/               # Mockups das telas Compose
docs/android/          # Emulador, APK e instalação (Android Studio)
docs/Modifications/    # Log de alterações de código (revisão na IDE)
tools/license-generator/  # Gerador de licenças (PC)
tools/speech-model/       # Download do modelo GGML (PC)
```

## Rodar no emulador e gerar APK

Ver [docs/android/README.md](docs/android/README.md) (passos no Android Studio Quail: emulador, APK e instalação no celular).

## Banco de dados

Ver [docs/database/README.md](docs/database/README.md) para tabelas, FKs, view de estoque e regras de negócio.

## Ativação por dispositivo

Ver [docs/activation/README.md](docs/activation/README.md) e [tools/license-generator/README.md](tools/license-generator/README.md).

## Transcrição de voz (ASR)

Ver [docs/speech/README.md](docs/speech/README.md). Backend whisper.cpp, idioma forçado `pt`, modelo **embutido no APK** (extraído para `files/models/` na primeira execução).

## Extração / classificação

Ver [docs/extraction/README.md](docs/extraction/README.md). Heurísticas PT-BR + resolução no cadastro; draft só em RAM até confirmação.

## Etapas do projeto

- [x] Banco de dados (Room + repositórios)
- [x] Controle de acesso offline (backend + gerador Node.js)
- [x] IA local para transcrever áudios em português (backend ASR)
- [x] IA local para classificar e extrair informações (backend heurístico)
- [x] Navegação principal (Início, Vendas, Compras, Cadastros)
- [x] Tela inicial (atalho para nova venda)
- [x] Tela de cadastro de produtos
- [x] Tela de cadastro de clientes
- [x] Tela de cadastro de fornecedores
- [x] Tela de cadastro de grupos
- [x] Tela de consulta de estoque
- [x] Tela de cadastro de vendas
- [x] Tela de cadastro de compras
- [x] Tela de acompanhamento de vendas
- [x] Tela de acompanhamento de compras
- [x] Cadastro por voz (microfone nas listas e formulários novos)
- [x] Consultas (Estoque, Vendas, Compras, A receber, A pagar) aninhadas no Início
- [x] Financeiro real (à vista / a prazo + baixas)
- [x] Agenda local com lembrete (AlarmManager)
- [x] Voz nas superfícies da v2 (consulta, início, baixa, evento)

## Testes

```bash
./gradlew test
```

Testes de integração Room usam Robolectric + banco in-memory. Testes de ASR usam engine fake (sem NDK no unit test).
