package no.statkart.skif.store2;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Henrik Fredholm
 */
public interface Store2 {
    void init();
    void clear();

    <T extends BubbleObject2, I extends BubbleId2<? extends T>> T get(@Nullable I bubbleId);
    <T extends BubbleObject2, I extends BubbleId2<? extends T>> Collection<T> get(Collection<I> bubbleIds);
    <T extends BubbleObject2, I extends BubbleId2<? extends T>> Set<T> get(Set<I> bubbleIds);
    <T extends BubbleObject2, I extends BubbleId2<? extends T>> List<T> get(List<I> bubbleIds);
    <T extends BubbleObject2, I extends BubbleId2<? extends T>> void get(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    <T extends BubbleObject2, I extends BubbleId2<? extends T>> Set<T> getOrdered(Set<I> bubbleIds);
    <T extends BubbleObject2, I extends BubbleId2<? extends T>> List<T> getOrdered(List<I> bubbleIds);
    <T extends BubbleObject2, I extends BubbleId2<? extends T>> void getOrdered(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    <T extends BubbleObject2, I extends BubbleId2<? extends T>> T lock(@Nullable I bubbleId);
    <T extends BubbleObject2, I extends BubbleId2<? extends T>> Collection<T> lock(Collection<I> bubbleIds);
    <T extends BubbleObject2, I extends BubbleId2<? extends T>> Set<T> lock(Set<I> bubbleIds);
    <T extends BubbleObject2, I extends BubbleId2<? extends T>> List<T> lock(List<I> bubbleIds);
    <T extends BubbleObject2, I extends BubbleId2<? extends T>> void lock(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    <T extends BubbleObject2> T register(T bubbleObject);
    <T extends BubbleObject2> Collection<? extends T> register(Collection<? extends T> bubbleObjects, Collection<? super T> resolvedObjects);
    <T extends BubbleObject2> T registerLocked(T bubbleObject);
    <T extends BubbleObject2, I extends BubbleId2<? extends T>> void registerLocked(Collection<T> bubbleObjects, Collection<T> resolvedObjects);
    <T extends BubbleObject2, I extends BubbleId2<? extends T>> void registerTransfer(UnitOfWorkTransfer2<T, I> transfer);

    <T extends BubbleObject2, I extends BubbleId2<? extends T>> boolean isLocked(I bubbleId);

    <T extends BubbleObject2, I extends BubbleId2<? extends T>> boolean evict(I bubbleId);
    <T extends BubbleObject2, I extends BubbleId2<? extends T>> boolean evict(Collection<I> bubbleIds);
    <T extends BubbleObject2, I extends BubbleId2<? extends T>> boolean evictAll();


 /*
    <T extends BubbleObject2> Collection<? extends T> get(Collection<? extends BubbleId2<? extends T>> bubbleIds, MissingObjectStrategy obj);

    <T extends BubbleObject2> T register(T bubbleObject);

    <T extends BubbleObject2, I extends BubbleId2<? extends T>> Map<I, T> register(Map<I, T> bubbleMap);

    <T extends BubbleObject2> Collection<T> register(Collection<T> bubbleObjects);


     void evict(Collection<? extends BubbleId2<?>> bubbleIds);

    void evictAll();

    <T extends BubbleObject2> void pin(T bubbleObject);

    <T extends BubbleObject2> void pin(Collection<? extends T> bubbleObjects);

    void unpin(BubbleId2<?> bubbleId);

    void unpin(Collection<? extends BubbleId2<?>> bubbleIds);
*/
    void startUnitOfWork();
    UnitOfWorkTransfer2 getUnitOfWorkTransfer();
    //void getUnitOfWorkSnapshot();

    void abortUnitOfWork();
    void endUnitOfWork();
    boolean inUnitOfWork();

    <S> S getService(Class<S> serviceClass);

}
