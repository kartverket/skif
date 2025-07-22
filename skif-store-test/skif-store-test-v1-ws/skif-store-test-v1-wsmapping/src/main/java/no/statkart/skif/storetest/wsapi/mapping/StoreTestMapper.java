package no.statkart.skif.storetest.wsapi.mapping;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.mapper.AbstractMapper;
import no.statkart.skif.mapper.ClassTypeMapper;
import no.statkart.skif.mapper.CollectionMapperFactory;
import no.statkart.skif.mapper.DefaultTypeMapperFactory;
import no.statkart.skif.mapper.IdentityTypeMapperFactory;
import no.statkart.skif.mapper.MappingOverrideBuilder;
import no.statkart.skif.mapper.MappingResolver;
import no.statkart.skif.mapper.SnapshotVersionTypeMapper;
import no.statkart.skif.mapper.TimestampTypeMapper;
import no.statkart.skif.mapping.BubbleIdTypeMapperFactory;
import no.statkart.skif.mapping.BubbleTransferTypeMapperFactory;
import no.statkart.skif.mapping.InverseRelationTypeMapperFactory;
import no.statkart.skif.mapping.SnapshotBubbleIdTypeMapperFactory;
import no.statkart.skif.mapping.TransferTypeMapperFactory;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.koder.ADbKode;
import no.statkart.skif.storetest.domain.demo.koder.ADbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKode;
import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.BDbKode;
import no.statkart.skif.storetest.domain.demo.koder.BDbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.BEnumKode;
import no.statkart.skif.storetest.domain.demo.koder.BEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.C1DbKode;
import no.statkart.skif.storetest.domain.demo.koder.C1DbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.C2DbKode;
import no.statkart.skif.storetest.domain.demo.koder.C2DbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.CDbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.SEnumKode;
import no.statkart.skif.storetest.domain.demo.koder.SEnumKodeId;
import no.statkart.skif.storetest.domain.demo.koder.XStrDbKode;
import no.statkart.skif.storetest.domain.demo.koder.XStrDbKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLong;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteString;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteStringId;
import no.statkart.skif.storetest.domain.koder.HistorikkEnumKode;

import java.sql.Timestamp;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class StoreTestMapper extends AbstractMapper<StoreTestMapping> {

    @Inject
    public StoreTestMapper(Provider<SnapshotVersion> snapshotVersionProvider) {
        super(StoreTestMapping.class);

        MappingResolver mappingResolver = new MappingResolver();
        mappingResolver.addPackageMapping("no.statkart.skif.storetest.wsapi.domain", "no.statkart.skif.storetest.domain");
        mappingResolver.addPackageMapping("no.statkart.skif.storetest.wsapi.domain", "no.statkart.skif.mockup");

        MappingOverrideBuilder builder = new MappingOverrideBuilder();
        // Disse klassene har av en eller annen grunn ikke samme navn i wsapi
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKodeId.class, AEnumKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBEnumKodeId.class, BEnumKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestSEnumKodeId.class, SEnumKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestADbKodeId.class, ADbKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBDbKodeId.class, BDbKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestCDbKodeId.class, CDbKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC1DbKodeId.class, C1DbKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC2DbKodeId.class, C2DbKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestXStrDbKodeId.class, XStrDbKodeId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId.class, StoreTestKodelisteLongId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLong.class, StoreTestKodelisteLong.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteStringId.class, StoreTestKodelisteStringId.class);
        builder.addBidirectional(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteString.class, StoreTestKodelisteString.class);
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

        addMapperFactory(new SnapshotBubbleIdTypeMapperFactory());
        addMapperFactory(new BubbleIdTypeMapperFactory(no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId.class, snapshotVersionProvider));
        addMapperFactory(new KodeTypeMapperFactory());
        addMapperFactory(new TransferTypeMapperFactory());

        addMapperFactory(new InverseRelationTypeMapperFactory());

        addMapperFactory(new CollectionMapperFactory());

        addMapperFactory(new DefaultTypeMapperFactory());

        // Basic domain types
        addMapper(TimestampTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp.class));
        addMapper(SnapshotVersionTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp.class));
        addMapper(new LocaleMapper());
        addMapper(new LocalizedStringTypeMapper());
        addMapper(new ClassTypeMapper());
        addMapper(new BobleklasseTypeMapper());
        addMapper(new EndringstypeTypeMapper());
        addMapper(new ReturnerBoblerTypeMapper());
        addMapper(new StoreBubbleTransferTypeMapper());

        // Endringer
        //addMapper(EndringTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.endringslogg.Endring.class, Endring.class));
//        addMapper(EndringTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.endringslogg.SimpleEndring.class, SimpleEndring.class));
//        addMapper(EndringTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.endringslogg.BubbleWithRelationEndring.class, BubbleWithRelationEndring.class));
//        addMapper(EndringTypeMapper.create(no.statkart.skif.storetest.wsapi.domain.endringslogg.SubTypedBubbleEndring.class, SubTypedBubbleEndring.class));

        // En litt spesiell kode
        addMapper(new KodeTypeMapperFactory.KodeTypeMapper<no.statkart.skif.storetest.wsapi.domain.koder.HistorikkEnumKode, HistorikkEnumKode>(no.statkart.skif.storetest.wsapi.domain.koder.HistorikkEnumKode.class, HistorikkEnumKode.class) {
            @Override
            public no.statkart.skif.storetest.wsapi.domain.koder.HistorikkEnumKode mapDomainObject(HistorikkEnumKode source) {
                no.statkart.skif.storetest.wsapi.domain.koder.HistorikkEnumKode target = super.mapDomainObject(source);
                target.setOppdateringsdato(getMapping().d2w(source.getOppdateringsdato()));
                target.setSluttdato(getMapping().d2w(source.getSluttdato()));
                return target;
            }

            @Override
            public HistorikkEnumKode mapWsapiObject(no.statkart.skif.storetest.wsapi.domain.koder.HistorikkEnumKode source) {
                HistorikkEnumKode target = super.mapWsapiObject(source);
                target.setOppdateringsdato(getMapping().w2d(source.getOppdateringsdato(), Timestamp.class));
                target.setSluttdato(getMapping().w2d(source.getSluttdato(), Timestamp.class));
                return target;
            }
        });

        addMapperFactory(new BubbleTransferTypeMapperFactory());
        addMapperFactory(new TransferTypeMapperFactory());
    }
}
