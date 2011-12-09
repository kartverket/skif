package no.statkart.skif.service.module.common;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.mapper.IdentityMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.BeanManagedTransactionRunOnServerService;
import no.statkart.skif.service.ContainerManagedTransactionRunOnServerService;

import java.util.Arrays;
import java.util.Collection;

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

    public RunOnServerRemoteServiceModule(ModuleConfiguration configuration) {
        super(configuration, Arrays.asList(ContainerManagedTransactionRunOnServerService.class, BeanManagedTransactionRunOnServerService.class), new IdentityMapper().getMapping());
    }

    @Override
    protected void configure() {
        if (moduleConfiguration.getServiceMode()== ServiceMode.SINGLE_VM) {
            super.configure();
        }
    }
}
