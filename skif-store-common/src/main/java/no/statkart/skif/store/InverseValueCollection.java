package no.statkart.skif.store;

import java.io.Serializable;
import java.util.Collection;

/**
 * Interface for Collection av inverse values som automatisk genererer change events for vedlikehold av
 * invers relasjon til eiende boble.
 *
 * @author Henrik Fredholm
 * @since 2.6.0
 */
public interface InverseValueCollection<O extends BubbleObject, E> extends Collection<E>, Serializable {
}
