package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodeliste;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeIdList;

import java.util.List;

/**
 * TypeMapper for Kodeliste i StoreTest applikasjonen.
 *
 * Kodeliste inneholder et felt (KodeIdClass) som angir med navnet på den KodeId klasse som
 * kodeliste instansen er kodeliste for. Dette feltet må mappes slik at klasse navnet blir riktig i det api det mappes
 * til. Det er ikke noen general regel for hvordan dette skal beregnes, men normalt vil alle klasser hedde det samme i
 * domene- og ws-apiet og kun pakken vil endres på standard vis når det mappes mellom api'en.
 *
 * I StoreTest applikasjonen er også klassenavnene på kodene forskjellige (i domene api'et har de prefix Test) og denne
 * klassen demonstrerer hvordan dette håndteres. Dersom det ikke er noen fast algoritme for hvordan Kodeklassene
 * navngis mellom api'ene må man f.eks skrive en KodelisteTypeMapper som bruker en map for å mappe kodeklasse navnene.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class KodelisteTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste, DomainT extends StoreTestKodeliste> extends StoreTestBubbleTypeMapper<WsapiT,DomainT> {
    private final String wsapiPackagePart;

     public KodelisteTypeMapper(String wsapiPackagePrefix, Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
          wsapiPackagePart = "." + wsapiPackagePrefix + ".domain";
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        super.mapDomainObject(source, target);
        target.setKodeIdClass(calcWsapiKodeIdClassname(source.getKodeIdClass()));
        target.setNavn(map.d2w(source.getNavn()));
        target.setBeskrivelse(map.d2w(source.getBeskrivelse()));
        target.setKodeIds(map.d2w(source.getKodeIds(), KodeIdList.class));
    }


    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        super.mapWsapiObject(source, target);
        target.setKodeIdClass(calcDomainKodeIdClass(source.getKodeIdClass()));
        target.setNavn(map.w2d(source.getNavn()));
        target.setBeskrivelse(map.w2d(source.getBeskrivelse()));
        target.setKodeIds(map.w2d(source.getKodeIds(), List.class));
    }

    private String calcWsapiKodeIdClassname(Class<? extends KodeId<?>> domainKodeIdClass) {
        if (domainKodeIdClass==null) return null;
        return  domainKodeIdClass.getName().replace(".domain", wsapiPackagePart).replace(".koder.", ".koder.Test");
    }

    private Class<KodeId<?>> calcDomainKodeIdClass(String wsapiKodeIdClassname) {
        try {
            if (wsapiKodeIdClassname==null) return null;
            String replace = wsapiKodeIdClassname.replace(wsapiPackagePart, ".domain").replace(".koder.Test", ".koder.");
            Class<?> kodeIdClass = Class.forName(replace);
            return (Class<KodeId<?>>) kodeIdClass;
        } catch (ClassNotFoundException e) {
            throw new ImplementationException(e);
        }
    }
}