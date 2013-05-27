package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.mapper.Mapping;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class AbstractStoreTestTypeMapper<WsapiT, DomainT> extends AbstractTypeMapper<WsapiT,  DomainT, StoreTestMapping> {
    protected AbstractStoreTestTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass, StoreTestMapping.class);
    }

}