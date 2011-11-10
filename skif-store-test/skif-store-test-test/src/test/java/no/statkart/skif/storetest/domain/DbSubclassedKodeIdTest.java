package no.statkart.skif.storetest.domain;


import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.storetest.domain.demo.koder.C1DbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.C2DbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.CDbKodeId;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

/**
 * @author Henrik Fredholm
 * @since 0.6
 */
@Test
public class DbSubclassedKodeIdTest {
    public void test() {
        CDbKodeId id1 = BubbleIds.createInstance(CDbKodeId.class, 27);
        CDbKodeId id2 = BubbleIds.createInstance(CDbKodeId.class, 27);
        C1DbKodeId c1DbKodeId1 = C1DbKodeId.createInstance(27);
        C1DbKodeId c1DbKodeId2 = C1DbKodeId.createInstance(27);
        C2DbKodeId c2DbKodeId1 = C2DbKodeId.createInstance(27);
        C2DbKodeId c2DbKodeId2 = C2DbKodeId.createInstance(27);

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
