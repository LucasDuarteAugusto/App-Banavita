const fs=require('fs'); const r='app/src/main/java/br/com/banavita/app/';
function edit(p,f){fs.writeFileSync(r+p,f(fs.readFileSync(r+p,'utf8').replace(/\r\n/g,'\n')))}
edit('ui/common/DocumentFormComponents.kt',s=>s.replace('    enabled: Boolean,\n    modifier: Modifier = Modifier,\n    label:', '    enabled: Boolean = true,\n    modifier: Modifier = Modifier,\n    label:'));
edit('ui/navigation/MainNavHost.kt',s=>{
 const start=s.indexOf('    composable(BanavitaRoutes.HOME) {'), end=s.indexOf('    composable(\n        route = BanavitaRoutes.VENDAS', start);
 s=s.slice(0,start)+`    composable(BanavitaRoutes.HOME) {
        br.com.banavita.app.ui.voice.VoiceEnabledScreen(
            voiceContext = VoiceScreenContext.INICIO,
            addContentDescription = "Nova venda", onAddClick = { navController.navigate(BanavitaRoutes.VENDA_FORM_NEW) },
            onVoiceSuccess = { result ->
                if (result is ConfirmResult.ConsultaAplicada) {
                    val route = when {
                        result.navigateNovaVenda -> BanavitaRoutes.VENDA_FORM_NEW
                        result.navigateAgenda -> BanavitaRoutes.agenda()
                        result.openProdutoId != null -> BanavitaRoutes.estoqueDetalhe(result.openProdutoId, result.somenteCriticos)
                        result.openClienteId != null -> BanavitaRoutes.receberDetalhe(result.openClienteId)
                        result.openFornecedorId != null -> BanavitaRoutes.pagarDetalhe(result.openFornecedorId)
                        else -> BanavitaRoutes.consultas(result.modo ?: ConsultaModo.ESTOQUE, result.busca, result.periodo, result.somenteCriticos)
                    }
                    navController.navigate(route)
                }
            },
        ) { dashboardModifier ->
            br.com.banavita.app.ui.dashboard.DashboardRoute(onNavigate = { navController.navigate(it) }, modifier = dashboardModifier)
        }
    }
    composable("dashboards/{kind}", arguments = listOf(navArgument("kind") { type = NavType.StringType })) {
        br.com.banavita.app.ui.dashboard.DashboardRoute(onNavigate = { navController.navigate(it) })
    }
`+s.slice(end);
 return s.replace('import br.com.banavita.app.ui.home.HomeRoute\n','').replace('import br.com.banavita.app.ui.home.HomeNavEvent\n','');
});
edit('ui/navigation/BanavitaRoutes.kt',s=>s.replace('route == SETTINGS || route.startsWith("consultas")', 'route.startsWith("dashboards/") || route == SETTINGS || route.startsWith("consultas")'));
edit('ui/shell/MainAppShell.kt',s=>s.replace('    val baseTitle = titleForRoute(', '    val dashboardTitle = navBackStackEntry?.arguments?.getString("kind")?.let { raw -> br.com.banavita.app.domain.model.DashboardKind.entries.find { it.name == raw }?.title }\n    val baseTitle = dashboardTitle ?: titleForRoute(').replace('        route == BanavitaRoutes.HOME -> R.string.home_title','        route == BanavitaRoutes.HOME -> R.string.dashboard_general_title'));
edit('data/repository/EventoRepository.kt',s=>{
 s=s.replace('                notifCorpo = notifCorpo?.trim()?.takeIf { it.isNotEmpty() },\n            ),','                notifCorpo = notifCorpo?.trim()?.takeIf { it.isNotEmpty() },\n                tipo = tipo?.name ?: existing.tipo,\n                status = status?.name ?: existing.status,\n                prioridade = prioridade?.name ?: existing.prioridade,\n            ),');
 s=s.replace('        getById(id)?.let { reminderScheduler.schedule(it) }','        refreshReminder(id)');
 s=s.replace('        validate(titulo, clienteId, fornecedorId, antecipacaoMinutos)\n        eventoDao.get().update(', '        if (eventoDao.get().isLinked(id) > 0 && (tipo != null && tipo.name != existing.tipo || status != null && status.name != existing.status)) throw InvalidDocumentException("Altere a etapa pela encomenda vinculada.")\n        validate(titulo, clienteId, fornecedorId, antecipacaoMinutos)\n        eventoDao.get().update(');
 s=s.replace('    suspend fun delete(id: UUID) {', '    suspend fun delete(id: UUID) {\n        if (eventoDao.get().isLinked(id) > 0) throw InvalidDocumentException("Exclua pela encomenda vinculada.")');
 return s;
});
edit('data/local/dao/EventoDao.kt',s=>s.replace('    @Delete','    @Query("SELECT COUNT(*) FROM encomenda WHERE ENC_EVENTO = :id")\n    suspend fun isLinked(id: UUID): Int\n\n    @Delete'));
edit('data/repository/EncomendaRepository.kt',s=>s.replace('            existing.encomenda.eventoId?.let { eventoRepository.get().delete(it) }\n            encomendaDao.get().delete(id)', '            encomendaDao.get().delete(id)\n            existing.encomenda.eventoId?.let { eventoRepository.get().delete(it) }').replace('        var event: UUID? = null\n','').replace('            event = current.encomenda.eventoId\n            event?.let', '            current.encomenda.eventoId?.let').replace(/                val balance = available.getOrPut\(key\)[\s\S]*?else balance\n/, '                val stock = available[key] ?: loteRepository.saldoPorEstado(item.produtoId, MaturacaoEstado.valueOf(item.estado))\n'));
edit('core/reminder/AndroidReminderScheduler.kt',s=>s.replace('if (!evento.notificar) return','if (!evento.notificar || evento.status == br.com.banavita.app.domain.model.EventoStatus.CONCLUIDO) return'));
edit('core/reminder/PereciveisAlarmReceiver.kt',s=>s.replace('    @Inject lateinit var loteRepository:', '    @Inject lateinit var dashboardRepository: br.com.banavita.app.data.repository.DashboardRepository\n    @Inject lateinit var loteRepository:').replace('                loteRepository.avancarMaturacao()', '                dashboardRepository.refreshStockSnapshot()'));
edit('data/repository/LoteRepository.kt',s=>s.replace('it.estado == MaturacaoEstado.EM_MATURACAO', 'it.estado == MaturacaoEstado.EM_MATURACAO').replace('var falta = itemEnc.quantidade', 'var falta = itemEnc.quantidadePendente'));
edit('ui/dashboard/DashboardViewModel.kt',s=>s.replace('MutableStateFlow(savedStateHandle.get<DashboardFilter>("dashboardFilter") ?: initial)', 'MutableStateFlow(initial)'));
edit('data/repository/DashboardRepository.kt',s=>s.replace('@Singleton\nclass DashboardRepository', '@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)\n@Singleton\nclass DashboardRepository'));
const resource=r+'../';
let strings=fs.readFileSync('app/src/main/res/values/strings.xml','utf8');
fs.writeFileSync('app/src/main/res/values/strings.xml',strings.replace('</resources>','    <string name="dashboard_general_title">Dashboard Geral</string>\n</resources>'));
