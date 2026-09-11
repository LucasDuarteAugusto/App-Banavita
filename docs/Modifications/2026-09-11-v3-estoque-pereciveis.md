# Banavita v3 — estoque de perecíveis

Estoque de banana por **lote + câmara + estado**, sem criar um produto por cor. Compra perecível entra verde na fria; o relógio só anda na etileno; venda exige estado e baixa FIFO; perda, aviso matinal, queima do dia, previsão, relatório e encomenda entram no mesmo app (sem quinta aba).

## Criado

| Caminho |
|---------|
| `app/src/main/java/br/com/banavita/app/core/reminder/PereciveisAlarmReceiver.kt` |
| `app/src/main/java/br/com/banavita/app/core/reminder/PereciveisMorningScheduler.kt` |
| `app/src/main/java/br/com/banavita/app/core/reminder/PereciveisNotifications.kt` |
| `app/src/main/java/br/com/banavita/app/core/time/RipeningClock.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/dao/LoteDao.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/dao/PerecivelDaos.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/entity/EncomendaEntity.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/entity/LoteEntity.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/entity/PerecivelEntities.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/relation/EncomendaWithItems.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/EncomendaRepository.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/LoteRepository.kt` |
| `app/src/main/java/br/com/banavita/app/domain/model/PereciveisModels.kt` |
| `app/src/main/java/br/com/banavita/app/ui/common/PereciveisUi.kt` |
| `app/src/main/java/br/com/banavita/app/ui/pereciveis/encomenda/EncomendaFormScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/pereciveis/encomenda/EncomendaFormViewModel.kt` |
| `app/src/main/java/br/com/banavita/app/ui/pereciveis/etileno/EtilenoFilaScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/pereciveis/etileno/EtilenoFilaViewModel.kt` |
| `app/src/main/java/br/com/banavita/app/ui/pereciveis/perda/PerdaFormScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/pereciveis/perda/PerdaFormViewModel.kt` |
| `app/src/test/java/br/com/banavita/app/PereciveisIntegrationTest.kt` |
| `app/src/test/java/br/com/banavita/app/TestRepositories.kt` |
| `app/src/test/java/br/com/banavita/app/core/time/RipeningClockTest.kt` |
| `docs/ui/mockups/consultas-encomendas.png` |
| `docs/ui/mockups/consultas-perdas.png` |
| `docs/ui/mockups/consultas-previsao.png` |
| `docs/ui/mockups/encomenda-form.png` |
| `docs/ui/mockups/encomenda-form-editar.png` |
| `docs/ui/mockups/etileno-fila.png` |
| `docs/ui/mockups/perda-form.png` |

## Alterado

| Caminho |
|---------|
| `app/src/main/AndroidManifest.xml` |
| `app/src/main/java/br/com/banavita/app/BanavitaApplication.kt` |
| `app/src/main/java/br/com/banavita/app/core/extraction/ContextParsers.kt` |
| `app/src/main/java/br/com/banavita/app/core/extraction/EntityResolver.kt` |
| `app/src/main/java/br/com/banavita/app/core/extraction/ExtractionPipeline.kt` |
| `app/src/main/java/br/com/banavita/app/core/extraction/V2Parsers.kt` |
| `app/src/main/java/br/com/banavita/app/core/reminder/BootCompletedReceiver.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/BanavitaDatabase.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/BanavitaDatabaseHolder.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/BanavitaMigrations.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/dao/EstoqueDao.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/entity/ClienteEntity.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/entity/ProdutoEntity.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/entity/VendaItemEntity.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/settings/AppSettingsLocalDataSource.kt` |
| `app/src/main/java/br/com/banavita/app/data/local/view/ProdutoEstoqueView.kt` |
| `app/src/main/java/br/com/banavita/app/data/mapper/EntityMappers.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/AppSettingsRepository.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/ClienteRepository.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/CompraRepository.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/DatabaseBackupRepository.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/DocumentoSupport.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/EstoqueRepository.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/ExtractionRepository.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/ProdutoRepository.kt` |
| `app/src/main/java/br/com/banavita/app/data/repository/VendaRepository.kt` |
| `app/src/main/java/br/com/banavita/app/di/DatabaseModule.kt` |
| `app/src/main/java/br/com/banavita/app/domain/model/ConsultaModels.kt` |
| `app/src/main/java/br/com/banavita/app/domain/model/DomainModels.kt` |
| `app/src/main/java/br/com/banavita/app/domain/model/extraction/ExtractionModels.kt` |
| `app/src/main/java/br/com/banavita/app/ui/app/BanavitaApp.kt` |
| `app/src/main/java/br/com/banavita/app/ui/app/BanavitaAppViewModel.kt` |
| `app/src/main/java/br/com/banavita/app/ui/cadastros/clientes/ClienteFormScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/cadastros/clientes/ClienteFormViewModel.kt` |
| `app/src/main/java/br/com/banavita/app/ui/cadastros/produtos/ProdutoFormScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/cadastros/produtos/ProdutoFormUiState.kt` |
| `app/src/main/java/br/com/banavita/app/ui/cadastros/produtos/ProdutoFormViewModel.kt` |
| `app/src/main/java/br/com/banavita/app/ui/common/DocumentFormComponents.kt` |
| `app/src/main/java/br/com/banavita/app/ui/common/DocumentFormModels.kt` |
| `app/src/main/java/br/com/banavita/app/ui/consultas/ConsultasScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/consultas/ConsultasUiState.kt` |
| `app/src/main/java/br/com/banavita/app/ui/consultas/ConsultasViewModel.kt` |
| `app/src/main/java/br/com/banavita/app/ui/consultas/estoque/EstoqueDetalheScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/consultas/estoque/EstoqueDetalheViewModel.kt` |
| `app/src/main/java/br/com/banavita/app/ui/home/HomeScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/home/HomeUiState.kt` |
| `app/src/main/java/br/com/banavita/app/ui/home/HomeViewModel.kt` |
| `app/src/main/java/br/com/banavita/app/ui/navigation/BanavitaRoutes.kt` |
| `app/src/main/java/br/com/banavita/app/ui/navigation/MainNavHost.kt` |
| `app/src/main/java/br/com/banavita/app/ui/settings/SettingsScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/settings/SettingsUiState.kt` |
| `app/src/main/java/br/com/banavita/app/ui/settings/SettingsViewModel.kt` |
| `app/src/main/java/br/com/banavita/app/ui/shell/MainAppShell.kt` |
| `app/src/main/java/br/com/banavita/app/ui/vendas/VendaFormScreen.kt` |
| `app/src/main/java/br/com/banavita/app/ui/vendas/VendaFormViewModel.kt` |
| `app/src/main/java/br/com/banavita/app/ui/voice/ExtractionReviewSheet.kt` |
| `app/src/main/res/values/strings.xml` |
| `app/src/test/java/br/com/banavita/app/BanavitaDatabaseIntegrationTest.kt` |
| `app/src/test/java/br/com/banavita/app/data/backup/DatabaseBackupRepositoryTest.kt` |
| `app/src/test/java/br/com/banavita/app/data/repository/ExtractionRepositoryTest.kt` |
| `app/src/test/java/br/com/banavita/app/data/repository/FinanceiroRepositoryTest.kt` |
| `docs/database/README.md` |
| `docs/ui/README.md` |
| `docs/ui/mockups/cliente-form-novo.png` |
| `docs/ui/mockups/consultas-estoque.png` |
| `docs/ui/mockups/estoque-detalhe.png` |
| `docs/ui/mockups/home.png` |
| `docs/ui/mockups/produto-form-editar.png` |
| `docs/ui/mockups/produto-form-novo.png` |
| `docs/ui/mockups/settings.png` |
| `docs/ui/mockups/venda-form-editar.png` |
| `docs/ui/mockups/venda-form-novo.png` |
| `docs/ui/mockups/voice-review-sheet.png` |

## Removido

Nenhum arquivo de produto. Schema Room **3.json** permanece (migração 3→4).

## Escolhas técnicas (desvios explícitos)

- **FIFO:** mesmo produto+estado; lotes na **etileno primeiro**, depois `LOT_DATA` / `created_at` mais antigos (fria entra se ainda houver saldo daquele estado).
- **R$ da perda:** `round(qty × LOT_CUSTO_UN)` (custo unitário da compra). Documentado na UI do relatório.
- **Folga da etileno:** capacidade **0** e kg/dia **0** = sem teto (`folgaEtileno()` retorna `null`).
- **Queima:** unique `(produto, estado, início do dia local)`; diálogo com valor em R$, sem % automático.
- **Transferência:** pai reduz restante; filho `LOT_ORIGEM` = pai; relógio começa **hoje como verde na etileno**.
- **Não perecível na migração:** `perecivel=false`; saldo continua compras − vendas até existirem lotes. Perda só perecível.
- **Voz de perda/encomenda:** não entrou neste corte (formulários manuais). Venda extrai estado; se o cliente resolve e o estado não foi falado, usa o ponto preferido na revisão.

## O que conferir na IDE

1. **Sync Gradle** (schema Room 4: após o compile, conferir `app/schemas/.../4.json`).
2. `RipeningClockTest`, `PereciveisIntegrationTest`, `BanavitaDatabaseIntegrationTest` (produto comum continua `perecivel=false`).
3. Cadastro **Banana-nanica** perecível: curva e preços por estado; um produto caixa permanece sem essa UI.
4. Compra 800 kg → detalhe do estoque: lote verde na fria; soltar 200 kg → 600 fria + 200 etileno com “vira em X dias”.
5. Venda: chips de estado; sem estado não grava; FIFO não baixa outro estado.
6. Perda 10 kg: some do lote, não entra em vendas de hoje; Consultas · Perdas mostra kg/%/R$.
7. Configurações: capacidade/kg dia + horário 07:00; Início: cartão Perecíveis + queima do dia; notificação canal `banavita_pereciveis`.
8. Consultas · Previsão: fria não aparece até soltar. Encomenda: atender gera venda se houver saldo.
9. Restore de backup reagenda aviso matinal além da agenda.
