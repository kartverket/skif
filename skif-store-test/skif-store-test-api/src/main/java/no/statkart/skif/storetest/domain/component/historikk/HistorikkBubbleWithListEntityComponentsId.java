package no.statkart.skif.storetest.domain.component.historikk;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleWithHistoryId;

/**
 * Id for {@link HistorikkBubbleWithListEntityComponents}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public class HistorikkBubbleWithListEntityComponentsId<T extends HistorikkBubbleWithListEntityComponents> extends AbstractStoreTestBubbleWithHistoryId<T> {
    private static final long serialVersionUID = 1L;

    public HistorikkBubbleWithListEntityComponentsId(Long value) {
        super(value);
    }

    public HistorikkBubbleWithListEntityComponentsId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}
