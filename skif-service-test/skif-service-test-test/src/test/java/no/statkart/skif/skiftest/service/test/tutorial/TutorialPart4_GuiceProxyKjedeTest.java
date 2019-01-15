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
import no.statkart.skif.service.provider.ServiceProvider;
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
 *  Tester som demonstrerer hvordan Guice brukes til å opprette en Service som bruker SKIF ServiceProvider til
 *  å legge på en eller flere ProxyHandlere og hvordan kejden av ProxyHandlere kan utvides ved å legge til
 *  ekstra moduler.
 *
 *
 */
@Test(groups = "server-required")
public class TutorialPart4_GuiceProxyKjedeTest {

    /**
     * Eksemple på bruk binding av String
     */
    public void multibinderBindingAvString() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(String.class).toInstance("A");
                // Dette går ikke:
                // bind(String.class).toInstance("B");
            }
        });
        String s = injector.getInstance(Key.get(new TypeLiteral<String>() {}));
        assertThat(s).isEqualTo("A");
    }

    /**
     * Eksemple på bruk av multibinder med {@code String} som eksempel
     */
    public void multibinderBindingAvSetOfString() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                Multibinder<String> multibinder
                        = Multibinder.newSetBinder(binder(), String.class);
                multibinder.addBinding().toInstance("A");
                multibinder.addBinding().toInstance("B");
                multibinder.addBinding().toInstance("C");
                multibinder.addBinding().toInstance("D");
            }
        });
        Set<String> multiboundSet = injector.getInstance(Key.get(new TypeLiteral<Set<String>>() {}));
        assertThat(multiboundSet).hasSize(4);
        assertThat(multiboundSet).contains("A", "B", "C", "D");
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
     * Eksemple på bruk av multibinder med {@code OrderedType} og sorting av typer
     */
    public void multibinderWithOrderedType() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                Multibinder<OrderedType<Object>> multibinder
                        = Multibinder.newSetBinder(binder(), new TypeLiteral<OrderedType<Object>>() {});
                multibinder.addBinding().toInstance(new OrderedType<>(new TypeLiteral<String>() {}, 1.0f));
                multibinder.addBinding().toInstance(new OrderedType<>(new TypeLiteral<Float>() {}, 2.0f));
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
        assertThat(sortedList.get(1).getType()).isEqualTo(new TypeLiteral<Float>() {});
        assertThat(sortedList.get(2).getType()).isEqualTo(new TypeLiteral<Long>() {});
        assertThat(sortedList.get(3).getType()).isEqualTo(new TypeLiteral<Boolean>() {});
    }

    /**
     * Oppretter en Service vha SKIF ServiceProvider klassen. Denne klassen setter sammen en liste av
     * ProxyHandlere ut fra en ordnet liste av CallServiceChainFactories. I dette eksempel er det kun
     * en slik factory som implementeres via subklassing.
     *
     * <pre>
     * {@code MyService -> Implementation} for qualifier @Implementation
     * {@code MyService -> ServiceProvider}
     * {@code CallServiceFactory<MyService> -> ToImplementationProxyHandler}
     * {@code servicecall ->  ToImplementationProxyHandler -> Implementation}
     * </pre>
     */
    public void myServiceUsingServiceProviderAndFactoryImplementation() {
        Injector injector = Guice.createInjector(
                new AbstractModule() {
                    protected void configure() {
                        bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
                        bind(MyService.class).toProvider(new TypeLiteral<ServiceProvider<MyService>>(){});
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
                                        return 0; // 0 angir handler er først i kjeden
                                    }

                                    @Override
                                    public ProxyHandler<MyService> createChain() {
                                        return implementationProxyHandler;
                                    }

                                    @Override
                                    public ProxyHandler<MyService> extendChain(@Nullable ProxyHandler<MyService> firstInChain) {
                                        throw new UnsupportedOperationException("Uventet kall, denne ServiceChainFactory må stå sist i kjeden");
                                    }
                                };
                            }
                        });
                    }
                });

        MyService myService = injector.getInstance(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
        assertThatThrownBy(() -> myService.myMethod(new A(5 /*error here*/), new B(2))).isInstanceOf(MyException.class);
    }

    /**
     * Oppretter en Service vha SKIF ServiceProvider klassen. I dette eksemplet angis facoryen ved å opprette en
     * CallServiceChainFactoryProvider instans hvor parametre angir klasse på ProxyHandler og posisjon i kjeden.
     * angir ProxyHanderTypenog hvilken posisjon som handleren ha listen via argumenter til klassen
     *
     * <pre>
     * {@code MyService -> Implementation} for qualifier @Implementation
     * {@code MyService -> ServiceProvider}
     * {@code CallServiceFactory<MyService> -> ToImplementationProxyHandler}
     * {@code servicecall ->  ToImplementationProxyHandler -> Implementation}
     * </pre>
     */
    public void myServiceUsingServiceProviderAndInstanceBasedCallServiceFactoryProvider() {
        Injector injector = Guice.createInjector(
                new AbstractModule() {
                    protected void configure() {
                        bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
                        bind(MyService.class).toProvider(new TypeLiteral<ServiceProvider<MyService>>(){});
                        Multibinder<CallServiceChainFactory<MyService>> multibinder
                                = Multibinder.newSetBinder(binder(), new TypeLiteral<CallServiceChainFactory<MyService>>() {});
                        multibinder.addBinding().toProvider(new CallServiceChainFactoryProvider<>(new TypeLiteral<ToImplementationProxyHandler<MyService>>() {}, 0));
                    }
                });

        MyService myService = injector.getInstance(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
        assertThatThrownBy(() -> myService.myMethod(new A(5 /*error here*/), new B(2))).isInstanceOf(MyException.class);
    }


    /**
     * Oppretter en Service vha SKIF ServiceProvider klassen. Dette eksemplet har en ekstra proxyhandler
     * som konfigurers via en egen modul som legger handleren inn imellom de to eksisterende handlere.
     * <p>
     * Eksemplet viser hvordan ekstra moduler kan brukes til å utvide en basis kjeden. Rekkefølgen av ProxyHandlere
     * styres via en {@code order} paramerter av type float så man plasere inn nye ProxyHandlere i kjeden
     * uten å måtte endre på eksisterende rekkefølge konfigurasjon.
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
    public void myServiceUsingServiceProviderWithAdditionalProxyHandlers() {
        Injector injector = Guice.createInjector(
                new AbstractModule() {
                    protected void configure() {
                        bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
                        bind(MyService.class).toProvider(new TypeLiteral<ServiceProvider<MyService>>() {
                        });
                        Multibinder<CallServiceChainFactory<MyService>> multibinder
                                = Multibinder.newSetBinder(binder(), new TypeLiteral<CallServiceChainFactory<MyService>>() {});
                        multibinder.addBinding().toProvider(new CallServiceChainFactoryProvider<>(new TypeLiteral<ToImplementationProxyHandler<MyService>>() {}, 0));
                        multibinder.addBinding().toProvider(new CallServiceChainFactoryProvider<>(new TypeLiteral<ExceptionCountingProxyHandler<MyService>>() {}, 1));
                    }
                }, new AbstractModule() {
                    @Override
                    protected void configure() {
                        Multibinder<CallServiceChainFactory<MyService>> multibinder
                                = Multibinder.newSetBinder(binder(), new TypeLiteral<CallServiceChainFactory<MyService>>() {});
                        multibinder.addBinding().toProvider(new CallServiceChainFactoryProvider<>(new TypeLiteral<CallCountingProxyHandler<MyService>>() {}, 1.5f));
                    }
                });

        MyService myService = injector.getInstance(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
        assertThatThrownBy(() -> myService.myMethod(new A(5 /*error here*/), new B(2))).isInstanceOf(MyException.class);
    }
}

