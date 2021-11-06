package no.statkart.skif.storetest.config;

import no.statkart.skif.service.ejb.EJBRegistrationSpring;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2AAWithEntityComponentFinderService;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2AAWithEntityComponentFinderServiceEJBBean;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AAFinderService;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AAFinderServiceEJBBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * EJB'er må listes her for at skif skal kunne finne frem til dem.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Configuration
public class StoreTestDomainFinderServicesEJBsSpring extends EJBRegistrationSpring {
    public StoreTestDomainFinderServicesEJBsSpring() {
        super(new StoreTestDomainFinderServices());
    }

    @Bean
    public X1AAFinderService gX1AAFinderService() {
        return new X1AAFinderServiceEJBBean();
    }

    @Bean
    public X2AAWithEntityComponentFinderService getX2AAWithEntityComponentFinderService() {
        return new X2AAWithEntityComponentFinderServiceEJBBean();
    }

}
