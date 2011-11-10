package no.statkart.skif.storetest.wsapi.exception.mapping;

import no.statkart.skif.mapper.*;
import no.statkart.skif.mapper.ObjectFactory;
import no.statkart.skif.storetest.wsapi.exception.ServiceException;
import no.statkart.skif.storetest.wsapi.exception.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Mapper for JAX-WS exception classes in API
 *
 * @author Leif Lislegård
 * @since 2.0
 */
public class StoreTestExceptionMapper extends AbstractExceptionMapper {

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
        exceptionClassMap.put(Feilkode.Kategori.SERVICE_APPLICATION_EXCEPTION.value, no.statkart.skif.exception.ApplicationException.class);
        exceptionClassMap.put(Feilkode.Kategori.SERVICE_SYSTEM_EXCEPTION.value, no.statkart.skif.exception.SystemException.class);
        exceptionClassMap.put(Feilkode.Kategori.SERVICE_EXCEPTION.value, no.statkart.skif.exception.SkifException.class);
    }


    public StoreTestExceptionMapper() {
        this(new DefaultObjectFactory(), new DefaultObjectFactory());
    }

    public StoreTestExceptionMapper(ObjectFactory wsapiObjectFactory, ObjectFactory domainObjectFactory) {
        super(StoreTestExceptionMapping.class, wsapiObjectFactory, domainObjectFactory, true, true);
        addMapptersForExceptionTypes();
    }

    @Override
    public StoreTestExceptionMapping getMapping() {
        return (StoreTestExceptionMapping) super.getMapping();
    }


    private void addMapptersForExceptionTypes() {
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