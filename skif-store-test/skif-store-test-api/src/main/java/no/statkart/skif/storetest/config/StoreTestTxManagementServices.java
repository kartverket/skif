package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ServicesListing;
import no.statkart.skif.storetest.service.txmanagement.BeanManagedTxAService;
import no.statkart.skif.storetest.service.txmanagement.ContainerManagedTxAService;
import no.statkart.skif.storetest.service.txmanagement.ContainerManagedTxCMTCascadeService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Services i SkifTest som test bean managed persistence
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestTxManagementServices implements ServicesListing {
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
