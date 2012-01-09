package no.statkart.skif.service.logging;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.logging.Level;

/**
 * Proxy for å kalle direkte til underliggende objekt. Brukes som avsluttende element i en proxykjede.
 *
 * @author Henrik Fredholm
 */
public class ServerLoggingProxyHandler<T> extends ChainedProxyHandler<T> {
    private static Logger logger = LoggerFactory.getLogger(ServerLoggingProxyHandler.class);

    private static Logger usageLogger = LoggerFactory.getLogger(ServerLoggingProxyHandler.class.getName() + ".usage");

    private static Logger errorLogger = LoggerFactory.getLogger(ServerLoggingProxyHandler.class.getName() + ".error");

    private Provider<ServiceRequestContext> serviceContextProvider;


    // Gi hver kald en egen id så det blir enkelere å finde matchende retur output når flere tråde
    // gjøre kald samtidig.
    static long lastUsedCallId;

    private static synchronized long getNextCallId() {
        return ++lastUsedCallId;
    }

    @Inject
    public ServerLoggingProxyHandler(Provider<ServiceRequestContext> serviceContextProvider) {
        this.serviceContextProvider = serviceContextProvider;
    }

    @Override
    protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
        ServiceRequestContext requestContext = serviceContextProvider.get();
        requestContext.setCallId(getNextCallId());
        String nestedLevelString = "";
        final String methodName = method.getName();

        long start = System.currentTimeMillis();

        try {

//            ch.qos.logback.classic.LoggerContext lc = (ch.qos.logback.classic.LoggerContext) LoggerFactory.getILoggerFactory();
//            ch.qos.logback.core.util.StatusPrinter.print(lc);


            if (requestContext.getParentCallId() != 0) {
                nestedLevelString = String.format("[p=%d (%d)]", requestContext.getParentCallId(), requestContext.getNestedLevel());
            }
//            System.out.println("Call [id={}] {} --> service: {} method: {} user: {} tx: {} thread: {}" + new StringBuilder().append(requestContext.getCallId()).append( nestedLevelString).append( requestContext.getServicename()).append( methodName).append( requestContext.getUserName()).append( requestContext.isTransactional()).append( Thread.currentThread().getName()).toString());
            logger.info("Call [id={}] {} --> service: {} method: {} user: {} tx: {} thread: {}",
                    new Object[]{requestContext.getCallId(), nestedLevelString, requestContext.getServicename(), methodName, requestContext.getUserName(), requestContext.isTransactional(), Thread.currentThread().getName()});

            Object result = chained.invoke(proxy, method, args);

            long end = System.currentTimeMillis();
            long duration = end - start;

            logger.error("Call [id={}] {} <-- service: {} method: {} duration: {} ms",
                    new Object[]{requestContext.getCallId(), nestedLevelString, requestContext.getServicename(), methodName, duration});

            // Usage-logging kun på ytterste kall
            if (requestContext.getParentCallId() == 0) {
                usageLogger.info("[id={}] {}.{} user: {}, duration: {} ms",
                        new Object[]{requestContext.getCallId(), requestContext.getServicename(), methodName, requestContext.getUserName(), duration});
            }

            return result;
        } catch (Throwable t) {
            long end = System.currentTimeMillis();
            long duration = end - start;

            logger.info("Call [id={}] {} <-- service: {} method: {} kastet exception: \n{}",
                    new Object[]{requestContext.getCallId(), nestedLevelString, requestContext.getServicename(), methodName, t});

            // Usage- og error-logging kun på ytterste kall
            if (requestContext.getParentCallId() == 0) {
                usageLogger.info("[id={}] {}.{} user: {}, duration: {} ms, kastet exception: {}",
                        new Object[]{requestContext.getCallId(), requestContext.getServicename(), methodName, requestContext.getUserName(), duration, t.toString()});

                // Dessverre er det ingen støtte for både formatering og exceptions. Må bygge tekststrengen manuelt.
                errorLogger.error("[id=" + requestContext.getCallId() + "] " + requestContext.getServicename() + "." + methodName + " kastet exception:", t);
            }

            throw t;
        }
    }

}