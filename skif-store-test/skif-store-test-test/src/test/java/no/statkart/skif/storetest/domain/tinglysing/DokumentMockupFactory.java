package no.statkart.skif.storetest.domain.tinglysing;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.mockup.AbstractMockupFactory;
import no.statkart.skif.mockup.MockupStore;
import no.statkart.skif.mockup.TestIdGenerator;
import no.statkart.skif.mockup.TestNumber;

/**
 * @author Knut Inge Bøe
 */
@Singleton
public class DokumentMockupFactory extends AbstractMockupFactory<TestIdGenerator<Long>> {
    private final DokumentId<?> id_100394_200_2010_2;
    private final DokumentId<?> id_2026_56_2002_1;
    private final DokumentId<?> id_2338_56_2002_3;
    // FA_FAR
    private final DokumentId<?> id_404680_200_2007_1;
    // HJ_HJG, TF_HJF
    private final DokumentId<?> id_283560_200_2010_1;
    private final DokumentId<?> id_2567_56_1995_1;
    private final DokumentId<?> id_2782_56_1986_1;
    // KL_KRE
    private final DokumentId<?> id_301173_58_1964_1;
    private final DokumentId<?> id_1062140_200_2007_2;
    private final DokumentId<?> id_1062121_200_2007_1;
    private final DokumentId<?> id_1062140_200_2007_1;
    private final DokumentId<?> id_1062121_200_2007_3;
    private final DokumentId<?> id_1062121_200_2007_4;
    private final DokumentId<?> id_873_200_2009_2;
    private final DokumentId<?> id_873_200_2009_1;

    @Inject
    public DokumentMockupFactory(MockupStore store, TestNumber testNumber, TestIdGenerator<Long> testIdGenerator) {
        super(store, testNumber, testIdGenerator);
        id_100394_200_2010_2 = getNextId(DokumentId.class);
        id_2338_56_2002_3 = getNextId(DokumentId.class);
        id_2026_56_2002_1 = getNextId(DokumentId.class);
        // FA_FAR
        id_404680_200_2007_1 = getNextId(DokumentId.class);
        // HJ_HJG, TF_HJF
        id_283560_200_2010_1 = getNextId(DokumentId.class);
        id_2782_56_1986_1 = getNextId(DokumentId.class);
        id_2567_56_1995_1 = getNextId(DokumentId.class);
        // KL_KRE
        id_1062140_200_2007_2 = getNextId(DokumentId.class);
        id_1062121_200_2007_4 = getNextId(DokumentId.class);
        id_1062121_200_2007_3 = getNextId(DokumentId.class);
        id_301173_58_1964_1 = getNextId(DokumentId.class);
        id_1062140_200_2007_1 = getNextId(DokumentId.class);
        id_1062121_200_2007_1 = getNextId(DokumentId.class);
        id_873_200_2009_1 = getNextId(DokumentId.class);
        id_873_200_2009_2 = getNextId(DokumentId.class);
    }

    public void createAllMockups() {
        store.insert(createDokument(id_2026_56_2002_1, 2002, 2026, "  "));
        store.insert(createDokument(id_2338_56_2002_3, 2002, 2338, "  "));
        store.insert(createDokument(id_100394_200_2010_2, 2010, 100394, "  "));
        // FA_FAR
        store.insert(createDokument(id_404680_200_2007_1, 2007, 404680, "  "));
        // HJ_HJG, TF_HJF
        store.insert(createDokument(id_283560_200_2010_1, 2010, 283560, "  "));
        store.insert(createDokument(id_2782_56_1986_1, 1986, 2782, "  "));
        store.insert(createDokument(id_2567_56_1995_1, 1995, 2567, "  "));
        // KL_KRE
        store.insert(createDokument(id_1062121_200_2007_1, 2007, 1062121, "  "));
        store.insert(createDokument(id_1062121_200_2007_4, 2007, 1062121, "  "));
        store.insert(createDokument(id_1062140_200_2007_1, 2007, 1062140, "  "));
        store.insert(createDokument(id_1062140_200_2007_2, 2007, 1062140, "  "));
        store.insert(createDokument(id_1062121_200_2007_3, 2007, 1062121, "  "));
        store.insert(createDokument(id_301173_58_1964_1, 1964, 301173, "  "));
        store.insert(createDokument(id_873_200_2009_1, 2009, 873, "  "));
        store.insert(createDokument(id_873_200_2009_2, 2009, 873, "  "));
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

    public DokumentId<?> getId_404680_200_2007_1() {
        return id_404680_200_2007_1;
    }

    public DokumentId<?> getId_283560_200_2010_1() {
        return id_283560_200_2010_1;
    }

    public DokumentId<?> getId_2567_56_1995_1() {
        return id_2567_56_1995_1;
    }

    public DokumentId<?> getId_2782_56_1986_1() {
        return id_2782_56_1986_1;
    }

    public DokumentId<?> getId_301173_58_1964_1() {
        return id_301173_58_1964_1;
    }

    public DokumentId<?> getId_1062140_200_2007_2() {
        return id_1062140_200_2007_2;
    }

    public DokumentId<?> getId_1062121_200_2007_1() {
        return id_1062121_200_2007_1;
    }

    public DokumentId<?> getId_1062140_200_2007_1() {
        return id_1062140_200_2007_1;
    }

    public DokumentId<?> getId_1062121_200_2007_3() {
        return id_1062121_200_2007_3;
    }

    public DokumentId<?> getId_1062121_200_2007_4() {
        return id_1062121_200_2007_4;
    }

    public DokumentId<?> getId_873_200_2009_1() {
        return id_873_200_2009_1;
    }

    public DokumentId<?> getId_873_200_2009_2() {
        return id_873_200_2009_2;
    }
}