package no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.server;

import com.google.inject.Inject;
import no.statkart.skif.service.annotation.WSServiceChain;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.A;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.B;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.C;

/**
 * JAX-WS Bean for JAX-WS Web Service. Serverklasse som håndtere Web Service kall
 *
 * Dette er ikke en ekte implementasjon og brukes bare i SKIFs tutorial eksempel som demonstrere overordnet konsept
 * for SKIFs Service Rammeverk
 */
public class ExServiceWSBean implements ExServiceWSI {
    private final ExServiceWSI wsServiceChain;

    @Inject
    public ExServiceWSBean(@WSServiceChain ExServiceWSI wsServiceChain) {
        this.wsServiceChain = wsServiceChain;
    }

    @Override
    public C doEx(A a, B b) {
        return wsServiceChain.doEx(a, b);
    }
}
