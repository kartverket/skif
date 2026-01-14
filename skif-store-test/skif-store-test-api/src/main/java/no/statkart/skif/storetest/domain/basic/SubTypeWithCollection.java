package no.statkart.skif.storetest.domain.basic;

import java.util.HashSet;
import java.util.Set;

/**
 * Subtype med en collection.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class SubTypeWithCollection extends SubTypedBubble {
    private Set<String> tekster = new HashSet<String>();

    @Override
    public SubTypeWithCollectionId<?> getId() {
        SubTypedBubbleId<?> id = (SubTypedBubbleId<?>) super.getId();
        if (id == null) {
            return null;
        }
        if (id instanceof SubTypeWithCollectionId) {
            return (SubTypeWithCollectionId<?>) id;
        }
        return new SubTypeWithCollectionId<>(id.getValue(), id.getSnapshotVersion());
    }

    public Set<String> getTekster() {
        return tekster;
    }

    public void setTekster(Set<String> tekster) {
        this.tekster = tekster;
    }
}
