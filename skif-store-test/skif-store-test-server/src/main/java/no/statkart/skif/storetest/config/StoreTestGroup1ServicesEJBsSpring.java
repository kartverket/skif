package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistrationSpring;
import no.statkart.skif.storetest.service.storetest1.StoreTest1Service;
import no.statkart.skif.storetest.service.storetest1.StoreTest1ServiceEJBBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * EJB'er må listes her for at skif skal kunne finne frem til dem.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Configuration
public class StoreTestGroup1ServicesEJBsSpring extends EJBRegistrationSpring {
    public StoreTestGroup1ServicesEJBsSpring() {
        super(new StoreTestGroup1Services());
    }

    @Bean
    public StoreTest1Service getTest1Service() {
        return new StoreTest1ServiceEJBBean();
    }
}
