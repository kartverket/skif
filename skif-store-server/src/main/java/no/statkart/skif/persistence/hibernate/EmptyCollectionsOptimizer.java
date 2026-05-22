package no.statkart.skif.persistence.hibernate;

import no.statkart.skif.persistence.hibernate.type.EmptyCollectionsOptimizerFlagType;
import no.statkart.skif.store.BubbleObject;
import org.hibernate.MappingException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.PreLoadEvent;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.type.CollectionType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.CustomType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Support class for optimizing Hibernates loading of empty collections. The entity (BubbleObject) being loaded is
 * expected to have a flag where the individual bits in the flag tells which collections in the
 * bubble that are known to be empty. Note the collections can also be located on entities owned by the bubble.
 * These collections are then pre-initialized to empty collections such that Hibernate will not need to read the
 * collections from the database through queries.
 * <p>
 * The flag will be maintained automatically by the system such that it is always reflects which collections that
 * are empty. It is however okay to set the flag to 0 if collections are modified outside hibernate or for some
 * reason is not trusted. This will force Hibernate to execute SQLs when loading collections that are empty. The flag
 * will be recalculated automatically the next time Hibernate is used for updating the bubble. The flag is only
 * calculated for materialized collections, so in order to update the flag that has been reset the bubble should be
 * fully initialized before update is called as shown in the example below.
 * <pre>
 *     BubbleObject bubble = store.lock(id);
 *     store.ensureFullyLoaded(bubble);
 *     store.update(bubble);
 * </pre>
 * <p>
 * The empty collections flag for each bubble must be updated whenever the contents of one of the bubbles collections are changed.
 * A straight forward approach is to recalculate the flag whenever the bubble is flushed. This approach, however, can
 * lead to deadlocks and subtle race conditions where a flush during a pure read operation can causes the flag to be
 * updated because it is outdated with respect to a collection that just has been materialized. This can happen for
 * several reasons. The most likely is that the flag has been reset to 0 (which is okay) and the bubble contains empty
 * collections. If the flag is recalculated it will be changed to reflect this and can cause an update of the bubble even
 * though the bubble is just being read. An unwanted update of the flag can also happen due to a race condition where the
 * bubble (including the value of the flag) has been read, but a collection is still unmaterialized. Then the
 * unmaterialized collection is changed by another process in a different transaction that is committed. Then the
 * collection is materialized by the bubble that has the old value of the flag. In this case the flag should not be
 * updated since the bubble has not been locked for update - if it had been locked for update then the other process
 * would not have been able to alter the collection. In this case the bubble that was read will have an inconsistent
 * view of the collection. This has nothing to do with the empty collections flag, but is just how Hibernate works
 * in general. Inconsistent reads happens, but they should not give rise to updates.
 * <p>
 * To avoid the problem above, the flag is not updated automatically for each flush operation. Instead the
 * flag is recalculated by Hibernate listeners that works on bubbles that have been saved or updated. The
 * system will have exclusive write access for such bubbles (as the should be locked before updates occur) so an
 * update of the flag will not cause deadlocks when the bubble is flushed to the database. Note that it is important
 * that these flag updates happens whenever these listener events occur and not only when {@code Store.insert} or {@code Store.update} are
 * explicitly called. This is because there is no guarantee that Store.update calls always are executed as the last
 * operation after all updates.
 * </p>
 * The flag is recalculated for the materialized collections in the bubble only. The bit values for unmaterialized
 * collections are left unchanged since theses collections are unmodified. The value of the flag can always be
 * initialized to zero. The flag will remain zero until the collections are updated or the bubble is explicitly updated.
 * The flag will then be recalculated and bits for empty collections will be set allowing the optimization to kick in
 * the next time the object is loaded.
 * <p>
 * Note: This optimization works because collections are always updated through bubble instance and
 * never directly through custom SQLs.
 *
 * @since 2.11
 * @author Henrik Fredholm
 */
public class EmptyCollectionsOptimizer {
    protected static Logger logger = LoggerFactory.getLogger(EmptyCollectionsOptimizer.class);
    // Common optimizer for bubbles that do not have an empty collection flag
    private final static EmptyCollectionsOptimizer NO_OPTIMIZER_MARKER = new EmptyCollectionsOptimizer();

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

        for (int i = 0; i < size; i++) {
            String role = flagType.getProperties().getProperty("bit." + i).trim();
            if (logger.isDebugEnabled()) {
                logger.debug("Attempting to map role for persister: " + persister.getEntityName() + " role: " + role);
            }

            CollectionMapper mapper = createCollectionMappper(types, role);
            if (mapper == null) {
                if (role.startsWith(persister.getEntityName() + ".") || role.startsWith(persister.getRootEntityName() + ".")) {
                    throw new MappingException("Cannot map to collection: " + role + " for entity: " + persister.getEntityName());
                }
                if (logger.isInfoEnabled()) {
                    logger.info("Role was not mapped for persister: " + persister.getEntityName() + " role: " + role + ". This is okay if the subtype does not map role");
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Role mapped for persister: " + persister.getEntityName() + " role: " + role + " index: " + i);
                }
                collectionMappers[i] = mapper;
                collectionTypes[i] = mapper.getCollectionType(types);
                collectionPersisters[i] = persister.getFactory().getMappingMetamodel().getCollectionDescriptor(collectionTypes[i].getRole());
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
        for (int i = 0; i < types.length; i++) {
            if (types[i] instanceof CollectionType) {
                CollectionType type = (CollectionType) types[i];
                if (type.getRole().equals(role)) {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Comparing role (match found): " + role + " to role in type: " + type.getRole());
                    }
                    return new CollectionMapper(i, null);
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug("Comparing role (no match): " + role + " to role in type: " + type.getRole());
                    }
                }
            } else if (types[i].isComponentType()) {
                CollectionMapper componentMapper = createCollectionMappper(((CompositeType) types[i]).getSubtypes(), role);
                if (componentMapper != null) {
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
         * Returns the collection mapped by this CollectionMapper. There is no guarantee that this collection
         * will be a subtype of PersistentCollection.
         */
        Collection getCollection(Object[] values, Type[] types) {
            if (component != null) {
                CompositeType type = (CompositeType) types[index];
                Type[] componentTypes = type.getSubtypes();
                Object[] componentValues = type.getPropertyValues(values[index]);
                return component.getCollection(componentValues, componentTypes);
            } else {
                return (Collection) values[index];
            }
        }

        CollectionType getCollectionType(Type[] types) {
            if (component != null) {
                CompositeType type = (CompositeType) types[index];
                Type[] componentTypes = type.getSubtypes();
                return component.getCollectionType(componentTypes);
            } else {
                return (CollectionType) types[index];
            }
        }
    }

    public static EmptyCollectionsOptimizer createOptimizer(EntityPersister persister) {
        Type[] types = persister.getPropertyTypes();
        for (int i = 0; i < types.length; i++) {
            if (types[i] instanceof CustomType && ((CustomType) types[i]).getUserType() instanceof EmptyCollectionsOptimizerFlagType) {
                return new EmptyCollectionsOptimizer(i, persister);
            }
        }
        return NO_OPTIMIZER_MARKER;
    }

    /**
     * Initializes all collections that are known to be empty
     */
    public void initializeEmptyCollections(PreLoadEvent event, EntityPersister persister) {
        if (this == NO_OPTIMIZER_MARKER) return;

        Object[] values = event.getState();
        Type[] types = persister.getPropertyTypes();
        EventSource eventSource = event.getSession();
        PersistenceContext persistenceContext = eventSource.getPersistenceContext();

        // Each bit in the flag corresponds to a collection. If the bit is set the collection is known to be empty.
        long flag = (Long) values[indexOfFlag];

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Optimizing initialization of collections using empty collection flag %d (octal: 0%o) for entity: %s", flag, flag, getEntityInfoString(event, persister)));
        }

        for (int i = 0; i < collectionMappers.length; i++) {
            if (collectionMappers[i] == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Collection for bit %d is not mapped for entity subtype: %s"), i, getEntityInfoString(event, persister));
                }
                continue; // i'th bit in the flag does not map to a collection for this subtype
            }

            long bit_i = 1 << i;
            if ((flag & bit_i) == bit_i) {
                // The object is being read from the database, thus the collection is guaranteed to be a PersistentCollection
                PersistentCollection collection = (PersistentCollection) collectionMappers[i].getCollection(values, types);

                if (!collection.wasInitialized()) {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Collection for bit %d is known to be empty. Initializing collection [%s] without database queries for entity: %s", i, collectionTypes[i].getRole(), getEntityInfoString(event, persister)));
                    }
                    // Initialize the collection to 0 elements without querying the database
                    collection.initializeEmptyCollection(collectionPersisters[i]);

                    // Set the snapshot of the collection such that this also will be initialized to an empty collection.
                    CollectionEntry ce = persistenceContext.getCollectionEntry(collection);
                    ce.postInitialize(collection, eventSource);
                } else {
                    if (logger.isDebugEnabled()) {
                        logger.debug(String.format("Collection for bit %d is already initialized. No need to optimize initialization of collection [%s] for entity: %s", i, collectionTypes[i].getRole(), getEntityInfoString(event, persister)));
                    }
                }
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Collection for bit %d may contain elements and must be read from database. Cannot optimize initialization of collection [%s] for entity: %s", i, collectionTypes[i].getRole(), getEntityInfoString(event, persister)));
                }
            }
        }
    }

    public void updateEmptyCollectionFlag(EntityPersister persister, BubbleObject entity) {
        if (this == NO_OPTIMIZER_MARKER) return;

        Object[] values = persister.getPropertyValues(entity);
        Type[] types = persister.getPropertyTypes();
        long flag = (Long) values[indexOfFlag];
        long oldFlag = flag;

        for (int i = 0; i < collectionMappers.length; i++) {
            if (collectionMappers[i] == null) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Collection for bit %d is not mapped for entity subtype: %s", i, getEntityInfoString(entity)));
                }
                continue; // i'th bit in the flag does not map to a collection for this subtype
            }

            // During flush there is no guarantee that the collection is an instance of PersistentCollection.
            Collection collection = collectionMappers[i].getCollection(values, types);
            boolean wasInitialized = !(collection instanceof PersistentCollection) || ((PersistentCollection) collection).wasInitialized();

            if (wasInitialized) {
                // Need to update flag for i'th bit.
                long bit_i = 1 << i;
                if ((collection.size() == 0)) {
                    // set i'th bit in flag
                    if (logger.isDebugEnabled()) {
                        logger.debug("Collection for bit " + i + " is empty. Setting bit in flag for collection [" + collectionTypes[i].getRole() + "]");
                    }
                    flag |= bit_i;
                } else {
                    // clear i'th bit  in flag
                    if (logger.isDebugEnabled()) {
                        logger.debug("Collection for bit " + i + " is non empty. Clearing bit in flag for collection [" + collectionTypes[i].getRole() + "]");
                    }
                    flag &= ~bit_i;
                }
            } else {
                // Leave flag untouched for i'th bit. Collection has not been read and is therefore unchanged.
                if (logger.isDebugEnabled()) {
                    logger.debug("Collection for bit " + i + " is not initialized. Leaving bit in flag unchanged for collection [" + collectionTypes[i].getRole() + "]");
                }
            }
        }
        if (flag != oldFlag) {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Empty collection flag was modified for entity. Old flag: %d (octal: 0%o) new flag: %d (octal: 0%o)", oldFlag, oldFlag, flag, flag));
            }
            persister.setPropertyValue(entity, indexOfFlag, flag);
        } else {
            if (logger.isDebugEnabled()) {
                logger.debug(String.format("Empty collection flag is unchanged for entity. Flag: %d (octal: 0%o)", flag, flag));
            }
        }
    }

    private String getEntityInfoString(PreLoadEvent event, EntityPersister persister) {
        return MessageHelper.infoString(persister, event.getId(), event.getSession().getFactory());
    }

    private String getEntityInfoString(BubbleObject entity) {
        return MessageHelper.infoString(entity.getClass().getName(), entity.getId());
    }
}
