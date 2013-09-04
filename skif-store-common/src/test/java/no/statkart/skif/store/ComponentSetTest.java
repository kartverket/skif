package no.statkart.skif.store;

import com.google.common.collect.Sets;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

/**
 * Test av basisfunksjonalitet på {@link ComponentSet}. Dette er ikke brukseksempler.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class ComponentSetTest {
    @Test
    public void testIterator() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component(1, "A");
        Component b = new Component(2, "B");
        bubble.getComponents().add(a);
        Components.setOwner(a, bubble);
        bubble.getComponents().add(b);
        Components.setOwner(b, bubble);

        ComponentSet<Bubble, Component> componentSet = new ComponentSet<Bubble, Component>(bubble, bubble.getComponents());
        Assert.assertEquals(Sets.newHashSet(componentSet.iterator()), bubble.getComponents());

        Iterator<Component> iterator = componentSet.iterator();
        iterator.next();
        iterator.remove();

        Assert.assertEquals(bubble.getComponents().size(), 1, "components.size");
    }

    @Test
    public void testRemoveAll() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component(1, "A");
        Component b = new Component(2, "B");
        bubble.getComponents().add(a);
        Components.setOwner(a, bubble);
        bubble.getComponents().add(b);
        Components.setOwner(b, bubble);

        ComponentSet<Bubble, Component> componentSet = new ComponentSet<Bubble, Component>(bubble, bubble.getComponents());
        componentSet.removeAll(Collections.singleton(a));

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertEquals(b.getOwner(), bubble, "b.owner");
        Assert.assertEquals((Object) bubble.getComponents(), (Object) Sets.newHashSet(b), "aaComponents");
    }

    @Test
    public void testAdd() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component(1, "A");

        ComponentSet<Bubble, Component> componentSet = new ComponentSet<Bubble, Component>(bubble, bubble.getComponents());
        componentSet.add(a);

        Assert.assertEquals(a.getOwner(), bubble, "a.owner");
        Assert.assertEquals((Object) bubble.getComponents(), (Object) Collections.singleton(a), "aaComponents");
    }

    @Test
    public void testRemove() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component(1, "A");
        Component b = new Component(2, "B");
        bubble.getComponents().add(a);
        Components.setOwner(a, bubble);
        bubble.getComponents().add(b);
        Components.setOwner(b, bubble);

        ComponentSet<Bubble, Component> componentSet = new ComponentSet<Bubble, Component>(bubble, bubble.getComponents());
        componentSet.remove(a);

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertEquals(b.getOwner(), bubble, "b.owner");
        Assert.assertEquals((Object) bubble.getComponents(), (Object) Sets.newHashSet(b), "aaComponents");
    }

    @Test
    public void testAddAll() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component(1, "A");
        Component b = new Component(2, "B");

        ComponentSet<Bubble, Component> componentSet = new ComponentSet<Bubble, Component>(bubble, bubble.getComponents());
        componentSet.addAll(Arrays.asList(a, b));

        Assert.assertEquals(a.getOwner(), bubble, "a.owner");
        Assert.assertEquals(b.getOwner(), bubble, "b.owner");
        Assert.assertEquals((Object) bubble.getComponents(), (Object) Sets.newHashSet(a, b), "aaComponents");
    }

    @Test
    public void testRetainAll() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component(1, "A");
        Component b = new Component(2, "B");
        bubble.getComponents().add(a);
        Components.setOwner(a, bubble);
        bubble.getComponents().add(b);
        Components.setOwner(b, bubble);

        ComponentSet<Bubble, Component> componentSet = new ComponentSet<Bubble, Component>(bubble, bubble.getComponents());
        componentSet.retainAll(Collections.singleton(b));

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertEquals(b.getOwner(), bubble, "b.owner");
        Assert.assertEquals((Object) bubble.getComponents(), (Object) Sets.newHashSet(b), "aaComponents");
    }

    @Test
    public void testClear() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component(1, "A");
        Component b = new Component(2, "B");
        bubble.getComponents().add(a);
        Components.setOwner(a, bubble);
        bubble.getComponents().add(b);
        Components.setOwner(b, bubble);

        ComponentSet<Bubble, Component> componentSet = new ComponentSet<Bubble, Component>(bubble, bubble.getComponents());
        componentSet.clear();

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertNull(b.getOwner(), "b.owner");
        Assert.assertEquals(bubble.getComponents().size(), 0, "aaComponents.size");
    }

    private static class Bubble extends AbstractBubbleObject {
        private static final long serialVersionUID = 1L;

        private Set<Component> components = new HashSet<Component>();

        public Set<Component> getComponents() {
            return components;
        }
    }

    private static class Component implements BubbleComponent<Bubble> {
        private static final long serialVersionUID = 1L;

        private Bubble owner;
        private int number;
        private String text;

        public Component(int number, String text) {
            this.number = number;
            this.text = text;
        }

        @Override
        public Bubble getOwner() {
            return owner;
        }

        @Override
        public void setOwner(Bubble owner) {
            // TODO Sjekking
            this.owner = owner;
        }

        @Override
        public String toString() {
            return "Component{" +
                    "text='" + text + '\'' +
                    ", number=" + number +
                    '}';
        }
    }
}
