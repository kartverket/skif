package no.statkart.skif.mockup;

import com.google.inject.*;
import com.google.inject.spi.LinkedKeyBinding;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.service.test.TestNumberService;
import no.statkart.skif.service.test.TestdataService;
import no.statkart.skif.store.*;

import java.util.Map;

/**
 * Baseklasse for opprettelse av mockupfacade-instanser. En faktisk implementasjon trenger bare implementere
 * konstruktøren. MockupFacadeBuilder-instanser må instansieres fra klientmodulen, mens MockupFacade-objektene
 * som builderen oppretter blir instansiert via en egen fritstående modul som forsyner MockupFacaden med
 * en egen {@code MockupStore}-instans, {@code TestNumer}-instans og {@code IdService}-instans for generering
 * av test id'er.
 *
 * Klassen er annotert med @Singleton slik at readTestSet gjenbrukes på tvers av tester. Tilsvarende må subklasser
 * annoteres med @Singleton side Guice ikke tar hensyn til annotasjoner på superklasser.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Singleton
public abstract class AbstractMockupFacadeBuilder<T extends AbstractMockupFacade> {
    private final Class<T> mockupFacadeClass;

    private final T readFacade;

    private final TestdataService testdataService;
    private final Class<? extends IdService> idServiceImplementationClass;

    protected AbstractMockupFacadeBuilder(Class<T> mockupFacadeClass, TestdataService testdataService) {
        this(mockupFacadeClass, testdataService, TestIdServiceLong.class);
    }

    protected AbstractMockupFacadeBuilder(Class<T> mockupFacadeClass, TestdataService testdataService, Class<? extends IdService> idServiceImplementationClass) {
        this.mockupFacadeClass = mockupFacadeClass;
        this.testdataService = testdataService;
        this.idServiceImplementationClass = idServiceImplementationClass;
        readFacade = createFacade(TestNumber.NR_0);
    }

    /**
     * Returnerer mockupfacade for tester som ikke skriver til databasen.  Gjenntatte kall til denne metoden gir
     * samme mockupfacase. Objekter i denne mockupfacade bør ikke endres siden de gjenbrukes på tvers av alle tester.
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
        return createFacade(testdataService.getNextTestNumber());
    }

    private T createFacade(final TestNumber testNumber) {
        Module module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestNumber.class).toInstance(testNumber);
                bind(IdService.class).to(idServiceImplementationClass);
                bind(Store.class).to(MockupStore.class);
            }
        };

        Injector injector = Guice.createInjector(module);
        T facade = injector.getInstance(mockupFacadeClass);
        facade.createAllMockups();

        // Reset snapshotversion til current slik at facaden alltid starter i samme tilstand
        facade.getStore().setSnapshotVersion(SnapshotVersion.CURRENT);

        return facade;
    }
}
