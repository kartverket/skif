package no.statkart.skif.store;

import no.statkart.skif.store.kodelistesupport.KodeImpl;
import no.statkart.skif.store.kodelistesupport.KodeImplId;
import no.statkart.skif.store.kodelistesupport.KodelisteId;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class KodelisteTransfer<KID extends KodeImplId, LID extends KodelisteId, K extends KodeImpl> {
    private Collection<? extends KID> kodeIds;
    private Collection<? extends LID> kodelisteIds;
    private Collection<? extends K> objects;

    public KodelisteTransfer(Collection<? extends KID> kodeIds, Collection<? extends LID> kodelisteIds, Collection<? extends K> objects) {
        this.kodeIds = kodeIds;
        this.kodelisteIds = kodelisteIds;
        this.objects = objects;
    }

    public Collection<? extends KID> getKodeIds() {
        return kodeIds;
    }

    public Collection<? extends LID> getKodelisteIds() {
        return kodelisteIds;
    }

    public Collection<? extends K> getObjects() {
        return objects;
    }
}
