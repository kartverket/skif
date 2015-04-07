package no.statkart.skif.store;

import java.io.Serializable;
import java.util.Collection;

/**
 * Interface for Collection av bubble id-er, som automatisk genererer change events for vedlikehold av invers relasjon.
 *
 * @author Henrik Fredholm
 * @since 2.4.0
 */
@Deprecated
public interface BubbleIdCollection<O extends BubbleObject, E extends BubbleId<?>> extends Collection<E>, Serializable {
}
