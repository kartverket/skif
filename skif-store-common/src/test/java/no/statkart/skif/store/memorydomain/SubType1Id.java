package no.statkart.skif.store.memorydomain;

import no.statkart.skif.store.SnapshotVersion;

public class SubType1Id extends BaseTypeId<SubType1> {
    private static final long serialVersionUID = 1;

    public SubType1Id(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
