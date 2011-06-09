package no.statkart.skif.test.guice;

import com.google.inject.*;
import org.testng.annotations.Test;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
@Test
public class BindingTest {

    /**
     * Hvordan binne opp en klasse som skal bruke en bestemt parameter uten at parameteren selv bindes i Guice.
     *
     */
    public  void test1() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestService.class).to(TestService1.class);
                final String a = "Test";
                bind(String.class).toProvider(new Provider<String>() {
                    @Inject
                    Injector injector;

                    @Override
                    public String get() {
                        final TestService instance = injector.getInstance(TestService.class);
                        return instance.getClass().getName() + " " + a;
                    }
                });

            }
        });

        String s = injector.getInstance(String.class);
        System.out.println(s);
    }

}
