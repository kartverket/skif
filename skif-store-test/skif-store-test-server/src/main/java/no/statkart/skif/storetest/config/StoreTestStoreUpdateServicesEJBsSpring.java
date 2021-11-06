package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistrationSpring;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import no.statkart.skif.storetest.service.store.StoreUpdateServiceEJBBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@Configuration
public class StoreTestStoreUpdateServicesEJBsSpring extends EJBRegistrationSpring {
    public StoreTestStoreUpdateServicesEJBsSpring() {
        super(new StoreTestStoreUpdateServices());
    }

    @Bean
    public StoreUpdateService geStoreUpdateService() {
        return new StoreUpdateServiceEJBBean();
    }
}
