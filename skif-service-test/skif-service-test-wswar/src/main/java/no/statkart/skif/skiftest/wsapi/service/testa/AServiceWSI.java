package no.statkart.skif.skiftest.wsapi.service.testa;

import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.skiftest.wsapi.domain.StringList;

import javax.jws.WebParam;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public interface AServiceWSI extends ServiceWSI {
    public String m1(@WebParam(name = "callSpec") StringList callSpec);
    public String m2(@WebParam(name = "callSpec") StringList callSpec);
    public String m3(@WebParam(name = "callSpec") StringList callSpec);
}
