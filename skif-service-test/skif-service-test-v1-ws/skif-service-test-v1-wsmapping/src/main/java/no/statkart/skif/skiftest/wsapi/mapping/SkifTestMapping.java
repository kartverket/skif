package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.skiftest.domain.A;
import no.statkart.skif.skiftest.domain.B;
import no.statkart.skif.skiftest.domain.C;
import no.statkart.skif.skiftest.domain.M;
import no.statkart.skif.skiftest.wsapi.domain.AList;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface SkifTestMapping extends Mapping {

    no.statkart.skif.skiftest.wsapi.domain.A d2w(A a);
    A w2d(no.statkart.skif.skiftest.wsapi.domain.A a);

    no.statkart.skif.skiftest.wsapi.domain.B d2w(B b);
    B w2d(no.statkart.skif.skiftest.wsapi.domain.B b);

    no.statkart.skif.skiftest.wsapi.domain.C d2w(C c);
    C w2d(no.statkart.skif.skiftest.wsapi.domain.C c);

    no.statkart.skif.skiftest.wsapi.domain.M d2w(M m);
    M w2d(no.statkart.skif.skiftest.wsapi.domain.M m);

    AList d2w(Collection source);
    <T extends Collection> T w2d(AList source, Class<T> target);

}