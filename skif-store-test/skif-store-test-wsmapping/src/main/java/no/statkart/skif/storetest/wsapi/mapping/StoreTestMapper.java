package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.SkifException;
import no.statkart.skif.mapper.*;
import no.statkart.skif.service.annotation.Implementation;
import no.statkart.skif.storetest.domain.*;
import no.statkart.skif.storetest.domain.kodeliste.*;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbKodeliste;
import no.statkart.skif.storetest.domain.kodeliste.impl.DbKodelisteId;
import no.statkart.skif.storetest.domain.kodeliste.impl.EnumKodeliste;
import no.statkart.skif.storetest.domain.kodeliste.impl.EnumKodelisteId;
import no.statkart.skif.storetest.wsapi.domain.AList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleList;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeIdList;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteIdList;

import java.util.Collection;

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


        // Klasser hvor objekter skal mappes til seg selv
        useIdentityMapping(String.class);
        useIdentityMapping(Integer.class);
        useIdentityMapping(Long.class);

        // Alle Id'er
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId.class, StoreTestBubbleId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.TestBubbleId.class, TestBubbleId.class));

        // TODO: Endre kodemapper til å kunne håndtere multiple mappinger for source, rekkefølgen er viktig her
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class, DbKodelisteId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class, EnumKodelisteId.class));
        addMapper(new StoreTestBubbleIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId.class, KodelisteId.class));

        // Id Lister
        addMapper(new WsapiListTypeMapper(StoreTestBubbleIdList.class, Collection.class));
        addMapper(new WsapiListTypeMapper(KodeIdList.class, Collection.class));
        addMapper(new WsapiListTypeMapper(KodelisteIdList.class, Collection.class));

        // Boble Objekter
        addMapper(new StoreTestBubbleTypeMapper(no.statkart.skif.storetest.wsapi.domain.StoreTestBubble.class, StoreTestBubble.class));
        addMapper(new TestBubbleTypeMapper(no.statkart.skif.storetest.wsapi.domain.TestBubble.class, TestBubble.class));

        // TODO: Endre kodemapper til å kunne håndtere multiple mappinger for source, rekkefølgen er viktig her
        addMapper(new KodelisteTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste.class, DbKodeliste.class));
        addMapper(new KodelisteTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste.class, EnumKodeliste.class));
        addMapper(new KodelisteTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.Kodeliste.class, Kodeliste.class));

        // Boble lister
        addMapper(new WsapiListTypeMapper(AList.class, Collection.class));

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
        addMapper(new KodeIdTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestC2DbKodeId.class, TestC2DbKodeId.class));

        // Kode
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestAEnumKode.class, TestAEnumKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestBEnumKode.class, TestBEnumKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestCEnumKode.class, TestCEnumKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestADbKode.class, TestADbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestBDbKode.class, TestBDbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestC1DbKode.class, TestC1DbKode.class));
        addMapper(new KodeTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.TestC2DbKode.class, TestC2DbKode.class));

        addMapper(new KodelisteTransferTypeMapper(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteTransfer.class, KodelisteTransfer.class));
    }

    @Override
    public StoreTestMapping getMapping() {
        return (StoreTestMapping) super.getMapping();
    }
}