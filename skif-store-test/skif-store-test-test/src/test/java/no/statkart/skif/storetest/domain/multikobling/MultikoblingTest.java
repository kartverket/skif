package no.statkart.skif.storetest.domain.multikobling;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.multikobling.Multikobling;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Tester {@link no.statkart.skif.store.multikobling.Multikobling} via {@link Multirefererende}.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
@Test
public class MultikoblingTest extends StoreTestTestCase {
    @Inject
    private StoreTestMockupFacadeFactory mockupFacadeFactory;

    @Test(groups = "singlevm-required")
    public void testPersistens() {
        final StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getWriteMockupFacade();

        final MultirefererendeId<?> id = mockupFacade.getIdService().getNextId(MultirefererendeId.class);

        RunOnServerWithTxRequiresNewService runOnServerService = injector.getInstance(RunOnServerWithTxRequiresNewService.class);

        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                Multirefererende multirefererende = new Multirefererende();
                multirefererende.setId(id);

                multirefererende.getMultikobling().get("Over").add("1");
                multirefererende.getMultikobling().get("Over").add("2");

                store.insert(multirefererende);

                return null;
            }
        });

        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                Multirefererende multirefererende = store.lock(id);

                Assert.assertEquals(multirefererende.getMultikobling().get("Over").size(), 2, "Feil antall 'over'");

                multirefererende.getMultikobling().get("Over").remove("1");

                store.update(multirefererende);

                return null;
            }
        });

        runOnServerService.run(new RunOnServerMethod() {
            @Inject
            private Store store;

            @Override
            public Object run() {
                Multirefererende multirefererende = store.get(id);

                Assert.assertEquals(multirefererende.getMultikobling().get("Over").size(), 1, "Feil antall 'over'");
                Assert.assertEquals(multirefererende.getMultikobling().get("Over").iterator().next(), "2", "Feil verdi");

                return null;
            }
        });
    }

    public void testAddAndReset() {
        Multirefererende multirefererende = new Multirefererende();

        multirefererende.getMultikobling().put("A", "A");
        Assert.assertEquals(multirefererende.getMultikobling().get("A").size(), 1);
        Assert.assertEquals(multirefererende.getMultikobling().getKoblinger().size(), 1);
        Assert.assertEquals(multirefererende.getMultikobling().getKoblinger().iterator().next(), new MultirefererendeKobling("A", "A"));

        multirefererende.getMultikobling().setKoblinger(new HashSet<MultirefererendeKobling>());
        Assert.assertEquals(multirefererende.getMultikobling().getKoblinger().size(), 0);
        Assert.assertEquals(multirefererende.getMultikobling().get("A").size(), 0);
    }

    public void testAddTwoAndClearOne() {
        Multirefererende multirefererende = new Multirefererende();

        multirefererende.getMultikobling().get("A").add("A1");
        multirefererende.getMultikobling().get("B").add("B1");
        Assert.assertEquals(multirefererende.getMultikobling().get("A").size(), 1);
        Assert.assertEquals(multirefererende.getMultikobling().get("B").size(), 1);
        Assert.assertEquals(multirefererende.getMultikobling().getKoblinger().size(), 2);
        Assert.assertEquals(multirefererende.getMultikobling().getKoblinger(), ImmutableSet.of(new MultirefererendeKobling("A", "A1"), new MultirefererendeKobling("B", "B1")));

        multirefererende.getMultikobling().get("A").clear();
        Assert.assertEquals(multirefererende.getMultikobling().get("A").size(), 0);
        Assert.assertEquals(multirefererende.getMultikobling().get("B").size(), 1);
        Assert.assertEquals(multirefererende.getMultikobling().getKoblinger().size(), 1);
        Assert.assertEquals(multirefererende.getMultikobling().getKoblinger(), ImmutableSet.of(new MultirefererendeKobling("B", "B1")));
    }

    public void testAddThreeAndRemoveOne() {
        Multirefererende multirefererende = new Multirefererende();

        multirefererende.getMultikobling().get("A").add("A1");
        multirefererende.getMultikobling().get("A").add("A2");
        multirefererende.getMultikobling().get("B").add("B1");
        Assert.assertEquals(multirefererende.getMultikobling().get("A").size(), 2);
        Assert.assertEquals(multirefererende.getMultikobling().get("B").size(), 1);
        Assert.assertEquals(multirefererende.getMultikobling().getKoblinger().size(), 3);
        Assert.assertEquals(multirefererende.getMultikobling().getKoblinger(), ImmutableSet.of(new MultirefererendeKobling("A", "A1"), new MultirefererendeKobling("A", "A2"), new MultirefererendeKobling("B", "B1")));

        multirefererende.getMultikobling().get("A").remove("A2");
        Assert.assertEquals(multirefererende.getMultikobling().get("A").size(), 1);
        Assert.assertEquals(multirefererende.getMultikobling().get("B").size(), 1);
        Assert.assertEquals(multirefererende.getMultikobling().getKoblinger().size(), 2);
        Assert.assertEquals(multirefererende.getMultikobling().getKoblinger(), ImmutableSet.of(new MultirefererendeKobling("A", "A1"), new MultirefererendeKobling("B", "B1")));
    }

    public void testFlereReferanser() {
        Multirefererende multirefererende = new Multirefererende();

        Set<String> a1 = multirefererende.getMultikobling().get("A");
        Set<String> a2 = multirefererende.getMultikobling().get("A");

        a1.add("A");

        Assert.assertEquals(a1.size(), 1);
        Assert.assertEquals(a2.size(), 1);
    }

    public void testClear() throws Exception {
        Multirefererende multirefererende = new Multirefererende();
        Multikobling<String, String, MultirefererendeKobling> multikobling = multirefererende.getMultikobling();
        Set<String> aObjects = multikobling.get("A");
        aObjects.add("Test");
        aObjects.add("Test2"); // En ekstra pga. av mulighet for ConcurrentModificationException ved uheldig implementasjon av clear()
        aObjects.clear();
        Assert.assertTrue(aObjects.isEmpty());
        Assert.assertTrue(multikobling.getKoblinger().isEmpty());
    }

    public void testRemoveFromIterator() throws Exception {
        Multirefererende multirefererende = new Multirefererende();
        Multikobling<String, String, MultirefererendeKobling> multikobling = multirefererende.getMultikobling();
        Set<String> aObjects = multikobling.get("A");
        aObjects.add("Test");
        final Iterator<String> iterator = aObjects.iterator();
        iterator.next();
        iterator.remove();
        Assert.assertTrue(aObjects.isEmpty());
        Assert.assertTrue(multikobling.getKoblinger().isEmpty());
    }

    public void testRemove() throws Exception {
        Multirefererende multirefererende = new Multirefererende();
        Multikobling<String, String, MultirefererendeKobling> multikobling = multirefererende.getMultikobling();
        Set<String> aObjects = multikobling.get("A");
        aObjects.add("Test");
        aObjects.remove("Test");
        Assert.assertTrue(aObjects.isEmpty());
        Assert.assertTrue(multikobling.getKoblinger().isEmpty());
    }

    public void testRemoveAll() throws Exception {
        Multirefererende multirefererende = new Multirefererende();
        Multikobling<String, String, MultirefererendeKobling> multikobling = multirefererende.getMultikobling();
        Set<String> aObjects = multikobling.get("A");
        Set<String> objs = new HashSet<String>();
        objs.add("Test");
        aObjects.addAll(objs);
        aObjects.removeAll(objs);
        Assert.assertTrue(aObjects.isEmpty());
        Assert.assertTrue(multikobling.getKoblinger().isEmpty());
    }

    public void testRetainAll() throws Exception {
        Multirefererende multirefererende = new Multirefererende();
        Multikobling<String, String, MultirefererendeKobling> multikobling = multirefererende.getMultikobling();
        Set<String> aObjects = multikobling.get("A");
        aObjects.add("Test");
        aObjects.retainAll(Collections.emptySet());
        Assert.assertTrue(aObjects.isEmpty());
        Assert.assertTrue(multikobling.getKoblinger().isEmpty());
    }
}
