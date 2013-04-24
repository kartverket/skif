package no.statkart.skif.service.module.server;

import com.google.inject.Binder;
import com.google.inject.PrivateBinder;
import com.google.inject.Singleton;
import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.module.ModuleStrategy;
import no.statkart.skif.service.annotation.WSServiceChain;
import no.statkart.skif.service.chain.*;
import no.statkart.skif.service.ejb.EJBCallProxyHandler;
import no.statkart.skif.service.provider.WSServiceChainProvider;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;
import no.statkart.skif.service.proxy.W2DAdapterWithServiceContextMapperProxyHandler;
import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.service.ws.SkifWSInterceptor;
import no.statkart.skif.service.ws.WebServiceImplementationFactory;
import no.statkart.skif.service.ws.WebServiceImplementationFactoryImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static no.statkart.skif.SkifUtil.typeLiteral;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class WSServerServiceModuleStrategy extends ModuleStrategy {
    private Class<? extends WSServiceChainFactory> wsServiceChainFactoryClassForWSI = WSServiceChainFactoryBase.class;
    private Class<? extends WSServiceChainFactory> wsServiceChainFactoryClassForService = WSServiceChainFactoryBase.class;

    /**
     * Definerer mapping mellom intern og webservice service-klasser.
     * <br />
     * Skal være på formen {@code <intern>:<extern>}
     */
    protected String[] classWSIPackageMappings = {"api:wsapi", "service:wsapi.service"};


    public Class<? extends WSServiceChainFactory> getWsServiceChainFactoryClassForWSI() {
        return wsServiceChainFactoryClassForWSI;
    }

    public WSServerServiceModuleStrategy setWsServiceChainFactoryClassForWSI(Class<? extends WSServiceChainFactory> wsServiceChainFactoryClassForWSI) {
        this.wsServiceChainFactoryClassForWSI = wsServiceChainFactoryClassForWSI;
        return this;
    }

    public Class<? extends WSServiceChainFactory> getWsServiceChainFactoryClassForService() {
        return wsServiceChainFactoryClassForService;
    }

    /**
     * @return {@link #classWSIPackageMappings}
     */
    public WSServerServiceModuleStrategy setWsServiceChainFactoryClassForService(Class<? extends WSServiceChainFactory> wsServiceChainFactoryClassForService) {
        this.wsServiceChainFactoryClassForService = wsServiceChainFactoryClassForService;
        return this;
    }

    /**
     * @see #classWSIPackageMappings
     */
    public WSServerServiceModuleStrategy setClassWSIPackageMappings(String... classWSIPackageMappings) {
        this.classWSIPackageMappings = classWSIPackageMappings;
        return this;
    }

    protected void bindSkifWSInterceptorForService(Binder outerBinder, PrivateBinder innerBinder, Class<? extends Object> service, Class<? extends ServiceWSI> serviceWSIClass) {
        outerBinder.bind(typeLiteral(WebServiceImplementationFactory.class, serviceWSIClass)).to(typeLiteral(WebServiceImplementationFactoryImpl.class, serviceWSIClass));
        outerBinder.bind(typeLiteral(SkifWSInterceptor.class, serviceWSIClass));
    }

    protected void bindWSServiceChainFactoryForService(Binder outerBinder, PrivateBinder innerBinder, Class<? extends Object> serviceClass, Class<? extends ServiceWSI> serviceWSIClass) {
        // W2DAdapterWithServiceContextMapperProxyHandler refererer til Mapping som er bunnet til innerBinder. Må derfor selv bindes i innerBinder
        innerBinder.bind(typeLiteral(TerminatingProxyHandler.class, serviceWSIClass)).annotatedWith(WSServiceChain.class).to(typeLiteral(W2DAdapterWithServiceContextMapperProxyHandler.class, serviceWSIClass, serviceClass));
        // wsServiceChainFactoryClassForWSI refererer til TerminatingProxyHandler som er bunnet til innerBinder i innerBinder
        innerBinder.bind(typeLiteral(WSServiceChainFactory.class, serviceWSIClass)).to(typeLiteral(wsServiceChainFactoryClassForWSI, serviceWSIClass));
        innerBinder.bind(typeLiteral(wsServiceChainFactoryClassForWSI, serviceWSIClass)).in(Singleton.class);
        innerBinder.expose(typeLiteral(WSServiceChainFactory.class, serviceWSIClass));

        outerBinder.bind(typeLiteral(WSServiceChainFactory.class, serviceClass)).to(typeLiteral(wsServiceChainFactoryClassForService, serviceClass));
        outerBinder.bind(typeLiteral(wsServiceChainFactoryClassForService, serviceClass)).in(Singleton.class);
        outerBinder.bind(typeLiteral(TerminatingProxyHandler.class, serviceClass)).annotatedWith(WSServiceChain.class).to(typeLiteral(EJBCallProxyHandler.class, serviceClass));
    }

    protected void bindService(Binder outerBinder, PrivateBinder innerBinder, Class<? extends Object> serviceClass, Class<? extends ServiceWSI> serviceWSIClass) {
        outerBinder.bind(serviceClass).annotatedWith(WSServiceChain.class).toProvider(typeLiteral(WSServiceChainProvider.class, serviceClass));
    }


    protected <S> Class<? extends ServiceWSI> findWSIClass(Class<S> serviceClass, ClassLoader classLoader) {
        Class<? extends ServiceWSI> webServiceClass = null;
        String serviceClassname = serviceClass.getName();

        List<String> mapingsTried = new ArrayList<String>();
        List<String> classNamesTried = new ArrayList<String>();

        for (String classPackageMapping : classWSIPackageMappings) {
            final String[] mapping = classPackageMapping.split(":");
            if (mapping.length != 2) {
                throw new ConfigurationException("Error in Web Service classmapping. Expected format \"fromPackage:toPackage\":" + classPackageMapping);
            }
            final String fromPackage = mapping[0];
            final String toPackage = mapping[1];
            String webServiceClassname = Pattern.compile(Matcher.quoteReplacement(fromPackage)).matcher(serviceClassname).replaceFirst(toPackage) + "WSI";
            try {
                webServiceClass = (Class<? extends ServiceWSI>) Class.forName(webServiceClassname, true, classLoader);
                break; // found class
            } catch (ClassNotFoundException e) {
                mapingsTried.add(classPackageMapping);
                classNamesTried.add(webServiceClassname);
                // Ignore
            }
        }
        if (webServiceClass == null) {
            throw new ConfigurationException("Could not find Web Service class for service: " + serviceClass.getName() + " using packagemappings: " + mapingsTried + ". The following Web Service classes where tried: " + classNamesTried);
        }
        return webServiceClass;
    }

}
