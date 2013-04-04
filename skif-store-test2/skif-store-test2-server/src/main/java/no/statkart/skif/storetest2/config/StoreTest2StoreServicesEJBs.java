package no.statkart.skif.storetest2.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest2.service.locker.DBLockerInTransactionService;
import no.statkart.skif.storetest2.service.locker.DBLockerService;
import no.statkart.skif.storetest2.service.store.StoreService;

import javax.ejb.EJB;
import javax.ejb.EJBs;

/**
 * EJB-er må listes her for at skif skal kunne finne frem til dem.
 *
 * @author Tor Egil R. Strand
 * @see no.statkart.skif.service.ejb.EJBRegistration
 * @since 2.2.0
 */
@EJBs({
        @EJB(name = "ejb/StoreServiceEJBBean", beanInterface = StoreService.class),
        @EJB(name = "ejb/DBLockerServiceEJBBean", beanInterface = DBLockerService.class),
        @EJB(name = "ejb/DBLockerInTransactionServiceEJBBean", beanInterface = DBLockerInTransactionService.class)
})
public class StoreTest2StoreServicesEJBs extends EJBRegistration {
    public StoreTest2StoreServicesEJBs() {
        super(new StoreTest2StoreServices());
    }
}
