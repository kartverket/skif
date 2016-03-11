package no.statkart.skif.mockup;

import com.google.inject.ImplementedBy;

/**
 * Factory for å generere TestNumber objekter. Factory-implementasjon bør være et singleton objekt
 * @author Henrik Fredholm
 * @since 2.1
 */
@ImplementedBy(TestNumberFactoryImpl.class)
public interface TestNumberFactory {
    TestNumber create(int number);
}
