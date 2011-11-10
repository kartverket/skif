package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.skiftest.service.testa.AService;
import no.statkart.skif.skiftest.service.testb.BService;
import no.statkart.skif.skiftest.service.testc.CService;
import no.statkart.skif.skiftest.service.testd.DService;
import no.statkart.skif.skiftest.service.testex.TestExService;

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
        @EJB(name = "ejb/TestExServiceEJBBean", beanInterface = TestExService.class)
})
public class SkifTestGroupExServicesEJBs extends EJBRegistration {
    public SkifTestGroupExServicesEJBs() {
        super(new SkifTestGroupExServices());
    }
}
