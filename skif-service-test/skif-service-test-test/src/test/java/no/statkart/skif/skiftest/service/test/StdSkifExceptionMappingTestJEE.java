package no.statkart.skif.skiftest.service.test;

import com.google.inject.Inject;
import no.statkart.skif.exception.FinderException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ServerException;
import no.statkart.skif.exception.SkifException;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.skiftest.exception.SimpleException;
import no.statkart.skif.skiftest.exception.SimpleNonMappedException;
import no.statkart.skif.skiftest.service.testd.DService;
import no.statkart.skif.skiftest.wsapi.exception.impl.mapping.SkifTestExceptionMapper;
import no.statkart.skif.util.testsupport.SkifTestCase;
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
 * @since 2.0
 */
@Test(groups = "server-required")
public class StdSkifExceptionMappingTestJEE extends SkifTestCase {
    @Inject
    DService service;

    public StdSkifExceptionMappingTestJEE() {
        setModuleClass(SkifTestClientModule.class);
        setSingleVmServerModuleClass(SkifTestServerModule.class);
        setSingleVm(false);
    }

    /**
     * Test kall til Web service virker når det ikke genereres exception.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og gjør ikke kall videre
     */
    @Test(groups = "server-required")
    public void testNoExceptionNonMappedWSCall() throws SimpleException, SimpleNonMappedException {
        // Sjekk at det er hull igjennom til servicen
        assertEquals(service.nonMappedWSCall("", "abc"), "abc");
    }

    /**
     * Web service kaster en runtime exception som JAX-WS Web service rammeverket på serveren automatisk gjør om
     * til en SOAPFaultException. På klienten mappes denne med en IdentityMapper.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og gjør ikke kall videre
     */
    @Test(groups = "server-required", expectedExceptions = SOAPFaultException.class, expectedExceptionsMessageRegExp = "abc")
    public void testThrowNonMappedRuntimeExceptionNonMappedWSCall() throws SimpleException, SimpleNonMappedException {
        service.nonMappedWSCall(RuntimeException.class.getName(), "abc");
    }

    /**
     * Web service kaster en checked exception som JAX-WS Web service rammeverket på serveren sender videre uforandret
     * siden den er annotert med @WebFault. På klienten mappes denne ikke og det produseres en MappingException.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og gjør ikke kall videre
     */
    @Test(groups = "server-required")
    public void testThrowNonMappedCheckedExceptionNonMappedWSCall() throws SimpleException, SimpleNonMappedException {
        try {
            service.nonMappedWSCall("simple.SimpleNonMappedException", "abc");
        } catch (Throwable t) {
            String expectedMessage = String.format("Mapper[no.statkart.skif.skiftest.wsapi.exception.impl.mapping.SkifTestExceptionMapper] could not map from %s to %s", no.statkart.skif.skiftest.wsapi.exception.SimpleNonMappedException.class.getName(), Throwable.class.getName());

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
    @Test(groups = "server-required")
    public void testThrowMappedExceptionNonMappedWSCall() throws SimpleException, SimpleNonMappedException {
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
    @Test(groups = "server-required")
    public void testNoExceptionNonMappedEJBCall() throws SimpleException, SimpleNonMappedException {
        // Sjekk at det er hull igjennom til servicen
        assertEquals(service.nonMappedEJBCall("", "abc"), "abc");
    }

    /**
     * Web service kaster en runtime exception som JAX-WS Web service rammeverket på serveren automatisk gjør om
     * til en SOAPFaultException. På klienten mappes denne med en IdentityMapper.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og gjør ikke kall videre
     */
    @Test(groups = "server-required")
    public void testThrowNonMappedRuntimeExceptionNonMappedEJBCall() throws SimpleException, SimpleNonMappedException {
        try {
            service.nonMappedEJBCall(RuntimeException.class.getName(), "abc");
        } catch (SkifException e) {
            assertInstanceOf(e, ImplementationException.class, "mapped exception class");
            assertEquals(e.getMessage(), "abc");
            assertTrue(e.getCause().getClass()==RuntimeException.class || e.getCause().getClass()==ServerException.class);

            // Server mapper automatisk ukjendte exception ved å wrapped dem i en  ImplementationException først.
            // Stacktrace settes til det samme som opprindelig exception slik at det er enklet å se hvor feilen opprindelig forekom
            final StackTraceElement stackTraceElement = e.getStackTrace()[0];
            assertEquals(stackTraceElement.getClassName(), "no.statkart.skif.skiftest.service.testd.DServiceEJBBean");
            assertEquals(stackTraceElement.getFileName(), "DServiceEJBBean.java");
            assertEquals(stackTraceElement.getMethodName(), "nonMappedEJBCall");
            assertTrue(stackTraceElement.getLineNumber() > 0);

            // Sjekk cause
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
    @Test(groups = "server-required")
    public void testThrowMappedExceptionNonMappedEJBCall() throws SimpleException, SimpleNonMappedException {
        try {
            service.nonMappedEJBCall("ikke vesentlig", "abc");
        } catch (SkifException e) {
            assertInstanceOf(e, ImplementationException.class, "mapped exception class");
            assertEquals(e.getMessage(), "abc");
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