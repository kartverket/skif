package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.mapper.*;

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