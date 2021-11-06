package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistrationSpring;
import no.statkart.skif.storetest.service.id.SequenceBlockAllocatorService;
import no.statkart.skif.storetest.service.id.SequenceBlockAllocatorServiceEJBBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * EJB'er må listes her for at skif skal kunne finne frem til dem.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Configuration
public class StoreTestSequenceBlockAllocatorServicesEJBsSpring extends EJBRegistrationSpring {
    public StoreTestSequenceBlockAllocatorServicesEJBsSpring() {
        super(new StoreTestSequenceBlockAllocatorServices());
    }

    @Bean
    public SequenceBlockAllocatorService getSequenceBlockAllocatorService() {
        return new SequenceBlockAllocatorServiceEJBBean();
    }
}
