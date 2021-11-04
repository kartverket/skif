package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ejb.EJBRegistrationSpring;
import no.statkart.skif.skiftest.service.test1.Test1Service;
import no.statkart.skif.skiftest.service.test1.Test1ServiceEJBBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkifTestGroup1ServicesEJBsSpring extends EJBRegistrationSpring {
    public SkifTestGroup1ServicesEJBsSpring() {
        super(new SkifTestGroup1Services());
    }

    @Bean
    public Test1Service getTest1Service() {
        return new Test1ServiceEJBBean();
    }
}
