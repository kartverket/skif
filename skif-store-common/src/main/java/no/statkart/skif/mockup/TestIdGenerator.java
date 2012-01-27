package no.statkart.skif.mockup;

import no.statkart.skif.store.BubbleId;

/**
 * Interface for en "service" som lager boble-id-er for et test-sett.
 * <p/>
 * Det er mulig å flere TestIdGeneratorer for et testsett, så lenge de opererer for forskjellige verdityper, f.eks.
 * én TestIdGenerator for id-er med verditype <code>long</code> og én for id-er med verditype <code>String</code>.
 * Typeparameteren <code>T</code> brukes for å skille mellom dem.
 *
 * @param <T> typen som blir brukt som verdi i id-ene
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public interface TestIdGenerator<T> {
    public <I extends BubbleId> I getNextId(TestNumber testNumber, Class<I> idClass);
}
