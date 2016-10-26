package no.statkart.skif.skiftest.service.logging;

import com.google.inject.util.Providers;
import no.statkart.skif.exception.ApplicationException;
import no.statkart.skif.exception.ValidationException;
import no.statkart.skif.service.CallIdProvider;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.PrincipalImpl;
import no.statkart.skif.service.ServiceRequestContext;
import no.statkart.skif.service.logging.ClientCallLogger;
import no.statkart.skif.service.logging.DefaultClientCallLogger;
import no.statkart.skif.service.logging.DefaultServerCallLogger;
import no.statkart.skif.service.logging.ServerCallLogger;
import no.statkart.skif.service.proxy.ClientLoggingProxyHandler;
import no.statkart.skif.service.proxy.InvokeViaInstanceProxyHandler;
import no.statkart.skif.service.proxy.WsLoggingProxyHandler;
import no.statkart.skif.skiftest.wsapi.exception.ServiceException;
import no.statkart.skif.skiftest.wsapi.exception.impl.ServiceFaultInfo;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.security.Principal;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Tester at exceptions i en service produserer den forventede log-outputen.
 */
@Test
public class ExceptionLoggingTest {
    private final Principal principal1 = new PrincipalImpl("user1");

    public void testApplicationExceptionInServiceWithDebugEnabledServerLogger() {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext(principal1, "testSimple", 1);
        ArgumentCaptor<String> strArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Throwable> thrArg = ArgumentCaptor.forClass(Throwable.class);

        final Logger logger = mockWithDebugEnabled();
        DefaultServerCallLogger defaultServerCallLogger = createDefaultServerCallLogger(serviceRequestContext, logger);

        try {
            TestWSServiceWithServiceException service = createProxyToMockWithApplicationException(defaultServerCallLogger);
            service.simple();
            Assert.fail("Skulle feilet med en ValidationException");
        } catch (Exception e) {
            verify(logger).debug(strArg.capture(), thrArg.capture());
            assertThat(strArg.getValue()).contains("application exception");
            assertThat(thrArg.getValue()).isInstanceOf(ApplicationException.class);

        }
    }

    public void testApplicationExceptionInServiceWithInfoEnabledServerLogger() {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext(principal1, "testSimple", 1);
        ArgumentCaptor<String> strArg = ArgumentCaptor.forClass(String.class);

        final Logger logger = mockWithInfoEnabled();
        DefaultServerCallLogger defaultServerCallLogger = createDefaultServerCallLogger(serviceRequestContext, logger);

        try {
            TestWSServiceWithServiceException service = createProxyToMockWithApplicationException(defaultServerCallLogger);
            service.simple();
            Assert.fail("Skulle feilet med en ValidationException");
        } catch (Exception e) {
            verify(logger, times(2)).info(strArg.capture());
            assertThat(strArg.getValue()).contains("application exception");
        }
    }

    public void testNullPointerExceptionInServiceWithErrorEnabledServerLogger() {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext(principal1, "testSimple", 1);
        ArgumentCaptor<String> strArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Throwable> thrArg = ArgumentCaptor.forClass(Throwable.class);

        final Logger logger = mockLogger();
        DefaultServerCallLogger defaultServerCallLogger = createDefaultServerCallLogger(serviceRequestContext, logger);

        try {
            TestWSServiceWithServiceException service = createProxyToMockWithNullPointerException(defaultServerCallLogger);
            service.simple();
            Assert.fail("Skulle feilet med en NullPointerException");
        } catch (Exception e) {
            verify(logger).error(strArg.capture(), thrArg.capture());
            assertThat(strArg.getValue()).doesNotContain("application exception");
            assertThat(strArg.getValue()).contains("exception");
            assertThat(thrArg.getValue()).isInstanceOf(NullPointerException.class);
        }
    }

    public void testApplicationExceptionInServiceWithErrorEnabledServerLogger() {
        ServiceRequestContext serviceRequestContext = new ServiceRequestContext(principal1, "testSimple", 1);
        ArgumentCaptor<String> strArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Throwable> thrArg = ArgumentCaptor.forClass(Throwable.class);

        final Logger logger = mockLogger();
        DefaultServerCallLogger defaultServerCallLogger = createDefaultServerCallLogger(serviceRequestContext, logger);

        try {
            TestWSServiceWithServiceException service = createProxyToMockWithApplicationException(defaultServerCallLogger);
            service.simple();
            Assert.fail("Skulle feilet med en ApplicationException");
        } catch (Exception e) {
            verify(logger, times(0)).error(strArg.capture(), thrArg.capture());
        }
    }

    public void testApplicationExceptionInServiceWithErrorEnabledClientLogger() {
        LoginUserHolder loginUserHolder = new LoginUserHolder() {
            @Override
            public LoginUser get() {
                return new LoginUser("test", "test");
            }

            @Override
            public LoginUser set(LoginUser newInstance) {
                return new LoginUser("test", "test");
            }
        };
        ArgumentCaptor<String> strArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Throwable> thrArg = ArgumentCaptor.forClass(Throwable.class);

        final Logger logger = mockLogger();
        DefaultClientCallLogger defaultClientCallLogger = createDefaultClientCallLogger(loginUserHolder, logger);
        EjbLoggingProxyHandlerTestService service = createProxyToClientsideMockWithApplicationException(defaultClientCallLogger);

        try {
            service.simple();
            Assert.fail("Skulle feilet med en ValidationException");
        } catch (Exception e) {
            verify(logger, times(0)).error(strArg.capture(), thrArg.capture());
        }
    }


    public void testApplicationExceptionInServiceWithInfoEnabledClientLogger() {
        LoginUserHolder loginUserHolder = new LoginUserHolder() {
            @Override
            public LoginUser get() {
                return new LoginUser("test", "test");
            }

            @Override
            public LoginUser set(LoginUser newInstance) {
                return new LoginUser("test", "test");
            }
        };
        ArgumentCaptor<String> strArg = ArgumentCaptor.forClass(String.class);

        final Logger logger = mockWithInfoEnabled();
        DefaultClientCallLogger defaultClientCallLogger = createDefaultClientCallLogger(loginUserHolder, logger);
        EjbLoggingProxyHandlerTestService service = createProxyToClientsideMockWithApplicationException(defaultClientCallLogger);

        try {
            service.simple();
            Assert.fail("Skulle feilet med en ValidationException");
        } catch (Exception e) {
            verify(logger, times(2)).info(strArg.capture());
            assertThat(strArg.getValue()).contains("application exception");
        }
    }

    public void testApplicationExceptionInServiceWithDebugEnabledClientLogger() {
        LoginUserHolder loginUserHolder = new LoginUserHolder() {
            @Override
            public LoginUser get() {
                return new LoginUser("test", "test");
            }

            @Override
            public LoginUser set(LoginUser newInstance) {
                return new LoginUser("test", "test");
            }
        };
        ArgumentCaptor<String> strArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Throwable> thrArg = ArgumentCaptor.forClass(Throwable.class);

        final Logger logger = mockWithDebugEnabled();
        DefaultClientCallLogger defaultClientCallLogger = createDefaultClientCallLogger(loginUserHolder, logger);
        EjbLoggingProxyHandlerTestService service = createProxyToClientsideMockWithApplicationException(defaultClientCallLogger);

        try {
            service.simple();
            Assert.fail("Skulle feilet med en ValidationException");
        } catch (Exception e) {
            verify(logger).debug(strArg.capture(), thrArg.capture());
            assertThat(strArg.getValue()).contains("application exception");
            assertThat(thrArg.getValue()).isInstanceOf(ApplicationException.class);
        }
    }

    public void testNullPointerExceptionInServiceWithErrorEnabledClientLogger() {
        LoginUserHolder loginUserHolder = new LoginUserHolder() {
            @Override
            public LoginUser get() {
                return new LoginUser("test", "test");
            }

            @Override
            public LoginUser set(LoginUser newInstance) {
                return new LoginUser("test", "test");
            }
        };
        ArgumentCaptor<String> strArg = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Throwable> thrArg = ArgumentCaptor.forClass(Throwable.class);

        final Logger logger = mockLogger();
        DefaultClientCallLogger defaultClientCallLogger = createDefaultClientCallLogger(loginUserHolder, logger);
        EjbLoggingProxyHandlerTestService service = createProxyToClientsideMockWithNullPointerException(defaultClientCallLogger);

        try {
            service.simple();
            Assert.fail("Skulle feilet med en ValidationException");
        } catch (Exception e) {
            verify(logger).error(strArg.capture(), thrArg.capture());
            assertThat(strArg.getValue()).doesNotContain("application exception");
            assertThat(strArg.getValue()).contains("exception");
            assertThat(thrArg.getValue()).isInstanceOf(NullPointerException.class);
        }
    }

    private TestWSServiceWithServiceException createProxyToMockWithApplicationException(ServerCallLogger serverCallLogger) throws ServiceException {
        TestWSServiceWithServiceException mock = mock(TestWSServiceWithServiceException.class);
        ServiceException e = new ServiceException("Test exception", new ServiceFaultInfo(), new ValidationException("Something"));
        doThrow(e).when(mock).simple();

        WsLoggingProxyHandler<TestWSServiceWithServiceException> loggingProxyHandler = new WsLoggingProxyHandler<>(serverCallLogger);
        loggingProxyHandler.setChained(new InvokeViaInstanceProxyHandler<>(mock));
        return loggingProxyHandler.buildProxy(TestWSServiceWithServiceException.class);
    }

    private TestWSServiceWithServiceException createProxyToMockWithNullPointerException(ServerCallLogger serverCallLogger) throws ServiceException {
        TestWSServiceWithServiceException mock = mock(TestWSServiceWithServiceException.class);
        ServiceException e = new ServiceException("Test exception", new ServiceFaultInfo(), new NullPointerException("Noe var null"));
        doThrow(e).when(mock).simple();

        WsLoggingProxyHandler<TestWSServiceWithServiceException> loggingProxyHandler = new WsLoggingProxyHandler<>(serverCallLogger);
        loggingProxyHandler.setChained(new InvokeViaInstanceProxyHandler<>(mock));
        return loggingProxyHandler.buildProxy(TestWSServiceWithServiceException.class);
    }

    private EjbLoggingProxyHandlerTestService createProxyToClientsideMockWithApplicationException(ClientCallLogger clientCallLogger){
        EjbLoggingProxyHandlerTestService mock = mock(EjbLoggingProxyHandlerTestService.class);
        ValidationException e = new ValidationException("Something");
        doThrow(e).when(mock).simple();

        ClientLoggingProxyHandler<EjbLoggingProxyHandlerTestService> loggingProxyHandler = new ClientLoggingProxyHandler<>(clientCallLogger, new CallIdProvider());
        loggingProxyHandler.setChained(new InvokeViaInstanceProxyHandler<>(mock));
        return loggingProxyHandler.buildProxy(EjbLoggingProxyHandlerTestService.class);
    }

    private EjbLoggingProxyHandlerTestService createProxyToClientsideMockWithNullPointerException(ClientCallLogger clientCallLogger){
        EjbLoggingProxyHandlerTestService mock = mock(EjbLoggingProxyHandlerTestService.class);
        NullPointerException e = new NullPointerException("Something");
        doThrow(e).when(mock).simple();

        ClientLoggingProxyHandler<EjbLoggingProxyHandlerTestService> loggingProxyHandler = new ClientLoggingProxyHandler<>(clientCallLogger, new CallIdProvider());
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

    private DefaultClientCallLogger createDefaultClientCallLogger(final LoginUserHolder loginUserHolder, final Logger logger) {
        return new DefaultClientCallLogger(loginUserHolder) {
            @Override
            protected Logger getLogger() {
                return logger;
            }
        };
    }

    private Logger mockLogger() {
        return mock(Logger.class);
    }

    private Logger mockWithDebugEnabled() {
        Logger logger = mockLogger();
        doReturn(true).when(logger).isDebugEnabled();
        return logger;
    }

    private Logger mockWithInfoEnabled() {
        Logger logger = mockLogger();
        doReturn(true).when(logger).isInfoEnabled();
        return logger;
    }

}
