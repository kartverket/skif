package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest.service.kodeliste.KodelisteService;
import no.statkart.skif.storetest.service.store.StoreService;
import no.statkart.skif.storetest.service.storetest1.StoreTest1Service;

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
        @EJB(name = "ejb/KodelisteServiceEJBBean", beanInterface = KodelisteService.class),
        @EJB(name = "ejb/StoreTerviceEJBBean", beanInterface = StoreService.class)
})
public class StoreTestStoreServicesEJBs extends EJBRegistration {
    public StoreTestStoreServicesEJBs() {
        super(new StoreTestStoreServices());
    }
}
