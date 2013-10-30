package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.*;
import no.statkart.skif.store.InverseRelation;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.storetest.domain.basic.GeometricElementId;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.kodeliste.*;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.basic.*;
import no.statkart.skif.storetest.domain.koder.HistorikkEnumKode;
import no.statkart.skif.storetest.domain.koder.HistorikkEnumKodeId;
import no.statkart.skif.storetest.domain.koder.SimpleEnumKode;
import no.statkart.skif.storetest.domain.koder.SimpleEnumKodeId;
import no.statkart.skif.storetest.wsapi.domain.basic.Endringsklasse;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestMapper extends AbstractMapper<StoreTestMapping> {

    public StoreTestMapper() {
        super(StoreTestMapping.class);

        // DefaultTypeMapper. Brukes for objekter som har samme properties i domenene
        DefaultTypeMapper dtm = new DefaultTypeMapper();
        dtm.addPackageMapping("no.statkart.skif.storetest.wsapi.domain.basic","no.statkart.skif.storetest.domain.basic");
        dtm.addPackageMapping("no.statkart.skif.storetest.wsapi.domain", "no.statkart.skif.mockup");
        setDefaultMapper(dtm);


        // Klasser hvor objekter skal mappes til seg selv
        useIdentityMapping(String.class);
        useIdentityMapping(Integer.class);
        useIdentityMapping(Long.class);
        useIdentityMapping(Boolean.class);

        // Basic domain types
        addMapper(new TimestampTypeMapper());
        addMapper(new SnapshotVersionTypeMapper());
        addMapper(new SelectionPolygonTypeMapper());
        addMapper(new LocaleMapper());
        addMapper(new EndringsklasseTypeMapper());
        addMapper(new DomeneklasseTypeMapper());

        // Alle Id'er
        addMapper(new StoreTestBubbleIdTypeMapper<no.statkart.skif.storetest.wsapi.domain.basic.SimpleId, SimpleId>(no.statkart.skif.storetest.wsapi.domain.basic.SimpleId.class, SimpleId.class));
        addMapper(new StoreTestBubbleIdTypeMapper<no.statkart.skif.storetest.wsapi.domain.basic.BubbleWithFilterId, BubbleWithFilterId>(no.statkart.skif.storetest.wsapi.domain.basic.BubbleWithFilterId.class, BubbleWithFilterId.class));
        addMapper(new StoreTestBubbleIdTypeMapper<no.statkart.skif.storetest.wsapi.domain.basic.BubbleWithKodeId, BubbleWithKodeId>(no.statkart.skif.storetest.wsapi.domain.basic.BubbleWithKodeId.class, BubbleWithKodeId.class));
        addMapper(new StoreTestBubbleIdTypeMapper<no.statkart.skif.storetest.wsapi.domain.basic.BubbleWithRelationId, BubbleWithRelationId>(no.statkart.skif.storetest.wsapi.domain.basic.BubbleWithRelationId.class, BubbleWithRelationId.class));
        addMapper(new StoreTestBubbleIdTypeMapper<no.statkart.skif.storetest.wsapi.domain.basic.HistSimpleId, HistSimpleId>(no.statkart.skif.storetest.wsapi.domain.basic.HistSimpleId.class, HistSimpleId.class));
        addMapper(new StoreTestBubbleIdTypeMapper<no.statkart.skif.storetest.wsapi.domain.basic.HistWithRelationId, HistWithRelationId>(no.statkart.skif.storetest.wsapi.domain.basic.HistWithRelationId.class, HistWithRelationId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.basic.GeometricElementId.class, GeometricElementId.class));

        addMapper(new StoreTestBubbleIdTypeMapper<no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId, no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId>(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId.class, no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId.class));
        addMapper(new StoreTestBubbleIdTypeMapper<no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteStringId, no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteStringId>(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteStringId.class, no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteStringId.class));
        addMapper(new KodelisteTypeMapper<no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLong, StoreTestKodelisteLong>("wsapi", no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLong.class, StoreTestKodelisteLong.class));
        addMapper(new KodelisteTypeMapper<no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteString, StoreTestKodelisteString>("wsapi", no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteString.class, StoreTestKodelisteString.class));

        // Alle InverseRelations
        addMapper(new InverseRelationTypeMapper<no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdListInverseRelation, InverseRelation>(no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdListInverseRelation.class, InverseRelation.class));

        // Non boble objekter

        // Non boble Lister

        // KodeId
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKodeId.class, AEnumKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBEnumKodeId.class, BEnumKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestSEnumKodeId.class, SEnumKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestADbKodeId.class, ADbKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBDbKodeId.class, BDbKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC1DbKodeId.class, C1DbKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC2DbKodeId.class, C2DbKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestXStrDbKodeId.class, XStrDbKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.koder.SimpleEnumKodeId.class, SimpleEnumKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.koder.HistorikkEnumKodeId.class, HistorikkEnumKodeId.class));

        // Kode
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKode.class, AEnumKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBEnumKode.class, BEnumKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestSEnumKode.class, SEnumKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestADbKode.class, ADbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBDbKode.class, BDbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC1DbKode.class, C1DbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC2DbKode.class, C2DbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestXStrDbKode.class, XStrDbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.koder.SimpleEnumKode.class, SimpleEnumKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.koder.HistorikkEnumKode.class, HistorikkEnumKode.class));

        addMapper(new KodelisteTransferTypeMapper<no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer, KodelisteTransfer>(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer.class, KodelisteTransfer.class));
    }
}
