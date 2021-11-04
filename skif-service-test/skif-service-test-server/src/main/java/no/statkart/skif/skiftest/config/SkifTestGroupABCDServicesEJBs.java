package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.skiftest.service.testa.AService;
import no.statkart.skif.skiftest.service.testb.BService;
import no.statkart.skif.skiftest.service.testc.CService;
import no.statkart.skif.skiftest.service.testd.DService;
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
        @EJB(name = "ejb/AServiceEJBBean", beanInterface = AService.class),
        @EJB(name = "ejb/BServiceEJBBean", beanInterface = BService.class),
        @EJB(name = "ejb/CServiceEJBBean", beanInterface = CService.class),
        @EJB(name = "ejb/DServiceEJBBean", beanInterface = DService.class)
})
@ConditionalOnProperty("ExcludeWhenRunningOnSpring") // A trick to prevent Spring from picking up this ServletContextListener
public class SkifTestGroupABCDServicesEJBs extends EJBRegistration {
    public SkifTestGroupABCDServicesEJBs() {
        super(new SkifTestGroupABCDServices());
    }
}
