package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public interface CompositeBubbleComponent<T extends BubbleObject> extends CompositeComponent<T>, BubbleComponent<T> {
}
