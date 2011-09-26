package no.statkart.skif.store2;

import no.statkart.skif.store.kodelistesupport.BubbleKode;
import no.statkart.skif.store.kodelistesupport.BubbleKodeId;
import no.statkart.skif.store.kodelistesupport.BubbleKodelisteId;
import no.statkart.skif.store2.kodelistesupport2.Kode2;
import no.statkart.skif.store2.kodelistesupport2.KodeId2;
import no.statkart.skif.store2.kodelistesupport2.KodelisteId2;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class KodelisteTransfer2<KID extends KodeId2, LID extends KodelisteId2, K extends Kode2> {
    private Collection<? extends KID> kodeIds;
    private Collection<? extends LID> kodelisteIds;
    private Collection<? extends K> objects;

    public KodelisteTransfer2(Collection<? extends KID> kodeIds, Collection<? extends LID> kodelisteIds, Collection<? extends K> objects) {
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
