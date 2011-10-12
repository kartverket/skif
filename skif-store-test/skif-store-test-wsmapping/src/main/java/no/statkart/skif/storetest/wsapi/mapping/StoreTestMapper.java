package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.*;
import no.statkart.skif.mapper.ObjectFactory;
import no.statkart.skif.store.KodelisteTransfer;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.*;
import no.statkart.skif.store.kodelistesupport.Kodeliste;
import no.statkart.skif.store.kodelistesupport.KodelisteId;
import no.statkart.skif.storetest.domain.*;
import no.statkart.skif.storetest.domain.A;
import no.statkart.skif.storetest.domain.BarFoosId;
import no.statkart.skif.storetest.domain.BarId;
import no.statkart.skif.storetest.domain.BazId;
import no.statkart.skif.storetest.domain.FooId;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.domain.TestAEnumKode;
import no.statkart.skif.storetest.domain.TestAEnumKodeId;
import no.statkart.skif.storetest.domain.TestBEnumKode;
import no.statkart.skif.storetest.domain.TestBEnumKodeId;
import no.statkart.skif.storetest.domain.TestBubble;
import no.statkart.skif.storetest.domain.TestBubbleId;
import no.statkart.skif.storetest.domain.TestCEnumKode;
import no.statkart.skif.storetest.domain.TestCEnumKodeId;
import no.statkart.skif.storetest.domain.kodeliste.TestADbKodeId;
import no.statkart.skif.storetest.wsapi.domain.*;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

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
        super(mappingClass, wsapiObjectFactory, domainObjectFactory, false);

        // DefaultTypeMapper. Brukes for objekter som har samme properties i domenene
        DefaultTypeMapper dtm = new DefaultTypeMapper();
        dtm.addPackageMapping("no.statkart.skif.storetest.wsapi.domain","no.statkart.skif.storetest.domain");
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
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId.class, StoreTestBubbleId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.TestBubbleId.class, TestBubbleId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.BarId.class, BarId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.FooId.class, FooId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.BarFoosId.class, BarFoosId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.BazId.class, BazId.class));

        // TODO: Endre kodemapper til å kunne håndtere multiple mappinger for source, rekkefølgen er viktig her
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class, DbKodelisteId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class, EnumKodelisteId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class, KodelisteId.class));

        // Id Lister
        addMapper(new WsapiListTypeMapper(StoreTestBubbleIdList.class, Collection.class));
        addMapper(new WsapiListTypeMapper(KodeIdList.class, Collection.class));
        addMapper(new WsapiListTypeMapper(KodelisteIdList.class, Collection.class));
        addMapper(new WsapiListTypeMapper(BarIdList.class, Collection.class));
        addMapper(new WsapiListTypeMapper(BarFoosIdList.class, Collection.class));
        addMapper(new WsapiListTypeMapper(FooIdList.class, Collection.class));

        // Boble Objekter
//        addMapper(new StoreTestBubbleTypeMapper(no.statkart.skif.storetest.wsapi.domain.StoreTestBubble.class, StoreTestBubble.class));
//        addMapper(new TestBubbleTypeMapper(no.statkart.skif.storetest.wsapi.domain.TestBubble.class, TestBubble.class));
        addMapper(new BarfoosTypeMapper());
        addMapper(new FooTypeMapper());

        // TODO: Endre kodemapper til å kunne håndtere multiple mappinger for source, rekkefølgen er viktig her
        addMapper(new KodelisteTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste.class, DbKodeliste.class));
        addMapper(new KodelisteTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste.class, EnumKodeliste.class));
        addMapper(new KodelisteTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste.class, Kodeliste.class));

        // Boble lister
        addMapper(new WsapiListTypeMapper(AList.class, Collection.class));

        // Boble map
        addMapper(new WsapiMapTypeMapper(StoreTestBubbleIdListForStoreTestBubbleIdsMap.class, Map.class));

        // Non boble objekter
        addMapper(new ATypeMapper(no.statkart.skif.storetest.wsapi.domain.A.class, A.class));

        // Non boble Lister
        addMapper(new WsapiListTypeMapper(StoreTestBubbleList.class, Collection.class));

        // KodeId
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestAEnumKodeId.class, TestAEnumKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestBEnumKodeId.class, TestBEnumKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestCEnumKodeId.class, TestCEnumKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestADbKodeId.class, TestADbKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestBDbKodeId.class, TestBDbKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestC1DbKodeId.class, TestC1DbKodeId.class));
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestCDbKodeId.class, TestCDbKodeId.class));

        // Kode
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestAEnumKode.class, TestAEnumKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestBEnumKode.class, TestBEnumKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestCEnumKode.class, TestCEnumKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestADbKode.class, TestADbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestBDbKode.class, TestBDbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestC1DbKode.class, TestC1DbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestCDbKode.class, TestCDbKode.class));

        addMapper(new KodelisteTransferTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer.class, KodelisteTransfer.class));
    }

    @Override
    public StoreTestMapping getMapping() {
        return (StoreTestMapping) super.getMapping();
    }
}