const fs = require('fs');
const root = 'app/src/main/java/br/com/banavita/app/';
function edit(path, transform) { const p = root + path; const s = fs.readFileSync(p, 'utf8').replace(/\r\n/g, '\n'); fs.writeFileSync(p, transform(s)); }
function replace(path, from, to) { edit(path, s => { if (!s.includes(from)) throw Error(path + ': missing ' + from); return s.replaceAll(from, to); }); }
for (const [name, prefix] of [['Venda','VEN'], ['Compra','COM']]) {
  edit(`data/local/entity/${name}Entity.kt`, s => s.replace(`\n    @ColumnInfo(name = "${prefix}_VENCIMENTO")\n    val vencimento: Long? = null,`, ''));
}
replace('data/local/entity/EventoEntity.kt', '    val notifCorpo: String? = null,', `    val notifCorpo: String? = null,\n    @ColumnInfo(name = "EVE_TIPO", defaultValue = "'LEMBRETE'")\n    val tipo: String = "LEMBRETE",\n    @ColumnInfo(name = "EVE_STATUS", defaultValue = "'PENDENTE'")\n    val status: String = "PENDENTE",\n    @ColumnInfo(name = "EVE_PRIORIDADE", defaultValue = "'MEDIA'")\n    val prioridade: String = "MEDIA",`);
edit('domain/model/DomainModels.kt', s => s.replace('    val notifCorpo: String?,', '    val notifCorpo: String?,\n    val tipo: EventoTipo = EventoTipo.LEMBRETE,\n    val status: EventoStatus = EventoStatus.PENDENTE,\n    val prioridade: Prioridade = Prioridade.MEDIA,'));
edit('data/mapper/EntityMappers.kt', s => s.replace('        notifCorpo = notifCorpo,', '        notifCorpo = notifCorpo,\n        tipo = br.com.banavita.app.domain.model.EventoTipo.valueOf(tipo),\n        status = br.com.banavita.app.domain.model.EventoStatus.valueOf(status),\n        prioridade = br.com.banavita.app.domain.model.Prioridade.valueOf(prioridade),'));
replace('data/local/BanavitaDatabase.kt', 'version = 8', 'version = 9');
replace('data/local/BanavitaDatabase.kt', 'SCHEMA_VERSION = 8', 'SCHEMA_VERSION = 9');
replace('data/local/BanavitaDatabaseHolder.kt', 'MIGRATION_7_8)', 'MIGRATION_7_8, MIGRATION_8_9)');
replace('core/time/RipeningClock.kt', 'camara == Camara.FRIA || etapaInicioEpochDay == null', 'estadoRegistrado == MaturacaoEstado.CRUA || etapaInicioEpochDay == null');
replace('data/local/dao/LoteDao.kt', "WHERE LOT_CAMARA = 'ETILENO' AND LOT_QTDE_RESTANTE > 0.0000001\n        \"\"\",\n    )\n    suspend fun listEtilenoAbertos()", "WHERE LOT_ESTADO != 'CRUA' AND LOT_QTDE_RESTANTE > 0.0000001\n        \"\"\",\n    )\n    suspend fun listEtilenoAbertos()");
edit('data/repository/LoteRepository.kt', s => s.replace('if (novaCamara == Camara.ETILENO) today else null', 'if (estado != MaturacaoEstado.CRUA) today else null').replace('if (camaraFinal == Camara.ETILENO) today else null', 'if (estado != MaturacaoEstado.CRUA) today else null'));
// Update callers to include every pending step.
for (const p of ['ui/home/HomeViewModel.kt','ui/consultas/ConsultasViewModel.kt','ui/pereciveis/encomenda/EncomendaFormScreen.kt','ui/pereciveis/encomenda/EncomendaFormViewModel.kt']) {
 edit(p, s => s.replaceAll('EncomendaStatus.ATENDIDO', 'EncomendaStatus.ENTREGUE').replaceAll('it.status == EncomendaStatus.ABERTO', 'it.status.pendente').replaceAll('uiState.status == EncomendaStatus.ABERTO', 'uiState.status.pendente').replaceAll('state.status != EncomendaStatus.ABERTO', '!state.status.pendente'));
}
replace('data/local/dao/PerecivelDaos.kt', "SUM(ENI_QTDE)", 'SUM(ENI_QTDE - ENI_ENTREGUE)');
replace('data/local/dao/PerecivelDaos.kt', "ENC_STATUS = 'ABERTO'", "ENC_STATUS != 'ENTREGUE'");
