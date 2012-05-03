package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestEnumKodeSupport;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author rorchr
 */
public class RettstypeKodeId extends StoreTestEnumKodeId<RettstypeKode> {
    private static StoreTestEnumKodeSupport<RettstypeKode, RettstypeKodeId> kodeSupport = new StoreTestEnumKodeSupport<RettstypeKode, RettstypeKodeId>(RettstypeKodeId.class, 2, "RettstypeKode");

    public static StoreTestKodelisteLongId<?> KODELISTE_ID = kodeSupport.getKodelisteId();
    public static RettstypeKodeId AF = define(1, "AF", "AF", "Arealoverføring");
    public static RettstypeKodeId DI = define(2, "DI", "DI", "Diverse påtegning");
    public static RettstypeKodeId EI = define(3, "EI", "EI", "Eiendomsforvalter");
    public static RettstypeKodeId EN = define(4, "EN", "EN", "Påtegning på andel");
    public static RettstypeKodeId ET = define(5, "ET", "ET", "Tvangspåt. på ID");
    public static RettstypeKodeId FA = define(6, "FA", "FA", "Fellesareal");
    public static RettstypeKodeId FB = define(7, "FB", "FB", "Overføring fra tidligere festenummer");
    public static RettstypeKodeId FE = define(8, "FE", "FE", "Festekontrakt");
    public static RettstypeKodeId FL = define(9, "FL", "FL", "Fremleieavtale");
    public static RettstypeKodeId FN = define(10, "FN", "FN", "Fellesareal - endring");
    public static RettstypeKodeId FO = define(11, "FO", "FO", "Forhøyelse obligasjon");
    public static RettstypeKodeId FR = define(12, "FR", "FR", "Fradeling");
    public static RettstypeKodeId FS = define(13, "FS", "FS", "Opphør av fellesareal");
    public static RettstypeKodeId HI = define(14, "HI", "HI", "Historisk eiendom");
    public static RettstypeKodeId HJ = define(15, "HJ", "HJ", "Hjemmelsovergang");
    public static RettstypeKodeId HL = define(16, "HL", "HL", "Heftelse i rettighet");
    public static RettstypeKodeId ID = define(17, "ID", "ID", "ID endring");
    public static RettstypeKodeId JE = define(18, "JE", "JE", "Endring av jordsameie");
    public static RettstypeKodeId JO = define(19, "JO", "JO", "Opphør av jordsameie");
    public static RettstypeKodeId JS = define(20, "JS", "JS", "Jordsameie");
    public static RettstypeKodeId KA = define(21, "KA", "KA", "Kartforretning");
    public static RettstypeKodeId KL = define(22, "KL", "KL", "Registrering anke");
    public static RettstypeKodeId NE = define(23, "NE", "NE", "Nedkvittering");
    public static RettstypeKodeId OB = define(24, "OB", "OB", "Obligasjon");
    public static RettstypeKodeId OM = define(25, "OM", "OM", "Ommatrikulering");
    public static RettstypeKodeId PA = define(26, "PA", "PA", "Pantefrafall");
    public static RettstypeKodeId PB = define(27, "PB", "PB", "Prioritet ikke tinglyst dokument");
    public static RettstypeKodeId PE = define(28, "PE", "PE", "Leieavtale");
    public static RettstypeKodeId PI = define(29, "PI", "PI", "Pantefrafall ikke tinglyst eiendom");
    public static RettstypeKodeId PR = define(30, "PR", "PR", "Prioritet for dokumentnummer");
    public static RettstypeKodeId PU = define(31, "PU", "PU", "Pantutvidelse");
    public static RettstypeKodeId RE = define(32, "RE", "RE", "Rettighet");
    public static RettstypeKodeId SA = define(33, "SA", "SA", "Sammenføyning");
    public static RettstypeKodeId SB = define(34, "SB", "SB", "Endring av sameiebrøk");
    public static RettstypeKodeId SE = define(35, "SE", "SE", "Seksjonering");
    public static RettstypeKodeId SF = define(36, "SF", "SF", "Fjerning av seksjoner");
    public static RettstypeKodeId SH = define(37, "SH", "SH", "Oppheving av seksjoner");
    public static RettstypeKodeId SI = define(38, "SI", "SI", "Sletting av eiendomsforvalter");
    public static RettstypeKodeId SL = define(39, "SL", "SL", "Sletting");
    public static RettstypeKodeId SO = define(40, "SO", "SO", "Oppdeling av seksjoner");
    public static RettstypeKodeId SP = define(41, "SP", "SP", "Servitutt pengeheftelse");
    public static RettstypeKodeId SR = define(42, "SR", "SR", "Servitutt");
    public static RettstypeKodeId SS = define(43, "SS", "SS", "Sammenslåing av seksjoner");
    public static RettstypeKodeId ST = define(44, "ST", "ST", "Tilleggsseksjonering");
    public static RettstypeKodeId TE = define(45, "TE", "TE", "Transport av rettighet");
    public static RettstypeKodeId TF = define(46, "TF", "TF", "Transport av feste");
    public static RettstypeKodeId TI = define(47, "TI", "TI", "Uregistrert grunn");
    public static RettstypeKodeId TL = define(48, "TL", "TL", "Tvangsforretning i rettighet");
    public static RettstypeKodeId TN = define(49, "TN", "TN", "Tinglysing på ny");
    public static RettstypeKodeId TP = define(50, "TP", "TP", "Transport av leie");
    public static RettstypeKodeId TR = define(51, "TR", "TR", "Transport av panthaver");
    public static RettstypeKodeId TV = define(52, "TV", "TV", "Tvangsforretning");
    public static RettstypeKodeId VF = define(53, "VF", "VF", "Vilkår i feste");
    public static RettstypeKodeId VL = define(54, "VL", "VL", "Nye vilkår i leie");
    public static RettstypeKodeId VN = define(55, "VN", "VN", "Nye vilkår i festeavtale");
    public static RettstypeKodeId SD = define(56, "SD", "SD", "Reseksjonering");

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

    protected static RettstypeKodeId define(long idValue, String kodeResourceKey, String ident, String kodeVerdi) {
        RettstypeKode rettstypeKode = kodeSupport.defineKode(idValue, kodeResourceKey);
        rettstypeKode.setIdent(ident);
        rettstypeKode.setKodeverdi(kodeVerdi);
        return rettstypeKode.getId();
    }

}
