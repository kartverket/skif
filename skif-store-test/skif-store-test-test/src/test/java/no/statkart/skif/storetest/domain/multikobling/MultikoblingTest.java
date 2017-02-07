package no.statkart.skif.storetest.domain.multikobling;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.SetMultimap;
import com.google.inject.Inject;
import com.thoughtworks.xstream.XStream;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.service.RunOnServerWithTxRequiresNewService;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.multikobling.DefaultKoblingFactory;
import no.statkart.skif.store.multikobling.Multikobling;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import no.statkart.skif.util.CopyHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Objects;
import java.util.Set;

import static org.fest.assertions.api.Assertions.assertThat;

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

    /**
     * Tester at refreshNeeded settes av {@code setKoblinger} og at {@code get()} refresher automatisk.
     */
    @SuppressWarnings("unchecked")
    public void testRefresh() throws NoSuchFieldException, IllegalAccessException  {
        Field delegateField = Multikobling.class.getDeclaredFields()[2];
        assertThat(delegateField.getName()).isEqualTo("delegate");
        delegateField.setAccessible(true);
        Field refreshNeededField = Multikobling.class.getDeclaredFields()[4];
        assertThat(refreshNeededField.getName()).isEqualTo("refreshNeeded");
        refreshNeededField.setAccessible(true);

        Multikobling<String, String, MultirefererendeKobling> multikobling = Multikobling.create(DefaultKoblingFactory.create(MultirefererendeKobling.class));
        multikobling.setKoblinger(new HashSet<>(ImmutableSet.of(new MultirefererendeKobling("Over", "10"))));   // setKoblinger setter refreshNeeded
        SetMultimap<String, String> delegate = (SetMultimap<String, String>) delegateField.get(multikobling);
        assertThat((Boolean) refreshNeededField.get(multikobling)).isTrue();
        assertThat(delegate.isEmpty()).isTrue();
        assertThat(multikobling.get("Over")).containsExactly("10");  // multikobling.get() gjør automatisk refresh
        assertThat((Boolean) refreshNeededField.get(multikobling)).isFalse();
        assertThat(delegate.isEmpty()).isFalse();
    }

    /**
     * Tester at serialisering ikke tar med {@code refreshNeeded} og {@code delegate} og at deserialisert objekt
     * har {@code refreshNeeded} satt til {@code true}. Testen oppretter en multikobling hvor {@code refreshNeeded}
     * er {@code false} og {@code delegate} er i synk med{@code koblinger}. Detetter lages det en kopi via
     * {@code CopyHelper} som bruker serialisering og deserialisering for kopiering. Kopien har
     * {@code refreshNeeded} satt til {@code true} og {@code delegate} er tom, dvs ikke i synk med {@code koblinger}.
     * Ved kall til {@code get} blir {@code  delegate} refreshet automatisk og {@code refreshNeeded} satt til
     * {@code false}.
     */
    @SuppressWarnings("unchecked")
    public void testSerialization() throws NoSuchFieldException, IllegalAccessException {
        Field delegateField = Multikobling.class.getDeclaredFields()[2];
        assertThat(delegateField.getName()).isEqualTo("delegate");
        delegateField.setAccessible(true);
        Field refreshNeededField = Multikobling.class.getDeclaredFields()[4];
        assertThat(refreshNeededField.getName()).isEqualTo("refreshNeeded");
        refreshNeededField.setAccessible(true);

        Multikobling<String, String, MultirefererendeKobling> multikobling1 = Multikobling.create(DefaultKoblingFactory.create(MultirefererendeKobling.class));
        multikobling1.put("Over", "10");
        SetMultimap<String, String> delegate1 = (SetMultimap<String, String>) delegateField.get(multikobling1);
        assertThat((Boolean) refreshNeededField.get(multikobling1)).isFalse();
        assertThat(delegate1.size()).isEqualTo(1);
        assertThat(delegate1.get("Over")).containsExactly("10");

        Multikobling<String, String, MultirefererendeKobling> multikobling2 = CopyHelper.copy(multikobling1);
        SetMultimap<String, String> delegate2 = (SetMultimap<String, String>) delegateField.get(multikobling2);
        assertThat((Boolean) refreshNeededField.get(multikobling2)).isTrue(); // refreshNeeded og delegate blir ikke med ved serialisering
        assertThat(delegate2.isEmpty()).isTrue();
        assertThat(multikobling2.get("Over")).containsExactly("10"); // refreshNeeded og delegate settes automatisk ved kall til get().
        assertThat((Boolean) refreshNeededField.get(multikobling2)).isFalse();
        assertThat(delegate2.isEmpty()).isFalse();
        assertThat(delegate2.get("Over")).containsExactly("10");
    }


    /**
     * Tester at to multikoblinger er like og genererer samme byte serialisering selvom de har forskjellige verdier for
     * {@code refreshNeeded} og {@code delegate}.
     */
    @SuppressWarnings("unchecked")
    public void testEqualsBySerialization() throws NoSuchFieldException, IllegalAccessException {
        Field delegateField = Multikobling.class.getDeclaredFields()[2];
        assertThat(delegateField.getName()).isEqualTo("delegate");
        delegateField.setAccessible(true);
        Field refreshNeededField = Multikobling.class.getDeclaredFields()[4];
        assertThat(refreshNeededField.getName()).isEqualTo("refreshNeeded");
        refreshNeededField.setAccessible(true);

        Multikobling<String, String, MultirefererendeKobling> multikobling1 = Multikobling.create(DefaultKoblingFactory.create(MultirefererendeKobling.class));
        multikobling1.put("Over", "10");
        Multikobling<String, String, MultirefererendeKobling> multikobling2 = CopyHelper.copy(multikobling1);

        // Multikobling1 og multikobling2 inneholder forskjellig state for 'delegate' og 'refreshNeeded', da disse ikke settes ved kopiering
        SetMultimap<String, String> delegate1 = (SetMultimap<String, String>) delegateField.get(multikobling1);
        SetMultimap<String, String> delegate2 = (SetMultimap<String, String>) delegateField.get(multikobling2);
        assertThat((Boolean) refreshNeededField.get(multikobling1)).isFalse();
        assertThat((Boolean) refreshNeededField.get(multikobling2)).isTrue(); // refreshNeeded og delegate blir ikke med ved serialisering
        assertThat(delegate1.isEmpty()).isFalse();
        assertThat(delegate2.isEmpty()).isTrue();
        assertThat(CopyHelper.equalsBySerialization(multikobling1, multikobling2)).isTrue();
        assertThat(multikobling1.equals(multikobling2)).isTrue();
    }

    public void testEqualsBySerializationMangeElementer() throws NoSuchFieldException, IllegalAccessException {
        Field delegateField = Multikobling.class.getDeclaredFields()[2];
        assertThat(delegateField.getName()).isEqualTo("delegate");
        delegateField.setAccessible(true);
        Field refreshNeededField = Multikobling.class.getDeclaredFields()[4];
        assertThat(refreshNeededField.getName()).isEqualTo("refreshNeeded");
        refreshNeededField.setAccessible(true);

        Multikobling<String, String, MultirefererendeKobling> multikobling1 = Multikobling.create(DefaultKoblingFactory.create(MultirefererendeKobling.class));
        for (int i = 0; i<50; i++) {
            multikobling1.put("Over", Integer.toString(i));
        }
        Multikobling<String, String, MultirefererendeKobling> multikobling2 = CopyHelper.copy(multikobling1);

        // Multikobling1 og multikobling2 inneholder forskjellig state for 'delegate' og 'refreshNeeded', da disse ikke settes ved kopiering
        SetMultimap<String, String> delegate1 = (SetMultimap<String, String>) delegateField.get(multikobling1);
        SetMultimap<String, String> delegate2 = (SetMultimap<String, String>) delegateField.get(multikobling2);
        assertThat((Boolean) refreshNeededField.get(multikobling1)).isFalse();
        assertThat((Boolean) refreshNeededField.get(multikobling2)).isTrue(); // refreshNeeded og delegate blir ikke med ved serialisering
        assertThat(delegate1.isEmpty()).isFalse();
        assertThat(delegate2.isEmpty()).isTrue();
        assertThat(CopyHelper.equalsBySerialization(multikobling1, multikobling2)).isTrue();
        assertThat(multikobling1.equals(multikobling2)).isTrue();
    }


    public void testEqualsBySerializationMangeElementerSomIkkeVirker() throws NoSuchFieldException, IllegalAccessException {
        Field delegateField = Multikobling.class.getDeclaredFields()[2];
        assertThat(delegateField.getName()).isEqualTo("delegate");
        delegateField.setAccessible(true);
        Field refreshNeededField = Multikobling.class.getDeclaredFields()[4];
        assertThat(refreshNeededField.getName()).isEqualTo("refreshNeeded");
        refreshNeededField.setAccessible(true);

        Multikobling<String, String, MultirefererendeKobling> multikobling1 = Multikobling.create(DefaultKoblingFactory.create(MultirefererendeKobling.class));
        multikobling1.setKoblinger(new HashSet<MultirefererendeKobling>(1000)); // LoadFaktor blir forskjellig
        for (int i = 0; i<50; i++) {
            multikobling1.put("Over", Integer.toString(i));
        }
        Multikobling<String, String, MultirefererendeKobling> multikobling2 = CopyHelper.copy(multikobling1);

        // Multikobling1 og multikobling2 inneholder forskjellig state for 'delegate' og 'refreshNeeded', da disse ikke settes ved kopiering
        SetMultimap<String, String> delegate1 = (SetMultimap<String, String>) delegateField.get(multikobling1);
        SetMultimap<String, String> delegate2 = (SetMultimap<String, String>) delegateField.get(multikobling2);
        assertThat((Boolean) refreshNeededField.get(multikobling1)).isFalse();
        assertThat((Boolean) refreshNeededField.get(multikobling2)).isTrue(); // refreshNeeded og delegate blir ikke med ved serialisering
        assertThat(delegate1.isEmpty()).isFalse();
        assertThat(delegate2.isEmpty()).isTrue();
        assertThat(CopyHelper.equalsBySerialization(multikobling1, multikobling2)).isFalse();  // Denne virker ikke, elementer i Multikobling har forskjellig rekkefølge pga forskjellig loadfaktor.
        assertThat(multikobling1.equals(multikobling2)).isTrue();

        // XStream serialisering virker heller ikke
        XStream xstream = new XStream();
        String o1xml = xstream.toXML(multikobling1);
        String o2xml = xstream.toXML(multikobling2);
        assertThat(o1xml).isNotEqualTo(o2xml);

    }

    /**
     * Tester at XStream serialisering blir den samme selvom interne felter {@code refreshNeeded} og {@code delegate}
     * er forskjellige. Disse felter er transiente og blir ikke med ved serialisering.
     */
    public void testXStreamSerialisering() {
        Multikobling<String, String, MultirefererendeKobling> multikobling1 = Multikobling.create(DefaultKoblingFactory.create(MultirefererendeKobling.class));
        multikobling1.put("Over", "10");
        Multikobling<String, String, MultirefererendeKobling> multikobling2 = CopyHelper.copy(multikobling1);

        XStream xstream = new XStream();
        String o1xml = xstream.toXML(multikobling1);
        String o2xml = xstream.toXML(multikobling2);
        assertThat(o1xml).isEqualTo(o2xml);
        assertThat(o1xml).doesNotContain("refreshNeeded");
        assertThat(o1xml).doesNotContain("delegate");
    }
}
