package no.statkart.skif.skiftest.service.test3;

import no.statkart.skif.skiftest.domain.A;
import no.statkart.skif.skiftest.domain.B;
import no.statkart.skif.skiftest.exception.SimpleException;

/**
 * Enkel test service som bruker en mapper for å mappe mellom domenemodell og Web Service modell, har ServiceContext
 * parameter og bruker ServiceRequestContext i implementasjonen. Servicen kaller ikke andre komponenter.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface Test3Service {

    A b2A(B b);

    String testThrowException(String exceptionClass, String message) throws SimpleException;

}
