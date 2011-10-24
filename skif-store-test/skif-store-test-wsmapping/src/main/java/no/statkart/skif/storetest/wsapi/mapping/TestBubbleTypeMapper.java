package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.storetest.domain.demo.TestBubble;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class TestBubbleTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.demo.TestBubble, DomainT extends TestBubble> extends StoreTestBubbleTypeMapper<WsapiT,DomainT> {
    public TestBubbleTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
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