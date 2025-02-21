package no.statkart.skif.generics.domain;

/**
 * Interface for basistype
 *
 * @author Leif Lislegård
 * @since 1.0 - sprint 28
 */
public interface Strut<I extends StrutId<?, ?>> {

    I getId();

    I setId(I strutId);

}
