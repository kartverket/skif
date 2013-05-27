package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.*;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.domain.kodeliste.*;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestMapper extends AbstractMapper<StoreTestMapping> {

    public StoreTestMapper() {
        super(StoreTestMapping.class);

        // DefaultTypeMapper. Brukes for objekter som har samme properties i domenene
        DefaultTypeMapper dtm = new DefaultTypeMapper();
        dtm.addPackageMapping("no.statkart.skif.storetest.wsapi.domain.demo","no.statkart.skif.storetest.domain.demo");
        dtm.addPackageMapping("no.statkart.skif.storetest.wsapi.domain", "no.statkart.skif.mockup");
        setDefaultMapper(dtm);


        // Klasser hvor objekter skal mappes til seg selv
        useIdentityMapping(String.class);
        useIdentityMapping(Integer.class);
        useIdentityMapping(Long.class);
        useIdentityMapping(Boolean.class);

        // Timestamp
        addMapper(new TimestampTypeMapper());

        // SnapshotVersion
        addMapper(new SnapshotVersionTypeMapper());

        //SelectionPolygon
        addMapper(new SelectionPolygonTypeMapper());

        // Alle Id'er
        addMapper(new StoreTestBubbleIdTypeMapper<no.statkart.skif.storetest.wsapi.domain.demo.TestBubbleId, no.statkart.skif.storetest.domain.demo.TestBubbleId>(no.statkart.skif.storetest.wsapi.domain.demo.TestBubbleId.class, no.statkart.skif.storetest.domain.demo.TestBubbleId.class));
        addMapper(new StoreTestBubbleIdTypeMapper<no.statkart.skif.storetest.wsapi.domain.demo.BarId, no.statkart.skif.storetest.domain.demo.BarId>(no.statkart.skif.storetest.wsapi.domain.demo.BarId.class, no.statkart.skif.storetest.domain.demo.BarId.class));
        addMapper(new StoreTestBubbleIdTypeMapper<no.statkart.skif.storetest.wsapi.domain.demo.FooId, no.statkart.skif.storetest.domain.demo.FooId>(no.statkart.skif.storetest.wsapi.domain.demo.FooId.class, no.statkart.skif.storetest.domain.demo.FooId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.BarFoosId.class, no.statkart.skif.storetest.domain.demo.BarFoosId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.BazId.class, no.statkart.skif.storetest.domain.demo.BazId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.RazId.class, no.statkart.skif.storetest.domain.demo.RazId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.GeometricElementId.class, no.statkart.skif.storetest.domain.demo.GeometricElementId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.ChildBubbleId.class, no.statkart.skif.storetest.domain.demo.ChildBubbleId.class));

        addMapper(new StoreTestBubbleIdTypeMapper<no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId, no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId>(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId.class, no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId.class));
        addMapper(new StoreTestBubbleIdTypeMapper<no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteStringId, no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteStringId>(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteStringId.class, no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteStringId.class));
        addMapper(new KodelisteTypeMapper<no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLong, StoreTestKodelisteLong>("wsapi", no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLong.class, StoreTestKodelisteLong.class));
        addMapper(new KodelisteTypeMapper<no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteString, StoreTestKodelisteString>("wsapi", no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteString.class, StoreTestKodelisteString.class));

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

        // Kode
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKode.class, AEnumKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBEnumKode.class, BEnumKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestSEnumKode.class, SEnumKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestADbKode.class, ADbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBDbKode.class, BDbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC1DbKode.class, C1DbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC2DbKode.class, C2DbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestXStrDbKode.class, XStrDbKode.class));

        addMapper(new KodelisteTransferTypeMapper<no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer, KodelisteTransfer>(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer.class, KodelisteTransfer.class));
    }
}
