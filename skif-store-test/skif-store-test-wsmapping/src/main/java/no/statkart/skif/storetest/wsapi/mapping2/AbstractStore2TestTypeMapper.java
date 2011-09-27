package no.statkart.skif.storetest.wsapi.mapping2;

import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapping;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public abstract class AbstractStore2TestTypeMapper<WsapiT, DomainT> extends AbstractTypeMapper<WsapiT,  DomainT> {
    protected StoreTestMapping2 map;

    protected AbstractStore2TestTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public Mapping getMapping() {
        return map;
    }

    @Override
    public void setMapping(Mapping mapping) {
        this.map = (StoreTestMapping2) mapping;
    }

}