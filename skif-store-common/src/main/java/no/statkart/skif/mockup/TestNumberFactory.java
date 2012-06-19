package no.statkart.skif.mockup;

import com.google.inject.ImplementedBy;
import com.google.inject.Singleton;

/**
 * Factory for å generere TestNumber objekter. Factory-implementasjon bør være et singleton objekt
 * @author Henrik Fredholm
 * @since 2.1
 */
@ImplementedBy(TestNumberFactoryImpl.class)
public interface TestNumberFactory {
    TestNumber create(int number);
}
