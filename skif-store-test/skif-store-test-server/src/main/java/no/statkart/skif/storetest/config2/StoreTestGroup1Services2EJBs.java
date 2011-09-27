package no.statkart.skif.storetest.config2;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest.config.StoreTestGroup1Services;
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
        @EJB(name = "ejb/StoreTest1Service2EJBBean", beanInterface = StoreTest1Service.class)
})
public class StoreTestGroup1Services2EJBs extends EJBRegistration {
    public StoreTestGroup1Services2EJBs() {
        super(new StoreTestGroup1Services2());
    }
}
