package no.statkart.skif.skiftest.service.logging;

import no.statkart.skif.skiftest.wsapi.exception.ServiceException;

public interface TestWSServiceWithServiceException {
    void simple() throws ServiceException;
}
