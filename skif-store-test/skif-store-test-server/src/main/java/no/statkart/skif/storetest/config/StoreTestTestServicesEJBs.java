package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest.service.test.TestService;

import javax.ejb.EJB;
import javax.ejb.EJBs;

/**
 * EJB'er må listes her for at skif skal kunne finne frem til dem.
 *
 * @author Tor Egil R. Strand
 * @see no.statkart.skif.service.ejb.EJBRegistration
 * @since 2.1
 */
@EJBs({
        @EJB(name = "ejb/TestServiceEJBBean", beanInterface = TestService.class)
})
public class StoreTestTestServicesEJBs extends EJBRegistration {
    public StoreTestTestServicesEJBs() {
        super(new StoreTestTestServices());
    }
}
