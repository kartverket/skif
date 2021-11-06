package no.statkart.skif.storetest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StoreTestServerInjectorConfigSpring {

    // Fremtvinger tidlig initialisering av konfigurationer som legger inn Services i EJBLookupHelper og som
    // denne injector er avhengig av er registrert der.
    @SuppressWarnings("unused")
    public StoreTestServerInjectorConfigSpring(
            StoreTestDomainFinderServicesEJBsSpring storeTestDomainFinderServicesEJBsSpring,
            StoreTestGroup1ServicesEJBsSpring storeTestGroup1ServicesEJBsSpring,
            StoreTestSequenceBlockAllocatorServicesEJBsSpring storeTestSequenceBlockAllocatorServicesEJBsSpring,
            StoreTestStoreServicesEJBsSpring storeTestStoreServicesEJBsSpring,
            StoreTestStoreUpdateServicesEJBsSpring storeTestStoreUpdateServicesEJBsSpring,
            StoreTestTestServicesEJBsSpring storeTestTestServicesEJBsSpring,
            StoreTestTxManagementServicesEJBsSpring storeTestTxManagementServicesEJBsSpring
    ) {}

    @Bean
    public StoreTestServerInjector getStoreTestServerInjectorEJBBean() {
        return new StoreTestServerInjectorEJBBeanSpring();
    }
}
