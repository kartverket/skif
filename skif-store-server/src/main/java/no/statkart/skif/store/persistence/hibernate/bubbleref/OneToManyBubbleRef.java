package no.statkart.skif.store.persistence.hibernate.bubbleref;

import org.hibernate.mapping.Value;
import org.hibernate.mapping.OneToMany;
import org.hibernate.MappingException;
import org.hibernate.type.Type;
import org.hibernate.type.ManyToOneType;

/**
 * A one-to-many association mapping that references an entity via a bubble reference. That is,
 * uses the id of the entity instead of a reference to the entity itself.
 *
 * @author Henrik Fredholm
 */
public class OneToManyBubbleRef extends OneToMany {
   final Type type;

   public OneToManyBubbleRef(OneToMany oneToMany) throws MappingException {
      super(oneToMany.getAssociatedClass());
      setAssociatedClass(oneToMany.getAssociatedClass());
      setReferencedEntityName(oneToMany.getReferencedEntityName());
      type = new ManyToOneBubbleRefType(oneToMany.getReferencedEntityName(), null, false, false, oneToMany.isEmbedded(), oneToMany.isIgnoreNotFound());
   }

   public Type getType() {
      return type;
   }


}
