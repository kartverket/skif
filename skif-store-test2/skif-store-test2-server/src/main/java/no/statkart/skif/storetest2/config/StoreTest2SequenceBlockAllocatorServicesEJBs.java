package no.statkart.skif.storetest2.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest2.service.id.SequenceBlockAllocatorService;

import javax.ejb.EJB;
import javax.ejb.EJBs;

/**
 * EJB'er må listes her for at skif skal kunne finne frem til dem.
 *
 * @author Henrik Fredholm
 * @see no.statkart.skif.service.ejb.EJBRegistration
 * @since 2.2.0
 */
@EJBs({
        @EJB(name = "ejb/SequenceBlockAllocatorServiceEJBBean", beanInterface = SequenceBlockAllocatorService.class)
})
public class StoreTest2SequenceBlockAllocatorServicesEJBs extends EJBRegistration {
    public StoreTest2SequenceBlockAllocatorServicesEJBs() {
        super(new StoreTest2SequenceBlockAllocatorServices());
    }
}
