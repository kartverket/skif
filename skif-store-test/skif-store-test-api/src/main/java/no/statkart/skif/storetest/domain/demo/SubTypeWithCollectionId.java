package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.store.SnapshotVersion;

/**
 * Id for {@link SubTypeWithCollection}.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class SubTypeWithCollectionId<T extends SubTypeWithCollection> extends SubTypedBubbleId<T> {
    public SubTypeWithCollectionId(Long value) {
        super(value);
    }

    public SubTypeWithCollectionId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
