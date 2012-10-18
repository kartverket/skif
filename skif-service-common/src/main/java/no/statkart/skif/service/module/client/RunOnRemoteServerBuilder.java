package no.statkart.skif.service.module.client;

import com.google.inject.Injector;
import no.statkart.skif.SkifModule;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.SkifConfiguration;
import no.statkart.skif.module.ModuleBuilder;
import no.statkart.skif.service.BeanManagedTransactionRunOnServerService;
import no.statkart.skif.service.ContainerManagedNotSupportedTransactionRunOnServerService;
import no.statkart.skif.service.ContainerManagedRequiresNewTransactionRunOnServerService;
import no.statkart.skif.service.ContainerManagedTransactionRunOnServerService;
import no.statkart.skif.service.proxy.ChainedProxyHandler;

/**
 * Hjelpeklasse for å sette opp en SingleVm klient som bare skal kjøre tjenester i server mode. Som parameter tar klasse
 * hvilke ServerModule som skal brukes for å sette opp serveren.
 *
 * Gjentatte kall til build*() metodene vil produsere klient-Injectorere som alle deler samme underliggende server.
 *
 * @author Henrik Fredholm
 * @see no.statkart.skif.service.module.client.RunOnRemoteServerClientModule
 * @since 2.0
 */
public class RunOnRemoteServerBuilder {
    final ModuleBuilder moduleBuilder;

    public RunOnRemoteServerBuilder(String serverModuleClassName) {
        this((Class<? extends SkifModule>)SkifUtil.classForName(serverModuleClassName));
    }

    public RunOnRemoteServerBuilder(Class<? extends SkifModule> serverModuleClass) {
        this(serverModuleClass, null, null);
    }

    public RunOnRemoteServerBuilder(Class<? extends SkifModule> serverModuleClass, Class<? extends SkifModule> serverModuleExtClass, Class<? extends ChainedProxyHandler> ejbServiceChainExtClass) {
        moduleBuilder = new ModuleBuilder();
        moduleBuilder.setSingleVm(true);
        moduleBuilder.setUseSharedServer(true);
        moduleBuilder.setModuleClass(RunOnRemoteServerClientModule.class);
        moduleBuilder.setSingleVmServerConfiguration(new SkifConfiguration());
        moduleBuilder.setSingleVmServerModuleClass(serverModuleClass);
        if(serverModuleExtClass!=null) {
            moduleBuilder.setSingleVmServerModuleExtClass(serverModuleExtClass);
        }
        if (ejbServiceChainExtClass!=null) {
            moduleBuilder.setSingleVmServerEjbServiceChainExtClass(ejbServiceChainExtClass);
        }
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

    public ContainerManagedNotSupportedTransactionRunOnServerService buildContainerManagedNotSupportedTranactionService() {
        return moduleBuilder.buildInjector().getInstance(ContainerManagedNotSupportedTransactionRunOnServerService.class);
    }

    public ContainerManagedRequiresNewTransactionRunOnServerService buildContainerManagedRequiresNewTranactionService() {
        return moduleBuilder.buildInjector().getInstance(ContainerManagedRequiresNewTransactionRunOnServerService.class);
    }

    public BeanManagedTransactionRunOnServerService buildBeanManagedService() {
        return moduleBuilder.buildInjector().getInstance(BeanManagedTransactionRunOnServerService.class);
    }
}
