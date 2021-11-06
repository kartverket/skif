package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest.service.store.StoreUpdateService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

import javax.ejb.EJB;
import javax.ejb.EJBs;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@EJBs({
        @EJB(name = "ejb/StoreUpdateServiceEJBBean", beanInterface = StoreUpdateService.class)
})
@ConditionalOnProperty("ExcludeWhenRunningOnSpring") // A trick to prevent Spring from picking up this ServletContextListener
public class StoreTestStoreUpdateServicesEJBs extends EJBRegistration {
    public StoreTestStoreUpdateServicesEJBs() {
        super(new StoreTestStoreUpdateServices());
    }
}
