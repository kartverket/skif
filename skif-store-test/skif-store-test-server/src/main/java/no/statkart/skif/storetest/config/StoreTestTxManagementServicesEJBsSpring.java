package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistrationSpring;
import no.statkart.skif.storetest.service.txbmt.BeanManagedTxAService;
import no.statkart.skif.storetest.service.txbmt.BeanManagedTxAServiceEJBBean;
import no.statkart.skif.storetest.service.txcascade.ContainerManagedTxCMTCascadeService;
import no.statkart.skif.storetest.service.txcascade.ContainerManagedTxCMTCascadeServiceEJBBean;
import no.statkart.skif.storetest.service.txcmt.ContainerManagedTxAService;
import no.statkart.skif.storetest.service.txcmt.ContainerManagedTxAServiceEJBBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * EJB'er må listes her for at skif skal kunne finne frem til dem.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Configuration
public class StoreTestTxManagementServicesEJBsSpring extends EJBRegistrationSpring {
    public StoreTestTxManagementServicesEJBsSpring() {
        super(new StoreTestTxManagementServices());
    }

    @Bean
    public BeanManagedTxAService getBeanManagedTxAService() {
        return new BeanManagedTxAServiceEJBBean();
    }

    @Bean
    public ContainerManagedTxAService getContainerManagedTxAService() {
        return new ContainerManagedTxAServiceEJBBean();
    }

    @Bean
    public ContainerManagedTxCMTCascadeService getContainerManagedTxCMTCascadeService() {
        return new ContainerManagedTxCMTCascadeServiceEJBBean();
    }
}
