const fs=require('fs'); const r='app/src/main/java/br/com/banavita/app/';
function edit(p,f){fs.writeFileSync(r+p,f(fs.readFileSync(r+p,'utf8').replace(/\r\n/g,'\n')))}
edit('data/repository/EncomendaRepository.kt',s=>{
 s=s.replace('existing.encomenda.status != EncomendaStatus.ABERTO.name', 'existing.encomenda.status == EncomendaStatus.ENTREGUE.name || existing.itens.any { it.quantidadeEntregue > 0 }');
 s=s.replace('notifCorpo = obs', 'tipo = br.com.banavita.app.domain.model.EventoTipo.ENTREGA,\n                notifCorpo = obs');
 const start=s.indexOf('    suspend fun atender('), end=s.indexOf('    suspend fun obterTotalEncomendadoAberto',start);
 s=s.slice(0,start)+`    suspend fun mudarEtapa(id: UUID, status: EncomendaStatus) {
        if (status == EncomendaStatus.ENTREGUE) throw InvalidDocumentException("Confirme a entrega para gerar a venda.")
        var event: UUID? = null
        database.get().withTransaction {
            val current = encomendaDao.get().getWithItems(id) ?: throw ReferencedEntityException("Encomenda não encontrada.")
            if (!EncomendaStatus.valueOf(current.encomenda.status).pendente) throw InvalidDocumentException("Encomenda entregue.")
            if (status.ordinal != EncomendaStatus.valueOf(current.encomenda.status).ordinal + 1) throw InvalidDocumentException("Avance uma etapa por vez.")
            encomendaDao.get().update(current.encomenda.copy(status = status.name, updatedAt = nowEpochMillis()))
            event = current.encomenda.eventoId
            event?.let { database.get().dashboardDao().syncDeliveryEvent(it, if (status == EncomendaStatus.EM_ROTA) "EM_ANDAMENTO" else "PENDENTE", nowEpochMillis()) }
        }
    }

    suspend fun atender(
        id: UUID, aPrazo: Boolean, permitirParcial: Boolean,
        vencimento: Long? = null, requestId: UUID = UUID.randomUUID(),
    ): UUID {
        val result = database.get().withTransaction {
            val history = database.get().dashboardDao().delivery(requestId)
            if (history.isNotEmpty()) return@withTransaction history.first().vendaId
            val existing = encomendaDao.get().getWithItems(id) ?: throw ReferencedEntityException("Encomenda não encontrada.")
            if (existing.encomenda.status == EncomendaStatus.ENTREGUE.name) throw InvalidDocumentException("Esta encomenda já foi entregue.")
            val now = nowEpochMillis()
            val inputs = mutableListOf<DocumentoItemInput>()
            val delivered = mutableMapOf<UUID, Double>()
            val available = mutableMapOf<Pair<UUID, String>, Double>()
            existing.itens.forEach { item ->
                val pending = (item.quantidade - item.quantidadeEntregue).coerceAtLeast(0.0)
                if (pending <= 1e-9) return@forEach
                val key = item.produtoId to item.estado
                val balance = available.getOrPut(key) { 0.0 }.let {
                    if (key !in available || it == 0.0) it else it
                }
                // Fetch each product/state once; account for duplicate items in the same order.
                val stock = if (delivered.keys.none { itemId -> existing.itens.any { it.id == itemId && (it.produtoId to it.estado) == key } })
                    loteRepository.saldoPorEstado(item.produtoId, MaturacaoEstado.valueOf(item.estado)) else balance
                val qty = if (permitirParcial) minOf(pending, stock) else pending
                if (qty > stock + 1e-9) throw InsufficientStockException(item.produtoId.toString(), stock, pending)
                available[key] = (stock - qty).coerceAtLeast(0.0)
                if (qty > 1e-9) {
                    inputs += DocumentoItemInput(item.produtoId, qty, item.valorUnitarioCentavos, MaturacaoEstado.valueOf(item.estado))
                    delivered[item.id] = qty
                }
            }
            if (inputs.isEmpty()) throw InvalidDocumentException("Não há saldo para entregar os itens pendentes.")
            val sale = vendaRepository.create(now, existing.encomenda.clienteId, inputs, aPrazo, vencimento)
            existing.itens.forEach { item -> delivered[item.id]?.let { qty ->
                encomendaDao.get().updateItem(item.copy(quantidadeEntregue = item.quantidadeEntregue + qty, updatedAt = now))
            } }
            val complete = existing.itens.all { it.quantidadeEntregue + (delivered[it.id] ?: 0.0) >= it.quantidade - 1e-9 }
            encomendaDao.get().update(existing.encomenda.copy(status = if (complete) "ENTREGUE" else "ABERTO", vendaId = sale, updatedAt = now))
            database.get().dashboardDao().insertDelivery(delivered.map { (item, qty) -> br.com.banavita.app.data.local.entity.EntregaItemEntity(requestId, item, sale, qty, now) })
            existing.encomenda.eventoId?.let { database.get().dashboardDao().syncDeliveryEvent(it, if (complete) "CONCLUIDO" else "PENDENTE", now) }
            sale
        }
        getById(id)?.eventoId?.let { eventoRepository.get().refreshReminder(it) }
        return result
    }

`+s.slice(end);
 return s.replace('.filter { it.status == EncomendaStatus.ABERTO }', '.filter { it.status.pendente }').replace('quantidade = item.quantidade,\n                valorUnitario', 'quantidade = item.quantidade,\n                quantidadeEntregue = item.quantidadeEntregue,\n                valorUnitario');
});
edit('data/repository/EventoRepository.kt',s=>s.replace('    suspend fun delete(id: UUID)',`    suspend fun refreshReminder(id: UUID) {
        getById(id)?.let { if (it.status == br.com.banavita.app.domain.model.EventoStatus.CONCLUIDO) reminderScheduler.cancel(id) else reminderScheduler.schedule(it) }
    }

    suspend fun delete(id: UUID)`));
// Protect the immutable sale behind a delivery history.
edit('data/repository/VendaRepository.kt', s=>s.replace('        val existing = vendaDao.get().getWithItems(id)', '        if (database.get().dashboardDao().linkedSale(id) > 0) throw br.com.banavita.app.domain.exception.InvalidDocumentException("Venda vinculada a entrega não pode ser alterada.")\n        val existing = vendaDao.get().getWithItems(id)').replace('    suspend fun delete(id: UUID) {','    suspend fun delete(id: UUID) {\n        if (database.get().dashboardDao().linkedSale(id) > 0) throw br.com.banavita.app.domain.exception.InvalidDocumentException("Venda vinculada a entrega não pode ser excluída.")'));
edit('ui/consultas/ConsultasViewModel.kt',s=>s.replace('if (enc.status == EncomendaStatus.ENTREGUE) "Atendido" else "Aberto"','enc.status.label'));
