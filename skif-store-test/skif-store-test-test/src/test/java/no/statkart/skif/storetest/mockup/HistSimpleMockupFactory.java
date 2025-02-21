package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.basic.HistSimple;
import no.statkart.skif.storetest.domain.basic.HistSimpleId;

/**
 * @author Henrik Fredholm
 */
@Singleton
public class HistSimpleMockupFactory extends AbstractMockupFactory {
    private final HistSimpleId<?> histSimpleId1;
    private final HistSimpleId<?> histSimpleId2;


    @Inject
    public HistSimpleMockupFactory(MockupStore store, TestNumber testNumber) {
        super(store, testNumber);

        histSimpleId1 = getNextId();
        histSimpleId2 = getNextId();
    }

    private HistSimpleId<?> getNextId() {
        return getNextId(HistSimpleId.class);
    }

    @Override
    public void createAllMockups() {
        // histSimpleId1. Ingen HistWithRelation peker på denne

        store.setSnapshotVersion(MockupSnapshots.S0);
        store.insert(createSimple(histSimpleId1, 1, "KARTGATA"));
        store.setSnapshotVersion(MockupSnapshots.S1);
        store.update(createSimple(histSimpleId1, 1, "KARTVEGEN"));
        store.setSnapshotVersion(MockupSnapshots.S2);
        store.update(createSimple(histSimpleId1, 1, "KARTVEIEN"));
        store.setSnapshotVersion(MockupSnapshots.S3);
        store.update(createSimple(histSimpleId1, 1, "KART-VEIEN"));
        store.setSnapshotVersion(MockupSnapshots.S4);
        store.update(createSimple(histSimpleId1, 1, "KARTVEIEN"));

        // histSimpleId2. En eller flere HistWithRelation peker på denne avhengig av tidspunkt
        store.setSnapshotVersion(MockupSnapshots.S2);
        store.insert(createSimple(histSimpleId2, 2, "GAMMELVEIEN"));
        store.setSnapshotVersion(MockupSnapshots.S3);
        store.update(createSimple(histSimpleId2, 2, "GAMMEL-VEIEN"));
    }

    private HistSimple createSimple(HistSimpleId<?> id, int nr, String text ) {
        HistSimple obj = new HistSimple();
        obj.setId(id);
        obj.setNr(nr);
        obj.setText(text);
        obj.setTestSetNumber(getTestNumber().getNumber());
       return obj;
    }

    public HistSimpleId<?> getHistSimpleId1() {
        return histSimpleId1;
    }

    public HistSimpleId<?> getHistSimpleId2() {
        return histSimpleId2;
    }
}
