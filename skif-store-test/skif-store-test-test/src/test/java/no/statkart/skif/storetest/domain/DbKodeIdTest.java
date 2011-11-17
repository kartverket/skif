package no.statkart.skif.storetest.domain;

import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.storetest.domain.demo.koder.ADbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.C1DbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.CDbKodeId;
import org.python.parser.ast.Assert;
import org.testng.annotations.Test;

import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * @author Henrik Fredholm
 */
@Test
public class DbKodeIdTest {
    static class TestDbKodeId extends ADbKodeId {
        public TestDbKodeId(Long value, SnapshotVersion snapshotVersion) {
            super(value, snapshotVersion);
        }
    }

    @Test(invocationCount = 20)
    public void testCreateDbKodeId() {
        TestDbKodeId testDbKodeId = null;
        for (int i = 0; i < 100000; i++) {
             testDbKodeId = new TestDbKodeId(new Long(i), SnapshotVersion.CURRENT);
        }
        if (testDbKodeId.getValue()==0) {
            fail("") ;
        }
    }

    @Test(invocationCount = 20)
    public void testCreateDbKodeId2() {
        TestDbKodeId testDbKodeId = null;
        for (int i = 0; i < 100000; i++) {
             testDbKodeId = BubbleIds.createInstance(TestDbKodeId.class, new Long(i), SnapshotVersion.CURRENT);
        }
        if (testDbKodeId.getValue()==0) {
            fail("") ;
        }
    }

    public void testMangeKoderISammeTabellViaSubklassing() {
        CDbKodeId id1 = BubbleIds.createInstance(CDbKodeId.class, new Long(27), SnapshotVersion.CURRENT);
        CDbKodeId id2 = BubbleIds.createInstance(CDbKodeId.class, new Long(27), SnapshotVersion.CURRENT);
        C1DbKodeId c1DbKodeId1 =  new C1DbKodeId(new Long(27), SnapshotVersion.CURRENT);
        C1DbKodeId c2DbKodeId1 =  new C1DbKodeId(new Long(27), SnapshotVersion.CURRENT);

        assertEquals(id1, id2);
        assertEquals(id2, id1);

        assertEquals(id1, c1DbKodeId1);
        assertEquals(c1DbKodeId1, id1);

        assertEquals(id1, c2DbKodeId1);
        assertEquals(c2DbKodeId1, id1);

        assertEquals(c1DbKodeId1, c2DbKodeId1);
        assertEquals(c2DbKodeId1, c1DbKodeId1);
    }

}
