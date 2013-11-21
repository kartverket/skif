package no.statkart.skif.storetest.domain.koder;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * Id-klasse for {@link SimpleEnumKode}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class SimpleEnumKodeId extends StoreTestEnumKodeId<SimpleEnumKode> {
    private static final long serialVersionUID = 1L;

    private static final StoreTestEnumKodeSupport<SimpleEnumKode, SimpleEnumKodeId> kodeSupport = new StoreTestEnumKodeSupport<SimpleEnumKode, SimpleEnumKodeId>(SimpleEnumKodeId.class, 11, "no.statkart.skif.storetest.domain.koder.KodeMsg");

    public static final StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static final SimpleEnumKodeId IkkeOppgittId = define(0, "SimpleEnumKode.IkkeOppgitt");
    public static final SimpleEnumKodeId KodeAId = define(1, "SimpleEnumKode.KodeA");
    public static final SimpleEnumKodeId KodeBId = define(2, "SimpleEnumKode.KodeB");

    private static SimpleEnumKodeId define(long idValue, String navn) {
        SimpleEnumKode simpleEnumKode = kodeSupport.defineKode(idValue, navn);
        simpleEnumKode.setKodeverdi("-");
        return simpleEnumKode.getId();
    }

    public SimpleEnumKodeId(Long value, SnapshotVersion snapshotVersion) {
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
