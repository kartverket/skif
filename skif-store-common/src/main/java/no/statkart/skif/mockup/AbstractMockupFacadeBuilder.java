package no.statkart.skif.mockup;

import com.google.inject.*;
import com.google.inject.spi.LinkedKeyBinding;
import no.statkart.skif.service.test.TestNumberService;
import no.statkart.skif.store.*;

import java.util.Map;

/**
 * Baseklasse for opprettelse av mockupfacade-instanser. En faktisk implementasjon trenger bare implementere
 * konstruktøren. MockupFacadeBuilder-instanser må instansieres fra klientmodulen, mens MockupFacade-objektene blir
 * instansiert via sin egen lille modul som forskyner dem med mockup-Store og andre småting.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public abstract class AbstractMockupFacadeBuilder<T extends AbstractMockupFacade> {
    private final Class<T> mockupFacadeClass;

    private final T readFacade;

    private final TestNumberService testNumberService;

    private final Injector outerInjector;

    protected AbstractMockupFacadeBuilder(Class<T> mockupFacadeClass, Injector injector, TestNumberService testNumberService) {
        this.mockupFacadeClass = mockupFacadeClass;
        outerInjector = injector;
        this.testNumberService = testNumberService;

        readFacade = createFacade(TestNumber.NR_0);
    }

    /**
     * Returnerer mockupfacade for tester som ikke skriver til databasen.
     *
     * @return mockupfacade
     */
    public T getForReadTest() {
        return readFacade;
    }

    /**
     * Returnerer mockupfacade for tester som skriver til databasen. Hvert kall vil returnere en ny facade.
     *
     * @return mockupfacade
     */
    public T getForWriteTest() {
        return createFacade(new TestNumber(testNumberService.getNextTestNumber()));
    }

    private T createFacade(final TestNumber testNumber) {
        Module module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestNumber.class).toInstance(testNumber);

                // Finn alle bindinger for klassen TestIdGenerator, og overfør dem til denne modulen
                for (Map.Entry<Key<?>, Binding<?>> keyBindingEntry : outerInjector.getBindings().entrySet()) {
                    Key key = keyBindingEntry.getKey();
                    if (key.getTypeLiteral().getRawType().equals(TestIdGenerator.class)) {
                        Binding binding = keyBindingEntry.getValue();
                        bind(key).to(((LinkedKeyBinding) binding).getLinkedKey());
                    }
                }
//                bind(TestIdGenerator.class).to(((LinkedKeyBinding<TestIdGenerator>) outerInjector.getBinding(TestIdGenerator.class)).getLinkedKey()); // Tilsvarer over før generifisering av TestIdGenerator

                bind(Store.class).to(MockupStore.class);
            }

            @Provides
            @Singleton
            public MockupStore provideStore() {
                return new MockupStore();
            }
        };

        Injector injector = Guice.createInjector(module);

        T facade = injector.getInstance(mockupFacadeClass);

        facade.createAllMockups();

        return facade;
    }
}
