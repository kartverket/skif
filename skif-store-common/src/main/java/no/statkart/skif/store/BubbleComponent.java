package no.statkart.skif.store;

/**
 * Interface for komponentobjekter. Har funksjon for å sette hard referanse til rot-objekt i grafen.
 *
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public interface BubbleComponent {
    public void setBubbleObject(BubbleObject bubbleObject);
}
