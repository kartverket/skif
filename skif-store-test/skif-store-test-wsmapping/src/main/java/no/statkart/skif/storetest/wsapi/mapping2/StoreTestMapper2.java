package no.statkart.skif.storetest.wsapi.mapping2;

import no.statkart.skif.mapper.*;
import no.statkart.skif.mapper.ObjectFactory;
import no.statkart.skif.store2.KodelisteTransfer2;
import no.statkart.skif.store2.kodelistesupport2.*;
import no.statkart.skif.store2.kodelistesupport2.Kodeliste2;
import no.statkart.skif.store2.kodelistesupport2.KodelisteId2;
import no.statkart.skif.storetest.domain2.*;
import no.statkart.skif.storetest.domain2.TestAEnumKode2;
import no.statkart.skif.storetest.domain2.TestAEnumKodeId2;
import no.statkart.skif.storetest.domain2.TestBEnumKode2;
import no.statkart.skif.storetest.domain2.TestBEnumKodeId2;
import no.statkart.skif.storetest.domain2.TestCEnumKode2;
import no.statkart.skif.storetest.domain2.TestCEnumKodeId2;
import no.statkart.skif.storetest.domain2.kodeliste.TestADbKodeId2;
import no.statkart.skif.storetest.wsapi.domain2.A2List;
import no.statkart.skif.storetest.wsapi.domain2.StoreTestBubble2List;
import no.statkart.skif.storetest.wsapi.domain2.StoreTestBubbleId2List;
import no.statkart.skif.storetest.wsapi.domain2.kodeliste.*;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public class StoreTestMapper2 extends AbstractMapper {
    Class<? extends Mapping> mappingClass;

    public StoreTestMapper2() {
        this(StoreTestMapping2.class, new DefaultObjectFactory(), new DefaultObjectFactory());
    }

    public StoreTestMapper2(Class<? extends Mapping> mappingClass) {
        this(mappingClass, new DefaultObjectFactory(), new DefaultObjectFactory());
    }


    @SuppressWarnings("unchecked")
    public StoreTestMapper2(Class<? extends Mapping> mappingClass, ObjectFactory wsapiObjectFactory, ObjectFactory domainObjectFactory) {
        super(mappingClass, wsapiObjectFactory, domainObjectFactory, false);


        // Klasser hvor objekter skal mappes til seg selv
        useIdentityMapping(String.class);
        useIdentityMapping(Integer.class);
        useIdentityMapping(Long.class);

        // Alle Id'er
        addMapper(new StoreTestBubbleId2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.StoreTestBubbleId2.class, StoreTestBubbleId2.class));
        addMapper(new StoreTestBubbleId2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.TestBubbleId2.class, TestBubbleId2.class));

        // TODO: Endre kodemapper til å kunne håndtere multiple mappinger for source, rekkefølgen er viktig her
        addMapper(new StoreTestBubbleId2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.KodelisteId2.class, DbKodelisteId2.class));
        addMapper(new StoreTestBubbleId2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.KodelisteId2.class, EnumKodelisteId2.class));
        addMapper(new StoreTestBubbleId2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.KodelisteId2.class, KodelisteId2.class));

        // Id Lister
        addMapper(new WsapiListTypeMapper(StoreTestBubbleId2List.class, Collection.class));
        addMapper(new WsapiListTypeMapper(KodeId2List.class, Collection.class));
        addMapper(new WsapiListTypeMapper(KodelisteId2List.class, Collection.class));

        // Boble Objekter
        addMapper(new StoreTestBubble2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.StoreTestBubble2.class, StoreTestBubble2.class));
        addMapper(new TestBubble2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.TestBubble2.class, TestBubble2.class));

        // TODO: Endre kodemapper til å kunne håndtere multiple mappinger for source, rekkefølgen er viktig her
        addMapper(new Kodeliste2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.Kodeliste2.class, DbKodeliste2.class));
        addMapper(new Kodeliste2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.Kodeliste2.class, EnumKodeliste2.class));
        addMapper(new Kodeliste2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.Kodeliste2.class, Kodeliste2.class));

        // Boble lister
        addMapper(new WsapiListTypeMapper(A2List.class, Collection.class));

        // Non boble objekter
        addMapper(new A2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.A2.class, A2.class));

        // Non boble Lister
        addMapper(new WsapiListTypeMapper(StoreTestBubble2List.class, Collection.class));

        // KodeId
        addMapper(new KodeId2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.TestAEnumKodeId2.class, TestAEnumKodeId2.class));
        addMapper(new KodeId2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.TestBEnumKodeId2.class, TestBEnumKodeId2.class));
        addMapper(new KodeId2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.TestCEnumKodeId2.class, TestCEnumKodeId2.class));
        addMapper(new KodeId2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.TestADbKodeId2.class, TestADbKodeId2.class));
        addMapper(new KodeId2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.TestBDbKodeId2.class, TestBDbKodeId2.class));
        addMapper(new KodeId2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.TestC1DbKodeId2.class, TestC1DbKodeId2.class));
        addMapper(new KodeId2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.TestC2DbKodeId2.class, TestC2DbKodeId2.class));

        // Kode
        addMapper(new Kode2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.TestAEnumKode2.class, TestAEnumKode2.class));
        addMapper(new Kode2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.TestBEnumKode2.class, TestBEnumKode2.class));
        addMapper(new Kode2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.TestCEnumKode2.class, TestCEnumKode2.class));
        addMapper(new Kode2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.TestADbKode2.class, TestADbKode2.class));
        addMapper(new Kode2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.TestBDbKode2.class, TestBDbKode2.class));
        addMapper(new Kode2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.TestC1DbKode2.class, TestC1DbKode2.class));
        addMapper(new Kode2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.TestC2DbKode2.class, TestC2DbKode2.class));

        addMapper(new KodelisteTransfer2TypeMapper(no.statkart.skif.storetest.wsapi.domain2.kodeliste.KodelisteTransfer2.class, KodelisteTransfer2.class));
    }

    @Override
    public StoreTestMapping2 getMapping() {
        return (StoreTestMapping2) super.getMapping();
    }
}