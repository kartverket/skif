package no.statkart.skif.storetest.mockup;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestNumber;
import no.statkart.skif.storetest.domain.basic.HistSimple;
import no.statkart.skif.storetest.domain.basic.HistSimpleId;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;

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
        store.setSnapshotVersion("2011-10-02 08:00:00.00");
        store.insert(createSimple(histSimpleId1, 1, "KARTGATA"));
        store.setSnapshotVersion("2011-10-02 08:01:00.00");
        store.update(createSimple(histSimpleId1, 1, "KARTVEGEN"));
        store.setSnapshotVersion("2011-10-02 08:02:00.00");
        store.update(createSimple(histSimpleId1, 1, "KARTVEIEN"));
        store.setSnapshotVersion("2011-10-02 08:03:00.00");
        store.update(createSimple(histSimpleId1, 1, "KART-VEIEN"));
        store.setSnapshotVersion("2011-10-02 08:04:00.00");
        store.update(createSimple(histSimpleId1, 1, "KARTVEIEN"));

        // histSimpleId2. En eller flere HistWithRelation peker på denne avhengig av tidspunkt
        store.setSnapshotVersion("2011-10-02 08:03:00.00");
        store.insert(createSimple(histSimpleId2, 2, "GAMMEL-VEIEN"));
        store.setSnapshotVersion("2011-10-02 08:04:00.00");
        store.update(createSimple(histSimpleId2, 2, "GAMMELVEIEN"));
    }

    private HistSimple createSimple(HistSimpleId<?> id, int nr, String text ) {
        HistSimple obj = new HistSimple();
        obj.setId(id);
        obj.setNr(nr);
        obj.setText(text);
       return obj;
    }

    public HistSimpleId<?> getHistSimpleId1() {
        return histSimpleId1;
    }

    public HistSimpleId<?> getHistSimpleId2() {
        return histSimpleId2;
    }
}
