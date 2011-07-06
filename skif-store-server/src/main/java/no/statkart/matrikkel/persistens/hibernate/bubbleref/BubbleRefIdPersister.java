package no.statkart.matrikkel.persistens.hibernate.bubbleref;

import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.persistence.hibernate.bubbleref.BubbleRefMapping;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.EntityMode;
import org.hibernate.cfg.Configuration;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.cache.CacheConcurrencyStrategy;

import java.io.Serializable;
import java.util.Map;

/**
 * This persister exists with the sole purpose of making bubble references pass Hibernate
 * persistence consistency checks. The persister tells Hibernate that object referenced via a bubble
 * reference can be assumed to exist in the database.
 * <p/>
 * When Hibernate is going to persist an object (entity), it check to see if objects referenced by
 * the entity are new (does not exist in database) or old. Hibernate makes this check to ensure that
 * reference constraints in the database are not violated. In order to make these kinds of checks
 * Hibernated needs to be able to look up the persister of the assocated object. Since
 * bubble references used the Id of the object instead of the object itself, a persister must
 * registered in Hibernated for the Id class.
 * <p/>
 * The persister registered for the Id class only needs to know whether or not the object represented
 * by the Id exists in the database or not. For bubble references it can be assumed that the object
 * always will exist in the database, since bubbles are inserted one by one and not as a connected
 * graph.
 *
 * @author Henrik Fredholm
 */
public class BubbleRefIdPersister extends SingleTableEntityPersister {
   SingleTableEntityPersister delegate;
   SessionFactoryImplementor factory;

   private static PersistentClass fix(PersistentClass persistentClass, Mapping mapping) {
      String s = persistentClass.getEntityName();
      String s2 = s.substring(0, s.length() - 2);
      PersistentClass c = ((BubbleRefMapping) mapping).getClassMapping(s2);
      c.setEntityName(s);

      return c;
//                  return persistentClass;
   }

   private static void resetFix(PersistentClass persistentClass, Mapping mapping) {
      String s = persistentClass.getEntityName();
      String s2 = s.substring(0, s.length() - 2);
      PersistentClass c = ((BubbleRefMapping) mapping).getClassMapping(s2);
      c.setEntityName(s2);
   }

   public BubbleRefIdPersister(PersistentClass persistentClass, CacheConcurrencyStrategy cache, SessionFactoryImplementor factory, Mapping mapping) throws HibernateException {
      super(fix(persistentClass, mapping), cache, factory, mapping);
      this.factory = factory;
      resetFix(persistentClass, mapping);

   }

   public EntityMode guessEntityMode(Object object) {
      return EntityMode.POJO;
   }

   public void postInstantiate() throws MappingException {
      // Do nothing. This persister is only used to tell Hibernate that objects referenced via
      // bubble ids can be assumed to exist in the database.
   }

   public Boolean isTransient(Object entity, SessionImplementor session) throws HibernateException {
      return false;
   }

   public Serializable getIdentifier(Object object, EntityMode entityMode) {
      if( object instanceof BubbleObject) {
         return ((BubbleObject) object).getId();
      } else {
         return (Serializable) object;
      }
   }
}
