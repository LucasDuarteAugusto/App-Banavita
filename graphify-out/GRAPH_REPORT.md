# Graph Report - App-Banavita  (2026-09-12)

## Corpus Check
- 342 files · ~822,351 words
- Verdict: corpus is large enough that graph structure adds value.

## Summary
- 2064 nodes · 2981 edges · 190 communities (114 shown, 68 thin omitted)
- Extraction: 90% EXTRACTED · 10% INFERRED · 0% AMBIGUOUS · INFERRED: 289 edges (avg confidence: 0.8)
- Token cost: 0 input · 0 output

## Graph Freshness
- Built from commit: `e94bf98c`
- Run `git rev-parse HEAD` and compare to check if the graph is stale.
- Run `graphify update .` after code changes (no API cost).

## Community Hubs (Navigation)
- MinimalExternalModuleDependency
- BanavitaDatabase
- CompraFormViewModel
- EventoRepository
- ConsultasUiState.kt
- BanavitaDatabaseIntegrationTest
- ClienteFormViewModel
- ConsultasViewModel
- ReferencedEntityException
- ClienteDao
- EstoqueDao
- GrupoDao
- ProdutoDao
- FinanceiroDetalheViewModel
- 1. Internationalization & Android Errors (Priority: High)
- EventoDao
- ExtractionRepositoryTest
- FormScaffold
- FornecedorDao
- SpeechException
- ProdutoListScreen
- CompraListScreen
- VendaListScreen
- RecebimentoDao
- ExtractionRepository
- VoiceScreenContext
- FornecedorFormViewModel
- GrupoFormViewModel
- SettingsScreen
- AgendaAlarmReceiver
- Fix Room Migration Crash
- ConsultaVisao
- MainNavHost
- PeriodRanges
- DocumentLineItemCard
- ClienteListScreen
- FornecedorListScreen
- GrupoListScreen
- BanavitaDomainException
- .buildInputs
- ExtractionModels.kt
- PeriodoPreset
- ReviewEditor
- AmplitudeMeter
- Build do APK com modelo dinâmico
- GrupoRepository
- DateTimeInputParser
- .buildInputs
- Money
- DependencyNotationSupplier
- CadastrosHubScreen
- DocumentFormComponents.kt
- EntityListContent
- DateInputParser
- Deprecated
- GroqWhisperClientTest
- MoneyInputParserTest
- .readPcm16MonoAsFloat
- UuidConverters
- NetworkModule
- MoneyInputParser
- CapabilityNotationParser
- Correção de Erros de Inspeção da IDE e Internacionalização
- PtBrPromptHints
- QuantityInputParser
- WavWriterTest
- PeriodRangesTest
- AgendaNotifications.kt
- LicenseConstants.kt
- GroqSpeechConstants.kt
- SpeechConstants.kt
- CompraDao
- DefaultVersionCatalog
- ImmutableAttributesFactory
- Banco de dados Banavita (SQLite / Room)
- ExtractionDraft
- ListItemCard
- Agenda: chip "1 dia antes" na linha seguinte
- FieldSource
- ConsultasViewModel.kt
- Inject
- NonNullApi
- ObjectFactory
- PluginDependency
- Provider
- ProviderFactory
- DefaultVersionCatalog
- ImmutableAttributesFactory
- Inject
- MinimalExternalModuleDependency
- NonNullApi
- ObjectFactory
- Provider
- ProviderFactory
- PluginFactory
- SubDependencyFactory
- VersionFactory
- ExtractionFields
- ConsultaModo
- AbstractExternalDependencyFactory
- AndroidReminderScheduler
- .setUp
- DomainModels.kt
- VendaFormViewModel
- AgendaListScreen
- ExtractionField
- FinanceiroRepository
- Backlog Banavita v2 — Consultas e controle
- ProdutoFormViewModel
- BanavitaAppViewModel
- PluginDependency
- TranscriptionProvider
- VoiceEnabledScreen
- VendaDao
- SpeechModelManager
- EventoFormViewModel
- BanavitaApplication
- BundleFactory
- target-banavita_whisper-Debug-1fa5bd414745eca54479.json
- target-ggml-cpu-Debug-d4bfd337640b1b65c784.json
- target-ggml-Debug-fa01bffe7c2523a84ce1.json
- target-whisper-Debug-6c968dcce937548b7043.json
- target-banavita_whisper-Debug-62481e1ff4b4aef1e2ef.json
- target-ggml-cpu-Debug-80cdf7e92b0a2c3b4919.json
- target-ggml-Debug-6d50df7a23945da2320d.json
- target-whisper-Debug-51f6fc416bfc63491bb0.json
- SpeechToTextRepository
- Ativação offline Banavita
- CapabilityNotationParser
- LicenseCodec
- DeviceFingerprint
- WhisperEngine
- ActivationRepository
- ActivationViewModel
- generate-license.js
- EntityResolver
- LicenseValidator
- Modern Android Development Skill (Jetpack Compose & Room)
- Java_br_com_banavita_app_core_speech_WhisperLib_fullTranscribe
- Download do modelo Whisper GGML (Banavita)
- ASR Banavita (Groq remoto ou whisper.cpp local)
- license-generator/package.json
- download-model.js
- AudioCapture
- BanavitaRoutes
- GroqConfig
- ExtractionParsersTest
- Tabelas
- App-Banavita
- AndroidDeviceKeystoreManager
- LicenseValidatorTest
- Rodar o Banavita no Android Studio Quail
- Gerador de licenças Banavita (PC)
- LicensePublicKeyProvider
- ActivationRepositoryTest
- speech-model/package.json
- .floatMonoToWavBytes
- Extração heurística Banavita (backend)
- WhisperLib
- SpeechToTextRepositoryTest
- generate-keys.js
- Configurações: transcrição Groq ou Whisper local
- MainDispatcherRule
- TextNormalizer
- PcmConverter
- AmplitudeMeterTest
- Banavita v2 — consultas, financeiro, agenda e voz
- Android Studio — ícones Unresolved e warnings
- Voz: segurar para gravar, intensidade e modelo
- MoneyTest
- PcmConverterTest
- RoleKeywords
- Hashing
- PtBrPromptHintsTest
- UnitDictionary
- ExtractionModule.kt
- Modifications/README.md
- ui/README.md
- gradlew
- loadDotEnv
- migrate
- BanavitaIcons

## God Nodes (most connected - your core abstractions)
1. `ExtractionFields` - 32 edges
2. `BanavitaDatabase` - 27 edges
3. `ConfirmResult` - 25 edges
4. `BanavitaDatabaseIntegrationTest` - 24 edges
5. `VendaFormViewModel` - 23 edges
6. `CompraFormViewModel` - 22 edges
7. `DocumentoItemInput` - 21 edges
8. `VoiceEnabledScreen()` - 21 edges
9. `ReferencedEntityException` - 20 edges
10. `ExtractionField` - 20 edges

## Surprising Connections (you probably didn't know these)
- `now()` --calls--> `nowEpochMillis()`  [INFERRED]
  app/src/main/java/br/com/banavita/app/data/repository/DocumentoSupport.kt → app/src/main/java/br/com/banavita/app/core/time/TimeProvider.kt
- `AgendaListRoute()` --calls--> `RequestNotificationPermissionIfNeeded()`  [INFERRED]
  app/src/main/java/br/com/banavita/app/ui/agenda/AgendaListScreen.kt → app/src/main/java/br/com/banavita/app/ui/common/NotificationPermission.kt
- `addAgendaScreens()` --calls--> `AgendaListRoute()`  [INFERRED]
  app/src/main/java/br/com/banavita/app/ui/navigation/MainNavHost.kt → app/src/main/java/br/com/banavita/app/ui/agenda/AgendaListScreen.kt
- `AgendaListScreen()` --calls--> `EntityListContent()`  [INFERRED]
  app/src/main/java/br/com/banavita/app/ui/agenda/AgendaListScreen.kt → app/src/main/java/br/com/banavita/app/ui/common/EntityListContent.kt
- `AgendaListScreen()` --calls--> `formatDateTime()`  [INFERRED]
  app/src/main/java/br/com/banavita/app/ui/agenda/AgendaListScreen.kt → app/src/main/java/br/com/banavita/app/ui/common/Formatters.kt

## Import Cycles
- None detected.

## Communities (190 total, 68 thin omitted)

### Community 1 - "BanavitaDatabase"
Cohesion: 0.12
Nodes (7): BanavitaDatabase, Flow, PagamentoDao, PagamentoEntity, DatabaseModule, Context, RoomDatabase

### Community 2 - "CompraFormViewModel"
Cohesion: 0.15
Nodes (6): CompraFormRoute(), CompraFormScreen(), Modifier, CompraFormViewModel, StateFlow, ViewModel

### Community 3 - "EventoRepository"
Cohesion: 0.32
Nodes (3): EventoRepository, Evento, Flow

### Community 4 - "ConsultasUiState.kt"
Cohesion: 0.18
Nodes (15): formatCurrency(), formatDateTime(), formatQuantity(), AgregadoConsultaItem, Compra, ConsultasFilters, ConsultasNavEvent, ConsultasUiState (+7 more)

### Community 5 - "BanavitaDatabaseIntegrationTest"
Cohesion: 0.17
Nodes (3): DocumentoItemInput, BanavitaDatabaseIntegrationTest, FinanceiroRepositoryTest

### Community 6 - "ClienteFormViewModel"
Cohesion: 0.16
Nodes (7): ClienteFormRoute(), ClienteFormScreen(), Modifier, ClienteFormUiState, ClienteFormViewModel, StateFlow, ViewModel

### Community 7 - "ConsultasViewModel"
Cohesion: 0.14
Nodes (11): EstoqueOrdenacao, NOME_ASC, SALDO_ASC, ConsultasList(), ConsultasRoute(), ConsultasScreen(), Modifier, ConsultasViewModel (+3 more)

### Community 8 - "ReferencedEntityException"
Cohesion: 0.24
Nodes (4): nowEpochMillis(), FornecedorRepository, ProdutoRepository, ReferencedEntityException

### Community 9 - "ClienteDao"
Cohesion: 0.20
Nodes (3): ClienteDao, Flow, ClienteEntity

### Community 10 - "EstoqueDao"
Cohesion: 0.15
Nodes (4): EstoqueDao, Flow, EstoqueMovimentoRow, ProdutoEstoqueView

### Community 11 - "GrupoDao"
Cohesion: 0.18
Nodes (3): GrupoDao, Flow, GrupoEntity

### Community 12 - "ProdutoDao"
Cohesion: 0.18
Nodes (3): Flow, ProdutoDao, ProdutoEntity

### Community 13 - "FinanceiroDetalheViewModel"
Cohesion: 0.22
Nodes (8): FinanceiroDetalheRoute(), FinanceiroDetalheScreen(), Modifier, DialogState, FinanceiroDetalheUiState, FinanceiroDetalheViewModel, StateFlow, ViewModel

### Community 14 - "1. Internationalization & Android Errors (Priority: High)"
Cohesion: 0.13
Nodes (14): 1. Internationalization & Android Errors (Priority: High), 2. Kotlin & General Clean-up (Priority: Medium), 3. XML & Resource Warnings (Priority: Low), Automated Tests, Implementation Plan - Fixing IDE Inspection Results, Manual Verification, [MODIFY] Kotlin Files (Project-wide), [MODIFY] [strings.xml](file:///C:/projetos-Lucas/App-Banavita/app/src/main/res/values/strings.xml) (+6 more)

### Community 15 - "EventoDao"
Cohesion: 0.22
Nodes (3): EventoDao, Flow, EventoEntity

### Community 16 - "ExtractionRepositoryTest"
Cohesion: 0.16
Nodes (3): ClienteRepository, Flow, ExtractionRepositoryTest

### Community 17 - "FormScaffold"
Cohesion: 0.24
Nodes (11): FornecedorFormRoute(), FornecedorFormScreen(), Modifier, GrupoFormRoute(), GrupoFormScreen(), Modifier, DeleteConfirmDialog(), FormLoadingState() (+3 more)

### Community 18 - "FornecedorDao"
Cohesion: 0.22
Nodes (3): FornecedorDao, Flow, FornecedorEntity

### Community 19 - "SpeechException"
Cohesion: 0.23
Nodes (10): createDefaultHttpClient(), GroqWhisperClient, FloatArray, OkHttpClient, parseErrorMessage(), parseTranscriptText(), SpeechEngineException, SpeechException (+2 more)

### Community 20 - "ProdutoListScreen"
Cohesion: 0.22
Nodes (10): Modifier, ProdutoListItem(), ProdutoListRoute(), ProdutoListScreen(), ProdutoListItem, ProdutoListUiState, toListItem(), StateFlow (+2 more)

### Community 21 - "CompraListScreen"
Cohesion: 0.22
Nodes (10): CompraListItem(), CompraListRoute(), CompraListScreen(), Modifier, CompraListItem, CompraListUiState, toListItem(), CompraListViewModel (+2 more)

### Community 22 - "VendaListScreen"
Cohesion: 0.22
Nodes (10): Modifier, VendaListItem(), VendaListRoute(), VendaListScreen(), toListItem(), VendaListItem, VendaListUiState, StateFlow (+2 more)

### Community 23 - "RecebimentoDao"
Cohesion: 0.23
Nodes (3): Flow, RecebimentoDao, RecebimentoEntity

### Community 24 - "ExtractionRepository"
Cohesion: 0.25
Nodes (5): ExtractionRepository, ExtractionConfirmException, ExtractionDraftNotFoundException, ExtractionException, ExtractionParseException

### Community 25 - "VoiceScreenContext"
Cohesion: 0.15
Nodes (12): VoiceScreenContext, AGENDA, CLIENTE, COMPRA, CONSULTAS, FORNECEDOR, GRUPO, INICIO (+4 more)

### Community 26 - "FornecedorFormViewModel"
Cohesion: 0.18
Nodes (4): FornecedorFormUiState, FornecedorFormViewModel, StateFlow, ViewModel

### Community 27 - "GrupoFormViewModel"
Cohesion: 0.18
Nodes (4): GrupoFormUiState, GrupoFormViewModel, StateFlow, ViewModel

### Community 28 - "SettingsScreen"
Cohesion: 0.24
Nodes (8): Modifier, ProviderOptionCard(), SettingsRoute(), SettingsScreen(), SettingsUiState, StateFlow, ViewModel, SettingsViewModel

### Community 29 - "AgendaAlarmReceiver"
Cohesion: 0.43
Nodes (5): AgendaAlarmReceiver, ensureChannel(), BroadcastReceiver, Context, Intent

### Community 30 - "Fix Room Migration Crash"
Cohesion: 0.50
Nodes (3): Arquivos Alterados, Fix Room Migration Crash, O que conferir na IDE

### Community 31 - "ConsultaVisao"
Cohesion: 0.40
Nodes (5): ConsultaVisao, DOCUMENTOS, POR_PARCEIRO, POR_PRODUTO, VisaoChips()

### Community 32 - "MainNavHost"
Cohesion: 0.42
Nodes (10): addAgendaScreens(), addCadastroFormScreens(), addCadastrosNestedScreens(), addConsultasScreens(), addDocumentoFormScreens(), addSettingsScreen(), addTopLevelScreens(), Modifier (+2 more)

### Community 34 - "DocumentLineItemCard"
Cohesion: 0.29
Nodes (5): Flow, Produto, DocumentLineItemCard(), computeDocumentTotalCentavos(), DocumentLineItemFormState

### Community 35 - "ClienteListScreen"
Cohesion: 0.31
Nodes (8): ClienteListItem(), ClienteListRoute(), ClienteListScreen(), Modifier, ClienteListUiState, ClienteListViewModel, StateFlow, ViewModel

### Community 36 - "FornecedorListScreen"
Cohesion: 0.31
Nodes (8): FornecedorListItem(), FornecedorListRoute(), FornecedorListScreen(), Modifier, FornecedorListUiState, FornecedorListViewModel, StateFlow, ViewModel

### Community 37 - "GrupoListScreen"
Cohesion: 0.31
Nodes (8): GrupoListItem(), GrupoListRoute(), GrupoListScreen(), Modifier, GrupoListUiState, GrupoListViewModel, StateFlow, ViewModel

### Community 38 - "BanavitaDomainException"
Cohesion: 0.33
Nodes (4): BanavitaDomainException, InsufficientStockException, InvalidDocumentException, Exception

### Community 39 - ".buildInputs"
Cohesion: 0.25
Nodes (3): VendaFormUiState, InputsResult, InputsResult

### Community 40 - "ExtractionModels.kt"
Cohesion: 0.22
Nodes (15): AgendaFiltroAplicado, BaixaTipo, PAGAR, RECEBER, ClienteCriado, CompraCriada, ConfirmResult, ConsultaAplicada (+7 more)

### Community 41 - "PeriodoPreset"
Cohesion: 0.29
Nodes (6): PeriodoPreset, HOJE, INTERVALO, MES, SEMANA, PeriodoChips()

### Community 42 - "ReviewEditor"
Cohesion: 0.39
Nodes (7): edited(), EditText(), ExtractionReviewSheet(), Modifier, T, PrazoChips(), ReviewEditor()

### Community 44 - "Build do APK com modelo dinâmico"
Cohesion: 0.50
Nodes (4): 1. Baixar o modelo selecionado, 2. Gerar o APK, Build do APK com modelo dinâmico, Opções de modelo

### Community 47 - ".buildInputs"
Cohesion: 0.29
Nodes (3): CompraFormUiState, InputsResult, InputsResult

### Community 51 - "CadastrosHubScreen"
Cohesion: 0.53
Nodes (5): CadastroCard(), CadastroEntry, CadastrosHubScreen(), ImageVector, Modifier

### Community 52 - "DocumentFormComponents.kt"
Cohesion: 0.53
Nodes (5): DocumentDateField(), DocumentEntityDropdown(), DocumentTotalRow(), Modifier, SearchableEntityDropdown()

### Community 53 - "EntityListContent"
Cohesion: 0.60
Nodes (5): EntityListContent(), EntityListScreen(), Modifier, PaddingValues, T

### Community 63 - "Correção de Erros de Inspeção da IDE e Internacionalização"
Cohesion: 0.50
Nodes (3): Arquivos Modificados, Correção de Erros de Inspeção da IDE e Internacionalização, O que conferir na IDE

### Community 72 - "CompraDao"
Cohesion: 0.15
Nodes (5): CompraDao, Flow, CompraEntity, CompraItemEntity, CompraWithItems

### Community 75 - "Banco de dados Banavita (SQLite / Room)"
Cohesion: 0.22
Nodes (8): Banco de dados Banavita (SQLite / Room), Diagrama relacional, Pacotes Kotlin, Regras de negócio (backend), Schema exportado, View de estoque, Visão geral, `vw_produto_estoque`

### Community 76 - "ExtractionDraft"
Cohesion: 0.21
Nodes (3): ExtractionSessionCache, java, ExtractionDraft

### Community 77 - "ListItemCard"
Cohesion: 0.23
Nodes (11): Modifier, ListEmptyState(), ListItemCard(), ListLoadingState(), EstoqueDetalheRoute(), EstoqueDetalheScreen(), Modifier, EstoqueDetalheUiState (+3 more)

### Community 78 - "Agenda: chip "1 dia antes" na linha seguinte"
Cohesion: 0.40
Nodes (4): Agenda: chip "1 dia antes" na linha seguinte, Arquivos, O que conferir na IDE, Resumo

### Community 79 - "FieldSource"
Cohesion: 0.50
Nodes (4): FieldSource, DATABASE_MATCH, DICTIONARY, HEURISTIC

### Community 80 - "ConsultasViewModel.kt"
Cohesion: 0.50
Nodes (3): Catalogs, MasterCatalog, MoneyCatalog

### Community 103 - "ExtractionFields"
Cohesion: 0.16
Nodes (12): ExtractionPipeline, BaixaDraft, ClienteDraft, CompraDraft, ConsultasDraft, DraftLineItem, EventoDraft, ExtractionFields (+4 more)

### Community 119 - "ConsultaModo"
Cohesion: 0.18
Nodes (11): ConsultaModo, COMPRAS, ESTOQUE, PAGAR, RECEBER, VENDAS, EstoqueMovimentoTipo, ENTRADA (+3 more)

### Community 131 - "AndroidReminderScheduler"
Cohesion: 0.08
Nodes (13): AndroidReminderScheduler, Evento, BootCompletedReceiver, BroadcastReceiver, Context, Intent, ReminderModule, Evento (+5 more)

### Community 132 - ".setUp"
Cohesion: 0.13
Nodes (11): CompraRepository, Compra, Flow, DocumentoItemFactory, EstoqueValidator, now(), totalCompraCentavos(), totalVendaCentavos() (+3 more)

### Community 138 - "DomainModels.kt"
Cohesion: 0.10
Nodes (19): Compra, Evento, Venda, toDomain(), EstoqueRepository, Flow, Flow, Cliente (+11 more)

### Community 141 - "VendaFormViewModel"
Cohesion: 0.16
Nodes (6): Modifier, VendaFormRoute(), VendaFormScreen(), StateFlow, ViewModel, VendaFormViewModel

### Community 143 - "AgendaListScreen"
Cohesion: 0.14
Nodes (12): AgendaRecorte, HOJE, PROXIMOS_7_DIAS, TODOS, AgendaListRoute(), AgendaListScreen(), Modifier, AgendaListViewModel (+4 more)

### Community 168 - "ExtractionField"
Cohesion: 0.05
Nodes (30): ClienteExtractionParser, CompraExtractionParser, DocumentLineParser, FornecedorExtractionParser, GrupoExtractionParser, ParsedCompraSlots, ParsedGrupoSlots, ParsedLineItem (+22 more)

### Community 173 - "FinanceiroRepository"
Cohesion: 0.23
Nodes (5): FinanceiroRepository, Flow, OverpaymentException, DocumentoAberto, ParceiroSaldo

### Community 230 - "Backlog Banavita v2 — Consultas e controle"
Cohesion: 0.04
Nodes (46): 10.1 Permissão e canal, 10.2 Lembrete por evento, 10.3 Comportamento no celular, 10.4 Voz no lembrete, 10.5 Fora deste bloco (não fazer agora), 1.1 Destino Consultas, 1.2 Estoque sai de Cadastros, 1.3 Voz no hub (+38 more)

### Community 237 - "ProdutoFormViewModel"
Cohesion: 0.13
Nodes (7): Modifier, ProdutoFormRoute(), ProdutoFormScreen(), ProdutoFormUiState, StateFlow, ViewModel, ProdutoFormViewModel

### Community 241 - "BanavitaAppViewModel"
Cohesion: 0.08
Nodes (21): MainActivity, ActivationScreen(), copyRequestCode(), Context, Modifier, RequestCodeCard(), ActivationUiState, AppUiState (+13 more)

### Community 285 - "TranscriptionProvider"
Cohesion: 0.18
Nodes (5): AppSettingsLocalDataSource, AppSettingsRepository, TranscriptionProvider, GROQ, LOCAL

### Community 331 - "VoiceEnabledScreen"
Cohesion: 0.05
Nodes (40): android, HomeRoute(), HomeScreen(), ImageVector, Modifier, ShortcutCard(), Agenda, Consultas (+32 more)

### Community 360 - "VendaDao"
Cohesion: 0.18
Nodes (5): Flow, VendaDao, VendaEntity, VendaItemEntity, VendaWithItems

### Community 381 - "SpeechModelManager"
Cohesion: 0.10
Nodes (13): SpeechModelCatalog, SpeechModelManager, DeviceSpeechProfile, INTERMEDIATE, MINIMUM, PREMIUM, ModelStatus, INVALID_CHECKSUM (+5 more)

### Community 397 - "EventoFormViewModel"
Cohesion: 0.10
Nodes (7): EventoFormRoute(), EventoFormScreen(), Modifier, EventoFormUiState, EventoFormViewModel, StateFlow, ViewModel

### Community 437 - "target-banavita_whisper-Debug-1fa5bd414745eca54479.json"
Cohesion: 0.07
Nodes (26): artifacts, backtrace, backtraceGraph, commands, files, nodes, compileGroups, dependencies (+18 more)

### Community 438 - "target-ggml-cpu-Debug-d4bfd337640b1b65c784.json"
Cohesion: 0.06
Nodes (35): archive, artifacts, backtrace, backtraceGraph, commands, files, nodes, compileGroups (+27 more)

### Community 439 - "target-ggml-Debug-fa01bffe7c2523a84ce1.json"
Cohesion: 0.05
Nodes (36): archive, artifacts, backtrace, backtraceGraph, commands, files, nodes, compileGroups (+28 more)

### Community 440 - "target-whisper-Debug-6c968dcce937548b7043.json"
Cohesion: 0.06
Nodes (30): archive, artifacts, backtrace, backtraceGraph, commands, files, nodes, compileGroups (+22 more)

### Community 444 - "target-banavita_whisper-Debug-62481e1ff4b4aef1e2ef.json"
Cohesion: 0.07
Nodes (26): artifacts, backtrace, backtraceGraph, commands, files, nodes, compileGroups, dependencies (+18 more)

### Community 445 - "target-ggml-cpu-Debug-80cdf7e92b0a2c3b4919.json"
Cohesion: 0.06
Nodes (35): archive, artifacts, backtrace, backtraceGraph, commands, files, nodes, compileGroups (+27 more)

### Community 446 - "target-ggml-Debug-6d50df7a23945da2320d.json"
Cohesion: 0.05
Nodes (36): archive, artifacts, backtrace, backtraceGraph, commands, files, nodes, compileGroups (+28 more)

### Community 447 - "target-whisper-Debug-51f6fc416bfc63491bb0.json"
Cohesion: 0.06
Nodes (30): archive, artifacts, backtrace, backtraceGraph, commands, files, nodes, compileGroups (+22 more)

### Community 468 - "SpeechToTextRepository"
Cohesion: 0.33
Nodes (3): FloatArray, SpeechToTextRepository, TranscriptionResult

### Community 469 - "Ativação offline Banavita"
Cohesion: 0.07
Nodes (29): 1. Gerar par de chaves (uma vez no PC), 2. Instalar o APK no celular, 3. Copiar o código do aparelho, 4. Gerar a licença no PC, 5. Ativar no celular, API do backend (`ActivationRepository`), Ativação offline Banavita, Componentes Android (+21 more)

### Community 518 - "LicenseCodec"
Cohesion: 0.33
Nodes (3): LicenseCodec, ParsedLicense, InvalidLicenseException

### Community 527 - "DeviceFingerprint"
Cohesion: 0.18
Nodes (5): AndroidDeviceFingerprintProvider, DeviceFingerprint, DeviceFingerprintProvider, LicenseCodecTest, FixedDeviceFingerprintProvider

### Community 573 - "WhisperEngine"
Cohesion: 0.14
Nodes (5): FloatArray, WhisperCppEngine, FloatArray, WhisperEngine, SpeechModule

### Community 575 - "ActivationViewModel"
Cohesion: 0.15
Nodes (5): ActivationRequest, ActivationViewModel, StateFlow, ViewModel, ActivationViewModelTest

### Community 577 - "generate-license.js"
Cohesion: 0.22
Nodes (14): __dirname, KEYS_DIR, loadPrivateKey(), main(), normalizeType(), parseArgs(), printUsage(), PRIVATE_KEY_PATH (+6 more)

### Community 597 - "EntityResolver"
Cohesion: 0.32
Nodes (3): EntityResolver, EntityCandidate, ResolvedEntity

### Community 604 - "LicenseValidator"
Cohesion: 0.16
Nodes (10): ByteArray, LicenseValidator, ValidatedLicense, ActivationException, DeviceMismatchException, LicenseExpiredException, LicensePayload, LicenseType (+2 more)

### Community 605 - "Modern Android Development Skill (Jetpack Compose & Room)"
Cohesion: 0.13
Nodes (14): 1. Camada de Dados (Room ORM), 1. Regras para Testes de Data/Room, 2. Camada de UI (Jetpack Compose), 2. Regras para Testes de ViewModel, 3. Conectividade (ViewModel & Coroutines), Code Templates & Examples, Description, Exemplo de Entidade e DAO (Room) (+6 more)

### Community 639 - "Java_br_com_banavita_app_core_speech_WhisperLib_fullTranscribe"
Cohesion: 0.37
Nodes (12): Java_br_com_banavita_app_core_speech_WhisperLib_freeContext(), Java_br_com_banavita_app_core_speech_WhisperLib_fullTranscribe(), Java_br_com_banavita_app_core_speech_WhisperLib_getTextSegment(), Java_br_com_banavita_app_core_speech_WhisperLib_getTextSegmentCount(), Java_br_com_banavita_app_core_speech_WhisperLib_initContext(), jfloatArray, jint, jlong (+4 more)

### Community 640 - "Download do modelo Whisper GGML (Banavita)"
Cohesion: 0.29
Nodes (7): Baixar e copiar para assets, Download do modelo Whisper GGML (Banavita), Gerar o APK, No aparelho, Pré-requisitos, Relação com tiers de hardware, Sidecar manual (legado / debug)

### Community 641 - "ASR Banavita (Groq remoto ou whisper.cpp local)"
Cohesion: 0.15
Nodes (13): API (`SpeechToTextRepository`), ASR Banavita (Groq remoto ou whisper.cpp local), Chave Groq, Conferir com `adb`, Conferir sem `adb` (recomendado), Exceções, Extração no aparelho (e por que a pasta USB `data` parece vazia), Limites (+5 more)

### Community 642 - "license-generator/package.json"
Cohesion: 0.15
Nodes (12): bin, banavita-license, description, engines, node, name, private, scripts (+4 more)

### Community 643 - "download-model.js"
Cohesion: 0.22
Nodes (12): ASSETS_DIR, CATALOG, CHECKSUMS_PATH, copyToAssets(), __dirname, download(), main(), MODELS_DIR (+4 more)

### Community 669 - "AudioCapture"
Cohesion: 0.33
Nodes (7): AudioCapture, FloatArray, Job, StateFlow, RecordingLevels, SpeechAudioException, AudioRecord

### Community 674 - "Tabelas"
Cohesion: 0.17
Nodes (12): `cliente`, `compra` (cabeçalho), `compra_item`, `evento` (schema v3), `fornecedor`, `grupo`, `pagamento` (schema v2), `produto` (+4 more)

### Community 675 - "App-Banavita"
Cohesion: 0.17
Nodes (12): App-Banavita, Ativação por dispositivo, Banco de dados, Estrutura, Etapas do projeto, Extração / classificação, Requisitos de build, Requisitos de dispositivo (IA local) (+4 more)

### Community 696 - "AndroidDeviceKeystoreManager"
Cohesion: 0.22
Nodes (5): AndroidDeviceKeystoreManager, DeviceKeystoreManager, KeystoreFingerprint, SecurityModule, KeyStore

### Community 697 - "LicenseValidatorTest"
Cohesion: 0.27
Nodes (3): FixedDeviceFingerprintProvider, LicenseValidatorTest, PrivateKey

### Community 698 - "Rodar o Banavita no Android Studio Quail"
Cohesion: 0.11
Nodes (18): 1. Abrir o projeto, 2. Instalar SDK, NDK e CMake, 3. Baixar o modelo de voz (obrigatório no APK com voz), A. Instalar pelo cabo (mais simples no dia a dia), Antes de tudo, APK de release (assinado), B. Gerar o APK e copiar o arquivo (sem depender do Run), Criar o AVD (+10 more)

### Community 700 - "Gerador de licenças Banavita (PC)"
Cohesion: 0.18
Nodes (11): Entrada manual (sem request code), Fluxo operacional, Gerador de licenças Banavita (PC), Gerar licença mensal (30 dias após ativação), Gerar licença permanente, Pré-requisitos, Regenerar chaves (produção), Salvar em arquivo (+3 more)

### Community 727 - "LicensePublicKeyProvider"
Cohesion: 0.36
Nodes (4): DefaultLicensePublicKeyProvider, LicensePublicKeyLoader, LicensePublicKeyProvider, PublicKey

### Community 728 - "ActivationRepositoryTest"
Cohesion: 0.21
Nodes (4): ActivationLocalDataSource, StoredActivation, ActivationRepositoryTest, java

### Community 729 - "speech-model/package.json"
Cohesion: 0.20
Nodes (9): description, engines, node, name, private, scripts, download, type (+1 more)

### Community 760 - ".floatMonoToWavBytes"
Cohesion: 0.40
Nodes (3): ByteArray, FloatArray, WavWriter

### Community 771 - "Extração heurística Banavita (backend)"
Cohesion: 0.25
Nodes (8): API, Contextos, Evolução, Exemplos, Extração heurística Banavita (backend), Fluxo, Pacotes, Por que não MediaPipe nesta etapa

### Community 804 - "SpeechToTextRepositoryTest"
Cohesion: 0.33
Nodes (3): FakeWhisperEngine, FloatArray, SpeechToTextRepositoryTest

### Community 805 - "generate-keys.js"
Cohesion: 0.25
Nodes (6): ANDROID_CONSTANTS_PATH, __dirname, KEYS_DIR, PRIVATE_KEY_PATH, PUBLIC_KEY_PATH, ROOT

### Community 816 - "Configurações: transcrição Groq ou Whisper local"
Cohesion: 0.33
Nodes (5): Alterado, Configurações: transcrição Groq ou Whisper local, Criado, O que conferir na IDE, Removido

### Community 823 - "MainDispatcherRule"
Cohesion: 0.40
Nodes (3): MainDispatcherRule, Description, TestWatcher

### Community 862 - "PcmConverter"
Cohesion: 0.40
Nodes (3): ByteArray, FloatArray, PcmConverter

### Community 864 - "Banavita v2 — consultas, financeiro, agenda e voz"
Cohesion: 0.33
Nodes (5): Alterado, Banavita v2 — consultas, financeiro, agenda e voz, Criado, O que conferir na IDE, Removido

### Community 865 - "Android Studio — ícones Unresolved e warnings"
Cohesion: 0.33
Nodes (5): Alterado, Android Studio — ícones Unresolved e warnings, Criado, O que conferir na IDE, Removido

### Community 866 - "Voz: segurar para gravar, intensidade e modelo"
Cohesion: 0.33
Nodes (5): Alterado, Criado, O que conferir na IDE, Removido, Voz: segurar para gravar, intensidade e modelo

## Knowledge Gaps
- **475 isolated node(s):** `Arquivos Alterados`, `O que conferir na IDE`, `artifacts`, `backtrace`, `add_library` (+470 more)
  These have ≤1 connection - possible missing edges or undocumented components. (Counts symbols only; 943 node(s) total have ≤1 connection when file, concept and rationale nodes are included.)
- **68 thin communities (<3 nodes) omitted from report** — run `graphify query` to explore isolated nodes.

## Suggested Questions
_Questions this graph is uniquely positioned to answer:_

- **Why does `VoiceScreenContext` connect `VoiceScreenContext` to `ExtractionModels.kt`, `VoiceEnabledScreen`, `ExtractionFields`?**
  _High betweenness centrality (0.090) - this node is a cross-community bridge._
- **Why does `VoiceEnabledScreen()` connect `VoiceEnabledScreen` to `ClienteListScreen`, `FornecedorListScreen`, `GrupoListScreen`, `ConsultasViewModel`, `ReviewEditor`, `AgendaListScreen`, `ProdutoListScreen`, `CompraListScreen`, `VendaListScreen`, `VoiceScreenContext`?**
  _High betweenness centrality (0.062) - this node is a cross-community bridge._
- **Why does `nowEpochMillis()` connect `ReferencedEntityException` to `AndroidReminderScheduler`, `.setUp`, `EventoRepository`, `ExtractionFields`, `ClienteDao`, `GrupoDao`, `FinanceiroRepository`, `GrupoRepository`, `ExtractionRepositoryTest`, `ExtractionRepository`?**
  _High betweenness centrality (0.059) - this node is a cross-community bridge._
- **What connects `Arquivos Alterados`, `O que conferir na IDE`, `artifacts` to the rest of the system?**
  _475 weakly-connected nodes found - possible documentation gaps or missing edges._
- **Should `BanavitaDatabase` be split into smaller, more focused modules?**
  _Cohesion score 0.1225296442687747 - nodes in this community are weakly interconnected._
- **Should `CompraFormViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.14761904761904762 - nodes in this community are weakly interconnected._
- **Should `ConsultasViewModel` be split into smaller, more focused modules?**
  _Cohesion score 0.14285714285714285 - nodes in this community are weakly interconnected._