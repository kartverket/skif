package no.statkart.skif.skiftest.service.logging;

import com.google.inject.util.Providers;
import jakarta.ejb.TransactionAttributeType;
import no.statkart.skif.service.PrincipalImpl;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.TxMode;
import no.statkart.skif.service.logging.DefaultServerCallLogger;
import no.statkart.skif.service.logging.ServerCallLogger;
import no.statkart.skif.service.proxy.EjbLoggingProxyHandler;
import no.statkart.skif.service.proxy.InvokeViaInstanceProxyHandler;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.testng.annotations.Test;

import java.security.Principal;

/**
 * Tester både {@link EjbLoggingProxyHandler} og {@link DefaultServerCallLogger}. Mest den siste, men kun de delene av
 * den som blir kalt fra førstnevnte.
 */
@Test
public class EjbLoggingProxyHandlerTest {
    private final Principal principal1 = new PrincipalImpl("user1");

    public void testSimple() {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext(principal1, 1);
        final Logger logger = mockLogger();
        DefaultServerCallLogger defaultServerCallLogger = createDefaultServerCallLogger(serviceRequestContext, logger);
        EjbLoggingProxyHandlerTestService service = createProxyToMock(defaultServerCallLogger);

        service.simple();

        Mockito.verify(logger).info("Kaller [id=1, owner=0] no.statkart.skif.skiftest.service.logging.EjbLoggingProxyHandlerTestService.simple() [user=user1] [" + Thread.currentThread() + "]");
        Mockito.verify(logger).info(Mockito.matches("<----- \\[id=1\\] tok \\d+ ms"));
        Mockito.verifyNoMoreInteractions(logger);
    }

    public void testWebServiceAktigNoTx() {
        ServiceRequestContext serviceRequestContextWs = new ServiceRequestContext(principal1, 1);
        ServiceRequestContext serviceRequestContextEjb = new ServiceRequestContext(serviceRequestContextWs, 2, TxMode.NO_TX, false, TransactionAttributeType.SUPPORTS);
        final Logger logger = mockLogger();
        DefaultServerCallLogger defaultServerCallLogger = createDefaultServerCallLogger(serviceRequestContextEjb, logger);
        EjbLoggingProxyHandlerTestService service = createProxyToMock(defaultServerCallLogger);

        service.simple();

        Mockito.verifyNoMoreInteractions(logger);
    }

    public void testWebServiceAktigTx() {
        ServiceRequestContext serviceRequestContextWs = new ServiceRequestContext(principal1, 1);
        ServiceRequestContext serviceRequestContextEjb = new ServiceRequestContext(serviceRequestContextWs, 2, TxMode.TX, false, TransactionAttributeType.REQUIRED);
        final Logger logger = mockLogger();
        DefaultServerCallLogger defaultServerCallLogger = createDefaultServerCallLogger(serviceRequestContextEjb, logger);
        EjbLoggingProxyHandlerTestService service = createProxyToMock(defaultServerCallLogger);

        service.simple();

        Mockito.verifyNoMoreInteractions(logger);
    }

    public void testWebServiceAktigBMT() {
        ServiceRequestContext serviceRequestContextWs = new ServiceRequestContext(principal1, 1);
        ServiceRequestContext serviceRequestContextEjb = new ServiceRequestContext(serviceRequestContextWs, 2, TxMode.TX, true, TransactionAttributeType.REQUIRES_NEW);
        final Logger logger = mockLogger();
        DefaultServerCallLogger defaultServerCallLogger = createDefaultServerCallLogger(serviceRequestContextEjb, logger);
        EjbLoggingProxyHandlerTestService service = createProxyToMock(defaultServerCallLogger);

        service.simple();

        Mockito.verifyNoMoreInteractions(logger);
    }

    public void testSingleVmAktigNoTx() {
        ServiceRequestContext serviceRequestContextFake = new ServiceRequestContext(principal1, 1);
        ServiceRequestContext serviceRequestContextEjb = new ServiceRequestContext(serviceRequestContextFake, 2, TxMode.TX, true, TransactionAttributeType.SUPPORTS);
        final Logger logger = mockLogger();
        DefaultServerCallLogger defaultServerCallLogger = createDefaultServerCallLogger(serviceRequestContextEjb, logger);
        EjbLoggingProxyHandlerTestService service = createProxyToMock(defaultServerCallLogger);

        service.simple();

        Mockito.verifyNoMoreInteractions(logger);
    }

    public void testSingleVmAktigTx() {
        ServiceRequestContext serviceRequestContextFake = new ServiceRequestContext(principal1, 1);
        ServiceRequestContext serviceRequestContextEjb = new ServiceRequestContext(serviceRequestContextFake, 2, TxMode.TX, true, TransactionAttributeType.REQUIRED);
        final Logger logger = mockLogger();
        DefaultServerCallLogger defaultServerCallLogger = createDefaultServerCallLogger(serviceRequestContextEjb, logger);
        EjbLoggingProxyHandlerTestService service = createProxyToMock(defaultServerCallLogger);

        service.simple();

        Mockito.verifyNoMoreInteractions(logger);
    }

    public void testSingleVmAktigBMT() {
        ServiceRequestContext serviceRequestContextFake = new ServiceRequestContext(principal1, 1);
        ServiceRequestContext serviceRequestContextEjb = new ServiceRequestContext(serviceRequestContextFake, 2, TxMode.TX, true, TransactionAttributeType.REQUIRES_NEW);
        final Logger logger = mockLogger();
        DefaultServerCallLogger defaultServerCallLogger = createDefaultServerCallLogger(serviceRequestContextEjb, logger);
        EjbLoggingProxyHandlerTestService service = createProxyToMock(defaultServerCallLogger);

        service.simple();

        Mockito.verifyNoMoreInteractions(logger);
    }

    public void testServletAktigNoTx() {
        ServiceRequestContext serviceRequestContextWs = new ServiceRequestContext(principal1, 0);
        ServiceRequestContext serviceRequestContextEjb = new ServiceRequestContext(serviceRequestContextWs, 1, TxMode.NO_TX, false, TransactionAttributeType.SUPPORTS);
        final Logger logger = mockLogger();
        DefaultServerCallLogger defaultServerCallLogger = createDefaultServerCallLogger(serviceRequestContextEjb, logger);
        EjbLoggingProxyHandlerTestService service = createProxyToMock(defaultServerCallLogger);

        service.simple();

        Mockito.verify(logger).info("Kaller [id=1, owner=0] no.statkart.skif.skiftest.service.logging.EjbLoggingProxyHandlerTestService.simple() [user=user1] [" + Thread.currentThread() + "]");
        Mockito.verify(logger).info(Mockito.matches("<----- \\[id=1\\] tok \\d+ ms"));
        Mockito.verifyNoMoreInteractions(logger);
    }

    public void testServletAktigTx() {
        ServiceRequestContext serviceRequestContextWs = new ServiceRequestContext(principal1, 0);
        ServiceRequestContext serviceRequestContextEjb = new ServiceRequestContext(serviceRequestContextWs, 1, TxMode.TX, false, TransactionAttributeType.REQUIRED);
        final Logger logger = mockLogger();
        DefaultServerCallLogger defaultServerCallLogger = createDefaultServerCallLogger(serviceRequestContextEjb, logger);
        EjbLoggingProxyHandlerTestService service = createProxyToMock(defaultServerCallLogger);

        service.simple();

        Mockito.verify(logger).info("Kaller [id=1, owner=0] no.statkart.skif.skiftest.service.logging.EjbLoggingProxyHandlerTestService.simple() [user=user1] [" + Thread.currentThread() + "]");
        Mockito.verify(logger).info(Mockito.matches("<----- \\[id=1\\] tok \\d+ ms"));
        Mockito.verifyNoMoreInteractions(logger);
    }

    public void testServletAktigBMT() {
        ServiceRequestContext serviceRequestContextWs = new ServiceRequestContext(principal1, 0);
        ServiceRequestContext serviceRequestContextEjb = new ServiceRequestContext(serviceRequestContextWs, 1, TxMode.TX, true, TransactionAttributeType.REQUIRES_NEW);
        final Logger logger = mockLogger();
        DefaultServerCallLogger defaultServerCallLogger = createDefaultServerCallLogger(serviceRequestContextEjb, logger);
        EjbLoggingProxyHandlerTestService service = createProxyToMock(defaultServerCallLogger);

        service.simple();

        Mockito.verify(logger).info("Kaller [id=1, owner=0] no.statkart.skif.skiftest.service.logging.EjbLoggingProxyHandlerTestService.simple() [user=user1] [" + Thread.currentThread() + "]");
        Mockito.verify(logger).info(Mockito.matches("<----- \\[id=1\\] tok \\d+ ms"));
        Mockito.verifyNoMoreInteractions(logger);
    }

    public void testNestedTxInNoTx() {
        ServiceRequestContext serviceRequestContextWs = new ServiceRequestContext(principal1, 1);
        ServiceRequestContext serviceRequestContextEjb = new ServiceRequestContext(serviceRequestContextWs, 2, TxMode.NO_TX, false, TransactionAttributeType.SUPPORTS);
        ServiceRequestContext serviceRequestContextEjb2 = new ServiceRequestContext(serviceRequestContextEjb, 3, TxMode.TX, false, TransactionAttributeType.REQUIRED);
        final Logger logger = mockLogger();
        DefaultServerCallLogger defaultServerCallLogger = createDefaultServerCallLogger(serviceRequestContextEjb2, logger);
        EjbLoggingProxyHandlerTestService service = createProxyToMock(defaultServerCallLogger);

        service.simple();

        Mockito.verify(logger).info("Kaller [id=3, owner=1] no.statkart.skif.skiftest.service.logging.EjbLoggingProxyHandlerTestService.simple() [user=user1] [" + Thread.currentThread() + "]");
        Mockito.verify(logger).info(Mockito.matches("<----- \\[id=3\\] tok \\d+ ms"));
        Mockito.verifyNoMoreInteractions(logger);
    }

    public void testNestedTxInTx() {
        ServiceRequestContext serviceRequestContextWs = new ServiceRequestContext(principal1, 1);
        ServiceRequestContext serviceRequestContextEjb = new ServiceRequestContext(serviceRequestContextWs, 2, TxMode.TX, false, TransactionAttributeType.REQUIRED);
        ServiceRequestContext serviceRequestContextEjb2 = new ServiceRequestContext(serviceRequestContextEjb, 3, TxMode.TX, false, TransactionAttributeType.REQUIRES_NEW);
        final Logger logger = mockLogger();
        DefaultServerCallLogger defaultServerCallLogger = createDefaultServerCallLogger(serviceRequestContextEjb2, logger);
        EjbLoggingProxyHandlerTestService service = createProxyToMock(defaultServerCallLogger);

        service.simple();

        Mockito.verify(logger).info("Kaller [id=3, owner=1] no.statkart.skif.skiftest.service.logging.EjbLoggingProxyHandlerTestService.simple() [user=user1] [" + Thread.currentThread() + "]");
        Mockito.verify(logger).info(Mockito.matches("<----- \\[id=3\\] tok \\d+ ms"));
        Mockito.verifyNoMoreInteractions(logger);
    }

    private EjbLoggingProxyHandlerTestService createProxyToMock(ServerCallLogger serverCallLogger) {
        EjbLoggingProxyHandlerTestService mock = Mockito.mock(EjbLoggingProxyHandlerTestService.class);
        Mockito.doNothing().when(mock).simple();

        EjbLoggingProxyHandler<EjbLoggingProxyHandlerTestService> loggingProxyHandler = new EjbLoggingProxyHandler<>(serverCallLogger);
        loggingProxyHandler.setChained(new InvokeViaInstanceProxyHandler<>(mock));
        return loggingProxyHandler.buildProxy(EjbLoggingProxyHandlerTestService.class);
    }

    private DefaultServerCallLogger createDefaultServerCallLogger(final ServiceRequestContext serviceRequestContext, final Logger logger) {
        return new DefaultServerCallLogger(Providers.of(serviceRequestContext)) {
                @Override
                protected Logger getLogger() {
                    return logger;
                }
            };
    }

    private Logger mockLogger() {
        return Mockito.mock(Logger.class);
    }
}
