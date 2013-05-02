package no.statkart.skif.service.ws;

import com.sun.xml.ws.developer.JAXWSProperties;
import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.exception.ImplementationException;

import javax.net.ssl.HostnameVerifier;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceClient;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builder for å opprette en JaxWS Web Service client for en gitt porttype. Implementasjonen antar
 * at Web service endpoint classname kan avledes fra porttypen ved å legge til "WS" på slutten av porttype classname og
 * og at context path kan avledes fra Web servicens target namespace.
 * <p>
 * Context path avledes fra target namespace "http://serveraddress/context1/../contextN/service/..." ved å plukke
 * ut "context1/../contextN" som context path.
 *
 * @author Henrik Fredholm
 */
public class JaxWsServiceBuilder<T> {
    final Class<T> portClass;
    final Class<? extends Service> endpointClass;
    private String username;
    private String password;
    private String serverUrl;
    private HostnameVerifier hostnameVerifier;
    private String webServiceContextPath;

    public JaxWsServiceBuilder(Class<T> type) {
        this.portClass = type;
        this.endpointClass = getEndpointClassAddWS(portClass);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public void setHostnameVerifier(HostnameVerifier hostnameVerifier) {
        this.hostnameVerifier = hostnameVerifier;
    }

    public String getWebServiceContextPath() {
        if (webServiceContextPath==null) {
            webServiceContextPath = getWebServiceContextPath(endpointClass);
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
            endpointClass = (Class<? extends Service>) Class.forName(endpointClassname);
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
            endpointClass = (Class<? extends Service>) Class.forName(endpointClassname);
        } catch (ClassNotFoundException e) {
            throw new ImplementationException("JAX-WS Endpoint class not found: " + endpointClassname);
        }
        return endpointClass;
    }

    private Service createEndpoint() {
        try {
            return endpointClass.newInstance();
        } catch (InstantiationException e) {
            throw new ImplementationException("Could not instantiate JAX-WS Endpoint class: " + endpointClass, e);
        } catch (IllegalAccessException e) {
            throw new ImplementationException("Could not instantiate JAX-WS Endpoint class: " + endpointClass, e);
        }
    }



    public T build() {
        Service endpoint = createEndpoint();
        T port = endpoint.getPort(portClass);
        BindingProvider bindings = (BindingProvider) port;
        if (username != null) {
            bindings.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, username);
            bindings.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, password);
        }
        if (serverUrl!=null) {
            String serviceEndpointUrl = serverUrl + getWebServiceContextPath();
            bindings.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, serviceEndpointUrl);
        }
        if (hostnameVerifier != null) {
            bindings.getRequestContext().put(JAXWSProperties.HOSTNAME_VERIFIER, hostnameVerifier);
        }
        return port;
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