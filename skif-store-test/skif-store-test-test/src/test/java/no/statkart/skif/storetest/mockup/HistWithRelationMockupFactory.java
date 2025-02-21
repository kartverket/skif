package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.basic.*;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
@Singleton
public class HistWithRelationMockupFactory extends AbstractMockupFactory {
    private final HistWithRelationId<?> histWithRelationId1;
    private final HistWithRelationId<?> histWithRelationId2;

    @Inject
    HistSimpleMockupFactory histSimpleMockupFactory;

    @Inject
    public HistWithRelationMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        histWithRelationId1 = getNextId();
        histWithRelationId2 = getNextId();
    }

    private HistWithRelationId<?> getNextId() {
        return getNextId(HistWithRelationId.class);
    }

    /**
     * Oppretter 3 objekter som peker på HistSimpleId2 på forskjellige tidspunkter.
     * <ul>
     *     <li>Kl '2011-10-02 08:02:01.00' Er det en som peker</li>
     *     <li>Kl '2011-10-02 08:02:02.00' Er det to som peker</li>
     *     <li>Kl '2011-10-02 08:02:03.00' Er det ingen som peker</li>
     * </ul>
     */
    @Override
    public void createAllMockups() {
        store.setSnapshotVersion(MockupSnapshots.S2_01);
        store.insert(createHistWithRelation(histWithRelationId1, 1, "Gruppe A", histSimpleMockupFactory.getHistSimpleId2()));

        store.setSnapshotVersion(MockupSnapshots.S2_02);
        store.update(createHistWithRelation(histWithRelationId1, 1, "Gruppe A", histSimpleMockupFactory.getHistSimpleId2()));
        store.insert(createHistWithRelation(histWithRelationId2, 2, "Gruppe B", histSimpleMockupFactory.getHistSimpleId2()));

        store.setSnapshotVersion(MockupSnapshots.S2_03);
        store.delete(createHistWithRelation(histWithRelationId1, 1, null, histSimpleMockupFactory.getHistSimpleId2()));
        store.delete(createHistWithRelation(histWithRelationId2, 2, null, histSimpleMockupFactory.getHistSimpleId2()));
    }

    private HistWithRelation createHistWithRelation(HistWithRelationId<?> id, int nr, String text, HistSimpleId<?> histSimpleId) {
        HistWithRelation obj = new HistWithRelation();
        obj.setId(id);
        obj.setNr(nr);
        obj.setText(text);
        obj.setHistSimpleId(histSimpleId);
        obj.setTestSetNumber(getTestNumber().getNumber());
        return obj;
    }

    public HistWithRelationId<?> getHistWithRelationId1() {
        return histWithRelationId1;
    }

    public HistWithRelationId<?> getHistWithRelationId2() {
        return histWithRelationId2;
    }

}
