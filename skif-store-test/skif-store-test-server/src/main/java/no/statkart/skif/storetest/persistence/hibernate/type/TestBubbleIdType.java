package no.statkart.skif.storetest.persistence.hibernate.type;


import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store.persistence.hibernate.type.BubbleIdType;
import no.statkart.skif.storetest.domain.TestBubbleId;

/**
 * @author Henrik Fredholm
 */
public class TestBubbleIdType extends BubbleIdType {
   public Class returnedClass() {
      return TestBubbleId.class;
   }

    @Override
    protected Object createPrototypeId(Long value, ReplicaVersion replicaVersion) {
        return new TestBubbleId(value,replicaVersion);
    }
}
