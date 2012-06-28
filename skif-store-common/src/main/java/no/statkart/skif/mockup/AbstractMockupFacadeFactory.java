package no.statkart.skif.mockup;

import com.google.inject.*;
import no.statkart.skif.service.sequence.IdService;
import no.statkart.skif.service.test.TestdataService;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;

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
public abstract class AbstractMockupFacadeFactory<T extends AbstractMockupFacade> {
    private final Class<T> mockupFacadeClass;

    private final Provider<T> readFacadeProvider;

    private final TestdataService testdataService;
    private final Class<? extends IdService> idServiceImplementationClass;

    protected AbstractMockupFacadeFactory(Class<T> mockupFacadeClass, TestdataService testdataService) {
        this(mockupFacadeClass, testdataService, TestIdServiceLong.class);
    }

    protected AbstractMockupFacadeFactory(Class<T> mockupFacadeClass, TestdataService testdataService, Class<? extends IdService> idServiceImplementationClass) {
        this.mockupFacadeClass = mockupFacadeClass;
        this.testdataService = testdataService;
        this.idServiceImplementationClass = idServiceImplementationClass;

        // Bruker her en Provider som oppretter readFacade første gang man ber om den. Må være lazy fordi testdataService ikke bør kalles
        // i forbindelse med opprettelse MockupFacadeFactory'en. TestdataService gjør et kall til serveren og krever bl.a
        // at bruker login er satt opp.
        readFacadeProvider = new Provider<T>() {
            @Override
            public T get() {
                return createFacade(AbstractMockupFacadeFactory.this.testdataService.getTestNumber_0());
            }
        };
    }

    /**
     * Returnerer mockupfacade med testsett for tester som ikke endre på data i databasen. Gjenntatte kall til denne
     * metoden gir samme mockupfacade. Objekter i denne mockupfacade bør ikke endres siden de gjenbrukes på tvers av alle tester.
     *
     * @return mockupfacade
     */
    public T getForReadTest() {
        return readFacadeProvider.get();
    }

    /**
     * Returnerer mockupfacade med testsett for tester som ikke endre på data i databasen og lagre testsettet
     * i databasen hvis det ikke allerede finnes.
     * @return
     */
    public T getForReadTestAndSaveData() {
        final T readFacade = readFacadeProvider.get();
        testdataService.saveAll(readFacade.getAllTransfers());
        return readFacade;
    }


    /**
     * Returnerer mockupfacade med testsett for tester som endre på data. Hvert kall vil returnere en ny facade
     * med et eget unikt datasett
     *
     * @return mockupfacade
     */
    public T getForWriteTest() {
        return createFacade(testdataService.getNextTestNumber());
    }



    /**
     * Returnerer mockupfacade med testsett for tester som endre på data og lagre testsettet til databaseb.
     * Hvert kall vil returnere en ny facade med et eget unikt datasett
     *
     * @return mockupfacade
     */
    public T getForWriteTestAndSaveData() {
        final T writeFacade = getForWriteTest();
        testdataService.saveAll(writeFacade.getAllTransfers());
        return writeFacade;
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
