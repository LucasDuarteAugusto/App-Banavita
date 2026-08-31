package br.com.banavita.app.di

import android.content.Context
import androidx.room.Room
import br.com.banavita.app.data.local.BanavitaDatabase
import br.com.banavita.app.data.local.dao.ClienteDao
import br.com.banavita.app.data.local.dao.CompraDao
import br.com.banavita.app.data.local.dao.EstoqueDao
import br.com.banavita.app.data.local.dao.FornecedorDao
import br.com.banavita.app.data.local.dao.GrupoDao
import br.com.banavita.app.data.local.dao.ProdutoDao
import br.com.banavita.app.data.local.dao.VendaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideBanavitaDatabase(
        @ApplicationContext context: Context,
    ): BanavitaDatabase =
        Room.databaseBuilder(
            context,
            BanavitaDatabase::class.java,
            BanavitaDatabase.DATABASE_NAME,
        )
            .fallbackToDestructiveMigration()
            .build()

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
}
