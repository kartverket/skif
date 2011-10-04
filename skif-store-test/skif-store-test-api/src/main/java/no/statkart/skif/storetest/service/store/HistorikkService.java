package no.statkart.skif.storetest.service.store;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

import java.util.List;
import java.util.Map;

/**
 * @author Roar Ingebrigtsen
 * @since 2.5
 */
public interface HistorikkService {

    /**
     * Henter ut en liste av versjoner for <code>id</code> som har endringstidspunkt innenfor tidsgrensene angitt av
     * @param id
     * @param start
     * @param end
     * @param <I>
     * @return
     */
    public <I extends StoreTestBubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end);
    public <I extends StoreTestBubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end);

}
