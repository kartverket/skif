package no.statkart.skif.service.module.common;

import com.google.inject.Binder;
import com.google.inject.PrivateBinder;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.service.chain.ClientCallServiceChainFactoryJEE;
import no.statkart.skif.service.provider.ServiceProvider;
import no.statkart.skif.service.proxy.D2WAdapterProxyHandler;
import no.statkart.skif.service.proxy.D2WAdapterWithServiceContextMapperProxyHandler;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;
import no.statkart.skif.service.ws.JaxWsServiceProvider;
import no.statkart.skif.service.ws.JaxWsServiceWithDynamicRequestContextProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public class RemoteServiceModuleStrategyJEE extends RemoteServiceModuleStrategy {
    public RemoteServiceModuleStrategyJEE() {
        setCallServiceChainFactoryClass(ClientCallServiceChainFactoryJEE.class);
    }

    @Override
    public void requireBindings(Binder binder) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public <S> void bindService(Binder outerBinder, PrivateBinder innerBinder,  Class<S> service) {
        Class<?> webServiceClass = findWebServicePortClass(service);
        bindService(outerBinder, innerBinder, service, webServiceClass);
    }

    protected <S> Class<?> findWebServicePortClass(Class<S> serviceClass) {
        Class<?> webServiceClass = null;
        String serviceClassname = serviceClass.getName();

        List<String> mapingsTried = new ArrayList<String>();
        List<String> classNamesTried = new ArrayList<String>();

        for (String classPackageMapping : classWSPackageMappings) {
            final String[] mapping = classPackageMapping.split(":");
            if (mapping.length != 2) {
                throw new ConfigurationException("Error in Web Service classmapping. Expected format \"fromPackage:toPackage\":" + classPackageMapping);
            }
            final String fromPackage = mapping[0];
            final String toPackage = mapping[1];
            final Matcher matcher = Pattern.compile(Matcher.quoteReplacement(fromPackage)).matcher(serviceClassname);
            String webServiceClassname = matcher.replaceFirst(toPackage);
            if (serviceClassname.equals(webServiceClassname)) {
                // Not mapping2
                webServiceClassname = "<mapping2-not-applicable>";
            }
            try {
                webServiceClass = Class.forName(webServiceClassname);
                break; // found class
            } catch (ClassNotFoundException e) {
                mapingsTried.add(classPackageMapping);
                classNamesTried.add(webServiceClassname);
                // Ignore
            }
        }
        if (webServiceClass == null) {
            throw new ConfigurationException("Could not find Web Service port class for service: " + serviceClass.getName() + " using packagemappings: " + mapingsTried + ". The following Web Service port classnames where tried: " + classNamesTried);
        }
        return webServiceClass;
    }

    protected <S,W> void bindService(Binder outerBinder, PrivateBinder innerBinder, Class<S> service, Class<W> webService) {
        TypeLiteral<ServiceProvider<S>> remoteServiceProviderType = SkifUtil.typeLiteral(ServiceProvider.class, service);
        TypeLiteral<TerminatingProxyHandler<S>> terminatingProxyHandlerType = SkifUtil.typeLiteral(TerminatingProxyHandler.class, service);
        TypeLiteral<D2WAdapterProxyHandler<S,W>> d2WAdapterProxyHandlerType = SkifUtil.typeLiteral(D2WAdapterWithServiceContextMapperProxyHandler.class, service, webService);
        TypeLiteral<JaxWsServiceProvider<W>> jaxWsServiceProviderType = SkifUtil.typeLiteral(JaxWsServiceWithDynamicRequestContextProvider.class, webService);

        outerBinder.bind(service).toProvider(remoteServiceProviderType);
        outerBinder.bind(webService).toProvider(jaxWsServiceProviderType);
        innerBinder.bind(terminatingProxyHandlerType).to(d2WAdapterProxyHandlerType) ;
        innerBinder.expose(terminatingProxyHandlerType);
    }
}
