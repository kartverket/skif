package no.statkart.skif.skiftest.wsapi.service.testc;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.skiftest.wsapi.domain.StringList;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface CServiceWSI extends ServiceWSI {
    public String m1(@WebParam(name = "callSpec") StringList callSpec);
    public String m2(@WebParam(name = "callSpec") StringList callSpec);
    public String m3(@WebParam(name = "callSpec") StringList callSpec);
}
