package no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.klient;

import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.A;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.B;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.C;

/**
 * JAX-WS interface på klient som normalt genereres fra WSDL.
 * <p>
 * Dette er ikke en ekte implementasjon og brukes bare i SKIFs tutorial eksempel som demonstrere overordnet konsept
 * for SKIFs Service Rammeverk
 */
public interface ExService {
    C doEx(A a, B b);
}
