package no.statkart.skif.service.ejb;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.service.*;
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

/**
 * @author Henrik Fredholm
 */
public abstract class EJBInterceptorJEE {
    private static Logger logger = LoggerFactory.getLogger(EJBInterceptorJEE.class);
    @Resource
    private SessionContext sessionContext;

    protected abstract Injector getInjector();

    @AroundInvoke
    public Object aroundService(InvocationContext invocationContext) throws Exception {
        Injector injector = getInjector();

        try {
            TransactionAttributeType txType = getTransactionAttributeType(invocationContext, injector);
            ServiceRequestContext serviceRequestContext = injector.getInstance(ServiceRequestContext.class);
            final TxMode origTxMode = serviceRequestContext.getTxMode();

            if (isNewContextRequired(origTxMode, txType)) {
                return executeInNewContext(injector, invocationContext, serviceRequestContext , txType);
            } else {
                return executeInExistingContext(injector, invocationContext, serviceRequestContext, origTxMode, txType);
            }
        } catch (Exception t) {
            logger.debug("Exception i EJBInterceptor", t);
            throw t;
        }
    }

    private TransactionAttributeType getTransactionAttributeType(InvocationContext invocationContext, Injector injector) {
        TypeLiteral<EJBAttributesLookup<?>> type = SkifUtil.typeLiteral(EJBAttributesLookup.class, invocationContext.getMethod().getDeclaringClass());
        EJBAttributesLookup<?> ejbAttributesLookup = injector.getInstance(Key.get(type));
        return ejbAttributesLookup.lookupAttribute(invocationContext.getMethod());
    }

    private Object executeInNewContext(Injector injector, InvocationContext invocationContext, ServiceRequestContext serviceRequestContext,  TransactionAttributeType txType) throws Exception {
        final TxMode txMode = (txType == TransactionAttributeType.REQUIRED || txType == TransactionAttributeType.REQUIRES_NEW) ? TxMode.TX : TxMode.NO_TX;
        ServiceRequestContext newServiceRequestContext = new ServiceRequestContext(serviceRequestContext, txMode);
        ServiceContext serviceContext = CopyHelper.copy(injector.getInstance(ServiceContext.class));
        newServiceRequestContext.setCallerPrincipal(sessionContext.getCallerPrincipal());
        newServiceRequestContext.incNestedLevel();
        newServiceRequestContext.setParentCallId(serviceRequestContext.getCallId());

        final ServiceRequestScope serviceRequestScope = injector.getInstance(ServiceRequestScope.class);
        serviceRequestScope.suspend();
        serviceRequestScope.enter();
        try {
            serviceRequestScope.seed(ServiceRequestContext.class, newServiceRequestContext);
            if (serviceContext != null) {
                serviceRequestScope.seed(ServiceContext.class, serviceContext);
            }
            return invokeInContext(injector, invocationContext);
        } finally {
            serviceRequestScope.exit();
            serviceRequestScope.resumeSuspended();
        }
    }

    private Object executeInExistingContext(Injector injector, InvocationContext invocationContext, ServiceRequestContext serviceRequestContext, TxMode origTxMode, TransactionAttributeType txType) throws Exception {
        final TxMode txMode = (txType == TransactionAttributeType.REQUIRED) ? TxMode.TX_CONTINUATION : TxMode.NO_TX_CONTINUATION;
        final ServiceRequestContext originalServiceRequestContext = new ServiceRequestContext(serviceRequestContext, origTxMode);
        try {
            serviceRequestContext.setTxMode(txMode);
            return invokeInContext(injector, invocationContext);
        } finally {
            serviceRequestContext.setFrom(originalServiceRequestContext);
        }
    }


    private Object invokeInContext(Injector injector, InvocationContext invocationContext) throws Exception {
        injector.injectMembers(invocationContext.getTarget());
        return invokeWithTimer(invocationContext);
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
                if (timer!=null) {
                    timer.cancel();
                }
            } catch (NoSuchObjectLocalException e) {
                logger.debug("NoSuchObjectLocalException", e)   ;
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

