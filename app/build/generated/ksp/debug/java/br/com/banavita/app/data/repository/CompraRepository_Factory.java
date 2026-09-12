package br.com.banavita.app.data.repository;

import br.com.banavita.app.data.local.BanavitaDatabase;
import br.com.banavita.app.data.local.dao.CompraDao;
import br.com.banavita.app.data.local.dao.FornecedorDao;
import br.com.banavita.app.data.local.dao.PagamentoDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation"
})
public final class CompraRepository_Factory implements Factory<CompraRepository> {
  private final Provider<BanavitaDatabase> databaseProvider;

  private final Provider<CompraDao> compraDaoProvider;

  private final Provider<FornecedorDao> fornecedorDaoProvider;

  private final Provider<DocumentoItemFactory> documentoItemFactoryProvider;

  private final Provider<PagamentoDao> pagamentoDaoProvider;

  private final Provider<LoteRepository> loteRepositoryProvider;

  public CompraRepository_Factory(Provider<BanavitaDatabase> databaseProvider,
      Provider<CompraDao> compraDaoProvider, Provider<FornecedorDao> fornecedorDaoProvider,
      Provider<DocumentoItemFactory> documentoItemFactoryProvider,
      Provider<PagamentoDao> pagamentoDaoProvider,
      Provider<LoteRepository> loteRepositoryProvider) {
    this.databaseProvider = databaseProvider;
    this.compraDaoProvider = compraDaoProvider;
    this.fornecedorDaoProvider = fornecedorDaoProvider;
    this.documentoItemFactoryProvider = documentoItemFactoryProvider;
    this.pagamentoDaoProvider = pagamentoDaoProvider;
    this.loteRepositoryProvider = loteRepositoryProvider;
  }

  @Override
  public CompraRepository get() {
    return newInstance(databaseProvider, compraDaoProvider, fornecedorDaoProvider, documentoItemFactoryProvider.get(), pagamentoDaoProvider, loteRepositoryProvider.get());
  }

  public static CompraRepository_Factory create(Provider<BanavitaDatabase> databaseProvider,
      Provider<CompraDao> compraDaoProvider, Provider<FornecedorDao> fornecedorDaoProvider,
      Provider<DocumentoItemFactory> documentoItemFactoryProvider,
      Provider<PagamentoDao> pagamentoDaoProvider,
      Provider<LoteRepository> loteRepositoryProvider) {
    return new CompraRepository_Factory(databaseProvider, compraDaoProvider, fornecedorDaoProvider, documentoItemFactoryProvider, pagamentoDaoProvider, loteRepositoryProvider);
  }

  public static CompraRepository newInstance(Provider<BanavitaDatabase> database,
      Provider<CompraDao> compraDao, Provider<FornecedorDao> fornecedorDao,
      DocumentoItemFactory documentoItemFactory, Provider<PagamentoDao> pagamentoDao,
      LoteRepository loteRepository) {
    return new CompraRepository(database, compraDao, fornecedorDao, documentoItemFactory, pagamentoDao, loteRepository);
  }
}
