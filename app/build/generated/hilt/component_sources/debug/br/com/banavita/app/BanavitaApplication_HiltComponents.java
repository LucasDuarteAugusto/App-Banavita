package br.com.banavita.app;

import br.com.banavita.app.core.reminder.AgendaAlarmReceiver_GeneratedInjector;
import br.com.banavita.app.core.reminder.BootCompletedReceiver_GeneratedInjector;
import br.com.banavita.app.di.DatabaseModule;
import br.com.banavita.app.di.ExtractionModule;
import br.com.banavita.app.di.NetworkModule;
import br.com.banavita.app.di.ReminderModule;
import br.com.banavita.app.di.SecurityModule;
import br.com.banavita.app.di.SpeechModule;
import br.com.banavita.app.ui.activation.ActivationViewModel_HiltModules;
import br.com.banavita.app.ui.agenda.AgendaListViewModel_HiltModules;
import br.com.banavita.app.ui.agenda.EventoFormViewModel_HiltModules;
import br.com.banavita.app.ui.app.BanavitaAppViewModel_HiltModules;
import br.com.banavita.app.ui.cadastros.clientes.ClienteFormViewModel_HiltModules;
import br.com.banavita.app.ui.cadastros.clientes.ClienteListViewModel_HiltModules;
import br.com.banavita.app.ui.cadastros.fornecedores.FornecedorFormViewModel_HiltModules;
import br.com.banavita.app.ui.cadastros.fornecedores.FornecedorListViewModel_HiltModules;
import br.com.banavita.app.ui.cadastros.grupos.GrupoFormViewModel_HiltModules;
import br.com.banavita.app.ui.cadastros.grupos.GrupoListViewModel_HiltModules;
import br.com.banavita.app.ui.cadastros.produtos.ProdutoFormViewModel_HiltModules;
import br.com.banavita.app.ui.cadastros.produtos.ProdutoListViewModel_HiltModules;
import br.com.banavita.app.ui.compras.CompraFormViewModel_HiltModules;
import br.com.banavita.app.ui.compras.CompraListViewModel_HiltModules;
import br.com.banavita.app.ui.consultas.ConsultasViewModel_HiltModules;
import br.com.banavita.app.ui.consultas.estoque.EstoqueDetalheViewModel_HiltModules;
import br.com.banavita.app.ui.consultas.financeiro.FinanceiroDetalheViewModel_HiltModules;
import br.com.banavita.app.ui.home.HomeViewModel_HiltModules;
import br.com.banavita.app.ui.settings.SettingsViewModel_HiltModules;
import br.com.banavita.app.ui.vendas.VendaFormViewModel_HiltModules;
import br.com.banavita.app.ui.vendas.VendaListViewModel_HiltModules;
import br.com.banavita.app.ui.voice.VoiceCaptureViewModel_HiltModules;
import dagger.Binds;
import dagger.Component;
import dagger.Module;
import dagger.Subcomponent;
import dagger.hilt.android.components.ActivityComponent;
import dagger.hilt.android.components.ActivityRetainedComponent;
import dagger.hilt.android.components.FragmentComponent;
import dagger.hilt.android.components.ServiceComponent;
import dagger.hilt.android.components.ViewComponent;
import dagger.hilt.android.components.ViewModelComponent;
import dagger.hilt.android.components.ViewWithFragmentComponent;
import dagger.hilt.android.flags.FragmentGetContextFix;
import dagger.hilt.android.flags.HiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.HiltViewModelFactory;
import dagger.hilt.android.internal.lifecycle.HiltWrapper_DefaultViewModelFactories_ActivityModule;
import dagger.hilt.android.internal.lifecycle.HiltWrapper_HiltViewModelFactory_ActivityCreatorEntryPoint;
import dagger.hilt.android.internal.lifecycle.HiltWrapper_HiltViewModelFactory_ViewModelModule;
import dagger.hilt.android.internal.managers.ActivityComponentManager;
import dagger.hilt.android.internal.managers.FragmentComponentManager;
import dagger.hilt.android.internal.managers.HiltWrapper_ActivityRetainedComponentManager_ActivityRetainedComponentBuilderEntryPoint;
import dagger.hilt.android.internal.managers.HiltWrapper_ActivityRetainedComponentManager_ActivityRetainedLifecycleEntryPoint;
import dagger.hilt.android.internal.managers.HiltWrapper_ActivityRetainedComponentManager_LifecycleModule;
import dagger.hilt.android.internal.managers.HiltWrapper_SavedStateHandleModule;
import dagger.hilt.android.internal.managers.ServiceComponentManager;
import dagger.hilt.android.internal.managers.ViewComponentManager;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.HiltWrapper_ActivityModule;
import dagger.hilt.android.scopes.ActivityRetainedScoped;
import dagger.hilt.android.scopes.ActivityScoped;
import dagger.hilt.android.scopes.FragmentScoped;
import dagger.hilt.android.scopes.ServiceScoped;
import dagger.hilt.android.scopes.ViewModelScoped;
import dagger.hilt.android.scopes.ViewScoped;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.internal.GeneratedComponent;
import dagger.hilt.migration.DisableInstallInCheck;
import javax.annotation.processing.Generated;
import javax.inject.Singleton;

@Generated("dagger.hilt.processor.internal.root.RootProcessor")
public final class BanavitaApplication_HiltComponents {
  private BanavitaApplication_HiltComponents() {
  }

  @Module(
      subcomponents = ServiceC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ServiceCBuilderModule {
    @Binds
    ServiceComponentBuilder bind(ServiceC.Builder builder);
  }

  @Module(
      subcomponents = ActivityRetainedC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ActivityRetainedCBuilderModule {
    @Binds
    ActivityRetainedComponentBuilder bind(ActivityRetainedC.Builder builder);
  }

  @Module(
      subcomponents = ActivityC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ActivityCBuilderModule {
    @Binds
    ActivityComponentBuilder bind(ActivityC.Builder builder);
  }

  @Module(
      subcomponents = ViewModelC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ViewModelCBuilderModule {
    @Binds
    ViewModelComponentBuilder bind(ViewModelC.Builder builder);
  }

  @Module(
      subcomponents = ViewC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ViewCBuilderModule {
    @Binds
    ViewComponentBuilder bind(ViewC.Builder builder);
  }

  @Module(
      subcomponents = FragmentC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface FragmentCBuilderModule {
    @Binds
    FragmentComponentBuilder bind(FragmentC.Builder builder);
  }

  @Module(
      subcomponents = ViewWithFragmentC.class
  )
  @DisableInstallInCheck
  @Generated("dagger.hilt.processor.internal.root.RootProcessor")
  abstract interface ViewWithFragmentCBuilderModule {
    @Binds
    ViewWithFragmentComponentBuilder bind(ViewWithFragmentC.Builder builder);
  }

  @Component(
      modules = {
          ApplicationContextModule.class,
          ActivityRetainedCBuilderModule.class,
          ServiceCBuilderModule.class,
          DatabaseModule.class,
          ExtractionModule.class,
          HiltWrapper_FragmentGetContextFix_FragmentGetContextFixModule.class,
          NetworkModule.class,
          ReminderModule.class,
          SecurityModule.class,
          SpeechModule.class
      }
  )
  @Singleton
  public abstract static class SingletonC implements BanavitaApplication_GeneratedInjector,
      AgendaAlarmReceiver_GeneratedInjector,
      BootCompletedReceiver_GeneratedInjector,
      FragmentGetContextFix.FragmentGetContextFixEntryPoint,
      HiltWrapper_ActivityRetainedComponentManager_ActivityRetainedComponentBuilderEntryPoint,
      ServiceComponentManager.ServiceComponentBuilderEntryPoint,
      SingletonComponent,
      GeneratedComponent {
  }

  @Subcomponent
  @ServiceScoped
  public abstract static class ServiceC implements ServiceComponent,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ServiceComponentBuilder {
    }
  }

  @Subcomponent(
      modules = {
          ActivationViewModel_HiltModules.KeyModule.class,
          AgendaListViewModel_HiltModules.KeyModule.class,
          BanavitaAppViewModel_HiltModules.KeyModule.class,
          ActivityCBuilderModule.class,
          ViewModelCBuilderModule.class,
          ClienteFormViewModel_HiltModules.KeyModule.class,
          ClienteListViewModel_HiltModules.KeyModule.class,
          CompraFormViewModel_HiltModules.KeyModule.class,
          CompraListViewModel_HiltModules.KeyModule.class,
          ConsultasViewModel_HiltModules.KeyModule.class,
          EstoqueDetalheViewModel_HiltModules.KeyModule.class,
          EventoFormViewModel_HiltModules.KeyModule.class,
          FinanceiroDetalheViewModel_HiltModules.KeyModule.class,
          FornecedorFormViewModel_HiltModules.KeyModule.class,
          FornecedorListViewModel_HiltModules.KeyModule.class,
          GrupoFormViewModel_HiltModules.KeyModule.class,
          GrupoListViewModel_HiltModules.KeyModule.class,
          HiltWrapper_ActivityRetainedComponentManager_LifecycleModule.class,
          HiltWrapper_SavedStateHandleModule.class,
          HomeViewModel_HiltModules.KeyModule.class,
          ProdutoFormViewModel_HiltModules.KeyModule.class,
          ProdutoListViewModel_HiltModules.KeyModule.class,
          SettingsViewModel_HiltModules.KeyModule.class,
          VendaFormViewModel_HiltModules.KeyModule.class,
          VendaListViewModel_HiltModules.KeyModule.class,
          VoiceCaptureViewModel_HiltModules.KeyModule.class
      }
  )
  @ActivityRetainedScoped
  public abstract static class ActivityRetainedC implements ActivityRetainedComponent,
      ActivityComponentManager.ActivityComponentBuilderEntryPoint,
      HiltWrapper_ActivityRetainedComponentManager_ActivityRetainedLifecycleEntryPoint,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ActivityRetainedComponentBuilder {
    }
  }

  @Subcomponent(
      modules = {
          FragmentCBuilderModule.class,
          ViewCBuilderModule.class,
          HiltWrapper_ActivityModule.class,
          HiltWrapper_DefaultViewModelFactories_ActivityModule.class
      }
  )
  @ActivityScoped
  public abstract static class ActivityC implements MainActivity_GeneratedInjector,
      ActivityComponent,
      DefaultViewModelFactories.ActivityEntryPoint,
      HiltWrapper_HiltViewModelFactory_ActivityCreatorEntryPoint,
      FragmentComponentManager.FragmentComponentBuilderEntryPoint,
      ViewComponentManager.ViewComponentBuilderEntryPoint,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ActivityComponentBuilder {
    }
  }

  @Subcomponent(
      modules = {
          ActivationViewModel_HiltModules.BindsModule.class,
          AgendaListViewModel_HiltModules.BindsModule.class,
          BanavitaAppViewModel_HiltModules.BindsModule.class,
          ClienteFormViewModel_HiltModules.BindsModule.class,
          ClienteListViewModel_HiltModules.BindsModule.class,
          CompraFormViewModel_HiltModules.BindsModule.class,
          CompraListViewModel_HiltModules.BindsModule.class,
          ConsultasViewModel_HiltModules.BindsModule.class,
          EstoqueDetalheViewModel_HiltModules.BindsModule.class,
          EventoFormViewModel_HiltModules.BindsModule.class,
          FinanceiroDetalheViewModel_HiltModules.BindsModule.class,
          FornecedorFormViewModel_HiltModules.BindsModule.class,
          FornecedorListViewModel_HiltModules.BindsModule.class,
          GrupoFormViewModel_HiltModules.BindsModule.class,
          GrupoListViewModel_HiltModules.BindsModule.class,
          HiltWrapper_HiltViewModelFactory_ViewModelModule.class,
          HomeViewModel_HiltModules.BindsModule.class,
          ProdutoFormViewModel_HiltModules.BindsModule.class,
          ProdutoListViewModel_HiltModules.BindsModule.class,
          SettingsViewModel_HiltModules.BindsModule.class,
          VendaFormViewModel_HiltModules.BindsModule.class,
          VendaListViewModel_HiltModules.BindsModule.class,
          VoiceCaptureViewModel_HiltModules.BindsModule.class
      }
  )
  @ViewModelScoped
  public abstract static class ViewModelC implements ViewModelComponent,
      HiltViewModelFactory.ViewModelFactoriesEntryPoint,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ViewModelComponentBuilder {
    }
  }

  @Subcomponent
  @ViewScoped
  public abstract static class ViewC implements ViewComponent,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ViewComponentBuilder {
    }
  }

  @Subcomponent(
      modules = ViewWithFragmentCBuilderModule.class
  )
  @FragmentScoped
  public abstract static class FragmentC implements FragmentComponent,
      DefaultViewModelFactories.FragmentEntryPoint,
      ViewComponentManager.ViewWithFragmentComponentBuilderEntryPoint,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends FragmentComponentBuilder {
    }
  }

  @Subcomponent
  @ViewScoped
  public abstract static class ViewWithFragmentC implements ViewWithFragmentComponent,
      GeneratedComponent {
    @Subcomponent.Builder
    abstract interface Builder extends ViewWithFragmentComponentBuilder {
    }
  }
}
