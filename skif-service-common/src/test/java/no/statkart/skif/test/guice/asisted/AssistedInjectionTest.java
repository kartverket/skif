package no.statkart.skif.test.guice.asisted;

import com.google.inject.*;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 *
 * Testen demostrerer bruk av Singleton scope sammen med provider basert konstruksjon hvor provideren får injected parametre.
 * Siden objektet som provides kun blir opprettet en gang (siden det er en singleton) vil en etterfølgende endring av
 * parameter objektet ikke ha noen effekt på det provided objektet.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test
public class AssistedInjectionTest {


    public static class Strategy {
        Boolean strategy;

        public void setStrategy(Boolean value) {
            strategy = value;
        }

        public Boolean getStrategy() {
            return strategy;
        }
    }

    public static class Result {
        final public boolean value;

        public Result(boolean value) {
            this.value = value;
        }
    }

    /**
     * Hvordan binne opp en klasse som skal bruke en bestemt parameter uten at parameteren selv bindes i Guice.
     */
    public void test1() {
        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(Strategy.class).in(Singleton.class);
            }

            @Provides
            @Singleton
            Result provideResult(Strategy s) {
                return new Result(s.getStrategy());
            }
        });

        Strategy s = injector.getInstance(Strategy.class);
        s.setStrategy(true);
        Result result = injector.getInstance(Result.class);
        assertTrue(result.value);
        s.setStrategy(false);
        result = injector.getInstance(Result.class);
        assertTrue(result.value); // Bemerk at vi ikke får false her siden Result er singleton
    }

}
