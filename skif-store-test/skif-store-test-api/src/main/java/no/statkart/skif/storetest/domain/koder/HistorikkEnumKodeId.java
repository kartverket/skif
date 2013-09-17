package no.statkart.skif.storetest.domain.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * Id-klasse for {@link HistorikkEnumKode}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class HistorikkEnumKodeId extends StoreTestEnumKodeId<HistorikkEnumKode> {
    private static final long serialVersionUID = 1L;

    private static final StoreTestEnumKodeSupport<HistorikkEnumKode, HistorikkEnumKodeId> kodeSupport = new StoreTestEnumKodeSupport<HistorikkEnumKode, HistorikkEnumKodeId>(HistorikkEnumKodeId.class, 12, "no.statkart.skif.storetest.domain.koder.KodeMsg");

    public static final StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static final HistorikkEnumKodeId Kode1Id = define(1, "Kode1", SnapshotVersion.START, SnapshotVersion.CURRENT);
    public static final HistorikkEnumKodeId Kode2Id = define(2, "Kode2", SnapshotVersion.START, SnapshotVersion.createInstance("2013-09-10 00:00:00.0"));
    public static final HistorikkEnumKodeId Kode3Id = define(3, "Kode3", SnapshotVersion.createInstance("2012-09-10 00:00:00.0"), SnapshotVersion.CURRENT);

    /**
     * @param idValue    Kodens idverdi
     * @param navn       Kodens tekniske navn
     * @param innfort    Når denne kodeverdien oppstod
     * @param utgatt     Når denne kodeverdien ble tatt ut av bruk (ingen sanntidsobjekter refererer til den lenger)
     * @return id for den nye koden
     */
    private static HistorikkEnumKodeId define(long idValue, String navn, SnapshotVersion innfort, SnapshotVersion utgatt) {
        HistorikkEnumKode historikkEnumKode = kodeSupport.defineKode(idValue, navn);
        historikkEnumKode.setOppdateringsdato(innfort.getTimestamp());
        historikkEnumKode.setSluttdato(utgatt.getTimestamp());
        return historikkEnumKode.getId();
    }

    public HistorikkEnumKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public KodelisteId<?> getKodelisteId() {
        return KODELISTE_ID;
    }

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }
}
