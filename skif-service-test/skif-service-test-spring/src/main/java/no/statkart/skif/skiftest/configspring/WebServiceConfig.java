package no.statkart.skif.skiftest.configspring;

import no.statkart.skif.skiftest.config.SkifTestServerInjectorConfigSpring;
import no.statkart.skif.skiftest.config.SkifTestTxManagementServerInjectorConfigSpring;
import no.statkart.skif.skiftest.wsapi.service.test1.Test1ServiceWSBean;
import no.statkart.skif.skiftest.wsapi.service.test2.Test2ServiceWSBean;
import no.statkart.skif.skiftest.wsapi.service.test3.Test3ServiceWSBean;
import no.statkart.skif.skiftest.wsapi.service.testa.AServiceWSBean;
import no.statkart.skif.skiftest.wsapi.service.testb.BServiceWSBean;
import no.statkart.skif.skiftest.wsapi.service.testc.CServiceWSBean;
import no.statkart.skif.skiftest.wsapi.service.testd.DServiceWSBean;
import no.statkart.skif.skiftest.wsapi.service.testex.TestExServiceWSBean;
import no.statkart.skif.skiftest.wsapi.service.txbmt.BeanManagedTxAServiceWSBean;
import no.statkart.skif.skiftest.wsapi.service.txcascade.ContainerManagedTxCMTCascadeServiceWSBean;
import no.statkart.skif.skiftest.wsapi.service.txcmt.ContainerManagedTxAServiceWSBean;
import org.apache.cxf.Bus;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.transport.servlet.CXFServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;
import org.w3c.dom.Element;

import javax.xml.ws.Endpoint;
import javax.xml.ws.EndpointReference;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.security.Principal;

@Configuration
@Import({SkifTestServerInjectorConfigSpring.class, SkifTestTxManagementServerInjectorConfigSpring.class})
public class WebServiceConfig {
    private final Bus cxfBus;

    public WebServiceConfig(Bus cxfBus) {
        this.cxfBus = cxfBus;
    }

    @Bean
    public ServletRegistrationBean cxfServlet() {
        ServletRegistrationBean<CXFServlet> servletRegistrationBean = new ServletRegistrationBean<>(new CXFServlet(), "/skiftest/wsapi/*");
        servletRegistrationBean.setLoadOnStartup(1);
        return servletRegistrationBean;
    }

    @Bean
    public Endpoint endpointTest1ServiceWSBean(Test1ServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/Test1ServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointTest2ServiceWSBean(Test2ServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/Test2ServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointTest3ServiceWSBean(Test3ServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/Test3ServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointAServiceWSBean(AServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/AServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointBServiceWSBean(BServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/BServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointCServiceWSBean(CServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/CServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointDServiceWSBean(DServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/DServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointTestExServiceWSBean(TestExServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/TestExServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointBeanManagedTxAServiceWSBean(BeanManagedTxAServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/BeanManagedTxAServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointContainerManagedTxAServiceWSBean(ContainerManagedTxAServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/ContainerManagedTxAServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointContainerManagedTxCMTCascadeServiceWSBean(ContainerManagedTxCMTCascadeServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/ContainerManagedTxCMTCascadeServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public WebServiceContext getWebServiceConfig() {
        return new WebServiceContextImpl();
    }
}

class WebServiceContextImpl implements WebServiceContext {
    @Override
    public MessageContext getMessageContext() {
        return null;
    }

    @Override
    public Principal getUserPrincipal() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public EndpointReference getEndpointReference(Element... referenceParameters) {
        return null;
    }

    @Override
    public <T extends EndpointReference> T getEndpointReference(Class<T> clazz, Element... referenceParameters) {
        return null;
    }

}
