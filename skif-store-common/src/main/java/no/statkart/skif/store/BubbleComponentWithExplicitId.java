package no.statkart.skif.store;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface BubbleComponentWithExplicitId<I extends Serializable, T extends BubbleObject> extends ComponentWithExplicitId<I>, BubbleComponent<T> {
}
