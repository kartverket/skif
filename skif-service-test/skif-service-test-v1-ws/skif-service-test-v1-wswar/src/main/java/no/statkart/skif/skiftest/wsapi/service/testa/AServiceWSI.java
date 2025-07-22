package no.statkart.skif.skiftest.wsapi.service.testa;

import jakarta.jws.WebParam;
import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.skiftest.wsapi.domain.StringList;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface AServiceWSI extends ServiceWSI {

    String m1(@WebParam(name = "callSpec") StringList callSpec);

    String m2(@WebParam(name = "callSpec") StringList callSpec);

    String m3(@WebParam(name = "callSpec") StringList callSpec);

}
