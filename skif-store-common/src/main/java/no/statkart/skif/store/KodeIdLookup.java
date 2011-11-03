package no.statkart.skif.store;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.kodelistesupport.KodeImpl;
import no.statkart.skif.store.kodelistesupport.KodeImplId;
import no.statkart.skif.store.kodelistesupport.Kodeliste;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class KodeIdLookup {
    final Map<Class<? extends KodeImplId>, Map<String, KodeImplId<?>>> idMapMap = new HashMap<Class<? extends KodeImplId>, Map<String, KodeImplId<?>>>();
    final Map<Class<? extends KodeImplId>, Map<KodeImplId<?>, String>> stringMapMap = new HashMap<Class<? extends KodeImplId>, Map<KodeImplId<?>, String>>();


    private KodeIdLookup(Collection<? extends KodeImpl> koder) {
        for (KodeImpl kode : koder) {
            { //verdier for lookup
                Map<String, KodeImplId<?>> map = idMapMap.get(kode.getId().getClass());
                if (map == null) {
                    map = new HashMap<String, KodeImplId<?>>();
                    idMapMap.put(kode.getId().getClass(), map);
                }
                map.put(kode.getKodeverdi(), kode.getId());
            }
            { //ids for lookup
                Map<KodeImplId<?>, String> map = stringMapMap.get(kode.getId().getClass());
                if (map == null) {
                    map = new HashMap<KodeImplId<?>, String>();
                    stringMapMap.put(kode.getId().getClass(), map);
                }
                map.put(kode.getId(), kode.getKodeverdi());
            }
        }
    }

    public static KodeIdLookup buildFromKodeliste(Collection<? extends Kodeliste> kodelisteCollection) {
        ArrayList<KodeImpl> kodes = new ArrayList<KodeImpl>();
        for (Kodeliste kodeliste : kodelisteCollection) {
            kodes.addAll(kodeliste.getKoder());
        }
        return buildFromKode(kodes);
    }

    public static KodeIdLookup buildFromKode(Collection<? extends KodeImpl> kodeCollection) {
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
    public <I extends KodeImplId<?>> I fromKodeVerdi(Class<I> kodeIdClass, String kodeVerdi) {
        I kodeId = null;
        Map<String, KodeImplId<?>> bubbleKodeIdMap = idMapMap.get(kodeIdClass);
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
    public <I extends KodeImplId<?>> String fromKodeId(I kodeId) {
        String verdi;
        Map<KodeImplId<?>, String> stringMap = stringMapMap.get(kodeId.getClass());
        if (stringMap != null) {
            return stringMap.get(kodeId);
        } else {
            throw new ImplementationException("Fant ikke kode-klasse " + kodeId);
        }
    }
}
