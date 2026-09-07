package br.com.banavita.app.data.repository;

import br.com.banavita.app.data.local.dao.ClienteDao;
import br.com.banavita.app.data.local.dao.CompraDao;
import br.com.banavita.app.data.local.dao.FornecedorDao;
import br.com.banavita.app.data.local.dao.PagamentoDao;
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
public final class FinanceiroRepository_Factory implements Factory<FinanceiroRepository> {
  private final Provider<VendaDao> vendaDaoProvider;

  private final Provider<CompraDao> compraDaoProvider;

  private final Provider<RecebimentoDao> recebimentoDaoProvider;

  private final Provider<PagamentoDao> pagamentoDaoProvider;

  private final Provider<ClienteDao> clienteDaoProvider;

  private final Provider<FornecedorDao> fornecedorDaoProvider;

  public FinanceiroRepository_Factory(Provider<VendaDao> vendaDaoProvider,
      Provider<CompraDao> compraDaoProvider, Provider<RecebimentoDao> recebimentoDaoProvider,
      Provider<PagamentoDao> pagamentoDaoProvider, Provider<ClienteDao> clienteDaoProvider,
      Provider<FornecedorDao> fornecedorDaoProvider) {
    this.vendaDaoProvider = vendaDaoProvider;
    this.compraDaoProvider = compraDaoProvider;
    this.recebimentoDaoProvider = recebimentoDaoProvider;
    this.pagamentoDaoProvider = pagamentoDaoProvider;
    this.clienteDaoProvider = clienteDaoProvider;
    this.fornecedorDaoProvider = fornecedorDaoProvider;
  }

  @Override
  public FinanceiroRepository get() {
    return newInstance(vendaDaoProvider.get(), compraDaoProvider.get(), recebimentoDaoProvider.get(), pagamentoDaoProvider.get(), clienteDaoProvider.get(), fornecedorDaoProvider.get());
  }

  public static FinanceiroRepository_Factory create(Provider<VendaDao> vendaDaoProvider,
      Provider<CompraDao> compraDaoProvider, Provider<RecebimentoDao> recebimentoDaoProvider,
      Provider<PagamentoDao> pagamentoDaoProvider, Provider<ClienteDao> clienteDaoProvider,
      Provider<FornecedorDao> fornecedorDaoProvider) {
    return new FinanceiroRepository_Factory(vendaDaoProvider, compraDaoProvider, recebimentoDaoProvider, pagamentoDaoProvider, clienteDaoProvider, fornecedorDaoProvider);
  }

  public static FinanceiroRepository newInstance(VendaDao vendaDao, CompraDao compraDao,
      RecebimentoDao recebimentoDao, PagamentoDao pagamentoDao, ClienteDao clienteDao,
      FornecedorDao fornecedorDao) {
    return new FinanceiroRepository(vendaDao, compraDao, recebimentoDao, pagamentoDao, clienteDao, fornecedorDao);
  }
}
