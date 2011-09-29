package no.statkart.skif.store;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Henrik Fredholm
 */
public interface Store {
    void init();
    void clear();

    <T extends BubbleObject, I extends BubbleId<? extends T>> T get(@Nullable I bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> get(Collection<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> get(Set<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> get(List<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> void get(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> getOrdered(Set<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> getOrdered(List<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> void getOrdered(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    <T extends BubbleObject, I extends BubbleId<? extends T>> T lock(@Nullable I bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<T> lock(Collection<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> lock(Set<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> List<T> lock(List<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> void lock(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    <T extends BubbleObject> T register(T bubbleObject);
    <T extends BubbleObject> Collection<? extends T> register(Collection<? extends T> bubbleObjects, Collection<? super T> resolvedObjects);
    <T extends BubbleObject> T registerLocked(T bubbleObject);
    <T extends BubbleObject, I extends BubbleId<? extends T>> void registerLocked(Collection<T> bubbleObjects, Collection<T> resolvedObjects);
    <T extends BubbleObject, I extends BubbleId<? extends T>> void registerTransfer(UnitOfWorkTransfer<T, I> transfer);

    <T extends BubbleObject, I extends BubbleId<? extends T>> boolean isLocked(I bubbleId);

    <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(I bubbleId);
    <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evict(Collection<I> bubbleIds);
    <T extends BubbleObject, I extends BubbleId<? extends T>> boolean evictAll();


 /*
    <T extends BubbleObject> Collection<? extends T> get(Collection<? extends BubbleId<? extends T>> bubbleIds, MissingObjectStrategy obj);

    <T extends BubbleObject> T register(T bubbleObject);

    <T extends BubbleObject, I extends BubbleId<? extends T>> Map<I, T> register(Map<I, T> bubbleMap);

    <T extends BubbleObject> Collection<T> register(Collection<T> bubbleObjects);


     void evict(Collection<? extends BubbleId<?>> bubbleIds);

    void evictAll();

    <T extends BubbleObject> void pin(T bubbleObject);

    <T extends BubbleObject> void pin(Collection<? extends T> bubbleObjects);

    void unpin(BubbleId<?> bubbleId);

    void unpin(Collection<? extends BubbleId<?>> bubbleIds);
*/
    void startUnitOfWork();
    UnitOfWorkTransfer getUnitOfWorkTransfer();
    //void getUnitOfWorkSnapshot();

    void abortUnitOfWork();
    void endUnitOfWork();
    boolean inUnitOfWork();

    <S> S getService(Class<S> serviceClass);

}
