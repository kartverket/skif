package no.statkart.skif.store.persistence.hibernate.bubbleref;

import org.hibernate.MappingException;
import org.hibernate.cfg.Mappings;
import org.hibernate.mapping.OneToMany;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;

/**
 * A one-to-many association mapping that references an entity via a bubble reference. That is,
 * uses the id of the entity instead of a reference to the entity itself.
 *
 * @author Henrik Fredholm
 */
public class OneToManyBubbleRef extends OneToMany {
    final Type type;

    // TODO: Mappings
    public OneToManyBubbleRef(Mappings mappings, OneToMany oneToMany) throws MappingException {
        super(mappings, oneToMany.getAssociatedClass());
        setAssociatedClass(oneToMany.getAssociatedClass());
        setReferencedEntityName(oneToMany.getReferencedEntityName());
        TypeFactory.TypeScope scope = null; // TODO
        boolean isLogicalOneToOne = false; // TODO
        type = new ManyToOneBubbleRefType(scope, oneToMany.getReferencedEntityName(), null, false, false, oneToMany.isEmbedded(), oneToMany.isIgnoreNotFound(), isLogicalOneToOne);
    }

    public Type getType() {
        return type;
    }


}
