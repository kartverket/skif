package no.statkart.skif.store.kodeliste;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.module.common.BubbleIdFactory;

/**
 * Superklasse for Koder.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class Kode extends AbstractBubbleObject {
    private static final long serialVersionUID = 1L;

    private transient KodelisteId<?> kodelisteId;

    @Override
    public KodeId<?> getId() {
        return (KodeId<?>) super.getId();
    }

    @Override
    public void setId(BubbleId<?> id) {
        try {
            String idString = this.getClass().getName() + "Id";
            Class classid = Class.forName(idString);
            if (classid != id.getClass()) {
                Object value = id.getValue();
                BubbleId<?> newBubbleId = (BubbleId<?>) BubbleIdFactory.createInstance(classid, value, id.getSnapshotVersion());
                super.setId(newBubbleId);
            } else {
                super.setId(id);
            }
        } catch (ClassNotFoundException e) {
            throw new ImplementationException("Class " + this.getClass().getName() + "Id was not found in classpath");
        }
    }

    public KodelisteId<?> getKodelisteId() {
        if (kodelisteId == null) {
            KodelisteId<?> kId = KodeId.class.cast(id).getKodelisteId();
            kodelisteId = kId.asSnapshotVersion(id);
        }
        return kodelisteId;
    }

}
