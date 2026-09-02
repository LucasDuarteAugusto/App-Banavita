# Banavita v2 — consultas, financeiro, agenda e voz

Hub de Consultas aninhado no Início (sem quinta aba), estoque fora de Cadastros, revisão de voz editável, financeiro à vista/a prazo com baixas (Room v2) e agenda local com AlarmManager (Room v3). A voz entra em cada superfície nova no mesmo ciclo da v1.

## Criado

| Caminho |
|---------|
| `app/src/main/java/br/com/banavita/app/core/extraction/V2Parsers.kt` |
| `app/src/main/java/br/com/banavita/app/core/reminder/AgendaAlarmReceiver.kt` |
| `app/src/main/java/br/com/banavita/app/core/reminder/AgendaNotifications.kt` |
| `app/src/main/java/br/com/banavita/app/core/reminder/AndroidReminderScheduler.kt` |
| `app/src/main/java/br/com/banavita/app/core/reminder/BootCompletedReceiver.kt` |
| `app/src/main/java/br/com/banavita/app/core/time/PeriodRanges.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/BanavitaMigrations.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/dao/EstoqueMovimentoRow.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/dao/EventoDao.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/dao/PagamentoDao.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/dao/RecebimentoDao.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/entity/EventoEntity.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/entity/PagamentoEntity.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/entity/RecebimentoEntity.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/EventoRepository.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/FinanceiroRepository.kt` |
| `app/src/main/java/br/com/banavita/app/di/ReminderModule.kt` |
| `app/src/main/java/br/com/banavita/app/domain/model/ConsultaModels.kt` |
| `app/src/main/java/br/com/banavita/app/domain/reminder/ReminderScheduler.kt` |
| `app/src/main/java/br/com/banavita/app/ui/agenda/` (lista, form, ViewModels) |
| `app/src/main/java/br/com/banavita/app/ui/common/DateTimeInputParser.kt` |
| `app/src/main/java/br/com/banavita/app/ui/common/NotificationPermission.kt` |
| `app/src/main/java/br/com/banavita/app/ui/consultas/` (hub, estoque detalhe, financeiro detalhe) |
| `app/src/main/java/br/com/banavita/app/ui/home/HomeUiState.kt` |
| `app/src/main/java/br/com/banavita/app/ui/home/HomeViewModel.kt` |
| `app/src/test/java/br/com/banavita/app/core/time/PeriodRangesTest.kt` |
| `app/src/test/java/br/com/banavita/app/data/repository/EventoRepositoryTest.kt` |
| `app/src/test/java/br/com/banavita/app/data/repository/FinanceiroRepositoryTest.kt` |
| `app/src/test/java/br/com/banavita/app/domain/reminder/FakeReminderScheduler.kt` |
| `docs/ui/mockups/consultas-estoque.png` |
| `docs/ui/mockups/consultas-vendas.png` |
| `docs/ui/mockups/consultas-receber.png` |
| `docs/ui/mockups/estoque-detalhe.png` |
| `docs/ui/mockups/agenda-list.png` |
| `docs/ui/mockups/agenda-form.png` |

## Alterado

| Caminho |
|---------|
| `README.md` |
| `app/src/main/AndroidManifest.xml` |
| `app/src/main/java/br/com/banavita/app/BanavitaApplication.kt` |
| `app/src/main/java/br/com/banavita/app/core/extraction/ContextParsers.kt` |
| `app/src/main/java/br/com/banavita/app/core/extraction/ExtractionPipeline.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/BanavitaDatabase.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/dao/EstoqueDao.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/entity/CompraEntity.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/entity/VendaEntity.kt` |
| `app/src/main/java/br/com/banavita/app/data/mapper/EntityMappers.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/CompraRepository.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/EstoqueRepository.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/ExtractionRepository.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/VendaRepository.kt` |
| `app/src/main/java/br/com/banavita/app/di/DatabaseModule.kt` |
| `app/src/main/java/br/com/banavita/app/domain/exception/BanavitaDomainException.kt` |
| `app/src/main/java/br/com/banavita/app/domain/model/DomainModels.kt` |
| `app/src/main/java/br/com/banavita/app/domain/model/extraction/ExtractionModels.kt` |
| `app/src/main/java/br/com/banavita/app/ui/cadastros/CadastrosHubScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/cadastros/clientes/ClienteFormScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/cadastros/fornecedores/FornecedorFormScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/cadastros/grupos/GrupoFormScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/cadastros/produtos/ProdutoFormScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/common/Formatters.kt` |
| `app/src/main/java/br/com/banavita/app/ui/common/ListComponents.kt` |
| `app/src/main/java/br/com/banavita/app/ui/compras/CompraFormScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/compras/CompraFormUiState.kt` |
| `app/src/main/java/br/com/banavita/app/ui/compras/CompraFormViewModel.kt` |
| `app/src/main/java/br/com/banavita/app/ui/home/HomeScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/navigation/BanavitaRoutes.kt` |
| `app/src/main/java/br/com/banavita/app/ui/navigation/MainNavHost.kt` |
| `app/src/main/java/br/com/banavita/app/ui/shell/MainAppShell.kt` |
| `app/src/main/java/br/com/banavita/app/ui/vendas/VendaFormScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/vendas/VendaFormUiState.kt` |
| `app/src/main/java/br/com/banavita/app/ui/vendas/VendaFormViewModel.kt` |
| `app/src/main/java/br/com/banavita/app/ui/voice/ExtractionDraftFormatter.kt` |
| `app/src/main/java/br/com/banavita/app/ui/voice/ExtractionReviewSheet.kt` |
| `app/src/main/java/br/com/banavita/app/ui/voice/VoiceCaptureViewModel.kt` |
| `app/src/main/java/br/com/banavita/app/ui/voice/VoiceEnabledScreen.kt` |
| `app/src/main/res/values/strings.xml` |
| `app/src/test/java/br/com/banavita/app/BanavitaDatabaseIntegrationTest.kt` |
| `app/src/test/java/br/com/banavita/app/core/extraction/ExtractionParsersTest.kt` |
| `app/src/test/java/br/com/banavita/app/data/repository/ExtractionRepositoryTest.kt` |
| `docs/database/README.md` |
| `docs/extraction/README.md` |
| `docs/ui/README.md` |
| `docs/ui/mockups/home.png` |
| `docs/ui/mockups/cadastros-hub.png` |
| `docs/ui/mockups/voice-review-sheet.png` |
| `docs/ui/mockups/venda-form-novo.png` |
| `docs/ui/mockups/compra-form-novo.png` |

## Removido

| Caminho |
|---------|
| `app/src/main/java/br/com/banavita/app/ui/cadastros/estoque/EstoqueListScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/cadastros/estoque/EstoqueListUiState.kt` |
| `app/src/main/java/br/com/banavita/app/ui/cadastros/estoque/EstoqueListViewModel.kt` |
| `docs/ui/mockups/estoque-list.png` |

## O que conferir na IDE

1. Sync Gradle (schema Room **3**; `MIGRATION_1_2` + `MIGRATION_2_3`; sem `fallbackToDestructiveMigration`).
2. Navegação: Início → cartões de consulta (bottom bar some, seta voltar). Cadastros **sem** card Estoque.
3. Consultas: chips Estoque / Vendas / Compras / A receber / A pagar; busca; período e visões em vendas/compras; zerados no estoque; detalhe com movimentações.
4. Nova venda/compra: chips **Pago agora** / **A prazo**. Docs v1 existentes entram como à vista.
5. Agenda: CRUD, recortes, permissão de notificação, deep link `banavita://agenda/edit/{id}`.
6. Mic em Início, Consultas, Agenda e forms novos: sheet **editável**; confirmar consulta não grava; confirmar baixa/evento grava.
7. `./gradlew test` (JDK 17 no Android Studio).
