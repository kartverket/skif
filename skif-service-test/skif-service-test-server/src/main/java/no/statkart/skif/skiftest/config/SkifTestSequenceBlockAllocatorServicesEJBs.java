package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.skiftest.service.id.SequenceBlockAllocatorService;

import javax.ejb.EJB;
import javax.ejb.EJBs;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@EJBs({
        @EJB(name = "ejb/SequenceBlockAllocatorServiceEJBBean", beanInterface = SequenceBlockAllocatorService.class)
})
public class SkifTestSequenceBlockAllocatorServicesEJBs extends EJBRegistration {
    public SkifTestSequenceBlockAllocatorServicesEJBs() {
        super(new SkifTestSequenceBlockAllocatorServices());
    }
}
