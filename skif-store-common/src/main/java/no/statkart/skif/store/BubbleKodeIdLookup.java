package no.statkart.skif.store;

import no.statkart.skif.store.kodelistesupport.BubbleKode;
import no.statkart.skif.store.kodelistesupport.BubbleKodeId;
import no.statkart.skif.store.kodelistesupport.BubbleKodeliste;
import no.statkart.skif.exception.ImplementationException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class BubbleKodeIdLookup {
    final Map<Class<? extends BubbleKodeId>, Map<String, BubbleKodeId<?>>> idMapMap = new HashMap<Class<? extends BubbleKodeId>, Map<String, BubbleKodeId<?>>>();
    final Map<Class<? extends BubbleKodeId>, Map<BubbleKodeId<?>, String>> stringMapMap = new HashMap<Class<? extends BubbleKodeId>, Map<BubbleKodeId<?>, String>>();


    private BubbleKodeIdLookup(Collection<? extends BubbleKode> koder) {
        for (BubbleKode kode : koder) {
            { //verdier for lookup
                Map<String, BubbleKodeId<?>> map = idMapMap.get(kode.getId().getClass());
                if (map == null) {
                    map = new HashMap<String, BubbleKodeId<?>>();
                    idMapMap.put(kode.getId().getClass(), map);
                }
                map.put(kode.getKodeverdi(), kode.getId());
            }
            { //ids for lookup
                Map<BubbleKodeId<?>, String> map = stringMapMap.get(kode.getId().getClass());
                if (map == null) {
                    map = new HashMap<BubbleKodeId<?>, String>();
                    stringMapMap.put(kode.getId().getClass(), map);
                }
                map.put(kode.getId(), kode.getKodeverdi());
            }
        }
    }

    public static BubbleKodeIdLookup buildFromKodeliste(Collection<? extends BubbleKodeliste> kodelisteCollection) {
        ArrayList<BubbleKode> kodes = new ArrayList<BubbleKode>();
        for (BubbleKodeliste kodeliste : kodelisteCollection) {
            kodes.addAll(kodeliste.getKoder());
        }
        return buildFromKode(kodes);
    }

    public static BubbleKodeIdLookup buildFromKode(Collection<? extends BubbleKode> kodeCollection) {
        return new BubbleKodeIdLookup(kodeCollection);

    }


    /**
     *
     * @return kode eller {@code null} dersom {@code kodeVerdi} er null
     * @throws ImplementationException
     * <ul>
     *  <li>dersom kodeklasse ikke funnet
     *  <li>dersom kodeverdi er gitt og kodeverdi for kode ikke funnet
     * </ul>
     */
    public <I extends BubbleKodeId<?>> I fromKodeVerdi(Class<I> kodeIdClass, String kodeVerdi) {
        I kodeId = null;
        Map<String, BubbleKodeId<?>> bubbleKodeIdMap = idMapMap.get(kodeIdClass);
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
     * @throws ImplementationException
     * <ul>
     *  <li>dersom id ikke funnet
     * </ul>
     */
    public <I extends BubbleKodeId<?>> String fromKodeId(I kodeId) {
        String verdi;
        Map<BubbleKodeId<?>, String> stringMap = stringMapMap.get(kodeId.getClass());
        if (stringMap != null) {
            return stringMap.get(kodeId);
        } else {
            throw new ImplementationException("Fant ikke kode-klasse " + kodeId);
        }
    }
}
