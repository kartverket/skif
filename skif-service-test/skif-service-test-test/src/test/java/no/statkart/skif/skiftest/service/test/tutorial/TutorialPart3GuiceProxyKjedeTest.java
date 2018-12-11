package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import no.statkart.skif.service.annotation.Implementation;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * @author Henrik Fredholm
 */
@Test(groups = "server-required")
public class TutorialPart3GuiceProxyKjedeTest {

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
     * Oppretter service via 'injector.getInstance' som separat implementation binding
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

    /**
     * Oppretter ProxyHandler for implementasjon via Guice. Henter ut proxies via Multibinder som produserer
     * {@code Set<ChainedProxyHandler<MyService>}.
     * <pre>
     * {@code servicecall -> ExceptionCountingProxyHandler -> CallCountingProxyHandler -> ToImplementationProxyHandler -> Implementation}
     * </pre>
     * Fordelen med å bruke multibinder er at flere moduler uavhengig av hverandre kan legge til flere bindinger. Ulempen
     * er at proxyene ikke er ordnet i forhold til hverandre.
     */
    public void myServiceUsingGuiceWithMultibinderBasedProxyHandlerProvider() {
        Injector injector = Guice.createInjector(
                new AbstractModule() {
                    protected void configure() {
                        bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
                        bind(MyService.class).toProvider(new TypeLiteral<MultibinderBasedProxiesProvider<MyService>>() {});
                        Multibinder<ChainedProxyHandler<MyService>> multibinder
                                = Multibinder.newSetBinder(binder(), new TypeLiteral<ChainedProxyHandler<MyService>>() {});
                        multibinder.addBinding().to(new TypeLiteral<CallCountingProxyHandler<MyService>>() {});
                    }
                },
                new AbstractModule() {
                    protected void configure() {
                        Multibinder<ChainedProxyHandler<MyService>> multibinder
                                = Multibinder.newSetBinder(binder(), new TypeLiteral<ChainedProxyHandler<MyService>>() {});
                        multibinder.addBinding().to(new TypeLiteral<ExceptionCountingProxyHandler<MyService>>() {});
                    }
                });
        MyService myService = injector.getInstance(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
        assertThatThrownBy(() -> myService.myMethod(new A(5 /*error here*/), new B(2))).isInstanceOf(MyException.class);
    }

    /**
     * For å kunne ordne elementer i et multibinder set kan man bruke en hjelpeklasse {@code OrderedType<T>} som knytter
     * et tall til typen som kan brukes ved sortering.
     */
    public void orderedType() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(new TypeLiteral<OrderedType<String>>() {}).toInstance(new OrderedType<>(new TypeLiteral<String>() {}, 1.0f));
            }
        });
        OrderedType<String> factory = injector.getInstance(Key.get(new TypeLiteral<OrderedType<String>>() {}));
        assertThat(factory.getOrder()).isEqualTo(1.0f);
        assertThat(factory.getType()).isEqualTo(new TypeLiteral<String>() {});
    }

    /**
     * {@code OrderedType<T>} kan også brukes for subtyper. I dette eksemple brukes {@code Object} som generell type
     * og {@code String} som subtype. Man bruker da {@code OrderedType<Object>} som key ved oppslag.
     * <pre>
     * {@code Object -> (String, 1.0f)
     * </pre>
     */
    public void orderedTypeWithSubclass() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(new TypeLiteral<OrderedType<Object>>() {}).toInstance(new OrderedType<>(new TypeLiteral<String>() {}, 1.0f));
            }
        });
        OrderedType<Object> factory = injector.getInstance(Key.get(new TypeLiteral<OrderedType<Object>>() {}));
        assertThat(factory.getOrder()).isEqualTo(1.0f);
        assertThat(factory.getType()).isEqualTo(new TypeLiteral<String>() {});
    }

    /**
     * Eksemple på bruk av multibinder med {@code OrderedType} og sorting av typer
     */
    public void multibinderWithOrderedType() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                Multibinder<OrderedType<Object>> multibinder
                        = Multibinder.newSetBinder(binder(), new TypeLiteral<OrderedType<Object>>() {});
                multibinder.addBinding().toInstance(new OrderedType<>(new TypeLiteral<String>() {}, 1.0f));
                multibinder.addBinding().toInstance(new OrderedType<>(new TypeLiteral<String>() {}, 2.0f));
                multibinder.addBinding().toInstance(new OrderedType<>(new TypeLiteral<Long>() {}, 3.0f));
                multibinder.addBinding().toInstance(new OrderedType<>(new TypeLiteral<Boolean>() {}, 4.0f));
            }
        });
        Set<OrderedType<Object>> multiboundSet = injector.getInstance(Key.get(new TypeLiteral<Set<OrderedType<Object>>>() {}));
        assertThat(multiboundSet).hasSize(4);
        // Sortering:
        List<OrderedType<Object>> sortedList = new ArrayList<>(multiboundSet);
        Collections.sort(sortedList, (o1, o2) -> (int) Math.signum(o1.getOrder() - o2.getOrder()));

        assertThat(sortedList.get(0).getOrder()).isEqualTo(1.0f);
        assertThat(sortedList.get(1).getOrder()).isEqualTo(2.0f);
        assertThat(sortedList.get(2).getOrder()).isEqualTo(3.0f);
        assertThat(sortedList.get(3).getOrder()).isEqualTo(4.0f);
        assertThat(sortedList.get(0).getType()).isEqualTo(new TypeLiteral<String>() {});
        assertThat(sortedList.get(1).getType()).isEqualTo(new TypeLiteral<String>() {});
        assertThat(sortedList.get(2).getType()).isEqualTo(new TypeLiteral<Long>() {});
        assertThat(sortedList.get(3).getType()).isEqualTo(new TypeLiteral<Boolean>() {});
    }

    /**
     * Oppretter ProxyHandler for implementasjon via Guice. Henter ut proxies via Multibinder fra 2 moduler og som
     * produserer {@code Set<OrderedType<ChainedProxyHandler<MyService>>}. Settet sorteres ihht angitt rekkefølge.
     * <pre>
     * {@code servicecall -> ExceptionCountingProxyHandler -> CallCountingProxyHandler -> HelloProxyHandler -> ToImplementationProxyHandler -> Implementation}
     * </pre>
     * Fordelen med å bruke multibinder er at flere moduler uavhengig av hverandre kan legge til flere bindinger. Ulempen
     * er at proxyene ikke er ordnet i forhold til hverandre.
     */
    public void myServiceUsingGuiceOrderedMultibinderBasedProxyHandlerProvider() {
        Injector injector = Guice.createInjector(
                new AbstractModule() {
                    protected void configure() {
                        bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
                        bind(MyService.class).toProvider(new TypeLiteral<OrderedMultibinderBasedProxiesProvider<MyService>>() {});
                        Multibinder<OrderedType<ChainedProxyHandler<MyService>>> multibinder
                                = Multibinder.newSetBinder(binder(), new TypeLiteral<OrderedType<ChainedProxyHandler<MyService>>>() {});
                        multibinder.addBinding().toInstance(new OrderedType<>(new TypeLiteral<CallCountingProxyHandler<MyService>>() {}, 1.0f));
                    }
                },
                new AbstractModule() {
                    protected void configure() {
                        Multibinder<OrderedType<ChainedProxyHandler<MyService>>> multibinder
                                = Multibinder.newSetBinder(binder(), new TypeLiteral<OrderedType<ChainedProxyHandler<MyService>>>() {});
                        multibinder.addBinding().toInstance(new OrderedType<>(new TypeLiteral<ExceptionCountingProxyHandler<MyService>>() {}, 2.0f));
                        multibinder.addBinding().toInstance(new OrderedType<>(new TypeLiteral<LogHelloProxyHandler<MyService>>() {}, 0.0f));
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

