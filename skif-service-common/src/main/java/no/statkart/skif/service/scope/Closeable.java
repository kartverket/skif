package no.statkart.skif.service.scope;

/**
 * Lifecycle interface som kalles på objekter som implementerer det når ServiceRequestScope afsluttes. Kan brukes ifm
 * automatisk lukking av ressurser når scope forlates.
 * TODO: Finne en bedre løsning
 * @author Henrik Fredholm
 * since 2.0
 */
public interface Closeable {
    void close();
}
