package no.statkart.skif.service.locker;

import no.statkart.skif.mapper.*;

import java.util.Collection;

/**
 * Nødvendig for injector. Brukes ikke for mapping for webservice (foreløpig)
 *
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class SkifMapper extends AbstractMapper {

   public SkifMapper() {
        this(SkifMapping.class, new DefaultObjectFactory(), new DefaultObjectFactory());
    }

    public SkifMapper(Class<? extends Mapping> mappingClass) {
         this(mappingClass, new DefaultObjectFactory(), new DefaultObjectFactory());
     }


    @SuppressWarnings("unchecked")
    public SkifMapper(Class<? extends Mapping> mappingClass, ObjectFactory wsapiObjectFactory, ObjectFactory domainObjectFactory) {
        super(mappingClass , wsapiObjectFactory, domainObjectFactory, false);

        // Klasser hvor objekter skal mappes til seg selv
        useIdentityMapping(String.class);
        useIdentityMapping(Integer.class);
        useIdentityMapping(Long.class);

        // Objekter

        // Lister

    }

    @Override
    public SkifMapping getMapping() {
        return (SkifMapping) super.getMapping();
    }
}
