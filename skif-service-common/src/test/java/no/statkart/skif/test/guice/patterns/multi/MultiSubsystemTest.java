package no.statkart.skif.test.guice.patterns.multi;

import com.google.inject.*;
import com.google.inject.name.Named;
import com.google.inject.name.Names;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Tester design pattern for håndtering av multiple moduler (subsystemer) som binner de samme klassene til
 * forskjellige instanser eller implementasjonsklasser. Her kan enten være snakk om at den samme modulen
 * brukes flere ganger i forskjelige konfigurasjon eller om forskjellige moduler som tilfeldigvis bruker
 * de samme klassene.
 * <p>
 * Standard løsningen for problemet er i utgangspunktet å bruke en {@code PrivateModule} for hvert subsystem og
 * eksponere utvalgte eller alle bindinger med forskjellig annotasjon slik at de ikke blir konflikt. F.eks kan subsystem
 * A eksponere alle sine bindinger som {@code @Named("A"}} og subsystem B som {@code @Named("B"}.
 * <p>
 * Problemet med ovenståenede teknikk er at det fort kan bli uklart hvor ting kommer fra og henger sammen når man
 * lager klasser som jobber på tvers av systemene. Hvis begge subsystemene har en tjenester som hedder {@code Store}
 * og {@code Service1} må man ved injection f.eks angi "{@code @Inject @Named("A") Store store; @Inject @Named("A") Service1 s;}"
 * for å kunne jobbe mot subsystem A. Skal man skrive kode som bare skal jobbe med et av subsystemene kan man fort ende
 * opp med dupliserte versjoner av koden som hhv. bruker {@code @Named("A")} {@code @Named("B")}. Det blir vanskelig
 * å skrive kode som kan ta som parameter det subsystemet som det skal jobbes med. Det må da angis indirekte
 * ved at man sender en annotasjon, f.eks ({@code @Named("A")}, som parameter og så må tjenestene plukkes
 * ut fra injectoren med annotasjonen som key. Dette kan fort bli kryptisk og fører til en rar programmeringsstil. Endvidere
 * kan det bli vanskelig å skjønne når man skal bruke {@code @Inject Store store;} og man bør bruke
 * {@code @Inject @Named("A") Store store}.
 * <p>
 * Det kan i mange tilfelle være hensiktsmessig fra starten av å oppfatte hvert subsystemet som en instans som man gjerne
 * vil kunne sende rundt som en parameter til kode som jobber på et eller flere subsystemer. I {@code PrivateModule} bør man
 * eksponere et interface som representere submodulen og som har metoder for å hente ut intern komponenter. F.eks
 * {@code interface SystemA { Store getStore(); Service1 getSerivce1();}}. Da kan man injecte {@code SystemA} de steder
 * hvor man trenger og jobbe med det. Klassen {@code SystemA} blir da en facade til subsystemet, noe som er et velkjendt
 * design pattern.
 * <p>
 * Denne testcasen tester ut forskjellige aspekter av dette prinsippet. Overordnet princip:
 * <ul>
 * <li>Hvert subsystem legges inn i et PrivateModule som inneholder:
 * <ul>
 * <li>Subsystemet
 * <li>Andre tilleggsmoduler som bare skal forholde seg til subsystemet
 * <li>En export modul som exponerer et interface som gir adgang til alle relevante
 * typer i modulen. Dette kunne i prinsippet være en injector for den private modulen.
 * <li>Moduler som skal jobbe på tvers av subsystemene legge inn i injectoren parallelt med
 * {@code PrivateModule}ne - evt som egne {@code PrivateModule}s
 * </ul>
 * </ul> *
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class MultiSubsystemTest {

    public void testSingleModule() {
        Injector injector = Guice.createInjector(new SubsystemModule("s", "message", TestService1Impl.class));
        Store s = injector.getInstance(Store.class);
        assertEquals(s.s, "s");
        final TestService testService = injector.getInstance(TestService.class);
        assertEquals(testService.hello("x"), "Hello1: x. ");

        final TestService testServiceViaStore = injector.getInstance(Store.class).getService(TestService.class);
        assertEquals(testServiceViaStore.hello("x"), "Hello1: x. ");

        final TestService3 testService3 = injector.getInstance(TestService3.class);
        assertEquals(testService3.hello("y"), "Hello3: y. message");
    }


    public void testMultiModule() {
        Injector injector = Guice.createInjector(
                new PrivateModule() {
                    @Override
                    protected void configure() {
                        install(new SubsystemModule("A", "messageA", TestService1Impl.class));
                        bind(Subsystem.class).annotatedWith(Names.named("A")).to(Subsystem.class);
                        expose(Subsystem.class).annotatedWith(Names.named("A"));
                    }
                },
                new PrivateModule() {
                    @Override
                    protected void configure() {
                        install(new SubsystemModule("B", "messageB", TestService2Impl.class));
                        bind(Subsystem.class).annotatedWith(Names.named("B")).to(Subsystem.class);
                        expose(Subsystem.class).annotatedWith(Names.named("B"));
                        // Hvis denne sette vil Inner ikke kunne brukes uten en eksplisitt binding
                        //binder().requireExplicitBindings();
                    }
                }
        );
        final Subsystem a = injector.getInstance(Key.get(Subsystem.class, Names.named("A")));
        Store sA = a.getStore();
        assertEquals(sA.s, "A");
        final TestService testServiceA = a.getInjector().getInstance(TestService.class);
        assertEquals(testServiceA.hello("x"), "Hello1: x. ");

        final Subsystem b = injector.getInstance(Key.get(Subsystem.class, Names.named("B")));
        Store sB = b.getStore();
        assertEquals(sB.s, "B");
        final TestService testServiceB = b.getInjector().getInstance(TestService.class);
        assertEquals(testServiceB.hello("x"), "Hello2: x. ");

        // Test AutoBinding in outer module:
        final CombinedService combinedService = injector.getInstance(CombinedService.class);
        final String[] messages = combinedService.getMessages();
        assertEquals(messages[0], "Hello1: A. ");
        assertEquals(messages[1], "Hello2: B. ");
        assertEquals(combinedService.getMessage(a), "Hello1: S. ");

        // Test AutoBinding in inner module:
        final Inner innerA = a.getInjector().getInstance(Inner.class);
        assertEquals(innerA.getMessage(), "Hello1: Inner. ");

        final Inner innerB = b.getInjector().getInstance(Inner.class);
        assertEquals(innerB.getMessage(), "Hello2: Inner. ");
    }
}

/**
 * Bindes direkte i modulen. Gi adgang til andre tjenester (klasser) gjennom seg
 */
class Store {
    @Inject
    private Injector injector;

    public String s;

    @Inject
    public Store(@Named("s") String s) {
        this.s = s;
    }

    public <T> T getService(Class<T> type) {
        return injector.getInstance(type);
    }
}

/**
 * Interface som bindes til forskjellige implementasjoner
 */
interface TestService {
    String hello(String s);
}

class TestService1Impl implements TestService {
    public String hello(String s) {
        return "Hello1: " + s + ". ";
    }
}

class TestService2Impl implements TestService {
    public String hello(String s) {
        return "Hello2: " + s + ". ";
    }
}

/**
 * Klasse som bindes eksplisitt i modulen og som får injected element (message) fra modulen den opprettes i
 */
class TestService3 {
    @Inject
    String message;

    public String hello(String s) {
        return "Hello3: " + s + ". " + message;
    }
}

class SubsystemModule extends AbstractModule {
    final String s;
    final String message;
    final Class<? extends TestService> serviceImpl;

    SubsystemModule(String s, String message, Class<? extends TestService> serviceImpl) {
        this.s = s;
        this.message = message;
        this.serviceImpl = serviceImpl;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("s")).to(s);
        bind(String.class).toInstance(message);
        bind(Store.class);
        bind(TestService.class).to(serviceImpl);
        bind(TestService3.class);
    }
}

/**
 * Fasade som exponerer modulen og som gir adgang til interne elementer i modulen
 */
class Subsystem {
    @Inject
    Injector injector;
    @Inject
    Store store;
    @Inject
    TestService testService;

    public Store getStore() {
        return store;
    }

    public Injector getInjector() {
        return injector;
    }

    public TestService getTestService() {
        return testService;
    }
}

/**
 * Klasse som jobber med flere instanser av SubsystemModule samtidig og som også har en metode som tar et subsystem som parameter
 */
class CombinedService {
    @Inject @Named("A") Subsystem a;
    @Inject @Named("B") Subsystem b;

    public String[] getMessages() {
        return new String[]{
                a.getTestService().hello("A"),
                b.getTestService().hello("B")
        };
    }

    public String getMessage(Subsystem s) {
        return s.getTestService().hello("S");
    }
}

/**
 * Klasse som kun kan jobbe innenfor en SubsystemModul. Subsystem modulen kjender ikke til denne klassen. Den bindes
 * opp dynamisk via autobinding. Vil ikke kunne brukes hvis modulen har autobinding slått av.
 */
class Inner {
    @Inject TestService service;

    public String getMessage() {
        return service.hello("Inner");
    }
}