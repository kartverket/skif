package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest.service.storetest1.StoreTest1Service;
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
        @EJB(name = "ejb/StoreTest1ServiceEJBBean", beanInterface = StoreTest1Service.class)
})
@ConditionalOnProperty("ExcludeWhenRunningOnSpring") // A trick to prevent Spring from picking up this ServletContextListener
public class StoreTestGroup1ServicesEJBs extends EJBRegistration {
    public StoreTestGroup1ServicesEJBs() {
        super(new StoreTestGroup1Services());
    }
}
