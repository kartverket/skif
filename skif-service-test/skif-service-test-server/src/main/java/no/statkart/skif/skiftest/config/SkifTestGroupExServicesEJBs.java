package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.skiftest.service.testex.TestExService;
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
        @EJB(name = "ejb/TestExServiceEJBBean", beanInterface = TestExService.class)
})
@ConditionalOnProperty("ExcludeWhenRunningOnSpring") // A trick to prevent Spring from picking up this ServletContextListener
public class SkifTestGroupExServicesEJBs extends EJBRegistration {
    public SkifTestGroupExServicesEJBs() {
        super(new SkifTestGroupExServices());
    }
}
