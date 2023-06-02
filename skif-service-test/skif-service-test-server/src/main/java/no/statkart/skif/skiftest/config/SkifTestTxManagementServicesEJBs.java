package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.skiftest.service.txbmt.BeanManagedTxAService;
import no.statkart.skif.skiftest.service.txcmt.ContainerManagedTxAService;
import no.statkart.skif.skiftest.service.txcascade.ContainerManagedTxCMTCascadeService;

import jakarta.ejb.EJB;
import jakarta.ejb.EJBs;

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
public class SkifTestTxManagementServicesEJBs extends EJBRegistration {
    public SkifTestTxManagementServicesEJBs() {
        super(new SkifTestTxManagementServices());
    }
}
