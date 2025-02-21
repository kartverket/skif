package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.mapper.AbstractMapper;
import no.statkart.skif.mapper.DefaultTypeMapperFactory;
import no.statkart.skif.mapper.IdentityTypeMapperFactory;
import no.statkart.skif.mapper.MappingResolver;
import no.statkart.skif.mapper.WsapiListTypeMapper;
import no.statkart.skif.skiftest.domain.A;
import no.statkart.skif.skiftest.domain.B;

import java.util.List;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifTestMapper<M extends SkifTestMapping> extends AbstractMapper<M> {
    public SkifTestMapper() {
        this((Class) SkifTestMapping.class); // Dette forutsetter at denne konstruktøren kun benyttes når denne klassen, og ikke en subklasse av den, benyttes rå
    }

    protected SkifTestMapper(Class<? extends M> mappingClass) {
        super(mappingClass);

        MappingResolver mappingResolver = new MappingResolver();
        mappingResolver.addPackageMapping("no.statkart.skif.skiftest.wsapi.domain", "no.statkart.skif.skiftest.domain");
        setMappingResolver(mappingResolver);

        // Klasser hvor objekter skal mappes til seg selv
        addMapperFactory(new IdentityTypeMapperFactory().useIdentityMappingForBasicTypes());

        addMapperFactory(new DefaultTypeMapperFactory());

        // Objekter
        addMapper(new ATypeMapper<>(no.statkart.skif.skiftest.wsapi.domain.A.class, A.class));
        addMapper(new BTypeMapper<>(no.statkart.skif.skiftest.wsapi.domain.B.class, B.class));

        // Lister
        addMapper(new WsapiListTypeMapper<>(no.statkart.skif.skiftest.wsapi.domain.AList.class, no.statkart.skif.skiftest.wsapi.domain.A.class, Set.class, A.class));
        addMapper(new WsapiListTypeMapper<>(no.statkart.skif.skiftest.wsapi.domain.StringList.class, String.class, List.class, String.class));
    }
}