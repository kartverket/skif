package no.statkart.skif.store2.persistence;

import java.util.Collection;

/**
 * Implementerer basal funksjonalitet for å laste bobler fra en underliggende session eller connection. En StoreSession
 * kan styres av en {@link no.statkart.skif.store2.persistence.StoreSessionManager2} som da ansvar for automatisk å åpne en StoreSession når en service
 * etterspør den og automatisk lukke den når inneværende ServiceRequestContext avsluttes
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreSession2<S,T,I> {
    S getWrappedSession();
    T get(I bubbleId);
    Collection<? extends T> get(Collection<? extends I> bubbleIds);

    void evict(I bubbleId);
    void evictAll();

}
