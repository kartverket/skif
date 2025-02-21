package no.statkart.skif.store;

import no.statkart.skif.domain.EqualityByFields;
import no.statkart.skif.domain.EqualsByFields;

import java.io.Serializable;
import java.lang.reflect.Field;

/**
 * Baseklasse for EntityComponents som har mutabel ident og som derfor må ha en id for å sjekke på likhet.
 *
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public abstract class AbstractEntityComponent implements EntityComponent, Serializable, EqualityByFields {
    private static final long serialVersionUID = 1L;

    private Long pseudoId = null;

    public abstract Long getId();

    private Long getPseudoId() {
        if (pseudoId != null) return pseudoId;
        if (getId() != null) return getId();
        return pseudoId = (long) System.identityHashCode(this);
    }

    @Override
    public final boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof AbstractEntityComponent)) return false;

        AbstractEntityComponent abstractEntityComponent = (AbstractEntityComponent) obj;
        return getPseudoId().equals(abstractEntityComponent.getPseudoId());
    }

    @Override
    public final int hashCode() {
        return getPseudoId().hashCode();
    }

    @Override
    public boolean fieldFilter(Field field) {
        String fieldName = field.getName();
        //noinspection SimplifiableIfStatement
        if (fieldName.equals("id") || fieldName.equals("pseudoId")) {
            return false;
        }
        return EqualityByFields.super.fieldFilter(field);
    }

    @Override
    public boolean equalsByFields(Object other, EqualsByFields comparator) {
        // Kaller equals for å sammenligne id/pseudoId.
        return equals(other) && EqualityByFields.super.equalsByFields(other, comparator);
    }
}
