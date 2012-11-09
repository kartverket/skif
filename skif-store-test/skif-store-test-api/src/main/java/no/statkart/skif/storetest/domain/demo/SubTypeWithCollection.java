package no.statkart.skif.storetest.domain.demo;

import no.statkart.skif.storetest.domain.demo.koder.AEnumKodeId;

import java.util.HashSet;
import java.util.Set;

/**
 * Subtype med en collection.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class SubTypeWithCollection extends SubTypedBubble {
    private Set<AEnumKodeId> aEnumKoderIds = new HashSet<AEnumKodeId>();

    @Override
    public SubTypeWithCollectionId<?> getId() {
        return (SubTypeWithCollectionId<?>) super.getId();
    }

    public Set<AEnumKodeId> getaEnumKoderIds() {
        return aEnumKoderIds;
    }

    public void setaEnumKoderIds(Set<AEnumKodeId> aEnumKoderIds) {
        this.aEnumKoderIds = aEnumKoderIds;
    }
}
