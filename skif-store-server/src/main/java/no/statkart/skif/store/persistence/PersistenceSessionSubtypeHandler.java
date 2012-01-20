package no.statkart.skif.store.persistence;

import no.statkart.skif.store.BubbleId;

/**
 * @author Henrik Fredholm
 */
public interface PersistenceSessionSubtypeHandler extends PersistenceSessionForSnapshot {
    boolean acceptsSubtype(Class<? extends BubbleId> type);
}
