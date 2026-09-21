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
import br.com.banavita.app.core.reminder.PereciveisAlarmReceiver;
import br.com.banavita.app.core.reminder.PereciveisAlarmReceiver_MembersInjector;
import br.com.banavita.app.core.reminder.PereciveisMorningScheduler;
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
import br.com.banavita.app.data.local.BanavitaDatabaseHolder;
import br.com.banavita.app.data.local.activation.ActivationLocalDataSource;
import br.com.banavita.app.data.local.dao.ClienteDao;
import br.com.banavita.app.data.local.dao.CompraDao;
import br.com.banavita.app.data.local.dao.EncomendaDao;
import br.com.banavita.app.data.local.dao.EstoqueDao;
import br.com.banavita.app.data.local.dao.EventoDao;
import br.com.banavita.app.data.local.dao.FornecedorDao;
import br.com.banavita.app.data.local.dao.GrupoDao;
import br.com.banavita.app.data.local.dao.LoteDao;
import br.com.banavita.app.data.local.dao.PagamentoDao;
import br.com.banavita.app.data.local.dao.PerdaDao;
import br.com.banavita.app.data.local.dao.PrecoQueimaDao;
import br.com.banavita.app.data.local.dao.ProdutoDao;
import br.com.banavita.app.data.local.dao.RecebimentoDao;
import br.com.banavita.app.data.local.dao.VendaDao;
import br.com.banavita.app.data.local.settings.AppSettingsLocalDataSource;
import br.com.banavita.app.data.remote.groq.EnvGroqConfig;
import br.com.banavita.app.data.remote.groq.GroqWhisperClient;
import br.com.banavita.app.data.repository.ActivationRepository;
import br.com.banavita.app.data.repository.AppSettingsRepository;
import br.com.banavita.app.data.repository.ClienteRepository;
import br.com.banavita.app.data.repository.CompraRepository;
import br.com.banavita.app.data.repository.DatabaseBackupRepository;
import br.com.banavita.app.data.repository.DocumentoItemFactory;
import br.com.banavita.app.data.repository.EncomendaRepository;
import br.com.banavita.app.data.repository.EstoqueRepository;
import br.com.banavita.app.data.repository.EstoqueValidator;
import br.com.banavita.app.data.repository.EventoRepository;
import br.com.banavita.app.data.repository.ExtractionRepository;
import br.com.banavita.app.data.repository.FinanceiroRepository;
import br.com.banavita.app.data.repository.FornecedorRepository;
import br.com.banavita.app.data.repository.GrupoRepository;
import br.com.banavita.app.data.repository.LoteRepository;
import br.com.banavita.app.data.repository.ProdutoRepository;
import br.com.banavita.app.data.repository.SpeechToTextRepository;
import br.com.banavita.app.data.repository.VendaRepository;
import br.com.banavita.app.di.DatabaseModule_ProvideBanavitaDatabaseFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideClienteDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideCompraDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideEncomendaDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideEstoqueDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideEventoDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideFornecedorDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideGrupoDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideLoteDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvidePagamentoDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvidePerdaDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvidePrecoQueimaDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideProdutoDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideRecebimentoDaoFactory;
import br.com.banavita.app.di.DatabaseModule_ProvideVendaDaoFactory;
import br.com.banavita.app.di.NetworkModule_ProvideOkHttpClientFactory;
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
import br.com.banavita.app.ui.consultas.ConsultasViewModel;
import br.com.banavita.app.ui.consultas.ConsultasViewModel_HiltModules;
import br.com.banavita.app.ui.consultas.estoque.EstoqueDetalheViewModel;
import br.com.banavita.app.ui.consultas.estoque.EstoqueDetalheViewModel_HiltModules;
import br.com.banavita.app.ui.consultas.financeiro.FinanceiroDetalheViewModel;
import br.com.banavita.app.ui.consultas.financeiro.FinanceiroDetalheViewModel_HiltModules;
import br.com.banavita.app.ui.home.HomeViewModel;
import br.com.banavita.app.ui.home.HomeViewModel_HiltModules;
import br.com.banavita.app.ui.pereciveis.encomenda.EncomendaFormViewModel;
import br.com.banavita.app.ui.pereciveis.encomenda.EncomendaFormViewModel_HiltModules;
import br.com.banavita.app.ui.pereciveis.etileno.EtilenoFilaViewModel;
import br.com.banavita.app.ui.pereciveis.etileno.EtilenoFilaViewModel_HiltModules;
import br.com.banavita.app.ui.pereciveis.perda.PerdaFormViewModel;
import br.com.banavita.app.ui.pereciveis.perda.PerdaFormViewModel_HiltModules;
import br.com.banavita.app.ui.settings.SettingsViewModel;
import br.com.banavita.app.ui.settings.SettingsViewModel_HiltModules;
import br.com.banavita.app.ui.vendas.VendaFormViewModel;
import br.com.banavita.app.ui.vendas.VendaFormViewModel_HiltModules;
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
import okhttp3.OkHttpClient;

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
      return LazyClassKeyMap.<Boolean>of(MapBuilder.<String, Boolean>newMapBuilder(23).put(LazyClassKeyProvider.br_com_banavita_app_ui_activation_ActivationViewModel, ActivationViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_agenda_AgendaListViewModel, AgendaListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_app_BanavitaAppViewModel, BanavitaAppViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_clientes_ClienteFormViewModel, ClienteFormViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_clientes_ClienteListViewModel, ClienteListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_compras_CompraFormViewModel, CompraFormViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_consultas_ConsultasViewModel, ConsultasViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_pereciveis_encomenda_EncomendaFormViewModel, EncomendaFormViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_consultas_estoque_EstoqueDetalheViewModel, EstoqueDetalheViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_pereciveis_etileno_EtilenoFilaViewModel, EtilenoFilaViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_agenda_EventoFormViewModel, EventoFormViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_consultas_financeiro_FinanceiroDetalheViewModel, FinanceiroDetalheViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_fornecedores_FornecedorFormViewModel, FornecedorFormViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_fornecedores_FornecedorListViewModel, FornecedorListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_grupos_GrupoFormViewModel, GrupoFormViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_grupos_GrupoListViewModel, GrupoListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_home_HomeViewModel, HomeViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_pereciveis_perda_PerdaFormViewModel, PerdaFormViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_produtos_ProdutoFormViewModel, ProdutoFormViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_produtos_ProdutoListViewModel, ProdutoListViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_settings_SettingsViewModel, SettingsViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_vendas_VendaFormViewModel, VendaFormViewModel_HiltModules.KeyModule.provide()).put(LazyClassKeyProvider.br_com_banavita_app_ui_voice_VoiceCaptureViewModel, VoiceCaptureViewModel_HiltModules.KeyModule.provide()).build());
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
      static String br_com_banavita_app_ui_cadastros_grupos_GrupoFormViewModel = "br.com.banavita.app.ui.cadastros.grupos.GrupoFormViewModel";

      static String br_com_banavita_app_ui_agenda_AgendaListViewModel = "br.com.banavita.app.ui.agenda.AgendaListViewModel";

      static String br_com_banavita_app_ui_consultas_estoque_EstoqueDetalheViewModel = "br.com.banavita.app.ui.consultas.estoque.EstoqueDetalheViewModel";

      static String br_com_banavita_app_ui_agenda_EventoFormViewModel = "br.com.banavita.app.ui.agenda.EventoFormViewModel";

      static String br_com_banavita_app_ui_pereciveis_perda_PerdaFormViewModel = "br.com.banavita.app.ui.pereciveis.perda.PerdaFormViewModel";

      static String br_com_banavita_app_ui_cadastros_fornecedores_FornecedorListViewModel = "br.com.banavita.app.ui.cadastros.fornecedores.FornecedorListViewModel";

      static String br_com_banavita_app_ui_compras_CompraFormViewModel = "br.com.banavita.app.ui.compras.CompraFormViewModel";

      static String br_com_banavita_app_ui_vendas_VendaFormViewModel = "br.com.banavita.app.ui.vendas.VendaFormViewModel";

      static String br_com_banavita_app_ui_consultas_financeiro_FinanceiroDetalheViewModel = "br.com.banavita.app.ui.consultas.financeiro.FinanceiroDetalheViewModel";

      static String br_com_banavita_app_ui_cadastros_fornecedores_FornecedorFormViewModel = "br.com.banavita.app.ui.cadastros.fornecedores.FornecedorFormViewModel";

      static String br_com_banavita_app_ui_cadastros_produtos_ProdutoFormViewModel = "br.com.banavita.app.ui.cadastros.produtos.ProdutoFormViewModel";

      static String br_com_banavita_app_ui_cadastros_produtos_ProdutoListViewModel = "br.com.banavita.app.ui.cadastros.produtos.ProdutoListViewModel";

      static String br_com_banavita_app_ui_cadastros_clientes_ClienteFormViewModel = "br.com.banavita.app.ui.cadastros.clientes.ClienteFormViewModel";

      static String br_com_banavita_app_ui_cadastros_grupos_GrupoListViewModel = "br.com.banavita.app.ui.cadastros.grupos.GrupoListViewModel";

      static String br_com_banavita_app_ui_home_HomeViewModel = "br.com.banavita.app.ui.home.HomeViewModel";

      static String br_com_banavita_app_ui_consultas_ConsultasViewModel = "br.com.banavita.app.ui.consultas.ConsultasViewModel";

      static String br_com_banavita_app_ui_pereciveis_etileno_EtilenoFilaViewModel = "br.com.banavita.app.ui.pereciveis.etileno.EtilenoFilaViewModel";

      static String br_com_banavita_app_ui_cadastros_clientes_ClienteListViewModel = "br.com.banavita.app.ui.cadastros.clientes.ClienteListViewModel";

      static String br_com_banavita_app_ui_pereciveis_encomenda_EncomendaFormViewModel = "br.com.banavita.app.ui.pereciveis.encomenda.EncomendaFormViewModel";

      static String br_com_banavita_app_ui_settings_SettingsViewModel = "br.com.banavita.app.ui.settings.SettingsViewModel";

      static String br_com_banavita_app_ui_voice_VoiceCaptureViewModel = "br.com.banavita.app.ui.voice.VoiceCaptureViewModel";

      static String br_com_banavita_app_ui_app_BanavitaAppViewModel = "br.com.banavita.app.ui.app.BanavitaAppViewModel";

      static String br_com_banavita_app_ui_activation_ActivationViewModel = "br.com.banavita.app.ui.activation.ActivationViewModel";

      @KeepFieldType
      GrupoFormViewModel br_com_banavita_app_ui_cadastros_grupos_GrupoFormViewModel2;

      @KeepFieldType
      AgendaListViewModel br_com_banavita_app_ui_agenda_AgendaListViewModel2;

      @KeepFieldType
      EstoqueDetalheViewModel br_com_banavita_app_ui_consultas_estoque_EstoqueDetalheViewModel2;

      @KeepFieldType
      EventoFormViewModel br_com_banavita_app_ui_agenda_EventoFormViewModel2;

      @KeepFieldType
      PerdaFormViewModel br_com_banavita_app_ui_pereciveis_perda_PerdaFormViewModel2;

      @KeepFieldType
      FornecedorListViewModel br_com_banavita_app_ui_cadastros_fornecedores_FornecedorListViewModel2;

      @KeepFieldType
      CompraFormViewModel br_com_banavita_app_ui_compras_CompraFormViewModel2;

      @KeepFieldType
      VendaFormViewModel br_com_banavita_app_ui_vendas_VendaFormViewModel2;

      @KeepFieldType
      FinanceiroDetalheViewModel br_com_banavita_app_ui_consultas_financeiro_FinanceiroDetalheViewModel2;

      @KeepFieldType
      FornecedorFormViewModel br_com_banavita_app_ui_cadastros_fornecedores_FornecedorFormViewModel2;

      @KeepFieldType
      ProdutoFormViewModel br_com_banavita_app_ui_cadastros_produtos_ProdutoFormViewModel2;

      @KeepFieldType
      ProdutoListViewModel br_com_banavita_app_ui_cadastros_produtos_ProdutoListViewModel2;

      @KeepFieldType
      ClienteFormViewModel br_com_banavita_app_ui_cadastros_clientes_ClienteFormViewModel2;

      @KeepFieldType
      GrupoListViewModel br_com_banavita_app_ui_cadastros_grupos_GrupoListViewModel2;

      @KeepFieldType
      HomeViewModel br_com_banavita_app_ui_home_HomeViewModel2;

      @KeepFieldType
      ConsultasViewModel br_com_banavita_app_ui_consultas_ConsultasViewModel2;

      @KeepFieldType
      EtilenoFilaViewModel br_com_banavita_app_ui_pereciveis_etileno_EtilenoFilaViewModel2;

      @KeepFieldType
      ClienteListViewModel br_com_banavita_app_ui_cadastros_clientes_ClienteListViewModel2;

      @KeepFieldType
      EncomendaFormViewModel br_com_banavita_app_ui_pereciveis_encomenda_EncomendaFormViewModel2;

      @KeepFieldType
      SettingsViewModel br_com_banavita_app_ui_settings_SettingsViewModel2;

      @KeepFieldType
      VoiceCaptureViewModel br_com_banavita_app_ui_voice_VoiceCaptureViewModel2;

      @KeepFieldType
      BanavitaAppViewModel br_com_banavita_app_ui_app_BanavitaAppViewModel2;

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

    private Provider<ConsultasViewModel> consultasViewModelProvider;

    private Provider<EncomendaFormViewModel> encomendaFormViewModelProvider;

    private Provider<EstoqueDetalheViewModel> estoqueDetalheViewModelProvider;

    private Provider<EtilenoFilaViewModel> etilenoFilaViewModelProvider;

    private Provider<EventoFormViewModel> eventoFormViewModelProvider;

    private Provider<FinanceiroDetalheViewModel> financeiroDetalheViewModelProvider;

    private Provider<FornecedorFormViewModel> fornecedorFormViewModelProvider;

    private Provider<FornecedorListViewModel> fornecedorListViewModelProvider;

    private Provider<GrupoFormViewModel> grupoFormViewModelProvider;

    private Provider<GrupoListViewModel> grupoListViewModelProvider;

    private Provider<HomeViewModel> homeViewModelProvider;

    private Provider<PerdaFormViewModel> perdaFormViewModelProvider;

    private Provider<ProdutoFormViewModel> produtoFormViewModelProvider;

    private Provider<ProdutoListViewModel> produtoListViewModelProvider;

    private Provider<SettingsViewModel> settingsViewModelProvider;

    private Provider<VendaFormViewModel> vendaFormViewModelProvider;

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
      this.consultasViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 6);
      this.encomendaFormViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 7);
      this.estoqueDetalheViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 8);
      this.etilenoFilaViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 9);
      this.eventoFormViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 10);
      this.financeiroDetalheViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 11);
      this.fornecedorFormViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 12);
      this.fornecedorListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 13);
      this.grupoFormViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 14);
      this.grupoListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 15);
      this.homeViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 16);
      this.perdaFormViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 17);
      this.produtoFormViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 18);
      this.produtoListViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 19);
      this.settingsViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 20);
      this.vendaFormViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 21);
      this.voiceCaptureViewModelProvider = new SwitchingProvider<>(singletonCImpl, activityRetainedCImpl, viewModelCImpl, 22);
    }

    @Override
    public Map<Class<?>, javax.inject.Provider<ViewModel>> getHiltViewModelMap() {
      return LazyClassKeyMap.<javax.inject.Provider<ViewModel>>of(MapBuilder.<String, javax.inject.Provider<ViewModel>>newMapBuilder(23).put(LazyClassKeyProvider.br_com_banavita_app_ui_activation_ActivationViewModel, ((Provider) activationViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_agenda_AgendaListViewModel, ((Provider) agendaListViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_app_BanavitaAppViewModel, ((Provider) banavitaAppViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_clientes_ClienteFormViewModel, ((Provider) clienteFormViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_clientes_ClienteListViewModel, ((Provider) clienteListViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_compras_CompraFormViewModel, ((Provider) compraFormViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_consultas_ConsultasViewModel, ((Provider) consultasViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_pereciveis_encomenda_EncomendaFormViewModel, ((Provider) encomendaFormViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_consultas_estoque_EstoqueDetalheViewModel, ((Provider) estoqueDetalheViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_pereciveis_etileno_EtilenoFilaViewModel, ((Provider) etilenoFilaViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_agenda_EventoFormViewModel, ((Provider) eventoFormViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_consultas_financeiro_FinanceiroDetalheViewModel, ((Provider) financeiroDetalheViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_fornecedores_FornecedorFormViewModel, ((Provider) fornecedorFormViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_fornecedores_FornecedorListViewModel, ((Provider) fornecedorListViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_grupos_GrupoFormViewModel, ((Provider) grupoFormViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_grupos_GrupoListViewModel, ((Provider) grupoListViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_home_HomeViewModel, ((Provider) homeViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_pereciveis_perda_PerdaFormViewModel, ((Provider) perdaFormViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_produtos_ProdutoFormViewModel, ((Provider) produtoFormViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_cadastros_produtos_ProdutoListViewModel, ((Provider) produtoListViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_settings_SettingsViewModel, ((Provider) settingsViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_vendas_VendaFormViewModel, ((Provider) vendaFormViewModelProvider)).put(LazyClassKeyProvider.br_com_banavita_app_ui_voice_VoiceCaptureViewModel, ((Provider) voiceCaptureViewModelProvider)).build());
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }

    @IdentifierNameString
    private static final class LazyClassKeyProvider {
      static String br_com_banavita_app_ui_pereciveis_etileno_EtilenoFilaViewModel = "br.com.banavita.app.ui.pereciveis.etileno.EtilenoFilaViewModel";

      static String br_com_banavita_app_ui_app_BanavitaAppViewModel = "br.com.banavita.app.ui.app.BanavitaAppViewModel";

      static String br_com_banavita_app_ui_cadastros_produtos_ProdutoFormViewModel = "br.com.banavita.app.ui.cadastros.produtos.ProdutoFormViewModel";

      static String br_com_banavita_app_ui_consultas_ConsultasViewModel = "br.com.banavita.app.ui.consultas.ConsultasViewModel";

      static String br_com_banavita_app_ui_cadastros_grupos_GrupoFormViewModel = "br.com.banavita.app.ui.cadastros.grupos.GrupoFormViewModel";

      static String br_com_banavita_app_ui_cadastros_clientes_ClienteFormViewModel = "br.com.banavita.app.ui.cadastros.clientes.ClienteFormViewModel";

      static String br_com_banavita_app_ui_cadastros_grupos_GrupoListViewModel = "br.com.banavita.app.ui.cadastros.grupos.GrupoListViewModel";

      static String br_com_banavita_app_ui_compras_CompraFormViewModel = "br.com.banavita.app.ui.compras.CompraFormViewModel";

      static String br_com_banavita_app_ui_cadastros_fornecedores_FornecedorFormViewModel = "br.com.banavita.app.ui.cadastros.fornecedores.FornecedorFormViewModel";

      static String br_com_banavita_app_ui_settings_SettingsViewModel = "br.com.banavita.app.ui.settings.SettingsViewModel";

      static String br_com_banavita_app_ui_cadastros_fornecedores_FornecedorListViewModel = "br.com.banavita.app.ui.cadastros.fornecedores.FornecedorListViewModel";

      static String br_com_banavita_app_ui_home_HomeViewModel = "br.com.banavita.app.ui.home.HomeViewModel";

      static String br_com_banavita_app_ui_vendas_VendaFormViewModel = "br.com.banavita.app.ui.vendas.VendaFormViewModel";

      static String br_com_banavita_app_ui_pereciveis_encomenda_EncomendaFormViewModel = "br.com.banavita.app.ui.pereciveis.encomenda.EncomendaFormViewModel";

      static String br_com_banavita_app_ui_consultas_financeiro_FinanceiroDetalheViewModel = "br.com.banavita.app.ui.consultas.financeiro.FinanceiroDetalheViewModel";

      static String br_com_banavita_app_ui_cadastros_clientes_ClienteListViewModel = "br.com.banavita.app.ui.cadastros.clientes.ClienteListViewModel";

      static String br_com_banavita_app_ui_agenda_AgendaListViewModel = "br.com.banavita.app.ui.agenda.AgendaListViewModel";

      static String br_com_banavita_app_ui_cadastros_produtos_ProdutoListViewModel = "br.com.banavita.app.ui.cadastros.produtos.ProdutoListViewModel";

      static String br_com_banavita_app_ui_pereciveis_perda_PerdaFormViewModel = "br.com.banavita.app.ui.pereciveis.perda.PerdaFormViewModel";

      static String br_com_banavita_app_ui_activation_ActivationViewModel = "br.com.banavita.app.ui.activation.ActivationViewModel";

      static String br_com_banavita_app_ui_consultas_estoque_EstoqueDetalheViewModel = "br.com.banavita.app.ui.consultas.estoque.EstoqueDetalheViewModel";

      static String br_com_banavita_app_ui_agenda_EventoFormViewModel = "br.com.banavita.app.ui.agenda.EventoFormViewModel";

      static String br_com_banavita_app_ui_voice_VoiceCaptureViewModel = "br.com.banavita.app.ui.voice.VoiceCaptureViewModel";

      @KeepFieldType
      EtilenoFilaViewModel br_com_banavita_app_ui_pereciveis_etileno_EtilenoFilaViewModel2;

      @KeepFieldType
      BanavitaAppViewModel br_com_banavita_app_ui_app_BanavitaAppViewModel2;

      @KeepFieldType
      ProdutoFormViewModel br_com_banavita_app_ui_cadastros_produtos_ProdutoFormViewModel2;

      @KeepFieldType
      ConsultasViewModel br_com_banavita_app_ui_consultas_ConsultasViewModel2;

      @KeepFieldType
      GrupoFormViewModel br_com_banavita_app_ui_cadastros_grupos_GrupoFormViewModel2;

      @KeepFieldType
      ClienteFormViewModel br_com_banavita_app_ui_cadastros_clientes_ClienteFormViewModel2;

      @KeepFieldType
      GrupoListViewModel br_com_banavita_app_ui_cadastros_grupos_GrupoListViewModel2;

      @KeepFieldType
      CompraFormViewModel br_com_banavita_app_ui_compras_CompraFormViewModel2;

      @KeepFieldType
      FornecedorFormViewModel br_com_banavita_app_ui_cadastros_fornecedores_FornecedorFormViewModel2;

      @KeepFieldType
      SettingsViewModel br_com_banavita_app_ui_settings_SettingsViewModel2;

      @KeepFieldType
      FornecedorListViewModel br_com_banavita_app_ui_cadastros_fornecedores_FornecedorListViewModel2;

      @KeepFieldType
      HomeViewModel br_com_banavita_app_ui_home_HomeViewModel2;

      @KeepFieldType
      VendaFormViewModel br_com_banavita_app_ui_vendas_VendaFormViewModel2;

      @KeepFieldType
      EncomendaFormViewModel br_com_banavita_app_ui_pereciveis_encomenda_EncomendaFormViewModel2;

      @KeepFieldType
      FinanceiroDetalheViewModel br_com_banavita_app_ui_consultas_financeiro_FinanceiroDetalheViewModel2;

      @KeepFieldType
      ClienteListViewModel br_com_banavita_app_ui_cadastros_clientes_ClienteListViewModel2;

      @KeepFieldType
      AgendaListViewModel br_com_banavita_app_ui_agenda_AgendaListViewModel2;

      @KeepFieldType
      ProdutoListViewModel br_com_banavita_app_ui_cadastros_produtos_ProdutoListViewModel2;

      @KeepFieldType
      PerdaFormViewModel br_com_banavita_app_ui_pereciveis_perda_PerdaFormViewModel2;

      @KeepFieldType
      ActivationViewModel br_com_banavita_app_ui_activation_ActivationViewModel2;

      @KeepFieldType
      EstoqueDetalheViewModel br_com_banavita_app_ui_consultas_estoque_EstoqueDetalheViewModel2;

      @KeepFieldType
      EventoFormViewModel br_com_banavita_app_ui_agenda_EventoFormViewModel2;

      @KeepFieldType
      VoiceCaptureViewModel br_com_banavita_app_ui_voice_VoiceCaptureViewModel2;
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
          return (T) new AgendaListViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.eventoRepositoryProvider.get());

          case 2: // br.com.banavita.app.ui.app.BanavitaAppViewModel 
          return (T) new BanavitaAppViewModel(singletonCImpl.activationRepositoryProvider.get(), singletonCImpl.appSettingsRepositoryProvider.get(), singletonCImpl.loteRepositoryProvider.get(), singletonCImpl.pereciveisMorningSchedulerProvider.get());

          case 3: // br.com.banavita.app.ui.cadastros.clientes.ClienteFormViewModel 
          return (T) new ClienteFormViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.clienteRepositoryProvider.get());

          case 4: // br.com.banavita.app.ui.cadastros.clientes.ClienteListViewModel 
          return (T) new ClienteListViewModel(singletonCImpl.clienteRepositoryProvider.get());

          case 5: // br.com.banavita.app.ui.compras.CompraFormViewModel 
          return (T) new CompraFormViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.compraRepositoryProvider.get(), singletonCImpl.fornecedorRepositoryProvider.get(), singletonCImpl.produtoRepositoryProvider.get(), singletonCImpl.loteRepositoryProvider.get());

          case 6: // br.com.banavita.app.ui.consultas.ConsultasViewModel 
          return (T) new ConsultasViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.estoqueRepositoryProvider.get(), singletonCImpl.produtoRepositoryProvider.get(), singletonCImpl.vendaRepositoryProvider.get(), singletonCImpl.compraRepositoryProvider.get(), singletonCImpl.clienteRepositoryProvider.get(), singletonCImpl.fornecedorRepositoryProvider.get(), singletonCImpl.financeiroRepositoryProvider.get(), singletonCImpl.loteRepositoryProvider.get(), singletonCImpl.encomendaRepositoryProvider.get(), singletonCImpl.provideRecebimentoDaoProvider.get(), singletonCImpl.providePagamentoDaoProvider.get());

          case 7: // br.com.banavita.app.ui.pereciveis.encomenda.EncomendaFormViewModel 
          return (T) new EncomendaFormViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.encomendaRepositoryProvider.get(), singletonCImpl.clienteRepositoryProvider.get(), singletonCImpl.produtoRepositoryProvider.get(), singletonCImpl.loteRepositoryProvider.get(), singletonCImpl.eventoRepositoryProvider.get());

          case 8: // br.com.banavita.app.ui.consultas.estoque.EstoqueDetalheViewModel 
          return (T) new EstoqueDetalheViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.produtoRepositoryProvider.get(), singletonCImpl.estoqueRepositoryProvider.get(), singletonCImpl.loteRepositoryProvider.get());

          case 9: // br.com.banavita.app.ui.pereciveis.etileno.EtilenoFilaViewModel 
          return (T) new EtilenoFilaViewModel(singletonCImpl.loteRepositoryProvider.get());

          case 10: // br.com.banavita.app.ui.agenda.EventoFormViewModel 
          return (T) new EventoFormViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.eventoRepositoryProvider.get(), singletonCImpl.clienteRepositoryProvider.get(), singletonCImpl.fornecedorRepositoryProvider.get());

          case 11: // br.com.banavita.app.ui.consultas.financeiro.FinanceiroDetalheViewModel 
          return (T) new FinanceiroDetalheViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.financeiroRepositoryProvider.get(), singletonCImpl.clienteRepositoryProvider.get(), singletonCImpl.fornecedorRepositoryProvider.get());

          case 12: // br.com.banavita.app.ui.cadastros.fornecedores.FornecedorFormViewModel 
          return (T) new FornecedorFormViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.fornecedorRepositoryProvider.get());

          case 13: // br.com.banavita.app.ui.cadastros.fornecedores.FornecedorListViewModel 
          return (T) new FornecedorListViewModel(singletonCImpl.fornecedorRepositoryProvider.get());

          case 14: // br.com.banavita.app.ui.cadastros.grupos.GrupoFormViewModel 
          return (T) new GrupoFormViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.grupoRepositoryProvider.get());

          case 15: // br.com.banavita.app.ui.cadastros.grupos.GrupoListViewModel 
          return (T) new GrupoListViewModel(singletonCImpl.grupoRepositoryProvider.get());

          case 16: // br.com.banavita.app.ui.home.HomeViewModel 
          return (T) new HomeViewModel(singletonCImpl.estoqueRepositoryProvider.get(), singletonCImpl.vendaRepositoryProvider.get(), singletonCImpl.compraRepositoryProvider.get(), singletonCImpl.financeiroRepositoryProvider.get(), singletonCImpl.eventoRepositoryProvider.get(), singletonCImpl.loteRepositoryProvider.get(), singletonCImpl.encomendaRepositoryProvider.get(), singletonCImpl.produtoRepositoryProvider.get());

          case 17: // br.com.banavita.app.ui.pereciveis.perda.PerdaFormViewModel 
          return (T) new PerdaFormViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.loteRepositoryProvider.get(), singletonCImpl.produtoRepositoryProvider.get());

          case 18: // br.com.banavita.app.ui.cadastros.produtos.ProdutoFormViewModel 
          return (T) new ProdutoFormViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.produtoRepositoryProvider.get(), singletonCImpl.grupoRepositoryProvider.get());

          case 19: // br.com.banavita.app.ui.cadastros.produtos.ProdutoListViewModel 
          return (T) new ProdutoListViewModel(singletonCImpl.produtoRepositoryProvider.get(), singletonCImpl.grupoRepositoryProvider.get());

          case 20: // br.com.banavita.app.ui.settings.SettingsViewModel 
          return (T) new SettingsViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.appSettingsRepositoryProvider.get(), singletonCImpl.databaseBackupRepositoryProvider.get(), singletonCImpl.pereciveisMorningSchedulerProvider.get(), singletonCImpl.speechToTextRepositoryProvider.get());

          case 21: // br.com.banavita.app.ui.vendas.VendaFormViewModel 
          return (T) new VendaFormViewModel(viewModelCImpl.savedStateHandle, singletonCImpl.vendaRepositoryProvider.get(), singletonCImpl.clienteRepositoryProvider.get(), singletonCImpl.produtoRepositoryProvider.get(), singletonCImpl.estoqueRepositoryProvider.get(), singletonCImpl.loteRepositoryProvider.get(), singletonCImpl.encomendaRepositoryProvider.get());

          case 22: // br.com.banavita.app.ui.voice.VoiceCaptureViewModel 
          return (T) new VoiceCaptureViewModel(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.speechToTextRepositoryProvider.get(), singletonCImpl.extractionRepositoryProvider.get(), singletonCImpl.audioCaptureProvider.get(), singletonCImpl.produtoRepositoryProvider.get(), singletonCImpl.clienteRepositoryProvider.get(), singletonCImpl.fornecedorRepositoryProvider.get());

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

    private Provider<AppSettingsLocalDataSource> appSettingsLocalDataSourceProvider;

    private Provider<EnvGroqConfig> envGroqConfigProvider;

    private Provider<AppSettingsRepository> appSettingsRepositoryProvider;

    private Provider<BanavitaDatabaseHolder> banavitaDatabaseHolderProvider;

    private Provider<BanavitaDatabase> provideBanavitaDatabaseProvider;

    private Provider<EventoDao> provideEventoDaoProvider;

    private Provider<ClienteDao> provideClienteDaoProvider;

    private Provider<FornecedorDao> provideFornecedorDaoProvider;

    private Provider<AndroidReminderScheduler> androidReminderSchedulerProvider;

    private Provider<EventoRepository> eventoRepositoryProvider;

    private Provider<LoteDao> provideLoteDaoProvider;

    private Provider<ProdutoDao> provideProdutoDaoProvider;

    private Provider<PerdaDao> providePerdaDaoProvider;

    private Provider<PrecoQueimaDao> providePrecoQueimaDaoProvider;

    private Provider<LoteRepository> loteRepositoryProvider;

    private Provider<PereciveisMorningScheduler> pereciveisMorningSchedulerProvider;

    private Provider<AndroidDeviceKeystoreManager> androidDeviceKeystoreManagerProvider;

    private Provider<AndroidDeviceFingerprintProvider> androidDeviceFingerprintProvider;

    private Provider<LicenseCodec> licenseCodecProvider;

    private Provider<DefaultLicensePublicKeyProvider> defaultLicensePublicKeyProvider;

    private Provider<LicenseValidator> licenseValidatorProvider;

    private Provider<ActivationLocalDataSource> activationLocalDataSourceProvider;

    private Provider<ActivationRepository> activationRepositoryProvider;

    private Provider<ClienteRepository> clienteRepositoryProvider;

    private Provider<CompraDao> provideCompraDaoProvider;

    private Provider<DocumentoItemFactory> documentoItemFactoryProvider;

    private Provider<PagamentoDao> providePagamentoDaoProvider;

    private Provider<CompraRepository> compraRepositoryProvider;

    private Provider<FornecedorRepository> fornecedorRepositoryProvider;

    private Provider<GrupoDao> provideGrupoDaoProvider;

    private Provider<ProdutoRepository> produtoRepositoryProvider;

    private Provider<EstoqueDao> provideEstoqueDaoProvider;

    private Provider<EstoqueRepository> estoqueRepositoryProvider;

    private Provider<VendaDao> provideVendaDaoProvider;

    private Provider<EstoqueValidator> estoqueValidatorProvider;

    private Provider<RecebimentoDao> provideRecebimentoDaoProvider;

    private Provider<VendaRepository> vendaRepositoryProvider;

    private Provider<FinanceiroRepository> financeiroRepositoryProvider;

    private Provider<EncomendaDao> provideEncomendaDaoProvider;

    private Provider<EncomendaRepository> encomendaRepositoryProvider;

    private Provider<GrupoRepository> grupoRepositoryProvider;

    private Provider<DatabaseBackupRepository> databaseBackupRepositoryProvider;

    private Provider<SpeechModelManager> speechModelManagerProvider;

    private Provider<WhisperCppEngine> whisperCppEngineProvider;

    private Provider<OkHttpClient> provideOkHttpClientProvider;

    private Provider<GroqWhisperClient> groqWhisperClientProvider;

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
      initialize3(applicationContextModuleParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.appSettingsLocalDataSourceProvider = DoubleCheck.provider(new SwitchingProvider<AppSettingsLocalDataSource>(singletonCImpl, 1));
      this.envGroqConfigProvider = DoubleCheck.provider(new SwitchingProvider<EnvGroqConfig>(singletonCImpl, 2));
      this.appSettingsRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AppSettingsRepository>(singletonCImpl, 0));
      this.banavitaDatabaseHolderProvider = DoubleCheck.provider(new SwitchingProvider<BanavitaDatabaseHolder>(singletonCImpl, 6));
      this.provideBanavitaDatabaseProvider = new SwitchingProvider<>(singletonCImpl, 5);
      this.provideEventoDaoProvider = new SwitchingProvider<>(singletonCImpl, 4);
      this.provideClienteDaoProvider = new SwitchingProvider<>(singletonCImpl, 7);
      this.provideFornecedorDaoProvider = new SwitchingProvider<>(singletonCImpl, 8);
      this.androidReminderSchedulerProvider = DoubleCheck.provider(new SwitchingProvider<AndroidReminderScheduler>(singletonCImpl, 9));
      this.eventoRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<EventoRepository>(singletonCImpl, 3));
      this.provideLoteDaoProvider = new SwitchingProvider<>(singletonCImpl, 11);
      this.provideProdutoDaoProvider = new SwitchingProvider<>(singletonCImpl, 12);
      this.providePerdaDaoProvider = new SwitchingProvider<>(singletonCImpl, 13);
      this.providePrecoQueimaDaoProvider = new SwitchingProvider<>(singletonCImpl, 14);
      this.loteRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<LoteRepository>(singletonCImpl, 10));
      this.pereciveisMorningSchedulerProvider = DoubleCheck.provider(new SwitchingProvider<PereciveisMorningScheduler>(singletonCImpl, 15));
      this.androidDeviceKeystoreManagerProvider = DoubleCheck.provider(new SwitchingProvider<AndroidDeviceKeystoreManager>(singletonCImpl, 18));
      this.androidDeviceFingerprintProvider = DoubleCheck.provider(new SwitchingProvider<AndroidDeviceFingerprintProvider>(singletonCImpl, 17));
      this.licenseCodecProvider = DoubleCheck.provider(new SwitchingProvider<LicenseCodec>(singletonCImpl, 19));
      this.defaultLicensePublicKeyProvider = DoubleCheck.provider(new SwitchingProvider<DefaultLicensePublicKeyProvider>(singletonCImpl, 21));
      this.licenseValidatorProvider = DoubleCheck.provider(new SwitchingProvider<LicenseValidator>(singletonCImpl, 20));
      this.activationLocalDataSourceProvider = DoubleCheck.provider(new SwitchingProvider<ActivationLocalDataSource>(singletonCImpl, 22));
      this.activationRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ActivationRepository>(singletonCImpl, 16));
      this.clienteRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ClienteRepository>(singletonCImpl, 23));
      this.provideCompraDaoProvider = new SwitchingProvider<>(singletonCImpl, 25);
    }

    @SuppressWarnings("unchecked")
    private void initialize2(final ApplicationContextModule applicationContextModuleParam) {
      this.documentoItemFactoryProvider = DoubleCheck.provider(new SwitchingProvider<DocumentoItemFactory>(singletonCImpl, 26));
      this.providePagamentoDaoProvider = new SwitchingProvider<>(singletonCImpl, 27);
      this.compraRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<CompraRepository>(singletonCImpl, 24));
      this.fornecedorRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<FornecedorRepository>(singletonCImpl, 28));
      this.provideGrupoDaoProvider = new SwitchingProvider<>(singletonCImpl, 30);
      this.produtoRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ProdutoRepository>(singletonCImpl, 29));
      this.provideEstoqueDaoProvider = new SwitchingProvider<>(singletonCImpl, 32);
      this.estoqueRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<EstoqueRepository>(singletonCImpl, 31));
      this.provideVendaDaoProvider = new SwitchingProvider<>(singletonCImpl, 34);
      this.estoqueValidatorProvider = DoubleCheck.provider(new SwitchingProvider<EstoqueValidator>(singletonCImpl, 35));
      this.provideRecebimentoDaoProvider = new SwitchingProvider<>(singletonCImpl, 36);
      this.vendaRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<VendaRepository>(singletonCImpl, 33));
      this.financeiroRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<FinanceiroRepository>(singletonCImpl, 37));
      this.provideEncomendaDaoProvider = new SwitchingProvider<>(singletonCImpl, 39);
      this.encomendaRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<EncomendaRepository>(singletonCImpl, 38));
      this.grupoRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<GrupoRepository>(singletonCImpl, 40));
      this.databaseBackupRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<DatabaseBackupRepository>(singletonCImpl, 41));
      this.speechModelManagerProvider = DoubleCheck.provider(new SwitchingProvider<SpeechModelManager>(singletonCImpl, 43));
      this.whisperCppEngineProvider = DoubleCheck.provider(new SwitchingProvider<WhisperCppEngine>(singletonCImpl, 44));
      this.provideOkHttpClientProvider = DoubleCheck.provider(new SwitchingProvider<OkHttpClient>(singletonCImpl, 46));
      this.groqWhisperClientProvider = DoubleCheck.provider(new SwitchingProvider<GroqWhisperClient>(singletonCImpl, 45));
      this.speechToTextRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<SpeechToTextRepository>(singletonCImpl, 42));
      this.entityResolverProvider = DoubleCheck.provider(new SwitchingProvider<EntityResolver>(singletonCImpl, 49));
      this.extractionPipelineProvider = DoubleCheck.provider(new SwitchingProvider<ExtractionPipeline>(singletonCImpl, 48));
      this.extractionSessionCacheProvider = DoubleCheck.provider(new SwitchingProvider<ExtractionSessionCache>(singletonCImpl, 50));
    }

    @SuppressWarnings("unchecked")
    private void initialize3(final ApplicationContextModule applicationContextModuleParam) {
      this.extractionRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ExtractionRepository>(singletonCImpl, 47));
      this.audioCaptureProvider = DoubleCheck.provider(new SwitchingProvider<AudioCapture>(singletonCImpl, 51));
    }

    @Override
    public void injectBanavitaApplication(BanavitaApplication banavitaApplication) {
      injectBanavitaApplication2(banavitaApplication);
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
    public void injectPereciveisAlarmReceiver(PereciveisAlarmReceiver pereciveisAlarmReceiver) {
      injectPereciveisAlarmReceiver2(pereciveisAlarmReceiver);
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

    private BanavitaApplication injectBanavitaApplication2(BanavitaApplication instance) {
      BanavitaApplication_MembersInjector.injectAppSettingsRepository(instance, appSettingsRepositoryProvider.get());
      return instance;
    }

    private AgendaAlarmReceiver injectAgendaAlarmReceiver2(AgendaAlarmReceiver instance2) {
      AgendaAlarmReceiver_MembersInjector.injectEventoRepository(instance2, eventoRepositoryProvider.get());
      AgendaAlarmReceiver_MembersInjector.injectAppSettingsRepository(instance2, appSettingsRepositoryProvider.get());
      return instance2;
    }

    private BootCompletedReceiver injectBootCompletedReceiver2(BootCompletedReceiver instance3) {
      BootCompletedReceiver_MembersInjector.injectEventoRepository(instance3, eventoRepositoryProvider.get());
      BootCompletedReceiver_MembersInjector.injectReminderScheduler(instance3, androidReminderSchedulerProvider.get());
      BootCompletedReceiver_MembersInjector.injectLoteRepository(instance3, loteRepositoryProvider.get());
      BootCompletedReceiver_MembersInjector.injectMorningScheduler(instance3, pereciveisMorningSchedulerProvider.get());
      return instance3;
    }

    private PereciveisAlarmReceiver injectPereciveisAlarmReceiver2(
        PereciveisAlarmReceiver instance4) {
      PereciveisAlarmReceiver_MembersInjector.injectLoteRepository(instance4, loteRepositoryProvider.get());
      PereciveisAlarmReceiver_MembersInjector.injectMorningScheduler(instance4, pereciveisMorningSchedulerProvider.get());
      PereciveisAlarmReceiver_MembersInjector.injectAppSettingsRepository(instance4, appSettingsRepositoryProvider.get());
      return instance4;
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
          case 0: // br.com.banavita.app.data.repository.AppSettingsRepository 
          return (T) new AppSettingsRepository(singletonCImpl.appSettingsLocalDataSourceProvider.get(), singletonCImpl.envGroqConfigProvider.get());

          case 1: // br.com.banavita.app.data.local.settings.AppSettingsLocalDataSource 
          return (T) new AppSettingsLocalDataSource(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 2: // br.com.banavita.app.data.remote.groq.EnvGroqConfig 
          return (T) new EnvGroqConfig(singletonCImpl.appSettingsLocalDataSourceProvider.get());

          case 3: // br.com.banavita.app.data.repository.EventoRepository 
          return (T) new EventoRepository(singletonCImpl.provideEventoDaoProvider, singletonCImpl.provideClienteDaoProvider, singletonCImpl.provideFornecedorDaoProvider, singletonCImpl.androidReminderSchedulerProvider.get());

          case 4: // br.com.banavita.app.data.local.dao.EventoDao 
          return (T) DatabaseModule_ProvideEventoDaoFactory.provideEventoDao(singletonCImpl.provideBanavitaDatabaseProvider.get());

          case 5: // br.com.banavita.app.data.local.BanavitaDatabase 
          return (T) DatabaseModule_ProvideBanavitaDatabaseFactory.provideBanavitaDatabase(singletonCImpl.banavitaDatabaseHolderProvider.get());

          case 6: // br.com.banavita.app.data.local.BanavitaDatabaseHolder 
          return (T) new BanavitaDatabaseHolder(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // br.com.banavita.app.data.local.dao.ClienteDao 
          return (T) DatabaseModule_ProvideClienteDaoFactory.provideClienteDao(singletonCImpl.provideBanavitaDatabaseProvider.get());

          case 8: // br.com.banavita.app.data.local.dao.FornecedorDao 
          return (T) DatabaseModule_ProvideFornecedorDaoFactory.provideFornecedorDao(singletonCImpl.provideBanavitaDatabaseProvider.get());

          case 9: // br.com.banavita.app.core.reminder.AndroidReminderScheduler 
          return (T) new AndroidReminderScheduler(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 10: // br.com.banavita.app.data.repository.LoteRepository 
          return (T) new LoteRepository(singletonCImpl.provideBanavitaDatabaseProvider, singletonCImpl.provideLoteDaoProvider, singletonCImpl.provideProdutoDaoProvider, singletonCImpl.providePerdaDaoProvider, singletonCImpl.providePrecoQueimaDaoProvider, singletonCImpl.appSettingsRepositoryProvider.get());

          case 11: // br.com.banavita.app.data.local.dao.LoteDao 
          return (T) DatabaseModule_ProvideLoteDaoFactory.provideLoteDao(singletonCImpl.provideBanavitaDatabaseProvider.get());

          case 12: // br.com.banavita.app.data.local.dao.ProdutoDao 
          return (T) DatabaseModule_ProvideProdutoDaoFactory.provideProdutoDao(singletonCImpl.provideBanavitaDatabaseProvider.get());

          case 13: // br.com.banavita.app.data.local.dao.PerdaDao 
          return (T) DatabaseModule_ProvidePerdaDaoFactory.providePerdaDao(singletonCImpl.provideBanavitaDatabaseProvider.get());

          case 14: // br.com.banavita.app.data.local.dao.PrecoQueimaDao 
          return (T) DatabaseModule_ProvidePrecoQueimaDaoFactory.providePrecoQueimaDao(singletonCImpl.provideBanavitaDatabaseProvider.get());

          case 15: // br.com.banavita.app.core.reminder.PereciveisMorningScheduler 
          return (T) new PereciveisMorningScheduler(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.appSettingsRepositoryProvider.get());

          case 16: // br.com.banavita.app.data.repository.ActivationRepository 
          return (T) new ActivationRepository(singletonCImpl.androidDeviceFingerprintProvider.get(), singletonCImpl.licenseCodecProvider.get(), singletonCImpl.licenseValidatorProvider.get(), singletonCImpl.activationLocalDataSourceProvider.get());

          case 17: // br.com.banavita.app.core.security.AndroidDeviceFingerprintProvider 
          return (T) new AndroidDeviceFingerprintProvider(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.androidDeviceKeystoreManagerProvider.get());

          case 18: // br.com.banavita.app.core.security.AndroidDeviceKeystoreManager 
          return (T) new AndroidDeviceKeystoreManager();

          case 19: // br.com.banavita.app.core.security.LicenseCodec 
          return (T) new LicenseCodec();

          case 20: // br.com.banavita.app.core.security.LicenseValidator 
          return (T) new LicenseValidator(singletonCImpl.licenseCodecProvider.get(), singletonCImpl.androidDeviceFingerprintProvider.get(), singletonCImpl.defaultLicensePublicKeyProvider.get());

          case 21: // br.com.banavita.app.core.security.DefaultLicensePublicKeyProvider 
          return (T) new DefaultLicensePublicKeyProvider();

          case 22: // br.com.banavita.app.data.local.activation.ActivationLocalDataSource 
          return (T) new ActivationLocalDataSource(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 23: // br.com.banavita.app.data.repository.ClienteRepository 
          return (T) new ClienteRepository(singletonCImpl.provideClienteDaoProvider);

          case 24: // br.com.banavita.app.data.repository.CompraRepository 
          return (T) new CompraRepository(singletonCImpl.provideBanavitaDatabaseProvider, singletonCImpl.provideCompraDaoProvider, singletonCImpl.provideFornecedorDaoProvider, singletonCImpl.documentoItemFactoryProvider.get(), singletonCImpl.providePagamentoDaoProvider, singletonCImpl.loteRepositoryProvider.get());

          case 25: // br.com.banavita.app.data.local.dao.CompraDao 
          return (T) DatabaseModule_ProvideCompraDaoFactory.provideCompraDao(singletonCImpl.provideBanavitaDatabaseProvider.get());

          case 26: // br.com.banavita.app.data.repository.DocumentoItemFactory 
          return (T) new DocumentoItemFactory(singletonCImpl.provideProdutoDaoProvider, singletonCImpl.loteRepositoryProvider.get());

          case 27: // br.com.banavita.app.data.local.dao.PagamentoDao 
          return (T) DatabaseModule_ProvidePagamentoDaoFactory.providePagamentoDao(singletonCImpl.provideBanavitaDatabaseProvider.get());

          case 28: // br.com.banavita.app.data.repository.FornecedorRepository 
          return (T) new FornecedorRepository(singletonCImpl.provideFornecedorDaoProvider);

          case 29: // br.com.banavita.app.data.repository.ProdutoRepository 
          return (T) new ProdutoRepository(singletonCImpl.provideProdutoDaoProvider, singletonCImpl.provideGrupoDaoProvider);

          case 30: // br.com.banavita.app.data.local.dao.GrupoDao 
          return (T) DatabaseModule_ProvideGrupoDaoFactory.provideGrupoDao(singletonCImpl.provideBanavitaDatabaseProvider.get());

          case 31: // br.com.banavita.app.data.repository.EstoqueRepository 
          return (T) new EstoqueRepository(singletonCImpl.provideEstoqueDaoProvider);

          case 32: // br.com.banavita.app.data.local.dao.EstoqueDao 
          return (T) DatabaseModule_ProvideEstoqueDaoFactory.provideEstoqueDao(singletonCImpl.provideBanavitaDatabaseProvider.get());

          case 33: // br.com.banavita.app.data.repository.VendaRepository 
          return (T) new VendaRepository(singletonCImpl.provideBanavitaDatabaseProvider, singletonCImpl.provideVendaDaoProvider, singletonCImpl.provideClienteDaoProvider, singletonCImpl.documentoItemFactoryProvider.get(), singletonCImpl.estoqueValidatorProvider.get(), singletonCImpl.provideRecebimentoDaoProvider, singletonCImpl.loteRepositoryProvider.get(), singletonCImpl.provideProdutoDaoProvider);

          case 34: // br.com.banavita.app.data.local.dao.VendaDao 
          return (T) DatabaseModule_ProvideVendaDaoFactory.provideVendaDao(singletonCImpl.provideBanavitaDatabaseProvider.get());

          case 35: // br.com.banavita.app.data.repository.EstoqueValidator 
          return (T) new EstoqueValidator(singletonCImpl.provideEstoqueDaoProvider, singletonCImpl.provideProdutoDaoProvider, singletonCImpl.provideLoteDaoProvider);

          case 36: // br.com.banavita.app.data.local.dao.RecebimentoDao 
          return (T) DatabaseModule_ProvideRecebimentoDaoFactory.provideRecebimentoDao(singletonCImpl.provideBanavitaDatabaseProvider.get());

          case 37: // br.com.banavita.app.data.repository.FinanceiroRepository 
          return (T) new FinanceiroRepository(singletonCImpl.provideVendaDaoProvider, singletonCImpl.provideCompraDaoProvider, singletonCImpl.provideRecebimentoDaoProvider, singletonCImpl.providePagamentoDaoProvider, singletonCImpl.provideClienteDaoProvider, singletonCImpl.provideFornecedorDaoProvider);

          case 38: // br.com.banavita.app.data.repository.EncomendaRepository 
          return (T) new EncomendaRepository(singletonCImpl.provideBanavitaDatabaseProvider, singletonCImpl.provideEncomendaDaoProvider, singletonCImpl.provideClienteDaoProvider, singletonCImpl.provideProdutoDaoProvider, singletonCImpl.loteRepositoryProvider.get(), singletonCImpl.vendaRepositoryProvider.get(), singletonCImpl.eventoRepositoryProvider);

          case 39: // br.com.banavita.app.data.local.dao.EncomendaDao 
          return (T) DatabaseModule_ProvideEncomendaDaoFactory.provideEncomendaDao(singletonCImpl.provideBanavitaDatabaseProvider.get());

          case 40: // br.com.banavita.app.data.repository.GrupoRepository 
          return (T) new GrupoRepository(singletonCImpl.provideGrupoDaoProvider);

          case 41: // br.com.banavita.app.data.repository.DatabaseBackupRepository 
          return (T) new DatabaseBackupRepository(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule), singletonCImpl.banavitaDatabaseHolderProvider.get(), singletonCImpl.eventoRepositoryProvider.get(), singletonCImpl.androidReminderSchedulerProvider.get(), singletonCImpl.pereciveisMorningSchedulerProvider.get());

          case 42: // br.com.banavita.app.data.repository.SpeechToTextRepository 
          return (T) new SpeechToTextRepository(singletonCImpl.speechModelManagerProvider.get(), singletonCImpl.whisperCppEngineProvider.get(), singletonCImpl.groqWhisperClientProvider.get(), singletonCImpl.appSettingsRepositoryProvider.get());

          case 43: // br.com.banavita.app.core.speech.SpeechModelManager 
          return (T) new SpeechModelManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 44: // br.com.banavita.app.core.speech.WhisperCppEngine 
          return (T) new WhisperCppEngine();

          case 45: // br.com.banavita.app.data.remote.groq.GroqWhisperClient 
          return (T) new GroqWhisperClient(singletonCImpl.provideOkHttpClientProvider.get(), singletonCImpl.envGroqConfigProvider.get());

          case 46: // okhttp3.OkHttpClient 
          return (T) NetworkModule_ProvideOkHttpClientFactory.provideOkHttpClient();

          case 47: // br.com.banavita.app.data.repository.ExtractionRepository 
          return (T) new ExtractionRepository(singletonCImpl.extractionPipelineProvider.get(), singletonCImpl.extractionSessionCacheProvider.get(), singletonCImpl.vendaRepositoryProvider.get(), singletonCImpl.compraRepositoryProvider.get(), singletonCImpl.clienteRepositoryProvider.get(), singletonCImpl.fornecedorRepositoryProvider.get(), singletonCImpl.produtoRepositoryProvider.get(), singletonCImpl.grupoRepositoryProvider.get(), singletonCImpl.financeiroRepositoryProvider.get(), singletonCImpl.eventoRepositoryProvider.get());

          case 48: // br.com.banavita.app.core.extraction.ExtractionPipeline 
          return (T) new ExtractionPipeline(singletonCImpl.entityResolverProvider.get());

          case 49: // br.com.banavita.app.core.extraction.EntityResolver 
          return (T) new EntityResolver(singletonCImpl.provideProdutoDaoProvider, singletonCImpl.provideClienteDaoProvider, singletonCImpl.provideFornecedorDaoProvider, singletonCImpl.provideGrupoDaoProvider);

          case 50: // br.com.banavita.app.data.cache.ExtractionSessionCache 
          return (T) new ExtractionSessionCache();

          case 51: // br.com.banavita.app.core.speech.AudioCapture 
          return (T) new AudioCapture(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
