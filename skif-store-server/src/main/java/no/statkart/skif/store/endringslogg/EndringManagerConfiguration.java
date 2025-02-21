package no.statkart.skif.store.endringslogg;

import no.statkart.skif.store.BubbleObject;

/**
 * Deklarativ konfigurasjon for {@link AbstractEndringManager}.
 *
 * @author Leif Lislegård
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.5.0
 */
public interface EndringManagerConfiguration<E extends AbstractEndring> {
    Class<? extends BubbleObject> getDomainklasse(Class<? extends E> endringklasse);

    Class<? extends BubbleObject> getDomainklasseNullSafe(Class<? extends E> endringklasse);

    Class<? extends E> getEndringsklasse(Class<? extends BubbleObject> domainklasse);

    Class<? extends E> getEndringsklasseNullSafe(Class<? extends BubbleObject> domainklasse);

    Class<? extends E> findEndringClass(Class<? extends BubbleObject> domainklasse);
}
