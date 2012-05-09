package no.statkart.skif.storetest.domain.tinglysing;

import no.statkart.skif.store.SnapshotVersion;

/**
 * @author Knut Inge Bøe
 */
public class ReseksjoneringId<T extends Reseksjonering> extends PaategningForMatrikkelenheterId<T> {
    public ReseksjoneringId(Long value) {
        super(value);
    }

    public ReseksjoneringId(Long value, SnapshotVersion snapshotVersion) {
        super(value, snapshotVersion);
    }

    public static ReseksjoneringId<?> create(long value) {
        return new ReseksjoneringId<Reseksjonering>(new Long(value));
    }
}
