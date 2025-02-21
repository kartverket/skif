package no.statkart.skif.mockup;

import com.google.inject.Singleton;

/**
 * Implementasjon av TestNumberFactory som gir mulighet for å angi hvilket offset som TestNumber objekter skal bruke.
 * Som default brukes {@code offset=10000}, men det kan endres ved eksplisitt å bind en opp egen instans i Guice.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@Singleton
public class TestNumberFactoryImpl implements TestNumberFactory {
    private final int offset;

    public TestNumberFactoryImpl() {
        this(10000);
    }

    public TestNumberFactoryImpl(int offset) {
        this.offset = offset;
    }

    @Override
    public TestNumber create(int number) {
        return new TestNumber(offset, number);
    }

}
