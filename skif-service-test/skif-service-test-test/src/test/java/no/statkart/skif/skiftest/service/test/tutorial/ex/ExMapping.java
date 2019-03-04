package no.statkart.skif.skiftest.service.test.tutorial.ex;

import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.skiftest.service.test.tutorial.ex.api.A;
import no.statkart.skif.skiftest.service.test.tutorial.ex.api.B;
import no.statkart.skif.skiftest.service.test.tutorial.ex.api.C;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface ExMapping extends Mapping {

    no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.A d2w(A a);
    A w2d(no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.A a);

    no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.B d2w(B b);
    B w2d(no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.B b);

    no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.C d2w(C c);
    C w2d(no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi.C c);
}