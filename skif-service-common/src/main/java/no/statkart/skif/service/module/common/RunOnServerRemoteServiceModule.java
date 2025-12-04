package no.statkart.skif.service.module.common;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.mapper.IdentityMapper;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.RunOnServerWithTxBeanManagedService;
import no.statkart.skif.service.RunOnServerWithTxNotSupportedService;
import no.statkart.skif.service.RunOnServerWithTxRequiredService;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;

import java.util.Collection;
import java.util.List;

/**
 * RemoteServiceModule som i SingleVm mode installere tjenester som gjøre det mulig for en klient å kjøre
 * vilkårlig kode på serveren uten først å måtte definere en service som inneholder koden.
 *
 * Denne modul brukes for testing og patching i SingleVm mode. I JEE mode installerer modulen ingen tjenester.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class RunOnServerRemoteServiceModule extends RemoteServiceModule {
    public static final Collection<Class<? extends Object>> CLASSES = List.of(
        RunOnServerWithTxNotSupportedService.class,
        RunOnServerWithTxRequiresNewService.class,
        RunOnServerWithTxRequiredService.class,
        RunOnServerWithTxBeanManagedService.class);


    public RunOnServerRemoteServiceModule(ModuleConfiguration configuration) {
        super(configuration, CLASSES, new IdentityMapper().getMapping());
    }

    @Override
    protected void configure() {
        if (moduleConfiguration.getServiceMode()== ServiceMode.SINGLE_VM) {
            super.configure();
        }
    }
}
