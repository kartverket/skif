package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ejb.EJBRegistrationSpring;
import no.statkart.skif.skiftest.service.test2.Test2Service;
import no.statkart.skif.skiftest.service.test2.Test2ServiceEJBBean;
import no.statkart.skif.skiftest.service.test3.Test3Service;
import no.statkart.skif.skiftest.service.test3.Test3ServiceEJBBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkifTestGroup2ServicesEJBsSpring extends EJBRegistrationSpring {
    public SkifTestGroup2ServicesEJBsSpring() {
         super(new SkifTestGroup2Services());
    }

    @Bean
    public Test2Service getTest2Service() {
        return new Test2ServiceEJBBean();
    }

    @Bean
    public Test3Service getTest3Service() {
        return new Test3ServiceEJBBean();
    }

}
