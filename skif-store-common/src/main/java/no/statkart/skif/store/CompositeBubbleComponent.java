package no.statkart.skif.store;

/**
 * Interface som angir at objektet er en CompositeComponent og at det eiende objektet er en boble
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public interface CompositeBubbleComponent<T extends BubbleObject> extends CompositeComponent<T,T>, BubbleComponent<T> {
}
