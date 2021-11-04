package no.statkart.skif.store.memorydomain;

import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.SnapshotVersion;

public class BaseTypeId<T extends BaseType> extends AbstractBubbleId<T> {
    private static final long serialVersionUID = 1;

    public BaseTypeId(Long value, SnapshotVersion version) {
        super(value, version);
    }

    @Override
    public Long getValue() {
        return (Long) super.getValue();
    }
}
