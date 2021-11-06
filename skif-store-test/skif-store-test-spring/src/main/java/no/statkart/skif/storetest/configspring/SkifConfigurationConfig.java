package no.statkart.skif.storetest.configspring;

import no.statkart.skif.config.SkifConfiguration;
import no.statkart.skif.config.SkifServerConfiguration;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkifConfigurationConfig {
    @Bean
    public SkifConfiguration skifServerConfiguration(ApplicationContext applicationContext) {
        // TODO: Integrate SKIF configuration with Spring. Should we just get properties from Spring application.properties?
        SkifServerConfiguration configuration = new SkifServerConfiguration();
        configuration.addProperty("spring.application.context", applicationContext);
        return configuration;
    }

}
