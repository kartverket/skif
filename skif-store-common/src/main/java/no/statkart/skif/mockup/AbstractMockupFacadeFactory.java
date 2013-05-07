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
 * <p/>
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

    private final Module[] extraModules;

    /**
     * Angir den snapshotversion som er default i {@link MockupStore}. For mockup-sett uten historikk bør dette være
     * {@link SnapshotVersion#CURRENT}. For historikk kan man bruke {@link SnapshotVersion#START} eller et mer spesifikt
     * tidspunkt.
     *
     * @since 2.3.0
     */
    private SnapshotVersion defaultSnapshotVersion = SnapshotVersion.CURRENT;

    protected AbstractMockupFacadeFactory(Class<T> mockupFacadeClass, TestdataService testdataService, Module... extraModules) {
        this(mockupFacadeClass, testdataService, TestIdServiceLong.class, extraModules);
    }

    protected AbstractMockupFacadeFactory(Class<T> mockupFacadeClass, TestdataService testdataService, Class<? extends IdService> idServiceImplementationClass, Module... extraModules) {
        this.mockupFacadeClass = mockupFacadeClass;
        this.testdataService = testdataService;
        this.idServiceImplementationClass = idServiceImplementationClass;
        this.extraModules = extraModules;

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
    public T getReadMockupFacade() {
        return readFacadeProvider.get();
    }

    @Deprecated
    public T getForReadTest() {
        return getReadMockupFacade();
    }

    /**
     * Returnerer mockupfacade med testsett for tester som ikke endre på data i databasen og lagre testsettet
     * i databasen hvis det ikke allerede finnes.
     *
     * @return mockupfacade
     */
    public T getReadMockupFacadeAndSaveData() {
        final T readFacade = getReadMockupFacade();
        testdataService.saveAll(readFacade.getAllTransfers());
        return readFacade;
    }

    @Deprecated
    public T getForReadTestAndSaveData() {
        return getReadMockupFacadeAndSaveData();
    }


    /**
     * Returnerer mockupfacade med testsett for tester som endre på data. Hvert kall vil returnere en ny facade
     * med et eget unikt datasett.
     *
     * @return mockupfacade
     */
    public T getWriteMockupFacade() {
        return createFacade(testdataService.getNextTestNumber());
    }

    @Deprecated
    public T getForWriteTest() {
        return getWriteMockupFacade();
    }


    /**
     * Returnerer mockupfacade med testsett for tester som endre på data og lagrer testsettet til database.
     * Hvert kall vil returnere en ny facade med et eget unikt datasett.
     *
     * @return mockupfacade
     */
    public T getWriteMockupFacadeAndSaveData() {
        final T writeFacade = getWriteMockupFacade();
        testdataService.saveAll(writeFacade.getAllTransfers());
        return writeFacade;
    }

    @Deprecated
    public T getForWriteTestAndSaveData() {
        return getWriteMockupFacadeAndSaveData();
    }


    /**
     * Returnerer en tom mockupfacade for tester som ikke vil ha forhåndsgenererte data.
     *
     * @return mockupfacade
     * @since 2.3.0
     */
    public T getEmptyMockupFacade() {
        return createEmptyFacade(testdataService.getNextTestNumber());
    }


    private T createEmptyFacade(final TestNumber testNumber) {
        Module module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(TestNumber.class).toInstance(testNumber);
                bind(IdService.class).to(idServiceImplementationClass);
                bind(Store.class).to(MockupStore.class);
            }
        };

        final Injector injector;
        if (extraModules != null) {
            Module[] modules = new Module[extraModules.length + 1];
            modules[0] = module;
            System.arraycopy(extraModules, 0, modules, 1, extraModules.length);
            injector = Guice.createInjector(modules);
        } else {
            injector = Guice.createInjector(module);
        }

        T facade = injector.getInstance(mockupFacadeClass);

        facade.getStore().setSnapshotVersion(defaultSnapshotVersion);

        return facade;
    }

    private T createFacade(TestNumber testNumber) {
        T facade = createEmptyFacade(testNumber);

        facade.createAllMockups();

        // Reset snapshotversion til current slik at facaden alltid starter i samme tilstand
        facade.getStore().setSnapshotVersion(SnapshotVersion.CURRENT);

        return facade;
    }

    /**
     * @return den {@link SnapshotVersion} som skal være standard i {@link MockupStore}
     * @see #defaultSnapshotVersion
     * @since 2.3.0
     */
    protected SnapshotVersion getDefaultSnapshotVersion() {
        return defaultSnapshotVersion;
    }

    /**
     * Setter standard {@link SnapshotVersion} for {@link MockupStore}. Denne metoden bør kalles fra konstruktøren og
     * aldri mer. Det er kun en egen setter for ikke å overlesse konstruktøren med parametre.
     *
     * @param defaultSnapshotVersion den {@link SnapshotVersion} som skal være standard i {@link MockupStore}
     * @see #defaultSnapshotVersion
     * @since 2.3.0
     */
    protected void setDefaultSnapshotVersion(SnapshotVersion defaultSnapshotVersion) {
        this.defaultSnapshotVersion = defaultSnapshotVersion;
    }
}
