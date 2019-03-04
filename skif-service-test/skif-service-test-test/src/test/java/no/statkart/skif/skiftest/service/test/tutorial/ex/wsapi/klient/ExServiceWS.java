package no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient;

import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.A;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.B;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.C;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.server.ExServiceWSBean;

/**
 * JAX-WS stub på klient som normalt genereres fra WSDL.
 * <p>
 * Dette er ikke en ekte implementasjon og brukes bare i SKIFs tutorial eksempel som demonstrere overordnet konsept
 * for SKIFs Service Rammeverk
 */
public class ExServiceWS implements ExService {
    private final ExServiceWSBean serverImplementation;

    public ExServiceWS(ExServiceWSBean serverImplementation) {
        this.serverImplementation = serverImplementation;
    }

    public C doEx(A a, B b) {
        return serverImplementation.doEx(a,b);
    }
}
