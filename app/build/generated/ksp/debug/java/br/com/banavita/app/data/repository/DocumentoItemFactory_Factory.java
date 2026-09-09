package br.com.banavita.app.data.repository;

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
public final class DocumentoItemFactory_Factory implements Factory<DocumentoItemFactory> {
  private final Provider<ProdutoDao> produtoDaoProvider;

  public DocumentoItemFactory_Factory(Provider<ProdutoDao> produtoDaoProvider) {
    this.produtoDaoProvider = produtoDaoProvider;
  }

  @Override
  public DocumentoItemFactory get() {
    return newInstance(produtoDaoProvider);
  }

  public static DocumentoItemFactory_Factory create(Provider<ProdutoDao> produtoDaoProvider) {
    return new DocumentoItemFactory_Factory(produtoDaoProvider);
  }

  public static DocumentoItemFactory newInstance(Provider<ProdutoDao> produtoDao) {
    return new DocumentoItemFactory(produtoDao);
  }
}
