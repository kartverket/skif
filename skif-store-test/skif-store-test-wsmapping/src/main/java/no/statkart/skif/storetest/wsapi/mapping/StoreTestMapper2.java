package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.AbstractMapper;
import no.statkart.skif.mapper.DefaultObjectFactory;
import no.statkart.skif.mapper.ObjectFactory;
import no.statkart.skif.mapper.WsapiListTypeMapper;
import no.statkart.skif.storetest.domain.A;
import no.statkart.skif.storetest.domain.B;
import no.statkart.skif.storetest.wsapi.domain.AList;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class StoreTestMapper2 extends StoreTestMapper {
    public StoreTestMapper2() {
        super(StoreTestMapping2.class);
    }

    @Override
    public StoreTestMapping2 getMapping() {
        return (StoreTestMapping2) super.getMapping();

    }
}