package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.mapper.AbstractMapper;
import no.statkart.skif.mapper.CollectionMapperFactory;
import no.statkart.skif.mapper.DefaultTypeMapperFactory;
import no.statkart.skif.mapper.IdentityTypeMapperFactory;
import no.statkart.skif.mapper.MappingResolver;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifDefaultTypeMapperTestMapper extends AbstractMapper<SkifTestMapping> {
    public SkifDefaultTypeMapperTestMapper() {
        super(SkifTestMapping.class);

        MappingResolver mappingResolver = new MappingResolver();
        mappingResolver.addPackageMapping("no.statkart.skif.skiftest.wsapi.domain", "no.statkart.skif.skiftest.domain");
        setMappingResolver(mappingResolver);

        // Klasser hvor objekter skal mappes til seg selv
        addMapperFactory(new IdentityTypeMapperFactory().useIdentityMappingForBasicTypes());

        addMapperFactory(new CollectionMapperFactory());

        addMapperFactory(new DefaultTypeMapperFactory());
    }
}
