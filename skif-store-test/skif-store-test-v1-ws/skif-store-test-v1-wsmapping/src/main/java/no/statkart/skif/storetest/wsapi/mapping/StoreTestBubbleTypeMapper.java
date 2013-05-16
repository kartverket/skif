package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestBubbleTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.StoreTestBubble, DomainT extends StoreTestBubble> extends AbstractStoreTestTypeMapper<WsapiT,DomainT> {
    public StoreTestBubbleTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        target.setId(map.d2w((StoreTestBubbleId) source.getId()));
        target.setVersion(map.d2w(source.getVersion()));
    }

    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        target.setId(map.w2d(source.getId()));
        target.setVersion(map.w2d(source.getVersion()));
    }
}