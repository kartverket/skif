package no.statkart.skif.skiftest.domain;

import no.statkart.skif.mapper.*;

/**
 * @author Roar Ingebrigtsen
 * @since 3.0
 */
public class ArrayTestMapper extends AbstractMapper<ArrayTestMapping> {

    public ArrayTestMapper(){
        super(ArrayTestMapping.class);

        MappingResolver mappingResolver = new MappingResolver();
        mappingResolver.addPackageMapping("no.statkart.skif.skiftest.domain.array2", "no.statkart.skif.skiftest.domain.array1");
        setMappingResolver(mappingResolver);

        addMapperFactory(new IdentityTypeMapperFactory().useIdentityMapping(String.class, Integer.TYPE, Class.class));
        addMapperFactory(new DefaultTypeMapperFactory());
    }
}
