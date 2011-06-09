package no.statkart.skif.service.ws;


import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import no.statkart.skif.exception.ConfigurationException;

import javax.xml.ws.WebServiceContext;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
public abstract class SkifWebService<T extends ServiceWSI> {
    Class<T> serviceClass;

    protected SkifWebService(Class<T> serviceClass) {
        this.serviceClass = serviceClass;
    }

    private WebServiceImplementationFactory<T> getFactory(Injector injector) {
        TypeLiteral<WebServiceImplementationFactory<T>> factoryType =  (TypeLiteral<WebServiceImplementationFactory<T>>) TypeLiteral.get(Types.newParameterizedType(WebServiceImplementationFactory.class, serviceClass));
        return injector.getInstance(Key.get(factoryType));
    }

    protected T getServiceImplementation(Injector injector, WebServiceContext ctx) {
        System.out.println("SKIF: Creating WebService: " + getClass().getName() + " using injector: " + System.identityHashCode(injector) );
        try {
            if (injector==null) {
                throw new ConfigurationException("Fant ikke injector for "+ getClass().getName() + ": Sjekk at init() metoden setter injector hørende til modulen");
            }
            return getFactory(injector).getService(ctx, this.getClass());
        } catch (RuntimeException e) {
            e.printStackTrace();
            throw e;
        }
    }
}