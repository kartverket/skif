package no.statkart.skif.store;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface BubbleEntityComponent<I extends Serializable, T extends BubbleObject> extends EntityComponent<I>, BubbleComponent<T> {
}
