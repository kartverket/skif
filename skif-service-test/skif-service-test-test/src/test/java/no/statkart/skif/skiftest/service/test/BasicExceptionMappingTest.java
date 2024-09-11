package no.statkart.skif.skiftest.service.test;

import com.google.inject.Inject;
import no.statkart.skif.ServiceMode;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.skiftest.config.SkifTestServerModule;
import no.statkart.skif.skiftest.exception.SimpleException;
import no.statkart.skif.skiftest.exception.SimpleNonMappedException;
import no.statkart.skif.skiftest.service.testex.TestExService;
import no.statkart.skif.util.testsupport.SkifTestCase;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;
import static org.testng.Assert.assertTrue;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BasicExceptionMappingTest extends SkifTestCase {
    @Inject
    private TestExService service;

    public BasicExceptionMappingTest() {
        setModuleClass(SkifTestClientModule.class);
        setSingleVmServerModuleClass(SkifTestServerModule.class);
    }

    /**
     * NoTx service kaster checked exception som mappes av server og klient. På server mappes exceptionen til en
     * wsapi exception annotert med @WebFault. På klient mappes wsapi excpetionen tilbake igjen til opprindelig exception
     * klasse (siden det brukes samme mapper på klient og server).
     */
    @Test
    public void testThrowMappedExceptionNoTx() throws SimpleException, SimpleNonMappedException {
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
    @Test
    public void testThrowMappedExceptionRequiredTx() throws SimpleException, SimpleNonMappedException {
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
    @Test
    public void testThrowMappedExceptionNewTx() throws SimpleException, SimpleNonMappedException {
        try {
            service.newTx(SimpleException.class.getName(), "abc");
        } catch (SimpleException e) {
            assertEquals(e.getMessage(), "abc newTx");
            assertEquals(e.getInfoField(), "infoFieldText");
        }
    }

    /**
     * NoTx service kaster checked exception som ikke kan mappes av server.
     */
    @Test
    public void testThrowNonMappedExceptionNoTx() throws SimpleException, SimpleNonMappedException {
        try {
            service.noTx(SimpleNonMappedException.class.getName(), "abc");
        } catch (Throwable t) {
            ServiceMode serviceMode = injector.getInstance(ServiceMode.class);
            if (serviceMode == ServiceMode.SINGLE_VM) {
                assertEquals(t.getClass(), ImplementationException.class);
            } else if (serviceMode == ServiceMode.SINGLE_VM_XML) {
                assertEquals(t.getClass(), MappingException.class);
            } else {
                assertTrue(t instanceof jakarta.xml.ws.soap.SOAPFaultException);
            }
        }
    }

    /**
     * Test kall til service virker når det ikke genereres exception. Kombinasjon indirect (3 typer) samt
     * indirect (3 typer) etterfulgt av direct (3 typer).
     */
    @Test
    public void testNoExceptionIndirectNoTx() throws SimpleException, SimpleNonMappedException {
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
    @Test
    public void testThrowMappedExceptionIndirectNoTxDirectNoTx() throws SimpleNonMappedException {
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
    @Test
    public void testThrowMappedExceptionIndirectNoTxDirectRequiresTx() throws SimpleNonMappedException {
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
    @Test
    public void testThrowMappedExceptionIndirectNoTxDirectNewTx() throws SimpleNonMappedException {
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
    @Test
    public void testThrowMappedExceptionIndirectRequiresTxDirectNoTx() throws SimpleNonMappedException {
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
    @Test
    public void testThrowMappedExceptionIndirectRequiresTxDirectRequiresTx() throws SimpleNonMappedException {
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
    @Test
    public void testThrowMappedExceptionIndirectRequiresTxDirectNewTx() throws SimpleNonMappedException {
        try {
            service.indirectRequiresTx(Arrays.asList("newTx"), SimpleException.class.getName(), "");
        } catch (SimpleException e) {
            assertSame(e.getClass(), SimpleException.class);
            assertEquals(e.getMessage(), "indirectRequiresTx newTx");
            assertEquals(e.getInfoField(), "infoFieldText");
        }

    }
}