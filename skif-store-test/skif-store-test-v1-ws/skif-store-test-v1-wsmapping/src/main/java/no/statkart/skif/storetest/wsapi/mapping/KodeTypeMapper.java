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
    public WsapiT mapDomainObject(DomainT source) {
        WsapiT target = super.mapDomainObject(source);
        KodelisteId kodelisteId= getMapping().d2w(source.getKodelisteId());
        target.setKodelisteId(kodelisteId);
        target.setKodeverdi(getMapping().d2w(source.getKodeverdi()));
        target.setBeskrivelse(getMapping().d2w(source.getBeskrivelse()));
        return target;
    }

    @Override
    public DomainT mapWsapiObject(WsapiT source) {
        DomainT target = super.mapWsapiObject(source);
        target.setKodeverdi(getMapping().w2d(source.getKodeverdi()));
        target.setKodelisteId(getMapping().w2d(source.getKodelisteId()));
        target.setBeskrivelse(getMapping().w2d(source.getBeskrivelse()));
        return target;
    }
}