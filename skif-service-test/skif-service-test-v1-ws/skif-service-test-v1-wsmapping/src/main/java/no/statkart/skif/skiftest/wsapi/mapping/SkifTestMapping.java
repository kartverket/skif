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
    public AList d2w(Collection source, AList target);
    public <T extends Collection> T w2d(AList source, T target);
}