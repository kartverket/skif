package no.statkart.skif.store.service;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;

import java.util.Collection;
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
    public <T extends BubbleObject> T getObject(BubbleId<? extends T> id);
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjects(Collection<I> ids);
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getObjectsIgnoreMissing(Collection<I> ids);
    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end);
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end);

    public <T extends BubbleObject> T lock(BubbleId<? extends T> id);
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void unlock(I id);
    public <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I id);
}
