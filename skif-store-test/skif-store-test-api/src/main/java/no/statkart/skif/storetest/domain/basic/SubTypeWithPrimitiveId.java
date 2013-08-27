package no.statkart.skif.storetest.domain.basic;

import no.statkart.skif.store.SnapshotVersion;

/**
 * Id for {@link SubTypeWithPrimitive}.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class SubTypeWithPrimitiveId<T extends SubTypeWithPrimitive> extends SubTypedBubbleId<T> {
    public SubTypeWithPrimitiveId(Long value) {
        super(value);
    }

    public SubTypeWithPrimitiveId(Long value, SnapshotVersion version) {
        super(value, version);
    }
}
