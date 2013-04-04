package no.statkart.skif.storetest2.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest2.service.test.TestdataService;

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
        @EJB(name = "ejb/TestServiceEJBBean", beanInterface = TestdataService.class)
})
public class StoreTest2TestServicesEJBs extends EJBRegistration {
    public StoreTest2TestServicesEJBs() {
        super(new StoreTest2TestServices());
    }
}
