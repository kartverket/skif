package no.statkart.skif.storetest.domain2;


import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store2.BubbleIds2;
import no.statkart.skif.store2.kodelistesupport2.DbSubclassedKodeId2;
import no.statkart.skif.storetest.domain.kodeliste.TestC1DbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.TestC2DbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbSubclassedKodeId;
import no.statkart.skif.storetest.domain2.kodeliste.TestC1DbKodeId2;
import no.statkart.skif.storetest.domain2.kodeliste.TestC2DbKodeId2;
import no.statkart.skif.storetest.domain2.kodeliste.TestDbSubclassedKodeIdImpl2;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
@Test
public class DbSubclassedKodeIdTest2 {
    public void test() {
        TestDbSubclassedKodeIdImpl2 id1 = BubbleIds2.createInstance(TestDbSubclassedKodeIdImpl2.class, 27);
        TestDbSubclassedKodeIdImpl2 id2 = BubbleIds2.createInstance(TestDbSubclassedKodeIdImpl2.class, 27);
        TestC1DbKodeId2 c1DbKodeId1 = TestC1DbKodeId2.createInstance(27);
        TestC1DbKodeId2 c1DbKodeId2 = TestC1DbKodeId2.createInstance(27);
        TestC2DbKodeId2 c2DbKodeId1 = TestC2DbKodeId2.createInstance(27);
        TestC2DbKodeId2 c2DbKodeId2 = TestC2DbKodeId2.createInstance(27);

        assertNotSame(id1, id2);
        assertEquals(id1, id2);
        assertEquals(id2, id1);

        assertSame(c1DbKodeId1, c1DbKodeId2);
        assertEquals(id1, c1DbKodeId1);
        assertEquals(c1DbKodeId1, id1);

        assertSame(c2DbKodeId1, c2DbKodeId2);
        assertEquals(id1, c2DbKodeId1);
        assertEquals(c2DbKodeId1, id1);

        assertEquals(c1DbKodeId1, c2DbKodeId1);
        assertEquals(c2DbKodeId1, c1DbKodeId1);


    }
}
