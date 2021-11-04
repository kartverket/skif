package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.skiftest.service.txbmt.BeanManagedTxAService;
import no.statkart.skif.skiftest.service.txcascade.ContainerManagedTxCMTCascadeService;
import no.statkart.skif.skiftest.service.txcmt.ContainerManagedTxAService;
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
        @EJB(name = "ejb/BeanManagedTxAServiceEJBBean", beanInterface = BeanManagedTxAService.class),
        @EJB(name = "ejb/ContainerManagedTxAServiceEJBBean", beanInterface = ContainerManagedTxAService.class),
        @EJB(name = "ejb/ContainerManagedTxCMTCascadeService", beanInterface = ContainerManagedTxCMTCascadeService.class)
})
@ConditionalOnProperty("ExcludeWhenRunningOnSpring") // A trick to prevent Spring from picking up this ServletContextListener
public class SkifTestTxManagementServicesEJBs extends EJBRegistration {
    public SkifTestTxManagementServicesEJBs() {
        super(new SkifTestTxManagementServices());
    }
}
