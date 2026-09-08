package br.com.banavita.app.di

import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.data.local.BanavitaDatabaseHolder
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    /**
     * Resolve sempre a instância atual do [BanavitaDatabaseHolder].
     * Repositórios Singleton devem injetar `Provider<BanavitaDatabase>` (ou DAOs via Provider)
     * para não ficarem com referência após um restore.
     */
    @Provides
    fun provideBanavitaDatabase(
        holder: BanavitaDatabaseHolder,
    ): BanavitaDatabase = holder.get()

    @Provides
    fun provideGrupoDao(database: BanavitaDatabase): GrupoDao = database.grupoDao()

    @Provides
    fun provideProdutoDao(database: BanavitaDatabase): ProdutoDao = database.produtoDao()

    @Provides
    fun provideClienteDao(database: BanavitaDatabase): ClienteDao = database.clienteDao()

    @Provides
    fun provideFornecedorDao(database: BanavitaDatabase): FornecedorDao = database.fornecedorDao()

    @Provides
    fun provideVendaDao(database: BanavitaDatabase): VendaDao = database.vendaDao()

    @Provides
    fun provideCompraDao(database: BanavitaDatabase): CompraDao = database.compraDao()

    @Provides
    fun provideEstoqueDao(database: BanavitaDatabase): EstoqueDao = database.estoqueDao()

    @Provides
    fun provideRecebimentoDao(database: BanavitaDatabase): RecebimentoDao = database.recebimentoDao()

    @Provides
    fun providePagamentoDao(database: BanavitaDatabase): PagamentoDao = database.pagamentoDao()

    @Provides
    fun provideEventoDao(database: BanavitaDatabase): EventoDao = database.eventoDao()
}
