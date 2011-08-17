package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.mapper.*;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class SkifDefaultTypeMapperTestMapper extends AbstractMapper {
    Class<? extends Mapping> mappingClass;

   public SkifDefaultTypeMapperTestMapper() {
        this(SkifTestMapping.class, new DefaultObjectFactory(), new DefaultObjectFactory());
    }

    public SkifDefaultTypeMapperTestMapper(Class<? extends Mapping> mappingClass) {
         this(mappingClass, new DefaultObjectFactory(), new DefaultObjectFactory());
     }


    @SuppressWarnings("unchecked")
    public SkifDefaultTypeMapperTestMapper(Class<? extends Mapping> mappingClass, ObjectFactory wsapiObjectFactory, ObjectFactory domainObjectFactory) {
        super(mappingClass , wsapiObjectFactory, domainObjectFactory, false);

        DefaultTypeMapper dtm = new DefaultTypeMapper();
        dtm.addPackageMapping("no.statkart.skif.skiftest.wsapi.domain","no.statkart.skif.skiftest.domain");
        setDefaultMapper(dtm);

        // Klasser hvor objekter skal mappes til seg selv
        useIdentityMapping(String.class);
        useIdentityMapping(Integer.class);
        useIdentityMapping(Long.class);

    }

    @Override
    public SkifTestMapping getMapping() {
        return (SkifTestMapping) super.getMapping();
    }
}