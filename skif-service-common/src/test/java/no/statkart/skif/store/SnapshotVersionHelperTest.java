package no.statkart.skif.store;

import org.testng.annotations.Test;

import java.sql.Timestamp;

import static no.statkart.skif.store.SnapshotVersionHelper.subtract;
import static org.testng.Assert.assertEquals;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test
public class SnapshotVersionHelperTest {
    public void testSubtract1() {
        Timestamp t = new Timestamp(10002000);
        Timestamp t2 = subtract(t, 1);
        assertEquals(t2.getTime(), 10001999);
        assertEquals(t2.getNanos(),999999999);
    }

    public void testSubtract2() {
        Timestamp t = new Timestamp(10002000);
        Timestamp t2 = subtract(t, 5);
        assertEquals(t2.getTime(), 10001999);
        assertEquals(t2.getNanos(),999999995);
    }

    public void testSubtract3() {
        Timestamp t = new Timestamp(10002004);
        Timestamp t2 = subtract(t, 5);
        assertEquals(t2.getTime(), 10002003);
        assertEquals(t2.getNanos(),3999995);
    }

    public void testSubtract4() {
        Timestamp t = new Timestamp(10002114);
        Timestamp t2 = subtract(t, 5);
        assertEquals(t2.getTime(), 10002113);
        assertEquals(t2.getNanos(),113999995);

        Timestamp t3 = subtract(t2, 13999994);
        assertEquals(t3.getTime(), 10002100);
        assertEquals(t3.getNanos(),100000001);

        Timestamp t4 = subtract(t2, 6);
        assertEquals(t4.getTime(), 10002113);
        assertEquals(t4.getNanos(),113999989);
    }

}
