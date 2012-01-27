package no.statkart.skif.service.test;

/**
 * Service for å finne ut hvilket testsettnummer som er det neste.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public interface TestNumberService {

    /**
     * Finner neste ledige testnummer.
     *
     * @return neste testnummer
     */
    public int getNextTestNumber();
}
