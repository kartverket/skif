package no.statkart.skif.storetest.wsapi.exception.mapping;

import com.google.common.reflect.TypeToken;
import no.statkart.skif.exception.SkifException;
import no.statkart.skif.mapper.exception.AbstractServiceFaultInfoTypeMapper;
import no.statkart.skif.storetest.wsapi.exception.ServiceFaultInfo;

import java.util.Set;

/**
 * Bindeleddet mellom {@link AbstractServiceFaultInfoTypeMapper} og StoreTest-prosjektet.
 */
public class StoreTestFaultInfoTypeMapper<WsapiT extends ServiceFaultInfo, DomainT extends SkifException> extends AbstractServiceFaultInfoTypeMapper<WsapiT, DomainT> {
    public StoreTestFaultInfoTypeMapper(Class<WsapiT> wsapiTClass, Class<DomainT> domainTClass) {
        this(TypeToken.of(wsapiTClass), TypeToken.of(domainTClass));
    }

    public StoreTestFaultInfoTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        super(wsapiTypeToken, domainTypeToken);
    }

    public StoreTestFaultInfoTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken, Set<Class<?>> doNotMapTheseClasses, boolean failIfMissingDomainProperties) {
        super(wsapiTypeToken, domainTypeToken, doNotMapTheseClasses, failIfMissingDomainProperties);
    }

    @Override
    protected String getExceptionDetailClassName(WsapiT source) {
        return source.getExceptionDetail().getClassName();
    }

    @Override
    protected String getExceptionDetailMessage(WsapiT source) {
        return source.getExceptionDetail().getMessage();
    }
}
