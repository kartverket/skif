package no.statkart.skif.storetest.mockup;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityInCompositeComponent;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityInCompositeComponentId;
import no.statkart.skif.storetest.domain.component.entity.Level1CompositeComponentWithEntity;
import no.statkart.skif.storetest.domain.component.entity.Level1EntityInCompositeComponent;
import no.statkart.skif.storetest.domain.component.entity.Level1SetEntityInCompositeComponent;
import no.statkart.skif.storetest.domain.component.entity.Level2CompositeComponentWithEntity;
import no.statkart.skif.storetest.domain.component.entity.Level2EntityInCompositeComponent;
import no.statkart.skif.storetest.domain.component.entity.Level2SetEntityInCompositeComponent;

import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
@Singleton
public class BubbleWithEntityInCompositeComponentMockupFactory extends AbstractMockupFactory {
    private final BubbleWithEntityInCompositeComponentId<?> withNullComponentsId;
    private final BubbleWithEntityInCompositeComponentId<?> withNullLevel2Id;
    private final BubbleWithEntityInCompositeComponentId<?> withNullLevel2Id2;
    private final BubbleWithEntityInCompositeComponentId<?> withNonNullComponentsId;
    private final BubbleWithEntityInCompositeComponentId<?> withNonNullComponentsId2;
    private final BubbleWithEntityInCompositeComponentId<?> withNonNullComponentsId3;


    @Inject
    public BubbleWithEntityInCompositeComponentMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        withNullComponentsId = getNextId();
        withNullLevel2Id = getNextId();
        withNullLevel2Id2 = getNextId();
        withNonNullComponentsId = getNextId();
        withNonNullComponentsId2 = getNextId();
        withNonNullComponentsId3 = getNextId();
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
                withNullLevel2Id2, ++i, "Obj " + i + " med null level2 component",
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
        store.insert(createBubbleWithValueComponent(
                withNonNullComponentsId2, ++i, "Obj " + i + " med level1 og level2 component",
                new Level1CompositeComponentWithEntity("Level1 del av obj "),
                new Level1EntityInCompositeComponent("EntityLevel1 i obj " + i),
                ImmutableSet.of(new Level1SetEntityInCompositeComponent("EntityLevel1Set i obj " + i)),
                new Level2CompositeComponentWithEntity("Level2 del a obj " + i),
                new Level2EntityInCompositeComponent("EntityLevel2 i obj " + i),
                ImmutableSet.of(new Level2SetEntityInCompositeComponent("EntityLevel2Set i obj " + i))
        ));
        store.insert(createBubbleWithValueComponent(
                withNonNullComponentsId3, ++i, "Obj " + i + " med level1 og level2 component",
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
        level1CompositeComponent.setEntity(level1Entity);
        level1CompositeComponent.setEntitySet(level1EntitySet);
        obj.setLevel1Component(level1CompositeComponent);
        return obj;

    }

    private BubbleWithEntityInCompositeComponent createBubbleWithValueComponent(BubbleWithEntityInCompositeComponentId<?> id, int nr, String text, Level1CompositeComponentWithEntity level1CompositeComponent, Level1EntityInCompositeComponent level1Entity, Set<Level1SetEntityInCompositeComponent> level1EntitySet, Level2CompositeComponentWithEntity level2CompositeComponent, Level2EntityInCompositeComponent level2Entity, Set<Level2SetEntityInCompositeComponent> level2EntitySet) {
        BubbleWithEntityInCompositeComponent obj = new BubbleWithEntityInCompositeComponent();
        obj.setId(id);
        obj.setNr(nr);
        obj.setText(text);

        level2CompositeComponent.setEntity(level2Entity);
        level2CompositeComponent.setEntitySet(level2EntitySet);

        level1CompositeComponent.setLevel2Component(level2CompositeComponent);
        level1CompositeComponent.setEntity(level1Entity);
        level1CompositeComponent.setEntitySet(level1EntitySet);

        obj.setLevel1Component(level1CompositeComponent);
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
