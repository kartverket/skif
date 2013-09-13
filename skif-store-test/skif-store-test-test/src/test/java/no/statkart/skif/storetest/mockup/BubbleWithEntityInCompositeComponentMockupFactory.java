package no.statkart.skif.storetest.mockup;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.basic.BeloepValueObject;
import no.statkart.skif.storetest.domain.component.composite.BubbleWithCompositeComponent;
import no.statkart.skif.storetest.domain.component.composite.BubbleWithCompositeComponentId;
import no.statkart.skif.storetest.domain.component.composite.Level1CompositeComponent;
import no.statkart.skif.storetest.domain.component.composite.Level2CompositeComponent;
import no.statkart.skif.storetest.domain.component.entity.*;

import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
@Singleton
public class BubbleWithEntityInCompositeComponentMockupFactory extends AbstractMockupFactory {
    private final BubbleWithEntityInCompositeComponentId<?> withNullComponentsId;
    private final BubbleWithEntityInCompositeComponentId<?> withNullLevel2Id;
    private final BubbleWithEntityInCompositeComponentId<?> withNonNullComponentsId;


    @Inject
    public BubbleWithEntityInCompositeComponentMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        withNullComponentsId = getNextId();
        withNullLevel2Id = getNextId();
        withNonNullComponentsId = getNextId();
    }

    private BubbleWithEntityInCompositeComponentId<?> getNextId() {
        return getNextId(BubbleWithEntityInCompositeComponentId.class);
    }

    @Override
    public void createAllMockups() {
        int i = 0;  // Angir logisk obj nr i testsett

        store.insert(createBubbleWithValueComponent(withNullComponentsId, ++i, "Obj " + i + " med null components"));
        store.insert(createBubbleWithValueComponent(
                withNullLevel2Id, ++i, "Obj " + i + " med null level2 component",
                new Level1CompositeComponentWithEntity("Del av obj " + i),
                new Level1EntityInCompositeComponent("EntityLevel1 i obj " + i),
                ImmutableSet.of(new Level1SetEntityInCompositeComponent("EntityLevel1Set i obj " + i))
        ));

        store.insert(createBubbleWithValueComponent(
                withNonNullComponentsId, ++i, "Obj " + i + " med level1 og level2 component",
                new Level1CompositeComponentWithEntity("Level1 del av obj "),
                new Level1EntityInCompositeComponent("EntityLevel1 i obj " + i),
                ImmutableSet.of(new Level1SetEntityInCompositeComponent("EntityLevel1Set i obj " + i)),
                new Level2CompositeComponentWithEntity("Level2 del a obj " + i),
                new Level2EntityInCompositeComponent("EntityLevel2 i obj " + i),
                ImmutableSet.of(new Level2SetEntityInCompositeComponent("EntityLevel2Set i obj " + i))
        ));
    }

    private BubbleWithEntityInCompositeComponent createBubbleWithValueComponent(BubbleWithEntityInCompositeComponentId<?> id, int nr, String text) {
        BubbleWithEntityInCompositeComponent obj = new BubbleWithEntityInCompositeComponent();
        obj.setId(id);
        obj.setNr(nr);
        obj.setText(text);
        return obj;

    }

    private BubbleWithEntityInCompositeComponent createBubbleWithValueComponent(BubbleWithEntityInCompositeComponentId<?> id, int nr, String text, Level1CompositeComponentWithEntity level1CompositeComponent, Level1EntityInCompositeComponent level1Entity, Set<Level1SetEntityInCompositeComponent> level1EntitySet) {
        BubbleWithEntityInCompositeComponent obj = new BubbleWithEntityInCompositeComponent();
        obj.setId(id);
        obj.setNr(nr);
        obj.setText(text);
        obj.setLevel1Component(level1CompositeComponent);
        obj.getLevel1Component().setEntity(level1Entity);
        obj.getLevel1Component().setEntitySet(level1EntitySet);
        return obj;

    }

    private BubbleWithEntityInCompositeComponent createBubbleWithValueComponent(BubbleWithEntityInCompositeComponentId<?> id, int nr, String text, Level1CompositeComponentWithEntity level1CompositeComponent, Level1EntityInCompositeComponent level1Entity, Set<Level1SetEntityInCompositeComponent> level1EntitySet, Level2CompositeComponentWithEntity level2CompositeComponent, Level2EntityInCompositeComponent level2Entity, Set<Level2SetEntityInCompositeComponent> level2EntitySet) {
        BubbleWithEntityInCompositeComponent obj = new BubbleWithEntityInCompositeComponent();
        obj.setId(id);
        obj.setNr(nr);
        obj.setText(text);
        obj.setLevel1Component(level1CompositeComponent);
        obj.getLevel1Component().setEntity(level1Entity);
        obj.getLevel1Component().setEntitySet(level1EntitySet);
        obj.getLevel1Component().setLevel2Component(level2CompositeComponent);
        obj.getLevel1Component().getLevel2Component().setEntity(level2Entity);
        obj.getLevel1Component().getLevel2Component().setEntitySet(level2EntitySet);
        return obj;
    }

    public BubbleWithEntityInCompositeComponentId<?> getWithNullComponentsId() {
        return withNullComponentsId;
    }

    public BubbleWithEntityInCompositeComponentId<?> getWithNullLevel2Id() {
        return withNullLevel2Id;
    }

    public BubbleWithEntityInCompositeComponentId<?> getWithNonNullComponentsId() {
        return withNonNullComponentsId;
    }
}
