package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Modules;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.service.SingleVmServer;
import no.statkart.skif.service.annotation.Implementation;
import no.statkart.skif.service.chain.CallServiceChainFactory;
import no.statkart.skif.service.provider.ServiceProvider;
import no.statkart.skif.service.proxy.ProxyHandler;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Denne tutorial viser hvordan Guice moduler brukes til å binde forskjellige typer for proxykjeder for services via
 * SKIFs {@code ServiceProvider} og {@code ProxyHandler} klasser.
 */
@Test(groups = "server-required")
public class TutorialPart5_ServiceModuleTest {


    static class MyServiceModule extends AbstractModule {

        @Override
        protected void configure() {
            bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
            bind(MyService.class).toProvider(new TypeLiteral<ServiceProvider<MyService>>() {});
            Multibinder<CallServiceChainFactory<MyService>> multibinder
                    = Multibinder.newSetBinder(binder(), new TypeLiteral<CallServiceChainFactory<MyService>>() {});
            multibinder.addBinding().toProvider(
                    new CallServiceChainFactoryProvider<>(
                            new TypeLiteral<ToImplementationProxyHandler<MyService>>() {}, 0));
            multibinder.addBinding().toProvider(
                    new CallServiceChainFactoryProvider<>(
                            new TypeLiteral<ExceptionCountingProxyHandler<MyService>>() {}, 1));
        }
    }

    static class MyServiceCallCountingProxyHandlerModule extends AbstractModule {

        @Override
        protected void configure() {
            Multibinder<CallServiceChainFactory<MyService>> multibinder
                    = Multibinder.newSetBinder(binder(), new TypeLiteral<CallServiceChainFactory<MyService>>() {});
            multibinder.addBinding().toProvider(
                    new CallServiceChainFactoryProvider<>(
                            new TypeLiteral<CallCountingProxyHandler<MyService>>() {}, 0.5f));
        }
    }

    /**
     * Oppretter en service vha SKIFs {@code ServiceProvider} med proxyhandlere før kallet hvor guice definisjoner er
     * pakket inn i moduler.
     *
     * <pre>
     * {@code MyService -> Implementation} for qualifier @Implementation
     * {@code MyService -> ServiceProvider}
     * {@code CallServiceFactory<MyService> -> ExceptionCountingProxyHandler}
     * {@code CallServiceFactory<MyService> -> CallCountingProxyHandler}
     * {@code CallServiceFactory<MyService> -> ToImplementationProxyHandler}
     * {@code servicecall ->  ToImplementationProxyHandler -> Implementation}
     * </pre>
     */
    public void myServiceUsingSpecificModules() {
        Injector injector = Guice.createInjector(new MyServiceModule(), new MyServiceCallCountingProxyHandlerModule());
        MyService myService = injector.getInstance(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
        assertThatThrownBy(() -> myService.myMethod(new A(5 /*error here*/), new B(2))).isInstanceOf(MyException.class);
    }


    static class ServiceModule extends AbstractModule {
        final Set<Class<?>> services = new HashSet<>();

        ServiceModule(Class<?>... services) {
            this.services.addAll(Arrays.asList(services));
        }

        @SuppressWarnings("unchecked")
        private <T> Class<? extends T> getImplementation(Class<T> serviceInterface) {
            try {
                return (Class<? extends T>) Class.forName(serviceInterface.getName() + "Impl");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        protected void configure() {
            for (Class<? extends Object> service : services) {
                bindService(service);
            }
        }

        private <S> void bindService(Class<S> service) {
            //bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
            bind(service).annotatedWith(Implementation.class).to(getImplementation(service));
            //bind(MyService.class).toProvider(new TypeLiteral<ServiceProvider<MyService>>() {});
            TypeLiteral<ServiceProvider<S>> callChainProxyHandlerType =
                    SkifUtil.typeLiteral(ServiceProvider.class, service);
            bind(service).toProvider(callChainProxyHandlerType);
        }
    }

    static class ProxyHandlerModule extends AbstractModule {
        private final Class<? extends ProxyHandler> proxyHandler;
        private float order;
        private final Set<Class<?>> services = new HashSet<>();

        ProxyHandlerModule(Class<? extends ProxyHandler> proxyHandler, float order, Class<?>... services) {
            this.proxyHandler = proxyHandler;
            this.order = order;
            this.services.addAll(Arrays.asList(services));
        }

        @Override
        protected void configure() {
            for (Class<? extends Object> service : services) {
                bindProxyHandlerToService(service);
            }
        }

        private <S> void bindProxyHandlerToService(Class<S> service) {
            //Multibinder<CallServiceChainFactory<MyService>> multibinder
            //        = Multibinder.newSetBinder(binder(), new TypeLiteral<CallServiceChainFactory<MyService>>() {});
            TypeLiteral<CallServiceChainFactory<S>> callServiceChainFactoryType =
                    SkifUtil.typeLiteral(CallServiceChainFactory.class, service);
            Multibinder<CallServiceChainFactory<S>> multibinder
                    = Multibinder.newSetBinder(binder(), callServiceChainFactoryType);
            //multibinder.addBinding().toProvider(new CallServiceChainFactoryProvider<>(new TypeLiteral<CallCountingProxyHandler<MyService>>() {}, 0.5f));
            TypeLiteral<ProxyHandler<S>> proxyHandlerType = SkifUtil.typeLiteral(proxyHandler, service);
            multibinder.addBinding().toProvider(new CallServiceChainFactoryProvider<>(proxyHandlerType, order));
        }
    }

    /**
     * Oppretter en service vha SKIFs {@code ServiceProvider} med proxyhandlere før kallet hvor guice definisjoner er
     * pakket inn i generaliserte moduler.
     *
     * <pre>
     * {@code MyService -> Implementation} for qualifier @Implementation
     * {@code MyService -> ServiceProvider}
     * {@code CallServiceFactory<MyService> -> ExceptionCountingProxyHandler}
     * {@code CallServiceFactory<MyService> -> CallCountingProxyHandler}
     * {@code CallServiceFactory<MyService> -> ToImplementationProxyHandler}
     * {@code servicecall ->  ToImplementationProxyHandler -> Implementation}
     * </pre>
     */
    public void myServiceUsingGeneralizedModules() {
        Injector injector = Guice.createInjector(
                new ServiceModule(MyService.class),
                new ProxyHandlerModule(ToImplementationProxyHandler.class, 0, MyService.class),
                new ProxyHandlerModule(CallCountingProxyHandler.class, 0.5f, MyService.class),
                new ProxyHandlerModule(ExceptionCountingProxyHandler.class, 1.0f, MyService.class));
        MyService myService = injector.getInstance(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
        assertThatThrownBy(() -> myService.myMethod(new A(5 /*error here*/), new B(2))).isInstanceOf(MyException.class);
    }

    interface AnotherService {
        A anotherMethod(A a);
    }

    @SuppressWarnings("unused") // Brukes via Guice
    static class AnotherServiceImpl implements AnotherService {
        public A anotherMethod(A a) {
            return new A(2 * a.x);
        }
    }

    /**
     * Eksempel med to services som bindes opp med forskjellige proxyhandlere
     */
    public void multipleServicesUsingGeneralizedModules() {
        Injector injector = Guice.createInjector(
                new ServiceModule(MyService.class, AnotherService.class),
                new ProxyHandlerModule(ToImplementationProxyHandler.class, 0, MyService.class, AnotherService.class),
                new ProxyHandlerModule(CallCountingProxyHandler.class, 0.5f, MyService.class, AnotherService.class),
                new ProxyHandlerModule(ExceptionCountingProxyHandler.class, 1.0f, MyService.class)); // AnotherServer trenger ikke denne
        MyService myService = injector.getInstance(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
        assertThatThrownBy(() -> myService.myMethod(new A(5 /*error here*/), new B(2))).isInstanceOf(MyException.class);
        AnotherService anotherService = injector.getInstance(AnotherService.class);
        assertThat(anotherService.anotherMethod(new A(10))).isEqualTo(new A(20));
    }


    /**
     * Eksempel som viser hvordan Guice injection brukes til å konfigurere opp proxy handlere. I dette eksemple har vi
     * introdusert et {@code User} object og en {@code UserProxyHandler}. Vi ønsker å kunne logge current user og
     * hvilken service som blir kalt. Dette gjøres ved å injecte {@code User} og service typen inn i {@code
     * Proxyhandler}. Resten av koden for konfigurasjon av services er uforandert
     *
     * <pre>{@code
     *     public class UserProxyHandler<S> extends ChainedProxyHandler<S> {
     *       final private TypeLiteral<S> type;
     *       final private User user;
     *
     *       @Inject
     *       public UserProxyHandler(TypeLiteral<S> type, User user) {
     *         this.type = type;
     *         this.user = user;
     *      }
     *   }
     * }</pre>
     */
    public void eksempelMedProxyHandlerSomFaarInjectetAndreKlasser() {
        Injector injector = Guice.createInjector(
                new AbstractModule() {
                    protected void configure() {
                        // Viktig med begge binder da vi henter ut både User og DefaultUser
                        bind(User.class).to(DefaultUser.class);
                        bind(DefaultUser.class).toInstance(new DefaultUser());
                    }
                },
                new ServiceModule(MyService.class),
                new ProxyHandlerModule(ToImplementationProxyHandler.class, 0, MyService.class),
                new ProxyHandlerModule(UserProxyHandler.class, 0.5f, MyService.class));
        MyService myService = injector.getInstance(MyService.class);
        DefaultUser user = injector.getInstance(DefaultUser.class); // Her brukes DefautlUser, ikke User
        user.setUsername("Henrik");
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
        user.setUsername("Trude");
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }

    /**
     * Eksempel som viser SingleVm konseptet.
     * <p>
     * For SingleVm trenger vi to injectors, en for klienten og en for serveren. Når vi er i klient mode bruker vi
     * klient injectoren til å hente ut services. Når vi er i server mode bruker vi server injectoren til å hente ut
     * services. Når vi kaller en tjeneste på klienten så er forwarder den kallet videre til servicen på serveren.
     * Dvs., klienten må vite som serveren for å kunne gjøre dette. Derfor binnes {@code SingleVmServer} klassen opp
     * i klient injectoren med link til server injectoren. Klassen {@code SingleVmServer} kan da injectes i {@code
     * ToServerProxyHandler}. Legg merke til at dette er en {@code TerminatingProxyHandler}. Det er fordi
     * den avslutter proxy kjeden.
     * <pre>{@code
     *   public class ToServerProxyHandler<S> extends TerminatingProxyHandler<S> {
     *     final private TypeLiteral<S> serviceType;
     *     final private SingleVmServer singleVmServer;
     *
     *     @Inject
     *     public ToServerProxyHandler(TypeLiteral<S> serviceType, SingleVmServer singleVmServer) {
     *         this.serviceType = serviceType;
     *         this.singleVmServer = singleVmServer;
     *     }
     *     ...
     * }
     * </pre>
     * Konfigurasjon som er felles kan legges i en common modul.
     */
    public void singleVmEksempel() {

        Module commonModule = Modules.combine(
                new ServiceModule(MyService.class),
                new ProxyHandlerModule(CallCountingProxyHandler.class, 1, MyService.class)
        );

        Injector serverInjector = Guice.createInjector(
                new ProxyHandlerModule(ToImplementationProxyHandler.class, 0, MyService.class),
                new ProxyHandlerModule(OnServerProxyHandler.class, 10, MyService.class),
                commonModule);

        final SingleVmServer singleVmServer = new SingleVmServer(serverInjector);

        Injector clientInjector = Guice.createInjector(
                new AbstractModule() {
                    protected void configure() {
                        bind(SingleVmServer.class).toInstance(singleVmServer);
                    }
                },
                new ProxyHandlerModule(ToServerProxyHandler.class, 0, MyService.class),
                new ProxyHandlerModule(OnClientProxyHandler.class, 10, MyService.class),
                commonModule);

        MyService clientService = clientInjector.getInstance(MyService.class);
        clientService.myMethod(new A(10), new B(5));
    }
}

