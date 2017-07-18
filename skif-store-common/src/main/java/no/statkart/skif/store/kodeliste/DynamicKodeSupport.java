package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class DynamicKodeSupport<I extends KodeId, KL extends Kodeliste, KLID extends KodelisteId<KL>> implements StaticKodelisteKodeSupport {
    private final Class<I> kodeIdClass;
    private final KLID kodelisteId;
    private final String resourceMsgName;

    public DynamicKodeSupport(Class<I> kodeIdClass, KLID kodelisteId, String resourceMsgName) {
        this.kodeIdClass = kodeIdClass;
        this.kodelisteId = kodelisteId;
        this.resourceMsgName = resourceMsgName;
    }

    public I defineId(Object idValue) {
        return BubbleIds.createInstance(kodeIdClass, idValue, SnapshotVersion.CURRENT);
    }

    public KLID getKodelisteId() {
        return kodelisteId;
    }

    public Class<I> getKodeIdClass() {
        return kodeIdClass;
    }

    @Override
    public String getKodelisteResourceKey() {
        String kodeName = getKodeName();
        return kodeName + ".kodeliste";
    }

    private String getKodeName() {
        return removeLastChars(getKodeIdClass().getSimpleName(), 2);
    }

    private static String removeLastChars(String simpleName, int n) {
        return simpleName.substring(0, simpleName.length() - n);
    }

    @Override
    public String getResourceMsgName() {
        return resourceMsgName;
    }
}
