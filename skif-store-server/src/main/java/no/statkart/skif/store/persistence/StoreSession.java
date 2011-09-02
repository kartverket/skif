package no.statkart.skif.store.persistence;

/**
 * Implementerer basal funksjonalitet for å laste bobler fra en underliggende session eller connection
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreSession<T> {
    T getWrappedSession();
}
