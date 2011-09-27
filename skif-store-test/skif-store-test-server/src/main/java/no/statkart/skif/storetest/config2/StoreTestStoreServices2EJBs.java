package no.statkart.skif.storetest.config2;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest.config.StoreTestStoreServices;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.service.store.StoreService;
import no.statkart.skif.storetest.service2.store2.StoreService2;

import javax.ejb.EJB;
import javax.ejb.EJBs;

/**
 * EJB'er må listes her for at skif skal kunne finne frem til dem.
 *
 * @author Henrik Fredholm
 * @see no.statkart.skif.service.ejb.EJBRegistration
 * @since 2.0
 */
@EJBs({
        @EJB(name = "ejb/KodelisteService2EJBBean", beanInterface = KodelisteService.class),
        @EJB(name = "ejb/StoreService2EJBBean", beanInterface = StoreService2.class)
})
public class StoreTestStoreServices2EJBs extends EJBRegistration {
    public StoreTestStoreServices2EJBs() {
        super(new StoreTestStoreServices2());
    }
}
