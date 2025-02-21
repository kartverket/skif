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

    private LinkedHashMap<I, T> koder = new LinkedHashMap<>();
    private HashMap<I, String> kodeResourceKeys = new HashMap<>();

    public EnumKodeSupport(Class<I> kodeIdClass, KLID kodelisteId, String resourceMsgName) {
        this.kodeIdClass = kodeIdClass;
        this.kodelisteId = kodelisteId;
        this.resourceMsgName = resourceMsgName;
    }

    public KLID getKodelisteId() {
        return kodelisteId;
    }

    public synchronized void addKode(T kode) {
        I kodeId = kodeIdClass.cast(kode.getId());
        if (koder.containsKey(kodeId)) {
            throw new ImplementationException("Forsøk på å definere samme kode flere ganger: " + kode);
        }
        koder.put(kodeId, kode);
    }

    public T defineKode(Object idValue,String kodeResourceKey) {
        I id = BubbleIds.createInstance(kodeIdClass, idValue, SnapshotVersion.CURRENT);
        T kode = id.createTypeInstance();
        kode.setId(id);
        addKode(kode);
        String key = getKodeName() + "." + kodeResourceKey;
        kodeResourceKeys.put(id, key);
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
        return kodeResourceKeys.get(kodeIdClass.cast(id));
    }

    public Class<I> getKodeIdClass() {
        return kodeIdClass;
    }

    public LinkedHashMap<I, T> getKoder() {
        return koder;
    }

    public String getResourceMsgName() {
        return resourceMsgName;
    }
}
