package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistrationSpring;
import no.statkart.skif.storetest.service.test.TestdataService;
import no.statkart.skif.storetest.service.test.TestdataServiceEJBBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * EJB'er må listes her for at skif skal kunne finne frem til dem.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Configuration
public class StoreTestTestServicesEJBsSpring extends EJBRegistrationSpring {
    public StoreTestTestServicesEJBsSpring() {
        super(new StoreTestTestServices());
    }

    @Bean
    public TestdataService getTestdataService() {
        return new TestdataServiceEJBBean();
    }
}
