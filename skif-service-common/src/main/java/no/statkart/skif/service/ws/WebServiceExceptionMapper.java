package no.statkart.skif.service.ws;

import jakarta.xml.ws.WebServiceException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

public interface WebServiceExceptionMapper {
    Optional<Throwable> mapException(WebServiceException e, Map<String, Object> responseContext, Method method, Object[] args);
}
