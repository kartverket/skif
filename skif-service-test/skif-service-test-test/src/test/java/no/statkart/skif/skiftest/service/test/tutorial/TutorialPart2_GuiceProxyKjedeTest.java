package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import no.statkart.skif.service.annotation.Implementation;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;
import no.statkart.skif.service.proxy.TerminatingProxyHandler;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 *  Tester som demonstrerer hvordan Guice brukes til å opprette en Service og legge på en proxy. Viser hvordan
 *  man setter opp en kjede av proxies og hvordan man bruker SKIFs ProxyHandler klasser.
 */
@Test(groups = "server-required")
public class TutorialPart2_GuiceProxyKjedeTest {

    /**
     * Oppretter service direkte via kall til new; dvs. standard java.
     */
    public void myServicePlain() {
        MyService myService = new MyServiceImpl();
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }


    /**
     * Service med proxy; helt standard java. Proxyen teller antall kall.
     * <pre>
     * {@code servicecall -> Proxy -> Implementation}
     * </pre>
     */
    public void myServiceMedProxy() {
        final MyService myServiceImpl = new MyServiceImpl();
        final AtomicInteger callCounter = new AtomicInteger();

        // Opprett Proxy som teller antall kall
        MyService myService = (MyService) Proxy.newProxyInstance(
                MyService.class.getClassLoader(),
                new Class<?>[]{MyService.class},
                (proxy, method, args) -> {
                    callCounter.incrementAndGet();
                    return method.invoke(myServiceImpl, args);
                });

        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
        assertThat(callCounter.get()).isEqualTo(1);
        assertThat(myService.myMethod(new A(6), new B(2))).isEqualTo(new C(8, 3));
        assertThat(callCounter.get()).isEqualTo(2);
    }

    /**
     * Basic proxykjede; helt standard java. Proxy1 teller kall og Proxy 2 teller exceptions
     * <pre>
     * {@code servicecall -> Proxy 2 -> Proxy 1 -> Implementation}
     * </pre>
     */
    public void myServiceMedProxyKjede() {
        final MyService myServiceImpl = new MyServiceImpl();
        final AtomicInteger callCounter = new AtomicInteger();
        final AtomicInteger exceptionCounter = new AtomicInteger();

        // Opprett Proxy1 som teller antall kall
        MyService myServiceWithProxy1 = (MyService) Proxy.newProxyInstance(
                MyService.class.getClassLoader(),
                new Class<?>[]{MyService.class},
                (proxy, method, args) -> {
                    callCounter.incrementAndGet();
                    try {
                        return method.invoke(myServiceImpl, args);  // Kaller implementasjon
                    } catch (InvocationTargetException e) {
                        throw e.getTargetException();
                    }
                });


        // Opprett Proxy2 som teller antall exceptions
        MyService myService = (MyService) Proxy.newProxyInstance(
                MyService.class.getClassLoader(),
                new Class<?>[]{MyService.class},
                (proxy, method, args) -> {
                    try {
                        return method.invoke(myServiceWithProxy1, args);  // NB kaller første proxy
                    } catch (InvocationTargetException e) {
                        exceptionCounter.incrementAndGet();
                        throw e.getTargetException();
                    }
                });

        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
        assertThat(callCounter.get()).isEqualTo(1);
        assertThat(exceptionCounter.get()).isEqualTo(0);
        assertThatThrownBy(() -> myService.myMethod(new A(5 /* ugyldig parameter */), new B(2))).isInstanceOf(MyException.class);
        assertThat(callCounter.get()).isEqualTo(2);
        assertThat(exceptionCounter.get()).isEqualTo(1);
    }

    /**
     * Variant som bruker ProxyHandler klassen til å kalle MyService
     * <pre>
     * {@code servicecall -> ProxyHandler -> Implementation}
     * </pre>
     */
    public void myServiceMedProxyHandler() {
        ProxyHandler<MyService> proxyHandler = new ProxyHandler<MyService>() {
            final MyService myServiceImpl = new MyServiceImpl();

            @Override
            protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
                try {
                    return method.invoke(myServiceImpl, args);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
        };
        MyService myService = proxyHandler.buildProxy(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }

    /**
     * Proxykjede basert på ProxyHandler klassene
     * <pre>
     * {@code servicecall -> ChainedProxyHandler 2 -> ChainedProxyHandler 1 -> TerminatingProxyHandler -> Implementation}
     * </pre>
     */
    public void myServiceMedProxyKjedeBasertPaaProxyHandler() {
        final AtomicInteger callCounter = new AtomicInteger();
        final AtomicInteger exceptionCounter = new AtomicInteger();

        TerminatingProxyHandler<MyService> toImplProxyHandler = new TerminatingProxyHandler<MyService>() {
            final MyService myServiceImpl = new MyServiceImpl();

            @Override
            protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
                try {
                    return method.invoke(myServiceImpl, args);
                } catch (InvocationTargetException e) {
                    throw e.getTargetException();
                }
            }
        };

        ChainedProxyHandler<MyService> callCounterProxyHandler = new ChainedProxyHandler<MyService>() {
            @Override
            protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
                callCounter.incrementAndGet();
                return chained.invoke(proxy, method, args); // NB: Kall via chained proxyhandler
            }
        };

        ChainedProxyHandler<MyService> exceptionCounterProxyHandler = new ChainedProxyHandler<MyService>() {
            @Override
            protected Object invokeMethod(Object proxy, Method method, Object[] args) throws Throwable {
                try {
                    return chained.invoke(proxy, method, args); // NB: Kall via chained proxyhandler
                } catch (Throwable e) {
                    exceptionCounter.incrementAndGet();
                    throw e;
                }
            }
        };
        callCounterProxyHandler.setChained(toImplProxyHandler);
        exceptionCounterProxyHandler.setChained(callCounterProxyHandler);

        MyService myService = exceptionCounterProxyHandler.buildProxy(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
        assertThatThrownBy(() -> myService.myMethod(new A(5 /*error here*/), new B(2))).isInstanceOf(MyException.class);
        assertThat(callCounter.get()).isEqualTo(2);
        assertThat(exceptionCounter.get()).isEqualTo(1);
    }

    /**
     * Proxykjede basert hvor handlene er egne subklasser
     * <pre>
     * {@code servicecall -> ExceptionCountingProxyHandler-> CallCountingProxyHandler 1 -> ToImplementationProxyHandler -> Implementation}
     * </pre>
     */
    public void myServiceMedProxyKjedeBasertPaaNavngiteProxyHandlerKlasser() {
        ExceptionCountingProxyHandler<MyService> exceptionCountingProxyHandler = new ExceptionCountingProxyHandler<>();
        CallCountingProxyHandler<MyService> callCountingProxyHandler = new CallCountingProxyHandler<>();
        exceptionCountingProxyHandler.setChained(callCountingProxyHandler);
        callCountingProxyHandler.setChained(new ToImplementationProxyHandler<>(new MyServiceImpl()));
        MyService myService = exceptionCountingProxyHandler.buildProxy(MyService.class);

        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
        assertThatThrownBy(() -> myService.myMethod(new A(5 /*error here*/), new B(2))).isInstanceOf(MyException.class);
        assertThat(callCountingProxyHandler.counter.get()).isEqualTo(2);
        assertThat(exceptionCountingProxyHandler.counter.get()).isEqualTo(1);
    }

    /**
     * Oppbygning av service kan pakkes inn i Guice via {@code @Provides}
     */
    public void oppbyggingAvServiceViaProvides() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
            }

            @Provides
            MyService provideMyService(ExceptionCountingProxyHandler<MyService> exceptionCountingProxyHandler,
                                       CallCountingProxyHandler<MyService> callCountingProxyHandler) {
                exceptionCountingProxyHandler.setChained(callCountingProxyHandler);
                callCountingProxyHandler.setChained(new ToImplementationProxyHandler<>(new MyServiceImpl()));
                return exceptionCountingProxyHandler.buildProxy(MyService.class);
            }

        });
        MyService myService = injector.getInstance(MyService.class); // Slår opp via interface klassen
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }


    /**
     * Vi kan bruke qualifiers til å skjelne mellom binding av service til implementasjonsklasse og service med proxyies
     * som skal gis ut når vi slår opp service.
     * <pre>
     * {@code service -> serviceImplementation}
     * {@code servicecall -> ExceptionCountingProxyHandler-> CallCountingProxyHandler 1 -> ToImplementationProxyHandler -> Implementation}
     * </pre>
     *
     */
    public void oppbyggingAvServiceViaProvidesMedBindingAvImplementasjonViaQualifier() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(MyService.class).annotatedWith(Names.named("implementation")).to(MyServiceImpl.class);
            }

            @Provides
            MyService provideMyService(ExceptionCountingProxyHandler<MyService> exceptionCountingProxyHandler,
                                       CallCountingProxyHandler<MyService> callCountingProxyHandler,
                                       @Named("implementation") MyService myServiceImpl) {
                exceptionCountingProxyHandler.setChained(callCountingProxyHandler);
                callCountingProxyHandler.setChained(new ToImplementationProxyHandler<>(myServiceImpl));
                return exceptionCountingProxyHandler.buildProxy(MyService.class);
            }

        });
        MyService myService = injector.getInstance(MyService.class); // Slår opp via interface klassen
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }


    /**
     * Vi kan bruke en forhåndsdefinert qualifier {@code @Implementation} til å skjelne mellom binding av service til
     * implementasjonsklasse og service med proxyies som skal gis ut når vi slår opp service.
     * <pre>
     * {@code service -> serviceImplementation}
     * {@code servicecall -> ExceptionCountingProxyHandler-> CallCountingProxyHandler 1 -> ToImplementationProxyHandler -> Implementation}
     * </pre>
     *
     */
    public void oppbyggingAvServiceViaProvidesMedBindingAvImplementasjonViaPredefinedQualifier() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
            }

            @Provides
            MyService provideMyService(ExceptionCountingProxyHandler<MyService> exceptionCountingProxyHandler,
                                       CallCountingProxyHandler<MyService> callCountingProxyHandler,
                                       @Implementation MyService myServiceImpl) {
                exceptionCountingProxyHandler.setChained(callCountingProxyHandler);
                callCountingProxyHandler.setChained(new ToImplementationProxyHandler<>(myServiceImpl));
                return exceptionCountingProxyHandler.buildProxy(MyService.class);
            }

        });
        MyService myService = injector.getInstance(MyService.class); // Slår opp via interface klassen
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }
}


