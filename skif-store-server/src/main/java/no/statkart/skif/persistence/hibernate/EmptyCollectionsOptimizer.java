package no.statkart.skif.persistence.hibernate;

import no.statkart.skif.persistence.hibernate.type.EmptyCollectionsOptimizerFlagType;
import org.hibernate.EntityMode;
import org.hibernate.MappingException;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.CollectionEntry;
import org.hibernate.engine.PersistenceContext;
import org.hibernate.event.EventSource;
import org.hibernate.event.FlushEntityEvent;
import org.hibernate.event.PreLoadEvent;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.AbstractComponentType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.CustomType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Support class for optimizing Hibernates loading of empty collections. The entity (BubbleObject) being loaded is
 * expected to have a flag where the individual bits in the flag tells which collections in the
 * object that are known to be empty. These collections are then pre-initialized to empty collections
 * such that Hibernate will not need to read the collections from the database.
 * <p>
 * The objects empty collection flag must be updated whenever the contentes of one of the objects collections are changed.
 * A straight forward approach is to recalculate the flagg whenever the object is flushed. This approach, however,  can
 * lead to deadlocks and subtle raceconditions where a flush during a pure read operation causes the flag to change.
 * To avoid this the flag is not updated automatically for each flush operation, instead the flag is recalculated in a
 * StoreChain listener for boble objects that has been inserted, updated or deleted.
 * </p>
 * The flag is recalculated for all of the objects initialized collections. The bit values for uninitialized collections
 * are not recalculated since theses collections will be unchanged. The initial value of the flag can be zero. When the
 * object is updated the flag will be recalculated and bits for empty collections will be set allowing the optimization
 * to kick in the next time the object is loaded.
 * <p>
 * Note: this optimalization works because the collections always are updated through the BubbleObject and
 * never directly (and because insert, update, remove always is called on the StoreChain whenever a BubbleObject changes).
 *
 * @author Henrik Fredholm
 * //@see StoreEmptyCollectionsFlagUpdater
 */
public class EmptyCollectionsOptimizer {
   protected static Logger logger = LoggerFactory.getLogger(EmptyCollectionsOptimizer.class);
   // Common optimizer for bubbles that do not have an empty collection flag
   public final static EmptyCollectionsOptimizer NO_OPTIMIZER_MARKER = new EmptyCollectionsOptimizer();

   // true hvis en eller flere bobler fikk oppdatert deres flag siden flagget sist ble nullstillt (gjelder for alle tråde)
   public static boolean updatedFlagForOneOrMoreObjects;

   private int indexOfFlag;

   private CollectionMapper[] collectionMappers;
   private CollectionPersister[] collectionPersisters;
   private CollectionType[] collectionTypes;

    private EmptyCollectionsOptimizer() {
   }

   /**
    * Creates an EmptyCollectionOptimizer object for a given persister.
    *
    * @param indexOfFlag the index of the property that contains the flag
    * @param persister   the persister used for reading and writing the object
    */
   protected EmptyCollectionsOptimizer(int indexOfFlag, EntityPersister persister) {
      this.indexOfFlag = indexOfFlag;
      Type[] types = persister.getPropertyTypes();

      EmptyCollectionsOptimizerFlagType flagType = (EmptyCollectionsOptimizerFlagType) ((CustomType) types[indexOfFlag]).getUserType();
      int size = flagType.getProperties().size();
      collectionMappers = new CollectionMapper[size];
      collectionPersisters = new CollectionPersister[size];
      collectionTypes = new CollectionType[size];

      for( int i = 0; i < size; i++ ) {
         String role = flagType.getProperties().getProperty("bit." + i);
         if( logger.isDebugEnabled() ) {
            logger.debug("Attempting to map role for persister: " + persister.getEntityName() + " role: " + role);
         }

         CollectionMapper mapper = createCollectionMappper(types, role);
         if( mapper == null ) {
            if( role.startsWith(persister.getEntityName()) || role.startsWith(persister.getRootEntityName()) ) {
               throw new MappingException("Cannot map to collection: " + role + "for entity: " + persister.getEntityName());
            }
            if( logger.isInfoEnabled() ) {
               logger.info("Role not mapped for persister: " + persister.getEntityName() + " role: " + role + ". This is okay if subtype does not map role");
            }
         } else {
            if( logger.isDebugEnabled() ) {
               logger.debug("Role mapped for persister: " + persister.getEntityName() + " role: " + role + " index: " + i);
            }
            collectionMappers[i] = mapper;
            collectionTypes[i] = mapper.getCollectionType(types);
            collectionPersisters[i] = persister.getFactory().getCollectionPersister(collectionTypes[i].getRole());
         }
      }
   }

   /**
    * Creates a Collection mapper for a given role
    *
    * @param types type info for the object being mapped
    * @param role  name of the role to map
    * @return a CollectionMapper that can retrieve the collection for the role being mapped
    */
   private CollectionMapper createCollectionMappper(Type[] types, String role) {
      for( int i = 0; i < types.length; i++ ) {
         if( types[i] instanceof CollectionType) {
            CollectionType type = (CollectionType) types[i];
            if( type.getRole().equals(role) ) {
               if( logger.isDebugEnabled() ) {
                  logger.debug("Comparing role (match found): " + role + " to role in type: " + type.getRole());
               }
               return new CollectionMapper(i, null);
            } else {
               if( logger.isDebugEnabled() ) {
                  logger.debug("Comparing role (no match): " + role + " to role in type: " + type.getRole());
               }
            }
         } else if( types[i].isComponentType() ) {
            CollectionMapper componentMapper = createCollectionMappper(((AbstractComponentType) types[i]).getSubtypes(), role);
            if( componentMapper != null ) {
               return new CollectionMapper(i, componentMapper);
            }
         }
      }
      return null;
   }

   /**
    * Helper class that knows how to retrieve a particular collection in a BubbleObject.
    */
   private static class CollectionMapper {
      private int index;
      private CollectionMapper component;

      private CollectionMapper(int index, CollectionMapper component) {
         this.index = index;
         this.component = component;
      }

      /**
       * Returns the collection mapped by this CollectionMapper. There is no guarantie that this collection
       * will be a subtype of PersistentCollection.
       *
       * @param values
       * @param types
       * @param entityMode
       */
      Collection getCollection(Object[] values, Type[] types, EntityMode entityMode) {
         if( component != null ) {
            AbstractComponentType type = (AbstractComponentType) types[index];
            Type[] componentTypes = type.getSubtypes();
            Object[] componentValues = type.getPropertyValues(values[index], entityMode);
            return component.getCollection(componentValues, componentTypes, entityMode);
         } else {
            return (Collection) values[index];
         }
      }

      CollectionType getCollectionType(Type[] types) {
         if( component != null ) {
            AbstractComponentType type = (AbstractComponentType) types[index];
            Type[] componentTypes = type.getSubtypes();
            return component.getCollectionType(componentTypes);
         } else {
            return (CollectionType) types[index];
         }
      }
   }

   public static EmptyCollectionsOptimizer createOptimizer(EntityPersister persister) {
      Type[] types = persister.getPropertyTypes();
      for( int i = 0; i < types.length; i++ ) {
         if( types[i] instanceof CustomType && ((CustomType) types[i]).getUserType() instanceof EmptyCollectionsOptimizerFlagType )
         {
            return new EmptyCollectionsOptimizer(i, persister);
         }
      }
      return NO_OPTIMIZER_MARKER;
   }

   /**
    * Initializes all collections that are known to be empty
    */
   public void initializeEmptyCollections(PreLoadEvent event, EntityPersister persister) {
      if( this == NO_OPTIMIZER_MARKER ) return;

      Object[] values = event.getState();
      Type[] types = persister.getPropertyTypes();
      EventSource eventSource = event.getSession();
      EntityMode entityMode = eventSource.getEntityMode();
      PersistenceContext persistenceContext = eventSource.getPersistenceContext();

      // Each bit in the flag corresponds to a collection. If the bit is set the collection is known to be empty.
      long flag = (Long) values[indexOfFlag];

      if( logger.isDebugEnabled() ) {
         logger.debug(String.format("Optimizing initialization of collections for entity: %s using empty collection flag: %d (octal: 0%o)", MessageHelper.infoString(persister, event.getId(), event.getSession().getFactory()), flag, flag));
      }

      for( int i = 0; i < collectionMappers.length; i++ ) {
         if( collectionMappers[i] == null ) {
            if( logger.isDebugEnabled() ) {
               logger.debug("Collection for bit " + i + " is not mapped for entity subtype: " + persister.getEntityName());
            }
            continue; // i'th bit in the flag does not map to a collection for this subtype
         }

         long bit_i = 1 << i;
         if( (flag & bit_i) == bit_i ) {
            // The object is beeing read from the database, thus the collection is guaranteed to be a PersistentCollection
            PersistentCollection collection = (PersistentCollection) collectionMappers[i].getCollection(values, types, entityMode);

            if( !collection.wasInitialized() ) {
               if( logger.isDebugEnabled() ) {
                  logger.debug("Collection for bit " + i + " is known to be empty. Initializing collection without going to the database: [" + collectionTypes[i].getRole() + "]");
               }
               // Initialize the collection to 0 elements without going to the database
               collection.beginRead();
               collection.beforeInitialize(collectionPersisters[i], 0);
               collection.endRead();

               // Set the snapshot of the collection such that this also will be initialized to an empty collection.
               CollectionEntry ce = persistenceContext.getCollectionEntry(collection);
               ce.postInitialize(collection);
            } else {
               if( logger.isDebugEnabled() ) {
                  logger.debug("Collection for bit " + i + " already initialized. No need to optimize initialization: [" + collectionTypes[i].getRole() + "]");
               }
            }
         } else {
            if( logger.isDebugEnabled() ) {
               logger.debug("Collection for bit " + i + " may contain elements and must be read from database. Cannot optimize initialization: [" + collectionTypes[i].getRole() + "]");
            }
         }
      }
   }

   /**
    * @param event
    * @param persister
    * @deprecated the flag should not be updated on each fluch
    */
   public void updateEmptyCollectionFlag(FlushEntityEvent event, EntityPersister persister) {
      if( this == NO_OPTIMIZER_MARKER ) return;

      if( logger.isDebugEnabled() ) {
         logger.debug(String.format("Calculating empty collection flag entity: %s", MessageHelper.infoString(persister, event.getEntityEntry().getId(), event.getSession().getFactory())));
      }

      Object entity = event.getEntity();
      EntityMode entityMode = event.getSession().getEntityMode();

      updateEmptyCollectionFlag(persister, entity, entityMode);
   }

   public void updateEmptyCollectionFlag(EntityPersister persister, Object entity, EntityMode entityMode) {
      if( this == NO_OPTIMIZER_MARKER ) return;

      Object[] values = persister.getPropertyValues(entity, entityMode);
      Type[] types = persister.getPropertyTypes();
      long flag = (Long) values[indexOfFlag];
      long oldFlag = flag;

      for( int i = 0; i < collectionMappers.length; i++ ) {
         if( collectionMappers[i] == null ) {
            if( logger.isDebugEnabled() ) {
               logger.debug("Collection for bit " + i + " is not mapped for entity subtype");
            }
            continue; // i'th bit in the flag does not map to a collection for this subtype
         }

         // During flush there is no guarantee that the collection is an instance of PersistentCollection.
         Collection collection = collectionMappers[i].getCollection(values, types, entityMode);
         boolean wasInitialized = collection instanceof PersistentCollection ? ((PersistentCollection) collection).wasInitialized() : true;

         if( wasInitialized ) {
            // Need to update flag for i'th bit.
            long bit_i = 1 << i;
            if( (collection.size() == 0) ) {
               // set i'th bit in flag
               if( logger.isDebugEnabled() ) {
                  logger.debug("Collection for bit " + i + " is empty. Setting bit in flag for [" + collectionTypes[i].getRole() + "]");
               }
               flag |= bit_i;
            } else {
               // clear i'th bit  in flag
               if( logger.isDebugEnabled() ) {
                  logger.debug("Collection for bit " + i + " is non empty. Clearing bit in flag for: [" + collectionTypes[i].getRole() + "]");
               }
               flag &= ~bit_i;
            }
         } else {
            // Leave flag untouched for i'th bit. Collection has not been read and is therefore unchanged.
            if( logger.isDebugEnabled() ) {
               logger.debug("Collection for bit " + i + " is not initialized. Leaving bit in flag unchanged for: [" + collectionTypes[i].getRole() + "]");
            }
         }
      }
      if( flag != oldFlag ) {
         if( logger.isDebugEnabled() ) {
            logger.debug(String.format("Empty collection flag was modified for entity. Old flag: %d (octal: 0%o) new flag: %d (octal: 0%o)", oldFlag, oldFlag, flag, flag));
         }
         persister.setPropertyValue(entity, indexOfFlag, new Long(flag), entityMode);
         updatedFlagForOneOrMoreObjects = true;
      } else {
         if( logger.isDebugEnabled() ) {
            logger.debug(String.format("Empty collection flag is unchanged for entity. Flag: %d (octal: 0%o)", flag, flag));
         }
      }
   }
}
