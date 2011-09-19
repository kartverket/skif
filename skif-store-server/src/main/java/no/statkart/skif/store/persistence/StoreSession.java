package no.statkart.skif.store.persistence;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;

import java.util.Collection;

/**
 * Implementerer basal funksjonalitet for å laste bobler fra en underliggende session eller connection. En StoreSession
 * kan styres av en {@link StoreSessionManager} som da ansvar for automatisk å åpne en StoreSession når en service
 * etterspør den og automatisk lukke den når inneværende ServiceRequestContext avsluttes
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreSession<S, T extends BubbleObject, I extends BubbleId<? extends T>> {
    S getWrappedSession();
    T get(I bubbleId);
    Collection<? extends T> get(Collection <? extends I> bubbleIds);

    void evict(I bubbleId);
    void evictAll();

}
