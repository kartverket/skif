package no.statkart.skif.store;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface BubbleComponentWithExplicitId<T extends BubbleObject> extends ComponentWithExplicitId, BubbleComponent<T> {
}
