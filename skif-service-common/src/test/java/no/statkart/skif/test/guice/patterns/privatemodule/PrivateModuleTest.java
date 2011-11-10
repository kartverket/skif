package no.statkart.skif.test.guice.patterns.privatemodule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.testng.annotations.Test;

import static org.testng.Assert.assertSame;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class PrivateModuleTest {
    public void testBasisOppsett() {
        final Injector injector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        // Disse trengs ikke. De kan være implisitt
                        //bind(A1.class);
                        //bind(A2.class);
                        //bind(B1.class);
                        //bind(B2.class);
                        bind(M.class).to(M1.class);
                    }
                });

        A1 a1 = injector.getInstance(A1.class);
        assertSame(a1.b.m.getClass(), M1.class);
        A2 a2 = injector.getInstance(A2.class);
        assertSame(a2.b.m.getClass(), M1.class);
    }

    public void testBasisOppsettWithExplisittBindings() {
        final Injector injector = Guice.createInjector(
                new AbstractModule() {
                    @Override
                    protected void configure() {
                        bind(A1.class);
                        bind(A2.class);
                        bind(B1.class);
                        bind(B2.class);
                        bind(C.class);
                        bind(M.class).to(M1.class);
                        binder().requireExplicitBindings();
                    }
                });

        A1 a1 = injector.getInstance(A1.class);
        assertSame(a1.b.m.getClass(), M1.class);
        A2 a2 = injector.getInstance(A2.class);
        assertSame(a2.b.m.getClass(), M1.class);
    }
}
