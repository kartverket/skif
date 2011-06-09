package no.statkart.skif.skiftest.config;

import no.statkart.skif.service.ejb.EJBRegistration;
import no.statkart.skif.skiftest.service.test1.Test1Service;

import javax.ejb.EJB;
import javax.ejb.EJBs;

/**
 * EJB'er må listes her for at skif skal kunne finne frem til dem.
 *
 * @author Henrik Fredholm
 * @see no.statkart.skif.service.ejb.EJBRegistration
 * @since 1.1
 */
@EJBs({
        @EJB(name = "ejb/Test1ServiceEJBBean", beanInterface = Test1Service.class)
})
public class SkifTestGroup1ServicesEJBs extends EJBRegistration {
    public SkifTestGroup1ServicesEJBs() {
        super(new SkifTestGroup1Services());
    }
}
