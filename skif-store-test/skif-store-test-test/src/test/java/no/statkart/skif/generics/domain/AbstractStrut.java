package no.statkart.skif.generics.domain;

/**
 * Ikke dokumentert
 *
 * @author Leif Lislegård
 * @since 1.0 - sprint 28
 */
public abstract class AbstractStrut<I extends AbstractStrutId<?,?>> implements Strut<I> {

    protected I id;

}
