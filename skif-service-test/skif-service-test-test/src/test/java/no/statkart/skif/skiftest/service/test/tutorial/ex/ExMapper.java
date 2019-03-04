package no.statkart.skif.skiftest.service.test.tutorial.ex;

import no.statkart.skif.mapper.AbstractMapper;
import no.statkart.skif.mapper.DefaultTypeMapperFactory;
import no.statkart.skif.mapper.IdentityTypeMapperFactory;
import no.statkart.skif.mapper.MappingResolver;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ExMapper extends AbstractMapper<ExMapping> {

    public ExMapper() {
        super(ExMapping.class);

        MappingResolver mappingResolver = new MappingResolver();
        mappingResolver.addPackageMapping("no.statkart.skif.skiftest.service.test.tutorial.ex.wsapi", "no.statkart.skif.skiftest.service.test.tutorial.ex.api");
        setMappingResolver(mappingResolver);

        // Klasser hvor objekter skal mappes til seg selv
        addMapperFactory(new IdentityTypeMapperFactory().useIdentityMappingForBasicTypes());

        addMapperFactory(new DefaultTypeMapperFactory());

        // Objekter
//        addMapper(new ATypeMapper<>(no.statkart.skif.skiftest.wsapi.domain.A.class, A.class));
//        addMapper(new BTypeMapper<>(no.statkart.skif.skiftest.wsapi.domain.B.class, B.class));

        // Lister
//        addMapper(new WsapiListTypeMapper<>(no.statkart.skif.skiftest.wsapi.domain.AList.class, no.statkart.skif.skiftest.wsapi.domain.A.class, Set.class, A.class));
//        addMapper(new WsapiListTypeMapper<>(no.statkart.skif.skiftest.wsapi.domain.StringList.class, String.class, List.class, String.class));
    }
}