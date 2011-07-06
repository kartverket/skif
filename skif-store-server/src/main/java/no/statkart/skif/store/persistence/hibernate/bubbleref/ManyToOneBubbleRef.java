package no.statkart.skif.store.persistence.hibernate.bubbleref;

import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.Table;
import org.hibernate.mapping.Column;
import org.hibernate.MappingException;
import org.hibernate.type.Type;

import java.util.Iterator;

/**
 * A many-to-one association mapping that references an entity via a bubble reference. That is,
 * uses the id of the entity instead of a reference to the entity itself.
 *
 * @author Henrik Fredholm
 */
public class ManyToOneBubbleRef extends ManyToOne {
   public ManyToOneBubbleRef(Table table) throws MappingException {
      super(table);
   }

   /**
    * Copy all field but change type from <type>ManyToOne</type> to <type>ManyToOneBubbleRef</type>
    *
    * @param value
    * @throws org.hibernate.MappingException
    */
   public ManyToOneBubbleRef(ManyToOne value) throws MappingException {
      super(value.getTable());

      for (Iterator iterator = value.getColumnIterator(); iterator.hasNext();) addColumn((Column) iterator.next());
      setAlternateUniqueKey(value.isAlternateUniqueKey());
      setCascadeDeleteEnabled(value.isCascadeDeleteEnabled());
      setEmbedded(value.isEmbedded());
      setFetchMode(value.getFetchMode());
      setForeignKeyName(value.getForeignKeyName());
      setIdentifierGeneratorProperties(value.getIdentifierGeneratorProperties());
      setIdentifierGeneratorStrategy(value.getIdentifierGeneratorStrategy());
      setIgnoreNotFound(value.isIgnoreNotFound());
      setLazy(value.isLazy());
      setNullValue(value.getNullValue());
      setReferencedEntityName(value.getReferencedEntityName());
      setReferencedPropertyName(value.getReferencedPropertyName());
      setTypeName(null);
      setTypeParameters(value.getTypeParameters());
      setUnwrapProxy(value.isUnwrapProxy());
//      // Enforce that the mapped class is of type BubbleObject.
//      if (!BubbleObject.class.isAssignableFrom(associatedClass))
//         throw new MappingException("Cannot use ManyToOneBubbleRef mapping. Asociated class " + associatedClass.getName() + " is  not a subclass of BubbleObject");

   }

   public Type getType() throws MappingException {
      return new ManyToOneBubbleRefType(
            getReferencedEntityName(),
            getReferencedPropertyName(),
            isLazy(),
            isUnwrapProxy(),
            isEmbedded(),
            isIgnoreNotFound()
      );
   }

/*
   public void setTypeByReflection(Class propertyClass, String propertyName) throws MappingException {
      try {
         if (getType() == null) {
            //setType(new ManyToOneBubbleRefType(ReflectHelper.reflectedPropertyClass(propertyClass, propertyName),
            //      referencedPropertyName));
         }
      } catch (HibernateException he) {
         throw new MappingException("Problem trying to set association type by reflection", he);
      }
   }
*/
}
