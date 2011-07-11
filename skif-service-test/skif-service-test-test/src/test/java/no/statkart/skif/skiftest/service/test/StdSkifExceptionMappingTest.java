package no.statkart.skif.skiftest.service.test;

import com.google.inject.Injector;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.SkifModule;
import no.statkart.skif.exception.FinderException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ServerException;
import no.statkart.skif.exception.SkifException;
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
import no.statkart.skif.skiftest.config.SkifTestGroupABCDServices;
import no.statkart.skif.skiftest.config.SkifTestGroupExServices;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.skiftest.exception.SimpleException;
import no.statkart.skif.skiftest.exception.SimpleNonMappedException;
import no.statkart.skif.skiftest.service.testd.DService;
import no.statkart.skif.skiftest.wsapi.exception.impl.mapping.SkifTestExceptionMapper;
import no.statkart.skif.skiftest.wsapi.exception.simple.mapping.SkifTestExceptionMapper2;
import no.statkart.skif.skiftest.wsapi.mapping.SkifTestMapper;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import javax.xml.ws.soap.SOAPFaultException;

import static org.testng.Assert.*;

/**
 * Tester mapping av skifs standard exception hierarki. Et viktig aspekt av exception mappingen er at det i stor
 * grad skal være transparent om JEE eller SINGLE_VM mode brukes.
 *
 * Denne testen viser også hvordan skif kan konfigureres slik at  serveren  automatisk wrapper alle ukjente runtime
 * exceptions i en ImplementationException. Dette skjer både i JEE og SINGLE_VM mode.
 *
 * @author Henrik Fredholm
 * @since 1.1
 */
@Test(groups = "server-required")
public class StdSkifExceptionMappingTest {
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
            install(new RemoteServiceModule(moduleConfiguration, new SkifTestGroupABCDServices().getServices(), new SkifTestMapper().getMapping()).
                    setExceptionMapping(new SkifTestExceptionMapper().getMapping()));
            install(new RemoteServiceModule(moduleConfiguration, new SkifTestGroupExServices().getServices(), new SkifTestMapper().getMapping()).
                    setExceptionMapping(new SkifTestExceptionMapper2().getMapping()));
        }
    }

    public DService setUpService(ServiceMode mode) {
        injector = createClientInjector(mode);
        setlogin();
        return injector.getInstance(DService.class);

    }

    /**
     * Test kall til Web service virker når det ikke genereres exception.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og gjør ikke kall videre
     */
    @Test(dataProvider = "serverModesJEE")
    public void testNoExceptionNonMappedWSCall(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final DService service = setUpService(mode);

        // Sjekk at det er hull igjennom til servicen
        assertEquals(service.nonMappedWSCall("", "abc"), "abc");
    }

    /**
     * Web service kaster en runtime exception som JAX-WS Web service rammeverket på serveren automatisk gjør om
     * til en SOAPFaultException. På klienten mappes denne med en IdentityMapper.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og gjør ikke kall videre
     */
    @Test(dataProvider = "serverModesJEE", expectedExceptions = SOAPFaultException.class, expectedExceptionsMessageRegExp = "abc")
    public void testThrowNonMappedRuntimeExceptionNonMappedWSCall(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final DService service = setUpService(mode);

        service.nonMappedWSCall(RuntimeException.class.getName(), "abc");
    }

    /**
     * Web service kaster en checked exception som JAX-WS Web service rammeverket på serveren sender videre uforandret
     * siden den er annotert med @WebFault. På klienten mappes denne ikke og det produseres en MappingException.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og gjør ikke kall videre
     */
    @Test(dataProvider = "serverModesJEE")
    public void testThrowNonMappedCheckedExceptionNonMappedWSCall(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final DService service = setUpService(mode);

        try {
            service.nonMappedWSCall("simple.SimpleNonMappedException", "abc");
        } catch (Throwable t) {
            String expectedMessage = String.format("TypeMapper[%s] has no mapper for for class: %s", SkifTestExceptionMapper.class.getName(), no.statkart.skif.skiftest.wsapi.exception.SimpleNonMappedException.class.getName());

            assertEquals(t.getClass(), MappingException.class, "Forventet exception type");
            assertEquals(t.getLocalizedMessage(), expectedMessage, "Excepted exception message");
        }
    }

    /**
     * Web service kaster en checked exception som JAX-WS Web service rammeverket på serveren sender videre uforandret
     * siden den er annotert med @WebFault. På klienten gjenkjennes denne og mappes til en domene runtime exception med
     * tilsvarende navn.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og gjør ikke kall videre
     */
    @Test(dataProvider = "serverModesJEE")
    public void testThrowMappedExceptionNonMappedWSCall(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final DService service = setUpService(mode);

        try {
            service.nonMappedWSCall("impl.ImplementationException", "abc");
        } catch (SkifException e) {
            assertInstanceOf(e, ImplementationException.class, "mapped exception class");
            assertEquals(e.getMessage(), "abc");
        } catch (Throwable t) {
            fail("Ikke forventet feil: ", t);
        }
    }


    /**
     * Test kall til Web service virker når det ikke genereres exception.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og gjør ikke kall videre
     */
    @Test(dataProvider = "serverModesJEE")
    public void testNoExceptionNonMappedEJBCall(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final DService service = setUpService(mode);

        // Sjekk at det er hull igjennom til servicen
        assertEquals(service.nonMappedEJBCall("", "abc"), "abc");
    }

    /**
     * Web service kaster en runtime exception som JAX-WS Web service rammeverket på serveren automatisk gjør om
     * til en SOAPFaultException. På klienten mappes denne med en IdentityMapper.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og gjør ikke kall videre
     */
    @Test(dataProvider = "serverModesJEE")
    public void testThrowNonMappedRuntimeExceptionNonMappedEJBCall(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final DService service = setUpService(mode);

        try {
            service.nonMappedEJBCall(RuntimeException.class.getName(), "abc");
        } catch (SkifException e) {
            assertInstanceOf(e, ImplementationException.class, "mapped exception class");
            assertEquals(e.getMessage(), "abc");
            assertTrue(e.getCause().getClass()==RuntimeException.class || e.getCause().getClass()==ServerException.class);
            assertEquals(e.getFeilkode(), "IE000");
            assertEquals(e.getFeilkodebeskrivelse(), "Implementasjonsfeil");

            final StackTraceElement stackTraceElement = e.getStackTrace()[0];
            assertEquals(stackTraceElement.getClassName(), "no.statkart.skif.mapper.AbstractExceptionMapper");
            assertEquals(stackTraceElement.getFileName(), "AbstractExceptionMapper.java");
            assertEquals(stackTraceElement.getMethodName(), "d2w");
            assertTrue(stackTraceElement.getLineNumber() > 0);

            // Sjekk exception
            assertTrue(e.getCause().getMessage().endsWith("abc"));
            final StackTraceElement causeStackTraceElement = e.getCause().getStackTrace()[0];
            assertEquals(causeStackTraceElement.getClassName(), "no.statkart.skif.skiftest.service.testd.DServiceEJBBean");
            assertEquals(causeStackTraceElement.getFileName(), "DServiceEJBBean.java");
            assertEquals(causeStackTraceElement.getMethodName(), "nonMappedEJBCall");
            assertTrue(causeStackTraceElement.getLineNumber() > 0);

        } catch (Throwable t) {
            fail("Ikke forventet feil: ", t);
        }
    }

    /**
     * Web service kaster en checked exception som JAX-WS Web service rammeverket på serveren sender videre uforandret
     * siden den er annotert med @WebFault. På klienten gjenkjennes denne og mappes til en domene runtime exception med
     * tilsvarende navn.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og gjør ikke kall videre
     */
    @Test(dataProvider = "serverModesJEE")
    public void testThrowMappedExceptionNonMappedEJBCall(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final DService service = setUpService(mode);

        try {
            service.nonMappedEJBCall("ikke vesentlig", "abc");
        } catch (SkifException e) {
            assertInstanceOf(e, ImplementationException.class, "mapped exception class");
            assertEquals(e.getMessage(), "abc");
        } catch (Throwable t) {
            fail("Ikke forventet feil: ", t);
        }
    }

    /**
     * Test kall til skif service virker når det ikke genereres exception
     */
    @Test(dataProvider = "serverModes")
    public void testNoExceptionNoTx(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final DService service = setUpService(mode);

        // Sjekk at det er hull igjennom til servicen
        assertEquals(service.noTx("", "abc"), "[NoTx:abc noTx]" );
    }


    /**
     * NoTx service kaster ImplementationException som mappes av server og klient. På server mappes exceptionen til en
     * wsapi exception annotert med @WebFault. På klient mappes wsapi excpetionen tilbake igjen til opprindelig exception
     * klasse (siden det brukes samme mapper på klient og server).
     */
    @Test(dataProvider = "serverModes")
    public void testThrowMappedImplementationExceptionNoTx(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final DService service = setUpService(mode);

        try {
            service.noTx(ImplementationException.class.getName(), "abc");
        } catch (SkifException e) {
            assertInstanceOf(e, ImplementationException.class, "mapped exception class");
            assertEquals(e.getMessage(), "abc noTx");
            assertNull(e.getCause());
            assertEquals(e.getFeilkode(), "feilkode");
            assertEquals(e.getFeilkodebeskrivelse(), "feilkodebeskrivelse");
            final StackTraceElement stackTraceElement = e.getStackTrace()[0];
            assertEquals(stackTraceElement.getClassName(), "no.statkart.skif.skiftest.service.testd.DServiceImpl");
            assertEquals(stackTraceElement.getFileName(), "DServiceImpl.java");
            assertEquals(stackTraceElement.getMethodName(), "createException");
            assertTrue(stackTraceElement.getLineNumber() > 0);
        } catch (Throwable t) {
            fail("Ikke forventet feil: ", t);
        }
    }

    /**
     * NoTx service kaster FinderException som mappes av server og klient. På server mappes exceptionen til en
     * wsapi exception annotert med @WebFault. På klient mappes wsapi excpetionen tilbake igjen til opprindelig exception
     * klasse (siden det brukes samme mapper på klient og server).
     */
    @Test(dataProvider = "serverModes")
    public void testThrowMappedFinderExceptionNoTx(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final DService service = setUpService(mode);

        try {

            service.noTx(FinderException.class.getName(), "abc");
        } catch (SkifException e) {
            assertInstanceOf(e, FinderException.class, "mapped exception class");
            assertEquals(e.getMessage(), "abc noTx");
//            assertNull(e.getCause());
//            assertEquals(e.getFeilkode(), "feilkode");
//            assertEquals(e.getFeilkodebeskrivelse(), "feilkodebeskrivelse");
//            final StackTraceElement stackTraceElement = e.getStackTrace()[0];
//            assertEquals(stackTraceElement.getClassName(), "no.statkart.skif.skiftest.service.testd.DServiceImpl");
//            assertEquals(stackTraceElement.getFileName(), "DServiceImpl.java");
//            assertEquals(stackTraceElement.getMethodName(), "createException");
//            assertTrue(stackTraceElement.getLineNumber() > 0);
        } catch (Throwable t) {
            fail("Ikke forventet feil: ", t);
        }
    }

    /**
     * NoTx service som kaster en RuntimeException som ikke er subtype av SkifException. Denne fanges opp på server i en
     * ProxyHandler som wrapper den i en ImplementationException som mappes av server og klient. I JEE mode
     * gjøres RuntimeExcpetion om til en ServerException. Dette skjer på klient.
     *
     */
    @Test(dataProvider = "serverModes")
    public void testThrowRuntimeExceptionNoTx(ServiceMode mode) throws SimpleException, SimpleNonMappedException {
        final DService service = setUpService(mode);

        try {
            service.noTx(RuntimeException.class.getName(), "abc");
        } catch (SkifException e) {
            assertInstanceOf(e, ImplementationException.class, "mapped exception class");
            assertEquals(e.getMessage(), "abc noTx");
            assertTrue(e.getCause().getClass()==RuntimeException.class || e.getCause().getClass()==ServerException.class);
            assertEquals(e.getFeilkode(), "IE000");
            assertEquals(e.getFeilkodebeskrivelse(), "Implementasjonsfeil");

            final StackTraceElement stackTraceElement = e.getStackTrace()[0];
            assertEquals(stackTraceElement.getClassName(), "no.statkart.skif.service.proxy.RuntimeExceptionProxyHandler");
            assertEquals(stackTraceElement.getFileName(), "RuntimeExceptionProxyHandler.java");
            assertEquals(stackTraceElement.getMethodName(), "invokeMethod");
            assertTrue(stackTraceElement.getLineNumber() > 0);

            // Sjekk exception
            assertTrue(e.getCause().getMessage().endsWith("abc noTx"));
            final StackTraceElement causeStackTraceElement = e.getCause().getStackTrace()[0];
            assertEquals(causeStackTraceElement.getClassName(), "no.statkart.skif.skiftest.service.testd.DServiceImpl");
            assertEquals(causeStackTraceElement.getFileName(), "DServiceImpl.java");
            assertEquals(causeStackTraceElement.getMethodName(), "createException");
            assertTrue(causeStackTraceElement.getLineNumber() > 0);
        } catch (Throwable t) {
            fail("Ikke forventet feil: ", t);
        }
    }

    static void assertInstanceOf(Throwable e, Class<? extends Throwable> aClass, String message) {
        if (!aClass.isAssignableFrom(e.getClass())) {
            fail(String.format("%s is not assignable to %s: %s", e.getClass().getName(), aClass.getName(), message));
        }
    }

}