package no.statkart.skif.skiftest.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({SkifTestTxManagementServicesEJBsSpring.class})
public class SkifTestTxManagementServerInjectorConfigSpring {

    // Fremtvinger tidlig initialisering av configurationer som legger inn Services i EJBLookupHelper og som
    // denne injector er avhengig av er registrert der.
    SkifTestTxManagementServerInjectorConfigSpring(
            SkifTestTxManagementServicesEJBsSpring skifTestTxManagementServicesEJBsSpring
    ) {}

    @Bean
    public SkifTestTxManagementServerInjector getSkifTestTxManagementServerInjectorEJBBeanSpring() {
        return new SkifTestTxManagementServerInjectorEJBBeanSpring();
    }
}
