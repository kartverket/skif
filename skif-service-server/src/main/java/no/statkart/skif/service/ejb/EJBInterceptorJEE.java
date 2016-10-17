package no.statkart.skif.service.ejb;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.StopRequest;
import no.statkart.skif.service.TxMode;
import no.statkart.skif.service.annotation.CallId;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.util.CopyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.SessionContext;
import javax.ejb.Timer;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.AroundInvoke;
import javax.interceptor.InvocationContext;
import javax.transaction.TransactionManager;

/**
 * Baseklasse for integrasjon mellom JEE og Guice, samt SKIFs custom scopes.
 */
public abstract class EJBInterceptorJEE {
    private static Logger logger = LoggerFactory.getLogger(EJBInterceptorJEE.class);

    @Resource
    private SessionContext sessionContext;

    @Resource(mappedName = "javax.transaction.TransactionManager")
    private TransactionManager transactionManager;

    /**
     * Prosjektene må implementere denne til å returnere sin server-injector.
     */
    protected abstract Injector getInjector();

    @AroundInvoke
    public Object aroundService(InvocationContext invocationContext) throws Exception {
        Injector injector = getInjector();

        TxMode origTxMode = null;
        try {
            TypeLiteral<EJBAttributesLookup<?>> type = SkifUtil.typeLiteral(EJBAttributesLookup.class, invocationContext.getMethod().getDeclaringClass());
            EJBAttributesLookup<?> ejbAttributesLookup = injector.getInstance(Key.get(type));
            TransactionAttributeType txType = ejbAttributesLookup.lookupAttribute(invocationContext.getMethod());
            ServiceRequestContext serviceRequestContext = injector.getInstance(ServiceRequestContext.class);
            origTxMode = serviceRequestContext.getTxMode();

            if (logger.isDebugEnabled()) {
                logger.debug("BEG - " + origTxMode + "-->" + txType + " " + invocationContext.getMethod());
            }

            if (isNewContextRequired(origTxMode, txType)) {
                return executeInNewContext(injector, invocationContext, serviceRequestContext, txType, ejbAttributesLookup.isBeanManagedTransaction());
            } else {
                return executeInExistingContext(injector, invocationContext, serviceRequestContext, origTxMode, txType);
            }
        } catch (Exception t) {
            logger.debug("Exception i EJBInterceptor", t);
            throw t;
        } finally {
            if (logger.isDebugEnabled()) {
                if (origTxMode == TxMode.NOT_IN_EJB) {
                    logger.debug("END - {}\n", invocationContext.getMethod());
                } else {
                    logger.debug("END - {}", invocationContext.getMethod());
                }
            }
        }
    }

    private TransactionAttributeType getTransactionAttributeType(InvocationContext invocationContext, Injector injector) {
        TypeLiteral<EJBAttributesLookup<?>> type = SkifUtil.typeLiteral(EJBAttributesLookup.class, invocationContext.getMethod().getDeclaringClass());
        EJBAttributesLookup<?> ejbAttributesLookup = injector.getInstance(Key.get(type));
        return ejbAttributesLookup.lookupAttribute(invocationContext.getMethod());
    }

    private Object executeInNewContext(Injector injector, InvocationContext invocationContext, ServiceRequestContext serviceRequestContext, TransactionAttributeType txType, boolean isBeanManagedTransaction) throws Exception {
        final TxMode txMode = (txType == TransactionAttributeType.REQUIRED || txType == TransactionAttributeType.REQUIRES_NEW) ? TxMode.TX : TxMode.NO_TX;
        ServiceRequestContext newServiceRequestContext = new ServiceRequestContext(
                serviceRequestContext,
                injector.getInstance(Key.get(Long.class, CallId.class)),
                txMode,
                isBeanManagedTransaction,
                txType
        );
        ServiceContext serviceContext = CopyHelper.copy(injector.getInstance(ServiceContext.class));

        final ServiceRequestScope serviceRequestScope = injector.getInstance(ServiceRequestScope.class);
        serviceRequestScope.suspend();
        serviceRequestScope.enter();
        try {
            serviceRequestScope.seed(ServiceRequestContext.class, newServiceRequestContext);
            serviceRequestScope.seed(TransactionManager.class, transactionManager);
            if (serviceContext != null) {
                serviceRequestScope.seed((Class<ServiceContext>) serviceContext.getClass(), serviceContext);
            }
            return invokeInContext(injector, invocationContext);
        } finally {
            serviceRequestScope.exit();
            serviceRequestScope.resumeSuspended();
        }
    }

    /**
     * Det finnes ingen tester som bruker denne funksjonaliteten. Det ser heller ikke ut til at det er logisk mulig å nå
     * denne metoden. Har derfor deaktivert den som ustøttet, men lar den ligge i tilfelle det blir bruk for den senere.
     */
    @SuppressWarnings("UnusedParameters")
    private Object executeInExistingContext(Injector injector, InvocationContext invocationContext, ServiceRequestContext serviceRequestContext, TxMode origTxMode, TransactionAttributeType txType) throws Exception {
        /*final TxMode txMode = (txType == TransactionAttributeType.REQUIRED) ? TxMode.TX_CONTINUATION : TxMode.NO_TX_CONTINUATION;
        final ServiceRequestContext originalServiceRequestContext = new ServiceRequestContext(serviceRequestContext, origTxMode, false, txType);
        try {
            serviceRequestContext.setTxMode(txMode);
            return invokeInContext(injector, invocationContext);
        } finally {
            serviceRequestContext.setFrom(originalServiceRequestContext);
        }*/
        throw new NotImplementedException("executeInExistingContext");
    }


    private Object invokeInContext(Injector injector, InvocationContext invocationContext) throws Exception {
        injector.injectMembers(invocationContext.getTarget());
//        return invokeWithTimer(invocationContext);
        return invocationContext.proceed();
    }

    private boolean isNewContextRequired(TxMode origTxMode, TransactionAttributeType txType) {
        return origTxMode == TxMode.NOT_IN_EJB
                || txType == TransactionAttributeType.REQUIRES_NEW
                || (txType == TransactionAttributeType.REQUIRED && (origTxMode == TxMode.NO_TX || origTxMode == TxMode.NO_TX_CONTINUATION));
    }


    private Object invokeWithTimer(InvocationContext invocationContext) throws Exception {
        Timer timer = null;
        StopRequest s = StopRequest.create();
        try {
            timer = createTimer(s);
            if (logger.isDebugEnabled()) {
                logger.debug("Created timer for method " + invocationContext.getMethod().getName() + " " + s);
            }
            return invocationContext.proceed();
        } finally {
            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("Canceling timer for " + s);
                }
                if (timer != null) {
                    timer.cancel();
                }
            } catch (NoSuchObjectLocalException e) {
                logger.debug("NoSuchObjectLocalException", e);
                // Ignore, timer has already fired
            }
        }
    }

    private Timer createTimer(StopRequest s) {
        try {
            return sessionContext.getTimerService().createTimer(1000 * 1000, s);
        } catch (RuntimeException e) {
            throw new OperationalException("Could not create timer: " + e.getMessage(), e);
        }
    }


}

