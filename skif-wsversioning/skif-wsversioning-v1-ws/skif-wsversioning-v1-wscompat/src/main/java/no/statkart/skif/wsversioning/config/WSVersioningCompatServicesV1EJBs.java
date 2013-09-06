package no.statkart.skif.wsversioning.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.wsversioning.service.GateService;

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
        @EJB(name = "ejb/GateServiceEJBBean", beanInterface = GateService.class)
})
public class WSVersioningCompatServicesV1EJBs extends EJBRegistration {
    public WSVersioningCompatServicesV1EJBs() {
        super(new WSVersioningCompatServicesV1());
    }
}
