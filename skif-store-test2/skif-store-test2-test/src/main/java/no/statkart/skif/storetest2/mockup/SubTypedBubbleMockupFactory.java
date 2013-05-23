package no.statkart.skif.storetest2.mockup;

import com.google.inject.Inject;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest2.domain.subtype.SubTypeWithCollection;
import no.statkart.skif.storetest2.domain.subtype.SubTypeWithCollectionId;
import no.statkart.skif.storetest2.domain.subtype.SubTypeWithPrimitive;
import no.statkart.skif.storetest2.domain.subtype.SubTypeWithPrimitiveId;

/**
 * Definerer mockuper for {@link no.statkart.skif.storetest2.domain.subtype.SubTypedBubble}.
  *
  * @author Tor Egil R. Strand
  * @since 2.3.0
 */
public class SubTypedBubbleMockupFactory extends AbstractMockupFactory {
    private final SubTypeWithCollectionId<?> differentHistoricSubtypesId;

    @Inject
    public SubTypedBubbleMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        differentHistoricSubtypesId = getNextId(SubTypeWithCollectionId.class);
    }

    @Override
    public void createAllMockups() {
        store.setSnapshotVersion("2011-10-02 08:00:00.00");
        SubTypeWithPrimitive differentHistoricSubtypes1 = new SubTypeWithPrimitive();
        differentHistoricSubtypes1.setId(new SubTypeWithPrimitiveId<SubTypeWithPrimitive>(differentHistoricSubtypesId.getValue()));
        differentHistoricSubtypes1.setText("Text");
        differentHistoricSubtypes1.setNum(1);
        store.insert(differentHistoricSubtypes1);

        store.setSnapshotVersion("2011-10-02 09:00:00.00");
        SubTypeWithCollection differentHistoricSubtypes2 = new SubTypeWithCollection();
        differentHistoricSubtypes2.setId(differentHistoricSubtypesId);
        differentHistoricSubtypes2.setText("Tekst");
        store.update(differentHistoricSubtypes2);
    }

    public SubTypeWithCollectionId<?> getDifferentHistoricSubtypesId() {
        return differentHistoricSubtypesId;
    }
}
