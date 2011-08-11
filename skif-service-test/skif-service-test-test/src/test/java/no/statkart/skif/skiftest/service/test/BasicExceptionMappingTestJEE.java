package no.statkart.skif.skiftest.service.test;

import com.google.inject.Inject;
import com.google.inject.Module;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.ConfigurationConstants;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.service.LoginUser;
import no.statkart.skif.service.LoginUserHolder;
import no.statkart.skif.service.ServerUrlHolder;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.skiftest.exception.SimpleException;
import no.statkart.skif.skiftest.exception.SimpleNonMappedException;
import no.statkart.skif.skiftest.service.testex.TestExService;
import no.statkart.skif.skiftest.wsapi.exception.simple.mapping.SkifTestExceptionMapper2;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.annotations.Test;

import javax.xml.ws.soap.SOAPFaultException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test(groups = "server-required")
public class BasicExceptionMappingTestJEE extends SkifTestCase {

    @Inject
    private TestExService service;

    public BasicExceptionMappingTestJEE() {
        setModuleClass(SkifTestClientModule.class);
        setSingleVmServerModuleClass(SkifTestServerModule.class);
        setSingleVm(false);
    }

    /**
     * Test kall til Web service virker når det ikke genereres exception.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og bruker ikke rammeverket
     */
    @Test(groups = "server-required")
    public void testNoExceptionNonMappedCall() throws SimpleException, SimpleNonMappedException {
        // Sjekk at det er hull igjennom til servicen
        assertEquals(service.nonMappedCall("", "abc"), "abc");
    }

    /**
     * Web service kaster en runtime exception som JAX-WS Web service rammeverket på serveren automatisk gjør om
     * til en SOAPFaultException. På klienten mappes denne med en IdentityMapper.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og bruker ikke rammeverket
     */
    @Test(groups = "server-required")
    public void testThrowNonMappedRuntimeExceptionNonMappedCall() throws SimpleException, SimpleNonMappedException {
        try {
            service.nonMappedCall(RuntimeException.class.getName(), "abc");
        } catch (Throwable t) {

            assertEquals(t.getClass(), SOAPFaultException.class, "Forventet exception type");
            assertEquals(t.getLocalizedMessage(), "abc", "Excepted exception message");
        }
    }

    /**
     * Web service kaster en checked exception som JAX-WS Web service rammeverket på serveren sender videre uforandret
     * siden den er annotert med @WebFault. På klienten mappes denne ikke og det produseres en MappingException.
     * <p/>
     * NB: Kallt Web Service metode er implementert direkte i WSBean klassen og bruker ikke rammeverket
     */
    @Test(groups = "server-required")
    public void testThrowNonMappedCheckedExceptionNonMappedCall() throws Exception {
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
    @Test(groups = "server-required")
    public void testThrowMappedExceptionNonMappedCall() throws SimpleException, SimpleNonMappedException {
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
    @Test(groups = "server-required")
    public void testNoExceptionNoTx() throws SimpleException, SimpleNonMappedException {
        // Sjekk at det er hull igjennom til servicen
        assertEquals(service.noTx("", "abc"), "abc noTx");
    }

    /**
     * NoTx service kaster checked exception som ikke kan mappes av server. Mapping rammeverket vil automatisk gjøre
     * exceptionen om til en SOAPFaultExceptionExceptionen gjøres derfor om på server
     * til en MappedException som JAX-WS gjør om til en SOAPFaultException.
     */
    @Test(groups = "server-required")
    public void testThrowNonMappedExceptionNoTxJEE() throws SimpleException, SimpleNonMappedException {
        try {
            service.noTx(SimpleNonMappedException.class.getName(), "abc");
        } catch (Throwable t) {
            String expectedMessage = String.format("TypeMapper[%s] has no mapper for for class: %s", SkifTestExceptionMapper2.class.getName(), SimpleNonMappedException.class.getName());

            assertEquals(t.getClass(), SOAPFaultException.class, "Forventet exception type");
            assertEquals(t.getLocalizedMessage(), expectedMessage, "Excepted exception message");
        }
    }

    /**
     * RequiredTx service kaster checked exception som ikke kan mappes av server. Exceptionen gjøres derfor om på server
     * til en MappedException som JAX-WS gjør om til en SOAPFaultException.
     */
    @Test(groups = "server-required")
    public void testThrowNonMappedExceptionRequiresTx() throws SimpleException, SimpleNonMappedException {
        try {
            service.requiresTx(SimpleNonMappedException.class.getName(), "abc");
        } catch (Throwable t) {
            String expectedMessage = String.format("TypeMapper[%s] has no mapper for for class: %s", SkifTestExceptionMapper2.class.getName(), SimpleNonMappedException.class.getName());

            assertEquals(t.getClass(), SOAPFaultException.class, "Forventet exception type");
            assertEquals(t.getLocalizedMessage(), expectedMessage, "Excepted exception message");
        }

    }


    /**
     * NewTx service kaster checked exception som ikke kan mappes av server. Exceptionen gjøres derfor om på server
     * til en MappedException som JAX-WS gjør om til en SOAPFaultException.
     */
    @Test(groups = "server-required")
    public void testThrowNonMappedExceptionNewTx() throws SimpleException, SimpleNonMappedException {
        try {
            service.newTx(SimpleNonMappedException.class.getName(), "abc");
        } catch (Throwable t) {
            String expectedMessage = String.format("TypeMapper[%s] has no mapper for for class: %s", SkifTestExceptionMapper2.class.getName(), SimpleNonMappedException.class.getName());

            assertEquals(t.getClass(), SOAPFaultException.class, "Forventet exception type");
            assertEquals(t.getLocalizedMessage(), expectedMessage, "Excepted exception message");
        }
    }

    /**
     * Test kall til metode som kalder andre metoder. Kall indirectRequiresTx->newTx
     */
    @Test(groups = "server-required")
    public void testThrowNonMappedExceptionIndirectRequiresTxDirectNewTx() throws SimpleNonMappedException, SimpleException {
        try {
            service.indirectRequiresTx(Arrays.asList("newTx"), SimpleNonMappedException.class.getName(), "");
        } catch (Throwable t) {
            String expectedMessage = String.format("TypeMapper[%s] has no mapper for for class: %s", SkifTestExceptionMapper2.class.getName(), SimpleNonMappedException.class.getName());

            assertEquals(t.getClass(), SOAPFaultException.class, "Forventet exception type");
            assertEquals(t.getLocalizedMessage(), expectedMessage, "Excepted exception message");
        }
    }
}