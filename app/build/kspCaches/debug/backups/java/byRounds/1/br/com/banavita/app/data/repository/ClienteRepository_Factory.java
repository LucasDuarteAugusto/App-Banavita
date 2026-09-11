package br.com.banavita.app.data.repository;

import br.com.banavita.app.data.local.dao.ClienteDao;
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
public final class ClienteRepository_Factory implements Factory<ClienteRepository> {
  private final Provider<ClienteDao> clienteDaoProvider;

  public ClienteRepository_Factory(Provider<ClienteDao> clienteDaoProvider) {
    this.clienteDaoProvider = clienteDaoProvider;
  }

  @Override
  public ClienteRepository get() {
    return newInstance(clienteDaoProvider);
  }

  public static ClienteRepository_Factory create(Provider<ClienteDao> clienteDaoProvider) {
    return new ClienteRepository_Factory(clienteDaoProvider);
  }

  public static ClienteRepository newInstance(Provider<ClienteDao> clienteDao) {
    return new ClienteRepository(clienteDao);
  }
}
