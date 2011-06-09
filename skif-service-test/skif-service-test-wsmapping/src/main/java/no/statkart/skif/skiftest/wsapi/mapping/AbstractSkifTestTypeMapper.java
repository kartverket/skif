package no.statkart.skif.skiftest.wsapi.mapping;

import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.mapper.Mapping;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public abstract class AbstractSkifTestTypeMapper<WsapiT, DomainT> extends AbstractTypeMapper<WsapiT,  DomainT> {
    protected SkifTestMapping map;

    protected AbstractSkifTestTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public Mapping getMapping() {
        return map;
    }

    @Override
    public void setMapping(Mapping mapping) {
        this.map = (SkifTestMapping) mapping;
    }

}