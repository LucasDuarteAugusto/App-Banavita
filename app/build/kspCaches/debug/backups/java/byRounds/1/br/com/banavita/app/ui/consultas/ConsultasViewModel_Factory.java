package br.com.banavita.app.ui.consultas;

import androidx.lifecycle.SavedStateHandle;
import br.com.banavita.app.data.local.dao.PagamentoDao;
import br.com.banavita.app.data.local.dao.RecebimentoDao;
import br.com.banavita.app.data.repository.ClienteRepository;
import br.com.banavita.app.data.repository.CompraRepository;
import br.com.banavita.app.data.repository.EncomendaRepository;
import br.com.banavita.app.data.repository.EstoqueRepository;
import br.com.banavita.app.data.repository.FinanceiroRepository;
import br.com.banavita.app.data.repository.FornecedorRepository;
import br.com.banavita.app.data.repository.LoteRepository;
import br.com.banavita.app.data.repository.ProdutoRepository;
import br.com.banavita.app.data.repository.VendaRepository;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class ConsultasViewModel_Factory implements Factory<ConsultasViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<EstoqueRepository> estoqueRepositoryProvider;

  private final Provider<ProdutoRepository> produtoRepositoryProvider;

  private final Provider<VendaRepository> vendaRepositoryProvider;

  private final Provider<CompraRepository> compraRepositoryProvider;

  private final Provider<ClienteRepository> clienteRepositoryProvider;

  private final Provider<FornecedorRepository> fornecedorRepositoryProvider;

  private final Provider<FinanceiroRepository> financeiroRepositoryProvider;

  private final Provider<LoteRepository> loteRepositoryProvider;

  private final Provider<EncomendaRepository> encomendaRepositoryProvider;

  private final Provider<RecebimentoDao> recebimentoDaoProvider;

  private final Provider<PagamentoDao> pagamentoDaoProvider;

  public ConsultasViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<EstoqueRepository> estoqueRepositoryProvider,
      Provider<ProdutoRepository> produtoRepositoryProvider,
      Provider<VendaRepository> vendaRepositoryProvider,
      Provider<CompraRepository> compraRepositoryProvider,
      Provider<ClienteRepository> clienteRepositoryProvider,
      Provider<FornecedorRepository> fornecedorRepositoryProvider,
      Provider<FinanceiroRepository> financeiroRepositoryProvider,
      Provider<LoteRepository> loteRepositoryProvider,
      Provider<EncomendaRepository> encomendaRepositoryProvider,
      Provider<RecebimentoDao> recebimentoDaoProvider,
      Provider<PagamentoDao> pagamentoDaoProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.estoqueRepositoryProvider = estoqueRepositoryProvider;
    this.produtoRepositoryProvider = produtoRepositoryProvider;
    this.vendaRepositoryProvider = vendaRepositoryProvider;
    this.compraRepositoryProvider = compraRepositoryProvider;
    this.clienteRepositoryProvider = clienteRepositoryProvider;
    this.fornecedorRepositoryProvider = fornecedorRepositoryProvider;
    this.financeiroRepositoryProvider = financeiroRepositoryProvider;
    this.loteRepositoryProvider = loteRepositoryProvider;
    this.encomendaRepositoryProvider = encomendaRepositoryProvider;
    this.recebimentoDaoProvider = recebimentoDaoProvider;
    this.pagamentoDaoProvider = pagamentoDaoProvider;
  }

  @Override
  public ConsultasViewModel get() {
    return newInstance(savedStateHandleProvider.get(), estoqueRepositoryProvider.get(), produtoRepositoryProvider.get(), vendaRepositoryProvider.get(), compraRepositoryProvider.get(), clienteRepositoryProvider.get(), fornecedorRepositoryProvider.get(), financeiroRepositoryProvider.get(), loteRepositoryProvider.get(), encomendaRepositoryProvider.get(), recebimentoDaoProvider.get(), pagamentoDaoProvider.get());
  }

  public static ConsultasViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<EstoqueRepository> estoqueRepositoryProvider,
      Provider<ProdutoRepository> produtoRepositoryProvider,
      Provider<VendaRepository> vendaRepositoryProvider,
      Provider<CompraRepository> compraRepositoryProvider,
      Provider<ClienteRepository> clienteRepositoryProvider,
      Provider<FornecedorRepository> fornecedorRepositoryProvider,
      Provider<FinanceiroRepository> financeiroRepositoryProvider,
      Provider<LoteRepository> loteRepositoryProvider,
      Provider<EncomendaRepository> encomendaRepositoryProvider,
      Provider<RecebimentoDao> recebimentoDaoProvider,
      Provider<PagamentoDao> pagamentoDaoProvider) {
    return new ConsultasViewModel_Factory(savedStateHandleProvider, estoqueRepositoryProvider, produtoRepositoryProvider, vendaRepositoryProvider, compraRepositoryProvider, clienteRepositoryProvider, fornecedorRepositoryProvider, financeiroRepositoryProvider, loteRepositoryProvider, encomendaRepositoryProvider, recebimentoDaoProvider, pagamentoDaoProvider);
  }

  public static ConsultasViewModel newInstance(SavedStateHandle savedStateHandle,
      EstoqueRepository estoqueRepository, ProdutoRepository produtoRepository,
      VendaRepository vendaRepository, CompraRepository compraRepository,
      ClienteRepository clienteRepository, FornecedorRepository fornecedorRepository,
      FinanceiroRepository financeiroRepository, LoteRepository loteRepository,
      EncomendaRepository encomendaRepository, RecebimentoDao recebimentoDao,
      PagamentoDao pagamentoDao) {
    return new ConsultasViewModel(savedStateHandle, estoqueRepository, produtoRepository, vendaRepository, compraRepository, clienteRepository, fornecedorRepository, financeiroRepository, loteRepository, encomendaRepository, recebimentoDao, pagamentoDao);
  }
}
