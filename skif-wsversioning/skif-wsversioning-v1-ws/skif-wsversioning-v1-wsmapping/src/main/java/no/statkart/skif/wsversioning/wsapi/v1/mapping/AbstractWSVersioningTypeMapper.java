package no.statkart.skif.wsversioning.wsapi.v1.mapping;

import no.statkart.skif.mapper.AbstractTypeMapper;

public abstract class AbstractWSVersioningTypeMapper<WsapiT, DomainT> extends AbstractTypeMapper<WsapiT,  DomainT, WSVersioningMapping> {
    protected AbstractWSVersioningTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass, WSVersioningMapping.class);
    }

}