package no.statkart.skif.storetest.service.store;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;

import java.util.List;
import java.util.Map;

/**
 * Full doc here
 *
 * @author Henrik Fredholm
 */
public interface StoreService extends no.statkart.skif.store.StoreService {

    /**
     * Full dokumentasjon for tjeneste beskrives her og ikke på superklasse (tror jeg)
     * @param id
     * @param <T>
     * @param <I>
     * @return
     */
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T getObject(I id);
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getObjects(List<I> ids);
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end);
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end);

}