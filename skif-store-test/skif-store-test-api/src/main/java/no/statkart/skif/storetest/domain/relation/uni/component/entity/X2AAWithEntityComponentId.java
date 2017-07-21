package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.relation.AbstractRelationTestBubbleId;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class X2AAWithEntityComponentId<T extends X2AAWithEntityComponent> extends AbstractRelationTestBubbleId<T> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("unused")
    public X2AAWithEntityComponentId(Long value) {
        super(value);
    }

    public X2AAWithEntityComponentId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }
}