package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ejb.EJBRegistrationSpring;
import no.statkart.skif.skiftest.service.txbmt.BeanManagedTxAService;
import no.statkart.skif.skiftest.service.txbmt.BeanManagedTxAServiceEJBBean;
import no.statkart.skif.skiftest.service.txcascade.ContainerManagedTxCMTCascadeService;
import no.statkart.skif.skiftest.service.txcascade.ContainerManagedTxCMTCascadeServiceEJBBean;
import no.statkart.skif.skiftest.service.txcmt.ContainerManagedTxAService;
import no.statkart.skif.skiftest.service.txcmt.ContainerManagedTxAServiceEJBBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkifTestTxManagementServicesEJBsSpring extends EJBRegistrationSpring {
    public SkifTestTxManagementServicesEJBsSpring() {
        super(new SkifTestTxManagementServices());
    }

    @Bean
    public BeanManagedTxAService getBeanManagedTxAService() {
        return new BeanManagedTxAServiceEJBBean();
    }

    @Bean
    public ContainerManagedTxAService getContainerManagedTxAServiceService() {
        return new ContainerManagedTxAServiceEJBBean();
    }

    @Bean
    public ContainerManagedTxCMTCascadeService getContainerManagedTxCMTCascadeService() {
        return new ContainerManagedTxCMTCascadeServiceEJBBean();
    }


}
