package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.kodelistesupport.KodeId;
import no.statkart.skif.storetest.domain.TestKodeliste;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeIdList;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class KodelisteTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste, DomainT extends TestKodeliste> extends StoreTestBubbleTypeMapper<WsapiT,DomainT> {
    public KodelisteTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        super.mapDomainObject(source, target);
        target.setKodeIdClass(getWsapiKodeIdClassname(source.getKodeIdClass()));
        target.setNavn(map.d2w(source.getNavn()));
        target.setBeskrivelse(map.d2w(source.getBeskrivelse()));
        target.setKodeIds(map.d2w(source.getKodeIds(), KodeIdList.class));
    }


    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        super.mapWsapiObject(source, target);
        target.setKodeIdClass(geDomainKodeIdClass(source.getKodeIdClass()));
        target.setNavn(map.w2d(source.getNavn()));
        target.setBeskrivelse(map.w2d(source.getBeskrivelse()));
        map.w2d(source.getKodeIds(), target.getKodeIds());
    }

    private String getWsapiKodeIdClassname(Class<? extends KodeId<?>> kodeIdClass) {
        return  kodeIdClass.getName().replace(".domain.kodeliste", ".wsapi.domain.kodeliste");
    }

    private Class<KodeId<?>> geDomainKodeIdClass(String kodeIdClassname) {
        try {
            Class<?> kodeIdClass = Class.forName(kodeIdClassname.replace(".wsapi.domain.kodeliste", ".domain.kodeliste"));
            return (Class<KodeId<?>>) kodeIdClass;
        } catch (ClassNotFoundException e) {
            throw new ImplementationException(e);
        }
    }
}