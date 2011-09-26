package no.statkart.skif.storetest.wsapi.mapping2;

import no.statkart.skif.store2.kodelistesupport2.Kode2;
import no.statkart.skif.storetest.domain.kodeliste.Kode;
import no.statkart.skif.storetest.domain2.TestKode2;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestBubbleTypeMapper;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class Kode2TypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain2.kodeliste.Kode2, DomainT extends TestKode2> extends StoreTestBubble2TypeMapper<WsapiT,DomainT> {
    public Kode2TypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
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