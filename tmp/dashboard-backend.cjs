const fs = require('fs');
const root = 'app/src/main/java/br/com/banavita/app/';
function edit(p, f) { fs.writeFileSync(root+p, f(fs.readFileSync(root+p,'utf8').replace(/\r\n/g,'\n'))); }
for(const [name,prefix] of [['Venda','VEN'],['Compra','COM']]) {
 edit(`data/repository/${name}Repository.kt`,s=>s.replace('aPrazo: Boolean = false,','aPrazo: Boolean = false,\n        vencimento: Long? = null,').replace('        val timestamp = now()', '        if (aPrazo && vencimento == null) throw br.com.banavita.app.domain.exception.InvalidDocumentException("Informe o vencimento.")\n        val timestamp = now()').replace('            aPrazo = aPrazo,','            aPrazo = aPrazo,\n            vencimento = if (aPrazo) vencimento else null,').replace('itens: List<DocumentoItemInput>,\n    ) {','itens: List<DocumentoItemInput>,\n        vencimento: Long? = null,\n    ) {').replace('valorTotalCentavos = total'+name+'Centavos(itemEntities),','valorTotalCentavos = total'+name+'Centavos(itemEntities),\n            vencimento = if (existing.'+name.toLowerCase()+'.aPrazo) vencimento else null,'));
}
edit('data/local/BanavitaDatabase.kt',s=>s.replace('        GrupoEntity::class,','        br.com.banavita.app.data.local.entity.EntregaItemEntity::class,\n        br.com.banavita.app.data.local.entity.EstoqueSnapshotEntity::class,\n        br.com.banavita.app.data.local.entity.CaixaBaseEntity::class,\n        GrupoEntity::class,').replace('    abstract fun grupoDao()', '    abstract fun dashboardDao(): br.com.banavita.app.data.local.dao.DashboardDao\n    abstract fun grupoDao()'));
edit('data/local/BanavitaMigrations.kt',s=>s+`
val MIGRATION_8_9 = object : Migration(8, 9) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE venda ADD COLUMN VEN_VENCIMENTO INTEGER")
        db.execSQL("ALTER TABLE compra ADD COLUMN COM_VENCIMENTO INTEGER")
        db.execSQL("ALTER TABLE evento ADD COLUMN EVE_TIPO TEXT NOT NULL DEFAULT 'LEMBRETE'")
        db.execSQL("ALTER TABLE evento ADD COLUMN EVE_STATUS TEXT NOT NULL DEFAULT 'PENDENTE'")
        db.execSQL("ALTER TABLE evento ADD COLUMN EVE_PRIORIDADE TEXT NOT NULL DEFAULT 'MEDIA'")
        db.execSQL("ALTER TABLE encomenda_item ADD COLUMN ENI_ENTREGUE REAL NOT NULL DEFAULT 0")
        db.execSQL("UPDATE encomenda_item SET ENI_ENTREGUE = ENI_QTDE WHERE ENI_ENCOMENDA IN (SELECT ENC_ID FROM encomenda WHERE ENC_STATUS = 'ATENDIDO')")
        db.execSQL("UPDATE evento SET EVE_TIPO = 'ENTREGA', EVE_STATUS = CASE WHEN EVE_ID IN (SELECT ENC_EVENTO FROM encomenda WHERE ENC_STATUS = 'ATENDIDO') THEN 'CONCLUIDO' ELSE 'PENDENTE' END WHERE EVE_ID IN (SELECT ENC_EVENTO FROM encomenda)")
        db.execSQL("UPDATE encomenda SET ENC_STATUS = 'ENTREGUE' WHERE ENC_STATUS = 'ATENDIDO'")
        db.execSQL("CREATE TABLE entrega_item (requestId TEXT NOT NULL, itemId TEXT NOT NULL, vendaId TEXT NOT NULL, quantidade REAL NOT NULL, data INTEGER NOT NULL, PRIMARY KEY(requestId, itemId), FOREIGN KEY(itemId) REFERENCES encomenda_item(ENI_ID) ON UPDATE NO ACTION ON DELETE RESTRICT, FOREIGN KEY(vendaId) REFERENCES venda(VEN_ID) ON UPDATE NO ACTION ON DELETE RESTRICT)")
        db.execSQL("CREATE INDEX index_entrega_item_itemId ON entrega_item(itemId)")
        db.execSQL("CREATE INDEX index_entrega_item_vendaId ON entrega_item(vendaId)")
        db.execSQL("CREATE TABLE estoque_snapshot (dia INTEGER NOT NULL, produtoId TEXT NOT NULL, unidade TEXT NOT NULL, estado TEXT NOT NULL, camara TEXT NOT NULL, quantidade REAL NOT NULL, PRIMARY KEY(dia, produtoId, unidade, estado, camara))")
        db.execSQL("CREATE TABLE caixa_base (id INTEGER NOT NULL PRIMARY KEY, data INTEGER NOT NULL, saldoCentavos INTEGER NOT NULL)")
        // Restart the new clock at the local migration day; preserve existing states.
        db.execSQL("UPDATE lote SET LOT_ETAPA_INICIO_DAY = CAST(julianday('now', 'localtime', 'start of day') - julianday('1970-01-01') AS INTEGER) WHERE LOT_ESTADO NOT IN ('CRUA', 'PODRE')")
    }
}
`);
edit('data/local/dao/PerecivelDaos.kt',s=>s.replace('    @Query("DELETE FROM encomenda_item', '    @Update\n    suspend fun updateItem(item: EncomendaItemEntity)\n\n    @Query("DELETE FROM encomenda_item'));
edit('data/local/dao/EventoDao.kt', s=>s.replace('WHERE EVE_NOTIFICAR = 1', "WHERE EVE_STATUS != 'CONCLUIDO' AND EVE_NOTIFICAR = 1"));
edit('data/repository/EventoRepository.kt',s=>s.replace('        notifCorpo: String? = null,','        notifCorpo: String? = null,\n        tipo: br.com.banavita.app.domain.model.EventoTipo = br.com.banavita.app.domain.model.EventoTipo.LEMBRETE,\n        status: br.com.banavita.app.domain.model.EventoStatus = br.com.banavita.app.domain.model.EventoStatus.PENDENTE,\n        prioridade: br.com.banavita.app.domain.model.Prioridade = br.com.banavita.app.domain.model.Prioridade.MEDIA,').replace('        notifCorpo: String?,','        notifCorpo: String?,\n        tipo: br.com.banavita.app.domain.model.EventoTipo? = null,\n        status: br.com.banavita.app.domain.model.EventoStatus? = null,\n        prioridade: br.com.banavita.app.domain.model.Prioridade? = null,').replace('                notifCorpo = notifCorpo?.trim()?.takeIf { it.isNotEmpty() },','                notifCorpo = notifCorpo?.trim()?.takeIf { it.isNotEmpty() },\n                tipo = tipo.name,\n                status = status.name,\n                prioridade = prioridade.name,').replace('                tipo = tipo.name,\n                status = status.name,\n                prioridade = prioridade.name,\n            ),\n        )\n        getById(id)?.let { reminderScheduler.schedule(it) }\n    }','                tipo = tipo?.name ?: existing.tipo,\n                status = status?.name ?: existing.status,\n                prioridade = prioridade?.name ?: existing.prioridade,\n            ),\n        )\n        getById(id)?.let { if (it.status == br.com.banavita.app.domain.model.EventoStatus.CONCLUIDO) reminderScheduler.cancel(id) else reminderScheduler.schedule(it) }\n    }'));
