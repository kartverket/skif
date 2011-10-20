package no.statkart.skif.storetest.wsapi.exception;

import javax.xml.ws.WebFault;

/**
 * Se dokumentasjon i {@link ServiceFaultInfo}
 *
 * @author Leif Lislegård
 * @since 1.1
 */
@WebFault(name = "ServiceException", targetNamespace = "http://skif.statkart.no/storetest/wsapi/exception")
public class ServiceException extends Exception {

    /**
     * Java type that goes as soapenv:Fault detail element.
     */
    private ServiceFaultInfo faultInfo;


    /**
     * Empty bean constructor, used by mapping system
     */
    public ServiceException() {
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public ServiceException(String message, ServiceFaultInfo faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * Std constructor in JAX-WS 2.0
     */
    public ServiceException(String message, ServiceFaultInfo faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * Std getter for detail element in JAX-WS 2.0
     */
    public ServiceFaultInfo getFaultInfo() {
        return faultInfo;
    }

    public void setFaultInfo(ServiceFaultInfo faultInfo) {
        this.faultInfo = faultInfo;
    }
}
