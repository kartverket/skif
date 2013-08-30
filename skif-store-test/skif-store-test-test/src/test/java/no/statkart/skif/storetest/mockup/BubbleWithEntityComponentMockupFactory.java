package no.statkart.skif.storetest.mockup;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.basic.BeloepValueObject;
import no.statkart.skif.storetest.domain.component.entity.*;

import java.util.Set;

/**
 * @author Henrik Fredholm
 */
@Singleton
public class BubbleWithEntityComponentMockupFactory extends AbstractMockupFactory {
    private final BubbleWithEntityComponentId<?> withNullComponentsId;
    private final BubbleWithEntityComponentId<?> withNullLevel2Id;
    private final BubbleWithEntityComponentId<?> withNonNullComponentsId;
    private final BubbleWithEntityComponentId<?> withNonNullComponentsId2;
    private final BubbleWithEntityComponentId<?> withOneSetAaComponentsId2;

    private int nextIdent= 100 * getTestNumber().getNumber()+1;  // Antar at vi ikke lager mer en 100 objekter per testset


    @Inject
    public BubbleWithEntityComponentMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        withNullComponentsId = getNextId();
        withNullLevel2Id = getNextId();
        withNonNullComponentsId = getNextId();
        withNonNullComponentsId2 = getNextId();
        withOneSetAaComponentsId2 = getNextId();
    }

    private BubbleWithEntityComponentId<?> getNextId() {
        return getNextId(BubbleWithEntityComponentId.class);
    }

    @Override
    public void createAllMockups() {
        int i=0;  // Angir logisk obj nr i testsett

        store.insert(createBubbleWithEntityComponent(withNullComponentsId, ++i, "Obj " + i + " med null components", null));
        store.insert(createBubbleWithEntityComponent(
                withNullLevel2Id, ++i, "Obj " + i + " med null level2 component",
                createLevel1Component(
                        "Del av obj " + i, new BeloepValueObject("NOK", i), ImmutableSet.of(new BeloepValueObject("DKR", i)),
                        null
                )
        ));

        store.insert(createBubbleWithEntityComponent(
                withNonNullComponentsId, ++i, "Obj " + i + " med level1 og level2 components",
                createLevel1Component(
                        "Level1 del av obj " + i, new BeloepValueObject("NOK", i), ImmutableSet.of(new BeloepValueObject("DKR", i)),
                        createLevel2Component(
                                "Level2 del a obj " + i, new BeloepValueObject("EUR", i), ImmutableSet.of(new BeloepValueObject("USD", i))
                        )
                )
        ));

        store.insert(createBubbleWithEntityComponent(
                withNonNullComponentsId2, ++i, "Obj " + i + " med level1 og level2 components",
                createLevel1Component(
                        "Level1 del av obj " + i, new BeloepValueObject("NOK", i), ImmutableSet.of(new BeloepValueObject("DKR", i)),
                        createLevel2Component(
                                "Level2 del a obj " + i, new BeloepValueObject("EUR", i), ImmutableSet.of(new BeloepValueObject("USD", i))
                        )
                )
        ));
        store.insert(createBubbleWithEntityComponent(
                withOneSetAaComponentsId2, ++i, "Obj " + i + " med 1 setAComponents", null,
                ImmutableSet.of(new SetAaEntityComponent(getNextIdent(), "Entity som tilhører Obj "+ i))));

    }

    private Level1EntityComponent createLevel1Component(String text, BeloepValueObject beloep, Set<BeloepValueObject> beloepSet, Level2EntityComponent level2Component) {
        final Level1EntityComponent obj = new Level1EntityComponent();
        obj.setText(text);
        obj.setBeloep(beloep);
        obj.setBeloepSet(beloepSet);
        obj.setLevel2Component(level2Component);
        return obj;
    }

    private Level2EntityComponent createLevel2Component(String text, BeloepValueObject beloep, Set<BeloepValueObject> beloepSet) {
        final Level2EntityComponent obj = new Level2EntityComponent();
        obj.setText(text);
        obj.setBeloep(beloep);
        obj.setBeloepSet(beloepSet);
        return obj;
    }

    private BubbleWithEntityComponent createBubbleWithEntityComponent(BubbleWithEntityComponentId<?> id, int nr, String text, Level1EntityComponent level1Component) {
        BubbleWithEntityComponent obj = new BubbleWithEntityComponent();
        obj.setId(id);
        obj.setNr(nr);
        obj.setText(text);
        obj.setLevel1Component(level1Component);
       return obj;
    }

    private BubbleWithEntityComponent createBubbleWithEntityComponent(BubbleWithEntityComponentId<?> id, int nr, String text, Level1EntityComponent level1Component, Set<SetAaEntityComponent> setAEntityComponents) {
        BubbleWithEntityComponent obj = new BubbleWithEntityComponent();
        obj.setId(id);
        obj.setNr(nr);
        obj.setText(text);
        obj.setLevel1Component(level1Component);
        obj.setAaComponents(setAEntityComponents);
        return obj;
    }
    public BubbleWithEntityComponentId<?> getWithNullComponentsId() {
        return withNullComponentsId;
    }

    public BubbleWithEntityComponentId<?> getWithNullLevel2Id() {
        return withNullLevel2Id;
    }

    public BubbleWithEntityComponentId<?> getWithNonNullComponentsId() {
        return withNonNullComponentsId;
    }

    public BubbleWithEntityComponentId<?> getWithNonNullComponentsId2() {
        return withNonNullComponentsId2;
    }

    public BubbleWithEntityComponentId<?> getWithOneSetAaComponentsId2() {
        return withOneSetAaComponentsId2;
    }

    public int getNextIdent() {
        return nextIdent++;
    }
}
