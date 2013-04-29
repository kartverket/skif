package no.statkart.skif.store;

import java.io.Serializable;

/**
 * Baseklasse for EntityComponents som har mutabel ident og som derfor må ha en id for å sjekke på likhet.
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public abstract class AbstractEntityComponent implements EntityComponent, Serializable {
    private static final long serialVersionUID = 1L;

    private Long pseudoId = null;

    public abstract Long getId();

    private Long getPseudoId() {
        if (pseudoId != null) return pseudoId;
        if (getId() != null) return getId();
        return pseudoId = (long) System.identityHashCode(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AbstractEntityComponent)) return false;

        AbstractEntityComponent abstractEntityComponent = (AbstractEntityComponent) obj;
        return getPseudoId().equals(abstractEntityComponent.getPseudoId());
    }

    @Override
    public int hashCode() {
        return getPseudoId().hashCode();
    }
}
