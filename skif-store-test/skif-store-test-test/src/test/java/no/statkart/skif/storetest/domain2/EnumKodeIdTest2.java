package no.statkart.skif.storetest.domain2;


import no.statkart.skif.store2.BubbleIds2;
import no.statkart.skif.store2.kodelistesupport2.EnumKodeIdImpl2;
import no.statkart.skif.store2.kodelistesupport2.KodeIdImpl2;
import no.statkart.skif.storetest.domain.kodeliste.KodeId;
import no.statkart.skif.storetest.domain.kodeliste.TestAEnumKodeId;
import no.statkart.skif.util.CopyHelper;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertNotSame;
import static org.testng.AssertJUnit.assertSame;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class EnumKodeIdTest2 {

    public void testIkkeLike() {
        assertNotSame(TestAEnumKodeId2.IkkeOppgittId, TestAEnumKodeId2.KodeAId);
        assertNotSame(TestAEnumKodeId2.IkkeOppgittId, TestAEnumKodeId2.KodeBId);
    }
    
    public void testLike()  {
        TestAEnumKodeId2 kodeAId = TestAEnumKodeId2.KodeAId;

        TestAEnumKodeId2 id = TestAEnumKodeId2.createInstance(kodeAId.getValue());
        assertSame(kodeAId, id);

        TestAEnumKodeId2 id1 = EnumKodeIdImpl2.createInstance(TestAEnumKodeId2.class, kodeAId.getValue());
        assertSame(kodeAId, id1);

        TestAEnumKodeId2 id2 = KodeIdImpl2.createInstance(TestAEnumKodeId2.class, kodeAId.getValue());
        assertSame(kodeAId, id2);

        TestAEnumKodeId2 id3 = BubbleIds2.createInstance(TestAEnumKodeId2.class, kodeAId.getValue());
        assertSame(kodeAId, id3);

    }

    public void testCopy() {
        TestAEnumKodeId id = CopyHelper.copy(TestAEnumKodeId.KodeAId);
        assertSame(id, TestAEnumKodeId.KodeAId);
    }

    public void testGetKodelisteId() {
        TestAEnumKodeId kodeAId = TestAEnumKodeId.KodeAId;
        assertSame(kodeAId.getKodelisteId(), TestAEnumKodeId.KODELISTE_ID);
        assertSame(KodeId.getKodelisteId(TestAEnumKodeId.class), TestAEnumKodeId.KODELISTE_ID);
    }
}
