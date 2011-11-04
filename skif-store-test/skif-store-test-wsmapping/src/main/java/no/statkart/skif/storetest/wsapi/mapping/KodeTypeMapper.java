package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.storetest.domain.kode.StoreTestKode;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class KodeTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.kode.Kode, DomainT extends StoreTestKode> extends StoreTestBubbleTypeMapper<WsapiT,DomainT> {
    public KodeTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        super.mapDomainObject(source, target);
        target.setKodeverdi(map.d2w(source.getKodeverdi()));
        target.setBeskrivelse(map.d2w(source.getBeskrivelse()));
    }

    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        super.mapWsapiObject(source, target);
        target.setKodeverdi(map.d2w(source.getKodeverdi()));
        target.setBeskrivelse(map.d2w(source.getBeskrivelse()));
    }
}