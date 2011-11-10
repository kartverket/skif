package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest.service.id.SequenceBlockAllocatorService;
import no.statkart.skif.storetest.service.txmanagement.BeanManagedTxAService;
import no.statkart.skif.storetest.service.txmanagement.ContainerManagedTxAService;
import no.statkart.skif.storetest.service.txmanagement.ContainerManagedTxCMTCascadeService;

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
        @EJB(name = "ejb/SequenceBlockAllocatorServiceEJBBean", beanInterface = SequenceBlockAllocatorService.class)
})
public class StoreTestSequenceBlockAllocatorServicesEJBs extends EJBRegistration {
    public StoreTestSequenceBlockAllocatorServicesEJBs() {
        super(new StoreTestSequenceBlockAllocatorServices());
    }
}
