package no.statkart.skif.store.kodeliste;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class EnumKodeSupport<T extends Kode, I extends KodeId<T>, KL extends Kodeliste, KLID extends KodelisteId<KL>> implements Serializable, StaticKodelisteKodeSupport {
    private static final long serialVersionUID = 1L;

    private final Class<I> kodeIdClass;
    private final KLID kodelisteId;
    private final String resourceMsgName;

    // Bruk av KodeId<?> her og i getKodeResourceKey() er fordi vi ikke har noen compile-time garanti for at T.getId() returnerer I
    private LinkedHashMap<KodeId<?>, T> koder = new LinkedHashMap<KodeId<?>, T>();
    private HashMap<KodeId<?>, String> kodeResourceKeys = new HashMap<KodeId<?>, String>();

    public EnumKodeSupport(Class<I> kodeIdClass, KLID kodelisteId, String resourceMsgName) {
        this.kodeIdClass = kodeIdClass;
        this.kodelisteId = kodelisteId;
        this.resourceMsgName = resourceMsgName;
    }

    public KLID getKodelisteId() {
        return kodelisteId;
    }

    public synchronized void addKode(T kode) {
        if (koder.containsKey(kode.getId())) {
            throw new ImplementationException("Forsøk på å definere samme kode flere ganger: " + kode );
        }
        koder.put(kode.getId(), kode);
    }

    public T defineKode(Object idValue,String kodeResourceKey) {
        I id = BubbleIds.createInstance(kodeIdClass, idValue, SnapshotVersion.CURRENT);
        T kode = id.createTypeInstance();
        kode.setId(id);
        addKode(kode);
        String key = getKodeName() + "." + kodeResourceKey;
        kodeResourceKeys.put(kode.getId(), key);
        return kode;
    }

    public String getKodelisteResourceKey() {
        String kodeName = getKodeName();
        return kodeName + ".kodeliste";
    }

    private String getKodeName() {
        return removeLastChars(getKodeIdClass().getSimpleName(), 2);
    }

    private static String removeLastChars(String simpleName, int n) {
        return simpleName.substring(0, simpleName.length()-n);
    }

    public String getKodeResourceKey(KodeId<?> id) {
        return kodeResourceKeys.get(id);
    }

    public Class<I> getKodeIdClass() {
        return kodeIdClass;
    }

    public LinkedHashMap<KodeId<?>, T> getKoder() {
        return koder;
    }

    public String getResourceMsgName() {
        return resourceMsgName;
    }
}
