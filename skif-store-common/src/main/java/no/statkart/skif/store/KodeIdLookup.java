package no.statkart.skif.store;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.kodeliste.Kodeliste;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class KodeIdLookup {
    final Map<Class<? extends KodeId>, Map<String, KodeId<?>>> idMapMap = new HashMap<Class<? extends KodeId>, Map<String, KodeId<?>>>();
    final Map<Class<? extends KodeId>, Map<KodeId<?>, String>> stringMapMap = new HashMap<Class<? extends KodeId>, Map<KodeId<?>, String>>();


    private KodeIdLookup(Collection<? extends Kode> koder) {
        for (Kode kode : koder) {
            { //verdier for lookup
                Map<String, KodeId<?>> map = idMapMap.get(kode.getId().getClass());
                if (map == null) {
                    map = new HashMap<String, KodeId<?>>();
                    idMapMap.put(kode.getId().getClass(), map);
                }
                map.put(kode.getKodeverdi(), kode.getId());
            }
            { //ids for lookup
                Map<KodeId<?>, String> map = stringMapMap.get(kode.getId().getClass());
                if (map == null) {
                    map = new HashMap<KodeId<?>, String>();
                    stringMapMap.put(kode.getId().getClass(), map);
                }
                map.put(kode.getId(), kode.getKodeverdi());
            }
        }
    }

    public static KodeIdLookup buildFromKodeliste(Collection<? extends Kodeliste> kodelisteCollection) {
        ArrayList<Kode> kodes = new ArrayList<Kode>();
        for (Kodeliste kodeliste : kodelisteCollection) {
            kodes.addAll(kodeliste.getKoder());
        }
        return buildFromKode(kodes);
    }

    public static KodeIdLookup buildFromKode(Collection<? extends Kode> kodeCollection) {
        return new KodeIdLookup(kodeCollection);

    }


    /**
     *
     * @return kode eller {@code null} dersom {@code kodeVerdi} er null
     * @throws no.statkart.skif.exception.ImplementationException
     * <ul>
     *  <li>dersom kodeklasse ikke funnet
     *  <li>dersom kodeverdi er gitt og kodeverdi for kode ikke funnet
     * </ul>
     */
    public <I extends KodeId<?>> I fromKodeVerdi(Class<I> kodeIdClass, String kodeVerdi) {
        I kodeId = null;
        Map<String, KodeId<?>> bubbleKodeIdMap = idMapMap.get(kodeIdClass);
        if (bubbleKodeIdMap != null) {
            kodeId = (I) bubbleKodeIdMap.get(kodeVerdi);

            if(kodeId == null && kodeVerdi != null) {
                throw new ImplementationException("Fant ikke kode " + kodeVerdi + " for kode-klasse " + kodeIdClass.getName());
            }

        } else {
            throw new ImplementationException("Fant ikke kode-klasse " + kodeIdClass.getName());
        }

        return kodeId;
    }

    /**
     *
     * @return verdi for kode eller {@code null} dersom ikke funnet
     * @throws no.statkart.skif.exception.ImplementationException
     * <ul>
     *  <li>dersom id ikke funnet
     * </ul>
     */
    public <I extends KodeId<?>> String fromKodeId(I kodeId) {
        String verdi;
        Map<KodeId<?>, String> stringMap = stringMapMap.get(kodeId.getClass());
        if (stringMap != null) {
            return stringMap.get(kodeId);
        } else {
            throw new ImplementationException("Fant ikke kode-klasse " + kodeId);
        }
    }
}
