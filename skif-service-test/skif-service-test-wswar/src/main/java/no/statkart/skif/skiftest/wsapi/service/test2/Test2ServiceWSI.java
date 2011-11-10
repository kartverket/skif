package no.statkart.skif.skiftest.wsapi.service.test2;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.skiftest.wsapi.domain.A;
import no.statkart.skif.skiftest.wsapi.domain.B;
import no.statkart.skif.skiftest.wsapi.domain.SkifTestContext;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface Test2ServiceWSI extends ServiceWSI {
    public B a2B(@WebParam(name = "a") A a, @WebParam(name="skifTestContext")SkifTestContext skifTestContext);
}
