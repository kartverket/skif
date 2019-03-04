package no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.server;

import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.A;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.B;
import no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.C;

/**
 * Denne klasse brukes bare for å teste ut Guice bining av ExServiceImpl
 */
public class ExServiceWSIDummyImpl implements ExServiceWSI {
    public C doEx(A a, B b) { return new C(a.getX() + b.getY(),a.getX()/b.getY());}
}
