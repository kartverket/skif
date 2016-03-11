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

/**
 * @author Henrik Fredholm
 * @since 2.4
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
        int i = 0;  // Angir logisk obj nr i testsett

        store.insert(createBubbleWithValueComponent(withNullComponentsId, ++i, "Obj " + i + " med null components"));
        store.insert(createBubbleWithValueComponent(
                withNullLevel2Id, ++i, "Obj " + i + " med null level2 component",
                new Level1CompositeComponent("Del av obj " + i, new BeloepValueObject("NOK", i), ImmutableSet.of(new BeloepValueObject("DKR", i)))
        ));

        store.insert(createBubbleWithValueComponent(
                withNonNullComponentsId, ++i, "Obj " + i + " med level1 og level2 component",
                new Level1CompositeComponent(
                        "Level1 del av obj " + i, new BeloepValueObject("NOK", i), ImmutableSet.of(new BeloepValueObject("DKR", i))),
                new Level2CompositeComponent(
                        "Level2 del a obj " + i, new BeloepValueObject("EUR", i), ImmutableSet.of(new BeloepValueObject("USD", i))
                )
        ));
    }

    private BubbleWithCompositeComponent createBubbleWithValueComponent(BubbleWithCompositeComponentId<?> id, int nr, String text) {
        BubbleWithCompositeComponent obj = new BubbleWithCompositeComponent();
        obj.setId(id);
        obj.setNr(nr);
        obj.setText(text);
        return obj;

    }

    private BubbleWithCompositeComponent createBubbleWithValueComponent(BubbleWithCompositeComponentId<?> id, int nr, String text, Level1CompositeComponent level1CompositeComponent) {
        BubbleWithCompositeComponent obj = new BubbleWithCompositeComponent();
        obj.setId(id);
        obj.setNr(nr);
        obj.setText(text);
        obj.setLevel1Component(level1CompositeComponent);
        return obj;

    }

    private BubbleWithCompositeComponent createBubbleWithValueComponent(BubbleWithCompositeComponentId<?> id, int nr, String text, Level1CompositeComponent level1CompositeComponent,  Level2CompositeComponent level2CompositeComponent) {
        BubbleWithCompositeComponent obj = new BubbleWithCompositeComponent();
        obj.setId(id);
        obj.setNr(nr);
        obj.setText(text);
        obj.setLevel1Component(level1CompositeComponent);
        obj.getLevel1Component().setLevel2Component(level2CompositeComponent);
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
