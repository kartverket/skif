package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.mapper.AbstractMapper;
import no.statkart.skif.mapper.DefaultObjectFactory;
import no.statkart.skif.mapper.ObjectFactory;
import no.statkart.skif.mapper.WsapiListTypeMapper;
import no.statkart.skif.skiftest.domain.A;
import no.statkart.skif.skiftest.domain.B;
import no.statkart.skif.skiftest.wsapi.domain.AList;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class SkifTestMapper2 extends SkifTestMapper {
    public SkifTestMapper2() {
        super(SkifTestMapping2.class);
    }

    @Override
    public SkifTestMapping2 getMapping() {
        return (SkifTestMapping2) super.getMapping();

    }
}