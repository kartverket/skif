package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X2CCManyId<T extends X2CCMany> extends AbstractRelationTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    public X2CCManyId(Long value) {
        super(value);
    }

    public X2CCManyId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}