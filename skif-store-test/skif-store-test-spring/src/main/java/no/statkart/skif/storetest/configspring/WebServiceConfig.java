package no.statkart.skif.storetest.configspring;

import no.statkart.skif.storetest.config.StoreTestServerInjectorConfigSpring;
import no.statkart.skif.storetest.wsapi.service.domain.relation.uni.comp.X2AAWithEntityComponentFinderServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.domain.relation.uni.direct.X1AAFinderServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.endringslogg.EndringsloggServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.exceptiontest.ExceptionTestServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.histtest.HistTestServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.id.SequenceBlockAllocatorServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.kodeliste.KodelisteServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.lock.LockServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.locking.LockingTestServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.nedlastning.NedlastningServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.store.StoreServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.store.StoreUpdateServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.storetest1.StoreTest1ServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.test.TestdataServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.txbmt.BeanManagedTxAServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.txcascade.ContainerManagedTxCMTCascadeServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.txcmt.ContainerManagedTxAServiceWSBean;
import no.statkart.skif.storetest.wsapi.service.uow.UowTestServiceWSBean;
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
@Import({StoreTestServerInjectorConfigSpring.class})
public class WebServiceConfig {
    private final Bus cxfBus;

    public WebServiceConfig(Bus cxfBus) {
        this.cxfBus = cxfBus;
    }

    @Bean
    public ServletRegistrationBean cxfServlet() {
        ServletRegistrationBean<CXFServlet> servletRegistrationBean = new ServletRegistrationBean<>(new CXFServlet(), "/storetest/wsapi/*");
        servletRegistrationBean.setLoadOnStartup(1);
        return servletRegistrationBean;
    }

    @Bean
    public Endpoint endpointX2AAWithEntityComponentFinderServiceWSBean(X2AAWithEntityComponentFinderServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/X2AAWithEntityComponentFinderServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointX1AAFinderServiceWSBean(X1AAFinderServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/X1AAFinderServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointEndringsloggServiceWSBean(EndringsloggServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/EndringsloggServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointExceptionTestServiceWSBean(ExceptionTestServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/ExceptionTestServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointHistTestServiceWSBean(HistTestServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/HistTestServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointSequenceBlockAllocatorServiceWSBean(SequenceBlockAllocatorServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/SequenceBlockAllocatorServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointKodelisteServiceWSBean(KodelisteServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/KodelisteServiceWS");
        endpoint.publish();
        return endpoint;
    }


    @Bean
    public Endpoint endpointLockServiceWSBean(LockServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/LockServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointLockingTestServiceWSBean(LockingTestServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/LockingTestServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointNedlastningServiceWSBean(NedlastningServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/NedlastningServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointStoreServiceWSBean(StoreServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/StoreServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointStoreUpdateServiceWSBean(StoreUpdateServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/StoreUpdateServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointStoreTest1ServiceWSBean(StoreTest1ServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/StoreTest1ServiceWS");
        endpoint.publish();
        return endpoint;
    }

    @Bean
    public Endpoint endpointTestdataServiceWSBean(TestdataServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/TestdataServiceWS");
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
    public Endpoint endpointContainerManagedTxCMTCascadeServiceWSBean(ContainerManagedTxCMTCascadeServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/ContainerManagedTxCMTCascadeServiceWS");
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
    public Endpoint endpointUowTestServiceWSBean(UowTestServiceWSBean serviceWSBean) {
        EndpointImpl endpoint = new EndpointImpl(cxfBus, serviceWSBean);
        endpoint.setAddress("/UowTestServiceWS");
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
