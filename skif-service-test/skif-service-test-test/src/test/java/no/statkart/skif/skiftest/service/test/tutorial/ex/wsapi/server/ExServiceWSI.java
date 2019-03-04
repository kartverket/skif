package no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.server;

import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.A;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.B;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.C;

/**
 * Dette interface brukes bare på server. Er interface for WSBean klasse og brukes i Web Service Proxy Handler
 * kjede på server.
 */
public interface ExServiceWSI {
    C doEx(A a, B b);
}
