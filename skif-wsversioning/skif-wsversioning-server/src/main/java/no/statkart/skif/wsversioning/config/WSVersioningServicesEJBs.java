package no.statkart.skif.wsversioning.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.wsversioning.service.StoreService;
import no.statkart.skif.wsversioning.service.VegService;

import javax.ejb.EJB;
import javax.ejb.EJBs;

/**
 * EJB-er må listes her for at skif skal kunne finne frem til dem.
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @see no.statkart.skif.service.ejb.EJBRegistration
 * @since 2.4.0
 */
@EJBs({
        @EJB(name = "ejb/StoreServiceEJBBean", beanInterface = StoreService.class),
        @EJB(name = "ejb/VegServiceEJBBean", beanInterface = VegService.class)
})
public class WSVersioningServicesEJBs extends EJBRegistration {
    public WSVersioningServicesEJBs() {
        super(new WSVersioningServices());
    }
}