package no.statkart.skif.store;

import no.statkart.skif.exception.ObjectsNotFoundException;
import no.statkart.skif.store.memorydomain.BaseType;
import no.statkart.skif.store.memorydomain.BaseTypeId;
import no.statkart.skif.store.memorydomain.MemoryDomainHelper;
import no.statkart.skif.store.memorydomain.ObjectSupplier;
import no.statkart.skif.store.memorydomain.SubType1;
import no.statkart.skif.store.memorydomain.SubType1Id;
import no.statkart.skif.store.memorydomain.SubType2;
import no.statkart.skif.store.memorydomain.SubType2Id;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

public class StoreTypeChangeTest {
    /**
     * Sjekker at typehierarkiet virker som forventet.
     */
    @Test
    public void testTypeHierarchy() {
        SoftAssertions.assertSoftly(softAssertions -> {
            BaseTypeId<?> baseId = new BaseTypeId<>(1L, SnapshotVersion.CURRENT);
            softAssertions.assertThat(baseId.getBaseType())
                    .describedAs("Basetypens base")
                    .isEqualTo(BaseType.class);
            softAssertions.assertThat(baseId.getType())
                    .describedAs("Basetypens type")
                    .isEqualTo(BaseType.class);
            softAssertions.assertThat(baseId.getBaseIdType())
                    .describedAs("Basetypens baseidtype")
                    .isEqualTo(BaseTypeId.class);

            SubType1Id sub1Id = new SubType1Id(1L, SnapshotVersion.CURRENT);
            softAssertions.assertThat(sub1Id.getBaseType())
                    .describedAs("Subtype1s base")
                    .isEqualTo(BaseType.class);
            softAssertions.assertThat(sub1Id.getType())
                    .describedAs("Subtype1s type")
                    .isEqualTo(SubType1.class);
            softAssertions.assertThat(sub1Id.getBaseIdType())
                    .describedAs("Subtype1s baseidtype")
                    .isEqualTo(BaseTypeId.class);

            SubType2Id sub2Id = new SubType2Id(1L, SnapshotVersion.CURRENT);
            softAssertions.assertThat(sub2Id.getBaseType())
                    .describedAs("Subtype2s base")
                    .isEqualTo(BaseType.class);
            softAssertions.assertThat(sub2Id.getType())
                    .describedAs("Subtype2s type")
                    .isEqualTo(SubType2.class);
            softAssertions.assertThat(sub2Id.getBaseIdType())
                    .describedAs("Subtype2s baseidtype")
                    .isEqualTo(BaseTypeId.class);

            softAssertions.assertThat(baseId).isEqualTo(sub1Id);
            softAssertions.assertThat(baseId).isEqualTo(sub2Id);
            softAssertions.assertThat(sub1Id).isEqualTo(sub2Id);
            softAssertions.assertThat(sub1Id).isEqualTo(baseId);
            softAssertions.assertThat(sub2Id).isEqualTo(baseId);
            softAssertions.assertThat(sub2Id).isEqualTo(sub1Id);
        });
    }

    @Test
    public void testGetBaseThenSub() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        BubbleObject baseObject = store.get(new BaseTypeId<>(1L, SnapshotVersion.CURRENT));
        SubType1 sub1Object = store.get(new SubType1Id(1L, SnapshotVersion.CURRENT));

        Assertions.assertThat(sub1Object).isSameAs(baseObject);
    }

    @Test
    public void testGetSubThenBase() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        SubType1 sub1Object = store.get(new SubType1Id(1L, SnapshotVersion.CURRENT));
        BubbleObject baseObject = store.get(new BaseTypeId<>(1L, SnapshotVersion.CURRENT));

        Assertions.assertThat(baseObject).isSameAs(sub1Object);
    }

    @Test
    public void testGetRightSubThenWrongSub() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        store.get(new SubType1Id(1L, SnapshotVersion.CURRENT));
        Assertions.assertThatThrownBy(() -> store.get(new SubType2Id(1L, SnapshotVersion.CURRENT)))
                .isInstanceOf(ObjectsNotFoundException.class)
                .satisfies(throwable -> Assertions.assertThat(((ObjectsNotFoundException) throwable).getIdsNotFound())
                        .containsOnly(new SubType2Id(1L, SnapshotVersion.CURRENT)));
    }

    @Test
    public void testGetWrongSubThenRightSub() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        Assertions.assertThatThrownBy(() -> store.get(new SubType2Id(1L, SnapshotVersion.CURRENT)))
                .isInstanceOf(ObjectsNotFoundException.class)
                .satisfies(throwable -> Assertions.assertThat(((ObjectsNotFoundException) throwable).getIdsNotFound())
                        .containsOnly(new SubType2Id(1L, SnapshotVersion.CURRENT)));
        store.get(new SubType1Id(1L, SnapshotVersion.CURRENT));
    }

    @Test
    public void testGetRightSubThenWrongSubAsCollection() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        store.get(Collections.singleton(new SubType1Id(1L, SnapshotVersion.CURRENT)));
        Assertions.assertThatThrownBy(() -> store.get(Collections.singleton(new SubType2Id(1L, SnapshotVersion.CURRENT))))
                .isInstanceOf(ObjectsNotFoundException.class)
                .satisfies(throwable -> Assertions.assertThat(((ObjectsNotFoundException) throwable).getIdsNotFound())
                        .containsOnly(new SubType2Id(1L, SnapshotVersion.CURRENT)));
    }

    @Test
    public void testGetWrongSubThenRightSubAsCollection() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        Assertions.assertThatThrownBy(() -> store.get(Collections.singleton(new SubType2Id(1L, SnapshotVersion.CURRENT))))
                .isInstanceOf(ObjectsNotFoundException.class)
                .satisfies(throwable -> Assertions.assertThat(((ObjectsNotFoundException) throwable).getIdsNotFound())
                        .containsOnly(new SubType2Id(1L, SnapshotVersion.CURRENT)));
        store.get(Collections.singleton(new SubType1Id(1L, SnapshotVersion.CURRENT)));
    }

    @Test
    public void testGetRightSubThenWrongSubAsOrderedCollection() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        store.getOrdered(Collections.singletonList(new SubType1Id(1L, SnapshotVersion.CURRENT)));
        Assertions.assertThatThrownBy(() -> store.getOrdered(Collections.singletonList(new SubType2Id(1L, SnapshotVersion.CURRENT))))
                .isInstanceOf(ObjectsNotFoundException.class)
                .satisfies(throwable -> Assertions.assertThat(((ObjectsNotFoundException) throwable).getIdsNotFound())
                        .containsOnly(new SubType2Id(1L, SnapshotVersion.CURRENT)));
    }

    @Test
    public void testGetWrongSubThenRightSubAsOrderedCollection() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        Assertions.assertThatThrownBy(() -> store.getOrdered(Collections.singletonList(new SubType2Id(1L, SnapshotVersion.CURRENT))))
                .isInstanceOf(ObjectsNotFoundException.class)
                .satisfies(throwable -> Assertions.assertThat(((ObjectsNotFoundException) throwable).getIdsNotFound())
                        .containsOnly(new SubType2Id(1L, SnapshotVersion.CURRENT)));
        store.getOrdered(Collections.singletonList(new SubType1Id(1L, SnapshotVersion.CURRENT)));
    }

    @Test
    public void testGetRightSubThenWrongSubAsIgnoreMissing() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        store.getIgnoreMissing(Collections.singleton(new SubType1Id(1L, SnapshotVersion.CURRENT)));
        Set<SubType2> found = store.getIgnoreMissing(Collections.singleton(new SubType2Id(1L, SnapshotVersion.CURRENT)));
        Assertions.assertThat(found).isEmpty();
    }

    @Test
    public void testGetWrongSubThenRightSubAsIgnoreMissing() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        Set<SubType2> found = store.getIgnoreMissing(Collections.singleton(new SubType2Id(1L, SnapshotVersion.CURRENT)));
        Assertions.assertThat(found).isEmpty();
        store.getIgnoreMissing(Collections.singleton(new SubType1Id(1L, SnapshotVersion.CURRENT)));
    }

    @Test
    public void testLockRightSubThenWrongSub() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        store.beginUnitOfWork();

        store.lock(new SubType1Id(1L, SnapshotVersion.CURRENT));
        Assertions.assertThatThrownBy(() -> store.lock(new SubType2Id(1L, SnapshotVersion.CURRENT)))
                .isInstanceOf(ObjectsNotFoundException.class)
                .satisfies(throwable -> Assertions.assertThat(((ObjectsNotFoundException) throwable).getIdsNotFound())
                        .containsOnly(new SubType2Id(1L, SnapshotVersion.CURRENT)));
    }

    @Test
    public void testLockWrongSubThenRightSub() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        store.beginUnitOfWork();

        Assertions.assertThatThrownBy(() -> store.lock(new SubType2Id(1L, SnapshotVersion.CURRENT)))
                .isInstanceOf(ObjectsNotFoundException.class)
                .satisfies(throwable -> Assertions.assertThat(((ObjectsNotFoundException) throwable).getIdsNotFound())
                        .containsOnly(new SubType2Id(1L, SnapshotVersion.CURRENT)));
        store.lock(new SubType1Id(1L, SnapshotVersion.CURRENT));
    }

    @Test
    public void testLockRightSubThenWrongSubAsCollection() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        store.beginUnitOfWork();

        store.lock(Collections.singleton(new SubType1Id(1L, SnapshotVersion.CURRENT)));
        Assertions.assertThatThrownBy(() -> store.lock(Collections.singleton(new SubType2Id(1L, SnapshotVersion.CURRENT))))
                .isInstanceOf(ObjectsNotFoundException.class)
                .satisfies(throwable -> Assertions.assertThat(((ObjectsNotFoundException) throwable).getIdsNotFound())
                        .containsOnly(new SubType2Id(1L, SnapshotVersion.CURRENT)));
    }

    @Test
    public void testLockWrongSubThenRightSubAsCollection() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        store.beginUnitOfWork();

        Assertions.assertThatThrownBy(() -> store.lock(Collections.singleton(new SubType2Id(1L, SnapshotVersion.CURRENT))))
                .isInstanceOf(ObjectsNotFoundException.class)
                .satisfies(throwable -> Assertions.assertThat(((ObjectsNotFoundException) throwable).getIdsNotFound())
                        .containsOnly(new SubType2Id(1L, SnapshotVersion.CURRENT)));
        store.lock(Collections.singleton(new SubType1Id(1L, SnapshotVersion.CURRENT)));
    }

    @Test
    public void testGetRightSubThenWrongSubTogether() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        Collection<BaseType> objects = store.getIgnoreMissing(Arrays.asList(
                new SubType1Id(1L, SnapshotVersion.CURRENT),
                new SubType2Id(1L, SnapshotVersion.CURRENT)
        ));

        Assertions.assertThat(objects)
                .hasSize(1)
                .allSatisfy(object -> Assertions.assertThat(object).isInstanceOf(SubType1.class));
    }

    /**
     * Dette kan i praksis ikke virke likt som {@link #testGetRightSubThenWrongSubTogether()}, da man stort sett
     * benytter {@link Set}, og da har den andre id-en forsvunnet før SKIF har muligheten til å oppdage situasjonen.
     */
    @Test
    public void testGetWrongSubThenRightSubTogether() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        Collection<BaseType> objects = store.getIgnoreMissing(Arrays.asList(
                new SubType2Id(1L, SnapshotVersion.CURRENT),
                new SubType1Id(1L, SnapshotVersion.CURRENT)
        ));

        // Ikke ideelt, men vanskelig å endre på.
        Assertions.assertThat(objects).isEmpty();
    }

    @Test
    public void testChangeType() {
        ObjectSupplier objectSupplier = new ObjectSupplier(
                new SubType1(new SubType1Id(1L, SnapshotVersion.CURRENT))
        );
        Store store = MemoryDomainHelper.createStore(objectSupplier);

        store.beginUnitOfWork();

        store.lock(new SubType1Id(1L, SnapshotVersion.CURRENT));
        SubType2 sub2 = new SubType2(new SubType2Id(1L, SnapshotVersion.CURRENT));
        store.update(sub2);

        SubType2 sub2_2 = store.get(new SubType2Id(1L, SnapshotVersion.CURRENT));
        Assertions.assertThat(sub2_2).describedAs("get(subtype2)").isSameAs(sub2);

        BubbleObject base = store.get(new BaseTypeId<>(1L, SnapshotVersion.CURRENT));
        Assertions.assertThat(base).describedAs("get(base)").isSameAs(sub2);

        Assertions.assertThatThrownBy(() -> store.get(new SubType1Id(1L, SnapshotVersion.CURRENT)))
                .isInstanceOf(ObjectsNotFoundException.class)
                .satisfies(throwable -> Assertions.assertThat(((ObjectsNotFoundException) throwable).getIdsNotFound())
                        .containsOnly(new SubType1Id(1L, SnapshotVersion.CURRENT)));
    }

}
