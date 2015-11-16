package no.statkart.skif.store;

import no.statkart.skif.store.kodeliste.KodelisteId;

import java.util.List;

/**
 * Transfer klasse for kodelister
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class KodelisteTransfer<I extends KodelisteId> extends BubbleTransfer<List<I>> {
    private static final long serialVersionUID = 1L;

    public KodelisteTransfer(List<I> kodelisteIds, Iterable<? extends BubbleObject> objects) {
        super(kodelisteIds, objects);
    }

    /**
     * Alternativ til {@link #getResult()}.
     */
    public List<I> getKodelisterIds() {
        return getResult();
    }
}
