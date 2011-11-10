package no.statkart.skif.service.ejb;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.service.*;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.util.CopyHelper;
import no.statkart.skif.service.scope.ServiceRequestScope;

import javax.ejb.TransactionAttributeType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * En {@code EJBProxyHandler} for services av type {@code S} som implementerer EJB-container funksjonalitet for
 * servicen  i {@code SINGLE_VM}-mode. Klassen har ansvar for å opprette og initialisere og evt opprette en ny
 * {@code ServiceRequestContext} slik at kallet utføres med ønsket isolasjonsnivå og evt kjøres som en gitt bruker.
 * <p/>
 * Kall til {@link #invoke} kan kun utføres når {@link ServiceRequestScope} er aktivt. Ved kall fra klient til server
 * må klienten derfor selv sette opp et initiellt {@code ServiceRequestScope} før kallet utføres og må også seede
 * scopet med et {@link SingleVmRemoteCallContext} objekt som inneholder credentials og andre nødvendige meta data.
 * Ut fra dette vil klassen definere opp et nytt {@code ServiceRequestScope} som kallet vil utføres under.
 * Ved kall fra serveren er det ikke nødvendig å sette opp et nytt {@code ServiceRequestScope} siden et scope da
 * allerede er aktivt.
 * <p/>
 * Klassen vil i de fleste tilfeller opprette et nytt {@link ServiceRequestScope} slik at kallet utføres isolert. En
 * unntagelse er nå kallet kommer i kontext av en annen EJB og ny transaksjon ikke er på krevet. Normalt vil slike
 * kall dog gå uten om denne klassen og utføres direkte på servicens {@code ImplementationServiceChain}.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class EJBInterceptorSingleVm<S> extends EJBCallProxyHandler<S> {
    protected final Provider<SingleVmRemoteCallContext> singleVmRemoteCallContextProvider;
    protected final Provider<ServiceContext> serviceContextProvider;
    protected final Provider<ServiceRequestContext> serviceRequestContextProvider;
    protected final Provider<ServiceRequestScope> serviceRequestScopeProvider;
    protected final Provider<S> ejbServiceChainProvider;
    protected final EJBAttributesLookup<S> ejbAttributesLookup;


    @Inject
    public EJBInterceptorSingleVm(Provider<SingleVmRemoteCallContext> singleVmRemoteCallContextProvider, Provider<ServiceRequestContext> serviceRequestContextProvider, Provider<ServiceRequestScope> serviceRequestScopeProvider, Provider<ServiceContext> serviceContextProvider, @EJBServiceChain Provider<S> ejbServiceChainProvider, EJBAttributesLookup<S> ejbAttributesLookup) {
        this.singleVmRemoteCallContextProvider = singleVmRemoteCallContextProvider;
        this.serviceRequestContextProvider = serviceRequestContextProvider;
        this.serviceRequestScopeProvider = serviceRequestScopeProvider;
        this.serviceContextProvider = serviceContextProvider;
        this.ejbServiceChainProvider = ejbServiceChainProvider;
        this.ejbAttributesLookup = ejbAttributesLookup;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        final ServiceRequestContext originalServiceRequestContext = serviceRequestContextProvider.get();
        final TxMode origTxMode = originalServiceRequestContext.getTxMode();
        TransactionAttributeType txType = ejbAttributesLookup.lookupAttribute(method);

        if (isNewContextRequired(origTxMode, txType)) {
            return executeInNewContext(origTxMode, txType, ejbAttributesLookup.isBeanManagedTransaction(),method, args);
        } else {
            return executeInExistingContext(origTxMode, txType, method, args);
        }
    }

    private Object executeInNewContext(TxMode origTxMode, TransactionAttributeType txType, boolean beanManagedTransaction, Method method, Object[] args) throws Throwable {
        final TxMode txMode = (txType == TransactionAttributeType.REQUIRED || txType == TransactionAttributeType.REQUIRES_NEW) ? TxMode.TX : TxMode.NO_TX;
        final SingleVmRemoteCallContext singleVmRemoteCallContext = singleVmRemoteCallContextProvider.get();
        final Map<String, Object> contextData = singleVmRemoteCallContext.getContextData();
        ServiceContext serviceContext = null;
        ServiceRequestContext serviceRequestContext;
        if (isRemoteCall(contextData)) {
            serviceContext = (ServiceContext) contextData.get("serviceContext");
            serviceRequestContext = new ServiceRequestContext(txMode, beanManagedTransaction, txType);
            LoginUser loginUser = (LoginUser) contextData.get("credentials");
            serviceRequestContext.setCallerPrincipal(new PrincipalImpl(loginUser.getUsername()));
            serviceRequestContext.setServicename(method.getName());
        } else {
            serviceRequestContext = new ServiceRequestContext(serviceRequestContextProvider.get(), txMode, false, txType);
            serviceContext = CopyHelper.copy(serviceContextProvider.get());
        }

        final ServiceRequestScope serviceRequestScope = serviceRequestScopeProvider.get();
        serviceRequestScope.suspend();
        serviceRequestScope.enter();
        try {
            serviceRequestScope.seed(ServiceRequestContext.class, serviceRequestContext);
            if (serviceContext != null) {
                serviceRequestScope.seed(ServiceContext.class, serviceContext);
            }
            return invokeInContext(method, args);
        } finally {
            serviceRequestScope.exit();
            serviceRequestScope.resumeSuspended();
        }
    }

    private Object executeInExistingContext(TxMode origTxMode, TransactionAttributeType txType, Method method, Object[] args) throws Throwable {
        final TxMode txMode = (txType == TransactionAttributeType.REQUIRED) ? TxMode.TX_CONTINUATION : TxMode.NO_TX_CONTINUATION;
        final ServiceRequestContext serviceRequestContext = serviceRequestContextProvider.get();
        final ServiceRequestContext originalServiceRequestContext = new ServiceRequestContext(serviceRequestContext, origTxMode, false, txType);
        try {
            serviceRequestContext.setTxMode(txMode);
            return invokeInContext(method, args);
        } finally {
            serviceRequestContext.setFrom(originalServiceRequestContext);
        }
    }

    private Object invokeInContext(Method method, Object[] args) throws Throwable {
        try {
            final S ejbServiceChain = ejbServiceChainProvider.get();
            Object result = method.invoke(ejbServiceChain, args);
            return result;
        } catch (InvocationTargetException e) {
            throw e.getTargetException();
        }


    }

    private boolean isNewContextRequired(TxMode origTxMode, TransactionAttributeType txType) {
        return origTxMode == TxMode.NOT_IN_EJB
                || txType == TransactionAttributeType.REQUIRES_NEW
                || (txType == TransactionAttributeType.REQUIRED && (origTxMode == TxMode.NO_TX || origTxMode == TxMode.NO_TX_CONTINUATION));
    }

    private boolean isRemoteCall(Map<String, Object> contextData) {
        return contextData != null;
    }
}
