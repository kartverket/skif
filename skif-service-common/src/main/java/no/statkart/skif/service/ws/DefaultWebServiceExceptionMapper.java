package no.statkart.skif.service.ws;

import jakarta.inject.Singleton;
import jakarta.xml.ws.BindingProvider;
import jakarta.xml.ws.WebServiceException;
import jakarta.xml.ws.handler.MessageContext;
import no.statkart.skif.exception.InvalidUserException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.exception.PermissionDeniedException;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Optional;

@Singleton
public class DefaultWebServiceExceptionMapper implements WebServiceExceptionMapper {
    @Override
    public Optional<Throwable> mapException(WebServiceException e, Map<String, Object> responseContext, Method method, Object[] args) {
        // Det kastes en intern exceptiontype, men den varierer ut fra implementasjonen. Teksten i den kan jo også
        // endre seg. Sjekker derfor HTTP-statuskoden direkte dersom det kastes en exception i det hele tatt.
        Integer responseCode = (Integer) responseContext.get(MessageContext.HTTP_RESPONSE_CODE);
        if (responseCode != null) {
            String endpoint = (String) responseContext.get(BindingProvider.ENDPOINT_ADDRESS_PROPERTY);
            if (responseCode == 401) {
                return Optional.of(new InvalidUserException("HTTP 401 Unauthorized from " + endpoint, e));
            } else if (responseCode == 403) {
                return Optional.of(new PermissionDeniedException("HTTP 403 Forbidden from " + endpoint, e));
            } else if (responseCode == 404 || responseCode == 502 || responseCode == 503) {
                return Optional.of(new OperationalException("HTTP " + responseCode + " from " + endpoint, e));
            }
        }
        return Optional.empty();
    }
}
