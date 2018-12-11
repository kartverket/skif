package no.statkart.skif.skiftest.service.test.tutorial;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
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
 * @author Henrik Fredholm
 */
@Test(groups = "server-required")
public class TutorialPart2ProxyKjedeTest {

    /**
     * Oppretter service direkte via new
     */
    public void myServicePlain() {
        MyService myService = new MyServiceImpl();
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }


    /**
     * Service med proxy
     * <pre>
     * {@code servicecall -> Proxy -> Implementation}
     * </pre>
     */
    public void myServiceMedProxy() {
        final MyService myServiceImpl = new MyServiceImpl();
        final AtomicInteger callCounter = new AtomicInteger();

        // Create Proxy that counts number of calls
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
     * Basic proxykjede:
     * <pre>
     * {@code servicecall -> Proxy 2 -> Proxy 1 -> Implementation}
     * </pre>
     */
    public void myServiceMedProxyKjede() {
        final MyService myServiceImpl = new MyServiceImpl();
        final AtomicInteger callCounter = new AtomicInteger();
        final AtomicInteger exceptionCounter = new AtomicInteger();

        // Create Proxy1 that counts number of calls
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


        // Create Proxy2 that counts number of exceptions
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
        assertThatThrownBy(() -> myService.myMethod(new A(5 /*error here*/), new B(2))).isInstanceOf(MyException.class);
        assertThat(callCounter.get()).isEqualTo(2);
        assertThat(exceptionCounter.get()).isEqualTo(1);
    }

    /**
     * Variant som bruker ProxyHandler klassen til å kalle MyService
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
     * Proxykjede basert på ProxyHandler klassene hvor handlene er egne klasser
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


    public void myServiceUsingGuice() {
        Injector injector = createEmptyInjector();
        MyService myService = injector.getInstance(MyServiceImpl.class); // NB: slår opp Impl klassen direkte
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }

    public void myServiceUsingGuiceWithBinding() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            protected void configure() {
                bind(MyService.class).to(MyServiceImpl.class);
            }
        });
        MyService myService = injector.getInstance(MyService.class); // Slår opp via interface klassen
        assertThat(myService.myMethod(new A(4), new B(2))).isEqualTo(new C(6, 2));
    }


    private Injector createEmptyInjector() {
        return Guice.createInjector(new AbstractModule() {
            protected void configure() {
            }
        });
    }

    public void createAnyObjectWithEmptyConstructorViaGuide() {
        Injector injector = createEmptyInjector();
        String emptyString = injector.getInstance(String.class);
        assertThat(emptyString).isEmpty();
        X x = injector.getInstance(X.class);
        assertThat(x).isNotNull();
    }
}


