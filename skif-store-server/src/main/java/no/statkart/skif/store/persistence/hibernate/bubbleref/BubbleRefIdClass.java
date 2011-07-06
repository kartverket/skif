package no.statkart.skif.store.persistence.hibernate.bubbleref;

import no.statkart.matrikkel.persistens.hibernate.bubbleref.BubbleRefIdPersister;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.RootClass;
import org.hibernate.mapping.SimpleValue;

/**
 * Hibernate persistence class for a bubble reference Id.
 * <p/>
 * For instance, if <tt>AnnenPerson</tt> is a bubble that has id class <tt>PersonId</tt>. Then Hibernate
 * will automatically create a persistence class for <tt>AnnenPerson</tt> since person is defined in the
 * Hibernate mapping document. However we also need to defined a persistence class for the Id class.
 * This the role of this class.
 *
 * This class exists mainly to allow (fool) hibernate to handling bubble references. Most of the values
 * are unimportant. Rather than leaving the values to their default unitialized value, the values are taken
 * from the persistent object that the id describes.
 *
 * @author Henrik Fredholm
 */
public class BubbleRefIdClass extends RootClass {

   /**
    * Creates an id persistence class for a given persistence class.
    *
    * @param c the persistance class represented by this id class
    */
   public BubbleRefIdClass(PersistentClass c) {
      // The name is important
      String name = c.getMappedClass().getName() + "Id";
      try {
         Class cl = Class.forName(name);
      } catch (ClassNotFoundException e) {
         throw new RuntimeException(e);
      }
      setEntityName(name);
      setClassName(name);
      // Probably not important.
      setBatchSize(c.getBatchSize());
      setCacheConcurrencyStrategy(c.getCacheConcurrencyStrategy());
      setEntityPersisterClass(BubbleRefIdPersister.class);
      setDiscriminator(c.getDiscriminator());

      setDiscriminatorInsertable(c.isDiscriminatorInsertable());
      setDiscriminatorValue(c.getDiscriminatorValue());
      setDynamicInsert(false);
      setDynamicUpdate(false);
      setEmbeddedIdentifier(false);
      setExplicitPolymorphism(false);
      setForceDiscriminator(c.isForceDiscriminator());
      setIdentifier(c.getIdentifier());
      setMetaAttributes(c.getMetaAttributes());
      setMutable(false);
      setOptimisticLockMode(c.getOptimisticLockMode());
      setPolymorphic(c.isPolymorphic());
      setProxyInterfaceName(null);
      setSelectBeforeUpdate(false);
      setTable(c.getTable());
      setVersion(c.getVersion());
      setWhere(c.getWhere());

   }
}
