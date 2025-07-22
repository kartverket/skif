package no.statkart.skif.service.proxy;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.SkifException;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.SingleVmRemoteCallContext;
import no.statkart.skif.service.SingleVmServer;
import no.statkart.skif.service.ejb.EJBCallProxyHandler;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.util.CopyHelper;

import java.lang.reflect.Method;
import java.util.HashMap;


/**
 * En abstract {@code ProxyHandler} som i {@code SINGLE_VM}-mode simulerer remote kall fra klient til server {@code singleVmServer}
 * i for service av type {@code <S>}. {@code ProxyHandler}en henter ut en {@code EJBProxyHandler<S>} fra serveren og
 * sender kall videre til denne.
 * <p>
 * I forkant av hvert kall til serveren henter ProxyHandleren ut {@code UserLogin}, og evt {@code ServiceContext}
 * hvis kallet krever det, fra klients injector. Disse data legges inn i et {@link SingleVmRemoteCallContext}-objekt
 * som overføres til serveren uten om selve service-kallet ved å binde objektet i serverens injector med
 * scope {@link ServiceRequestScope}. Det er nødvendig å opprette et ServiceRequestScope for å gjøre kall
 * til serveren slik at kallet utføres trådsikker siden flere klienter kan kalle serveren samtidig.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class SingleVmRemoteCallProxyHandler<S> extends TerminatingProxyHandler<S> {
    protected final TypeLiteral<S> serviceType;
    protected final SingleVmServer singleVmServer;
    protected final ServiceRequestScope serviceRequestScope;
    protected final Provider<EJBCallProxyHandler<S>> singleVmServerEJBProxyHandlerProvider;
    protected final LoginUserHolder loginUserHolder;


    public SingleVmRemoteCallProxyHandler(TypeLiteral<S> serviceType, SingleVmServer singleVmServer, LoginUserHolder loginUserHolder) {
        this.serviceType = serviceType;
        this.singleVmServer = singleVmServer;
        this.loginUserHolder = loginUserHolder;
        this.serviceRequestScope = singleVmServer.getInjector().getInstance(ServiceRequestScope.class);
        TypeLiteral<EJBCallProxyHandler<S>> ejbProxyHandlerType = SkifUtil.typeLiteral(EJBCallProxyHandler.class, serviceType.getType());
        this.singleVmServerEJBProxyHandlerProvider = singleVmServer.getInjector().getProvider(Key.get(ejbProxyHandlerType));
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {

        serviceRequestScope.enter();
        try {
            serviceRequestScope.seed(SingleVmRemoteCallContext.class, createSingleVmRemoteCallcontext());
            final EJBCallProxyHandler<S> ejbCallProxyHandler = singleVmServerEJBProxyHandlerProvider.get();
            args = copyArgs(args);
            Object result = ejbCallProxyHandler.invoke(proxy, method, args);
            return CopyHelper.copy(result);
        } catch (Exception e) {
            if (e instanceof SkifException || e.getClass().getName().equals("no.statkart.skif.util.testsupport.SkifServerTestCaseTestException")) {
                throw e;
            } else {
                throw new ImplementationException(e.getMessage(), e);
            }
        } finally {
            serviceRequestScope.exit();
        }
    }

    /**
     * Serialiserer argumenter før de sendes til server. Hvis argumentet er av type {@code
     * RunOnServerMethod} så serialiseres argumentet. Forsøk på å serialisere RunOnServerMethod
     * vil føre til en masse problemer siden objektet ofte er implementert som en anonym klasse og vil
     * har peker til et outer objekt (testcasen) som ikke kan serialiseres.
     * @param args
     * @return
     */
    private Object[] copyArgs(Object[] args) {
        if (args!=null && args.length==1 && args[0] instanceof RunOnServerMethod) {
            return args;
        } else {
            return CopyHelper.copy(args);
        }
    }

    protected SingleVmRemoteCallContext createSingleVmRemoteCallcontext() {
        final HashMap<String, Object> contextData = new HashMap<String, Object>();
        contextData.put("credentials", loginUserHolder.get());
        SingleVmRemoteCallContext svmContext = new SingleVmRemoteCallContext(contextData);
        return svmContext;
    }

}
