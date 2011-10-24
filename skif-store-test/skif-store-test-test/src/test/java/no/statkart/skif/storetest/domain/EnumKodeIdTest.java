package no.statkart.skif.storetest.domain;


import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.EnumKodeIdImpl;
import no.statkart.skif.store.kodelistesupport.KodeIdImpl;
import no.statkart.skif.storetest.domain.demo.koder.TestAEnumKode;
import no.statkart.skif.storetest.domain.demo.koder.TestAEnumKodeId;
import no.statkart.skif.util.CopyHelper;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNotSame;
import static org.testng.AssertJUnit.assertSame;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class EnumKodeIdTest {

    public void testIkkeLike() {
        assertNotSame(TestAEnumKodeId.IkkeOppgittId, TestAEnumKodeId.KodeAId);
        assertNotSame(TestAEnumKodeId.IkkeOppgittId, TestAEnumKodeId.KodeBId);
    }
    
    public void testLike()  {
        TestAEnumKodeId kodeAId = TestAEnumKodeId.KodeAId;

        TestAEnumKodeId id = TestAEnumKodeId.createInstance(kodeAId.getValue());
        assertSame(kodeAId, id);

        TestAEnumKodeId id1 = EnumKodeIdImpl.createInstance(TestAEnumKodeId.class, kodeAId.getValue());
        assertSame(kodeAId, id1);

        TestAEnumKodeId id2 = KodeIdImpl.createInstance(TestAEnumKodeId.class, kodeAId.getValue());
        assertSame(kodeAId, id2);

        TestAEnumKodeId id3 = BubbleIds.createInstance(TestAEnumKodeId.class, kodeAId.getValue());
        assertSame(kodeAId, id3);

    }

    public void testCopy() {
        TestAEnumKodeId id = CopyHelper.copy(TestAEnumKodeId.KodeAId);
        assertSame(id, TestAEnumKodeId.KodeAId);
    }

    public void testGetKodelisteId() {
        TestAEnumKodeId kodeAId =TestAEnumKodeId.KodeAId;
        assertSame(kodeAId.getKodelisteId(), TestAEnumKodeId.KODELISTE_ID);
        assertSame(KodeIdImpl.getKodelisteId(TestAEnumKodeId.class), TestAEnumKodeId.KODELISTE_ID);
    }

    public void testHistorikk() {
        AbstractBubbleId<TestAEnumKode> enumId = TestAEnumKodeId.createInstance(27);
        AbstractBubbleId<TestAEnumKode> enumId2 = enumId.asReplicaVersion(SnapshotVersion.createInstance("2011-10-02 08:03:15.00"));
        assertEquals(enumId2.getSnapshotVersion(), SnapshotVersion.CURRENT);
    }
}
