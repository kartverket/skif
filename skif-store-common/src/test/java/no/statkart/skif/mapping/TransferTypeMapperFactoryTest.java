package no.statkart.skif.mapping;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;
import no.statkart.skif.mapper.*;
import no.statkart.skif.store.*;
import no.statkart.skif.store.kodeliste.KodelisteLong;
import no.statkart.skif.store.kodeliste.KodelisteLongId;
import org.fest.assertions.api.Assertions;
import org.testng.annotations.Test;

import java.util.*;

/**
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class TransferTypeMapperFactoryTest {
    @Test
    public void testTransfer() {
        Mapping mapping = new TransferTypeMapperFactoryTestMapper().getMapping();

        DomainObjectId id1 = new DomainObjectId(1L);
        DomainObjectId id2 = new DomainObjectId(2L);
        DomainObject object1 = new DomainObject();
        object1.setId(id1);
        object1.setText("A");
        DomainObject object2 = new DomainObject();
        object2.setId(id2);
        object2.setText("B");

        DomainResult result = new DomainResult();
        result.setIds(ImmutableSet.of(id1, id2));
        Transfer<DomainResult> transfer = new Transfer<>(result, ImmutableList.of(object1, object2));

        TypeLiteral<Transfer<DomainResult>> domainTransferType = new TypeLiteral<Transfer<DomainResult>>() {};

        // Må angi source type siden det ikke kan utledes grunnet type erasure
        ApiTransfer apiTransfer = (ApiTransfer) mapping.d2w(transfer, domainTransferType.getType(), ApiTransfer.class);

        Assertions.assertThat(apiTransfer.getIds().getItem()).containsExactly(new ApiObjectId("1"), new ApiObjectId("2"));
        Assertions.assertThat(apiTransfer.getBubbleObjects().getItem()).containsExactly(new ApiObject(new ApiObjectId("1"), "A"), new ApiObject(new ApiObjectId("2"), "B"));

        // Source er her en ugenerisk klasse, så trenger ikke angi source type
        Transfer<DomainResult> domainTransfer = mapping.w2d(apiTransfer, domainTransferType);

        Assertions.assertThat(domainTransfer.getResult().getIds()).containsExactly(id1, id2);

        Set<DomainObjectId<?>> funnetIds = new LinkedHashSet<>(2);
        Assertions.assertThat(transfer.getBubbleObjects()).hasSize(2);
        for (BubbleObject bubbleObject : transfer.getBubbleObjects().values()) {
            DomainObject domainObject = (DomainObject) bubbleObject;
            funnetIds.add(domainObject.getId());
            Assertions.assertThat(domainObject.getId()).isIn(id1, id2);
            if (domainObject.getId().equals(id1)) {
                Assertions.assertThat(domainObject.getText()).isEqualTo("A");
            } else if (domainObject.getId().equals(id2)) {
                Assertions.assertThat(domainObject.getText()).isEqualTo("B");
            }
        }
        Assertions.assertThat(funnetIds).containsExactly(id1, id2);
    }

    @Test
    public void testExternalBubbleTransfer() {
        Mapping mapping = new TransferTypeMapperFactoryTestMapper().getMapping();

        DomainObjectId<?> id1 = new DomainObjectId<>(1L);
        DomainObjectId<?> id2 = new DomainObjectId<>(2L);
        DomainObject object1 = new DomainObject();
        object1.setId(id1);
        object1.setText("A");
        DomainObject object2 = new DomainObject();
        object2.setId(id2);
        object2.setText("B");

        DomainBubbleTransfer transfer = new DomainBubbleTransfer(id1, ImmutableList.of(object1, object2), ImmutableSet.of(id1, id2));

        ApiBubbleTransfer apiTransfer = mapping.d2w(transfer, ApiBubbleTransfer.class);

        Assertions.assertThat(apiTransfer.getId()).isEqualTo(new ApiObjectId("1"));
        Assertions.assertThat(apiTransfer.getBubbleObjects().getItem()).containsExactly(new ApiObject(new ApiObjectId("1"), "A"), new ApiObject(new ApiObjectId("2"), "B"));
        Assertions.assertThat(apiTransfer.getLockedIds().getItem()).containsExactly(new ApiObjectId("1"), new ApiObjectId("2"));

        DomainBubbleTransfer domainTransfer = mapping.w2d(apiTransfer, DomainBubbleTransfer.class);

        Assertions.assertThat((DomainObjectId) domainTransfer.getResult()).isEqualTo((DomainObjectId) id1);

        Set<DomainObjectId<?>> funnetIds = new LinkedHashSet<>(2);
        Assertions.assertThat(transfer.getBubbleObjects()).hasSize(2);
        for (BubbleObject bubbleObject : transfer.getBubbleObjects().values()) {
            DomainObject domainObject = (DomainObject) bubbleObject;
            funnetIds.add(domainObject.getId());
            Assertions.assertThat((DomainObjectId) domainObject.getId()).isIn((DomainObjectId) id1, (DomainObjectId) id2);
            if (domainObject.getId().equals(id1)) {
                Assertions.assertThat(domainObject.getText()).isEqualTo("A");
            } else if (domainObject.getId().equals(id2)) {
                Assertions.assertThat(domainObject.getText()).isEqualTo("B");
            }
        }
        Assertions.assertThat(funnetIds).containsExactly(id1, id2);
        Assertions.assertThat(domainTransfer.getLockedIds()).containsExactly(id1, id2);
    }

    @Test
    public void testExternalBubbleTransferSub() {
        Mapping mapping = new TransferTypeMapperFactoryTestMapper().getMapping();

        DomainObjectId<?> id1 = new DomainObjectId<>(1L);
        DomainObjectId<?> id2 = new DomainObjectId<>(2L);
        DomainObject object1 = new DomainObject();
        object1.setId(id1);
        object1.setText("A");
        DomainObject object2 = new DomainObject();
        object2.setId(id2);
        object2.setText("B");

        DomainBubbleTransferSub transfer = new DomainBubbleTransferSub(id1, ImmutableList.of(object1, object2), ImmutableSet.of(id1, id2));

        ApiBubbleTransferSub apiTransfer = mapping.d2w(transfer, ApiBubbleTransferSub.class);

        Assertions.assertThat(apiTransfer.getId()).isEqualTo(new ApiObjectId("1"));
        Assertions.assertThat(apiTransfer.getBubbleObjects().getItem()).containsExactly(new ApiObject(new ApiObjectId("1"), "A"), new ApiObject(new ApiObjectId("2"), "B"));
        Assertions.assertThat(apiTransfer.getLockedIds().getItem()).containsExactly(new ApiObjectId("1"), new ApiObjectId("2"));

        DomainBubbleTransferSub domainTransfer = mapping.w2d(apiTransfer, DomainBubbleTransferSub.class);

        Assertions.assertThat((DomainObjectId) domainTransfer.getResult()).isEqualTo((DomainObjectId) id1);

        Set<DomainObjectId<?>> funnetIds = new LinkedHashSet<>(2);
        Assertions.assertThat(transfer.getBubbleObjects()).hasSize(2);
        for (BubbleObject bubbleObject : transfer.getBubbleObjects().values()) {
            DomainObject domainObject = (DomainObject) bubbleObject;
            funnetIds.add(domainObject.getId());
            Assertions.assertThat((DomainObjectId) domainObject.getId()).isIn((DomainObjectId) id1, (DomainObjectId) id2);
            if (domainObject.getId().equals(id1)) {
                Assertions.assertThat(domainObject.getText()).isEqualTo("A");
            } else if (domainObject.getId().equals(id2)) {
                Assertions.assertThat(domainObject.getText()).isEqualTo("B");
            }
        }
        Assertions.assertThat(funnetIds).containsExactly(id1, id2);
        Assertions.assertThat(domainTransfer.getLockedIds()).containsExactly(id1, id2);
    }

    @Test
    public void testInternalBubbleTransfer() {
        Mapping mapping = new TransferTypeMapperFactoryTestMapper().getMapping();

        DomainObjectId<?> id1 = new DomainObjectId<>(1L);
        DomainObjectId<?> id2 = new DomainObjectId<>(2L);
        DomainObject object1 = new DomainObject();
        object1.setId(id1);
        object1.setText("A");
        DomainObject object2 = new DomainObject();
        object2.setId(id2);
        object2.setText("B");

        DomainResult result = new DomainResult();
        result.setIds(ImmutableSet.of(id1));
        DomainBubbleTransfer2<DomainResult> transfer = new DomainBubbleTransfer2<>(result, ImmutableList.of(object1, object2), ImmutableSet.of(id1, id2));

        ApiBubbleTransfer2 apiTransfer = mapping.d2w(transfer, ApiBubbleTransfer2.class);

        Assertions.assertThat(apiTransfer.getIds().getItem()).containsExactly(new ApiObjectId("1"));
        Assertions.assertThat(apiTransfer.getBubbleObjects().getItem()).containsExactly(new ApiObject(new ApiObjectId("1"), "A"), new ApiObject(new ApiObjectId("2"), "B"));
        Assertions.assertThat(apiTransfer.getLockedIds().getItem()).containsExactly(new ApiObjectId("1"), new ApiObjectId("2"));

        DomainBubbleTransfer2<DomainResult> domainTransfer = mapping.w2d(apiTransfer, new TypeLiteral<DomainBubbleTransfer2<DomainResult>>(){});

        Assertions.assertThat(domainTransfer.getResult().getIds()).containsExactly((DomainObjectId) id1);

        Set<DomainObjectId<?>> funnetIds = new LinkedHashSet<>(2);
        Assertions.assertThat(transfer.getBubbleObjects()).hasSize(2);
        for (BubbleObject bubbleObject : transfer.getBubbleObjects().values()) {
            DomainObject domainObject = (DomainObject) bubbleObject;
            funnetIds.add(domainObject.getId());
            Assertions.assertThat((DomainObjectId) domainObject.getId()).isIn((DomainObjectId) id1, (DomainObjectId) id2);
            if (domainObject.getId().equals(id1)) {
                Assertions.assertThat(domainObject.getText()).isEqualTo("A");
            } else if (domainObject.getId().equals(id2)) {
                Assertions.assertThat(domainObject.getText()).isEqualTo("B");
            }
        }
        Assertions.assertThat(funnetIds).containsExactly(id1, id2);
        Assertions.assertThat(domainTransfer.getLockedIds()).containsExactly(id1, id2);
    }

    @Test
    public void testInternalBubbleTransferSubclass() {
        Mapping mapping = new TransferTypeMapperFactoryTestMapper().getMapping();

        TypeLiteral<DomainBubbleTransfer2<DomainResultSub>> domainType = new TypeLiteral<DomainBubbleTransfer2<DomainResultSub>>() {};

        DomainObjectId<?> id1 = new DomainObjectId<>(1L);
        DomainObjectId<?> id2 = new DomainObjectId<>(2L);
        DomainObject object1 = new DomainObject();
        object1.setId(id1);
        object1.setText("A");
        DomainObject object2 = new DomainObject();
        object2.setId(id2);
        object2.setText("B");

        DomainResultSub result = new DomainResultSub();
        result.setIds(ImmutableSet.of(id1));
        result.setInfo("Testing");
        DomainBubbleTransfer2<DomainResultSub> transfer = new DomainBubbleTransfer2<>(result, ImmutableList.of(object1, object2), ImmutableSet.of(id1, id2));

        ApiBubbleTransfer2Sub apiTransfer = (ApiBubbleTransfer2Sub) mapping.d2w(transfer, domainType.getType(), ApiBubbleTransfer2Sub.class);

        Assertions.assertThat(apiTransfer.getIds().getItem()).containsExactly(new ApiObjectId("1"));
        Assertions.assertThat(apiTransfer.getInfo()).isEqualTo("Testing");
        Assertions.assertThat(apiTransfer.getBubbleObjects().getItem()).containsExactly(new ApiObject(new ApiObjectId("1"), "A"), new ApiObject(new ApiObjectId("2"), "B"));
        Assertions.assertThat(apiTransfer.getLockedIds().getItem()).containsExactly(new ApiObjectId("1"), new ApiObjectId("2"));

        DomainBubbleTransfer2<DomainResultSub> domainTransfer = mapping.w2d(apiTransfer, domainType);

        Assertions.assertThat(domainTransfer.getResult().getIds()).containsExactly((DomainObjectId) id1);
        Assertions.assertThat(domainTransfer.getResult().getInfo()).isEqualTo("Testing");

        Set<DomainObjectId<?>> funnetIds = new LinkedHashSet<>(2);
        Assertions.assertThat(transfer.getBubbleObjects()).hasSize(2);
        for (BubbleObject bubbleObject : transfer.getBubbleObjects().values()) {
            DomainObject domainObject = (DomainObject) bubbleObject;
            funnetIds.add(domainObject.getId());
            Assertions.assertThat((DomainObjectId) domainObject.getId()).isIn((DomainObjectId) id1, (DomainObjectId) id2);
            if (domainObject.getId().equals(id1)) {
                Assertions.assertThat(domainObject.getText()).isEqualTo("A");
            } else if (domainObject.getId().equals(id2)) {
                Assertions.assertThat(domainObject.getText()).isEqualTo("B");
            }
        }
        Assertions.assertThat(funnetIds).containsExactly(id1, id2);
        Assertions.assertThat(domainTransfer.getLockedIds()).containsExactly(id1, id2);
    }

    @Test
    public void testInternalBubbleTransferClosed() {
        Mapping mapping = new TransferTypeMapperFactoryTestMapper().getMapping();

        DomainObjectId<?> id1 = new DomainObjectId<>(1L);
        DomainObjectId<?> id2 = new DomainObjectId<>(2L);
        DomainObject object1 = new DomainObject();
        object1.setId(id1);
        object1.setText("A");
        DomainObject object2 = new DomainObject();
        object2.setId(id2);
        object2.setText("B");

        DomainResult result = new DomainResult();
        result.setIds(ImmutableSet.of(id1));
        DomainBubbleTransfer3 transfer = new DomainBubbleTransfer3(result, ImmutableList.of(object1, object2), ImmutableSet.of(id1, id2));

        ApiBubbleTransfer2 apiTransfer = (ApiBubbleTransfer2) mapping.d2w(transfer, DomainBubbleTransfer3.class, ApiBubbleTransfer2.class);

        Assertions.assertThat(apiTransfer.getIds().getItem()).containsExactly(new ApiObjectId("1"));
        Assertions.assertThat(apiTransfer.getBubbleObjects().getItem()).containsExactly(new ApiObject(new ApiObjectId("1"), "A"), new ApiObject(new ApiObjectId("2"), "B"));
        Assertions.assertThat(apiTransfer.getLockedIds().getItem()).containsExactly(new ApiObjectId("1"), new ApiObjectId("2"));

        DomainBubbleTransfer3 domainTransfer = mapping.w2d(apiTransfer, DomainBubbleTransfer3.class);

        Assertions.assertThat(domainTransfer.getResult().getIds()).containsExactly((DomainObjectId) id1);

        Set<DomainObjectId<?>> funnetIds = new LinkedHashSet<>(2);
        Assertions.assertThat(transfer.getBubbleObjects()).hasSize(2);
        for (BubbleObject bubbleObject : transfer.getBubbleObjects().values()) {
            DomainObject domainObject = (DomainObject) bubbleObject;
            funnetIds.add(domainObject.getId());
            Assertions.assertThat((DomainObjectId) domainObject.getId()).isIn((DomainObjectId) id1, (DomainObjectId) id2);
            if (domainObject.getId().equals(id1)) {
                Assertions.assertThat(domainObject.getText()).isEqualTo("A");
            } else if (domainObject.getId().equals(id2)) {
                Assertions.assertThat(domainObject.getText()).isEqualTo("B");
            }
        }
        Assertions.assertThat(funnetIds).containsExactly(id1, id2);
        Assertions.assertThat(domainTransfer.getLockedIds()).containsExactly(id1, id2);
    }

    @Test
    public void testKodeliste() {
        Mapping mapping = new TransferTypeMapperFactoryTestMapper().getMapping();

        List<DomainKodelisteId<?>> kodelisteIds = new ArrayList<>();
        DomainKodelisteId<DomainKodeliste> kodelisteId = new DomainKodelisteId<>(1L);
        kodelisteIds.add(kodelisteId);

        List<DomainKodeliste> kodelister = new ArrayList<>();
        DomainKodeliste kodeliste = new DomainKodeliste();
        kodeliste.setId(kodelisteId);
        kodelister.add(kodeliste);

        KodelisteTransfer<DomainKodelisteId<?>> kodelisteTransfer = new KodelisteTransfer<>(kodelisteIds, kodelister);

        ApiKodelisteTransfer apiKodelisteTransfer = mapping.d2w(kodelisteTransfer, ApiKodelisteTransfer.class);

        Assertions.assertThat(apiKodelisteTransfer.getKodelisterIds().getItem()).containsExactly(new ApiKodelisteId("1"));
        Assertions.assertThat(apiKodelisteTransfer.getBubbleObjects().getItem()).containsExactly(new ApiKodeliste(new ApiKodelisteId("1")));

        KodelisteTransfer domeneKodelisteTransfer = mapping.w2d(apiKodelisteTransfer, KodelisteTransfer.class);

        //noinspection unchecked
        Assertions.assertThat(domeneKodelisteTransfer.getKodelisterIds()).containsExactly(kodelisteId);
        Assertions.assertThat((Map<?, ?>) domeneKodelisteTransfer.getBubbleObjects()).hasSize(1);
        Assertions.assertThat(domeneKodelisteTransfer.getBubbleObjects().get(kodelisteId)).isNotNull();
    }

    public static class TransferTypeMapperFactoryTestMapper extends AbstractMapper<Mapping> {
        public TransferTypeMapperFactoryTestMapper() {
            super(Mapping.class);

            MappingResolver mappingResolver = new MappingResolver();
            MappingOverrideBuilder mappingOverrideBuilder = new MappingOverrideBuilder();
            mappingOverrideBuilder.addBidirectional(ApiObject.class, DomainObject.class);
            mappingOverrideBuilder.addBidirectional(ApiObjectId.class, DomainObjectId.class);
            mappingOverrideBuilder.addBidirectional(ApiKodeliste.class, DomainKodeliste.class);
            mappingOverrideBuilder.addBidirectional(ApiKodelisteId.class, DomainKodelisteId.class);
            mappingResolver.overrideClassMappings(mappingOverrideBuilder.build());
            setMappingResolver(mappingResolver);

            addMapperFactory(new IdentityTypeMapperFactory().useIdentityMappingForBasicTypes());
            addMapperFactory(new BubbleIdTypeMapperFactory(ApiObjectId.class, Providers.of(SnapshotVersion.CURRENT)));
            addMapperFactory(new BubbleIdTypeMapperFactory(ApiKodelisteId.class, Providers.of(SnapshotVersion.CURRENT)));
            addMapperFactory(new BubbleTransferTypeMapperFactory());
            addMapperFactory(new TransferTypeMapperFactory());
            addMapperFactory(new CollectionMapperFactory());
            addMapperFactory(new DefaultTypeMapperFactory());
        }
    }

    public static class DomainResult {
        Set<DomainObjectId<?>> ids;

        public Set<DomainObjectId<?>> getIds() {
            return ids;
        }

        public void setIds(Set<DomainObjectId<?>> ids) {
            this.ids = ids;
        }
    }

    public static class DomainResultSub extends DomainResult {
        private String info;

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }

    public static class DomainObject extends AbstractBubbleObject {
        private String text;

        @Override
        public DomainObjectId<?> getId() {
            return (DomainObjectId<?>) super.getId();
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class DomainObjectId<T extends DomainObject> extends AbstractBubbleId<T> {
        public DomainObjectId(Long value) {
            super(value);
        }

        @SuppressWarnings("UnusedDeclaration")
        public DomainObjectId(Long value, SnapshotVersion version) {
            super(value, version);
        }

        @Override
        public Long getValue() {
            return (Long) super.getValue();
        }
    }

    public static class DomainBubbleTransfer extends BubbleTransfer<DomainObjectId<?>> {
//        public DomainBubbleTransfer(DomainObjectId<?> result, Iterable<? extends BubbleObject> objects) {
//            super(result, objects);
//        }

        @SuppressWarnings("deprecation")
        public DomainBubbleTransfer(DomainObjectId<?> result, Iterable<? extends BubbleObject> objects, Iterable<? extends BubbleId> lockedIds) {
            super(result, objects, lockedIds);
        }
    }

    public static class DomainBubbleTransferSub extends DomainBubbleTransfer {

        public DomainBubbleTransferSub(
              DomainObjectId<?> result, Iterable<? extends BubbleObject> objects,
              Iterable<? extends BubbleId> lockedIds) {
            super(result, objects, lockedIds);
        }
    }

    public static class DomainBubbleTransfer2<T extends DomainResult> extends BubbleTransfer<T> {
        @SuppressWarnings("deprecation")
        public DomainBubbleTransfer2(T result, Iterable<? extends BubbleObject> objects, Iterable<? extends BubbleId> lockedIds) {
            super(result, objects, lockedIds);
        }
    }

    public static class DomainBubbleTransfer3 extends BubbleTransfer<DomainResult> {
        @SuppressWarnings("deprecation")
        public DomainBubbleTransfer3(DomainResult result, Iterable<? extends BubbleObject> objects, Iterable<? extends BubbleId> lockedIds) {
            super(result, objects, lockedIds);
        }
    }

    public static class ApiObject {
        private ApiObjectId id;
        private String text;

        // Api-klasser har normal ikke kostruktører, men her har de det for å forenkle testing

        @SuppressWarnings("UnusedDeclaration")
        public ApiObject() {
        }

        public ApiObject(ApiObjectId id, String text) {
            this.id = id;
            this.text = text;
        }

        public ApiObjectId getId() {
            return id;
        }

        public void setId(ApiObjectId id) {
            this.id = id;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        // Api-klasser har normal ikke equals og hashCode, men her har de det for å forenkle testing

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ApiObject apiObject = (ApiObject) o;

            return id.equals(apiObject.id) && text.equals(apiObject.text);
        }

        @Override
        public int hashCode() {
            int result = id.hashCode();
            result = 31 * result + text.hashCode();
            return result;
        }
    }

    public static class ApiObjectId {
        private String value;

        // Api-klasser har normal ikke kostruktører, men her har de det for å forenkle testing

        @SuppressWarnings("UnusedDeclaration")
        public ApiObjectId() {
        }

        public ApiObjectId(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @SuppressWarnings("UnusedDeclaration")
        public void setValue(String value) {
            this.value = value;
        }

        // Api-klasser har normal ikke equals og hashCode, men her har de det for å forenkle testing

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ApiObjectId that = (ApiObjectId) o;

            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    public static class ApiObjectList {
        private List<ApiObject> item;

        public List<ApiObject> getItem() {
            return item;
        }

        @SuppressWarnings("UnusedDeclaration")
        public void setItem(List<ApiObject> item) {
            this.item = item;
        }

    }

    public static class ApiIdList {
        private List<ApiObjectId> item;

        public List<ApiObjectId> getItem() {
            return item;
        }

        @SuppressWarnings("UnusedDeclaration")
        public void setItem(List<ApiObjectId> item) {
            this.item = item;
        }

    }

    public static class ApiTransfer {
        private ApiObjectList bubbleObjects;
        private ApiIdList ids;

        public ApiObjectList getBubbleObjects() {
            return bubbleObjects;
        }

        @SuppressWarnings("UnusedDeclaration")
        public void setBubbleObjects(ApiObjectList bubbleObjects) {
            this.bubbleObjects = bubbleObjects;
        }

        public ApiIdList getIds() {
            return ids;
        }

        public void setIds(ApiIdList ids) {
            this.ids = ids;
        }
    }

    public static class ApiBubbleTransfer {
        private ApiObjectList bubbleObjects;
        private ApiIdList lockedIds;
        private ApiObjectId id;

        public ApiObjectList getBubbleObjects() {
            return bubbleObjects;
        }

        @SuppressWarnings("UnusedDeclaration")
        public void setBubbleObjects(ApiObjectList bubbleObjects) {
            this.bubbleObjects = bubbleObjects;
        }

        public ApiObjectId getId() {
            return id;
        }

        public void setId(ApiObjectId id) {
            this.id = id;
        }

        public ApiIdList getLockedIds() {
            return lockedIds;
        }

        @SuppressWarnings("UnusedDeclaration") // WS-mapping
        public void setLockedIds(ApiIdList lockedIds) {
            this.lockedIds = lockedIds;
        }
    }

    public static class ApiBubbleTransferSub extends ApiBubbleTransfer {}

    public static class ApiBubbleTransfer2 {
        private ApiObjectList bubbleObjects;
        private ApiIdList lockedIds;
        private ApiIdList ids;

        public ApiObjectList getBubbleObjects() {
            return bubbleObjects;
        }

        @SuppressWarnings("UnusedDeclaration")
        public void setBubbleObjects(ApiObjectList bubbleObjects) {
            this.bubbleObjects = bubbleObjects;
        }

        public ApiIdList getIds() {
            return ids;
        }

        public void setIds(ApiIdList ids) {
            this.ids = ids;
        }

        public ApiIdList getLockedIds() {
            return lockedIds;
        }

        @SuppressWarnings("UnusedDeclaration") // WS-mapping
        public void setLockedIds(ApiIdList lockedIds) {
            this.lockedIds = lockedIds;
        }
    }

    public static class ApiBubbleTransfer2Sub extends ApiBubbleTransfer2 {
        private String info;

        public String getInfo() {
            return info;
        }

        public void setInfo(String info) {
            this.info = info;
        }
    }

    public static class ApiKodelisteTransfer {
        private ApiKodelisteList bubbleObjects;
        private ApiKodelisteIdList kodelisterIds;

        public ApiKodelisteList getBubbleObjects() {
            return bubbleObjects;
        }

        @SuppressWarnings("UnusedDeclaration")
        public void setBubbleObjects(ApiKodelisteList bubbleObjects) {
            this.bubbleObjects = bubbleObjects;
        }

        public ApiKodelisteIdList getKodelisterIds() {
            return kodelisterIds;
        }

        @SuppressWarnings("UnusedDeclaration") // WS-mapping
        public void setKodelisterIds(ApiKodelisteIdList kodelisterIds) {
            this.kodelisterIds = kodelisterIds;
        }
    }

    public static class DomainKodeliste extends KodelisteLong {
        private String text;

        @Override
        public DomainKodelisteId<?> getId() {
            return (DomainKodelisteId<?>) super.getId();
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }
    }

    public static class DomainKodelisteId<T extends DomainKodeliste> extends KodelisteLongId<T> {
        public DomainKodelisteId(Long value) {
            super(value);
        }

        @SuppressWarnings("UnusedDeclaration")
        public DomainKodelisteId(Long value, SnapshotVersion version) {
            super(value, version);
        }
    }

    public static class ApiKodelisteId {
        private String value;

        // Api-klasser har normal ikke kostruktører, men her har de det for å forenkle testing

        @SuppressWarnings("UnusedDeclaration")
        public ApiKodelisteId() {
        }

        public ApiKodelisteId(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @SuppressWarnings("UnusedDeclaration")
        public void setValue(String value) {
            this.value = value;
        }

        // Api-klasser har normal ikke equals og hashCode, men her har de det for å forenkle testing

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ApiKodelisteId that = (ApiKodelisteId) o;

            return value.equals(that.value);
        }

        @Override
        public int hashCode() {
            return value.hashCode();
        }
    }

    public static class ApiKodeliste {
        private ApiKodelisteId id;

        // Api-klasser har normal ikke kostruktører, men her har de det for å forenkle testing

        @SuppressWarnings("UnusedDeclaration")
        public ApiKodeliste() {
        }

        public ApiKodeliste(ApiKodelisteId id) {
            this.id = id;
        }

        public ApiKodelisteId getId() {
            return id;
        }

        public void setId(ApiKodelisteId id) {
            this.id = id;
        }
        // Api-klasser har normal ikke equals og hashCode, men her har de det for å forenkle testing

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ApiKodeliste apiKodeliste = (ApiKodeliste) o;

            return id.equals(apiKodeliste.id);
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    public static class ApiKodelisteList {
        private List<ApiKodeliste> item;

        public List<ApiKodeliste> getItem() {
            return item;
        }

        @SuppressWarnings("UnusedDeclaration")
        public void setItem(List<ApiKodeliste> item) {
            this.item = item;
        }

    }

    public static class ApiKodelisteIdList {
        private List<ApiKodelisteId> item;

        public List<ApiKodelisteId> getItem() {
            return item;
        }

        @SuppressWarnings("UnusedDeclaration")
        public void setItem(List<ApiKodelisteId> item) {
            this.item = item;
        }

    }
}
