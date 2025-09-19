package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithSubtypedEntityComponent;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithSubtypedEntityComponentId;
import no.statkart.skif.storetest.domain.component.entity.Subtype1EntityComponent;
import no.statkart.skif.storetest.domain.component.entity.SubtypedEntityComponent;

/**
 * @author Martin Halleland
 * @since 4.6
 */
@Singleton
public class BubbleWithSubtypedEntityComponentMockupFactory extends AbstractMockupFactory {
    private final BubbleWithSubtypedEntityComponentId<?> withNonNullSubtypedComponentsId;

    private int nextIdent = 100 * getTestNumber().getNumber() + 1;  // Antar at vi ikke lager mer en 100 objekter per testset


    @Inject
    public BubbleWithSubtypedEntityComponentMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        withNonNullSubtypedComponentsId = getNextId();
    }

    private BubbleWithSubtypedEntityComponentId<?> getNextId() {
        return getNextId(BubbleWithSubtypedEntityComponentId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createBubbleWithSubtypedEntityComponent(
            withNonNullSubtypedComponentsId,
            createSubtype1Component()
        ));
    }

    private Subtype1EntityComponent createSubtype1Component() {
        return new Subtype1EntityComponent();
    }


    private BubbleWithSubtypedEntityComponent createBubbleWithSubtypedEntityComponent(BubbleWithSubtypedEntityComponentId<?> id, SubtypedEntityComponent entityComponent) {
        BubbleWithSubtypedEntityComponent obj = new BubbleWithSubtypedEntityComponent();
        obj.setId(id);
        obj.setSubtypedEntityComponent(entityComponent);
        return obj;
    }


    public BubbleWithSubtypedEntityComponentId<?> getWithNonNullSubtypedComponentsId() {
        return withNonNullSubtypedComponentsId;
    }

    public int getNextIdent() {
        return nextIdent++;
    }
}
