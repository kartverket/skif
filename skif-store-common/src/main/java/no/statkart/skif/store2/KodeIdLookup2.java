package no.statkart.skif.store2;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store2.kodelistesupport2.Kode2;
import no.statkart.skif.store2.kodelistesupport2.KodeId2;
import no.statkart.skif.store2.kodelistesupport2.Kodeliste2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class KodeIdLookup2 {
    final Map<Class<? extends KodeId2>, Map<String, KodeId2<?>>> idMapMap = new HashMap<Class<? extends KodeId2>, Map<String, KodeId2<?>>>();
    final Map<Class<? extends KodeId2>, Map<KodeId2<?>, String>> stringMapMap = new HashMap<Class<? extends KodeId2>, Map<KodeId2<?>, String>>();


    private KodeIdLookup2(Collection<? extends Kode2> koder) {
        for (Kode2 kode : koder) {
            { //verdier for lookup
                Map<String, KodeId2<?>> map = idMapMap.get(kode.getId().getClass());
                if (map == null) {
                    map = new HashMap<String, KodeId2<?>>();
                    idMapMap.put(kode.getId().getClass(), map);
                }
                map.put(kode.getKodeverdi(), kode.getId());
            }
            { //ids for lookup
                Map<KodeId2<?>, String> map = stringMapMap.get(kode.getId().getClass());
                if (map == null) {
                    map = new HashMap<KodeId2<?>, String>();
                    stringMapMap.put(kode.getId().getClass(), map);
                }
                map.put(kode.getId(), kode.getKodeverdi());
            }
        }
    }

    public static KodeIdLookup2 buildFromKodeliste(Collection<? extends Kodeliste2> kodelisteCollection) {
        ArrayList<Kode2> kodes = new ArrayList<Kode2>();
        for (Kodeliste2 kodeliste : kodelisteCollection) {
            kodes.addAll(kodeliste.getKoder());
        }
        return buildFromKode(kodes);
    }

    public static KodeIdLookup2 buildFromKode(Collection<? extends Kode2> kodeCollection) {
        return new KodeIdLookup2(kodeCollection);

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
    public <I extends KodeId2<?>> I fromKodeVerdi(Class<I> kodeIdClass, String kodeVerdi) {
        I kodeId = null;
        Map<String, KodeId2<?>> bubbleKodeIdMap = idMapMap.get(kodeIdClass);
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
    public <I extends KodeId2<?>> String fromKodeId(I kodeId) {
        String verdi;
        Map<KodeId2<?>, String> stringMap = stringMapMap.get(kodeId.getClass());
        if (stringMap != null) {
            return stringMap.get(kodeId);
        } else {
            throw new ImplementationException("Fant ikke kode-klasse " + kodeId);
        }
    }
}
