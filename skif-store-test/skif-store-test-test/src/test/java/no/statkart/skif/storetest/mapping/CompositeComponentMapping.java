package no.statkart.skif.storetest.mapping;

import com.google.inject.util.Providers;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.component.composite.BubbleWithCompositeComponent;
import no.statkart.skif.storetest.domain.component.composite.Level1CompositeComponent;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapping;
import org.testng.Assert;
import org.testng.annotations.Test;

public class CompositeComponentMapping {
    @Test
    @SuppressWarnings("ConstantConditions")
    public void testCompositeComponentMapping() {
        BubbleWithCompositeComponent bubble = new BubbleWithCompositeComponent();
        bubble.setLevel1Component(new Level1CompositeComponent());
        // Dette er forutsetninger, ikke det som egentlig testes i denne omgang
        Assert.assertSame(bubble.getLevel1Component().getOwner(), bubble);
        Assert.assertSame(bubble.getLevel1Component().getCompositeRootOwner(), bubble);
        Assert.assertSame(bubble.getLevel1Component().getLevel2Component().getOwner(), bubble.getLevel1Component());
        Assert.assertSame(bubble.getLevel1Component().getLevel2Component().getCompositeRootOwner(), bubble);

        StoreTestMapping mapping = new StoreTestMapper(Providers.of(SnapshotVersion.CURRENT)).getMapping();

        no.statkart.skif.storetest.wsapi.domain.component.composite.BubbleWithCompositeComponent wsBubble = mapping.d2w(bubble, no.statkart.skif.storetest.wsapi.domain.component.composite.BubbleWithCompositeComponent.class);
        BubbleWithCompositeComponent bubble2 = mapping.w2d(wsBubble, BubbleWithCompositeComponent.class);

        Assert.assertSame(bubble2.getLevel1Component().getOwner(), bubble2);
        Assert.assertSame(bubble2.getLevel1Component().getCompositeRootOwner(), bubble2);
        Assert.assertSame(bubble2.getLevel1Component().getLevel2Component().getOwner(), bubble2.getLevel1Component());
        Assert.assertSame(bubble2.getLevel1Component().getLevel2Component().getCompositeRootOwner(), bubble2);
    }
}
