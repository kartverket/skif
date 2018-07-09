package no.statkart.skif.service.ws;

import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.exception.ImplementationException;

import javax.net.ssl.HostnameVerifier;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceClient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Pool av JAX-WS klienter for en gitt porttype. Implementasjonen antar at Web service endpoint classname kan avledes
 * fra porttypen ved å legge til "WS" på slutten av porttype classname og at context path kan avledes fra Web servicens
 * target namespace.
 * <p>
 * Context path avledes fra target namespace "http://serveraddress/context1/../contextN/service/..." ved å plukke
 * ut "context1/../contextN" som context path.
 * <p>
 * Pooling er nødvendig fordi JAX-WS klienter ikke er trådsikre. Poolet består foreløpig av en enkel ThreadLocal, siden
 * det antas at de trådene som kaller en service er konstante og få.
 */
public class JaxWsServicePool<T> {
    final Class<T> portClass;
    final Service endpoint;
    private HostnameVerifier hostnameVerifier;
    private String webServiceContextPath;

    private final ThreadLocal<T> ports = new ThreadLocal<T>() {
        @Override
        protected T initialValue() {
            T port = endpoint.getPort(portClass);
            BindingProvider bindings = (BindingProvider) port;
            if (hostnameVerifier != null) {
                bindings.getRequestContext().put("com.sun.xml.ws.transport.https.client.hostname.verifier", hostnameVerifier);
            }
            return port;
        }
    };

    public JaxWsServicePool(Class<T> type) {
        this.portClass = type;
        Class<? extends Service> endpointClass = getEndpointClassAddWS(portClass);
        try {
            endpoint = endpointClass.newInstance();
        } catch (InstantiationException e) {
            throw new ImplementationException("Could not instantiate JAX-WS Endpoint class: " + endpointClass, e);
        } catch (IllegalAccessException e) {
            throw new ImplementationException("Could not instantiate JAX-WS Endpoint class: " + endpointClass, e);
        }
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public String getWebServiceContextPath() {
        if (webServiceContextPath==null) {
            webServiceContextPath = getWebServiceContextPath(endpoint.getClass());
        }
        return webServiceContextPath;
    }

    public void setWebServiceContextPath(String webServiceContextPath) {
        this.webServiceContextPath = webServiceContextPath;
    }

    private Class<? extends Service> getEndpointClassReplaceWSIWithWS(Class<T> portClass) {
        String portClassname = portClass.getCanonicalName();
        String endpointClassname = portClassname.replaceFirst("WSI$", "WS");
        if (portClassname.equals(endpointClassname)) {
            throw new ImplementationException("Could not deduce JAX-WS Endpoint class name from port class name:" + portClassname);
        }
        Class<? extends Service> endpointClass;
        try {
            endpointClass = Class.forName(endpointClassname).asSubclass(Service.class);
        } catch (ClassNotFoundException e) {
            throw new ImplementationException("JAX-WS Endpoint class not found: " + endpointClassname);
        }
        return endpointClass;
    }

    private Class<? extends Service> getEndpointClassAddWS(Class<T> portClass) {
        String portClassname = portClass.getCanonicalName();
        String endpointClassname = portClassname + "WS";
        if (portClassname.equals(endpointClassname)) {
            throw new ImplementationException("Could not deduce JAX-WS Endpoint class name from port class name:" + portClassname);
        }
        Class<? extends Service> endpointClass;
        try {
            endpointClass = Class.forName(endpointClassname).asSubclass(Service.class);
        } catch (ClassNotFoundException e) {
            throw new ImplementationException("JAX-WS Endpoint class not found: " + endpointClassname);
        }
        return endpointClass;
    }

    public T get() {
        return ports.get();
    }

    private String getWebServiceContextPath(Class<? extends javax.xml.ws.Service> wsClass) {
        // Matcher http://serveraddress/context1/../contextN/service/...
        // Plukker ut context1/../contextN som gruppe 1
        // Håndtere også tilfellet hvor det ikke er noen "/" etter service
        Pattern pattern = Pattern.compile(".*://[^/]*/(.*)/service(/?.*)?");
        WebServiceClient annotation = wsClass.getAnnotation(WebServiceClient.class);
        if (annotation==null) {
            throw new ConfigurationException("WebService does not have a @WebserviceClient annotation: " + wsClass.getName());
        }
        Matcher m = pattern.matcher(annotation.targetNamespace());
        if (m.matches()) {
            String  contextPath = m.group(1);
            return "/" + contextPath  + "/" + wsClass.getSimpleName();
        }  else {
            throw new ConfigurationException("WebService target namespace does not match expected pattern (http://address/.../service/...): " +  annotation.targetNamespace());
        }
    }

}