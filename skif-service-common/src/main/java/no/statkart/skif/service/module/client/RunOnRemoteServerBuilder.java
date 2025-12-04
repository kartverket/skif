package no.statkart.skif.service.module.client;

import com.google.inject.Injector;
import no.statkart.skif.SkifModule;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.SkifServerConfiguration;
import no.statkart.skif.module.ModuleBuilder;
import no.statkart.skif.service.RunOnServerWithTxBeanManagedService;
import no.statkart.skif.service.RunOnServerWithTxNotSupportedService;
import no.statkart.skif.service.RunOnServerWithTxRequiredService;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
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
        this(SkifUtil.<SkifModule>classForName(serverModuleClassName));
    }

    public RunOnRemoteServerBuilder(Class<? extends SkifModule> serverModuleClass) {
        this(serverModuleClass, null, null);
    }

    public RunOnRemoteServerBuilder(Class<? extends SkifModule> serverModuleClass, Class<? extends SkifModule> serverModuleExtClass, Class<? extends ChainedProxyHandler> ejbServiceChainExtClass) {
        moduleBuilder = new ModuleBuilder();
        moduleBuilder.setSingleVm(true);
        moduleBuilder.setUseSharedServer(true);
        moduleBuilder.setModuleClass(RunOnRemoteServerClientModule.class);
        moduleBuilder.setSingleVmServerConfiguration(new SkifServerConfiguration());
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
     */
    public ClientModule buildModule() {
          return (ClientModule) moduleBuilder.buildModule();
    }

    public Injector buildInjector() {
          return  moduleBuilder.buildInjector();
    }

    public RunOnServerWithTxNotSupportedService buildContainerManagedNotSupportedTranactionService() {
        return moduleBuilder.buildInjector().getInstance(RunOnServerWithTxNotSupportedService.class);
    }

    public RunOnServerWithTxRequiresNewService buildContainerManagedRequiresNewTranactionService() {
        return moduleBuilder.buildInjector().getInstance(RunOnServerWithTxRequiresNewService.class);
    }

    public RunOnServerWithTxRequiredService buildContainerManagedRequiredTranactionService() {
        return moduleBuilder.buildInjector().getInstance(RunOnServerWithTxRequiredService.class);
    }

    public RunOnServerWithTxBeanManagedService buildBeanManagedService() {
        return moduleBuilder.buildInjector().getInstance(RunOnServerWithTxBeanManagedService.class);
    }
}
