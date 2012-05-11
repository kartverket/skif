package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;

/**
 * Tester mixed kjørsel på klient og tjener.
 *
 * Klient og tjener kjøre i forskjellige omgivelser og skal ikke dele sekvenser.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test
public class StoreServerTest extends StoreTestServerTestCase {

    @Inject
    Store clientStore;

    public void testMixIdAllocation() {
        TestBubble testBubbleOnClient = new TestBubble();
        clientStore.beginUnitOfWork();
        clientStore.insert(testBubbleOnClient);

        TestBubble testBubbleFromServer = (TestBubble) server.runInTxSupported(new RunOnServerMethod() {
            @Inject
            Store storeOnServer;
            @Override
            public Object run() {
                TestBubble testBubbleOnServer = new TestBubble();
                storeOnServer.insert(testBubbleOnServer);

                return testBubbleOnServer;
            }
        });
        assertFalse(testBubbleOnClient.getId().equals(testBubbleFromServer.getId()));

        TestBubble testBubbleFromServer2 = (TestBubble) server.runInTxSupported(new RunOnServerMethod() {
            @Inject
            Store storeOnServer;
            @Override
            public Object run() {
                TestBubble testBubbleOnServer = new TestBubble();
                storeOnServer.insert(testBubbleOnServer);

                return testBubbleOnServer;
            }
        });
        // Test at sekvens på objekt opprettet på serveren er en større enn forrige server objekt
        assertEquals(testBubbleFromServer.getId().getValue().longValue() +1, testBubbleFromServer2.getId().getValue().longValue());
        TestBubble testBubbleOnClient2 = new TestBubble();
        clientStore.insert(testBubbleOnClient2);
        // Test at sekvens på objekt opprettet på klientn er en større enn forrige server objekt
        assertEquals(testBubbleOnClient.getId().getValue().longValue() +1, testBubbleOnClient2.getId().getValue().longValue());



        clientStore.abortUnitOfWork();


    }
}
