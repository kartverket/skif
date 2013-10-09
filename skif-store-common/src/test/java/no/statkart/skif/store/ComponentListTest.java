package no.statkart.skif.store;

import com.google.common.collect.Lists;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

import static org.fest.assertions.api.Assertions.assertThat;

/**
 * Test av basisfunksjonalitet på {@link ComponentList}. Dette er ikke brukseksempler.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class ComponentListTest {
    @Test
    public void testAddIndex() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");
        Component b = new Component("B");
        Component c = new Component("C");
        bubble.getComponents().addAll(Arrays.asList(a, b));
        bubble.getComponents().add(1, c);

        Assert.assertEquals(c.getOwner(), bubble, "c.owner");
        Assert.assertEquals(bubble.getComponents(), Arrays.asList(a, c, b), "components");
    }

    @Test
    public void testAddAllIndex() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");
        Component b = new Component("B");
        Component c = new Component("C");

        bubble.getComponents().add(c);
        bubble.getComponents().addAll(0, Arrays.asList(a, b));

        Assert.assertEquals(a.getOwner(), bubble, "a.owner");
        Assert.assertEquals(b.getOwner(), bubble, "b.owner");
        Assert.assertEquals(bubble.getComponents(), Arrays.asList(a, b, c), "components");
    }

    @Test
    public void testRemoveIndex() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");
        Component b = new Component("B");
        bubble.getComponents().add(a);
        bubble.getComponents().add(b);
        bubble.getComponents().remove(0);

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertEquals(b.getOwner(), bubble, "b.owner");
        Assert.assertEquals(bubble.getComponents(), Arrays.asList(b), "components");
    }

    @Test
    public void testSet() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");
        Component b = new Component("B");
        bubble.getComponents().add(a);

        Assert.assertEquals(a.getOwner(), bubble, "a.owner");
        Assert.assertNull(b.getOwner(), "b.owner");
        Assert.assertEquals(bubble.getComponents(), Arrays.asList(a), "components");

        bubble.getComponents().set(0, b);

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertEquals(b.getOwner(), bubble, "b.owner");
        Assert.assertEquals(bubble.getComponents(), Arrays.asList(b), "components");
    }

    @Test
    public void testIterator() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");
        Component b = new Component("B");
        bubble.getComponents().add(a);
        bubble.getComponents().add(b);

        assertThat(bubble.getComponents()).containsOnly(a, b);

        ListIterator<Component> listIterator = bubble.getComponents().listIterator();
        Component x = listIterator.next();
        Assert.assertSame(x, a);
        Assert.assertTrue(listIterator.hasNext(), "next");
        Assert.assertTrue(listIterator.hasPrevious(), "previous");

        listIterator.remove();

        Assert.assertEquals(bubble.getComponents(), Arrays.asList(b), "components");
    }

    @Test
    public void testIteratorAdd() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");
        Component b = new Component("B");
        bubble.getComponents().add(a);


        ListIterator<Component> listIterator = bubble.getComponents().listIterator();
        Component x = listIterator.next();
        Assert.assertSame(x, a);
        listIterator.add(b);

        Assert.assertEquals(a.getOwner(), bubble, "a.owner");
        Assert.assertEquals(b.getOwner(), bubble, "b.owner");
        Assert.assertEquals(bubble.getComponents(), Arrays.asList(a, b), "components");
    }

    @Test
    public void testIteratorSet() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");
        Component b = new Component("B");
        bubble.getComponents().add(a);


        ListIterator<Component> listIterator = bubble.getComponents().listIterator();
        Component x = listIterator.next();
        Assert.assertSame(x, a);

        listIterator.set(b);

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertEquals(b.getOwner(), bubble, "b.owner");
        Assert.assertEquals(bubble.getComponents(), Arrays.asList(b), "components");
    }

    @Test
    public void testIteratorRemovePrevious() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");
        Component b = new Component("B");
        bubble.getComponents().add(a);
        bubble.getComponents().add(b);


        ListIterator<Component> listIterator = bubble.getComponents().listIterator(1);
        Component x = listIterator.previous();
        Assert.assertSame(x, a);

        listIterator.remove();

        Assert.assertEquals(bubble.getComponents(), Arrays.asList(b), "components");
    }

    @Test
    public void testRemoveAll() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");
        Component b = new Component("B");
        bubble.getComponents().add(a);
        bubble.getComponents().add(b);

        bubble.getComponents().removeAll(Collections.singleton(a));

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertEquals(b.getOwner(), bubble, "b.owner");
        Assert.assertEquals(bubble.getComponents(), Arrays.asList(b), "components");
    }

    @Test
    public void testAdd() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");

        bubble.getComponents().add(a);

        Assert.assertEquals(a.getOwner(), bubble, "a.owner");
        Assert.assertEquals(bubble.getComponents(), Collections.singletonList(a), "components");
    }

    @Test
    public void testRemove() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");
        Component b = new Component("B");
        bubble.getComponents().add(a);
        bubble.getComponents().add(b);
        bubble.getComponents().remove(a);

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertEquals(b.getOwner(), bubble, "b.owner");
        Assert.assertEquals(bubble.getComponents(), Arrays.asList(b), "components");
    }

    @Test
    public void testAddAll() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");
        Component b = new Component("B");

        bubble.getComponents().addAll(Arrays.asList(a, b));

        Assert.assertEquals(a.getOwner(), bubble, "a.owner");
        Assert.assertEquals(b.getOwner(), bubble, "b.owner");
        Assert.assertEquals(bubble.getComponents(), Arrays.asList(a, b), "components");
    }

    @Test
    public void testRetainAll() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");
        Component b = new Component("B");
        bubble.getComponents().add(a);
        bubble.getComponents().add(b);

        bubble.getComponents().retainAll(Collections.singleton(b));

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertEquals(b.getOwner(), bubble, "b.owner");
        Assert.assertEquals(bubble.getComponents(), Arrays.asList(b), "aaComponents");
    }

    @Test
    public void testClear() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");
        Component b = new Component("B");
        bubble.getComponents().add(a);
        bubble.getComponents().add(b);

        bubble.getComponents().clear();

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertNull(b.getOwner(), "b.owner");
        Assert.assertEquals(bubble.getComponents().size(), 0, "aaComponents.size");
    }

    private static class Bubble extends AbstractBubbleObject {
        private static final long serialVersionUID = 1L;

        private final List<Component> components = Components.newList(this);

        public List<Component> getComponents() {
            return components;
        }
    }

    private static class Component implements BubbleComponent<Bubble> {
        private static final long serialVersionUID = 1L;

        private Bubble owner;
        private String text;

        public Component(String text) {
            this.text = text;
        }

        @Override
        public Bubble getOwner() {
            return owner;
        }

        @Override
        public void setOwner(Bubble owner) {
            this.owner = Components.checkSetOwner(this, this.owner, owner);
        }

        @Override
        public String toString() {
            return "Component{" +
                    "text='" + text + '\'' +
                    '}';
        }
    }
}
