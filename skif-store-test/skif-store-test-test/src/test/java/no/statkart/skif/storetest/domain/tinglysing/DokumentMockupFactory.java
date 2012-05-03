package no.statkart.skif.storetest.domain.tinglysing;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestIdGenerator;
import no.statkart.skif.mockup.TestNumber;

@Singleton
public class DokumentMockupFactory extends AbstractMockupFactory<TestIdGenerator<Long>> {
    private final DokumentId<?> id_100394_200_2010_2;
    private final DokumentId<?> id_2026_56_2002_1;
    private final DokumentId<?> id_2338_56_2002_3;

    @Inject
    public DokumentMockupFactory(MockupStore store, TestNumber testNumber, TestIdGenerator<Long> testIdGenerator) {
        super(store, testNumber, testIdGenerator);
        id_100394_200_2010_2 = getNextId(DokumentId.class);
        id_2338_56_2002_3 = getNextId(DokumentId.class);
        id_2026_56_2002_1 = getNextId(DokumentId.class);
    }

    public void createAllMockups() {
        store.insert(createDokument(id_2026_56_2002_1, 2002, 2026, "  "));
        store.insert(createDokument(id_2338_56_2002_3, 2002, 2338, "  "));
        store.insert(createDokument(id_100394_200_2010_2, 2010, 100394, "  "));
    }

    private Dokument createDokument(DokumentId<?> id, int dokumentaar, int dokumentnummer, String status) {
        Dokument dokument = new Dokument();
        dokument.setId(id);
        dokument.setDokumentaar(dokumentaar);
        dokument.setDokumentnummer(dokumentnummer);
        dokument.setStatus(status);
        return dokument;
    }

    public DokumentId<?> getId_100394_200_2010_2() {
        return id_100394_200_2010_2;
    }

    public DokumentId<?> getId_2026_56_2002_1() {
        return id_2026_56_2002_1;
    }

    public DokumentId<?> getId_2338_56_2002_3() {
        return id_2338_56_2002_3;
    }
}
