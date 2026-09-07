package br.com.banavita.app;

import android.app.Activity;
import android.app.Service;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import br.com.banavita.app.core.extraction.EntityResolver;
import br.com.banavita.app.core.extraction.ExtractionPipeline;
import br.com.banavita.app.core.reminder.AgendaAlarmReceiver;
import br.com.banavita.app.core.reminder.AgendaAlarmReceiver_MembersInjector;
import br.com.banavita.app.core.reminder.AndroidReminderScheduler;
import br.com.banavita.app.core.reminder.BootCompletedReceiver;
import br.com.banavita.app.core.reminder.BootCompletedReceiver_MembersInjector;
import br.com.banavita.app.core.security.AndroidDeviceFingerprintProvider;
import br.com.banavita.app.core.security.AndroidDeviceKeystoreManager;
import br.com.banavita.app.core.security.DefaultLicensePublicKeyProvider;
import br.com.banavita.app.core.security.LicenseCodec;
import br.com.banavita.app.core.security.LicenseValidator;
import br.com.banavita.app.core.speech.AudioCapture;
import br.com.banavita.app.core.speech.SpeechModelManager;
import br.com.banavita.app.core.speech.WhisperCppEngine;
import br.com.banavita.app.data.cache.ExtractionSessionCache;
import br.com.banavita.app.data.local.BanavitaDatabase;
import br.com.banavita.app.data.local.activation.ActivationLocalDataSource;
import br.com.banavita.app.data.local.dao.ClienteDao;
import br.com.banavita.app.data.local.dao.CompraDao;
import br.com.banavita.app.data.local.dao.EstoqueDao;
import br.com.banavita.app.data.local.dao.EventoDao;
import br.com.banavita.app.data.local.dao.FornecedorDao;
import br.com.banavita.app.data.local.dao.GrupoDao;
import br.com.banavita.app.data.local.dao.PagamentoDao;
import br.com.banavita.app.data.local.dao.ProdutoDao;
import br.com.banavita.app.data.local.dao.RecebimentoDao;
import br.com.banavita.app.data.local.dao.VendaDao;
import br.com.banavita.app.data.repository.ActivationRepository;
import br.com.banavita.app.data.repository.ClienteRepository;
import br.com.banavita.app.data.repository.CompraRepository;
import br.com.banavita.app.data.repository.DocumentoItemFactory;
import br.com.banavita.app.data.repository.EstoqueRepository;
import br.com.banavita.app.data.repository.EstoqueValidator;
import br.com.banavita.app.data.repository.EventoRepository;
import br.com.banavita.app.data.repository.ExtractionRepository;
import br.com.banavita.app.data.repository.FinanceiroRepository;
import br.com.banavita.app.data.repository.FornecedorRepository;
import br.com.banavita.app.data.repository.GrupoRepository;
import br.com.banavita.app.data.repository.ProdutoRepository;
import br.com.banavita.app.data.repository.SpeechToTextRepository;
import br.com.banavita.app.data.repository.VendaRepository;
import br.com.banavita.app.di.DatabaseModule_ProvideBanavitaDatabaseFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideClienteDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideCompraDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideEstoqueDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideEventoDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideFornecedorDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideGrupoDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvidePagamentoDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideProdutoDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideRecebimentoDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideVendaDaoFactory;
import br.com.banavita.app.ui.activation.ActivationViewModel;
import br.com.banavita.app.ui.activation.ActivationViewModel_HiltModules;
import br.com.banavita.app.ui.agenda.AgendaListViewModel;
import br.com.banavita.app.ui.agenda.AgendaListViewModel_HiltModules;
import br.com.banavita.app.ui.agenda.EventoFormViewModel;
import br.com.banavita.app.ui.agenda.EventoFormViewModel_HiltModules;
import br.com.banavita.app.ui.app.BanavitaAppViewModel;
import br.com.banavita.app.ui.app.BanavitaAppViewModel_HiltModules;
import br.com.banavita.app.ui.cadastros.clientes.ClienteFormViewModel;
import br.com.banavita.app.ui.cadastros.clientes.ClienteFormViewModel_HiltModules;
import br.com.banavita.app.ui.cadastros.clientes.ClienteListViewModel;
import br.com.banavita.app.ui.cadastros.clientes.ClienteListViewModel_HiltModules;
import br.com.banavita.app.ui.cadastros.fornecedores.FornecedorFormViewModel;
import br.com.banavita.app.ui.cadastros.fornecedores.FornecedorFormViewModel_HiltModules;
import br.com.banavita.app.ui.cadastros.fornecedores.FornecedorListViewModel;
import br.com.banavita.app.ui.cadastros.fornecedores.FornecedorListViewModel_HiltModules;
import br.com.banavita.app.ui.cadastros.grupos.GrupoFormViewModel;
import br.com.banavita.app.ui.cadastros.grupos.GrupoFormViewModel_HiltModules;
import br.com.banavita.app.ui.cadastros.grupos.GrupoListViewModel;
import br.com.banavita.app.ui.cadastros.grupos.GrupoListViewModel_HiltModules;
import br.com.banavita.app.ui.cadastros.produtos.ProdutoFormViewModel;
import br.com.banavita.app.ui.cadastros.produtos.ProdutoFormViewModel_HiltModules;
import br.com.banavita.app.ui.cadastros.produtos.ProdutoListViewModel;
import br.com.banavita.app.ui.cadastros.produtos.ProdutoListViewModel_HiltModules;
import br.com.banavita.app.ui.compras.CompraFormViewModel;
import br.com.banavita.app.ui.compras.CompraFormViewModel_HiltModules;
import br.com.banavita.app.ui.compras.CompraListViewModel;
import br.com.banavita.app.ui.compras.CompraListViewModel_HiltModules;
import br.com.banavita.app.ui.consultas.ConsultasViewModel;
import br.com.banavita.app.ui.consultas.ConsultasViewModel_HiltModules;
import br.com.banavita.app.ui.consultas.estoque.EstoqueDetalheViewModel;
import br.com.banavita.app.ui.consultas.estoque.EstoqueDetalheViewModel_HiltModules;
import br.com.banavita.app.ui.consultas.financeiro.FinanceiroDetalheViewModel;
import br.com.banavita.app.ui.consultas.financeiro.FinanceiroDetalheViewModel_HiltModules;
import br.com.banavita.app.ui.home.HomeViewModel;
import br.com.banavita.app.ui.home.HomeViewModel_HiltModules;
import br.com.banavita.app.ui.vendas.VendaFormViewModel;
import br.com.banavita.app.ui.vendas.VendaFormViewModel_HiltModules;
import br.com.banavita.app.ui.vendas.VendaListViewModel;
import br.com.banavita.app.ui.vendas.VendaListViewModel_HiltModules;
import br.com.banavita.app.ui.voice.VoiceCaptureViewModel;
import br.com.banavita.app.ui.voice.VoiceCaptureViewModel_HiltModules;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.IdentifierNameString;
import dagger.internal.KeepFieldType;
import dagger.internal.LazyClassKeyMap;
import dagger.internal.MapBuilder;
import dagger.internal.Preconditions;
import dagger.internal.Provider;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

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
public final class DaggerBanavitaApplication_HiltComponents_SingletonC {
  private DaggerBanavitaApplication_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public BanavitaApplication_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements BanavitaApplication_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public BanavitaApplication_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements BanavitaApplication_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public BanavitaApplication_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements BanavitaApplication_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public BanavitaApplication_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements BanavitaApplication_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public BanavitaApplication_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements BanavitaApplication_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public BanavitaApplication_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements BanavitaApplication_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public BanavitaApplication_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements BanavitaApplication_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public BanavitaApplication_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends BanavitaApplication_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends BanavitaApplication_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends BanavitaApplication_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends BanavitaApplication_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(getViewModelKeys(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(21).put(LazyClassKeyProvider.br_com_banavita_app_ui_activation_ActivationViewModel, ActivationViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_agenda_AgendaListViewModel, AgendaListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_app_BanavitaAppViewModel, BanavitaAppViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_clientes_ClienteFormViewModel, ClienteFormViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_clientes_ClienteListViewModel, ClienteListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_compras_CompraFormViewModel, CompraFormViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_compras_CompraListViewModel, CompraListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_consultas_ConsultasViewModel, ConsultasViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_consultas_estoque_EstoqueDetalheViewModel, EstoqueDetalheViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_agenda_EventoFormViewModel, EventoFormViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_consultas_financeiro_FinanceiroDetalheViewModel, FinanceiroDetalheViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_fornecedores_FornecedorFormViewModel, FornecedorFormViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_fornecedores_FornecedorListViewModel, FornecedorListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_grupos_GrupoFormViewModel, GrupoFormViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_grupos_GrupoListViewModel, GrupoListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_produtos_ProdutoFormViewModel, ProdutoFormViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_produtos_ProdutoListViewModel, ProdutoListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_vendas_VendaFormViewModel, VendaFormViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_vendas_VendaListViewModel, VendaListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_voice_VoiceCaptureViewModel, VoiceCaptureViewModel_HiltModules.KeyModule.provide()).build());
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String br_com_banavita_app_ui_cadastros_clientes_ClienteListViewModel = "br.com.banavita.app.ui.cadastros.clientes.ClienteListViewModel";

      static String br_com_banavita_app_ui_agenda_EventoFormViewModel = "br.com.banavita.app.ui.agenda.EventoFormViewModel";

      static String br_com_banavita_app_ui_vendas_VendaListViewModel = "br.com.banavita.app.ui.vendas.VendaListViewModel";

      static String br_com_banavita_app_ui_compras_CompraFormViewModel = "br.com.banavita.app.ui.compras.CompraFormViewModel";

      static String br_com_banavita_app_ui_cadastros_clientes_ClienteFormViewModel = "br.com.banavita.app.ui.cadastros.clientes.ClienteFormViewModel";

      static String br_com_banavita_app_ui_vendas_VendaFormViewModel = "br.com.banavita.app.ui.vendas.VendaFormViewModel";

      static String br_com_banavita_app_ui_voice_VoiceCaptureViewModel = "br.com.banavita.app.ui.voice.VoiceCaptureViewModel";

      static String br_com_banavita_app_ui_consultas_estoque_EstoqueDetalheViewModel = "br.com.banavita.app.ui.consultas.estoque.EstoqueDetalheViewModel";

      static String br_com_banavita_app_ui_app_BanavitaAppViewModel = "br.com.banavita.app.ui.app.BanavitaAppViewModel";

      static String br_com_banavita_app_ui_cadastros_produtos_ProdutoFormViewModel = "br.com.banavita.app.ui.cadastros.produtos.ProdutoFormViewModel";

      static String br_com_banavita_app_ui_consultas_financeiro_FinanceiroDetalheViewModel = "br.com.banavita.app.ui.consultas.financeiro.FinanceiroDetalheViewModel";

      static String br_com_banavita_app_ui_cadastros_produtos_ProdutoListViewModel = "br.com.banavita.app.ui.cadastros.produtos.ProdutoListViewModel";

      static String br_com_banavita_app_ui_cadastros_fornecedores_FornecedorFormViewModel = "br.com.banavita.app.ui.cadastros.fornecedores.FornecedorFormViewModel";

      static String br_com_banavita_app_ui_agenda_AgendaListViewModel = "br.com.banavita.app.ui.agenda.AgendaListViewModel";

      static String br_com_banavita_app_ui_cadastros_grupos_GrupoListViewModel = "br.com.banavita.app.ui.cadastros.grupos.GrupoListViewModel";

      static String br_com_banavita_app_ui_compras_CompraListViewModel = "br.com.banavita.app.ui.compras.CompraListViewModel";

      static String br_com_banavita_app_ui_consultas_ConsultasViewModel = "br.com.banavita.app.ui.consultas.ConsultasViewModel";

      static String br_com_banavita_app_ui_cadastros_grupos_GrupoFormViewModel = "br.com.banavita.app.ui.cadastros.grupos.GrupoFormViewModel";

      static String br_com_banavita_app_ui_cadastros_fornecedores_FornecedorListViewModel = "br.com.banavita.app.ui.cadastros.fornecedores.FornecedorListViewModel";

      static String br_com_banavita_app_ui_home_HomeViewModel = "br.com.banavita.app.ui.home.HomeViewModel";

      static String br_com_banavita_app_ui_activation_ActivationViewModel = "br.com.banavita.app.ui.activation.ActivationViewModel";

      @KeepFieldType
      ClienteListViewModel br_com_banavita_app_ui_cadastros_clientes_ClienteListViewModel2;

      @KeepFieldType
      EventoFormViewModel br_com_banavita_app_ui_agenda_EventoFormViewModel2;

      @KeepFieldType
      VendaListViewModel br_com_banavita_app_ui_vendas_VendaListViewModel2;

      @KeepFieldType
      CompraFormViewModel br_com_banavita_app_ui_compras_CompraFormViewModel2;

      @KeepFieldType
      ClienteFormViewModel br_com_banavita_app_ui_cadastros_clientes_ClienteFormViewModel2;

      @KeepFieldType
      VendaFormViewModel br_com_banavita_app_ui_vendas_VendaFormViewModel2;

      @KeepFieldType
      VoiceCaptureViewModel br_com_banavita_app_ui_voice_VoiceCaptureViewModel2;

      @KeepFieldType
      EstoqueDetalheViewModel br_com_banavita_app_ui_consultas_estoque_EstoqueDetalheViewModel2;

      @KeepFieldType
      BanavitaAppViewModel br_com_banavita_app_ui_app_BanavitaAppViewModel2;

      @KeepFieldType
      ProdutoFormViewModel br_com_banavita_app_ui_cadastros_produtos_ProdutoFormViewModel2;

      @KeepFieldType
      FinanceiroDetalheViewModel br_com_banavita_app_ui_consultas_financeiro_FinanceiroDetalheViewModel2;

      @KeepFieldType
      ProdutoListViewModel br_com_banavita_app_ui_cadastros_produtos_ProdutoListViewModel2;

      @KeepFieldType
      FornecedorFormViewModel br_com_banavita_app_ui_cadastros_fornecedores_FornecedorFormViewModel2;

      @KeepFieldType
      AgendaListViewModel br_com_banavita_app_ui_agenda_AgendaListViewModel2;

      @KeepFieldType
      GrupoListViewModel br_com_banavita_app_ui_cadastros_grupos_GrupoListViewModel2;

      @KeepFieldType
      CompraListViewModel br_com_banavita_app_ui_compras_CompraListViewModel2;

      @KeepFieldType
      ConsultasViewModel br_com_banavita_app_ui_consultas_ConsultasViewModel2;

      @KeepFieldType
      GrupoFormViewModel br_com_banavita_app_ui_cadastros_grupos_GrupoFormViewModel2;

      @KeepFieldType
      FornecedorListViewModel br_com_banavita_app_ui_cadastros_fornecedores_FornecedorListViewModel2;

      @KeepFieldType
      HomeViewModel br_com_banavita_app_ui_home_HomeViewModel2;

      @KeepFieldType
      ActivationViewModel br_com_banavita_app_ui_activation_ActivationViewModel2;
    }
  }

  private static final class ViewModelCImpl extends BanavitaApplication_HiltComponents.ViewModelC {
    private final SavedStateHandle savedStateHandle;

    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private Provider<ActivationViewModel> activationViewModelProvider;

    private Provider<AgendaListViewModel> agendaListViewModelProvider;

    private Provider<BanavitaAppViewModel> banavitaAppViewModelProvider;

    private Provider<ClienteFormViewModel> clienteFormViewModelProvider;

    private Provider<ClienteListViewModel> clienteListViewModelProvider;

    private Provider<CompraFormViewModel> compraFormViewModelProvider;

    private Provider<CompraListViewModel> compraListViewModelProvider;

    private Provider<ConsultasViewModel> consultasViewModelProvider;

    private Provider<EstoqueDetalheViewModel> estoqueDetalheViewModelProvider;

    private Provider<EventoFormViewModel> eventoFormViewModelProvider;

    private Provider<FinanceiroDetalheViewModel> financeiroDetalheViewModelProvider;

    private Provider<FornecedorFormViewModel> fornecedorFormViewModelProvider;

    private Provider<FornecedorListViewModel> fornecedorListViewModelProvider;

    private Provider<GrupoFormViewModel> grupoFormViewModelProvider;

    private Provider<GrupoListViewModel> grupoListViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<ProdutoFormViewModel> produtoFormViewModelProvider;

    private Provider<ProdutoListViewModel> produtoListViewModelProvider;

    private Provider<VendaFormViewModel> vendaFormViewModelProvider;

    private Provider<VendaListViewModel> vendaListViewModelProvider;

    private Provider<VoiceCaptureViewModel> voiceCaptureViewModelProvider;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.savedStateHandle = savedStateHandleParam;
      initialize(savedStateHandleParam, viewModelLifecycleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandle savedStateHandleParam,
        final ViewModelLifecycle viewModelLifecycleParam) {
      this.activationViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 0);
      this.agendaListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 1);
      this.banavitaAppViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 2);
      this.clienteFormViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 3);
      this.clienteListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 4);
      this.compraFormViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 5);
      this.compraListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.consultasViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.estoqueDetalheViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.eventoFormViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.financeiroDetalheViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
      this.fornecedorFormViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 11);
      this.fornecedorListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 12);
      this.grupoFormViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 13);
      this.grupoListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 14);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 15);
      this.produtoFormViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 16);
      this.produtoListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 17);
      this.vendaFormViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 18);
      this.vendaListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 19);
      this.voiceCaptureViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 20);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(21).put(LazyClassKeyProvider.br_com_banavita_app_ui_activation_ActivationViewModel, ((Provider) activationViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_agenda_AgendaListViewModel, ((Provider) agendaListViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_app_BanavitaAppViewModel, ((Provider) banavitaAppViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_clientes_ClienteFormViewModel, ((Provider) clienteFormViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_clientes_ClienteListViewModel, ((Provider) clienteListViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_compras_CompraFormViewModel, ((Provider) compraFormViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_compras_CompraListViewModel, ((Provider) compraListViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_consultas_ConsultasViewModel, ((Provider) consultasViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_consultas_estoque_EstoqueDetalheViewModel, ((Provider) estoqueDetalheViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_agenda_EventoFormViewModel, ((Provider) eventoFormViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_consultas_financeiro_FinanceiroDetalheViewModel, ((Provider) financeiroDetalheViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_fornecedores_FornecedorFormViewModel, ((Provider) fornecedorFormViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_fornecedores_FornecedorListViewModel, ((Provider) fornecedorListViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_grupos_GrupoFormViewModel, ((Provider) grupoFormViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_grupos_GrupoListViewModel, ((Provider) grupoListViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_produtos_ProdutoFormViewModel, ((Provider) produtoFormViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_produtos_ProdutoListViewModel, ((Provider) produtoListViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_vendas_VendaFormViewModel, ((Provider) vendaFormViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_vendas_VendaListViewModel, ((Provider) vendaListViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_voice_VoiceCaptureViewModel, ((Provider) voiceCaptureViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String br_com_banavita_app_ui_app_BanavitaAppViewModel = "br.com.banavita.app.ui.app.BanavitaAppViewModel";

      static String br_com_banavita_app_ui_cadastros_clientes_ClienteListViewModel = "br.com.banavita.app.ui.cadastros.clientes.ClienteListViewModel";

      static String br_com_banavita_app_ui_cadastros_fornecedores_FornecedorFormViewModel = "br.com.banavita.app.ui.cadastros.fornecedores.FornecedorFormViewModel";

      static String br_com_banavita_app_ui_cadastros_grupos_GrupoFormViewModel = "br.com.banavita.app.ui.cadastros.grupos.GrupoFormViewModel";

      static String br_com_banavita_app_ui_agenda_EventoFormViewModel = "br.com.banavita.app.ui.agenda.EventoFormViewModel";

      static String br_com_banavita_app_ui_compras_CompraListViewModel = "br.com.banavita.app.ui.compras.CompraListViewModel";

      static String br_com_banavita_app_ui_consultas_estoque_EstoqueDetalheViewModel = "br.com.banavita.app.ui.consultas.estoque.EstoqueDetalheViewModel";

      static String br_com_banavita_app_ui_cadastros_produtos_ProdutoListViewModel = "br.com.banavita.app.ui.cadastros.produtos.ProdutoListViewModel";

      static String br_com_banavita_app_ui_vendas_VendaFormViewModel = "br.com.banavita.app.ui.vendas.VendaFormViewModel";

      static String br_com_banavita_app_ui_home_HomeViewModel = "br.com.banavita.app.ui.home.HomeViewModel";

      static String br_com_banavita_app_ui_vendas_VendaListViewModel = "br.com.banavita.app.ui.vendas.VendaListViewModel";

      static String br_com_banavita_app_ui_agenda_AgendaListViewModel = "br.com.banavita.app.ui.agenda.AgendaListViewModel";

      static String br_com_banavita_app_ui_consultas_ConsultasViewModel = "br.com.banavita.app.ui.consultas.ConsultasViewModel";

      static String br_com_banavita_app_ui_cadastros_produtos_ProdutoFormViewModel = "br.com.banavita.app.ui.cadastros.produtos.ProdutoFormViewModel";

      static String br_com_banavita_app_ui_cadastros_grupos_GrupoListViewModel = "br.com.banavita.app.ui.cadastros.grupos.GrupoListViewModel";

      static String br_com_banavita_app_ui_compras_CompraFormViewModel = "br.com.banavita.app.ui.compras.CompraFormViewModel";

      static String br_com_banavita_app_ui_cadastros_fornecedores_FornecedorListViewModel = "br.com.banavita.app.ui.cadastros.fornecedores.FornecedorListViewModel";

      static String br_com_banavita_app_ui_consultas_financeiro_FinanceiroDetalheViewModel = "br.com.banavita.app.ui.consultas.financeiro.FinanceiroDetalheViewModel";

      static String br_com_banavita_app_ui_activation_ActivationViewModel = "br.com.banavita.app.ui.activation.ActivationViewModel";

      static String br_com_banavita_app_ui_voice_VoiceCaptureViewModel = "br.com.banavita.app.ui.voice.VoiceCaptureViewModel";

      static String br_com_banavita_app_ui_cadastros_clientes_ClienteFormViewModel = "br.com.banavita.app.ui.cadastros.clientes.ClienteFormViewModel";

      @KeepFieldType
      BanavitaAppViewModel br_com_banavita_app_ui_app_BanavitaAppViewModel2;

      @KeepFieldType
      ClienteListViewModel br_com_banavita_app_ui_cadastros_clientes_ClienteListViewModel2;

      @KeepFieldType
      FornecedorFormViewModel br_com_banavita_app_ui_cadastros_fornecedores_FornecedorFormViewModel2;

      @KeepFieldType
      GrupoFormViewModel br_com_banavita_app_ui_cadastros_grupos_GrupoFormViewModel2;

      @KeepFieldType
      EventoFormViewModel br_com_banavita_app_ui_agenda_EventoFormViewModel2;

      @KeepFieldType
      CompraListViewModel br_com_banavita_app_ui_compras_CompraListViewModel2;

      @KeepFieldType
      EstoqueDetalheViewModel br_com_banavita_app_ui_consultas_estoque_EstoqueDetalheViewModel2;

      @KeepFieldType
      ProdutoListViewModel br_com_banavita_app_ui_cadastros_produtos_ProdutoListViewModel2;

      @KeepFieldType
      VendaFormViewModel br_com_banavita_app_ui_vendas_VendaFormViewModel2;

      @KeepFieldType
      HomeViewModel br_com_banavita_app_ui_home_HomeViewModel2;

      @KeepFieldType
      VendaListViewModel br_com_banavita_app_ui_vendas_VendaListViewModel2;

      @KeepFieldType
      AgendaListViewModel br_com_banavita_app_ui_agenda_AgendaListViewModel2;

      @KeepFieldType
      ConsultasViewModel br_com_banavita_app_ui_consultas_ConsultasViewModel2;

      @KeepFieldType
      ProdutoFormViewModel br_com_banavita_app_ui_cadastros_produtos_ProdutoFormViewModel2;

      @KeepFieldType
      GrupoListViewModel br_com_banavita_app_ui_cadastros_grupos_GrupoListViewModel2;

      @KeepFieldType
      CompraFormViewModel br_com_banavita_app_ui_compras_CompraFormViewModel2;

      @KeepFieldType
      FornecedorListViewModel br_com_banavita_app_ui_cadastros_fornecedores_FornecedorListViewModel2;

      @KeepFieldType
      FinanceiroDetalheViewModel br_com_banavita_app_ui_consultas_financeiro_FinanceiroDetalheViewModel2;

      @KeepFieldType
      ActivationViewModel br_com_banavita_app_ui_activation_ActivationViewModel2;

      @KeepFieldType
      VoiceCaptureViewModel br_com_banavita_app_ui_voice_VoiceCaptureViewModel2;

      @KeepFieldType
      ClienteFormViewModel br_com_banavita_app_ui_cadastros_clientes_ClienteFormViewModel2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final ViewModelCImpl viewModelCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          ViewModelCImpl viewModelCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.viewModelCImpl = viewModelCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // br.com.banavita.app.ui.activation.ActivationViewModel 
          return (T) new ActivationViewModel(singletonCImpl.activationRepositoryProvider.get());

          case 1: // br.com.banavita.app.ui.agenda.AgendaListViewModel 
          return (T) new AgendaListViewModel(singletonCImpl.eventoRepositoryProvider.get());

          case 2: // br.com.banavita.app.ui.app.BanavitaAppViewModel 
          return (T) new BanavitaAppViewModel(singletonCImpl.activationRepositoryProvider.get());

          case 3: // br.com.banavita.app.ui.cadastros.clientes.ClienteFormViewModel 
          return (T) new ClienteFormViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.clienteRepositoryProvider.get());

          case 4: // br.com.banavita.app.ui.cadastros.clientes.ClienteListViewModel 
          return (T) new ClienteListViewModel(singletonCImpl.clienteRepositoryProvider.get());

          case 5: // br.com.banavita.app.ui.compras.CompraFormViewModel 
          return (T) new CompraFormViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.compraRepositoryProvider.get(), singletonCImpl.fornecedorRepositoryProvider.get(), singletonCImpl.produtoRepositoryProvider.get());

          case 6: // br.com.banavita.app.ui.compras.CompraListViewModel 
          return (T) new CompraListViewModel(singletonCImpl.compraRepositoryProvider.get(), singletonCImpl.fornecedorRepositoryProvider.get());

          case 7: // br.com.banavita.app.ui.consultas.ConsultasViewModel 
          return (T) new ConsultasViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.estoqueRepositoryProvider.get(), singletonCImpl.produtoRepositoryProvider.get(), singletonCImpl.vendaRepositoryProvider.get(), singletonCImpl.compraRepositoryProvider.get(), singletonCImpl.clienteRepositoryProvider.get(), singletonCImpl.fornecedorRepositoryProvider.get(), singletonCImpl.financeiroRepositoryProvider.get());

          case 8: // br.com.banavita.app.ui.consultas.estoque.EstoqueDetalheViewModel 
          return (T) new EstoqueDetalheViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.produtoRepositoryProvider.get(), singletonCImpl.estoqueRepositoryProvider.get());

          case 9: // br.com.banavita.app.ui.agenda.EventoFormViewModel 
          return (T) new EventoFormViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.eventoRepositoryProvider.get(), singletonCImpl.clienteRepositoryProvider.get(), singletonCImpl.fornecedorRepositoryProvider.get());

          case 10: // br.com.banavita.app.ui.consultas.financeiro.FinanceiroDetalheViewModel 
          return (T) new FinanceiroDetalheViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.financeiroRepositoryProvider.get(), singletonCImpl.clienteRepositoryProvider.get(), singletonCImpl.fornecedorRepositoryProvider.get());

          case 11: // br.com.banavita.app.ui.cadastros.fornecedores.FornecedorFormViewModel 
          return (T) new FornecedorFormViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.fornecedorRepositoryProvider.get());

          case 12: // br.com.banavita.app.ui.cadastros.fornecedores.FornecedorListViewModel 
          return (T) new FornecedorListViewModel(singletonCImpl.fornecedorRepositoryProvider.get());

          case 13: // br.com.banavita.app.ui.cadastros.grupos.GrupoFormViewModel 
          return (T) new GrupoFormViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.grupoRepositoryProvider.get());

          case 14: // br.com.banavita.app.ui.cadastros.grupos.GrupoListViewModel 
          return (T) new GrupoListViewModel(singletonCImpl.grupoRepositoryProvider.get());

          case 15: // br.com.banavita.app.ui.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.estoqueRepositoryProvider.get(), singletonCImpl.vendaRepositoryProvider.get(), singletonCImpl.compraRepositoryProvider.get(), singletonCImpl.financeiroRepositoryProvider.get(), singletonCImpl.eventoRepositoryProvider.get());

          case 16: // br.com.banavita.app.ui.cadastros.produtos.ProdutoFormViewModel 
          return (T) new ProdutoFormViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.produtoRepositoryProvider.get(), singletonCImpl.grupoRepositoryProvider.get());

          case 17: // br.com.banavita.app.ui.cadastros.produtos.ProdutoListViewModel 
          return (T) new ProdutoListViewModel(singletonCImpl.produtoRepositoryProvider.get(), singletonCImpl.grupoRepositoryProvider.get());

          case 18: // br.com.banavita.app.ui.vendas.VendaFormViewModel 
          return (T) new VendaFormViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.vendaRepositoryProvider.get(), singletonCImpl.clienteRepositoryProvider.get(), singletonCImpl.produtoRepositoryProvider.get(), singletonCImpl.estoqueRepositoryProvider.get());

          case 19: // br.com.banavita.app.ui.vendas.VendaListViewModel 
          return (T) new VendaListViewModel(singletonCImpl.vendaRepositoryProvider.get(), singletonCImpl.clienteRepositoryProvider.get());

          case 20: // br.com.banavita.app.ui.voice.VoiceCaptureViewModel 
          return (T) new VoiceCaptureViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.speechToTextRepositoryProvider.get(), singletonCImpl.extractionRepositoryProvider.get(), singletonCImpl.audioCaptureProvider.get());

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ActivityRetainedCImpl extends BanavitaApplication_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends BanavitaApplication_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }
  }

  private static final class SingletonCImpl extends BanavitaApplication_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private Provider<BanavitaDatabase> provideBanavitaDatabaseProvider;

    private Provider<AndroidReminderScheduler> androidReminderSchedulerProvider;

    private Provider<EventoRepository> eventoRepositoryProvider;

    private Provider<AndroidDeviceKeystoreManager> androidDeviceKeystoreManagerProvider;

    private Provider<AndroidDeviceFingerprintProvider> androidDeviceFingerprintProvider;

    private Provider<LicenseCodec> licenseCodecProvider;

    private Provider<DefaultLicensePublicKeyProvider> defaultLicensePublicKeyProvider;

    private Provider<LicenseValidator> licenseValidatorProvider;

    private Provider<ActivationLocalDataSource> activationLocalDataSourceProvider;

    private Provider<ActivationRepository> activationRepositoryProvider;

    private Provider<ClienteRepository> clienteRepositoryProvider;

    private Provider<DocumentoItemFactory> documentoItemFactoryProvider;

    private Provider<CompraRepository> compraRepositoryProvider;

    private Provider<FornecedorRepository> fornecedorRepositoryProvider;

    private Provider<ProdutoRepository> produtoRepositoryProvider;

    private Provider<EstoqueRepository> estoqueRepositoryProvider;

    private Provider<EstoqueValidator> estoqueValidatorProvider;

    private Provider<VendaRepository> vendaRepositoryProvider;

    private Provider<FinanceiroRepository> financeiroRepositoryProvider;

    private Provider<GrupoRepository> grupoRepositoryProvider;

    private Provider<SpeechModelManager> speechModelManagerProvider;

    private Provider<WhisperCppEngine> whisperCppEngineProvider;

    private Provider<SpeechToTextRepository> speechToTextRepositoryProvider;

    private Provider<EntityResolver> entityResolverProvider;

    private Provider<ExtractionPipeline> extractionPipelineProvider;

    private Provider<ExtractionSessionCache> extractionSessionCacheProvider;

    private Provider<ExtractionRepository> extractionRepositoryProvider;

    private Provider<AudioCapture> audioCaptureProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);
      initialize2(applicationContextModuleParam);

    }

    private EventoDao eventoDao() {
      return DatabaseModule_ProvideEventoDaoFactory.provideEventoDao(provideBanavitaDatabaseProvider.get());
    }

    private ClienteDao clienteDao() {
      return DatabaseModule_ProvideClienteDaoFactory.provideClienteDao(provideBanavitaDatabaseProvider.get());
    }

    private FornecedorDao fornecedorDao() {
      return DatabaseModule_ProvideFornecedorDaoFactory.provideFornecedorDao(provideBanavitaDatabaseProvider.get());
    }

    private CompraDao compraDao() {
      return DatabaseModule_ProvideCompraDaoFactory.provideCompraDao(provideBanavitaDatabaseProvider.get());
    }

    private ProdutoDao produtoDao() {
      return DatabaseModule_ProvideProdutoDaoFactory.provideProdutoDao(provideBanavitaDatabaseProvider.get());
    }

    private PagamentoDao pagamentoDao() {
      return DatabaseModule_ProvidePagamentoDaoFactory.providePagamentoDao(provideBanavitaDatabaseProvider.get());
    }

    private GrupoDao grupoDao() {
      return DatabaseModule_ProvideGrupoDaoFactory.provideGrupoDao(provideBanavitaDatabaseProvider.get());
    }

    private EstoqueDao estoqueDao() {
      return DatabaseModule_ProvideEstoqueDaoFactory.provideEstoqueDao(provideBanavitaDatabaseProvider.get());
    }

    private VendaDao vendaDao() {
      return DatabaseModule_ProvideVendaDaoFactory.provideVendaDao(provideBanavitaDatabaseProvider.get());
    }

    private RecebimentoDao recebimentoDao() {
      return DatabaseModule_ProvideRecebimentoDaoFactory.provideRecebimentoDao(provideBanavitaDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.provideBanavitaDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<BanavitaDatabase>(singletonCImpl, 1));
      this.androidReminderSchedulerProvider = DoubleCheck.provider(new SwitchingProvider<AndroidReminderScheduler>(singletonCImpl, 2));
      this.eventoRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<EventoRepository>(singletonCImpl, 0));
      this.androidDeviceKeystoreManagerProvider = DoubleCheck.provider(new SwitchingProvider<AndroidDeviceKeystoreManager>(singletonCImpl, 5));
      this.androidDeviceFingerprintProvider = DoubleCheck.provider(new SwitchingProvider<AndroidDeviceFingerprintProvider>(singletonCImpl, 4));
      this.licenseCodecProvider = DoubleCheck.provider(new SwitchingProvider<LicenseCodec>(singletonCImpl, 6));
      this.defaultLicensePublicKeyProvider = DoubleCheck.provider(new SwitchingProvider<DefaultLicensePublicKeyProvider>(singletonCImpl, 8));
      this.licenseValidatorProvider = DoubleCheck.provider(new SwitchingProvider<LicenseValidator>(singletonCImpl, 7));
      this.activationLocalDataSourceProvider = DoubleCheck.provider(new SwitchingProvider<ActivationLocalDataSource>(singletonCImpl, 9));
      this.activationRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ActivationRepository>(singletonCImpl, 3));
      this.clienteRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ClienteRepository>(singletonCImpl, 10));
      this.documentoItemFactoryProvider = DoubleCheck.provider(new SwitchingProvider<DocumentoItemFactory>(singletonCImpl, 12));
      this.compraRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<CompraRepository>(singletonCImpl, 11));
      this.fornecedorRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<FornecedorRepository>(singletonCImpl, 13));
      this.produtoRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ProdutoRepository>(singletonCImpl, 14));
      this.estoqueRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<EstoqueRepository>(singletonCImpl, 15));
      this.estoqueValidatorProvider = DoubleCheck.provider(new SwitchingProvider<EstoqueValidator>(singletonCImpl, 17));
      this.vendaRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<VendaRepository>(singletonCImpl, 16));
      this.financeiroRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<FinanceiroRepository>(singletonCImpl, 18));
      this.grupoRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<GrupoRepository>(singletonCImpl, 19));
      this.speechModelManagerProvider = DoubleCheck.provider(new SwitchingProvider<SpeechModelManager>(singletonCImpl, 21));
      this.whisperCppEngineProvider = DoubleCheck.provider(new SwitchingProvider<WhisperCppEngine>(singletonCImpl, 22));
      this.speechToTextRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SpeechToTextRepository>(singletonCImpl, 20));
      this.entityResolverProvider = DoubleCheck.provider(new SwitchingProvider<EntityResolver>(singletonCImpl, 25));
      this.extractionPipelineProvider = DoubleCheck.provider(new SwitchingProvider<ExtractionPipeline>(singletonCImpl, 24));
    }

    @SuppressWarnings("unchecked")
    private void initialize2(final ApplicationContextModule applicationContextModuleParam) {
      this.extractionSessionCacheProvider = DoubleCheck.provider(new SwitchingProvider<ExtractionSessionCache>(singletonCImpl, 26));
      this.extractionRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ExtractionRepository>(singletonCImpl, 23));
      this.audioCaptureProvider = DoubleCheck.provider(new SwitchingProvider<AudioCapture>(singletonCImpl, 27));
    }

    @Override
    public void injectBanavitaApplication(BanavitaApplication banavitaApplication) {
    }

    @Override
    public void injectAgendaAlarmReceiver(AgendaAlarmReceiver agendaAlarmReceiver) {
      injectAgendaAlarmReceiver2(agendaAlarmReceiver);
    }

    @Override
    public void injectBootCompletedReceiver(BootCompletedReceiver bootCompletedReceiver) {
      injectBootCompletedReceiver2(bootCompletedReceiver);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    private AgendaAlarmReceiver injectAgendaAlarmReceiver2(AgendaAlarmReceiver instance) {
      AgendaAlarmReceiver_MembersInjector.injectEventoRepository(instance, eventoRepositoryProvider.get());
      return instance;
    }

    private BootCompletedReceiver injectBootCompletedReceiver2(BootCompletedReceiver instance2) {
      BootCompletedReceiver_MembersInjector.injectEventoRepository(instance2, eventoRepositoryProvider.get());
      BootCompletedReceiver_MembersInjector.injectReminderScheduler(instance2, androidReminderSchedulerProvider.get());
      return instance2;
    }

    private static final class SwitchingProvider<T> implements Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // br.com.banavita.app.data.repository.EventoRepository 
          return (T) new EventoRepository(singletonCImpl.eventoDao(), singletonCImpl.clienteDao(), singletonCImpl.fornecedorDao(), singletonCImpl.androidReminderSchedulerProvider.get());

          case 1: // br.com.banavita.app.data.local.BanavitaDatabase 
          return (T) DatabaseModule_ProvideBanavitaDatabaseFactory.provideBanavitaDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // br.com.banavita.app.core.reminder.AndroidReminderScheduler 
          return (T) new AndroidReminderScheduler(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // br.com.banavita.app.data.repository.ActivationRepository 
          return (T) new ActivationRepository(singletonCImpl.androidDeviceFingerprintProvider.get(), singletonCImpl.licenseCodecProvider.get(), singletonCImpl.licenseValidatorProvider.get(), singletonCImpl.activationLocalDataSourceProvider.get());

          case 4: // br.com.banavita.app.core.security.AndroidDeviceFingerprintProvider 
          return (T) new AndroidDeviceFingerprintProvider(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.androidDeviceKeystoreManagerProvider.get());

          case 5: // br.com.banavita.app.core.security.AndroidDeviceKeystoreManager 
          return (T) new AndroidDeviceKeystoreManager();

          case 6: // br.com.banavita.app.core.security.LicenseCodec 
          return (T) new LicenseCodec();

          case 7: // br.com.banavita.app.core.security.LicenseValidator 
          return (T) new LicenseValidator(singletonCImpl.licenseCodecProvider.get(), singletonCImpl.androidDeviceFingerprintProvider.get(), singletonCImpl.defaultLicensePublicKeyProvider.get());

          case 8: // br.com.banavita.app.core.security.DefaultLicensePublicKeyProvider 
          return (T) new DefaultLicensePublicKeyProvider();

          case 9: // br.com.banavita.app.data.local.activation.ActivationLocalDataSource 
          return (T) new ActivationLocalDataSource(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // br.com.banavita.app.data.repository.ClienteRepository 
          return (T) new ClienteRepository(singletonCImpl.clienteDao());

          case 11: // br.com.banavita.app.data.repository.CompraRepository 
          return (T) new CompraRepository(singletonCImpl.provideBanavitaDatabaseProvider.get(), singletonCImpl.compraDao(), singletonCImpl.fornecedorDao(), singletonCImpl.documentoItemFactoryProvider.get(), singletonCImpl.pagamentoDao());

          case 12: // br.com.banavita.app.data.repository.DocumentoItemFactory 
          return (T) new DocumentoItemFactory(singletonCImpl.produtoDao());

          case 13: // br.com.banavita.app.data.repository.FornecedorRepository 
          return (T) new FornecedorRepository(singletonCImpl.fornecedorDao());

          case 14: // br.com.banavita.app.data.repository.ProdutoRepository 
          return (T) new ProdutoRepository(singletonCImpl.produtoDao(), singletonCImpl.grupoDao());

          case 15: // br.com.banavita.app.data.repository.EstoqueRepository 
          return (T) new EstoqueRepository(singletonCImpl.estoqueDao());

          case 16: // br.com.banavita.app.data.repository.VendaRepository 
          return (T) new VendaRepository(singletonCImpl.provideBanavitaDatabaseProvider.get(), singletonCImpl.vendaDao(), singletonCImpl.clienteDao(), singletonCImpl.documentoItemFactoryProvider.get(), singletonCImpl.estoqueValidatorProvider.get(), singletonCImpl.recebimentoDao());

          case 17: // br.com.banavita.app.data.repository.EstoqueValidator 
          return (T) new EstoqueValidator(singletonCImpl.estoqueDao());

          case 18: // br.com.banavita.app.data.repository.FinanceiroRepository 
          return (T) new FinanceiroRepository(singletonCImpl.vendaDao(), singletonCImpl.compraDao(), singletonCImpl.recebimentoDao(), singletonCImpl.pagamentoDao(), singletonCImpl.clienteDao(), singletonCImpl.fornecedorDao());

          case 19: // br.com.banavita.app.data.repository.GrupoRepository 
          return (T) new GrupoRepository(singletonCImpl.grupoDao());

          case 20: // br.com.banavita.app.data.repository.SpeechToTextRepository 
          return (T) new SpeechToTextRepository(singletonCImpl.speechModelManagerProvider.get(), singletonCImpl.whisperCppEngineProvider.get());

          case 21: // br.com.banavita.app.core.speech.SpeechModelManager 
          return (T) new SpeechModelManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 22: // br.com.banavita.app.core.speech.WhisperCppEngine 
          return (T) new WhisperCppEngine();

          case 23: // br.com.banavita.app.data.repository.ExtractionRepository 
          return (T) new ExtractionRepository(singletonCImpl.extractionPipelineProvider.get(), singletonCImpl.extractionSessionCacheProvider.get(), singletonCImpl.vendaRepositoryProvider.get(), singletonCImpl.compraRepositoryProvider.get(), singletonCImpl.clienteRepositoryProvider.get(), singletonCImpl.fornecedorRepositoryProvider.get(), singletonCImpl.produtoRepositoryProvider.get(), singletonCImpl.grupoRepositoryProvider.get(), singletonCImpl.financeiroRepositoryProvider.get(), singletonCImpl.eventoRepositoryProvider.get());

          case 24: // br.com.banavita.app.core.extraction.ExtractionPipeline 
          return (T) new ExtractionPipeline(singletonCImpl.entityResolverProvider.get());

          case 25: // br.com.banavita.app.core.extraction.EntityResolver 
          return (T) new EntityResolver(singletonCImpl.produtoDao(), singletonCImpl.clienteDao(), singletonCImpl.fornecedorDao(), singletonCImpl.grupoDao());

          case 26: // br.com.banavita.app.data.cache.ExtractionSessionCache 
          return (T) new ExtractionSessionCache();

          case 27: // br.com.banavita.app.core.speech.AudioCapture 
          return (T) new AudioCapture(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
