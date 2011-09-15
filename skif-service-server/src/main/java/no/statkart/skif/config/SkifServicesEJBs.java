package no.statkart.skif.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.service.locker.DBLockerService;

import javax.ejb.EJB;
import javax.ejb.EJBs;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
@EJBs({
        @EJB(name = "ejb/DBLockerServiceEJBBean", beanInterface = DBLockerService.class)
})
public class SkifServicesEJBs extends EJBRegistration{
    public SkifServicesEJBs() {
        super(new SkifServices());
    }
}
