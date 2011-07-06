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

    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> T get(@Nullable I bubbleId);
    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> Collection<T> get(Collection<I> bubbleIds);
    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> Set<T> get(Set<I> bubbleIds);
    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> List<T> get(List<I> bubbleIds);
    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> void get(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> Set<T> getOrdered(Set<I> bubbleIds);
    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> List<T> getOrdered(List<I> bubbleIds);
    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> void getOrdered(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> T lock(@Nullable I bubbleId);
    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> Collection<T> lock(Collection<I> bubbleIds);
    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> Set<T> lock(Set<I> bubbleIds);
    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> List<T> lock(List<I> bubbleIds);
    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> void lock(Collection<I> bubbleIds, Collection<T> bubbleObjects);

    <T extends AbstractBubbleObject> T register(T bubbleObject);
    <T extends AbstractBubbleObject> Collection<? extends T> register(Collection<? extends T> bubbleObjects, Collection<? super T> resolvedObjects);
    <T extends AbstractBubbleObject> T registerLocked(T bubbleObject);
    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> void registerLocked(Collection<T> bubbleObjects,Collection<T> resolvedObjects );
    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> void registerTransfer(UnitOfWorkTransfer<T,I> transfer);

    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> boolean isLocked(I bubbleId);
    
    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> boolean evict(I bubbleId);
    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> boolean evict(Collection<I> bubbleIds);
    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> boolean evictAll();


 /*
    <T extends AbstractBubbleObject> Collection<? extends T> get(Collection<? extends AbstractBubbleId<? extends T>> bubbleIds, MissingObjectStrategy obj);

    <T extends AbstractBubbleObject> T register(T bubbleObject);

    <T extends AbstractBubbleObject, I extends AbstractBubbleId<? extends T>> Map<I, T> register(Map<I, T> bubbleMap);

    <T extends AbstractBubbleObject> Collection<T> register(Collection<T> bubbleObjects);


     void evict(Collection<? extends AbstractBubbleId<?>> bubbleIds);

    void evictAll();

    <T extends AbstractBubbleObject> void pin(T bubbleObject);

    <T extends AbstractBubbleObject> void pin(Collection<? extends T> bubbleObjects);

    void unpin(AbstractBubbleId<?> bubbleId);

    void unpin(Collection<? extends AbstractBubbleId<?>> bubbleIds);
*/
    void startUnitOfWork();
    UnitOfWorkTransfer getUnitOfWorkTransfer();
    //void getUnitOfWorkSnapshot();

    void abortUnitOfWork();
    void endUnitOfWork();
    boolean inUnitOfWork();

    <S> S getService(Class<S> serviceClass);

}
