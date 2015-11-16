package no.statkart.skif.service.proxy;

import com.google.inject.Inject;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.SingleVmRemoteCallContext;
import no.statkart.skif.service.SingleVmServer;
import no.statkart.skif.service.chain.WSServiceChainFactory;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.service.ws.ServiceWSI;

import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * En {@code ProxyHandler} som i {@code SINGLE_VM}-mode simulerer remote kall fra klient til server {@code singleVmServer}
 * for webservice av type {@code <T>}. Siden implementasjonen på tjeneren benytter interface {@code <A extends ServiceWSI>},
 * så må kallet også adapteres fra {@code <T>} til {@code <A>}. På tjenersiden sendes kallet videre til proxyhandlerkjeden
 * som er genereres av den oppbundete {@code WSServiceChainFactory<A>}.
 * <p>
 * I forkant av hvert kall til serveren henter ProxyHandleren ut {@code UserLogin} fra klients injector.
 * Disse data legges inn i et {@link no.statkart.skif.service.SingleVmRemoteCallContext}-objekt som overføres til serveren uten om selve
 * service-kallet ved å binde objektet i serverens injector med scope {@link no.statkart.skif.service.scope.ServiceRequestScope}. Det er nødvendig å
 * opprette et ServiceRequestScope for å gjøre kall til serveren slik at kallet utføres trådsikker siden flere klienter
 * kan kalle serveren samtidig.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class SingleVmWSRemoteCallProxyHandler<T, A extends ServiceWSI> extends AdapterProxyHandler<T, A> {
    protected final ServiceRequestScope serviceRequestScope;
    protected final LoginUserHolder loginUserHolder;

    @Inject
    public SingleVmWSRemoteCallProxyHandler(TypeLiteral<A> wsiClass, SingleVmServer singleVmServer, LoginUserHolder loginUserHolder) {
        super((Class<A>) wsiClass.getRawType(), createProxyHandler((Class<A>) wsiClass.getRawType(), singleVmServer)); // <A> skal være en ugenerisk klasse, kan derfor caste vekk det med "? super"
        this.loginUserHolder = loginUserHolder;
        this.serviceRequestScope = singleVmServer.getInjector().getInstance(ServiceRequestScope.class);
    }

    @Override
    public Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        serviceRequestScope.enter();
        try {
            serviceRequestScope.seed(SingleVmRemoteCallContext.class, createSingleVmRemoteCallcontext());
            return super.invokeMethod(proxy, method, args);
        } finally {
            serviceRequestScope.exit();
        }
    }

    protected SingleVmRemoteCallContext createSingleVmRemoteCallcontext() {
        final HashMap<String, Object> contextData = new HashMap<String, Object>();
        contextData.put("credentials", loginUserHolder.get());
        SingleVmRemoteCallContext svmContext = new SingleVmRemoteCallContext(contextData);
        return svmContext;
    }

    /**
     * Finn WSI-klasse på tjeneren som tilsvarer gitt ws-interface på klienten.
     *
     * @param wsClientInterface interface på klientsiden
     * @return WSI på tjenersiden
     */
    public static Class<? extends ServiceWSI> findWSI(Class<?> wsClientInterface) {
        String name = wsClientInterface.getName();
        try {
            final Class<?> clazz;
            if (ServiceWSI.class.isAssignableFrom(wsClientInterface)) {
                clazz = wsClientInterface;
            } else {
                name = name + "WSI";
                try {
                    clazz = Class.forName(name);
                } catch (ClassNotFoundException e) {
                    throw new ImplementationException("Could not find class " + name + " for " + wsClientInterface.getName());
                }
            }
            return clazz.asSubclass(ServiceWSI.class);
        } catch (ClassCastException e) {
            throw new ImplementationException(name + " is not a subclass of ServiceWSI");
        }
    }

    private static <A> ProxyHandler<A> createProxyHandler(Class<A> wsiClass, SingleVmServer singleVmServer) {
        TypeLiteral<WSServiceChainFactory<A>> wsServiceChainFactoryType = SkifUtil.typeLiteral(WSServiceChainFactory.class, wsiClass);
        return singleVmServer.getInjector().getInstance(Key.get(wsServiceChainFactoryType)).createChain();
    }
}
