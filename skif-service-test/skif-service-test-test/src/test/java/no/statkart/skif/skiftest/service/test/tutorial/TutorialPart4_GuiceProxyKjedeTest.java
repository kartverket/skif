package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import no.statkart.skif.service.annotation.Implementation;
import no.statkart.skif.service.chain.CallServiceChainFactory;
import no.statkart.skif.service.proxy.ChainedProxyHandler;
import no.statkart.skif.service.proxy.ProxyHandler;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Denne tutorial er work in progress
 *
 */
@Test(groups = "server-required")
public class TutorialPart4_GuiceProxyKjedeTest {


    public void workInProgress() {
        Injector injector = Guice.createInjector(
                new AbstractModule() {
                    protected void configure() {
                        bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);

                        Multibinder<CallServiceChainFactory<MyService>> multibinder
                                = Multibinder.newSetBinder(binder(), new TypeLiteral<CallServiceChainFactory<MyService>>() {});
                        multibinder.addBinding().toProvider(new Provider<CallServiceChainFactory<MyService>>() {
                            @Inject
                            ToImplementationProxyHandler<MyService> implementationProxyHandler;

                            @Override
                            public CallServiceChainFactory<MyService> get() {
                                return new CallServiceChainFactory<MyService>() {
                                    @Override
                                    public float getChainPosition() {
                                        return 0;
                                    }

                                    @Override
                                    public ProxyHandler<MyService> createChain() {
                                        return implementationProxyHandler;
                                    }

                                    @Override
                                    public ProxyHandler<MyService> extendChain(@Nullable ProxyHandler<MyService> firstInChain) {
                                        return null;
                                    }
                                };
                            }
                        });
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

