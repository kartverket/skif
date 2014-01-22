package no.statkart.skif.store;

/**
 * Baseklasse for EntityComponents som har mutabel ident og som derfor må ha en id for å sjekke på likhet. Og som
 * i tillegg har referanse til owner.
 *
 * @author Henrik Fredholm
 * @since 2.3
 */
public abstract class AbstractEntityComponentWithOwner<T> extends AbstractEntityComponent implements EntityComponentWithOwnerReference<T> {
    private static final long serialVersionUID = 1L;
}
