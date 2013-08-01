package no.statkart.skif.storetest.domain.relation.uni.direct;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.3
 */
public class X1DUniqueId<T extends X1DUnique> extends AbstractRelationTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    public X1DUniqueId(Long value) {
        super(value);
    }

    public X1DUniqueId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}