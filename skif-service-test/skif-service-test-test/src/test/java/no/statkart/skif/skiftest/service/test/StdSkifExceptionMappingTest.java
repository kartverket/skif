package no.statkart.skif.skiftest.service.test;

import com.google.inject.Inject;
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
import no.statkart.skif.util.testsupport.SkifTestCase;
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
public class StdSkifExceptionMappingTest extends SkifTestCase {

    @Inject
    DService service;

    public StdSkifExceptionMappingTest() {
        setModuleClass(SkifTestClientModule.class);
        setSingleVmServerModuleClass(SkifTestServerModule.class);
    }

    /**
     * Test kall til skif service virker når det ikke genereres exception
     */
    @Test
    public void testNoExceptionNoTx() throws SimpleException, SimpleNonMappedException {
        // Sjekk at det er hull igjennom til servicen
        assertEquals(service.noTx("", "abc"), "[NoTx:abc noTx]" );
    }


    /**
     * NoTx service kaster ImplementationException som mappes av server og klient. På server mappes exceptionen til en
     * wsapi exception annotert med @WebFault. På klient mappes wsapi excpetionen tilbake igjen til opprindelig exception
     * klasse (siden det brukes samme mapper på klient og server).
     */
    @Test
    public void testThrowMappedImplementationExceptionNoTx() throws SimpleException, SimpleNonMappedException {
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
    @Test
    public void testThrowMappedFinderExceptionNoTx() throws SimpleException, SimpleNonMappedException {
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
    @Test
    public void testThrowRuntimeExceptionNoTx() throws SimpleException, SimpleNonMappedException {
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