package br.com.banavita.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import br.com.banavita.app.data.local.converter.UuidConverters
import br.com.banavita.app.data.local.dao.ClienteDao
import br.com.banavita.app.data.local.dao.CompraDao
import br.com.banavita.app.data.local.dao.EstoqueDao
import br.com.banavita.app.data.local.dao.EventoDao
import br.com.banavita.app.data.local.dao.FornecedorDao
import br.com.banavita.app.data.local.dao.GrupoDao
import br.com.banavita.app.data.local.dao.PagamentoDao
import br.com.banavita.app.data.local.dao.ProdutoDao
import br.com.banavita.app.data.local.dao.RecebimentoDao
import br.com.banavita.app.data.local.dao.VendaDao
import br.com.banavita.app.data.local.entity.ClienteEntity
import br.com.banavita.app.data.local.entity.CompraEntity
import br.com.banavita.app.data.local.entity.CompraItemEntity
import br.com.banavita.app.data.local.entity.EventoEntity
import br.com.banavita.app.data.local.entity.FornecedorEntity
import br.com.banavita.app.data.local.entity.GrupoEntity
import br.com.banavita.app.data.local.entity.PagamentoEntity
import br.com.banavita.app.data.local.entity.ProdutoEntity
import br.com.banavita.app.data.local.entity.RecebimentoEntity
import br.com.banavita.app.data.local.entity.VendaEntity
import br.com.banavita.app.data.local.entity.VendaItemEntity
import br.com.banavita.app.data.local.view.ProdutoEstoqueView

@Database(
    entities = [
        GrupoEntity::class,
        ProdutoEntity::class,
        ClienteEntity::class,
        FornecedorEntity::class,
        VendaEntity::class,
        VendaItemEntity::class,
        CompraEntity::class,
        CompraItemEntity::class,
        RecebimentoEntity::class,
        PagamentoEntity::class,
        EventoEntity::class,
    ],
    views = [ProdutoEstoqueView::class],
    version = 3,
    exportSchema = true,
)
@TypeConverters(UuidConverters::class)
abstract class BanavitaDatabase : RoomDatabase() {
    abstract fun grupoDao(): GrupoDao
    abstract fun produtoDao(): ProdutoDao
    abstract fun clienteDao(): ClienteDao
    abstract fun fornecedorDao(): FornecedorDao
    abstract fun vendaDao(): VendaDao
    abstract fun compraDao(): CompraDao
    abstract fun estoqueDao(): EstoqueDao
    abstract fun recebimentoDao(): RecebimentoDao
    abstract fun pagamentoDao(): PagamentoDao
    abstract fun eventoDao(): EventoDao

    companion object {
        const val DATABASE_NAME = "banavita.db"
        const val SCHEMA_VERSION = 3
    }
}
