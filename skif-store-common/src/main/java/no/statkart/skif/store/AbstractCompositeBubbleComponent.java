package no.statkart.skif.store;

/**
 * @author Henrik Fredholm
 */
public abstract class AbstractCompositeBubbleComponent<T extends BubbleObject> extends AbstractCompositeComponent<T,T> implements CompositeBubbleComponent<T> {
    private static final long serialVersionUID = 1L;
}
