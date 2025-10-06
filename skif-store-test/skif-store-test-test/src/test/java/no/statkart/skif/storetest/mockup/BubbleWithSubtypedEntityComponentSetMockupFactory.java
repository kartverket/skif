package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithSubtypedEntityComponentSet;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithSubtypedEntityComponentSetId;
import no.statkart.skif.storetest.domain.component.entity.Subtype1EntityComponentForSet;
import no.statkart.skif.storetest.domain.component.entity.SubtypedEntityComponentForSet;

import java.util.Collections;

/**
 * @since 4.6
 */
@Singleton
public class BubbleWithSubtypedEntityComponentSetMockupFactory extends AbstractMockupFactory {
    private final BubbleWithSubtypedEntityComponentSetId<?> withNonNullSubtypedComponentsId;

    private int nextIdent = 100 * getTestNumber().getNumber() + 1;  // Antar at vi ikke lager mer en 100 objekter per testset


    @Inject
    public BubbleWithSubtypedEntityComponentSetMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        withNonNullSubtypedComponentsId = getNextId();
    }

    private BubbleWithSubtypedEntityComponentSetId<?> getNextId() {
        return getNextId(BubbleWithSubtypedEntityComponentSetId.class);
    }

    @Override
    public void createAllMockups() {
        store.insert(createBubbleWithSubtypedEntityComponent(
            withNonNullSubtypedComponentsId,
            createSubtype1Component()
        ));
    }

    private Subtype1EntityComponentForSet createSubtype1Component() {
        return new Subtype1EntityComponentForSet();
    }


    private BubbleWithSubtypedEntityComponentSet createBubbleWithSubtypedEntityComponent(BubbleWithSubtypedEntityComponentSetId<?> id, SubtypedEntityComponentForSet entityComponent) {
        BubbleWithSubtypedEntityComponentSet obj = new BubbleWithSubtypedEntityComponentSet();
        obj.setId(id);
        obj.setSubtypedEntityComponentSet(Collections.singletonList(entityComponent));
        return obj;
    }


    public BubbleWithSubtypedEntityComponentSetId<?> getWithNonNullSubtypedComponentsId() {
        return withNonNullSubtypedComponentsId;
    }

    public int getNextIdent() {
        return nextIdent++;
    }
}
