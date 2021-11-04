package no.statkart.skif.skiftest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkifTestServerInjectorConfigSpring {
    // Fremtvinger tidlig initialisering av konfigurationer som legger inn Services i EJBLookupHelper og som
    // denne injector er avhengig av er registrert der.
    @SuppressWarnings("unused")
    SkifTestServerInjectorConfigSpring(
            SkifTestGroup1ServicesEJBsSpring skifTestGroup1ServicesEJBsSpring,
            SkifTestGroup2ServicesEJBsSpring skifTestGroup2ServicesEJBsSpring,
            SkifTestGroupABCDServicesEJBsSpring skifTestGroupABCDServicesEJBsSpring,
            SkifTestGroupExServicesEJBsSpring skifTestGroupExServicesEJBsSpring
    ) {}

    @Bean
    public SkifTestServerInjector getSkifTestServerInjectorEJBBean() {
        return new SkifTestServerInjectorEJBBean();
    }
}
