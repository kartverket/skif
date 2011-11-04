package no.statkart.skif.storetest.domain;


import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.EnumKodeId;
import no.statkart.skif.store.kodeliste.KodeId;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKode;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
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
        assertNotSame(AEnumKodeId.IkkeOppgittId, AEnumKodeId.KodeAId);
        assertNotSame(AEnumKodeId.IkkeOppgittId, AEnumKodeId.KodeBId);
    }
    
    public void testLike()  {
        AEnumKodeId kodeAId = AEnumKodeId.KodeAId;

        AEnumKodeId id = AEnumKodeId.createInstance(kodeAId.getValue());
        assertSame(kodeAId, id);

        AEnumKodeId id1 = EnumKodeId.createInstance(AEnumKodeId.class, kodeAId.getValue());
        assertSame(kodeAId, id1);

        AEnumKodeId id2 = KodeId.createInstance(AEnumKodeId.class, kodeAId.getValue());
        assertSame(kodeAId, id2);

        AEnumKodeId id3 = BubbleIds.createInstance(AEnumKodeId.class, kodeAId.getValue());
        assertSame(kodeAId, id3);

    }

    public void testCopy() {
        AEnumKodeId id = CopyHelper.copy(AEnumKodeId.KodeAId);
        assertSame(id, AEnumKodeId.KodeAId);
    }

    public void testGetKodelisteId() {
        AEnumKodeId kodeAId = AEnumKodeId.KodeAId;
        assertSame(kodeAId.getKodelisteId(), AEnumKodeId.KODELISTE_ID);
        assertSame(KodeId.getKodelisteId(AEnumKodeId.class), AEnumKodeId.KODELISTE_ID);
    }

    public void testHistorikk() {
        AbstractBubbleId<AEnumKode> enumId = AEnumKodeId.createInstance(27);
        AbstractBubbleId<AEnumKode> enumId2 = enumId.asReplicaVersion(SnapshotVersion.createInstance("2011-10-02 08:03:15.00"));
        assertEquals(enumId2.getSnapshotVersion(), SnapshotVersion.CURRENT);
    }
}
