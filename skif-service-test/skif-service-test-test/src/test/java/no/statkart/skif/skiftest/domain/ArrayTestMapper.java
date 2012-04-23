package no.statkart.skif.skiftest.domain;

import no.statkart.skif.mapper.*;

/**
 * @author Roar Ingebrigtsen
 * @since 3.0
 */
public class ArrayTestMapper extends AbstractMapper{

    public ArrayTestMapper(){
        this(Mapping.class);
    }

    public ArrayTestMapper(Class<? extends Mapping> mappingClass) {
        this(mappingClass, new DefaultObjectFactory(), new DefaultObjectFactory(), true);
    }

    public ArrayTestMapper(Class<? extends Mapping> mappingClass, ObjectFactory wsapiObjectFactory, ObjectFactory domainObjectFactory, boolean mergeMapping) {
        super(mappingClass, wsapiObjectFactory, domainObjectFactory, mergeMapping);

        DefaultTypeMapper dtm = new DefaultTypeMapper();
        dtm.addPackageMapping("no.statkart.skif.skiftest.domain.array2", "no.statkart.skif.skiftest.domain.array1");

        setDefaultMapper(dtm);

        useIdentityMapping(String.class);
        useIdentityMapping(Integer.class);
        useIdentityMapping(Class.class);

    }
}
