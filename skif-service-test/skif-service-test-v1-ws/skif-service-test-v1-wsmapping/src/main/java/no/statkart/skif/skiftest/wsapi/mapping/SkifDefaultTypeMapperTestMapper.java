package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.mapper.*;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifDefaultTypeMapperTestMapper extends AbstractMapper {
    Class<? extends Mapping> mappingClass;

   public SkifDefaultTypeMapperTestMapper() {
        super(SkifTestMapping.class);

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