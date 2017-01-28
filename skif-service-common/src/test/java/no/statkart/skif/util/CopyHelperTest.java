package no.statkart.skif.util;

import org.testng.annotations.Test;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 */
public class CopyHelperTest {
    @Test
    public void testCopy() throws Exception {
        String a = CopyHelper.copy(new String("a"));
        assertThat(a).isEqualTo("a");

        A a1 = new A("a", true);
        A a2 = CopyHelper.copy((a1));
        assertThat(a1.a).isEqualTo("a");
        assertThat(a2.a).isEqualTo("a");
        assertThat(a1.b).isTrue();
        assertThat(a2.b).isFalse(); // b er transient så den blir ikke med ved copy.
    }

    @Test
    public void testEqualsBySerialization() throws Exception {
        A a1 = new A("a", true);
        A a2 = CopyHelper.copy((a1));
        assertThat(a1.b).isNotEqualTo(a2.b); // b er transient så den blir ikke med ved copy
        assertThat(CopyHelper.equalsBySerialization(a1,a2)).isTrue();
    }
}
