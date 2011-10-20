package no.statkart.skif.storetest.domain;


import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.storetest.domain.demo.koder.TestC1DbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.TestC2DbKodeId;
import no.statkart.skif.storetest.domain.kode.TestDbSubclassedKodeIdImpl;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
@Test
public class DbSubclassedKodeIdTest {
    public void test() {
        TestDbSubclassedKodeIdImpl id1 = BubbleIds.createInstance(TestDbSubclassedKodeIdImpl.class, 27);
        TestDbSubclassedKodeIdImpl id2 = BubbleIds.createInstance(TestDbSubclassedKodeIdImpl.class, 27);
        TestC1DbKodeId c1DbKodeId1 = TestC1DbKodeId.createInstance(27);
        TestC1DbKodeId c1DbKodeId2 = TestC1DbKodeId.createInstance(27);
        TestC2DbKodeId c2DbKodeId1 = TestC2DbKodeId.createInstance(27);
        TestC2DbKodeId c2DbKodeId2 = TestC2DbKodeId.createInstance(27);

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
