package no.statkart.skif.service.module.client;

import com.google.inject.Injector;
import com.google.inject.Module;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.SkifConfiguration;
import no.statkart.skif.module.ModuleBuilder;
import no.statkart.skif.service.BeanManagedTransactionRunOnServerService;
import no.statkart.skif.service.ContainerManagedTransactionRunOnServerService;

/**
 * Hjelpeklasse for å sette opp en SingleVm klient som bare skal kjøre tjenester i server mode. Som parameter tar klasse
 * hvilke ServerModule som skal brukes for å sette opp serveren.
 *
 * Gjentatte kall til build metodene vil dele samme underliggende server.
 * @author Henrik Fredholm
 * @since 2.1
 */
public class RunOnRemoteServerBuilder {
    final ModuleBuilder moduleBuilder;

    public RunOnRemoteServerBuilder(String serverModuleClassName) {
        this((Class<? extends Module>)SkifUtil.classForName(serverModuleClassName));
    }

    public RunOnRemoteServerBuilder(Class<? extends Module> serverModuleClass) {
        moduleBuilder = new ModuleBuilder();
        moduleBuilder.setSingleVm(true);
        moduleBuilder.setUseSharedServer(true);
        moduleBuilder.setModuleClass(RunOnRemoteServerClientModule.class);
        moduleBuilder.setSingleVmServerConfiguration(new SkifConfiguration());
        moduleBuilder.setSingleVmServerModuleClass(serverModuleClass);
    }

    /**
     * Returnerer en modul som kan settes sammen med andre Guice moduler
     * @return
     */
    public ClientModule buildModule() {
          return (ClientModule) moduleBuilder.buildModule();
    }

    public Injector buildInjector() {
          return  moduleBuilder.buildInjector();
    }

    public ContainerManagedTransactionRunOnServerService buildContainerManagedService() {
        return moduleBuilder.buildInjector().getInstance(ContainerManagedTransactionRunOnServerService.class);
    }

    public BeanManagedTransactionRunOnServerService buildBeanManagedService() {
        return moduleBuilder.buildInjector().getInstance(BeanManagedTransactionRunOnServerService.class);
    }
}
