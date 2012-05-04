package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author rorchr
 */
public class RettstypeKodeId extends StoreTestDbKodeId<RettstypeKode> {
    
    private static StoreTestDbKodeSupport<RettstypeKodeId> kodeSupport = new StoreTestDbKodeSupport<RettstypeKodeId>(RettstypeKodeId.class, 2L);
    public static StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    
    public static RettstypeKodeId AF = define(1);
    public static RettstypeKodeId DI = define(2);
    public static RettstypeKodeId EI = define(3);
    public static RettstypeKodeId EN = define(4);
    public static RettstypeKodeId ET = define(5);
    public static RettstypeKodeId FA = define(6);
    public static RettstypeKodeId FB = define(7);
    public static RettstypeKodeId FE = define(8);
    public static RettstypeKodeId FL = define(9);
    public static RettstypeKodeId FN = define(10);
    public static RettstypeKodeId FO = define(11);
    public static RettstypeKodeId FR = define(12);
    public static RettstypeKodeId FS = define(13);
    public static RettstypeKodeId HI = define(14);
    public static RettstypeKodeId HJ = define(15);
    public static RettstypeKodeId HL = define(16);
    public static RettstypeKodeId ID = define(17);
    public static RettstypeKodeId JE = define(18);
    public static RettstypeKodeId JO = define(19);
    public static RettstypeKodeId JS = define(20);
    public static RettstypeKodeId KA = define(21);
    public static RettstypeKodeId KL = define(22);
    public static RettstypeKodeId NE = define(23);
    public static RettstypeKodeId OB = define(24);
    public static RettstypeKodeId OM = define(25);
    public static RettstypeKodeId PA = define(26);
    public static RettstypeKodeId PB = define(27);
    public static RettstypeKodeId PE = define(28);
    public static RettstypeKodeId PI = define(29);
    public static RettstypeKodeId PR = define(30);
    public static RettstypeKodeId PU = define(31);
    public static RettstypeKodeId RE = define(32);
    public static RettstypeKodeId SA = define(33);
    public static RettstypeKodeId SB = define(34);
    public static RettstypeKodeId SE = define(35);
    public static RettstypeKodeId SF = define(36);
    public static RettstypeKodeId SH = define(37);
    public static RettstypeKodeId SI = define(38);
    public static RettstypeKodeId SL = define(39);
    public static RettstypeKodeId SO = define(40);
    public static RettstypeKodeId SP = define(41);
    public static RettstypeKodeId SR = define(42);
    public static RettstypeKodeId SS = define(43);
    public static RettstypeKodeId ST = define(44);
    public static RettstypeKodeId TE = define(45);
    public static RettstypeKodeId TF = define(46);
    public static RettstypeKodeId TI = define(47);
    public static RettstypeKodeId TL = define(48);
    public static RettstypeKodeId TN = define(49);
    public static RettstypeKodeId TP = define(50);
    public static RettstypeKodeId TR = define(51);
    public static RettstypeKodeId TV = define(52);
    public static RettstypeKodeId VF = define(53);
    public static RettstypeKodeId VL = define(54);
    public static RettstypeKodeId VN = define(55);
    public static RettstypeKodeId SD = define(56);

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
