package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ejb.EJBRegistrationSpring;
import no.statkart.skif.skiftest.service.testex.TestExService;
import no.statkart.skif.skiftest.service.testex.TestExServiceEJBBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkifTestGroupExServicesEJBsSpring extends EJBRegistrationSpring {
    public SkifTestGroupExServicesEJBsSpring() {
        super(new SkifTestGroupExServices());
    }

    @Bean
    public TestExService getTestExService() {
        return new TestExServiceEJBBean();
    }

}
