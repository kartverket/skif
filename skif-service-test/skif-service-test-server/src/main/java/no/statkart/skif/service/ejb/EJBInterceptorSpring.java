package no.statkart.skif.service.ejb;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.TxMode;
import no.statkart.skif.service.annotation.CallId;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.util.CopyHelper;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.TransactionAttributeType;

public abstract class EJBInterceptorSpring {
    final private static Logger logger = LoggerFactory.getLogger(EJBInterceptorSpring.class);
    protected abstract Injector getInjector();

    public Object aroundService(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Injector injector = getInjector();
        injector.injectMembers(proceedingJoinPoint.getTarget());
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();

        TxMode origTxMode = null;
        try {
            TypeLiteral<EJBAttributesLookup<?>> type = SkifUtil.typeLiteral(EJBAttributesLookup.class, methodSignature.getMethod().getDeclaringClass());
            EJBAttributesLookup<?> ejbAttributesLookup = injector.getInstance(Key.get(type));
            TransactionAttributeType txType = ejbAttributesLookup.lookupAttribute(methodSignature.getMethod());
            ServiceRequestContext serviceRequestContext = injector.getInstance(ServiceRequestContext.class);
            origTxMode = serviceRequestContext.getTxMode();

            if (logger.isDebugEnabled()) {
                logger.debug("BEG - " + origTxMode + "-->" + txType + " " + methodSignature.getMethod());
            }

            if (isNewContextRequired(origTxMode, txType)) {
                return executeInNewContext(injector, proceedingJoinPoint, serviceRequestContext, txType, ejbAttributesLookup.isBeanManagedTransaction());
            } else {
                return executeInExistingContext(injector, proceedingJoinPoint, serviceRequestContext, origTxMode);
            }
        } catch (Exception t) {
            logger.debug("Exception i EJBInterceptor", t);
            throw t;
        } finally {
            if (logger.isDebugEnabled()) {
                if (origTxMode == TxMode.NOT_IN_EJB) {
                    logger.debug("END - {}\n", methodSignature.getMethod());
                } else {
                    logger.debug("END - {}", methodSignature.getMethod());
                }
            }
        }
    }

    private Object executeInNewContext(Injector injector, ProceedingJoinPoint proceedingJoinPoint, ServiceRequestContext serviceRequestContext, TransactionAttributeType txType, boolean isBeanManagedTransaction) throws Throwable {
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
            if (serviceContext != null) {
                serviceRequestScope.seed((Class<ServiceContext>) serviceContext.getClass(), serviceContext);
            }
            return invokeInContext(injector, proceedingJoinPoint);
        } finally {
            serviceRequestScope.exit();
            serviceRequestScope.resumeSuspended();
        }
    }

    private Object executeInExistingContext(Injector injector, ProceedingJoinPoint proceedingJoinPoint, ServiceRequestContext serviceRequestContext, TxMode origTxMode) throws Throwable {
        final TxMode txMode;
        if (origTxMode == TxMode.NO_TX || origTxMode == TxMode.NO_TX_CONTINUATION) {
            txMode = TxMode.NO_TX_CONTINUATION;
        } else if (origTxMode == TxMode.TX || origTxMode == TxMode.TX_CONTINUATION) {
            txMode = TxMode.TX_CONTINUATION;
        } else {
            throw new ImplementationException("TxMode " + origTxMode.name() + " is unsupported for execute in existing context");
        }

        try {
            serviceRequestContext.setTxMode(txMode);
            return invokeInContext(injector, proceedingJoinPoint);
        } finally {
            serviceRequestContext.setTxMode(origTxMode);
        }
    }

    private Object invokeInContext(Injector injector, ProceedingJoinPoint proceedingJoinPoint ) throws Throwable {
        injector.injectMembers(proceedingJoinPoint.getTarget());
        return proceedingJoinPoint.proceed();
    }

    private boolean isNewContextRequired(TxMode origTxMode, TransactionAttributeType txType) {
        return origTxMode == TxMode.NOT_IN_EJB
                || txType == TransactionAttributeType.REQUIRES_NEW
                || (txType == TransactionAttributeType.REQUIRED && (origTxMode == TxMode.NO_TX || origTxMode == TxMode.NO_TX_CONTINUATION));
    }

}
