package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest.config.StoreTestGroup1Services;
import no.statkart.skif.storetest.service.storetest1.StoreTest1Service;

import jakarta.ejb.EJB;
import jakarta.ejb.EJBs;

/**
 * EJB'er må listes her for at skif skal kunne finne frem til dem.
 *
 * @author Henrik Fredholm
 * @see no.statkart.skif.service.ejb.EJBRegistration
 * @since 2.0
 */
@EJBs({
        @EJB(name = "ejb/StoreTest1ServiceEJBBean", beanInterface = StoreTest1Service.class)
})
public class StoreTestGroup1ServicesEJBs extends EJBRegistration {
    public StoreTestGroup1ServicesEJBs() {
        super(new StoreTestGroup1Services());
    }
}
