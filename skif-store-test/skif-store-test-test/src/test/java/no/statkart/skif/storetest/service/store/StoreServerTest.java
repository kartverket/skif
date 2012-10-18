package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.demo.*;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * Tester mixed kjørsel på klient og tjener.
 * <p/>
 * Klient og tjener kjøre i forskjellige omgivelser og skal ikke dele sekvenser.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test
public class StoreServerTest extends StoreTestMixedTestCase {

    public static final FooId<Foo> FOO_ID_100 = new FooId<Foo>(100L);
    @Inject
    Store clientStore;

    public void testMixIdAllocation() {
        TestBubble testBubbleOnClient = new TestBubble();
        clientStore.beginUnitOfWork();
        clientStore.insert(testBubbleOnClient);

        TestBubble testBubbleFromServer = (TestBubble) server.runInTxNotSupported(new RunOnServerMethod() {
            @Inject
            Store storeOnServer;

            @Override
            public Object run() {
                TestBubble testBubbleOnServer = new TestBubble();
                // TODO: Burde egentlig feile siden det gjøre en oppdatering og metoden ikke har transaksjonskontekst
                storeOnServer.insert(testBubbleOnServer);

                return testBubbleOnServer;
            }
        });
        assertFalse(testBubbleOnClient.getId().equals(testBubbleFromServer.getId()));

        TestBubble testBubbleFromServer2 = (TestBubble) server.runInTxNotSupported(new RunOnServerMethod() {
            @Inject
            Store storeOnServer;

            @Override
            public Object run() {
                TestBubble testBubbleOnServer = new TestBubble();
                // TODO: Burde egentlig feile siden det gjøre en oppdatering og metoden ikke har transaksjonskontekst
                storeOnServer.insert(testBubbleOnServer);

                return testBubbleOnServer;
            }
        });
        // Test at sekvens på objekt opprettet på serveren er en større enn forrige server objekt
        assertEquals(testBubbleFromServer.getId().getValue().longValue() + 1, testBubbleFromServer2.getId().getValue().longValue());
        TestBubble testBubbleOnClient2 = new TestBubble();
        clientStore.insert(testBubbleOnClient2);
        // Test at sekvens på objekt opprettet på klientn er en større enn forrige server objekt
        assertEquals(testBubbleOnClient.getId().getValue().longValue() + 1, testBubbleOnClient2.getId().getValue().longValue());

        clientStore.abortUnitOfWork();
    }

    public void testUpdateEntityComponenet() {

        // Opprett BubbleObject som inneholder en EntityComponenet som automatisk tildeles id ved lagring
        final Raz testBubbleFromServer = (Raz) server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store storeOnServer;

            @Override
            public Object run() {
                Raz raz = new Raz();
                raz.setText("Foo");
                RazComponent razComponent = new RazComponent();
                razComponent.setFooId(FOO_ID_100);
                razComponent.setCompText("Bar");
                raz.setRazComponent(razComponent);
                raz.setRazEntityComponent(new RazEntityComponent("test"));
                storeOnServer.insert(raz);
                return raz;
            }
        });
        assertNotNull(testBubbleFromServer.getId());

        // Oppdater ComponentWithExplicitId
        Raz testBubbleFromServer2 = (Raz) server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store storeOnServer;

            @Override
            public Object run() {
                Raz raz = storeOnServer.lock(testBubbleFromServer.getId());
                raz.getRazEntityComponent().setComponentName("updated");
                storeOnServer.update(raz);
                return raz;
            }
        });

        // Slett EntityComponenet. Hibernate bruker delete-orphan
        Raz testBubbleFromServer3 = (Raz) server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store storeOnServer;

            @Override
            public Object run() {
                Raz raz = storeOnServer.lock(testBubbleFromServer.getId());
                raz.setRazEntityComponent(null);
                storeOnServer.update(raz);
                return raz;
            }
        });

    }

    public void testUpdateEntityComponenet_virker_ikke() {

        // Opprett BubbleObject som inneholder en EntityComponenet som automatisk tildeles id ved lagring
        final Raz testBubbleFromServer = (Raz) server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store storeOnServer;

            @Override
            public Object run() {
                Raz raz = new Raz();
                raz.setText("Foo");
                RazComponent razComponent = new RazComponent();
                razComponent.setFooId(FOO_ID_100);
                razComponent.setCompText("Bar");
                raz.setRazComponent(razComponent);
                raz.setRazEntityComponent(new RazEntityComponent("test"));
                storeOnServer.insert(raz);
                return raz;
            }
        });
        assertNotNull(testBubbleFromServer.getId());

        // Oppdater ComponentWithExplicitId med ny ComponentWithExplicitId. Den opprinnelige EntiyComponent blir feilaktig liggende igjen.
        // TODO: Hibernate delete-orphan virker ikke!
        System.out.println("Denne blir liggende igjen: " + testBubbleFromServer.getRazEntityComponent().getId());
        Raz testBubbleFromServer2 = (Raz) server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store storeOnServer;

            @Override
            public Object run() {
                Raz raz = storeOnServer.lock(testBubbleFromServer.getId());
                //raz = CopyHelper.copy(raz);
                final RazEntityComponent component = new RazEntityComponent("test1");
                //component.setId(raz.getRazEntityComponent().getId());
                raz.setRazEntityComponent(component);
                storeOnServer.update(raz);
                return raz;
            }
        });

        System.out.println("Denne blir slettet: " + testBubbleFromServer2.getRazEntityComponent().getId());
        // Slett Raz boble
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            Store storeOnServer;

            @Override
            public Object run() {
                Raz raz = storeOnServer.lock(testBubbleFromServer.getId());
                storeOnServer.delete(raz);
                return null;
            }
        });

    }

}
