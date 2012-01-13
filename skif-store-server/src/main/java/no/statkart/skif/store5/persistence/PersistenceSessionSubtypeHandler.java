package no.statkart.skif.store5.persistence;

import no.statkart.skif.store.BubbleId;

/**
 * @author Henrik Fredholm
 */
public interface PersistenceSessionSubtypeHandler extends PersistenceSessionForSnapshot {
    boolean acceptsSubtype(Class<? extends BubbleId> type);
}
