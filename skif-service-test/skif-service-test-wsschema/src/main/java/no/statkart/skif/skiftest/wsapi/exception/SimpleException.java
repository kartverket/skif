package no.statkart.skif.skiftest.wsapi.exception;


import javax.xml.ws.WebFault;
import no.statkart.skif.skiftest.wsapi.exception.simple.*;

/**
 * Se dokumentasjon i {@link SimpleFaultInfo}
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
@WebFault(name = "SimpleException", targetNamespace = "http://skif.statkart.no/skiftest/wsapi/exception")
public class SimpleException extends java.lang.Exception {

    /**
     * Java type that goes as soapenv:Fault detail element.
     */
    private SimpleFaultInfo faultInfo;


    /**
     * Empty bean constructor, used by mapping2 system
     */
    public SimpleException() {
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public SimpleException(String message, SimpleFaultInfo faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public SimpleException(String message, SimpleFaultInfo faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * Std getter for detail element in JAX-WS 2.0
     */
    public SimpleFaultInfo getFaultInfo() {
        return faultInfo;
    }

    public void setFaultInfo(SimpleFaultInfo faultInfo) {
        this.faultInfo = faultInfo;
    }
}