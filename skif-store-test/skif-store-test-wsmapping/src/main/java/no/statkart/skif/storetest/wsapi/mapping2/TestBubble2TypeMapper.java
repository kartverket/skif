package no.statkart.skif.storetest.wsapi.mapping2;

import no.statkart.skif.storetest.domain2.TestBubble2;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestBubbleTypeMapper;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class TestBubble2TypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain2.TestBubble2, DomainT extends TestBubble2> extends StoreTestBubble2TypeMapper<WsapiT,DomainT> {
    public TestBubble2TypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        super.mapDomainObject(source, target);
        target.setText(map.d2w(source.getText()));
    }

    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        super.mapWsapiObject(source, target);
        target.setText(map.w2d(source.getText()));
    }
}