package no.statkart.skif.domain;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import org.testng.annotations.Test;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

public class EqualsByFieldsTest {
    @Test
    public void testSimple() {
        String s1 = "foo";
        String s2 = "foo";
        String s3 = "bar";

        EqualsByFields equalsByFields = new EqualsByFields();

        assertTrue(equalsByFields.isEqualByFields(s1, s1));
        assertTrue(equalsByFields.isEqualByFields(s1, s2));
        assertTrue(equalsByFields.isEqualByFields(s2, s1));
        assertFalse(equalsByFields.isEqualByFields(s1, s3));
        assertFalse(equalsByFields.isEqualByFields(s3, s1));
        assertFalse(equalsByFields.isEqualByFields(null, s1));
        assertFalse(equalsByFields.isEqualByFields(s1, null));
    }

    @Test
    public void testC1() {
        C1 c1 = new C1(1, "Foo");
        C1 c2 = new C1(1, "Foo");
        C1 c3 = new C1(1, "Bar");
        C1 c4 = new C1(2, "Bar");

        EqualsByFields equalsByFields = new EqualsByFields();

        assertTrue(equalsByFields.isEqualByFields(c1, c1));
        assertTrue(Objects.equals(c1, c1));

        assertTrue(equalsByFields.isEqualByFields(c1, c2));
        assertTrue(Objects.equals(c1, c2));
        assertTrue(equalsByFields.isEqualByFields(c2, c1));
        assertTrue(Objects.equals(c2, c1));

        assertFalse(equalsByFields.isEqualByFields(c1, c3));
        assertTrue(Objects.equals(c1, c3));
        assertFalse(equalsByFields.isEqualByFields(c3, c1));
        assertTrue(Objects.equals(c3, c1));

        assertFalse(equalsByFields.isEqualByFields(c4, c3));
        assertFalse(Objects.equals(c4, c3));
        assertFalse(equalsByFields.isEqualByFields(c3, c4));
        assertFalse(Objects.equals(c3, c4));
    }

    @Test
    public void testC2() {
        C2 c1 = new C2(1, Collections.emptyList());
        C2 c2 = new C2(1, new ArrayList<>());
        C2 c3 = new C2(1, ImmutableList.of(new C4(10, "Foo")));
        C2 c4 = new C2(1, ImmutableList.of(new C4(10, "Foo")));
        C2 c5 = new C2(1, ImmutableList.of(new C4(10, "Bar")));
        C2 c6 = new C2(2, ImmutableList.of(new C4(20, "Foo"), new C4(21, "Bar")));
        C2 c7 = new C2(2, ImmutableList.of(new C4(21, "Bar"), new C4(20, "Foo")));
        C2 c8 = new C2(2, ImmutableList.of(new C4(20, "Bar"), new C4(21, "Foo")));

        EqualsByFields equalsByFields = new EqualsByFields();
        equalsByFields.addHandler(C4.class, new C4_Handler());

        assertTrue(equalsByFields.isEqualByFields(c1, c1));
        assertTrue(Objects.equals(c1.children, c1.children));

        assertTrue(equalsByFields.isEqualByFields(c2, c2));
        assertTrue(Objects.equals(c2.children, c2.children));

        assertTrue(equalsByFields.isEqualByFields(c3, c3));
        assertTrue(Objects.equals(c3.children, c3.children));

        assertFalse(equalsByFields.isEqualByFields(c1, c3));
        assertFalse(equalsByFields.isEqualByFields(c3, c1));

        assertTrue(equalsByFields.isEqualByFields(c3, c4));
        assertTrue(Objects.equals(c3.children, c4.children));
        assertTrue(equalsByFields.isEqualByFields(c4, c3));
        assertTrue(Objects.equals(c4.children, c3.children));

        assertFalse(equalsByFields.isEqualByFields(c4, c5));
        assertTrue(Objects.equals(c4.children, c5.children));
        assertFalse(equalsByFields.isEqualByFields(c5, c4));
        assertTrue(Objects.equals(c5.children, c4.children));

        assertTrue(equalsByFields.isEqualByFields(c6, c6));
        assertTrue(Objects.equals(c6.children, c6.children));

        assertFalse(equalsByFields.isEqualByFields(c6, c7));
        assertFalse(Objects.equals(c6.children, c7.children));
        assertFalse(equalsByFields.isEqualByFields(c7, c6));
        assertFalse(Objects.equals(c7.children, c6.children));

        assertFalse(equalsByFields.isEqualByFields(c6, c8));
        assertTrue(Objects.equals(c6.children, c8.children));
        assertFalse(equalsByFields.isEqualByFields(c8, c6));
        assertTrue(Objects.equals(c8.children, c6.children));
    }

    @Test
    public void testC3() {
        C3 c1 = new C3(1, Collections.emptySet());
        C3 c2 = new C3(1, new HashSet<>());
        C3 c3 = new C3(1, ImmutableSet.of(new C4(10, "Foo")));
        C3 c4 = new C3(1, ImmutableSet.of(new C4(10, "Foo")));
        C3 c5 = new C3(1, ImmutableSet.of(new C4(10, "Bar")));
        C3 c6 = new C3(2, ImmutableSet.of(new C4(20, "Foo"), new C4(21, "Bar")));
        C3 c7 = new C3(2, ImmutableSet.of(new C4(21, "Bar"), new C4(20, "Foo")));
        C3 c8 = new C3(2, ImmutableSet.of(new C4(20, "Bar"), new C4(21, "Foo")));

        EqualsByFields equalsByFields = new EqualsByFields();
        equalsByFields.addHandler(C4.class, new C4_Handler());

        assertTrue(equalsByFields.isEqualByFields(c1, c1));
        assertTrue(Objects.equals(c1.children, c1.children));

        assertTrue(equalsByFields.isEqualByFields(c2, c2));
        assertTrue(Objects.equals(c2.children, c2.children));

        assertTrue(equalsByFields.isEqualByFields(c3, c3));
        assertTrue(Objects.equals(c3.children, c3.children));

        assertFalse(equalsByFields.isEqualByFields(c1, c3));
        assertFalse(equalsByFields.isEqualByFields(c3, c1));

        assertTrue(equalsByFields.isEqualByFields(c3, c4));
        assertTrue(Objects.equals(c3.children, c4.children));
        assertTrue(equalsByFields.isEqualByFields(c4, c3));
        assertTrue(Objects.equals(c4.children, c3.children));

        assertFalse(equalsByFields.isEqualByFields(c4, c5));
        assertTrue(Objects.equals(c4.children, c5.children));
        assertFalse(equalsByFields.isEqualByFields(c5, c4));
        assertTrue(Objects.equals(c5.children, c4.children));

        assertTrue(equalsByFields.isEqualByFields(c6, c6));
        assertTrue(Objects.equals(c6.children, c6.children));

        assertTrue(equalsByFields.isEqualByFields(c6, c7));
        assertTrue(Objects.equals(c6.children, c7.children));
        assertTrue(equalsByFields.isEqualByFields(c7, c6));
        assertTrue(Objects.equals(c7.children, c6.children));

        assertFalse(equalsByFields.isEqualByFields(c6, c8));
        assertTrue(Objects.equals(c6.children, c8.children));
        assertFalse(equalsByFields.isEqualByFields(c8, c6));
        assertTrue(Objects.equals(c8.children, c6.children));
    }

    @Test
    public void testMapOfC1toC4() {
        EqualsByFields equalsByFields = new EqualsByFields();
        equalsByFields.addHandler(C4.class, new C4_Handler());

        Map<C1, C4> m1 = ImmutableMap.of(new C1(1, "Foo"), new C4(11, "Bar"));
        Map<C1, C4> m2 = ImmutableMap.of(new C1(1, "Foz"), new C4(11, "Baz"));
        Map<C1, C4> m3 = ImmutableMap.of(new C1(2, "Foo"), new C4(12, "Bar"));
        Map<C1, C4> m4 = ImmutableMap.of(
                new C1(1, "Foo"), new C4(11, "Bar"),
                new C1(2, "Foz"), new C4(12, "Baz")
        );
        Map<C1, C4> m5 = ImmutableMap.of(
                new C1(2, "Foz"), new C4(12, "Baz"),
                new C1(1, "Foo"), new C4(11, "Bar")
        );

        assertTrue(equalsByFields.isEqualByFields(m1, m1));
        assertTrue(Objects.equals(m1, m1));

        assertFalse(equalsByFields.isEqualByFields(m1, m2));
        assertTrue(Objects.equals(m1, m2));
        assertFalse(equalsByFields.isEqualByFields(m2, m1));
        assertTrue(Objects.equals(m2, m1));

        assertFalse(equalsByFields.isEqualByFields(m1, m3));
        assertFalse(Objects.equals(m1, m3));
        assertFalse(equalsByFields.isEqualByFields(m3, m1));
        assertFalse(Objects.equals(m3, m1));

        assertTrue(equalsByFields.isEqualByFields(m4, m4));
        assertTrue(Objects.equals(m4, m4));

        assertTrue(equalsByFields.isEqualByFields(m4, m5));
        assertTrue(Objects.equals(m4, m5));
        assertTrue(equalsByFields.isEqualByFields(m5, m4));
        assertTrue(Objects.equals(m5, m4));

        assertFalse(equalsByFields.isEqualByFields(m1, m5));
        assertFalse(Objects.equals(m1, m5));
        assertFalse(equalsByFields.isEqualByFields(m5, m1));
        assertFalse(Objects.equals(m5, m1));
    }

    @Test
    public void testMultimapOfC1toC4() {
        EqualsByFields equalsByFields = new EqualsByFields();
        equalsByFields.addHandler(C4.class, new C4_Handler());

        Multimap<C1, C4> m1 = ImmutableMultimap.of(new C1(1, "Foo"), new C4(11, "Bar"));
        Multimap<C1, C4> m2 = ImmutableMultimap.of(new C1(1, "Foz"), new C4(11, "Baz"));
        Multimap<C1, C4> m3 = ImmutableMultimap.of(new C1(2, "Foo"), new C4(12, "Bar"));
        Multimap<C1, C4> m4 = ImmutableMultimap.of(
                new C1(1, "Foo"), new C4(11, "Bar"),
                new C1(2, "Foz"), new C4(12, "Baz")
        );
        Map<C1, C4> m5 = ImmutableMap.of(
                new C1(2, "Foz"), new C4(12, "Baz"),
                new C1(1, "Foo"), new C4(11, "Bar")
        );

        assertTrue(equalsByFields.isEqualByFields(m1, m1));
        assertTrue(Objects.equals(m1, m1));

        assertFalse(equalsByFields.isEqualByFields(m1, m2));
        assertTrue(Objects.equals(m1, m2));
        assertFalse(equalsByFields.isEqualByFields(m2, m1));
        assertTrue(Objects.equals(m2, m1));

        assertFalse(equalsByFields.isEqualByFields(m1, m3));
        assertFalse(Objects.equals(m1, m3));
        assertFalse(equalsByFields.isEqualByFields(m3, m1));
        assertFalse(Objects.equals(m3, m1));

        assertTrue(equalsByFields.isEqualByFields(m4, m4));
        assertTrue(Objects.equals(m4, m4));

        // Disse multimaps er ordered
        assertFalse(equalsByFields.isEqualByFields(m4, m5));
        assertFalse(Objects.equals(m4, m5));
        assertFalse(equalsByFields.isEqualByFields(m5, m4));
        assertFalse(Objects.equals(m5, m4));

        assertFalse(equalsByFields.isEqualByFields(m1, m5));
        assertFalse(Objects.equals(m1, m5));
        assertFalse(equalsByFields.isEqualByFields(m5, m1));
        assertFalse(Objects.equals(m5, m1));
    }

    // Typisk Hibernate entity (bortsett fra mutability)
    private static class C1 implements EqualityByFields {
        private final int id;
        @SuppressWarnings("unused")
        private final String s;

        private C1(int id, String s) {
            this.id = id;
            this.s = s;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            C1 c1 = (C1) o;

            return id == c1.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

    // Mer kompleks greie med liste
    private static class C2 implements EqualityByFields {
        private final int id;
        private final List<C4> children;

        public C2(int id, List<C4> children) {
            this.id = id;
            this.children = children;
        }

        @Override
        public boolean equalsByFields(Object o, EqualsByFields comparator) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            C2 c2 = (C2) o;

            return id == c2.id
                    && comparator.isEqualByFields(children, c2.children);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            C2 c2 = (C2) o;

            return id == c2.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }

    private static class C4 {
        private final int id;
        private final String s;

        public C4(int id, String s) {
            this.id = id;
            this.s = s;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            C4 c4 = (C4) o;
            return id == c4.id;
        }

        @Override
        public int hashCode() {
            return Objects.hash(id);
        }
    }

    private static class C4_Handler implements EqualityHandler<C4> {

        @Override
        public boolean checkEquals(C4 o1, @Nullable Object o2, EqualsByFields comparator) {
            if (o1 == o2) return true;
            if (o2 == null || o1.getClass() != o2.getClass()) return false;

            C4 c2 = (C4) o2;

            return comparator.isEqualByFields(o1.id, c2.id)
                    && comparator.isEqualByFields(o1.s, c2.s);
        }
    }
    
    // Mer kompleks greie med set
    private static class C3 implements EqualityByFields {
        private final int id;
        private final Set<C4> children;

        public C3(int id, Set<C4> children) {
            this.id = id;
            this.children = children;
        }

        @Override
        public boolean equalsByFields(Object o, EqualsByFields comparator) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            C3 c2 = (C3) o;

            return id == c2.id
                    && comparator.isEqualByFields(children, c2.children);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            C3 c2 = (C3) o;

            return id == c2.id;
        }

        @Override
        public int hashCode() {
            return id;
        }
    }
}
