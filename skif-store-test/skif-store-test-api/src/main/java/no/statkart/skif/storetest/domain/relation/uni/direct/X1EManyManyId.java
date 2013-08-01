package no.statkart.skif.storetest.domain.relation.uni.direct;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubbleId;

/**
 * @author Henrik Fredholm
 */
public class X1EManyManyId<T extends X1EManyMany> extends AbstractRelationTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    public X1EManyManyId(Long value) {
        super(value);
    }

    public X1EManyManyId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}