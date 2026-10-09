# App-Banavita Development Guidelines

## Projeto e stack

Aplicativo Android nativo para o controle do negócio Banavita, com persistência local e operação offline. Abrange vendas, compras, clientes, fornecedores, produtos, grupos, financeiro, agenda, estoque perecível, câmaras, maturação, perdas e encomendas. A transcrição por Groq é uma opção online; os dados de negócio permanecem no banco local.

- **Android**: módulo único `:app`, application ID e namespace `br.com.banavita.app`; Android mínimo API 26, compile/target SDK 35.
- **Linguagem**: Kotlin 2.0.21, estilo oficial Kotlin e destino JVM 17.
- **Interface**: Jetpack Compose, Material 3, Compose BOM 2024.10.01, Navigation Compose 2.8.5 e AndroidX Lifecycle/ViewModel.
- **Arquitetura**: MVVM com separação entre UI, domínio, dados e utilitários; Coroutines 1.9.0, Flow/StateFlow e Hilt 2.52 para injeção de dependências.
- **Persistência**: SQLite via Room 2.6.1, geração com KSP 2.0.21-1.0.28; banco `banavita.db`, schema atual **8**, migrações explícitas de 1 até 8.
- **Preferências e segurança**: SharedPreferences para configurações, EncryptedSharedPreferences para ativação, Android Keystore e licenças assinadas com RSA/SHA-256.
- **Voz offline**: captura com AudioRecord, processamento PCM/WAV, whisper.cpp v1.7.6 via JNI (C/C++17), modelos GGML quantizados e idioma português.
- **Voz online**: Groq Whisper (`whisper-large-v3-turbo`) via OkHttp 4.12.0; extração dos campos feita por heurísticas locais PT-BR.
- **Lembretes**: AlarmManager, notificações e BroadcastReceivers para agenda, avisos de perecíveis e reagendamento após boot.
- **Backup**: ZIP contendo metadados e o arquivo SQLite, selecionado pela interface de documentos do Android.
- **Build**: Gradle Wrapper 8.13, Android Gradle Plugin 8.13.2, Gradle Kotlin DSL, Android NDK e CMake 3.22.1; ABI empacotada `arm64-v8a`.
- **Ferramentas auxiliares**: Node.js com scripts JavaScript ESM em `tools/` para baixar modelos e gerar licenças; não há servidor Node.js para os dados do app.
- **Testes**: JUnit 4, Robolectric, Room Testing, MockK, Turbine e kotlinx-coroutines-test; dependências AndroidX JUnit/Espresso para testes instrumentados.

As versões acima refletem a configuração deste checkout. Consulte `gradle/libs.versions.toml`, `app/build.gradle.kts`, o wrapper e `CMakeLists.txt` antes de atualizar dependências.

## Build / Test / Lint

Execute os comandos na raiz `App-Banavita`. No Windows/PowerShell, use `./gradlew.bat`; em Linux/macOS, use `./gradlew`.

- **Pré-requisitos**: JDK 17 compatível com o build, Android SDK 35, NDK, CMake 3.22.1 e Node.js para os utilitários. Configure o SDK local em `local.properties`, sem versionar caminhos da máquina.
- **Modelo offline**: `npm --prefix tools/speech-model run download -- base`. O script baixa o modelo para `tools/speech-model/models/`; o build copia o Base para `app/src/main/assets/models/` e verifica sua presença.
- **APK debug**: `./gradlew.bat :app:assembleDebug`; saída em `app/build/outputs/apk/debug/`.
- **APK release**: `./gradlew.bat :app:assembleRelease`; configure a assinatura necessária para distribuição.
- **Instalar debug**: `./gradlew.bat :app:installDebug` com dispositivo compatível conectado. A ABI atual exige `arm64-v8a`; emuladores x86/x86_64 não executam essa biblioteca nativa.
- **Testes locais**: `./gradlew.bat :app:testDebugUnitTest`.
- **Teste específico**: `./gradlew.bat :app:testDebugUnitTest --tests "br.com.banavita.app.data.repository.FinanceiroRepositoryTest"`.
- **Lint Android**: `./gradlew.bat :app:lintDebug`. O projeto não configura ktlint ou detekt.
- **Testes instrumentados**: `./gradlew.bat :app:connectedDebugAndroidTest`, quando houver testes em `app/src/androidTest/` e dispositivo/emulador compatível.
- **Sem modelo embutido**: acrescente `-PskipSpeechModel` para verificações que não dependam de ASR real. Isso pula a exigência do arquivo, mas não elimina o build nativo nem torna a voz offline funcional sem modelo.

O CMake usa FetchContent para obter whisper.cpp; o primeiro build pode precisar de acesso à rede. Testes locais de voz usam fakes e testes Room usam Robolectric com banco em memória, mas tarefas Gradle ainda dependem do ambiente Android configurado.

## Estrutura e arquitetura

Código Kotlin em `app/src/main/java/br/com/banavita/app/`:

- `ui/`: telas Compose, ViewModels, estados de UI, tema, navegação, shell e componentes de captura/revisão por voz.
- `domain/`: modelos de negócio, exceções e contratos, como o agendamento de lembretes.
- `data/local/`: banco Room, holder, migrações, entidades, DAOs, relações, views, conversores e fontes de preferências.
- `data/repository/`: operações de negócio, transações, estoque, financeiro, voz, extração e backup.
- `data/mapper/`, `data/cache/`, `data/backup/` e `data/remote/groq/`: conversões, rascunhos em memória, formato de backup e cliente remoto.
- `core/`: dinheiro, datas, maturação, segurança, áudio, transcrição, parsers PT-BR e lembretes.
- `di/`: módulos Hilt. `BanavitaApplication` e `MainActivity` inicializam o aplicativo.

Outros caminhos relevantes: `app/src/main/cpp/` (JNI/CMake), `app/src/test/` (testes locais), `app/schemas/` (schemas exportados pelo Room), `tools/license-generator/` e `tools/speech-model/`.

## Code Style / UI

- Siga o estilo Kotlin oficial e as convenções existentes: classes em PascalCase, funções/propriedades em camelCase e constantes em UPPER_SNAKE_CASE.
- Mantenha telas Compose voltadas à apresentação; estados e ações no ViewModel, regras e persistência nos repositórios, SQL nos DAOs.
- Reutilize Hilt, Coroutines/Flow, componentes em `ui/common/`, tema em `ui/theme/` e infraestrutura de voz em `ui/voice/`.
- Execute I/O e processamento pesado fora da thread principal; preserve o tratamento de erros com as exceções de domínio existentes.
- Preserve textos, formatos monetários, datas e interpretação de voz em PT-BR. Use os recursos Android existentes quando aplicável.
- As quatro rotas principais são Início, Vendas, Compras e Cadastros. Consulte `BanavitaRoutes` e `MainNavHost` ao alterar consultas, agenda, perecíveis ou deep links.
- Confira `.cursor/rules/ui-mockups.mdc` ao mudar telas: regenere os mockups correspondentes em `docs/ui/mockups/` e atualize `docs/ui/README.md`. Compare a regra com a tela atual antes de copiar layouts antigos.

## Banco e regras de negócio

- Preserve as migrações em `BanavitaMigrations.kt` e seu registro em `BanavitaDatabaseHolder`. Não adicione migração destrutiva para resolver divergências de schema.
- Ao mudar o schema, atualize a versão do banco, a migração, os schemas exportados e a documentação correspondente. O schema JSON versionado pode estar atrasado em relação ao código.
- IDs são UUID; datas de documentos usam epoch millis, e maturação usa epoch day no fuso local do aparelho. Quantidades aceitam frações.
- Valores monetários são `Long` em centavos. Use `Money` e seu arredondamento com BigDecimal; preserve os snapshots de preço dos itens.
- Venda/compra gravam cabeçalho, itens e efeitos relacionados em transações Room. À vista gera baixa financeira automática; a prazo tem saldo calculado pelos recebimentos/pagamentos.
- Estoque não perecível deriva das compras e vendas. Perecíveis usam lotes, saldo por estado/câmara e baixas FIFO; preserve a recusa de estoque insuficiente e a reversão das baixas ao editar/excluir vendas.
- Maturação avança na etileno e fica parada na fria. Confira `RipeningClock`, `MaturacaoEstado` e `LoteRepository` para estados, transferências e capacidades atuais; a documentação antiga descreve um fluxo anterior.
- Perda é movimento próprio, não venda de valor zero. Encomendas têm integração com agenda e validação de disponibilidade; preserve esses vínculos.
- Respeite as FKs RESTRICT/CASCADE/SET NULL, timestamps e verificações dos repositórios ao excluir registros.
- Restore substitui o banco, reabre Room e reagenda lembretes. Repositórios singleton devem resolver banco/DAOs por `Provider` para não manter referências à instância fechada.
- Preserve o checkpoint WAL e as validações de backup. O pacote atual contém o banco e metadados; não assuma que inclui preferências, licença ou modelos ASR.

## Voz e ativação

- O fluxo de voz é captura → transcrição local/Groq → extração heurística por contexto → revisão → confirmação. O rascunho fica em RAM (`ExtractionSessionCache`); não persista dados antes da confirmação.
- O build empacota somente `ggml-base-q5_1.bin`. Preferências usam Base por padrão, mas o catálogo também lista Tiny como ativo; confira assets, catálogo e seleção nas configurações em conjunto antes de alterar modelos.
- A chave Groq usada em execução vem das preferências preenchidas na tela de configurações (`EnvGroqConfig`). O build também gera `API_KEY` a partir de `.env` (`GROQ_API_KEY`, com alias legado `GROK_API_KEY`), mas o cliente atual não usa esse campo como fallback; não assuma que preencher `.env` basta para habilitar Groq.
- `GroqBuildFields.kt` é gerado por `generateGroqBuildFields` no preBuild e ignorado pelo Git. Não versione esse arquivo, `.env`, chaves privadas ou credenciais, nem registre áudio/transcrições sensíveis sem o mascaramento existente.
- A ativação é por dispositivo e validada offline com fingerprint, Keystore e assinatura RSA. Preserve os formatos `BAN1-REQ-` e `BAN1-LIC-` e a validade das licenças existentes.
- Não regenere o par de chaves como etapa rotineira de build: isso pode invalidar licenças já emitidas. Consulte `docs/activation/README.md` e `tools/license-generator/README.md` para manutenção desse fluxo.

## General Guidelines / Documentação

- Prefira alterações pequenas e coerentes com a arquitetura existente; reutilize utilitários e dependências antes de introduzir abstrações ou bibliotecas.
- Verifique as camadas afetadas e os efeitos em estoque, financeiro, agenda e backup antes de modificar regras compartilhadas.
- Execute testes existentes pertinentes e lint/build conforme o alcance da alteração. Valide voz nativa, permissões e notificações em dispositivo quando a mudança depender do Android real; fakes não comprovam esses comportamentos.
- Para criação/alteração de código, siga `.cursor/rules/modifications-log.mdc`: escreva `docs/Modifications/YYYY-MM-DD-titulo-curto.md` na mesma tarefa, com resumo, tabela de caminhos criados/alterados/removidos e orientações para revisão na IDE.
- Referências: `README.md`, `docs/android/`, `docs/database/`, `docs/speech/`, `docs/extraction/`, `docs/activation/`, `docs/ui/` e `docs/Modifications/`.
- Quando documentação e implementação divergirem, confirme no código e na configuração de build. Em particular, o schema atual é 8, apesar de `docs/database/README.md` ainda mencionar 4; o comando antigo `downloadWhisperModel` do README não corresponde às tarefas atuais Tiny/Base.

## Commit Messages / PR

- Prefira Conventional Commits: `type(scope): assunto`, com escopo opcional.
- Descreva o problema resolvido e o comportamento resultante; inclua verificações executadas e limitações relevantes para revisão.
- Não versione APKs, builds, modelos GGML ou configurações locais. Mantenha os logs de alterações e a documentação ligados ao resultado final.
