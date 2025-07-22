package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.skiftest.service.txbmt.BeanManagedTxAService;
import no.statkart.skif.skiftest.service.txcascade.ContainerManagedTxCMTCascadeService;
import no.statkart.skif.skiftest.service.txcmt.ContainerManagedTxAService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Services i SkifTest som test bean managed persistence
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifTestTxManagementServices implements ServicesListing {
    private static final List<Class<?>> services;

    static {
        List<Class<?>> modifiableList = new ArrayList<Class<?>>();
        
        modifiableList.add(BeanManagedTxAService.class);
        modifiableList.add(ContainerManagedTxAService.class);
        modifiableList.add(ContainerManagedTxCMTCascadeService.class);
        services = Collections.unmodifiableList(modifiableList);
     }

    @Override
    public List<Class<?>> getServices() {
        return services;
    }
}
