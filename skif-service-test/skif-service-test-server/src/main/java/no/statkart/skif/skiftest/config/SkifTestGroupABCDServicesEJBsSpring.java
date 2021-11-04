package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ejb.EJBRegistrationSpring;
import no.statkart.skif.skiftest.service.testa.AService;
import no.statkart.skif.skiftest.service.testa.AServiceEJBBean;
import no.statkart.skif.skiftest.service.testb.BService;
import no.statkart.skif.skiftest.service.testb.BServiceEJBBean;
import no.statkart.skif.skiftest.service.testc.CService;
import no.statkart.skif.skiftest.service.testc.CServiceEJBBean;
import no.statkart.skif.skiftest.service.testd.DService;
import no.statkart.skif.skiftest.service.testd.DServiceEJBBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SkifTestGroupABCDServicesEJBsSpring extends EJBRegistrationSpring {
    public SkifTestGroupABCDServicesEJBsSpring() {
        super(new SkifTestGroupABCDServices());
    }

    @Bean
    public AService getAService() {
        return new AServiceEJBBean();
    }

    @Bean
    public BService getBService() {
        return new BServiceEJBBean();
    }

    @Bean
    public CService getCService() {
        return new CServiceEJBBean();
    }

    @Bean
    public DService getDService() {
        return new DServiceEJBBean();
    }

}
