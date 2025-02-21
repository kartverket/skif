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
    public void testIterator() {
        Bubble bubble = new Bubble();
        Component a = new Component(1, "A");
        Component b = new Component(2, "B");
        bubble.getComponents().add(a);
        bubble.getComponents().add(b);

        Assert.assertEquals(Sets.newHashSet(bubble.getComponents()), bubble.getComponents());

        Iterator<Component> iterator = bubble.getComponents().iterator();
        iterator.next();
        iterator.remove();

        Assert.assertEquals(bubble.getComponents().size(), 1, "components.size");
    }

    @Test
    public void testRemoveAll() {
        Bubble bubble = new Bubble();
        Component a = new Component(1, "A");
        Component b = new Component(2, "B");
        bubble.getComponents().add(a);
        bubble.getComponents().add(b);

        bubble.getComponents().removeAll(Collections.singleton(a));

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertSame(b.getOwner(), bubble, "b.owner");
        // Ønsker overload for Object, ikke for Set eller Collection
        //noinspection RedundantCast
        Assert.assertEquals((Object) bubble.getComponents(), (Object) Sets.newHashSet(b), "aaComponents");
    }

    @Test
    public void testAdd() {
        Bubble bubble = new Bubble();
        Component a = new Component(1, "A");

        bubble.getComponents().add(a);

        Assert.assertSame(a.getOwner(), bubble, "a.owner");
        // Ønsker overload for Object, ikke for Set eller Collection
        //noinspection RedundantCast
        Assert.assertEquals((Object) bubble.getComponents(), (Object) Collections.singleton(a), "aaComponents");
    }

    @Test
    public void testRemove() {
        Bubble bubble = new Bubble();
        Component a = new Component(1, "A");
        Component b = new Component(2, "B");
        bubble.getComponents().add(a);
        bubble.getComponents().add(b);

        bubble.getComponents().remove(a);

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertSame(b.getOwner(), bubble, "b.owner");
        // Ønsker overload for Object, ikke for Set eller Collection
        //noinspection RedundantCast
        Assert.assertEquals((Object) bubble.getComponents(), (Object) Sets.newHashSet(b), "aaComponents");
    }

    @Test
    public void testAddAll() {
        Bubble bubble = new Bubble();
        Component a = new Component(1, "A");
        Component b = new Component(2, "B");

        bubble.getComponents().addAll(Arrays.asList(a, b));

        Assert.assertSame(a.getOwner(), bubble, "a.owner");
        Assert.assertSame(b.getOwner(), bubble, "b.owner");
        // Ønsker overload for Object, ikke for Set eller Collection
        //noinspection RedundantCast
        Assert.assertEquals((Object) bubble.getComponents(), (Object) Sets.newHashSet(a, b), "aaComponents");
    }

    @Test
    public void testRetainAll() {
        Bubble bubble = new Bubble();
        Component a = new Component(1, "A");
        Component b = new Component(2, "B");
        bubble.getComponents().add(a);
        bubble.getComponents().add(b);

        bubble.getComponents().retainAll(Collections.singleton(b));

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertSame(b.getOwner(), bubble, "b.owner");
        // Ønsker overload for Object, ikke for Set eller Collection
        //noinspection RedundantCast
        Assert.assertEquals((Object) bubble.getComponents(), (Object) Sets.newHashSet(b), "aaComponents");
    }

    @Test
    public void testClear() {
        Bubble bubble = new Bubble();
        Component a = new Component(1, "A");
        Component b = new Component(2, "B");
        bubble.getComponents().add(a);
        bubble.getComponents().add(b);

        bubble.getComponents().clear();

        Assert.assertNull(a.getOwner(), "a.owner");
        Assert.assertNull(b.getOwner(), "b.owner");
        // IntelliJ antar at collections oppfører seg riktig, men siden dette er vår implementasjon, så er det det vi vil sjekke
        //noinspection ConstantValue
        Assert.assertEquals(bubble.getComponents().size(), 0, "aaComponents.size");
    }

    private static class Bubble extends AbstractBubbleObject {
        private static final long serialVersionUID = 1L;

        private final Set<Component> components = Components.newSet(this);

        public Set<Component> getComponents() {
            return components;
        }
    }

    private static class Component implements BubbleComponent<Bubble> {
        private static final long serialVersionUID = 1L;

        private Bubble owner;
        private final int number;
        private final String text;

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
            this.owner = Components.checkSetOwner(this, this.owner, owner);
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
