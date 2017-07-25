package no.statkart.skif.service.proxy;

import com.google.inject.*;
import com.google.inject.util.Types;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.mapper.*;
import no.statkart.skif.service.*;
import no.statkart.skif.service.ejb.EJBCallProxyHandler;
import no.statkart.skif.service.scope.ServiceRequestScope;
import no.statkart.skif.service.scope.ServiceRequestScoped;
import no.statkart.skif.service.ws.ServiceWSI;
import no.statkart.skif.store.SnapshotVersionContext;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.testng.Assert.*;

public class SingleVmViaWSWithServiceContextSVMapperRemoteCallProxyHandlerTest {

    @Test
    public void testServiceContextNotLeaked() throws Throwable {
        AbstractModule commonModule = new AbstractModule() {
            @Override
            protected void configure() {
                bind(ServiceContext.class).to(DefaultServiceContext.class);
                bind(SnapshotVersionContext.class).toInstance(SnapshotVersionContext.getInstance());
            }
        };

        Injector serverInjector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(SkifUtil.<TypeLiteral<EJBCallProxyHandler<TestService>>>typeLiteral(EJBCallProxyHandler.class, TestServiceImpl.class)).to(TestEJBCallProxyHandler.class);
                bind(TestService.class).to(TestServiceImpl.class);
                bind(DefaultServiceContext.class).in(ServiceRequestScoped.class);
                ServiceRequestScope serviceRequestScope = new ServiceRequestScope();
                bindScope(ServiceRequestScoped.class, serviceRequestScope);
                bind(ServiceRequestScope.class).toInstance(serviceRequestScope);
            }
        }, commonModule);

        final SingleVmServer singleVmServer = new SingleVmServer(serverInjector);

        Injector clientInjector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(SingleVmServer.class).toInstance(singleVmServer);
                bind(LoginUserHolder.class).toInstance(new LoginUserHolderImpl());
                bind(DefaultServiceContext.class).in(Singleton.class);
                bind(SkifUtil.<TypeLiteral<ServiceContextMapper<?>>>typeLiteral(ServiceContextMapper.class, Types.subtypeOf(Object.class))).to(TestServiceContextMapper.class);
                bind(Mapping.class).toInstance(new TestMapper().getMapping());
                bind(ExceptionMapping.class).toInstance(new TestExceptionMapper().getMapping());
            }
        }, commonModule);

        ServiceContext clientContext = clientInjector.getInstance(ServiceContext.class);
        clientContext.setSystemVersion("1.2.3");

        SingleVmViaWSWithServiceContextSVMapperRemoteCallProxyHandler<TestService, TestServiceWSI> proxyHandler = clientInjector.getInstance(
                Key.get(SkifUtil.<TypeLiteral<SingleVmViaWSWithServiceContextSVMapperRemoteCallProxyHandler<TestService, TestServiceWSI>>>typeLiteral(
                        SingleVmViaWSWithServiceContextSVMapperRemoteCallProxyHandler.class, TestServiceImpl.class, TestServiceWSI.class
                ))
        );

        Method method = TestServiceImpl.class.getMethod("test");

        proxyHandler.invoke(null, method, new Object[0]);

        assertEquals(clientContext.getSystemVersion(), "1.2.3");
    }

    public static class TestServiceContextMapper extends AbstractServiceContextMapper<TestServiceContextWS> {
        @Inject
        public TestServiceContextMapper(Provider<ServiceContext> serviceContextProvider) {
            super(serviceContextProvider);
        }

        @Override
        public TestServiceContextWS createWSServiceContextFromDomainServiceContext(Mapping map) {
            ServiceContext serviceContext = serviceContextProvider.get();
            TestServiceContextWS contextWS = new TestServiceContextWS();
            contextWS.setSystemVersion(serviceContext.getSystemVersion());
            contextWS.setLocale(serviceContext.getLocale().toString());
            return contextWS;
        }

        @Override
        public void setDomainServiceContextFromWSServiceContext(Mapping map, TestServiceContextWS contextWS) {
            ServiceContext serviceContext = serviceContextProvider.get();
            serviceContext.setSystemVersion("Leak!");
            serviceContext.setLocale(localeFromString(contextWS.getLocale()));
        }
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public interface TestService {
        void test();
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class TestServiceImpl implements TestService {
        @Inject
        private Provider<ServiceContext> serviceContextProvider;

        @Override
        public void test() {
            assertEquals(serviceContextProvider.get().getSystemVersion(), "Leak!");
        }
    }

    @SuppressWarnings({"unused", "WeakerAccess"})
    public static class TestServiceContextWS {
        private String systemVersion;
        private String locale;

        public String getSystemVersion() {
            return systemVersion;
        }

        public void setSystemVersion(String systemVersion) {
            this.systemVersion = systemVersion;
        }

        public String getLocale() {
            return locale;
        }

        public void setLocale(String locale) {
            this.locale = locale;
        }
    }

    @SuppressWarnings("unused")
    public interface TestServiceWSI extends ServiceWSI {
        void test(TestServiceContextWS contextWS);
    }

    public static class TestEJBCallProxyHandler extends EJBCallProxyHandler<TestService> {
        private final TestService testService;

        @Inject
        public TestEJBCallProxyHandler(TestService testService) {
            this.testService = testService;
        }

        @Override
        protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(testService, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }

    private static class TestMapper extends AbstractMapper<Mapping> {
        TestMapper() {
            super(Mapping.class);
        }
    }

    private static class TestExceptionMapper extends AbstractExceptionMapper<ExceptionMapping> {
        TestExceptionMapper() {
            super(ExceptionMapping.class, true);

            addMapper(new IdentityExceptionTypeMapper<>(Throwable.class));
        }
    }
}
