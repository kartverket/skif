package no.statkart.skif.store;

/**
 * Baseklasse for EntityBubbleComponents som har mutabel ident og som derfor må ha en id for å sjekke på likhet. Og
 * som i tillegg har referanse til owner
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public abstract class AbstractEntityBubbleComponentWithOwner<T  extends BubbleObject> extends AbstractEntityComponent implements EntityBubbleComponent<T>, EntityComponentWithOwnerReferance<T> {
    private static final long serialVersionUID = 1L;
}
