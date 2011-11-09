package no.statkart.skif.test.guice;

import com.google.inject.*;
import com.google.inject.multibindings.Multibinder;
import com.google.inject.util.Types;
import org.testng.annotations.Test;

import java.util.Set;

import static org.testng.Assert.assertEquals;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
@Test
public class MultiBinderTest {

    /**
     * Bind opp mange kopier av TestService på tvers av 2 moduler
     */
    public void testMultibinderConsept() {
        final Module module1 = new AbstractModule() {
            @Override
            protected void configure() {
                Multibinder<TestService> s = Multibinder.newSetBinder(binder(), TestService.class);

            }
        };
        final Module module2 = new AbstractModule() {
            @Override
            protected void configure() {
                Multibinder<TestService> s = Multibinder.newSetBinder(binder(), TestService.class);
                s.addBinding().to(TestService1.class);
                s.addBinding().to(TestService1.class);
            }
        };

        final Injector injector = Guice.createInjector(module1, module2);
        final Set<TestService> instance = injector.getInstance(Key.get(new TypeLiteral<Set<TestService>>() {
        }));
        assertEquals(instance.size(), 2);
        final Set<TestService> instance2 = injector.getInstance(Key.get(new TypeLiteral<Set<TestService>>() {
        }));
        assertEquals(instance2.size(), 2);
    }

    /**
     * Opprett 2 multibinder set; et for TestService1 og et for TestService2
     */
    public void testMultibinderDynamicClassBinding() {
        final Class<?> service1 = TestService1.class;
        final Class<?> service2 = TestService2.class;

        final Module module1 = new AbstractModule() {
            @Override
            protected void configure() {
                bindService(2, service1);
                bindService(3, service2);
            }

            private <S> void bindService(int count, Class<S> service) {
                Multibinder<S> s1 = Multibinder.newSetBinder(binder(), service);
                for (int i = 0; i < count; i++) {
                    s1.addBinding().to(service);

                }
            }
        };

        final Injector injector = Guice.createInjector(module1);
        final Set<TestService1> instance1 = injector.getInstance(Key.get(new TypeLiteral<Set<TestService1>>() {
        }));
        assertEquals(instance1.size(), 2);
        final Set<TestService2> instance2 = injector.getInstance(Key.get(new TypeLiteral<Set<TestService2>>() {
        }));
        assertEquals(instance2.size(), 3);
    }

    /**
     * Opprett 2 multibinder set; et for TestFactory<TestService1> og et for TestFactory<TestService2>
     */
    public void testMultibinderGenericClassBinding() {
        final Module module1 = new AbstractModule() {
            @Override
            protected void configure() {
                Multibinder<TestFactory<TestService1>> s1 = Multibinder.newSetBinder(binder(), new TypeLiteral<TestFactory<TestService1>>(){});
                    s1.addBinding().to(new TypeLiteral<TestFactory1<TestService1>>() {});
                    s1.addBinding().to(new TypeLiteral<TestFactory2<TestService1>>() {});

                Multibinder<TestFactory<TestService2>> s2 = Multibinder.newSetBinder(binder(), new TypeLiteral<TestFactory<TestService2>>(){});
                    s2.addBinding().to(new TypeLiteral<TestFactory1<TestService2>>() {});
            }
        };

        final Injector injector = Guice.createInjector(module1);
        final Set<TestFactory<TestService1>> instance1 = injector.getInstance(Key.get(new TypeLiteral<Set<TestFactory<TestService1>>>() {
        }));
        assertEquals(instance1.size(), 2);
        final Set<TestFactory<TestService2>> instance2 = injector.getInstance(Key.get(new TypeLiteral<Set<TestFactory<TestService2>>>() {
        }));
        assertEquals(instance2.size(), 1);
    }

    /**
     * Opprett 2 multibinder set; et for TestFactory<TestService1> og et for TestFactory<TestService2>
     */
    public void testMultibinderDynamicGenericClassBinding() {
        final Class<?> service1 = TestService1.class;
        final Class<?> service2 = TestService2.class;

        final Module module1 = new AbstractModule() {
            @Override
            protected void configure() {
                bindFactory(TestFactory.class, service1, TestFactory1.class);
                bindFactory(TestFactory.class, service1, TestFactory2.class);

                bindFactory(TestFactory.class, service2, TestFactory1.class);
            }

            private <S, F extends TestFactory<S>, FImpl extends F> void bindFactory(Class<F> factory, Class<S> service, Class<FImpl> factoryImpl) {
                TypeLiteral<F> factoryType = (TypeLiteral<F>) TypeLiteral.get(Types.newParameterizedType(factory, service));
                TypeLiteral<FImpl> factoryImplType = (TypeLiteral<FImpl>) TypeLiteral.get(Types.newParameterizedType(factoryImpl, service));

                Multibinder<F> multibinder = Multibinder.newSetBinder(binder(), factoryType);
                multibinder.addBinding().to(factoryImplType);
            }
        };

        final Injector injector = Guice.createInjector(module1);
        final Set<TestFactory<TestService1>> instance1 = injector.getInstance(Key.get(new TypeLiteral<Set<TestFactory<TestService1>>>() {
        }));
        assertEquals(instance1.size(), 2);
        final Set<TestFactory<TestService2>> instance2 = injector.getInstance(Key.get(new TypeLiteral<Set<TestFactory<TestService2>>>() {
        }));
        assertEquals(instance2.size(), 1);
    }


    public  void test2() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestService.class).to(TestService1.class);
                final String a= "Test";
                bind(String.class).toProvider(new Provider<String>() {
                    @Inject Injector injector;

                    @Override
                    public String get() {
                        final TestService instance = injector.getInstance(TestService.class);
                        return instance.getClass().getName() + " " + a;
                    }
                });

            }
        });

        String s = injector.getInstance(String.class);
        assertEquals(s, "no.statkart.skif.test.guice.TestService1 Test");
    }
}


