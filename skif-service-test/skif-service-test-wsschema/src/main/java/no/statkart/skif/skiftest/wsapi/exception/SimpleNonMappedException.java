package no.statkart.skif.skiftest.wsapi.exception;


import javax.xml.ws.WebFault;
import no.statkart.skif.skiftest.wsapi.exception.simple.*;

/**
 * Se dokumentasjon i {@link SimpleNonMappedFaultInfo}
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
@WebFault(name = "SimpleNonMappedException", targetNamespace = "http://skif.statkart.no/skiftest/wsapi/exception")
public class SimpleNonMappedException extends Exception {

    /**
     * Java type that goes as soapenv:Fault detail element.
     */
    private SimpleNonMappedFaultInfo faultInfo;


    /**
     * Empty bean constructor, used by mapping2 system
     */
    public SimpleNonMappedException() {
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public SimpleNonMappedException(String message, SimpleNonMappedFaultInfo faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public SimpleNonMappedException(String message, SimpleNonMappedFaultInfo faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * Std getter for detail element in JAX-WS 2.0
     */
    public SimpleNonMappedFaultInfo getFaultInfo() {
        return faultInfo;
    }

    public void setFaultInfo(SimpleNonMappedFaultInfo faultInfo) {
        this.faultInfo = faultInfo;
    }
}