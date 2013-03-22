package no.statkart.skif.skiftest.wsapi.exception.impl.mapping;

import no.statkart.skif.mapper.*;
import no.statkart.skif.mapper.ObjectFactory;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;
import no.statkart.skif.skiftest.wsapi.exception.impl.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mapper for JAX-WS exception classes in API
 *
 * @author Leif Lislegård
 * @since 2.0
 */
public class SkifTestExceptionMapper extends AbstractExceptionMapper {

    /**
     * Definisjon av hvordan man mapper tilbake til domenet basert på 'category'
     *
     * NB: insertion order er viktig. legg superklasser til sist!
     */

    private final static Map<String, Class<? extends no.statkart.skif.exception.SkifException>> exceptionClassMap = new LinkedHashMap<String, Class<? extends no.statkart.skif.exception.SkifException>>();
    static {
        exceptionClassMap.put(Feilkode.Kategori.SERVICE_APPLICATION_FINDER_EXCEPTION.value, no.statkart.skif.exception.FinderException.class);
        exceptionClassMap.put(Feilkode.Kategori.SERVICE_APPLICATION_VALIDATION_EXCEPTION.value, no.statkart.skif.exception.ValidationException.class);
        exceptionClassMap.put(Feilkode.Kategori.SERVICE_SYSTEM_IMPLEMENTATION_EXCEPTION.value, no.statkart.skif.exception.ImplementationException.class);
        exceptionClassMap.put(Feilkode.Kategori.SERVICE_SYSTEM_OPERATIONAL_EXCEPTION.value, no.statkart.skif.exception.OperationalException.class);
        exceptionClassMap.put(Feilkode.Kategori.SERVICE_PERMISSION_DENIED_EXCEPTION.value, no.statkart.skif.exception.PermissionDeniedException.class);
        exceptionClassMap.put(Feilkode.Kategori.SERVICE_INVALID_USER_EXCEPTION.value, no.statkart.skif.exception.InvalidUserException.class);
        exceptionClassMap.put(Feilkode.Kategori.SERVICE_ACCESS_EXCEPTION.value, no.statkart.skif.exception.AccessException.class);
        exceptionClassMap.put(Feilkode.Kategori.SERVICE_APPLICATION_EXCEPTION.value, no.statkart.skif.exception.ApplicationException.class);
        exceptionClassMap.put(Feilkode.Kategori.SERVICE_SYSTEM_EXCEPTION.value, no.statkart.skif.exception.SystemException.class);
        exceptionClassMap.put(Feilkode.Kategori.SERVICE_EXCEPTION.value, no.statkart.skif.exception.SkifException.class);
    }


    public SkifTestExceptionMapper() {
        this(new DefaultObjectFactory(), new DefaultObjectFactory());
    }

    public SkifTestExceptionMapper(ObjectFactory wsapiObjectFactory, ObjectFactory domainObjectFactory) {
        super(SkifTestExceptionMapping.class, wsapiObjectFactory, domainObjectFactory, true, true);
        addMappersForExceptionTypes();
    }

    @Override
    public SkifTestExceptionMapping getMapping() {
        return (SkifTestExceptionMapping) super.getMapping();
    }


    private void addMappersForExceptionTypes() {
        addMapper(new ServiceExceptionTypeMapper(exceptionClassMap, ServiceException.class, no.statkart.skif.exception.SkifException.class, ServiceFaultInfo.class));
        addMapperW2D(new IdentityExceptionTypeMapper(Error.class));
        addMapperW2D(new IdentityExceptionTypeMapper(RuntimeException.class));
    }


    public enum Feilkode {

        IkkeImplementert; //alternativ for implementasjon av feilkoder


        /**
         * Kategorisering av exceptions
         *
         * @author Leif Lislegård
         * @since 2.0
         */
        public static enum Kategori {
            SERVICE_EXCEPTION(":ServiceException:"),
            SERVICE_SYSTEM_EXCEPTION(":ServiceException:SystemException:"),
            SERVICE_SYSTEM_IMPLEMENTATION_EXCEPTION(":ServiceException:SystemException:ImplementationException:"),
            SERVICE_SYSTEM_OPERATIONAL_EXCEPTION(":ServiceException:SystemException:OperationalException:"),
            SERVICE_APPLICATION_EXCEPTION(":ServiceException:ApplicationException:"),
            SERVICE_ACCESS_EXCEPTION(":ServiceException:ApplicationException:AccessException:"),
            SERVICE_INVALID_USER_EXCEPTION(":ServiceException:ApplicationException:AccessException:InvalidUserException:"),
            SERVICE_PERMISSION_DENIED_EXCEPTION(":ServiceException:ApplicationException:AccessException:PermissionDeniedException:"),
            SERVICE_APPLICATION_FINDER_EXCEPTION(":ServiceException:ApplicationException:FinderException:"),
            SERVICE_APPLICATION_VALIDATION_EXCEPTION(":ServiceException:ApplicationException:ValidationException:"),;

            public String value;

            Kategori(String kategori) {
                this.value = kategori;
            }


            public String toString() {
                return new StringBuilder(name()).append('{').append(value).append('}').toString();
            }
        }
    }


}