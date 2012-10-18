package no.statkart.skif.store;

/**
 * Interface som angir at objektet er en komponent og at det eiende objektet er en boble
 *
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface BubbleComponent<T extends BubbleObject> extends ComponentWithOwnerReferance<T> {
}
