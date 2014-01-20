package no.statkart.skif.mapping;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Providers;
import no.statkart.skif.mapper.*;
import no.statkart.skif.store.*;
import org.fest.assertions.api.Assertions;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class TransferTypeMapperFactoryTest {
    @Test
    public void testMapping() {
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
        result.setIds(ImmutableSet.<DomainObjectId<?>>of(id1, id2));
        Transfer<DomainResult> transfer = new Transfer<DomainResult>(result, ImmutableList.of(object1, object2));

        TypeLiteral<Transfer<DomainResult>> domainTransferType = new TypeLiteral<Transfer<DomainResult>>() {};

        // Må angi source type siden det ikke kan utledes grunnet type erasure
        ApiTransfer apiTransfer = (ApiTransfer) mapping.d2w(transfer, domainTransferType.getType(), ApiTransfer.class);

        Assertions.assertThat(apiTransfer.getIds().getItem()).containsExactly(new ApiObjectId("1"), new ApiObjectId("2"));
        Assertions.assertThat(apiTransfer.getBubbleObjects().getItem()).containsExactly(new ApiObject(new ApiObjectId("1"), "A"), new ApiObject(new ApiObjectId("2"), "B"));

        // Source er her en ugenerisk klasse, så trenger ikke angi source type
        Transfer<DomainResult> domainTransfer = mapping.w2d(apiTransfer, domainTransferType);

        Assertions.assertThat(domainTransfer.getResult().getIds()).containsExactly(id1, id2);

        Set<DomainObjectId<?>> funnetIds = new HashSet<DomainObjectId<?>>(2);
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

    public static class TransferTypeMapperFactoryTestMapper extends AbstractMapper<Mapping> {
        public TransferTypeMapperFactoryTestMapper() {
            super(Mapping.class);

            MappingResolver mappingResolver = new MappingResolver();
            MappingOverrideBuilder mappingOverrideBuilder = new MappingOverrideBuilder();
            mappingOverrideBuilder.addBidirectional(ApiObject.class, DomainObject.class);
            mappingOverrideBuilder.addBidirectional(ApiObjectId.class, DomainObjectId.class);
            mappingResolver.overrideClassMappings(mappingOverrideBuilder.build());
            setMappingResolver(mappingResolver);

            addMapperFactory(new IdentityTypeMapperFactory().useIdentityMappingForBasicTypes());
            addMapperFactory(new BubbleIdTypeMapperFactory(ApiObjectId.class, Providers.of(SnapshotVersion.CURRENT)));
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
}
