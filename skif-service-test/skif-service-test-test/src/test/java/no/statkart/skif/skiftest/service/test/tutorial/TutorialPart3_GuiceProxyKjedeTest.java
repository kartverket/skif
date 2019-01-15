package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import no.statkart.skif.service.annotation.Implementation;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 *  Tester som demonstrerer hvordan Guice brukes til å opprette en Service og legge på en eller flere
 *  SKIF ProxyHandlere.
 *
 */
@Test(groups = "server-required")
public class TutorialPart3_GuiceProxyKjedeTest {

    /**
     * Oppretter service direkte via 'new'
     * <pre>
     * {@code servicecall -> Implementation}
     * </pre>
     */
    public void myServicePlain() {
        MyService myService = new MyServiceImpl();
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }


    /**
     * Oppretter service via 'injector.getInstance'
     * <pre>
     * {@code servicecall -> Implementation}
     * </pre>
     */
    public void myServiceUsingGuice() {
        Injector injector = createEmptyInjector();
        MyService myService = injector.getInstance(MyServiceImpl.class); // NB: slår opp Impl klassen direkte
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }

    /**
     * Oppretter service via 'injector.getInstance' som har binding
     * <pre>
     * {@code servicecall -> Implementation}
     * </pre>
     */
    public void myServiceUsingGuiceWithBinding() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(MyService.class).to(MyServiceImpl.class);
            }
        });
        MyService myService = injector.getInstance(MyService.class); // Slår opp via interface klassen
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }

    /**
     * Oppretter service via 'injector.getInstance' som separat implementation binding. Her brukes
     * {@code Implementation.class} som qualifier
     * <pre>
     * {@code servicecall -> Implementation}
     * </pre>
     */
    public void myServiceUsingGuiceWithImplementationBinding() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
            }
        });

        MyService myService = injector.getInstance(Key.get(MyService.class, Implementation.class));
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }


    /**
     * Oppretter ProxyHandler via Guice som får injected implementation av MyService. Henter ut proxy for MyService manuelt
     * <pre>
     * {@code servicecall -> ToImplementationProxyHandler -> Implementation}
     * </pre>
     */
    public void myServiceUsingGuiceWithImplementationProxy() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
            }
        });
        ProxyHandler<MyService> proxyHandler = injector.getInstance(Key.get(new TypeLiteral<ToImplementationProxyHandler<MyService>>() {}));
        MyService myService = proxyHandler.buildProxy(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }

    /**
     * Oppretter ProxyHandler for implementasjon via Guice. Henter ut proxy for MyService via Guice provides metode
     * <pre>
     * {@code servicecall -> ToImplementationProxyHandler -> Implementation}
     * </pre>
     */
    public void myServiceUsingGuiceWithImplementationProxyViaProvides() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
            }

            @Provides
            MyService myServiceProvider(ToImplementationProxyHandler<MyService> proxyHandler) {
                return proxyHandler.buildProxy(MyService.class);
            }
        });
        MyService myService = injector.getInstance(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }

    /**
     * Oppretter ProxyHandler for implementasjon via Guice. Henter ut proxy for MyService via Guice provider binding
     * <pre>
     * {@code servicecall -> ToImplementationProxyHandler -> Implementation}
     * </pre>
     */
    public void myServiceUsingGuiceWithImplementationProxyViaProvider() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
                bind(MyService.class).toProvider(new Provider<MyService>() {
                    @Inject
                    ToImplementationProxyHandler<MyService> proxyHandler;

                    @Override
                    public MyService get() {
                        return proxyHandler.buildProxy(MyService.class);
                    }
                });
            }
        });
        MyService myService = injector.getInstance(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }


    /**
     * Oppretter ProxyHandler for implementasjon via Guice. Henter ut proxy for MyService via Guice provider klasse
     * <pre>
     * {@code servicecall -> ToImplementationProxyHandler -> Implementation}
     * </pre>
     */
    public void myServiceUsingGuiceWithImplementationProxyViaProviderKlasse() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
                bind(MyService.class).toProvider(new TypeLiteral<ToImplementationProxyHandlerProvider<MyService>>() {});
            }
        });
        MyService myService = injector.getInstance(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }

    /**
     * Oppretter ProxyHandler for implementasjon via Guice. Henter ut proxy for MyService via Guice provider klasse
     * hvor proxies er hardkodet.
     * <pre>
     * {@code servicecall -> ExceptionCountingProxyHandler -> CallCountingProxyHandler -> ToImplementationProxyHandler -> Implementation}
     * </pre>
     */
    public void myServiceUsingGuiceWithHardcodedProxiesProvider() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
                bind(MyService.class).toProvider(new TypeLiteral<HardCodedProxiesProvider<MyService>>() {});
            }
        });
        MyService myService = injector.getInstance(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
        assertThatThrownBy(() -> myService.myMethod(new A(5 /*error here*/), new B(2))).isInstanceOf(MyException.class);
    }

    /**
     * Oppretter ProxyHandler for implementasjon via Guice. Henter ut proxy for MyService via Guice provider klasse
     * hvor proxies konfigureres ut fra liste
     * <pre>
     * {@code servicecall -> ExceptionCountingProxyHandler -> CallCountingProxyHandler -> ToImplementationProxyHandler -> Implementation}
     * </pre>
     */
    public void myServiceUsingGuiceWithListBasedProxyHandlerProvider() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
                bind(new TypeLiteral<List<ChainedProxyHandler<MyService>>>() {}).toInstance(Arrays.asList(
                        new CallCountingProxyHandler<>(),
                        new ExceptionCountingProxyHandler<>()));
                bind(MyService.class).toProvider(new TypeLiteral<ListBasedProxiesProvider<MyService>>() {});
            }
        });
        MyService myService = injector.getInstance(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
        assertThatThrownBy(() -> myService.myMethod(new A(5 /*error here*/), new B(2))).isInstanceOf(MyException.class);
    }

    private Injector createEmptyInjector() {
        return Guice.createInjector(new AbstractModule() {
            protected void configure() {
            }
        });
    }


}

