package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2AAWithEntityComponentFinderService;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AAFinderService;
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
        @EJB(name = "ejb/X1AAFinderServiceEJBBean", beanInterface = X1AAFinderService.class),
        @EJB(name = "ejb/X2AAWithEntityComponentFinderServiceEJBBean", beanInterface = X2AAWithEntityComponentFinderService.class)
})
@ConditionalOnProperty("ExcludeWhenRunningOnSpring") // A trick to prevent Spring from picking up this ServletContextListener
public class StoreTestDomainFinderServicesEJBs extends EJBRegistration {
    public StoreTestDomainFinderServicesEJBs() {
        super(new StoreTestDomainFinderServices());
    }
}
