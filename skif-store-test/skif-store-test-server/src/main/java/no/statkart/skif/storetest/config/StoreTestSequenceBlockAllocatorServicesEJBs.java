package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest.service.id.SequenceBlockAllocatorService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

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
@ConditionalOnProperty("ExcludeWhenRunningOnSpring") // A trick to prevent Spring from picking up this ServletContextListener
public class StoreTestSequenceBlockAllocatorServicesEJBs extends EJBRegistration {
    public StoreTestSequenceBlockAllocatorServicesEJBs() {
        super(new StoreTestSequenceBlockAllocatorServices());
    }
}
