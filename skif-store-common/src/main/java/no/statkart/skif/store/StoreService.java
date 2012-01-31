package no.statkart.skif.store;

import java.util.List;
import java.util.Map;

/**
 * @author Henrik Fredholm
 */
public interface StoreService {

    /**
     * Full doc here
     * @param id
     * @param <T>
     * @param <I>
     * @return
     */
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T getObject(I id);
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getObjects(List<I> ids);
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end);
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end);

    public <T extends BubbleObject, I extends BubbleId<? extends T>> T lock(I id);
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void unlock(I id);
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I id);
}
