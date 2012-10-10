package no.statkart.skif.store;

import java.io.Serializable;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface BubbleValueComponent<T extends BubbleObject> extends ValueComponent, BubbleComponent<T> {
}
