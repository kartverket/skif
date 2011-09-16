package no.statkart.skif.storetest.domain;


import no.statkart.skif.storetest.domain.kodeliste.KodeId;
import no.statkart.skif.storetest.domain.kodeliste.TestAEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.impl.EnumKodeId;
import no.statkart.skif.util.CopyHelper;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertNotSame;
import static org.testng.AssertJUnit.assertSame;

/**
 * @author Henrik Fredholm
 * @since 0.6
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

        TestAEnumKodeId id1 = EnumKodeId.createInstance(TestAEnumKodeId.class, kodeAId.getValue());
        assertSame(kodeAId, id1);

        TestAEnumKodeId id2 = KodeId.createInstance(TestAEnumKodeId.class, kodeAId.getValue());
        assertSame(kodeAId, id2);

        TestAEnumKodeId id3 = TestBubbleId.createInstance(TestAEnumKodeId.class, kodeAId.getValue());
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
