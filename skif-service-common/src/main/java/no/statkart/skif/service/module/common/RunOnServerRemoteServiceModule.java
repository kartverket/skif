package no.statkart.skif.service.module.common;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.mapper.IdentityMapper;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.RunOnServerWithTxBeanManagedService;
import no.statkart.skif.service.RunOnServerWithTxNotSupportedService;
import no.statkart.skif.service.RunOnServerWithTxRequiredService;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
import no.statkart.skif.service.ContainerManagedTransactionRunOnServerService;

import java.util.ArrayList;
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
        super(configuration, getList(), new IdentityMapper().getMapping());
    }

    private static Collection<Class<? extends Object>> getList() {
        ArrayList<Class<?>> list = new ArrayList<Class<?>>();
        list.add(RunOnServerWithTxNotSupportedService.class);
        list.add(RunOnServerWithTxRequiresNewService.class);
        list.add(RunOnServerWithTxRequiredService.class);
        list.add(RunOnServerWithTxBeanManagedService.class);

        // TODO: Ta bort
        list.add(ContainerManagedTransactionRunOnServerService.class);

        return list;
    }



    @Override
    protected void configure() {
        if (moduleConfiguration.getServiceMode()== ServiceMode.SINGLE_VM) {
            super.configure();
        }
    }
}
