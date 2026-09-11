package br.com.banavita.app.core.extraction;

import br.com.banavita.app.data.local.dao.ClienteDao;
import br.com.banavita.app.data.local.dao.FornecedorDao;
import br.com.banavita.app.data.local.dao.GrupoDao;
import br.com.banavita.app.data.local.dao.ProdutoDao;
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
public final class EntityResolver_Factory implements Factory<EntityResolver> {
  private final Provider<ProdutoDao> produtoDaoProvider;

  private final Provider<ClienteDao> clienteDaoProvider;

  private final Provider<FornecedorDao> fornecedorDaoProvider;

  private final Provider<GrupoDao> grupoDaoProvider;

  public EntityResolver_Factory(Provider<ProdutoDao> produtoDaoProvider,
      Provider<ClienteDao> clienteDaoProvider, Provider<FornecedorDao> fornecedorDaoProvider,
      Provider<GrupoDao> grupoDaoProvider) {
    this.produtoDaoProvider = produtoDaoProvider;
    this.clienteDaoProvider = clienteDaoProvider;
    this.fornecedorDaoProvider = fornecedorDaoProvider;
    this.grupoDaoProvider = grupoDaoProvider;
  }

  @Override
  public EntityResolver get() {
    return newInstance(produtoDaoProvider, clienteDaoProvider, fornecedorDaoProvider, grupoDaoProvider);
  }

  public static EntityResolver_Factory create(Provider<ProdutoDao> produtoDaoProvider,
      Provider<ClienteDao> clienteDaoProvider, Provider<FornecedorDao> fornecedorDaoProvider,
      Provider<GrupoDao> grupoDaoProvider) {
    return new EntityResolver_Factory(produtoDaoProvider, clienteDaoProvider, fornecedorDaoProvider, grupoDaoProvider);
  }

  public static EntityResolver newInstance(Provider<ProdutoDao> produtoDao,
      Provider<ClienteDao> clienteDao, Provider<FornecedorDao> fornecedorDao,
      Provider<GrupoDao> grupoDao) {
    return new EntityResolver(produtoDao, clienteDao, fornecedorDao, grupoDao);
  }
}
