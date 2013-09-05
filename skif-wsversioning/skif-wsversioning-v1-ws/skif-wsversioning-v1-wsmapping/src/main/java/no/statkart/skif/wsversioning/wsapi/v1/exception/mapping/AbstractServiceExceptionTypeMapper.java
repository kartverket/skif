package no.statkart.skif.wsversioning.wsapi.v1.exception.mapping;

import no.statkart.skif.exception.SkifException;
import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.wsversioning.wsapi.v1.exception.ServiceException;
import no.statkart.skif.wsversioning.wsapi.v1.exception.ServiceFaultInfo;

import java.util.Map;


/**
 * Abstract TypeMapper for exceptions
 *
 * @author Leif Lislegård
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
abstract class AbstractServiceExceptionTypeMapper<WsapiT extends ServiceException, DomainT extends SkifException> extends AbstractTypeMapper<WsapiT, DomainT, WSVersioningExceptionMapping> {

    private final Map<String, Class<? extends DomainT>> exceptionClassMap;

    protected AbstractServiceExceptionTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass, Map<String, Class<? extends DomainT>> exceptionClassMap) {
        super(wsapiClass, domainClass, WSVersioningExceptionMapping.class);
        this.exceptionClassMap = exceptionClassMap;
    }

    protected String findCategory(DomainT source) {
        Class<? extends SkifException> sourceClass = source.getClass();
        for (Map.Entry<String, Class<? extends DomainT>> entry : exceptionClassMap.entrySet()) {
            if (entry.getValue().isAssignableFrom(sourceClass)) {
                return entry.getKey();
            }
        }
        throw new MappingException("Could not find category for exception class: " + source.getClass().getName()); //skal ikke kunne forekomme
    }

    /**
     * Forsøker å finne intern domene-exception basert på {@link ServiceFaultInfo#category}
     * @return den exceptionen som domenet kjenner til og som passer best.
     */
    protected Class<? extends DomainT> findDomainClass(WsapiT source) {
        ServiceFaultInfo faultInfo = source.getFaultInfo();
        if (faultInfo != null) {
            String category = faultInfo.getCategory();
            if (category != null) {
                for (Map.Entry<String, Class<? extends DomainT>> entry : exceptionClassMap.entrySet()) {
                    if (category.startsWith(entry.getKey())) {
                        return entry.getValue();
                    }
                }
            }
        }
        return getDomainClass();
    }


}
