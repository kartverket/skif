package no.statkart.skif.store;

import com.google.common.collect.Lists;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.*;

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
        Components.setOwner(a, bubble);
        Components.setOwner(b, bubble);
        bubble.getComponents().addAll(Arrays.asList(a, b));

        ComponentList<Bubble, Component> ComponentList = new ComponentList<Bubble, Component>(bubble, bubble.getComponents());
        ComponentList.add(1, c);

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
        Components.setOwner(c, bubble);

        ComponentList<Bubble, Component> componentList = new ComponentList<Bubble, Component>(bubble, bubble.getComponents());
        componentList.addAll(0, Arrays.asList(a, b));

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
        Components.setOwner(a, bubble);
        bubble.getComponents().add(b);
        Components.setOwner(b, bubble);

        ComponentList<Bubble, Component> componentList = new ComponentList<Bubble, Component>(bubble, bubble.getComponents());
        componentList.remove(0);

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
        Components.setOwner(a, bubble);

        Assert.assertEquals(a.getOwner(), bubble, "a.owner");
        Assert.assertNull(b.getOwner(), "b.owner");
        Assert.assertEquals(bubble.getComponents(), Arrays.asList(a), "components");

        ComponentList<Bubble, Component> componentList = new ComponentList<Bubble, Component>(bubble, bubble.getComponents());
        componentList.set(0, b);

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
        Components.setOwner(a, bubble);
        bubble.getComponents().add(b);
        Components.setOwner(b, bubble);

        ComponentList<Bubble, Component> componentList = new ComponentList<Bubble, Component>(bubble, bubble.getComponents());
        Assert.assertEquals(Lists.newArrayList(componentList.iterator()), bubble.getComponents());

        ListIterator<Component> listIterator = componentList.listIterator();
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
        Components.setOwner(a, bubble);

        ComponentList<Bubble, Component> componentList = new ComponentList<Bubble, Component>(bubble, bubble.getComponents());

        ListIterator<Component> listIterator = componentList.listIterator();
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
        Components.setOwner(a, bubble);

        ComponentList<Bubble, Component> componentList = new ComponentList<Bubble, Component>(bubble, bubble.getComponents());

        ListIterator<Component> listIterator = componentList.listIterator();
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
        Components.setOwner(a, bubble);
        bubble.getComponents().add(b);
        Components.setOwner(b, bubble);

        ComponentList<Bubble, Component> componentList = new ComponentList<Bubble, Component>(bubble, bubble.getComponents());

        ListIterator<Component> listIterator = componentList.listIterator(1);
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
        Components.setOwner(a, bubble);
        bubble.getComponents().add(b);
        Components.setOwner(b, bubble);

        ComponentList<Bubble, Component> componentList = new ComponentList<Bubble, Component>(bubble, bubble.getComponents());
        componentList.removeAll(Collections.singleton(a));

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertEquals(b.getOwner(), bubble, "b.owner");
        Assert.assertEquals(bubble.getComponents(), Arrays.asList(b), "components");
    }

    @Test
    public void testAdd() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");

        ComponentList<Bubble, Component> componentList = new ComponentList<Bubble, Component>(bubble, bubble.getComponents());
        componentList.add(a);

        Assert.assertEquals(a.getOwner(), bubble, "a.owner");
        Assert.assertEquals(bubble.getComponents(), Collections.singletonList(a), "components");
    }

    @Test
    public void testRemove() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");
        Component b = new Component("B");
        bubble.getComponents().add(a);
        Components.setOwner(a, bubble);
        bubble.getComponents().add(b);
        Components.setOwner(b, bubble);

        ComponentList<Bubble, Component> componentList = new ComponentList<Bubble, Component>(bubble, bubble.getComponents());
        componentList.remove(a);

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertEquals(b.getOwner(), bubble, "b.owner");
        Assert.assertEquals(bubble.getComponents(), Arrays.asList(b), "components");
    }

    @Test
    public void testAddAll() throws Exception {
        Bubble bubble = new Bubble();
        Component a = new Component("A");
        Component b = new Component("B");

        ComponentList<Bubble, Component> componentList = new ComponentList<Bubble, Component>(bubble, bubble.getComponents());
        componentList.addAll(Arrays.asList(a, b));

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
        Components.setOwner(a, bubble);
        bubble.getComponents().add(b);
        Components.setOwner(b, bubble);

        ComponentList<Bubble, Component> componentList = new ComponentList<Bubble, Component>(bubble, bubble.getComponents());
        componentList.retainAll(Collections.singleton(b));

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
        Components.setOwner(a, bubble);
        bubble.getComponents().add(b);
        Components.setOwner(b, bubble);

        ComponentList<Bubble, Component> componentList = new ComponentList<Bubble, Component>(bubble, bubble.getComponents());
        componentList.clear();

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertNull(b.getOwner(), "b.owner");
        Assert.assertEquals(bubble.getComponents().size(), 0, "aaComponents.size");
    }

    private static class Bubble extends AbstractBubbleObject {
        private List<Component> components = new ArrayList<Component>();

        public List<Component> getComponents() {
            return components;
        }
    }

    private static class Component implements BubbleComponent<Bubble> {
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
            // TODO Sjekking
            this.owner = owner;
        }

        @Override
        public String toString() {
            return "Component{" +
                    "text='" + text + '\'' +
                    '}';
        }
    }
}
