package no.statkart.skif.skiftest.wsapi.exception.impl.mapping;

import no.statkart.skif.exception.SkifException;
import no.statkart.skif.mapper.AbstractExceptionMapper;
import no.statkart.skif.mapper.IdentityExceptionTypeMapper;
import no.statkart.skif.mapper.IdentityTypeMapperFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mapper for JAX-WS exception classes in API
 *
 * @since 2.0
 */
public class SkifTestExceptionMapper extends AbstractExceptionMapper<SkifTestExceptionMapping> {

    /**
     * Definisjon av hvordan man mapper tilbake til domenet basert på 'category'
     *
     * NB: insertion order er viktig. legg superklasser til sist!
     */

    private final static Map<String, Class<? extends SkifException>> exceptionClassMap = new LinkedHashMap<>();
    static {
        exceptionClassMap.put(Kategori.SERVICE_APPLICATION_FINDER_EXCEPTION.value, no.statkart.skif.exception.FinderException.class);
        exceptionClassMap.put(Kategori.SERVICE_APPLICATION_VALIDATION_EXCEPTION.value, no.statkart.skif.exception.ValidationException.class);
        exceptionClassMap.put(Kategori.SERVICE_SYSTEM_IMPLEMENTATION_EXCEPTION.value, no.statkart.skif.exception.ImplementationException.class);
        exceptionClassMap.put(Kategori.SERVICE_SYSTEM_OPERATIONAL_EXCEPTION.value, no.statkart.skif.exception.OperationalException.class);
        exceptionClassMap.put(Kategori.SERVICE_PERMISSION_DENIED_EXCEPTION.value, no.statkart.skif.exception.PermissionDeniedException.class);
        exceptionClassMap.put(Kategori.SERVICE_INVALID_USER_EXCEPTION.value, no.statkart.skif.exception.InvalidUserException.class);
        exceptionClassMap.put(Kategori.SERVICE_ACCESS_EXCEPTION.value, no.statkart.skif.exception.AccessException.class);
        exceptionClassMap.put(Kategori.SERVICE_APPLICATION_EXCEPTION.value, no.statkart.skif.exception.ApplicationException.class);
        exceptionClassMap.put(Kategori.SERVICE_SYSTEM_EXCEPTION.value, no.statkart.skif.exception.SystemException.class);
        exceptionClassMap.put(Kategori.SERVICE_EXCEPTION.value, no.statkart.skif.exception.SkifException.class);
    }


    public SkifTestExceptionMapper() {
        super(SkifTestExceptionMapping.class, true);

        addMapperFactory(new IdentityTypeMapperFactory().useIdentityMappingForBasicTypes());

        addMappersForExceptionTypes();

        addMappersForFaultTypes();
    }

    @Override
    public SkifTestExceptionMapping getMapping() {
        return super.getMapping();
    }

    private void addMappersForFaultTypes() {
        addMapper(new ServiceFaultInfoTypeMapper());
        addMapper(new SimpleFaultInfoTypeMapper());
    }


    private void addMappersForExceptionTypes() {
        addMapper(new ServiceExceptionTypeMapper(exceptionClassMap));
        addMapper(new IdentityExceptionTypeMapper<>(Error.class));
        addMapper(new IdentityExceptionTypeMapper<>(RuntimeException.class));
    }

    /**
     * Kategorisering av exceptions
     *
     * @author Leif Lislegård
     * @since 2.0
     */
    public enum Kategori {
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
            return name() + '{' + value + '}';
        }
    }
}
