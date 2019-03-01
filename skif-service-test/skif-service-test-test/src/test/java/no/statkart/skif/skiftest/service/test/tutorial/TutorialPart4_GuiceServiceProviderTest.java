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
 *  Tester som demonstrerer hvordan Guice brukes til å opprette en service som bruker SKIF ServiceProvider til
 *  å legge på en eller flere ProxyHandlere og hvordan kejden av ProxyHandlere kan utvides ved å legge til
 *  ekstra moduler.
 *
 *
 */
@Test(groups = "server-required")
public class TutorialPart4_GuiceServiceProviderTest {

    /**
     * Eksemple på bruk binding av {@code String}. Kan kun binde {@code String} til en verdi.
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
     * Eksempel på bruk av multibinder med {@code String} som eksempel. Multibinder kan binde til {@code String} flere
     * ganger. Man får da bundet opp {@code  Set<String>}. Ma kan fortsatt binde til {@code String} også.
     */
    public void multibinderBindingAvSetOfString() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(String.class).toInstance("A");


                Multibinder<String> multibinder
                        = Multibinder.newSetBinder(binder(), String.class);
                multibinder.addBinding().toInstance("X");
                multibinder.addBinding().toInstance("Y");
                multibinder.addBinding().toInstance("Z");

                // Kunne her ha brukt:
                // bind(new TypeLiteral<Set<String>>(){}).toInstance(new HashSet(Arrays.asList("X", "Y", "Z")));

            }
        });
        String s = injector.getInstance(Key.get(new TypeLiteral<String>() {}));
        assertThat(s).isEqualTo("A");
        Set<String> multiboundSet = injector.getInstance(Key.get(new TypeLiteral<Set<String>>() {}));
        assertThat(multiboundSet).hasSize(3);
        assertThat(multiboundSet).contains("X", "Y", "Z");
    }

    /**
     * Eksemple på bruk av multibinder hvor bindingen konstrueres over flere Guice moduler. Dvs bindingen kan
     * konstrueres modulært.
     */
    public void multibinderBindingOverFlereModuler() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(String.class).toInstance("A");

                Multibinder<String> multibinder
                        = Multibinder.newSetBinder(binder(), String.class);
                multibinder.addBinding().toInstance("X");
            }
        }, new AbstractModule() {
            protected void configure() {
                Multibinder<String> multibinder
                        = Multibinder.newSetBinder(binder(), String.class);
                multibinder.addBinding().toInstance("Y");
            }
        }, new AbstractModule() {
            protected void configure() {
                Multibinder<String> multibinder
                        = Multibinder.newSetBinder(binder(), String.class);
                multibinder.addBinding().toInstance("Z");
            }
        });
        String s = injector.getInstance(Key.get(new TypeLiteral<String>() {}));
        assertThat(s).isEqualTo("A");
        Set<String> multiboundSet = injector.getInstance(Key.get(new TypeLiteral<Set<String>>() {}));
        assertThat(multiboundSet).hasSize(3);
        assertThat(multiboundSet).contains("X", "Y", "Z");
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
     * Eksemple på bruk av multibinder med {@code OrderedType} slik at settet multibinder produserer kan sorteres.
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
     * SKIFs {@code ServiceProvider] brukes til å lage services med {@code ProxyHandler}e foran. {@code ServiceProvider]
     * konfigureres via et multibinder set av type {@code <Set<CallServiceChainFactory<S>>}, hvor hvert element kan
     * sorteres. Hver {@code CallServiceChainFactory} produsere ett  {@code ProxyHandler}-ledd i kjeden.
     * <p>
     * I dette eksempelet konfigureres kun en {@code CallServiceChainFactory} og denne bindes i Guice via subklassing av
     * {@code CallServiceChainFactory} som er en abstract klasse.
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
                                        return 0; // 0 angir handleren er først i kjeden
                                    }

                                    @Override
                                    public ProxyHandler<MyService> createChain() {
                                        return implementationProxyHandler;
                                    }

                                    @Override
                                    public ProxyHandler<MyService> extendChain(@Nullable ProxyHandler<MyService> firstInChain) {
                                        throw new UnsupportedOperationException("Uventet kall, denne ServiceChainFactory har må stå først i kjeden");
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
     * Oppretter en service vha SKIFs {@code ServiceProvider} klasse. I dette eksemplet opprettes
     * {@code CallServiceChainFactory} ved å binde opp en provider instans {@code CallServiceChainFactoryProvider}, som
     * angir posisjon og {@code ProxyHandler}-klasse som skal opprettes.
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
     * Oppretter en service vha SKIFs {@code ServiceProvider} klasse. I dette eksemplet konfigureres en ekstra
     * {@code ProxyHandler} i en egen modul som legger handleren inn imellom to eksisterende handlere.
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
                        bind(MyService.class).toProvider(new TypeLiteral<ServiceProvider<MyService>>() {});
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
                        multibinder.addBinding().toProvider(new CallServiceChainFactoryProvider<>(new TypeLiteral<CallCountingProxyHandler<MyService>>() {}, 0.5f));
                    }
                });

        MyService myService = injector.getInstance(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
        assertThatThrownBy(() -> myService.myMethod(new A(5 /*error here*/), new B(2))).isInstanceOf(MyException.class);
    }

    /**
     * Tilsvarende som eksemplet over, men her brukes en {@code ServiceProvider} klasse som er basert på {@code OrderedType} klassen
     * i stedet for {@code CallServiceChainFactoryProvider} klassen. Denne koden er kanskje enklere å forstå pga. et bedre
     * klassenavn og litt anderledes intern implementasjon. Bemerk dog, at selve koden for å binne opp en service er strukturell
     * identisk.
     * <p>
     * Her brukes klassen {@code OrderedTypeServiceProvider} til å binde opp en {@code Service} med
     * sortert rekkefølge av {@code ProxyHenlder}e som angis via multibinder og {@code OrderedType}.
     * <pre>
     * {@code servicecall -> ExceptionCountingProxyHandler -> CallCountingProxyHandler -> LogHelloProxyHandler -> ToImplementationProxyHandler -> Implementation}
     * </pre>
     */
    public void myServiceUsingGuiceOrderedMultibinderBasedProxyHandlerProvider() {
        Injector injector = Guice.createInjector(
                new AbstractModule() {
                    protected void configure() {
                        bind(MyService.class).annotatedWith(Implementation.class).to(MyServiceImpl.class);
                        bind(MyService.class).toProvider(new TypeLiteral<OrderedTypeServiceProvider<MyService>>() {});
                        Multibinder<OrderedType<ProxyHandler<MyService>>> multibinder
                                = Multibinder.newSetBinder(binder(), new TypeLiteral<OrderedType<ProxyHandler<MyService>>>() {});
                        multibinder.addBinding().toInstance(new OrderedType<>(new TypeLiteral<ToImplementationProxyHandler<MyService>>() {}, 0));
                        multibinder.addBinding().toInstance(new OrderedType<>(new TypeLiteral<CallCountingProxyHandler<MyService>>() {}, 1));
                    }
                },
                new AbstractModule() {
                    protected void configure() {
                        Multibinder<OrderedType<ChainedProxyHandler<MyService>>> multibinder
                                = Multibinder.newSetBinder(binder(), new TypeLiteral<OrderedType<ChainedProxyHandler<MyService>>>() {});
                        multibinder.addBinding().toInstance(new OrderedType<>(new TypeLiteral<ExceptionCountingProxyHandler<MyService>>() {}, 2));
                        multibinder.addBinding().toInstance(new OrderedType<>(new TypeLiteral<LogHelloProxyHandler<MyService>>() {}, 0.5f)); // Legges inn rett etter proxy som kaller implementasjon.
                    }

                });
        MyService myService = injector.getInstance(MyService.class);
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
        assertThatThrownBy(() -> myService.myMethod(new A(5 /*error here*/), new B(2))).isInstanceOf(MyException.class);
    }
}

