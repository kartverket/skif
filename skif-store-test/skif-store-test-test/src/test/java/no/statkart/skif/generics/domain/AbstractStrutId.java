package no.statkart.skif.generics.domain;


/**
 * Ikke dokumentert
 *
 * @author Leif Lislegård
 * @since 1.0 - sprint 28
 */
public abstract class AbstractStrutId<V, O extends AbstractStrut> implements StrutId<V, O> {
    protected V value;

    protected AbstractStrutId(V value) {
        this.value = value;
    }

}
