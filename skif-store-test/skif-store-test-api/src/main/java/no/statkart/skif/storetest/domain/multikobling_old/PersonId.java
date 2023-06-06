package no.statkart.skif.storetest.domain.multikobling_old;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class PersonId<T extends Person> extends AbstractStoreTestBubbleId<T> {
    public PersonId(Long value) {
        super(value, SnapshotVersion.CURRENT);
    }

    public PersonId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static PersonId<?> create(long value) {
        return new PersonId<>(value);
    }

}
