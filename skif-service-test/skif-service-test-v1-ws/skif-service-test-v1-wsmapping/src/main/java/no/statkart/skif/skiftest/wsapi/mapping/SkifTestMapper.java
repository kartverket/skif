package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.SkifException;
import no.statkart.skif.mapper.*;
import no.statkart.skif.service.annotation.Implementation;
import no.statkart.skif.skiftest.domain.*;
import no.statkart.skif.skiftest.wsapi.domain.AList;
import no.statkart.skif.skiftest.wsapi.domain.StringList;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class SkifTestMapper extends AbstractMapper {
    Class<? extends Mapping> mappingClass;

   public SkifTestMapper() {
        this(SkifTestMapping.class, new DefaultObjectFactory(), new DefaultObjectFactory());
    }

    public SkifTestMapper(Class<? extends Mapping> mappingClass) {
         this(mappingClass, new DefaultObjectFactory(), new DefaultObjectFactory());
     }


    @SuppressWarnings("unchecked")
    public SkifTestMapper(Class<? extends Mapping> mappingClass, ObjectFactory wsapiObjectFactory, ObjectFactory domainObjectFactory) {
        super(mappingClass , wsapiObjectFactory, domainObjectFactory, false);

        // Klasser hvor objekter skal mappes til seg selv
        useIdentityMapping(String.class);
        useIdentityMapping(Integer.class);
        useIdentityMapping(Long.class);

        // Objekter
        addMapper(new ATypeMapper(no.statkart.skif.skiftest.wsapi.domain.A.class, A.class));
        addMapper(new BTypeMapper(no.statkart.skif.skiftest.wsapi.domain.B.class, B.class));

        // Lister
        addMapper(new WsapiListTypeMapper(AList.class, Collection.class));
        addMapper(new WsapiListTypeMapper(StringList.class, Collection.class));

//        addMapper(new MatrikkelContextTypeMapper(MatrikkelContext.class, no.statkart.matrikkel.api.service.MatrikkelContext.class));

    }

    @Override
    public SkifTestMapping getMapping() {
        return (SkifTestMapping) super.getMapping();
    }
}