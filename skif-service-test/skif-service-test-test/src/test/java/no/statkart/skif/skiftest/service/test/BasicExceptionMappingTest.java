package no.statkart.skif.skiftest.service.test;

import com.google.inject.Injector;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.module.ModuleBuilder;
import no.statkart.skif.module.ModuleConfiguration;
import no.statkart.skif.module.ModuleStrategyFactory;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.service.module.ClientModuleStrategyFactory;
import no.statkart.skif.service.module.common.RemoteServerModule;
import no.statkart.skif.service.module.common.RemoteServiceModule;
import no.statkart.skif.skiftest.config.SkifTestGroupExServices;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.skiftest.exception.SimpleException;
import no.statkart.skif.skiftest.exception.SimpleNonMappedException;
import no.statkart.skif.skiftest.service.testex.TestExService;
import no.statkart.skif.skiftest.wsapi.exception.simple.mapping.SkifTestExceptionMapper2;
import no.statkart.skif.skiftest.wsapi.mapping.SkifTestMapper;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.xml.ws.soap.SOAPFaultException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * @author Henrik Fredholm
 * @since 1.1
 */
@Test(groups = "server-required")
public class BasicExceptionMappingTest {
    private Injector injector = null;

    @DataProvider(name = "serverModes")
    public Object[][] createServerModes() {
        return new Object[][]{
                {ServiceMode.JEE},
                {ServiceMode.SINGLE_VM},
        };
    }

    @DataProvider(name = "serverModesJEE")
    public Object[][] createServerModesJEE() {
        return new Object[][]{
                {ServiceMode.JEE},
        };
    }

    @DataProvider(name = "serverModesSVM")
    public Object[][] createServerModesSVM() {
        return new Object[][]{
                {ServiceMode.SINGLE_VM},
        };
    }

    private void setlogin() {
        final LoginUserHolder loginUserHolder = injector.getInstance(LoginUserHolder.class);
        loginUserHolder.set(new LoginUser("frehen", "matrikkel2"));
        final ServerUrlHolder serverUrlHolder = injector.getInstance(ServerUrlHolder.class);
        serverUrlHolder.set("https://localhost:7002");
    }

    public Injector createClientInjector(ServiceMode mode) {
        ModuleConfiguration cfg = null;
        return new ModuleBuilder()
                .setModuleClass(ClientModule.class)
                .setSingleVmServerModuleClass(SkifTestServerModule.class)
                .setServiceMode(mode)
                .buildInjector();
    }

    public static class ClientModule extends SkifModule {

        public ClientModule(ModuleConfiguration moduleConfiguration) {
            super(moduleConfiguration);
        }

        @Override
        protected ModuleStrategyFactory defineDefaultModuleStrategyFactory() {
            return new ClientModuleStrategyFactory();
        }

        @Override
        protected void configure() {
            install(new RemoteServerModule(moduleConfiguration));
            install(new RemoteServiceModule(moduleConfiguration, new SkifTestGroupExServices().getServices(), new SkifTestMapper().getMapping()).
                    setExceptionMapping(new SkifTestExceptionMapper2().getMapping()));
        }
    }

    public TestExService setUpService(ServiceMode mode) {
        injector = createClientInjector(mode);
        setlogin();
        return injector.getInstance(TestExService.class);

    }

    /**
     * Test kall til Web service virker når det ikke genereres exception.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og bruker ikke rammeverket
     */
    @Test(dataProvider = "serverModesJEE")
    public void testNoExceptionNonMappedCall(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final TestExService service = setUpService(mode);

        // Sjekk at det er hull igjennom til servicen
        assertEquals(service.nonMappedCall("", "abc"), "abc");
    }

    /**
     * Web service kaster en runtime exception som JAX-WS Web service rammeverket på serveren automatisk gjør om
     * til en SOAPFaultException. På klienten mappes denne med en IdentityMapper.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og bruker ikke rammeverket
     */
    @Test(dataProvider = "serverModesJEE")
    public void testThrowNonMappedRuntimeExceptionNonMappedCall(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final TestExService service = setUpService(mode);

        try {
            service.nonMappedCall(RuntimeException.class.getName(), "abc");
        } catch (Throwable t) {

            assertEquals(t.getClass(), javax.xml.ws.soap.SOAPFaultException.class, "Forventet exception type");
            assertEquals(t.getLocalizedMessage(), "abc", "Excepted exception message");
        }
    }

    /**
     * Web service kaster en checked exception som JAX-WS Web service rammeverket på serveren sender videre uforandret
     * siden den er annotert med @WebFault. På klienten mappes denne ikke og det produseres en MappingException.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og bruker ikke rammeverket
     */
    @Test(dataProvider = "serverModesJEE")
    public void testThrowNonMappedCheckedExceptionNonMappedCall(ServiceMode mode) throws Exception {
        final TestExService service = setUpService(mode);

        try {
            service.nonMappedCall(no.statkart.skif.skiftest.wsapi.exception.SimpleNonMappedException.class.getName(), "abc");
        } catch (Throwable t) {
            String expectedMessage = String.format("TypeMapper[%s] has no mapper for for class: %s", SkifTestExceptionMapper2.class.getName(), no.statkart.skif.skiftest.wsapi.exception.SimpleNonMappedException.class.getName());

            assertEquals(t.getClass(), MappingException.class, "Forventet exception type");
            assertEquals(t.getLocalizedMessage(), expectedMessage, "Excepted exception message");
        }
    }

    /**
     * Web service kaster en checked exception som JAX-WS Web service rammeverket på serveren sender videre uforandret
     * siden den er annotert med @WebFault. På klienten mappes denne til en domene runtime exception med tilsvarende navn.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og bruker ikke rammeverket
     */
    @Test(dataProvider = "serverModesJEE")
    public void testThrowMappedExceptionNonMappedCall(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final TestExService service = setUpService(mode);

        try {
            service.nonMappedCall(no.statkart.skif.skiftest.wsapi.exception.SimpleException.class.getName(), "abc");
        } catch (SimpleException e) {
            assertEquals(e.getMessage(), "abc");
            assertEquals(e.getInfoField(), "infoFieldMessage");
        }
    }

    /**
     * Test kall til skif service virker når det ikke genereres exception
     */
    @Test(dataProvider = "serverModesJEE")
    public void testNoExceptionNoTx(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final TestExService service = setUpService(mode);

        // Sjekk at det er hull igjennom til servicen
        assertEquals(service.noTx("", "abc"), "abc noTx" );
    }


    /**
     * NoTx service kaster checked exception som mappes av server og klient. På server mappes exceptionen til en
     * wsapi exception annotert med @WebFault. På klient mappes wsapi excpetionen tilbake igjen til opprindelig exception
     * klasse (siden det brukes samme mapper på klient og server).
     */
    @Test(dataProvider = "serverModes")
    public void testThrowMappedExceptionNoTx(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final TestExService service = setUpService(mode);

        try {
            service.noTx(SimpleException.class.getName(), "abc");
        } catch (SimpleException e) {
            assertEquals(e.getMessage(), "abc noTx");
            assertEquals(e.getInfoField(), "infoFieldText");
        }
    }

    /**
     * RequiresTx service kaster checked exception som mappes av server og klient
     */
    @Test(dataProvider = "serverModes")
    public void testThrowMappedExceptionRequiredTx(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final TestExService service = setUpService(mode);

        try {
            service.requiresTx(SimpleException.class.getName(), "abc");
        } catch (SimpleException e) {
            assertEquals(e.getMessage(), "abc requiresTx");
            assertEquals(e.getInfoField(), "infoFieldText");
        }
    }

    /**
     * NewTx service kaster checked exception som mappes av server og klient
     */
    @Test(dataProvider = "serverModes")
    public void testThrowMappedExceptionNewTx(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final TestExService service = setUpService(mode);

        try {
            service.newTx(SimpleException.class.getName(), "abc");
        } catch (SimpleException e) {
            assertEquals(e.getMessage(), "abc newTx");
            assertEquals(e.getInfoField(), "infoFieldText");
        }
    }


    /**
     * NoTx service kaster checked exception som ikke kan mappes av server. Exceptionen gjøres derfor om på server
     * til en MappedException som JAX-WS gjør om til en SOAPFaultException.
     */
    @Test(dataProvider = "serverModesJEE")
    public void testThrowNonMappedExceptionNoTxJEE(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final TestExService service = setUpService(mode);

        try {
            service.noTx(SimpleNonMappedException.class.getName(), "abc");
        } catch (Throwable t) {
            String expectedMessage = String.format("TypeMapper[%s] has no mapper for for class: %s", SkifTestExceptionMapper2.class.getName(), SimpleNonMappedException.class.getName());

            assertEquals(t.getClass(), javax.xml.ws.soap.SOAPFaultException.class, "Forventet exception type");
            assertEquals(t.getLocalizedMessage(), expectedMessage, "Excepted exception message");
        }
    }

    /**
     * NoTx service kaster checked exception som ikke kan mappes av server, men siden mapping ikke brukes i SINGLE_VM
     * kommer exceptionen over til klient likevel.
     */
    @Test(dataProvider = "serverModesSVM", expectedExceptions = SimpleNonMappedException.class)
    public void testThrowNonMappedExceptionNoTxSVM(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final TestExService service = setUpService(mode);

        service.noTx(SimpleNonMappedException.class.getName(), "abc");
    }

    /**
     * RequiredTx service kaster checked exception som ikke kan mappes av server. Exceptionen gjøres derfor om på server
     * til en MappedException som JAX-WS gjør om til en SOAPFaultException.
     */
    @Test(dataProvider = "serverModesJEE")
    public void testThrowNonMappedExceptionRequiresTx(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final TestExService service = setUpService(mode);

        try {
            service.requiresTx(SimpleNonMappedException.class.getName(), "abc");
        } catch (Throwable t) {
            String expectedMessage = String.format("TypeMapper[%s] has no mapper for for class: %s", SkifTestExceptionMapper2.class.getName(), SimpleNonMappedException.class.getName());

            assertEquals(t.getClass(), javax.xml.ws.soap.SOAPFaultException.class, "Forventet exception type");
            assertEquals(t.getLocalizedMessage(), expectedMessage, "Excepted exception message");
        }

    }


    /**
     * NewTx service kaster checked exception som ikke kan mappes av server. Exceptionen gjøres derfor om på server
     * til en MappedException som JAX-WS gjør om til en SOAPFaultException.
     */
    @Test(dataProvider = "serverModesJEE")
    public void testThrowNonMappedExceptionNewTx(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final TestExService service = setUpService(mode);

        try {
            service.newTx(SimpleNonMappedException.class.getName(), "abc");
        } catch (Throwable t) {
            String expectedMessage = String.format("TypeMapper[%s] has no mapper for for class: %s", SkifTestExceptionMapper2.class.getName(), SimpleNonMappedException.class.getName());

            assertEquals(t.getClass(), javax.xml.ws.soap.SOAPFaultException.class, "Forventet exception type");
            assertEquals(t.getLocalizedMessage(), expectedMessage, "Excepted exception message");
        }
    }

    /**
     * Test kall til service virker når det ikke genereres exception. Kombinasjon indirect (3 typer) samt
     * indirect (3 typer) etterfulgt av direct (3 typer).
     */
    @Test(dataProvider = "serverModesSVM")
    public void testNoExceptionIndirectNoTx(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final TestExService service = setUpService(mode);

        // Sjekk at det er hull igjennom til servicen
        assertEquals(service.indirectNoTx(new ArrayList<String>(), "", ""), "indirectNoTx");
        assertEquals(service.indirectNoTx(Arrays.asList("noTx"), "", ""), "indirectNoTx noTx");
        assertEquals(service.indirectNoTx(Arrays.asList("requiresTx"), "", ""), "indirectNoTx requiresTx");
        assertEquals(service.indirectNoTx(Arrays.asList("newTx"), "", ""), "indirectNoTx newTx");
        assertEquals(service.indirectRequiresTx(new ArrayList<String>(), "", ""), "indirectRequiresTx");
        assertEquals(service.indirectRequiresTx(Arrays.asList("noTx"), "", ""), "indirectRequiresTx noTx");
        assertEquals(service.indirectRequiresTx(Arrays.asList("requiresTx"), "", ""), "indirectRequiresTx requiresTx");
        assertEquals(service.indirectRequiresTx(Arrays.asList("newTx"), "", ""), "indirectRequiresTx newTx");
        assertEquals(service.indirectNewTx(new ArrayList<String>(), "", ""), "indirectNewTx");
        assertEquals(service.indirectNewTx(Arrays.asList("noTx"), "", ""), "indirectNewTx noTx");
        assertEquals(service.indirectNewTx(Arrays.asList("requiresTx"), "", ""), "indirectNewTx requiresTx");
        assertEquals(service.indirectNewTx(Arrays.asList("newTx"), "", ""), "indirectNewTx newTx");
    }

    /**
     * Test kall til metode som kalder andre metoder. Kall indirectNoTx->noTx
     */
    @Test(dataProvider = "serverModes")
    public void testThrowMappedExceptionIndirectNoTxDirectNoTx(ServiceMode mode) throws SimpleNonMappedException {
        final TestExService service = setUpService(mode);
        try {
            service.indirectNoTx(Arrays.asList("noTx"), SimpleException.class.getName(), "");
        } catch (SimpleException e) {
            assertSame(e.getClass(), SimpleException.class);
            assertEquals(e.getMessage(), "indirectNoTx noTx");
            assertEquals(e.getInfoField(), "infoFieldText");
        }

    }

    /**
     * Test kall til metode som kalder andre metoder. Kall indirectNoTx->requiresTx
     */
    @Test(dataProvider = "serverModes")
    public void testThrowMappedExceptionIndirectNoTxDirectRequiresTx(ServiceMode mode) throws SimpleNonMappedException {
        final TestExService service = setUpService(mode);
        try {
            service.indirectNoTx(Arrays.asList("requiresTx"), SimpleException.class.getName(), "");
        } catch (SimpleException e) {
            assertSame(e.getClass(), SimpleException.class);
            assertEquals(e.getMessage(), "indirectNoTx requiresTx");
            assertEquals(e.getInfoField(), "infoFieldText");
        }

    }

    /**
     * Test kall til metode som kalder andre metoder. Kall indirectNoTx->newTx
     */
    @Test(dataProvider = "serverModes")
    public void testThrowMappedExceptionIndirectNoTxDirectNewTx(ServiceMode mode) throws SimpleNonMappedException {
        final TestExService service = setUpService(mode);
        try {
            service.indirectNoTx(Arrays.asList("newTx"), SimpleException.class.getName(), "");
        } catch (SimpleException e) {
            assertSame(e.getClass(), SimpleException.class);
            assertEquals(e.getMessage(), "indirectNoTx newTx");
            assertEquals(e.getInfoField(), "infoFieldText");
        }

    }


    /**
    * Test kall til metode som kalder andre metoder. Kall indirectNoTx->noTx
    */
    @Test(dataProvider = "serverModes")
    public void testThrowMappedExceptionIndirectRequiresTxDirectNoTx(ServiceMode mode) throws SimpleNonMappedException {
        final TestExService service = setUpService(mode);
        try {
            service.indirectRequiresTx(Arrays.asList("noTx"), SimpleException.class.getName(), "");
        } catch (SimpleException e) {
            assertSame(e.getClass(), SimpleException.class);
            assertEquals(e.getMessage(), "indirectRequiresTx noTx");
            assertEquals(e.getInfoField(), "infoFieldText");
        }

    }

    /**
     * Test kall til metode som kalder andre metoder. Kall indirectRequiresTx->requiresTx
     */
    @Test(dataProvider = "serverModes")
    public void testThrowMappedExceptionIndirectRequiresTxDirectRequiresTx(ServiceMode mode) throws SimpleNonMappedException {
        final TestExService service = setUpService(mode);
        try {
            service.indirectRequiresTx(Arrays.asList("requiresTx"), SimpleException.class.getName(), "");
        } catch (SimpleException e) {
            assertSame(e.getClass(), SimpleException.class);
            assertEquals(e.getMessage(), "indirectRequiresTx requiresTx");
            assertEquals(e.getInfoField(), "infoFieldText");
        }

    }

    /**
     * Test kall til metode som kalder andre metoder. Kall indirectRequiresTx->newTx
     */
    @Test(dataProvider = "serverModes")
    public void testThrowMappedExceptionIndirectRequiresTxDirectNewTx(ServiceMode mode) throws SimpleNonMappedException {
        final TestExService service = setUpService(mode);
        try {
            service.indirectRequiresTx(Arrays.asList("newTx"), SimpleException.class.getName(), "");
        } catch (SimpleException e) {
            assertSame(e.getClass(), SimpleException.class);
            assertEquals(e.getMessage(), "indirectRequiresTx newTx");
            assertEquals(e.getInfoField(), "infoFieldText");
        }

    }


    /**
    * Test kall til metode som kalder andre metoder. Kall indirectRequiresTx->newTx
    */
    @Test(dataProvider = "serverModesJEE")
    public void testThrowNonMappedExceptionIndirectRequiresTxDirectNewTx(ServiceMode mode) throws SimpleNonMappedException, SimpleException {
        final TestExService service = setUpService(mode);

        try {
            service.indirectRequiresTx(Arrays.asList("newTx"), SimpleNonMappedException.class.getName(), "");
        } catch (Throwable t) {
            String expectedMessage = String.format("TypeMapper[%s] has no mapper for for class: %s", SkifTestExceptionMapper2.class.getName(), SimpleNonMappedException.class.getName());

            assertEquals(t.getClass(), javax.xml.ws.soap.SOAPFaultException.class, "Forventet exception type");
            assertEquals(t.getLocalizedMessage(), expectedMessage, "Excepted exception message");
        }
    }

}