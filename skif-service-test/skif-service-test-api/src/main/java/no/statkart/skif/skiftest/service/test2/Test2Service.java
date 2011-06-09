package no.statkart.skif.skiftest.service.test2;

import no.statkart.skif.skiftest.domain.A;
import no.statkart.skif.skiftest.domain.B;

/**
 * Enkel test service som bruker en mapper for å mappe mellom domenemodell og Web Service modell, har ServiceContext
 * parameter og bruker ServiceRequestContext i implementasjonen. Servicen kaller ikke andre komponenter.
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
public interface Test2Service {
    public B a2B(A a);
}
