package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;

/**
 * Superklasse for Koder.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class Kode extends AbstractBubbleObject {
    private static final long serialVersionUID = 1L;

    private KodelisteId<?> kodelisteId;

    @Override
    public KodeId<?> getId() {
        return (KodeId<?>) super.getId();
    }

    @Override
    public void setId(BubbleId<?> id) {
        super.setId(id);
    }

    public KodelisteId<?> getKodelisteId() {
        if (kodelisteId==null) {
            KodelisteId kodelisteId = KodeId.class.cast(id).getKodelisteId();
            setKodelisteId(kodelisteId.asSnapshotVersion(id));
        }
        return kodelisteId;
    }

    public void setKodelisteId(KodelisteId<?> kodelisteId) {
        this.kodelisteId = kodelisteId;
    }
}
