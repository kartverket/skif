package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.mapper.AbstractTypeMapper;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class AbstractSkifTestTypeMapper<WsapiT, DomainT> extends AbstractTypeMapper<WsapiT,  DomainT, SkifTestMapping> {

    protected AbstractSkifTestTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass, SkifTestMapping.class);
    }
}