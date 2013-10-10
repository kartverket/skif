package no.statkart.skif.store.relation.cache.annotation;

import no.statkart.skif.store.relation.cache.RelationName;

/**
 * @author Henrik Fredholm
 */
public class RelationDescriptor {
    private final Relation relationAnnotation;
    private final RelationName relationName;

    public RelationDescriptor(Relation relationAnnotation, RelationName relationName) {
        this.relationAnnotation = relationAnnotation;
        this.relationName = relationName;
    }

    public Relation getRelationAnnotation() {
        return relationAnnotation;
    }

    public RelationName getRelationName() {
        return relationName;
    }
}
