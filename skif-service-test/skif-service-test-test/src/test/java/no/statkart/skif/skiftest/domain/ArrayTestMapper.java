package no.statkart.skif.skiftest.domain;

import no.statkart.skif.mapper.*;

/**
 * @author Roar Ingebrigtsen
 * @since 3.0
 */
public class ArrayTestMapper extends AbstractMapper<ArrayTestMapping> {

    public ArrayTestMapper(){
        super(ArrayTestMapping.class);

        DefaultTypeMapper dtm = new DefaultTypeMapper();
        dtm.addPackageMapping("no.statkart.skif.skiftest.domain.array2", "no.statkart.skif.skiftest.domain.array1");

        setDefaultMapper(dtm);

        useIdentityMapping(String.class);
        useIdentityMapping(Integer.class);
        useIdentityMapping(Class.class);

    }
}
