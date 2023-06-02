package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.skiftest.service.test2.Test2Service;
import no.statkart.skif.skiftest.service.test3.Test3Service;

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
        @EJB(name = "ejb/Test2ServiceEJBBean", beanInterface = Test2Service.class),
        @EJB(name = "ejb/Test3ServiceEJBBean", beanInterface = Test3Service.class)
})
public class SkifTestGroup2ServicesEJBs extends EJBRegistration {
    public SkifTestGroup2ServicesEJBs() {
        super(new SkifTestGroup2Services());
    }
}
