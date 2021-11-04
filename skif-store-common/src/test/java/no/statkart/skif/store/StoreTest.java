package no.statkart.skif.store;

import no.statkart.skif.exception.ObjectsNotFoundException;
import no.statkart.skif.store.memorydomain.MemoryDomainHelper;
import no.statkart.skif.store.memorydomain.ObjectSupplier;
import no.statkart.skif.store.memorydomain.SubType1;
import no.statkart.skif.store.memorydomain.SubType1Id;
import org.assertj.core.api.Assertions;
import org.testng.annotations.Test;

import java.util.Collections;
import java.util.Set;

public class StoreTest {
    @Test
    public void getDeleted() {
        SubType1Id id = new SubType1Id(1L, SnapshotVersion.CURRENT);

        Store store = MemoryDomainHelper.createStore(
                new ObjectSupplier(
                        new SubType1(id)
                )
        );

        store.beginUnitOfWork();

        SubType1 object = store.lock(id);
        store.delete(object);

        Assertions.assertThatThrownBy(() -> store.get(id))
                .isInstanceOf(ObjectsNotFoundException.class)
                .satisfies(throwable -> Assertions.assertThat(((ObjectsNotFoundException) throwable).getIdsNotFound())
                        .containsOnly(id));
    }

    @Test
    public void getDeletedAsCollection() {
        SubType1Id id = new SubType1Id(1L, SnapshotVersion.CURRENT);

        Store store = MemoryDomainHelper.createStore(
                new ObjectSupplier(
                        new SubType1(id)
                )
        );

        store.beginUnitOfWork();

        SubType1 object = store.lock(id);
        store.delete(object);

        Assertions.assertThatThrownBy(() -> store.get(Collections.singleton(id)))
                .isInstanceOf(ObjectsNotFoundException.class)
                .satisfies(throwable -> Assertions.assertThat(((ObjectsNotFoundException) throwable).getIdsNotFound())
                        .containsOnly(id));
    }

    @Test
    public void getDeletedAsOrderedCollection() {
        SubType1Id id = new SubType1Id(1L, SnapshotVersion.CURRENT);

        Store store = MemoryDomainHelper.createStore(
                new ObjectSupplier(
                        new SubType1(id)
                )
        );

        store.beginUnitOfWork();

        SubType1 object = store.lock(id);
        store.delete(object);

        Assertions.assertThatThrownBy(() -> store.getOrdered(Collections.singletonList(id)))
                .isInstanceOf(ObjectsNotFoundException.class)
                .satisfies(throwable -> Assertions.assertThat(((ObjectsNotFoundException) throwable).getIdsNotFound())
                        .containsOnly(id));
    }

    @Test
    public void getDeletedIgnoreMissing() {
        SubType1Id id = new SubType1Id(1L, SnapshotVersion.CURRENT);

        Store store = MemoryDomainHelper.createStore(
                new ObjectSupplier(
                        new SubType1(id)
                )
        );

        store.beginUnitOfWork();

        SubType1 object = store.lock(id);
        store.delete(object);

        Set<SubType1> found = store.getIgnoreMissing(Collections.singleton(id));
        Assertions.assertThat(found).isEmpty();
    }

    @Test
    public void lockDeleted() {
        SubType1Id id = new SubType1Id(1L, SnapshotVersion.CURRENT);

        Store store = MemoryDomainHelper.createStore(
                new ObjectSupplier(
                        new SubType1(id)
                )
        );

        store.beginUnitOfWork();

        SubType1 object = store.lock(id);
        store.delete(object);

        Assertions.assertThatThrownBy(() -> store.lock(id))
                .isInstanceOf(ObjectsNotFoundException.class)
                .satisfies(throwable -> Assertions.assertThat(((ObjectsNotFoundException) throwable).getIdsNotFound())
                        .containsOnly(id));
    }

    @Test
    public void lockDeletedAsCollection() {
        SubType1Id id = new SubType1Id(1L, SnapshotVersion.CURRENT);

        Store store = MemoryDomainHelper.createStore(
                new ObjectSupplier(
                        new SubType1(id)
                )
        );

        store.beginUnitOfWork();

        SubType1 object = store.lock(id);
        store.delete(object);

        Assertions.assertThatThrownBy(() -> store.lock(Collections.singleton(id)))
                .isInstanceOf(ObjectsNotFoundException.class)
                .satisfies(throwable -> Assertions.assertThat(((ObjectsNotFoundException) throwable).getIdsNotFound())
                        .containsOnly(id));
    }
}
