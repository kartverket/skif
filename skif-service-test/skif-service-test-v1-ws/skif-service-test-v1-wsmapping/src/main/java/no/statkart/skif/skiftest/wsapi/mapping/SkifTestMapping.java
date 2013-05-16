package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.skiftest.domain.*;
import no.statkart.skif.skiftest.wsapi.domain.AList;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface SkifTestMapping extends Mapping {
    public no.statkart.skif.skiftest.wsapi.domain.A d2w(A a);
    public A w2d(no.statkart.skif.skiftest.wsapi.domain.A a);

    public no.statkart.skif.skiftest.wsapi.domain.B d2w(B b);
    public B w2d(no.statkart.skif.skiftest.wsapi.domain.B b);

    public no.statkart.skif.skiftest.wsapi.domain.M d2w(M m);
    public M w2d(no.statkart.skif.skiftest.wsapi.domain.M m);

    public AList d2w(Collection source);
    public <T extends Collection> T w2d(AList source, Class<T> target);
}