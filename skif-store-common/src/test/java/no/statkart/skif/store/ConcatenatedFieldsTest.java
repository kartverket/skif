package no.statkart.skif.store;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 */
@Test
public class ConcatenatedFieldsTest {

    public void testConstructor() {
        ConcatenatedFields concatenatedFields = new ConcatenatedFields("[abc,null,1]");
        assertEquals(concatenatedFields.getValue(),"[abc,null,1]");
    }

    public void testGetFields() {
        ConcatenatedFields concatenatedFields = new ConcatenatedFields("[abc,null,1]");
        assertEquals(concatenatedFields.getFields().length,3);
        assertEquals(concatenatedFields.getFields()[0], "abc");
        assertEquals(concatenatedFields.getFields()[1], null);
        assertEquals(concatenatedFields.getFields()[2], "1");
    }

    public void testCreate() {
        ConcatenatedFields concatenatedFields = ConcatenatedFields.create("abc", null, 1L);
        assertEquals(concatenatedFields.getFields().length,3);
        assertEquals(concatenatedFields.getFields()[0], "abc");
        assertEquals(concatenatedFields.getFields()[1], null);
        assertEquals(concatenatedFields.getFields()[2], "1");
    }
}
