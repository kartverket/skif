package no.statkart.skif.service.ws;


import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import jakarta.xml.ws.WebServiceContext;
import no.statkart.skif.exception.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class SkifWebService<T extends ServiceWSI> {
    private final Class<T> serviceClass;

    protected SkifWebService(Class<T> serviceClass) {
        this.serviceClass = serviceClass;
    }

    private WebServiceImplementationFactory<T> getFactory(Injector injector) {
        TypeLiteral<WebServiceImplementationFactory<T>> factoryType = (TypeLiteral<WebServiceImplementationFactory<T>>) TypeLiteral.get(Types.newParameterizedType(WebServiceImplementationFactory.class, serviceClass));
        return injector.getInstance(Key.get(factoryType));
    }

    protected T getServiceImplementation(Injector injector, WebServiceContext ctx) {
        Logger logger = LoggerFactory.getLogger(SkifWebService.class);

        if (logger.isInfoEnabled()) {
            logger.info("Oppretter WebService: " + getClass().getName() + " vha injector: " + System.identityHashCode(injector));
        }
        try {
            if (injector == null) {
                throw new ConfigurationException("No injector found for " + getClass().getName() + ": Check that the init() methods sets an injector for the module");
            }
            return getFactory(injector).getService(ctx, this.getClass());
        } catch (RuntimeException e) {
            logger.error("Kunne ikke opprette WebService: "+ getClass().getName() + " vha injector: " + System.identityHashCode(injector),e);
            throw e;
        }
    }
}
