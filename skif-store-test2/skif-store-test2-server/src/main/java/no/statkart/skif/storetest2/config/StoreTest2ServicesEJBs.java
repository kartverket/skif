package no.statkart.skif.storetest2.config;

import no.statkart.skif.service.ejb.EJBRegistration;

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
})
public class StoreTest2ServicesEJBs extends EJBRegistration {
    public StoreTest2ServicesEJBs() {
        super(new StoreTest2Services());
    }
}
