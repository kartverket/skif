package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.BubbleKodelisteTransfer;
import no.statkart.skif.store.kodelistesupport.BubbleKodeId;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
public class KodelisteTransfer extends BubbleKodelisteTransfer<BubbleKodeId, KodelisteId, Kode> {

    public KodelisteTransfer(Collection<BubbleKodeId> kodeIds, Collection<KodelisteId> kodelisteIds, Collection<Kode> objects) {
        super(kodeIds, kodelisteIds, objects);
    }
}
