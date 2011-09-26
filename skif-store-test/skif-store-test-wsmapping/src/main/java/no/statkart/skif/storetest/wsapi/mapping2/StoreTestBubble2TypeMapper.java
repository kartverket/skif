package no.statkart.skif.storetest.wsapi.mapping2;

import no.statkart.skif.storetest.domain2.StoreTestBubble2;
import no.statkart.skif.storetest.wsapi.domain2.StoreTestBubbleId2;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class
StoreTestBubble2TypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain2.StoreTestBubble2, DomainT extends StoreTestBubble2> extends AbstractStore2TestTypeMapper<WsapiT,DomainT> {
    public StoreTestBubble2TypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        super.mapDomainObject(source, target);
        target.setId(map.<StoreTestBubbleId2>d2w(source.getId()));
        target.setVersion(map.d2w(source.getVersion()));
    }

    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        super.mapWsapiObject(source, target);
        target.setId(map.w2d(source.getId()));
        target.setVersion(map.w2d(source.getVersion()));
    }
}