package no.statkart.skif.store2.kodelistesupport2;

import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.store2.AbstractBubbleId2;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class KodelisteIdImpl2<T extends KodelisteImpl2> extends AbstractBubbleId2<T> implements KodelisteId2<T> {

    public KodelisteIdImpl2(long value) {
        super(new Long(value));
    }

    public KodelisteIdImpl2(Long value) {
        super(value);
    }

    public KodelisteIdImpl2(String value) {
        super(Long.parseLong(value));
    }

    public KodelisteIdImpl2(Long value, ReplicaVersion replicaVersion) {
        super(value, replicaVersion);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +"{" +
                "value='" + getValue() + '\'' +
                '}';
    }

}