package no.statkart.skif.skiftest.wsapi.service.types;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebResult;
import jakarta.jws.WebService;

/**
 * Denne tjenesten er kun her for å teste ut hvordan andre rammeverk takler forskjellige typer. Den benytter ikke SKIF
 * på noen måte.
 */
@WebService(
        name = "TypeService",
        serviceName = "TypeServiceWS",
        targetNamespace = "http://skif.statkart.no/skiftest/wsapi/service/types")
public class TypeServiceWSBean {
    @WebMethod
    @WebResult(targetNamespace = "http://skif.statkart.no/skiftest/wsapi/service/types")
    public byte[] rawBytes(@WebParam(name = "bytes", targetNamespace = "http://skif.statkart.no/skiftest/wsapi/service/types") byte[] bytes) {
        return bytes;
    }
}
