package no.statkart.skif.storetest.domain.relation.uni.direct;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubbleId;

/**
 * @author Henrik Fredholm
 */
public class X1CManyId<T extends X1CMany> extends AbstractRelationTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    public X1CManyId(Long value) {
        super(value);
    }

    public X1CManyId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}