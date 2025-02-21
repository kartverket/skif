package no.statkart.skif.service.ws;

import jakarta.xml.ws.WebServiceContext;

/**
 * Factory interface for å få tak i service implementasjon for Web Service med interface {@code <T>}.
 * @since 2.0
 * @author Henrik Fredholm
 */
public interface WebServiceImplementationFactory<W> {
    W getService(WebServiceContext ctx, Class webServiceImpl);
}