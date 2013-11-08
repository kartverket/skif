package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.AbstractMapper;
import no.statkart.skif.mapper.DefaultTypeMapper;
import no.statkart.skif.store.InverseRelation;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.storetest.domain.basic.*;
import no.statkart.skif.storetest.domain.component.composite.BubbleWithCompositeComponentId;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityComponentId;
import no.statkart.skif.storetest.domain.component.entity.BubbleWithEntityInCompositeComponentId;
import no.statkart.skif.storetest.domain.component.historikk.HistorikkBubbleWithEntityComponentsId;
import no.statkart.skif.storetest.domain.component.historikk.HistorikkBubbleWithListEntityComponentsId;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.domain.endringslogg.BubbleWithRelationEndring;
import no.statkart.skif.storetest.domain.endringslogg.SimpleEndring;
import no.statkart.skif.storetest.domain.endringslogg.SubTypedBubbleEndring;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteString;
import no.statkart.skif.storetest.domain.koder.HistorikkEnumKode;
import no.statkart.skif.storetest.domain.koder.HistorikkEnumKodeId;
import no.statkart.skif.storetest.domain.koder.SimpleEnumKode;
import no.statkart.skif.storetest.domain.koder.SimpleEnumKodeId;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2AAWithEntityComponentId;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2BBOneId;
import no.statkart.skif.storetest.domain.relation.uni.component.entity.X2CCManyId;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1AAId;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1BBOneId;
import no.statkart.skif.storetest.domain.relation.uni.direct.X1CCManyId;

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
        dtm.addPackageMapping("no.statkart.skif.storetest.wsapi.domain.component","no.statkart.skif.storetest.domain.component");
        dtm.addPackageMapping("no.statkart.skif.storetest.wsapi.domain.relation","no.statkart.skif.storetest.domain.relation");
        dtm.addPackageMapping("no.statkart.skif.storetest.wsapi.domain.endringslogg","no.statkart.skif.storetest.domain.endringslogg");
        dtm.addPackageMapping("no.statkart.skif.storetest.wsapi.domain", "no.statkart.skif.store");
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
        addMapper(new LocalizedStringTypeMapper());
        addMapper(new EndringsklasseTypeMapper());
        addMapper(new DomeneklasseTypeMapper());
        addMapper(new EndringstypeTypeMapper());

//        addMapper(EndringTypeMapper.create((no.statkart.skif.storetest.wsapi.endringslogg.Endring.class, Endring.class));
        addMapper(EndringTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.endringslogg.SimpleEndring.class, SimpleEndring.class));
        addMapper(EndringTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.endringslogg.SubTypedBubbleEndring.class, SubTypedBubbleEndring.class));
        addMapper(EndringTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.endringslogg.BubbleWithRelationEndring.class, BubbleWithRelationEndring.class));
        addMapper(new EndringIdTypeMapper());

        // Alle Id'er
        // basic
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.basic.SimpleId.class, SimpleId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.basic.BubbleWithFilterId.class, BubbleWithFilterId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.basic.BubbleWithKodeId.class, BubbleWithKodeId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.basic.BubbleWithRelationId.class, BubbleWithRelationId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.basic.BubbleWithValueObjectId.class, BubbleWithValueObjectId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.basic.HistSimpleId.class, HistSimpleId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.basic.HistWithRelationId.class, HistWithRelationId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.basic.GeometricElementId.class, GeometricElementId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.basic.SubTypedBubbleId.class, SubTypedBubbleId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.basic.SubTypeWithCollectionId.class, SubTypeWithCollectionId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.basic.SubTypeWithPrimitiveId.class, SubTypeWithPrimitiveId.class));

        // composite
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.component.composite.BubbleWithCompositeComponentId.class, BubbleWithCompositeComponentId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.component.entity.BubbleWithEntityComponentId.class, BubbleWithEntityComponentId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.component.entity.BubbleWithEntityInCompositeComponentId.class, BubbleWithEntityInCompositeComponentId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.component.historikk.HistorikkBubbleWithListEntityComponentsId.class, HistorikkBubbleWithListEntityComponentsId.class));

        // Relation
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.relation.uni.direct.X1AAId.class, X1AAId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.relation.uni.direct.X1BBOneId.class, X1BBOneId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.relation.uni.direct.X1CCManyId.class, X1CCManyId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.relation.uni.component.entity.X2AAWithEntityComponentId.class, X2AAWithEntityComponentId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.relation.uni.component.entity.X2BBOneId.class, X2BBOneId.class));
        addMapper(StoreTestBubbleIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.relation.uni.component.entity.X2CCManyId.class, X2CCManyId.class));




        // Koder
        addMapper(new StoreTestBubbleIdTypeMapper<no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId, no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId>(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId.class, no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId.class));
        addMapper(new StoreTestBubbleIdTypeMapper<no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteStringId, no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteStringId>(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteStringId.class, no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteStringId.class));
        addMapper(new KodelisteTypeMapper<no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLong, StoreTestKodelisteLong>("wsapi", no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLong.class, StoreTestKodelisteLong.class));
        addMapper(new KodelisteTypeMapper<no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteString, StoreTestKodelisteString>("wsapi", no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteString.class, StoreTestKodelisteString.class));

        // Alle InverseRelations
        addMapper(InverseRelationTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdListInverseRelation.class, InverseRelation.class));
        addMapper(InverseRelationTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.relation.uni.direct.X1AAIdListInverseRelation.class, InverseRelation.class));
        addMapper(InverseRelationTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.relation.uni.direct.X1AAIdInverseRelation.class, InverseRelation.class));

        // Non boble objekter

        // Non boble Lister

        // KodeId
        addMapper(KodeIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKodeId.class, AEnumKodeId.class));
        addMapper(KodeIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBEnumKodeId.class, BEnumKodeId.class));
        addMapper(KodeIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestSEnumKodeId.class, SEnumKodeId.class));
        addMapper(KodeIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestADbKodeId.class, ADbKodeId.class));
        addMapper(KodeIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBDbKodeId.class, BDbKodeId.class));
        addMapper(KodeIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC1DbKodeId.class, C1DbKodeId.class));
        addMapper(KodeIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC2DbKodeId.class, C2DbKodeId.class));
        addMapper(KodeIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestXStrDbKodeId.class, XStrDbKodeId.class));
        addMapper(KodeIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.koder.SimpleEnumKodeId.class, SimpleEnumKodeId.class));
        addMapper(KodeIdTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.koder.HistorikkEnumKodeId.class, HistorikkEnumKodeId.class));

        // Kode
        addMapper(KodeTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKode.class, AEnumKode.class));
        addMapper(KodeTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBEnumKode.class, BEnumKode.class));
        addMapper(KodeTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestSEnumKode.class, SEnumKode.class));
        addMapper(KodeTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestADbKode.class, ADbKode.class));
        addMapper(KodeTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBDbKode.class, BDbKode.class));
        addMapper(KodeTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC1DbKode.class, C1DbKode.class));
        addMapper(KodeTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC2DbKode.class, C2DbKode.class));
        addMapper(KodeTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestXStrDbKode.class, XStrDbKode.class));
        addMapper(KodeTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.koder.SimpleEnumKode.class, SimpleEnumKode.class));
        addMapper(KodeTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.koder.HistorikkEnumKode.class, HistorikkEnumKode.class));

        addMapper(new KodelisteTransferTypeMapper<no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer, KodelisteTransfer>(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer.class, KodelisteTransfer.class));
    }
}
