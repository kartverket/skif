package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author rorchr
 */
public class RettstypeKodeId extends StoreTestDbKodeId<RettstypeKode> {

    private static final StoreTestDbKodeSupport<RettstypeKodeId> kodeSupport = new StoreTestDbKodeSupport<RettstypeKodeId>(RettstypeKodeId.class, 2L);
    public static final StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();

    public static final RettstypeKodeId AF = define(1);
    public static final RettstypeKodeId DI = define(2);
    public static final RettstypeKodeId EI = define(3);
    public static final RettstypeKodeId EN = define(4);
    public static final RettstypeKodeId ET = define(5);
    public static final RettstypeKodeId FA = define(6);
    public static final RettstypeKodeId FB = define(7);
    public static final RettstypeKodeId FE = define(8);
    public static final RettstypeKodeId FL = define(9);
    public static final RettstypeKodeId FN = define(10);
    public static final RettstypeKodeId FO = define(11);
    public static final RettstypeKodeId FR = define(12);
    public static final RettstypeKodeId FS = define(13);
    public static final RettstypeKodeId HI = define(14);
    public static final RettstypeKodeId HJ = define(15);
    public static final RettstypeKodeId HL = define(16);
    public static final RettstypeKodeId ID = define(17);
    public static final RettstypeKodeId JE = define(18);
    public static final RettstypeKodeId JO = define(19);
    public static final RettstypeKodeId JS = define(20);
    public static final RettstypeKodeId KA = define(21);
    public static final RettstypeKodeId KL = define(22);
    public static final RettstypeKodeId NE = define(23);
    public static final RettstypeKodeId OB = define(24);
    public static final RettstypeKodeId OM = define(25);
    public static final RettstypeKodeId PA = define(26);
    public static final RettstypeKodeId PB = define(27);
    public static final RettstypeKodeId PE = define(28);
    public static final RettstypeKodeId PI = define(29);
    public static final RettstypeKodeId PR = define(30);
    public static final RettstypeKodeId PU = define(31);
    public static final RettstypeKodeId RE = define(32);
    public static final RettstypeKodeId SA = define(33);
    public static final RettstypeKodeId SB = define(34);
    public static final RettstypeKodeId SE = define(35);
    public static final RettstypeKodeId SF = define(36);
    public static final RettstypeKodeId SH = define(37);
    public static final RettstypeKodeId SI = define(38);
    public static final RettstypeKodeId SL = define(39);
    public static final RettstypeKodeId SO = define(40);
    public static final RettstypeKodeId SP = define(41);
    public static final RettstypeKodeId SR = define(42);
    public static final RettstypeKodeId SS = define(43);
    public static final RettstypeKodeId ST = define(44);
    public static final RettstypeKodeId TE = define(45);
    public static final RettstypeKodeId TF = define(46);
    public static final RettstypeKodeId TI = define(47);
    public static final RettstypeKodeId TL = define(48);
    public static final RettstypeKodeId TN = define(49);
    public static final RettstypeKodeId TP = define(50);
    public static final RettstypeKodeId TR = define(51);
    public static final RettstypeKodeId TV = define(52);
    public static final RettstypeKodeId VF = define(53);
    public static final RettstypeKodeId VL = define(54);
    public static final RettstypeKodeId VN = define(55);
    public static final RettstypeKodeId SD = define(56);

    public Long getValue() {
        return (Long) super.getValue();
    }

    public RettstypeKodeId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    @Override
    public StoreTestKodelisteLongId<?> getKodelisteId() {
        return KODELISTE_ID.asSnapshotVersion(this);
    }

    protected static RettstypeKodeId define(long idValue) {
        return kodeSupport.defineId(idValue);
    }

}
