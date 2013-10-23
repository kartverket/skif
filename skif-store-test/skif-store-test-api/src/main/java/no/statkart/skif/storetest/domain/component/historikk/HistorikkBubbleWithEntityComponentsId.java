package no.statkart.skif.storetest.domain.component.historikk;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistoryId;

/**
 * Id for {@link HistorikkBubbleWithEntityComponents}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class HistorikkBubbleWithEntityComponentsId<T extends HistorikkBubbleWithEntityComponents> extends AbstractStoreTestBubbleWithHistoryId<T> {
    private static final long serialVersionUID = 1L;

    public HistorikkBubbleWithEntityComponentsId(Long value) {
        super(value);
    }

    public HistorikkBubbleWithEntityComponentsId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
