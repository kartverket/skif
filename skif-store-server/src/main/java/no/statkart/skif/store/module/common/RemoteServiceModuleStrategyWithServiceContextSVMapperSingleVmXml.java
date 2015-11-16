package no.statkart.skif.store.module.common;

import no.statkart.skif.service.module.common.RemoteServiceModuleStrategySingleVmXml;
import no.statkart.skif.service.proxy.SingleVmViaWSWithServiceContextSVMapperRemoteCallProxyHandler;

/**
 * Strategi for kjøring i SingleVM, men med serialisering via XML mellom klient og tjener og med mapping av {@code SnapshotVersion}
 * <p>
 * Denne klassen har samme funksjonalitet som {@link RemoteServiceModuleStrategySingleVmXml}, men konfigurerer opp
 * en annen {@code SingleVmRemoteCallProxyHandler}
 *
 * @author Henrik Fredholm
 * @since 2.4.0
 */
public class RemoteServiceModuleStrategyWithServiceContextSVMapperSingleVmXml extends RemoteServiceModuleStrategySingleVmXml {
    public RemoteServiceModuleStrategyWithServiceContextSVMapperSingleVmXml() {
        super(SingleVmViaWSWithServiceContextSVMapperRemoteCallProxyHandler.class);
    };
}
