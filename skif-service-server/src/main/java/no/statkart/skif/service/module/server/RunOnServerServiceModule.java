package no.statkart.skif.service.module.server;

import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.service.RunOnServerWithTxBeanManagedService;
import no.statkart.skif.service.RunOnServerWithTxNotSupportedService;
import no.statkart.skif.service.RunOnServerWithTxRequiredService;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
import no.statkart.skif.service.proxy.ChainedProxyHandler;

import java.util.Collection;
import java.util.List;

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
    public static final Collection<Class<? extends Object>> CLASSES = List.of(
        RunOnServerWithTxNotSupportedService.class,
        RunOnServerWithTxRequiresNewService.class,
        RunOnServerWithTxRequiredService.class,
        RunOnServerWithTxBeanManagedService.class);
    
    public RunOnServerServiceModule(ModuleConfiguration configuration) {
        super(configuration, CLASSES);
    }

    @Override
    protected void configure() {
        if (moduleConfiguration.getServiceMode()== ServiceMode.SINGLE_VM) {
            final String ejbServiceChainExtClassname = moduleConfiguration.getConfiguration().getString(SkifConfigConstants.EJB_SERVICE_CHAIN_EXT_CLASS);
            if (ejbServiceChainExtClassname!=null) {
                Class<? extends ChainedProxyHandler> ejbServiceChainExtClass = SkifUtil.classForName(ejbServiceChainExtClassname);
                getStrategy().getEjbServiceChainFactorySpecification().appendEJBServiceChainProxyHandler(ejbServiceChainExtClass);
            }
            super.configure();
        }
    }
}
