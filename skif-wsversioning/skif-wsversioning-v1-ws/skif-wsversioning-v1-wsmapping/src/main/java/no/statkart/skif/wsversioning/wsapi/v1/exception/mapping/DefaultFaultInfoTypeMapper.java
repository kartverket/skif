package no.statkart.skif.wsversioning.wsapi.v1.exception.mapping;

import com.google.common.reflect.TypeToken;
import no.statkart.skif.exception.SkifException;
import no.statkart.skif.mapper.exception.AbstractServiceFaultInfoTypeMapper;
import no.statkart.skif.wsversioning.wsapi.v1.exception.ServiceFaultInfo;

public class DefaultFaultInfoTypeMapper<WsapiT extends ServiceFaultInfo, DomainT extends SkifException> extends AbstractServiceFaultInfoTypeMapper<WsapiT, DomainT> {
    public DefaultFaultInfoTypeMapper(Class<WsapiT> wsapiTClass, Class<DomainT> domainTClass) {
        this(TypeToken.of(wsapiTClass), TypeToken.of(domainTClass));
    }

    public DefaultFaultInfoTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        super(wsapiTypeToken, domainTypeToken);
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
