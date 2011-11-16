package no.statkart.skif.service.module.server;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.mapper.IdentityMapper;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleWithStrategy;
import no.statkart.skif.service.BeanManagedTransactionRunOnServerService;
import no.statkart.skif.service.ContainerManagedTransactionRunOnServerService;

import java.util.*;

/**
 * ServerServiceModule som i SingleVm mode installere tjenester i serveren som gjøre det mulig for en klient å kjøre
 * vilkårlig kode på serveren uten først å måtte definere en service som inneholder koden.
 *
 * Denne modul brukes for testing og patching i SingleVm mode. I JEE mode installerer modulen ingen tjenester.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class RunOnServerServiceModule extends ServerServiceModule {
    public RunOnServerServiceModule(ModuleConfiguration configuration) {
        super(configuration, Arrays.asList(ContainerManagedTransactionRunOnServerService.class, BeanManagedTransactionRunOnServerService.class));
    }

    @Override
    protected void configure() {
        if (moduleConfiguration.getServiceMode()== ServiceMode.SINGLE_VM) {
            super.configure();
        }
    }
}
