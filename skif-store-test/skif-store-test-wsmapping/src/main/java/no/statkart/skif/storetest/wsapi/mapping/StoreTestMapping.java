package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.storetest.domain.*;
import no.statkart.skif.storetest.wsapi.domain.AList;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public interface StoreTestMapping extends Mapping {
    public AList d2w(Collection source, AList target);
    public <T extends Collection> T w2d(AList source, T target);
}