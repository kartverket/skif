package no.statkart.skif.storetest.domain.component;

import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityComponent;
import no.statkart.skif.storetest.domain.component.entity.Level1EntityComponent;
import no.statkart.skif.storetest.domain.component.entity.SetAaEntityComponent;
import no.statkart.skif.util.CopyHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Tester bruk av {@link CopyHelper} på entity components, både som midt-/løvnode, og som rotnode.
 * Tanken er at hvis en component er rotnoden i grafen som CopyHelper kopierer, så skal ikke owner tas med, men hvis
 * component tas med som barn av sin owner, så skal owner-koblingen bevares.
 */
@SuppressWarnings("ConstantConditions")
public class CopyHelperEntityComponentTest {
    @Test
    public void testOneToOne() {
        BubbleWithEntityComponent bubble = new BubbleWithEntityComponent();
        bubble.setLevel1Component(new Level1EntityComponent());
        Assert.assertSame(bubble.getLevel1Component().getOwner(), bubble);

        BubbleWithEntityComponent bubble2 = CopyHelper.copy(bubble);
        Assert.assertSame(bubble2.getLevel1Component().getOwner(), bubble2);

        Level1EntityComponent leve1Component3 = CopyHelper.copy(bubble.getLevel1Component());
        Assert.assertNull(leve1Component3.getOwner());
    }

    @Test
    public void testOneToMany() {
        BubbleWithEntityComponent bubble = new BubbleWithEntityComponent();
        bubble.getAaComponents().add(new SetAaEntityComponent());
        Assert.assertSame(bubble.getAaComponents().iterator().next().getOwner(), bubble);

        BubbleWithEntityComponent bubble2 = CopyHelper.copy(bubble);
        Assert.assertSame(bubble2.getAaComponents().iterator().next().getOwner(), bubble2);

        SetAaEntityComponent entityComponent3 = CopyHelper.copy(bubble.getAaComponents().iterator().next());
        Assert.assertNull(entityComponent3.getOwner());
    }
}
