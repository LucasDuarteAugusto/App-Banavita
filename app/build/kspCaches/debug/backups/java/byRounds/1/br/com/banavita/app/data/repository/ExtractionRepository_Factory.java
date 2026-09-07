package br.com.banavita.app.data.repository;

import br.com.banavita.app.core.extraction.ExtractionPipeline;
import br.com.banavita.app.data.cache.ExtractionSessionCache;
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
public final class ExtractionRepository_Factory implements Factory<ExtractionRepository> {
  private final Provider<ExtractionPipeline> pipelineProvider;

  private final Provider<ExtractionSessionCache> sessionCacheProvider;

  private final Provider<VendaRepository> vendaRepositoryProvider;

  private final Provider<CompraRepository> compraRepositoryProvider;

  private final Provider<ClienteRepository> clienteRepositoryProvider;

  private final Provider<FornecedorRepository> fornecedorRepositoryProvider;

  private final Provider<ProdutoRepository> produtoRepositoryProvider;

  private final Provider<GrupoRepository> grupoRepositoryProvider;

  private final Provider<FinanceiroRepository> financeiroRepositoryProvider;

  private final Provider<EventoRepository> eventoRepositoryProvider;

  public ExtractionRepository_Factory(Provider<ExtractionPipeline> pipelineProvider,
      Provider<ExtractionSessionCache> sessionCacheProvider,
      Provider<VendaRepository> vendaRepositoryProvider,
      Provider<CompraRepository> compraRepositoryProvider,
      Provider<ClienteRepository> clienteRepositoryProvider,
      Provider<FornecedorRepository> fornecedorRepositoryProvider,
      Provider<ProdutoRepository> produtoRepositoryProvider,
      Provider<GrupoRepository> grupoRepositoryProvider,
      Provider<FinanceiroRepository> financeiroRepositoryProvider,
      Provider<EventoRepository> eventoRepositoryProvider) {
    this.pipelineProvider = pipelineProvider;
    this.sessionCacheProvider = sessionCacheProvider;
    this.vendaRepositoryProvider = vendaRepositoryProvider;
    this.compraRepositoryProvider = compraRepositoryProvider;
    this.clienteRepositoryProvider = clienteRepositoryProvider;
    this.fornecedorRepositoryProvider = fornecedorRepositoryProvider;
    this.produtoRepositoryProvider = produtoRepositoryProvider;
    this.grupoRepositoryProvider = grupoRepositoryProvider;
    this.financeiroRepositoryProvider = financeiroRepositoryProvider;
    this.eventoRepositoryProvider = eventoRepositoryProvider;
  }

  @Override
  public ExtractionRepository get() {
    return newInstance(pipelineProvider.get(), sessionCacheProvider.get(), vendaRepositoryProvider.get(), compraRepositoryProvider.get(), clienteRepositoryProvider.get(), fornecedorRepositoryProvider.get(), produtoRepositoryProvider.get(), grupoRepositoryProvider.get(), financeiroRepositoryProvider.get(), eventoRepositoryProvider.get());
  }

  public static ExtractionRepository_Factory create(Provider<ExtractionPipeline> pipelineProvider,
      Provider<ExtractionSessionCache> sessionCacheProvider,
      Provider<VendaRepository> vendaRepositoryProvider,
      Provider<CompraRepository> compraRepositoryProvider,
      Provider<ClienteRepository> clienteRepositoryProvider,
      Provider<FornecedorRepository> fornecedorRepositoryProvider,
      Provider<ProdutoRepository> produtoRepositoryProvider,
      Provider<GrupoRepository> grupoRepositoryProvider,
      Provider<FinanceiroRepository> financeiroRepositoryProvider,
      Provider<EventoRepository> eventoRepositoryProvider) {
    return new ExtractionRepository_Factory(pipelineProvider, sessionCacheProvider, vendaRepositoryProvider, compraRepositoryProvider, clienteRepositoryProvider, fornecedorRepositoryProvider, produtoRepositoryProvider, grupoRepositoryProvider, financeiroRepositoryProvider, eventoRepositoryProvider);
  }

  public static ExtractionRepository newInstance(ExtractionPipeline pipeline,
      ExtractionSessionCache sessionCache, VendaRepository vendaRepository,
      CompraRepository compraRepository, ClienteRepository clienteRepository,
      FornecedorRepository fornecedorRepository, ProdutoRepository produtoRepository,
      GrupoRepository grupoRepository, FinanceiroRepository financeiroRepository,
      EventoRepository eventoRepository) {
    return new ExtractionRepository(pipeline, sessionCache, vendaRepository, compraRepository, clienteRepository, fornecedorRepository, produtoRepository, grupoRepository, financeiroRepository, eventoRepository);
  }
}
