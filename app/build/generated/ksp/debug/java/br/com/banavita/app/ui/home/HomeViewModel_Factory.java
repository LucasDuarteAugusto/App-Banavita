package br.com.banavita.app.ui.home;

import br.com.banavita.app.data.repository.CompraRepository;
import br.com.banavita.app.data.repository.EstoqueRepository;
import br.com.banavita.app.data.repository.EventoRepository;
import br.com.banavita.app.data.repository.FinanceiroRepository;
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
public final class HomeViewModel_Factory implements Factory<HomeViewModel> {
  private final Provider<EstoqueRepository> estoqueRepositoryProvider;

  private final Provider<VendaRepository> vendaRepositoryProvider;

  private final Provider<CompraRepository> compraRepositoryProvider;

  private final Provider<FinanceiroRepository> financeiroRepositoryProvider;

  private final Provider<EventoRepository> eventoRepositoryProvider;

  public HomeViewModel_Factory(Provider<EstoqueRepository> estoqueRepositoryProvider,
      Provider<VendaRepository> vendaRepositoryProvider,
      Provider<CompraRepository> compraRepositoryProvider,
      Provider<FinanceiroRepository> financeiroRepositoryProvider,
      Provider<EventoRepository> eventoRepositoryProvider) {
    this.estoqueRepositoryProvider = estoqueRepositoryProvider;
    this.vendaRepositoryProvider = vendaRepositoryProvider;
    this.compraRepositoryProvider = compraRepositoryProvider;
    this.financeiroRepositoryProvider = financeiroRepositoryProvider;
    this.eventoRepositoryProvider = eventoRepositoryProvider;
  }

  @Override
  public HomeViewModel get() {
    return newInstance(estoqueRepositoryProvider.get(), vendaRepositoryProvider.get(), compraRepositoryProvider.get(), financeiroRepositoryProvider.get(), eventoRepositoryProvider.get());
  }

  public static HomeViewModel_Factory create(Provider<EstoqueRepository> estoqueRepositoryProvider,
      Provider<VendaRepository> vendaRepositoryProvider,
      Provider<CompraRepository> compraRepositoryProvider,
      Provider<FinanceiroRepository> financeiroRepositoryProvider,
      Provider<EventoRepository> eventoRepositoryProvider) {
    return new HomeViewModel_Factory(estoqueRepositoryProvider, vendaRepositoryProvider, compraRepositoryProvider, financeiroRepositoryProvider, eventoRepositoryProvider);
  }

  public static HomeViewModel newInstance(EstoqueRepository estoqueRepository,
      VendaRepository vendaRepository, CompraRepository compraRepository,
      FinanceiroRepository financeiroRepository, EventoRepository eventoRepository) {
    return new HomeViewModel(estoqueRepository, vendaRepository, compraRepository, financeiroRepository, eventoRepository);
  }
}
