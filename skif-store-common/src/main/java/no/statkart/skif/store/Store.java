package no.statkart.skif.store;

import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Henrik Fredholm
 */
public interface Store {
    void clear();
    <T extends BubbleObject, I extends BubbleId<? extends T>> T get(@Nullable I bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> get(Collection<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> get(Set<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> get(List<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> void get(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> getOrdered(Collection<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getOrdered(Set<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getOrdered(List<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> void getOrdered(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    <T extends BubbleObject, I extends BubbleId<? extends T>> T lock(@Nullable I bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lock(Collection<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> lock(Set<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> lock(List<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> void lock(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    <T extends BubbleObject, I extends BubbleId<? extends T>> void unlock(@Nullable I bubbleId);

    <T extends BubbleObject, I extends BubbleId<? extends T>> void register(BubbleTransfer transfer);
    <T extends BubbleObject, I extends BubbleId<? extends T>> void registerTransfer(UnitOfWorkTransfer transfer);

    <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I bubbleId);

    <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(I bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(Collection<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictAll();

    public <I extends BubbleId<?>> List<I> getVersions(I id, SnapshotVersion start, SnapshotVersion end);
    public <I extends BubbleId<?>> Map<I, List<I>> getVersionsForList(List<I> ids, SnapshotVersion start, SnapshotVersion end);

    public <T extends BubbleObject> void insert(T bubbleObject);
    public <T extends BubbleObject> void update(T bubbleObject);
    public <T extends BubbleObject> void delete(T bubbleObject);
    public <T extends BubbleObject> void ensureFullyLoaded(T bubbleObject);



 /*

    <T extends BubbleObject> void pin(T bubbleObject);

    <T extends BubbleObject> void pin(Collection<? extends T> bubbleObjects);

    void unpin(BubbleId<?> bubbleId);

    void unpin(Collection<? extends BubbleId<?>> bubbleIds);
*/

    void beginUnitOfWork();
    void commitUnitOfWork();
    void abortUnitOfWork();
    UnitOfWorkTransfer getUnitOfWorkTransfer();
    void endUnitOfWork();
    //void getUnitOfWorkSnapshot();
    boolean inUnitOfWork();

    <S> S getInstance(Class<S> serviceClass);
}
