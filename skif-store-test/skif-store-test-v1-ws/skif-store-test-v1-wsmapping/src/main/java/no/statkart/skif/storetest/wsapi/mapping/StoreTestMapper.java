package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.*;
import no.statkart.skif.mapping.InverseRelationTypeMapperFactory;
import no.statkart.skif.mapping.BubbleIdTypeMapperFactory;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestMapper extends AbstractMapper<StoreTestMapping> {

    public StoreTestMapper() {
        super(StoreTestMapping.class);

        MappingResolver mappingResolver = new MappingResolver();
        mappingResolver.addPackageMapping("no.statkart.skif.storetest.wsapi.domain.basic", "no.statkart.skif.storetest.domain.basic");
        mappingResolver.addPackageMapping("no.statkart.skif.storetest.wsapi.domain.demo", "no.statkart.skif.storetest.domain.demo");
        mappingResolver.addPackageMapping("no.statkart.skif.storetest.wsapi.domain", "no.statkart.skif.mockup");

        MappingOverrideBuilder builder = new MappingOverrideBuilder();
        // Disse klassene har av en eller annen grunn ikke samme navn i wsapi
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKodeId.class, AEnumKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBEnumKodeId.class, BEnumKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestSEnumKodeId.class, SEnumKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestADbKodeId.class, ADbKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBDbKodeId.class, BDbKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC1DbKodeId.class, C1DbKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC2DbKodeId.class, C2DbKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestXStrDbKodeId.class, XStrDbKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId.class, StoreTestKodelisteLongId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKode.class, AEnumKode.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBEnumKode.class, BEnumKode.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestSEnumKode.class, SEnumKode.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestADbKode.class, ADbKode.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBDbKode.class, BDbKode.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC1DbKode.class, C1DbKode.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC2DbKode.class, C2DbKode.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestXStrDbKode.class, XStrDbKode.class);
        mappingResolver.overrideClassMappings(builder.build());

        setMappingResolver(mappingResolver);

        // Klasser hvor objekter skal mappes til seg selv
        addMapperFactory(new IdentityTypeMapperFactory().useIdentityMappingForBasicTypes());

        addMapperFactory(new BubbleIdTypeMapperFactory(no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId.class));
        addMapperFactory(new KodeTypeMapperFactory());

        addMapperFactory(new InverseRelationTypeMapperFactory());

        addMapperFactory(new CollectionMapperFactory());

        addMapperFactory(new DefaultTypeMapperFactory());

        // Basic domain types
        addMapper(new TimestampTypeMapper());
        addMapper(new SnapshotVersionTypeMapper());
        addMapper(new SelectionPolygonTypeMapper());
        addMapper(new LocaleMapper());
        addMapper(new LocalizedStringTypeMapper());
        addMapper(new ClassTypeMapper());

        addMapper(new KodelisteTransferTypeMapper<no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer, KodelisteTransfer>(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer.class, KodelisteTransfer.class));
    }
}
