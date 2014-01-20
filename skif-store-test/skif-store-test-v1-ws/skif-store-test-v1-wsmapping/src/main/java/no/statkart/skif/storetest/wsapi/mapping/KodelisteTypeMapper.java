package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.localization.LocalizedString;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodeliste;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeIdList;

import java.util.List;

/**
 * TypeMapper for Kodeliste i StoreTest applikasjonen.
 * <p/>
 * Kodeliste inneholder et felt (KodeIdClass) som angir med navnet på den KodeId klasse som
 * kodeliste instansen er kodeliste for. Dette feltet må mappes slik at klasse navnet blir riktig i det api det mappes
 * til. Det er ikke noen general regel for hvordan dette skal beregnes, men normalt vil alle klasser hedde det samme i
 * domene- og ws-apiet og kun pakken vil endres på standard vis når det mappes mellom api'en.
 * <p/>
 * I StoreTest applikasjonen er også klassenavnene på kodene forskjellige (i domene api'et har de prefix Test) og denne
 * klassen demonstrerer hvordan dette håndteres. Dersom det ikke er noen fast algoritme for hvordan Kodeklassene
 * navngis mellom api'ene må man f.eks skrive en KodelisteTypeMapper som bruker en map for å mappe kodeklasse navnene.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class KodelisteTypeMapper<WsapiT extends no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste, DomainT extends StoreTestKodeliste> extends StoreTestBubbleTypeMapper<WsapiT, DomainT> {
    private final String wsapiPackagePart;

    public KodelisteTypeMapper(String wsapiPackagePrefix, Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
        wsapiPackagePart = "." + wsapiPackagePrefix + ".domain";
    }

    @Override
    public WsapiT mapDomainObject(DomainT source) {
        WsapiT target = super.mapDomainObject(source);
        target.setKodeIdClass(calcWsapiKodeIdClassname(source.getKodeIdClass()));
        target.setNavn(getMapping().d2w(source.getNavn(), no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString.class));
        target.setBeskrivelse(getMapping().d2w(source.getBeskrivelse(), no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString.class));
        target.setKoderIds(getMapping().d2w(source.getKoderIds(), KodeIdList.class));
        return target;
    }


    @Override
    public DomainT mapWsapiObject(WsapiT source) {
        DomainT target = super.mapWsapiObject(source);
        target.setKodeIdClass(calcDomainKodeIdClass(source.getKodeIdClass()));
        target.setNavn(getMapping().w2d(source.getNavn(), LocalizedString.class));
        target.setBeskrivelse(getMapping().w2d(source.getBeskrivelse(), LocalizedString.class));
        target.setKoderIds(getMapping().w2d(source.getKoderIds(), List.class));
        return target;
    }

    private String calcWsapiKodeIdClassname(Class<? extends KodeId<?>> domainKodeIdClass) {
        if (domainKodeIdClass == null) return null;
        return domainKodeIdClass.getName().replace(".domain", wsapiPackagePart).replace(".koder.", ".koder.Test");
    }

    private Class<? extends KodeId<?>> calcDomainKodeIdClass(String wsapiKodeIdClassname) {
        try {
            if (wsapiKodeIdClassname == null) return null;
            String replace = wsapiKodeIdClassname.replace(wsapiPackagePart, ".domain").replace(".koder.Test", ".koder.");
            Class<? extends KodeId> kodeIdClass = Class.forName(replace).asSubclass(KodeId.class);
            return (Class<? extends KodeId<?>>) kodeIdClass;
        } catch (ClassNotFoundException e) {
            throw new ImplementationException(e);
        }
    }
}