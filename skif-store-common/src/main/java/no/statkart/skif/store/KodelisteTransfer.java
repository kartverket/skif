package no.statkart.skif.store;

import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.store.kodeliste.KodelisteId;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Transfer klasse for kodelister
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class KodelisteTransfer<I extends KodelisteId> extends BubbleTransfer {
    /** Sortert liste av alle kodelisteIds */
    private List<? extends I> kodelisteIds = new ArrayList<I>();

    public KodelisteTransfer(List<? extends I> kodelisteIds, Collection<? extends BubbleObject>... objects) {
        for (Collection<? extends BubbleObject> objectList : objects) {
            add(objectList);
        }
        this.kodelisteIds = kodelisteIds;
    }

    public List<? extends I> getKodelisteIds() {
        return kodelisteIds;
    }
}
