package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.storetest.domain.kodeliste.StoreTestKode;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class KodeTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.kodeliste.Kode, DomainT extends StoreTestKode> extends StoreTestBubbleTypeMapper<WsapiT,DomainT> {
    public KodeTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        super.mapDomainObject(source, target);
        KodelisteId kodelisteId= map.d2w(source.getKodelisteId());
        target.setKodelisteId(kodelisteId);
        target.setKodeverdi(map.d2w(source.getKodeverdi()));
        target.setBeskrivelse(map.d2w(source.getBeskrivelse()));
    }

    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        super.mapWsapiObject(source, target);
        target.setKodeverdi(map.w2d(source.getKodeverdi()));
        target.setKodelisteId(map.w2d(source.getKodelisteId()));
        target.setBeskrivelse(map.w2d(source.getBeskrivelse()));
    }
}