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
    public WsapiT mapDomainObject(DomainT source) {
        WsapiT target = createWsapiT();
        target.setId(getMapping().d2w((StoreTestBubbleId) source.getId()));
        return target;
    }

    @Override
    public DomainT mapWsapiObject(WsapiT source) {
        DomainT target = createDomainT();
        target.setId(getMapping().w2d(source.getId()));
        return target;
    }
}