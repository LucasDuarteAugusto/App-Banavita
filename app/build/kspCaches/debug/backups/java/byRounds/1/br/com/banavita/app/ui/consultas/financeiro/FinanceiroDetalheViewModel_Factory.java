package br.com.banavita.app.ui.consultas.financeiro;

import androidx.lifecycle.SavedStateHandle;
import br.com.banavita.app.data.repository.ClienteRepository;
import br.com.banavita.app.data.repository.FinanceiroRepository;
import br.com.banavita.app.data.repository.FornecedorRepository;
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
public final class FinanceiroDetalheViewModel_Factory implements Factory<FinanceiroDetalheViewModel> {
  private final Provider<SavedStateHandle> savedStateHandleProvider;

  private final Provider<FinanceiroRepository> financeiroRepositoryProvider;

  private final Provider<ClienteRepository> clienteRepositoryProvider;

  private final Provider<FornecedorRepository> fornecedorRepositoryProvider;

  public FinanceiroDetalheViewModel_Factory(Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<FinanceiroRepository> financeiroRepositoryProvider,
      Provider<ClienteRepository> clienteRepositoryProvider,
      Provider<FornecedorRepository> fornecedorRepositoryProvider) {
    this.savedStateHandleProvider = savedStateHandleProvider;
    this.financeiroRepositoryProvider = financeiroRepositoryProvider;
    this.clienteRepositoryProvider = clienteRepositoryProvider;
    this.fornecedorRepositoryProvider = fornecedorRepositoryProvider;
  }

  @Override
  public FinanceiroDetalheViewModel get() {
    return newInstance(savedStateHandleProvider.get(), financeiroRepositoryProvider.get(), clienteRepositoryProvider.get(), fornecedorRepositoryProvider.get());
  }

  public static FinanceiroDetalheViewModel_Factory create(
      Provider<SavedStateHandle> savedStateHandleProvider,
      Provider<FinanceiroRepository> financeiroRepositoryProvider,
      Provider<ClienteRepository> clienteRepositoryProvider,
      Provider<FornecedorRepository> fornecedorRepositoryProvider) {
    return new FinanceiroDetalheViewModel_Factory(savedStateHandleProvider, financeiroRepositoryProvider, clienteRepositoryProvider, fornecedorRepositoryProvider);
  }

  public static FinanceiroDetalheViewModel newInstance(SavedStateHandle savedStateHandle,
      FinanceiroRepository financeiroRepository, ClienteRepository clienteRepository,
      FornecedorRepository fornecedorRepository) {
    return new FinanceiroDetalheViewModel(savedStateHandle, financeiroRepository, clienteRepository, fornecedorRepository);
  }
}
