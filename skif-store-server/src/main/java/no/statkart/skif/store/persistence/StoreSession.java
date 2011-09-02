package no.statkart.skif.store.persistence;

/**
 * Implementerer basal funksjonalitet for å laste bobler fra en underliggende session eller connection. En StoreSession
 * kan styres av en {@link StoreSessionManager} som da ansvar for automatisk å åpne en StoreSession når en service
 * etterspør den og automatisk lukke den når inneværende ServiceRequestContext avsluttes
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreSession<T> {
    T getWrappedSession();
}
