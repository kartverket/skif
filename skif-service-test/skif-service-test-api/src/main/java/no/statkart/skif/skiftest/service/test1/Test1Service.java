package no.statkart.skif.skiftest.service.test1;

/**
 * Enkel test service som ikke har ServiceContext parameter, kaller andre tjenester eller bruker andre
 * rammeverkskomponenter i implementasjonen og som  bruker en identity mapping2 for å mappe mellom domene og Web Service modell.
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
public interface Test1Service {
    public String helloWorld(String message);
}
