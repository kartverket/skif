package no.statkart.skif.service.ejb;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.PrincipalImpl;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.SingleVmRemoteCallContext;
import no.statkart.skif.service.TxMode;
import no.statkart.skif.service.annotation.CallId;
import no.statkart.skif.service.annotation.EJBServiceChain;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.util.CopyHelper;

import javax.ejb.TransactionAttributeType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * En {@code EJBProxyHandler} for services av type {@code S} som implementerer EJB-container funksjonalitet for
 * servicen  i {@code SINGLE_VM}-mode. Klassen har ansvar for å opprette og initialisere og evt opprette en ny
 * {@code ServiceRequestContext} slik at kallet utføres med ønsket isolasjonsnivå og evt kjøres som en gitt bruker.
 * <p>
 * Kall til {@link #invoke} kan kun utføres når {@link ServiceRequestScope} er aktivt. Ved kall fra klient til server
 * må klienten derfor selv sette opp et initiellt {@code ServiceRequestScope} før kallet utføres og må også seede
 * scopet med et {@link SingleVmRemoteCallContext} objekt som inneholder credentials og andre nødvendige meta data.
 * Ut fra dette vil klassen definere opp et nytt {@code ServiceRequestScope} som kallet vil utføres under.
 * Ved kall fra serveren er det ikke nødvendig å sette opp et nytt {@code ServiceRequestScope} siden et scope da
 * allerede er aktivt.
 * <p>
 * Klassen vil i de fleste tilfeller opprette et nytt {@link ServiceRequestScope} slik at kallet utføres isolert. En
 * unntagelse er nå kallet kommer i kontekst av en annen EJB og ny transaksjon ikke er på krevet. Normalt vil slike
 * kall dog gå uten om denne klassen og utføres direkte på servicens {@code ImplementationServiceChain}.
 * <p>
 * Kaller må også selv sørge for å sette {@code SnapshotVersion} i {@code SnapshotVersionContext} til ønsket verdi
 * for kallet. Dette er løst slik for å gjøre det mulig at kall som ikke trenger ny transaksjon kontekst kan utføres
 * direkte på servicens {@code ImplementationServiceChain}
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
    protected final Provider<Long> callIdProvider;

    @Inject
    public EJBInterceptorSingleVm(Provider<SingleVmRemoteCallContext> singleVmRemoteCallContextProvider, Provider<ServiceRequestContext> serviceRequestContextProvider, Provider<ServiceRequestScope> serviceRequestScopeProvider, Provider<ServiceContext> serviceContextProvider, @EJBServiceChain Provider<S> ejbServiceChainProvider, EJBAttributesLookup<S> ejbAttributesLookup, @CallId Provider<Long> callIdProvider) {
        this.singleVmRemoteCallContextProvider = singleVmRemoteCallContextProvider;
        this.serviceRequestContextProvider = serviceRequestContextProvider;
        this.serviceRequestScopeProvider = serviceRequestScopeProvider;
        this.serviceContextProvider = serviceContextProvider;
        this.ejbServiceChainProvider = ejbServiceChainProvider;
        this.ejbAttributesLookup = ejbAttributesLookup;
        this.callIdProvider = callIdProvider;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        final ServiceRequestContext originalServiceRequestContext = serviceRequestContextProvider.get();
        final TxMode origTxMode = originalServiceRequestContext.getTxMode();
        TransactionAttributeType txType = ejbAttributesLookup.lookupAttribute(method);

        if (isNewContextRequired(origTxMode, txType)) {
            return executeInNewContext(txType, ejbAttributesLookup.isBeanManagedTransaction(),method, args);
        } else {
            return executeInExistingContext(origTxMode, txType, method, args);
        }
    }

    private Object executeInNewContext(TransactionAttributeType txType, boolean beanManagedTransaction, Method method, Object[] args) throws Throwable {
        final TxMode txMode = (txType == TransactionAttributeType.REQUIRED || txType == TransactionAttributeType.REQUIRES_NEW) ? TxMode.TX : TxMode.NO_TX;
        final SingleVmRemoteCallContext singleVmRemoteCallContext = singleVmRemoteCallContextProvider.get();
        final Map<String, Object> contextData = singleVmRemoteCallContext.getContextData();
        ServiceContext serviceContext;
        ServiceRequestContext serviceRequestContext;
        if (isRemoteCall(contextData)) {
            serviceContext = (ServiceContext) contextData.get("serviceContext");
            if (serviceContext == null) {
                serviceContext = CopyHelper.copy(serviceContextProvider.get());
            }
            serviceRequestContext = new ServiceRequestContext(txMode, beanManagedTransaction, txType);
            LoginUser loginUser = (LoginUser) contextData.get("credentials");
            final PrincipalImpl callerPrincipal = (loginUser==null) ? new PrincipalImpl(null) :  new PrincipalImpl(loginUser.getUsername());
            serviceRequestContext.setCallId(callIdProvider.get());
            serviceRequestContext.setCallerPrincipal(callerPrincipal);
            serviceRequestContext.setServicename(method.getName());
        } else {
            ServiceRequestContext oldServiceRequestContext = serviceRequestContextProvider.get();
            serviceRequestContext = new ServiceRequestContext(oldServiceRequestContext, txMode, beanManagedTransaction, txType);
            serviceRequestContext.incNestedLevel();
            serviceRequestContext.setCallId(callIdProvider.get());
            serviceRequestContext.setParentCallId(oldServiceRequestContext.getCallId());
            serviceContext = CopyHelper.copy(serviceContextProvider.get());
        }

        final ServiceRequestScope serviceRequestScope = serviceRequestScopeProvider.get();
        serviceRequestScope.suspend();
        serviceRequestScope.enter();
        try {
            serviceRequestScope.seed(ServiceRequestContext.class, serviceRequestContext);
            if (serviceContext != null) {
                serviceRequestScope.seed((Class<ServiceContext>)serviceContext.getClass(), serviceContext);
//                serviceRequestScope.seed(ServiceContext.class, serviceContext);
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
            //noinspection UnnecessaryLocalVariable
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
