package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X2BBOneId<T extends X2BBOne> extends AbstractRelationTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    public X2BBOneId(Long value) {
        super(value);
    }

    public X2BBOneId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}