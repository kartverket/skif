package no.statkart.skif.storetest.wsapi.mapping2;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.kodelistesupport.BubbleKodeId;
import no.statkart.skif.store2.kodelistesupport2.KodeId2;
import no.statkart.skif.store2.kodelistesupport2.Kodeliste2;
import no.statkart.skif.storetest.domain2.TestKodeId2;
import no.statkart.skif.storetest.domain2.TestKodeliste2;
import no.statkart.skif.storetest.wsapi.domain2.kodeliste.KodeId2List;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestBubbleTypeMapper;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class Kodeliste2TypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain2.kodeliste.Kodeliste2, DomainT extends TestKodeliste2> extends StoreTestBubble2TypeMapper<WsapiT,DomainT> {
    public Kodeliste2TypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        super.mapDomainObject(source, target);
        target.setKodeIdClass(getWsapiKodeIdClassname(source.getKodeIdClass()));
        target.setNavn(map.d2w(source.getNavn()));
        target.setBeskrivelse(map.d2w(source.getBeskrivelse()));
        target.setKodeIds(map.d2w(source.getKodeIds(), KodeId2List.class));
    }


    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        super.mapWsapiObject(source, target);
        target.setKodeIdClass(geDomainKodeIdClass(source.getKodeIdClass()));
        target.setNavn(map.w2d(source.getNavn()));
        target.setBeskrivelse(map.w2d(source.getBeskrivelse()));
        map.w2d(source.getKodeIds(), target.getKodeIds());
    }

    private String getWsapiKodeIdClassname(Class<? extends KodeId2<?>> kodeIdClass) {
        return  kodeIdClass.getName().replace(".domain.kodeliste", ".wsapi.domain.kodeliste");
    }

    private Class<KodeId2<?>> geDomainKodeIdClass(String kodeIdClassname) {
        try {
            Class<?> kodeIdClass = Class.forName(kodeIdClassname.replace(".wsapi.domain2.kodeliste", ".domain2.kodeliste"));
            return (Class<KodeId2<?>>) kodeIdClass;
        } catch (ClassNotFoundException e) {
            throw new ImplementationException(e);
        }
    }
}