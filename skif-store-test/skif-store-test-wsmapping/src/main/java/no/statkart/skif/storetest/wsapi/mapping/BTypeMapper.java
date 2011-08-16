package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.storetest.domain.B;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class BTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.B, DomainT extends B> extends AbstractStoreTestTypeMapper<WsapiT, DomainT> {

    protected BTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
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