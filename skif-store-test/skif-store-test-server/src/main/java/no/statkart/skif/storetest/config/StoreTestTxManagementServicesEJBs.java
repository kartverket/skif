package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.storetest.service.txbmt.BeanManagedTxAService;
import no.statkart.skif.storetest.service.txcmt.ContainerManagedTxAService;
import no.statkart.skif.storetest.service.txcascade.ContainerManagedTxCMTCascadeService;
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
public class StoreTestTxManagementServicesEJBs extends EJBRegistration {
    public StoreTestTxManagementServicesEJBs() {
        super(new StoreTestTxManagementServices());
    }
}
