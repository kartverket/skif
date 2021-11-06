package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistrationSpring;
import no.statkart.skif.storetest.service.locker.DBLockerInTransactionService;
import no.statkart.skif.storetest.service.locker.DBLockerInTransactionServiceEJBBean;
import no.statkart.skif.storetest.service.locker.DBLockerService;
import no.statkart.skif.storetest.service.locker.DBLockerServiceEJBBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * EJB'er må listes her for at skif skal kunne finne frem til dem.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Configuration
public class StoreTestLocalServicesEJBsSpring extends EJBRegistrationSpring {
    public StoreTestLocalServicesEJBsSpring() {
        super(new StoreTestLocalServices());
    }

    @Bean
    public DBLockerService getDbLockerService() {
        return new DBLockerServiceEJBBean();
    }

    @Bean
    public DBLockerInTransactionService getDbLockerInTransactionService() {
        return new DBLockerInTransactionServiceEJBBean();
    }
}
