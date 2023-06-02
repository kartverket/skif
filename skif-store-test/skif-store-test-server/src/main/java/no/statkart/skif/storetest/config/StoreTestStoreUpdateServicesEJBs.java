package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest.service.store.StoreUpdateService;

import jakarta.ejb.EJB;
import jakarta.ejb.EJBs;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@EJBs({
        @EJB(name = "ejb/StoreUpdateServiceEJBBean", beanInterface = StoreUpdateService.class)
})
public class StoreTestStoreUpdateServicesEJBs extends EJBRegistration {
    public StoreTestStoreUpdateServicesEJBs() {
        super(new StoreTestStoreUpdateServices());
    }
}
