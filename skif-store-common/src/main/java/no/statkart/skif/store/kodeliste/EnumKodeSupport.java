package no.statkart.skif.store.kodeliste;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class EnumKodeSupport<T extends EnumKode, I extends EnumKodeId<T>, KL extends Kodeliste, KLID extends KodelisteId<KL>> {
    private final Class<I> kodeIdClass;
    private final KLID kodelisteId;
    private final String resourceMsgName;
    private LinkedHashMap<EnumKodeId<?>, EnumKode> koder = new LinkedHashMap<EnumKodeId<?>, EnumKode>();
    private HashMap<EnumKodeId<?>, String> kodeResourceKeys = new HashMap<EnumKodeId<?>, String>();

    public EnumKodeSupport(Class<I> kodeIdClass, KLID kodelisteId, String resourceMsgName) {
        this.kodeIdClass = kodeIdClass;
        this.kodelisteId = kodelisteId;
        this.resourceMsgName = resourceMsgName;
    }

    public KLID getKodelisteId() {
        return kodelisteId;
    }

    public synchronized void addKode(EnumKode kode) {
        if (koder.containsKey(kode.getId())) {
            throw new ImplementationException("Forsøk på å definere samme kode flere ganger: " + kode );
        }
        koder.put(kode.getId(), kode);
    }

    public T defineKode(Object idValue, String kodeverdi, String kodeResourceKey) {
        I id = BubbleIds.createInstance(kodeIdClass, idValue, SnapshotVersion.CURRENT);
        T kode = id.createTypeInstance();
        kode.setId(id);
        kode.setKodeverdi(kodeverdi);
        kode.setKodelisteId(kodelisteId);
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

    private String removeLastChars(String simpleName, int n) {
        return simpleName.substring(0, simpleName.length()-2);
    }

    public String getKodeResourceKey(EnumKodeId<?> id) {
        return kodeResourceKeys.get(id);
    }

    public Class<? extends KodeId<?>> getKodeIdClass() {
        return kodeIdClass;
    }

    public LinkedHashMap<EnumKodeId<?>, EnumKode> getKoder() {
        return koder;
    }

    public String getResourceMsgName() {
        return resourceMsgName;
    }
}
