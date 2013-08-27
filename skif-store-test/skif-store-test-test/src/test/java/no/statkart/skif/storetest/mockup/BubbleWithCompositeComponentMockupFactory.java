package no.statkart.skif.storetest.mockup;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.basic.BeloepValueObject;
import no.statkart.skif.storetest.domain.component.composite.*;

import java.util.Set;

/**
 * @author Henrik Fredholm
 */
@Singleton
public class BubbleWithCompositeComponentMockupFactory extends AbstractMockupFactory {
    private final BubbleWithCompositeComponentId<?> withNullComponentsId;
    private final BubbleWithCompositeComponentId<?> withNullLevel2Id;
    private final BubbleWithCompositeComponentId<?> withNonNullComponentsId;


    @Inject
    public BubbleWithCompositeComponentMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        withNullComponentsId = getNextId();
        withNullLevel2Id = getNextId();
        withNonNullComponentsId = getNextId();
    }

    private BubbleWithCompositeComponentId<?> getNextId() {
        return getNextId(BubbleWithCompositeComponentId.class);
    }

    @Override
    public void createAllMockups() {
        int i=0;  // Angir logisk obj nr i testsett

        store.insert(createBubbleWithValueComponent(withNullComponentsId, ++i, "Obj " + i + " med null components",null));
        store.insert(createBubbleWithValueComponent(
                withNullLevel2Id, ++i, "Obj " + i + " med null level2 component",
                createLevel1Component(
                        "Del av obj " + i, new BeloepValueObject("NOK", i), ImmutableSet.of(new BeloepValueObject("DKR", i)),
                        new EntityInCompositeComponent("EntityLevel1 i obj " + i),
                        ImmutableSet.of(new EntityInCompositeComponent("EntityLevel1Set i obj " + i)),
                        null
                        )
        ));

        store.insert(createBubbleWithValueComponent(
                withNonNullComponentsId, ++i, "Obj " + i + " med level1 og level2 component",
                createLevel1Component(
                        "Level1 del av obj " + i, new BeloepValueObject("NOK", i), ImmutableSet.of(new BeloepValueObject("DKR", i)),
                        new EntityInCompositeComponent("EntityLevel1 i obj " + i),
                        ImmutableSet.of(new EntityInCompositeComponent("EntityLevel1Set i obj " + i)),
                        createLevel2Component(
                                "Level2 del a obj " + i, new BeloepValueObject("EUR", i), ImmutableSet.of(new BeloepValueObject("USD", i)),
                                new EntityInCompositeComponent("EntityLevel2 i obj " + i),
                                ImmutableSet.of(new EntityInCompositeComponent("EntityLevel2Set i obj " + i))
                        )
                )
        ));
    }

    private Level1CompositeComponent createLevel1Component(String text, BeloepValueObject beloep, Set<BeloepValueObject> beloepSet, EntityInCompositeComponent entity, Set<EntityInCompositeComponent> entitySet, Level2CompositeComponent level2ValueComponent) {
        final Level1CompositeComponent obj = new Level1CompositeComponent();
        obj.setText(text);
        obj.setBelop(beloep);
        obj.setBeloepSet(beloepSet);
        obj.setEntity(entity);
        obj.setEntitySet(entitySet);
        obj.setLevel2Component(level2ValueComponent);
        return obj;
    }

    private Level2CompositeComponent createLevel2Component(String text, BeloepValueObject beloep, Set<BeloepValueObject> beloepSet, EntityInCompositeComponent entity, Set<EntityInCompositeComponent> entitySet) {
        final Level2CompositeComponent obj = new Level2CompositeComponent();
        obj.setText(text);
        obj.setBelop(beloep);
        obj.setBeloepSet(beloepSet);
        obj.setEntity(entity);
        obj.setEntitySet(entitySet);
        return obj;
    }

    private BubbleWithCompositeComponent createBubbleWithValueComponent(BubbleWithCompositeComponentId<?> id, int nr, String text, Level1CompositeComponent level1ValueComponent ) {
        BubbleWithCompositeComponent obj = new BubbleWithCompositeComponent();
        obj.setId(id);
        obj.setNr(nr);
        obj.setText(text);
        obj.setLevel1Component(level1ValueComponent);
       return obj;
    }

    public BubbleWithCompositeComponentId<?> getWithNullComponentsId() {
        return withNullComponentsId;
    }

    public BubbleWithCompositeComponentId<?> getWithNullLevel2Id() {
        return withNullLevel2Id;
    }

    public BubbleWithCompositeComponentId<?> getWithNonNullComponentsId() {
        return withNonNullComponentsId;
    }
}
