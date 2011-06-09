package no.statkart.skif.skiftest.wsapi.service.test1;

import no.statkart.skif.service.ws.ServiceWSI;

import javax.jws.WebParam;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public interface Test1ServiceWSI extends ServiceWSI {
    public String helloWorld(@WebParam(name = "message") String message);
}
