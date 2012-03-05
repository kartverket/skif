package no.statkart.skif.storetest.domain.multikobling;

import com.google.inject.Inject;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.UnitOfWorkTransfer;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.demo.AbstractStoreTestBubbleId;
import no.statkart.skif.storetest.domain.multikobling.kobling.RettsstiftelsePersonRolle;
import no.statkart.skif.storetest.domain.multikobling.kobling.RetttstiftelseTilPersonKobling;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import static org.fest.assertions.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test
public class MultikoblingTest extends StoreTestTestCase {
    @Inject
    Store store;

    public void test() {
        List<PersonId<?>> personIds = new ArrayList<PersonId<?>>();
        List<ServituttId<?>> servituttIds = new ArrayList<ServituttId<?>>();
        List<PengeheftelseId<?>> pengeheftelseIds = new ArrayList<PengeheftelseId<?>>();
        store.beginUnitOfWork();

        for (int i = 0; i < 10; i++) {
            final Person person = (Person) createBubble(PersonId.class);
            store.insert(person);
            final PersonId<?> personId = (PersonId<?>) person.getId();
            personIds.add(personId);

        }

        for (int i = 0; i < 5; i++) {
            final Servitutt servitutt = (Servitutt) createBubble(ServituttId.class);
            store.insert(servitutt);
            final ServituttId<?> servituttId = (ServituttId<?>) servitutt.getId();
            servituttIds.add(servituttId);

            servitutt.getRettighetshaverAktivIds().add(personIds.get(i));
            servitutt.getRettighetshaverHistoriskIds().add(personIds.get(i + 5));
        }

        for (int i = 0; i < 5; i++) {
            final Pengeheftelse pengeheftelse = (Pengeheftelse) createBubble(PengeheftelseId.class);
            store.insert(pengeheftelse);
            final PengeheftelseId<?> pengeheftelseId = (PengeheftelseId<?>) pengeheftelse.getId();
            pengeheftelseIds.add(pengeheftelseId);

            pengeheftelse.getPanthaverAktivIds().add(personIds.get(i));
            pengeheftelse.getPanthaverAktivIds().add(personIds.get(i));
            pengeheftelse.getPanthavereHistoriskIds().add(personIds.get(i + 5));
        }
        System.out.println(personIds);
        System.out.println(servituttIds);
        System.out.println(pengeheftelseIds);
        final Pengeheftelse pengeheftelse = store.get(pengeheftelseIds.get(2));
        final UnitOfWorkTransfer unitOfWorkTransfer = store.getUnitOfWorkTransfer();
        store.endUnitOfWork();

    }

    /**
     * Oppretter BubbleObject av gitt type og tildeler "unik" id
     */
    static long nexId = 0;

    static <I extends AbstractStoreTestBubbleId<T>, T extends AbstractStoreTestBubble> T createBubble(Class<I> idClass) {
        final I id = BubbleIds.createInstance(idClass, new Long(++nexId), SnapshotVersion.CURRENT);
        final T bubble = id.createTypeInstance();
        bubble.setId(id);
        return bubble;
    }


    /**
     * Tester lasting av objekter fra hibernate og 2-veis synkronisering mellom KoblingMultimap og underliggende HashSet
     */
    public void loadObjects() {
        final Person person = store.get(PersonId.create(1001));
        assertEquals(person.getId().getValue(), new Long(1001));

        final Rettsstiftelse servitutt = store.get(RettsstiftelseId.create(2001));
        assertEquals(servitutt.getId().getValue(), new Long(2001));
        assertEquals(servitutt.getId().getType(), Servitutt.class);
        assertThat(Servitutt.class.cast(servitutt).getRettighetshaverAktivIds()).containsOnly(PersonId.create(1001));
        servitutt.rettsstiftelsePersonIdsKoblinger.put(RettsstiftelsePersonRolle.RETTIGHETSHAVER_AKTIV, PersonId.create(10));
        assertThat(Servitutt.class.cast(servitutt).getRettighetshaverAktivIds()).containsOnly(PersonId.create(1001), PersonId.create(10));
        servitutt.setPersonKoblinger(new HashSet<RetttstiftelseTilPersonKobling>());
        assertThat(Servitutt.class.cast(servitutt).getRettighetshaverAktivIds()).isEmpty();

        final Rettsstiftelse pengeheftelse = store.get(RettsstiftelseId.create(2101));
        assertEquals(pengeheftelse.getId().getValue(), new Long(2101));
        assertEquals(pengeheftelse.getId().getType(), Pengeheftelse.class);
        servitutt.rettsstiftelsePersonIdsKoblinger.clear();
        servitutt.rettsstiftelsePersonIdsKoblinger.put(RettsstiftelsePersonRolle.RETTIGHETSHAVER_AKTIV, PersonId.create(10));


    }
}
