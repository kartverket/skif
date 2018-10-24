package no.statkart.skif.store;

import com.google.common.collect.Sets;
import org.fest.assertions.api.Assertions;
import org.testng.annotations.Test;

import java.util.HashSet;

public class ComponentsTest {

    @Test
    public void testSetFrom_emptyToEmpty() {
        HashSet<TestComponent> to = new HashSet<>();
        HashSet<TestComponent> from = new HashSet<>();
        Components.setFrom(to, from);
        Assertions.assertThat(to).isEmpty();
        Assertions.assertThat(from).isEmpty();
    }

    @Test
    public void testSetFrom_somethingToEmpty() {
        HashSet<TestComponent> to = new HashSet<>();
        HashSet<TestComponent> from = Sets.newHashSet(new TestComponent(), new TestComponent());
        Components.setFrom(to, from);
        Assertions.assertThat(to).hasSize(2);
        Assertions.assertThat(to).isEqualTo(from);
        Assertions.assertThat(from).hasSize(2);
    }

    @Test
    public void testSetFrom_emptyToSomething() {
        HashSet<TestComponent> to = Sets.newHashSet(new TestComponent(), new TestComponent());
        HashSet<TestComponent> from = new HashSet<>();
        Components.setFrom(to, from);
        Assertions.assertThat(to).isEmpty();
        Assertions.assertThat(from).isEmpty();
    }

    @Test
    public void testSetFrom_somethingToSomething() {
        HashSet<TestComponent> to = Sets.newHashSet(new TestComponent(), new TestComponent());
        HashSet<TestComponent> from = Sets.newHashSet(new TestComponent(), new TestComponent());
        Assertions.assertThat(to).isNotEqualTo(from);
        Components.setFrom(to, from);
        Assertions.assertThat(to).hasSize(2);
        Assertions.assertThat(to).isEqualTo(from);
        Assertions.assertThat(from).hasSize(2);
    }

    @Test
    public void testSetFrom_fromToSame() {
        HashSet<TestComponent> set = Sets.newHashSet(new TestComponent(), new TestComponent());
        Components.setFrom(set, set);
        Assertions.assertThat(set).hasSize(2);
    }

    private static class TestComponent implements Component {
        private static final long serialVersionUID = 1L;
    }
}
