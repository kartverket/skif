package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.*;
import no.statkart.skif.mapper.ObjectFactory;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.storetest.domain.demo.koder.*;
import no.statkart.skif.storetest.domain.kodeliste.*;
import no.statkart.skif.storetest.wsapi.domain.demo.*;
import no.statkart.skif.storetest.wsapi.domain.kode.KodeIdList;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteIdList;

import java.util.Collection;
import java.util.HashMap;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class StoreTestMapper extends AbstractMapper {
    Class<? extends Mapping> mappingClass;

    public StoreTestMapper() {
        this(StoreTestMapping.class, new DefaultObjectFactory(), new DefaultObjectFactory());
    }

    public StoreTestMapper(Class<? extends Mapping> mappingClass) {
        this(mappingClass, new DefaultObjectFactory(), new DefaultObjectFactory());
    }


    @SuppressWarnings("unchecked")
    public StoreTestMapper(Class<? extends Mapping> mappingClass, ObjectFactory wsapiObjectFactory, ObjectFactory domainObjectFactory) {
        super(mappingClass, wsapiObjectFactory, domainObjectFactory, true);

        // DefaultTypeMapper. Brukes for objekter som har samme properties i domenene
        DefaultTypeMapper dtm = new DefaultTypeMapper();
        dtm.addPackageMapping("no.statkart.skif.storetest.wsapi.domain.demo","no.statkart.skif.storetest.domain.demo");
        setDefaultMapper(dtm);


        // Klasser hvor objekter skal mappes til seg selv
        useIdentityMapping(String.class);
        useIdentityMapping(Integer.class);
        useIdentityMapping(Long.class);

        // Timestamp
        addMapper(new TimestampTypeMapper());

        // SnapshotVersion
        addMapper(new SnapshotVersionTypeMapper());

        // Alle Id'er
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.TestBubbleId.class, no.statkart.skif.storetest.domain.demo.TestBubbleId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.BarId.class, no.statkart.skif.storetest.domain.demo.BarId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.FooId.class, no.statkart.skif.storetest.domain.demo.FooId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.BarFoosId.class, no.statkart.skif.storetest.domain.demo.BarFoosId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.BazId.class, no.statkart.skif.storetest.domain.demo.BazId.class));

        // TODO: Endre kodemapper til å kunne håndtere multiple mappinger for source, rekkefølgen er viktig her
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class, TestDbKodelisteId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class, TestEnumKodelisteId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class, TestKodelisteIdImpl.class));

        // Id Lister
        addMapper(new WsapiListTypeMapper(StoreTestBubbleIdList.class, Collection.class));
        addMapper(new WsapiListTypeMapper(KodeIdList.class, Collection.class));
        addMapper(new WsapiListTypeMapper(KodelisteIdList.class, Collection.class));
        addMapper(new WsapiListTypeMapper(BarIdList.class, Collection.class));
        addMapper(new WsapiListTypeMapper(BarFoosIdList.class, Collection.class));
        addMapper(new WsapiListTypeMapper(FooIdList.class, Collection.class));

        // Boble Objekter
        addMapper(new TestBubbleTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.TestBubble.class, no.statkart.skif.storetest.domain.demo.TestBubble.class));
        addMapper(new BarfoosTypeMapper());
        addMapper(new FooTypeMapper());

        // TODO: Endre kodemapper til å kunne håndtere multiple mappinger for source bedre, pt er rekkefølgen er viktig her, siste klasse vinner
        addMapper(new KodelisteTypeMapper("wsapi", no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste.class, TestDbKodeliste.class));
        addMapper(new KodelisteTypeMapper("wsapi", no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste.class, TestEnumKodeliste.class));
        addMapper(new KodelisteTypeMapper("wsapi", no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste.class, TestKodelisteImpl.class));

        // Boble lister
        addMapper(new WsapiListTypeMapper(StoreTestBubbleList.class, Collection.class));

        // Boble map
        addMapper(new WsapiMapTypeMapper(StoreTestBubbleIdListForStoreTestBubbleIdsMap.class, HashMap.class));

        // Non boble objekter

        // Non boble Lister

        // KodeId
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKodeId.class, no.statkart.skif.storetest.domain.demo.koder.TestAEnumKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBEnumKodeId.class, no.statkart.skif.storetest.domain.demo.koder.TestBEnumKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestCEnumKodeId.class, no.statkart.skif.storetest.domain.demo.koder.TestCEnumKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestADbKodeId.class, no.statkart.skif.storetest.domain.demo.koder.TestADbKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBDbKodeId.class, TestBDbKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC1DbKodeId.class, TestC1DbKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestCDbKodeId.class, TestCDbKodeId.class));

        // Kode
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKode.class, no.statkart.skif.storetest.domain.demo.koder.TestAEnumKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBEnumKode.class, no.statkart.skif.storetest.domain.demo.koder.TestBEnumKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestCEnumKode.class, no.statkart.skif.storetest.domain.demo.koder.TestCEnumKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestADbKode.class, TestADbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestBDbKode.class, TestBDbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestC1DbKode.class, TestC1DbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.demo.koder.TestCDbKode.class, TestCDbKode.class));

        addMapper(new KodelisteTransferTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer.class, KodelisteTransfer.class));
    }

    @Override
    public StoreTestMapping getMapping() {
        return (StoreTestMapping) super.getMapping();
    }
}
