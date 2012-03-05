package no.statkart.skif.storetest.persistence.hibernate.type.multikobling;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.demo.FooId;
import no.statkart.skif.storetest.domain.multikobling.PersonId;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class PersonIdType extends BubbleIdType {
    @Override
    public Class returnedClass() {
        return PersonId.class;
    }

    @Override
    protected Object createPrototypeId(Object value, SnapshotVersion snapshotTime) {
        return new PersonId((Long)value, snapshotTime);
    }
}
