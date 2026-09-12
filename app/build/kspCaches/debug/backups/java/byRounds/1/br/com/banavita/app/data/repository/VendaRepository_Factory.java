package br.com.banavita.app.data.repository;

import br.com.banavita.app.data.local.BanavitaDatabase;
import br.com.banavita.app.data.local.dao.ClienteDao;
import br.com.banavita.app.data.local.dao.ProdutoDao;
import br.com.banavita.app.data.local.dao.RecebimentoDao;
import br.com.banavita.app.data.local.dao.VendaDao;
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
public final class VendaRepository_Factory implements Factory<VendaRepository> {
  private final Provider<BanavitaDatabase> databaseProvider;

  private final Provider<VendaDao> vendaDaoProvider;

  private final Provider<ClienteDao> clienteDaoProvider;

  private final Provider<DocumentoItemFactory> documentoItemFactoryProvider;

  private final Provider<EstoqueValidator> estoqueValidatorProvider;

  private final Provider<RecebimentoDao> recebimentoDaoProvider;

  private final Provider<LoteRepository> loteRepositoryProvider;

  private final Provider<ProdutoDao> produtoDaoProvider;

  public VendaRepository_Factory(Provider<BanavitaDatabase> databaseProvider,
      Provider<VendaDao> vendaDaoProvider, Provider<ClienteDao> clienteDaoProvider,
      Provider<DocumentoItemFactory> documentoItemFactoryProvider,
      Provider<EstoqueValidator> estoqueValidatorProvider,
      Provider<RecebimentoDao> recebimentoDaoProvider,
      Provider<LoteRepository> loteRepositoryProvider, Provider<ProdutoDao> produtoDaoProvider) {
    this.databaseProvider = databaseProvider;
    this.vendaDaoProvider = vendaDaoProvider;
    this.clienteDaoProvider = clienteDaoProvider;
    this.documentoItemFactoryProvider = documentoItemFactoryProvider;
    this.estoqueValidatorProvider = estoqueValidatorProvider;
    this.recebimentoDaoProvider = recebimentoDaoProvider;
    this.loteRepositoryProvider = loteRepositoryProvider;
    this.produtoDaoProvider = produtoDaoProvider;
  }

  @Override
  public VendaRepository get() {
    return newInstance(databaseProvider, vendaDaoProvider, clienteDaoProvider, documentoItemFactoryProvider.get(), estoqueValidatorProvider.get(), recebimentoDaoProvider, loteRepositoryProvider.get(), produtoDaoProvider);
  }

  public static VendaRepository_Factory create(Provider<BanavitaDatabase> databaseProvider,
      Provider<VendaDao> vendaDaoProvider, Provider<ClienteDao> clienteDaoProvider,
      Provider<DocumentoItemFactory> documentoItemFactoryProvider,
      Provider<EstoqueValidator> estoqueValidatorProvider,
      Provider<RecebimentoDao> recebimentoDaoProvider,
      Provider<LoteRepository> loteRepositoryProvider, Provider<ProdutoDao> produtoDaoProvider) {
    return new VendaRepository_Factory(databaseProvider, vendaDaoProvider, clienteDaoProvider, documentoItemFactoryProvider, estoqueValidatorProvider, recebimentoDaoProvider, loteRepositoryProvider, produtoDaoProvider);
  }

  public static VendaRepository newInstance(Provider<BanavitaDatabase> database,
      Provider<VendaDao> vendaDao, Provider<ClienteDao> clienteDao,
      DocumentoItemFactory documentoItemFactory, EstoqueValidator estoqueValidator,
      Provider<RecebimentoDao> recebimentoDao, LoteRepository loteRepository,
      Provider<ProdutoDao> produtoDao) {
    return new VendaRepository(database, vendaDao, clienteDao, documentoItemFactory, estoqueValidator, recebimentoDao, loteRepository, produtoDao);
  }
}
