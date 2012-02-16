package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.demo.koder.*;
import org.testng.annotations.Test;

import static org.testng.Assert.*;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test
public class KodeIdTest {

    public void testCreateId() {
        AEnumKodeId a = AEnumKodeId.KodeAId;
        KodelisteId<?> kodelisteId = AEnumKodeId.KODELISTE_ID;
        assertEquals(a.getValue(), new Long(1));
        assertEquals(kodelisteId.getValue(), new Long(1));

        // Id'er er ikke unike instanser
        AEnumKodeId aCopy = new AEnumKodeId(1L, SnapshotVersion.CURRENT);
        assertEquals(a, aCopy);
        assertFalse(a == aCopy);
    }

    public void testEqualsButNotSame() {
        // Id'er er ikke unike instanser
        AEnumKodeId aCopy = new AEnumKodeId(1L, SnapshotVersion.CURRENT);
        assertEquals(AEnumKodeId.KodeAId, aCopy);
        assertFalse(AEnumKodeId.KodeAId == aCopy);
    }


    /**
     * Tester sammenlikning av koder på tvers av versjoner
     */
    public void testEqualsWithDifferentSnapshotVersions() {
        AEnumKodeId aOld = new AEnumKodeId(1L, SnapshotVersion.OLD);
        BEnumKodeId bOld = new BEnumKodeId(1L, SnapshotVersion.OLD);
        assertTrue(AEnumKodeId.KodeAId.equalsIgnoreSnapshotVersion(aOld));
        assertEquals(AEnumKodeId.KodeAId.asSnapshotVersion(aOld), aOld);

        // Skulle gjerne ha hatt kompiliering feil her, men får det ikke med dagens design
        assertFalse(AEnumKodeId.KodeAId.equalsIgnoreSnapshotVersion(bOld));
    }


    /**
     * Tester opprettelse via new
     */
    @Test(invocationCount = 20)
    public void testCreateDbKodeId() {
        AEnumKodeId testId = null;
        for (int i = 0; i < 100000; i++) {
             testId = new AEnumKodeId(new Long(i), SnapshotVersion.CURRENT);
        }
        if (testId.getValue()==0) {
            fail("") ;
        }
    }

    /**
     * Tester opprettelse via createInstance
     */
    @Test(invocationCount = 20)
    public void testCreateDbKodeId2() {
        AEnumKodeId testId = null;
        for (int i = 0; i < 100000; i++) {
             testId = BubbleIds.createInstance(AEnumKodeId.class, new Long(i), SnapshotVersion.CURRENT);
        }
        if (testId.getValue()==0) {
            fail("") ;
        }
    }

    /**
     *  Tester at CDbKodeId(27) eq C1DbKodeId(27) eq C2DbKodeId(27)
     */
    public void testMangeKoderISammeTabellViaSubklassing() {
        CDbKodeId id1 = BubbleIds.createInstance(CDbKodeId.class, new Long(27), SnapshotVersion.CURRENT);
        CDbKodeId id2 = BubbleIds.createInstance(CDbKodeId.class, new Long(27), SnapshotVersion.CURRENT);
        C1DbKodeId c1DbKodeId1 =  new C1DbKodeId(new Long(27), SnapshotVersion.CURRENT);
        C2DbKodeId c2DbKodeId1 =  new C2DbKodeId(new Long(27), SnapshotVersion.CURRENT);

        assertEquals(id1, id2);
        assertEquals(id2, id1);

        assertEquals(id1, c1DbKodeId1);
        assertEquals(c1DbKodeId1, id1);

        assertEquals(id1, c2DbKodeId1);
        assertEquals(c2DbKodeId1, id1);

        assertEquals(c1DbKodeId1, c2DbKodeId1);
        assertEquals(c2DbKodeId1, c1DbKodeId1);

        CDbKodeId id28 = BubbleIds.createInstance(CDbKodeId.class, new Long(28), SnapshotVersion.CURRENT);
        assertFalse(id1.equals(id28));
    }


}
