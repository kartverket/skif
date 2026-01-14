package no.statkart.skif.store.persistence.hibernate;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.EntityComponent;
import no.statkart.skif.util.CopyHelper;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.Session;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.collection.spi.PersistentMap;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.CascadeStyles;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.Assigned;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.CollectionType;
import org.hibernate.type.ComponentType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.CustomType;
import org.hibernate.type.EntityType;
import org.hibernate.type.MapType;
import org.hibernate.type.Type;
import org.hibernate.type.BasicType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static no.statkart.skif.store.persistence.hibernate.HibernateHelper.erAvTypeSomIkkeSkalInitialiseresVidere;

/**
 * Koden i denne klassen er trukke ut fra {@code HibernatePersistenceSessionMasterImpl} og inneholder så få endringer
 * som mulig i forhold til denne klasse for gjøre det enklere å sammenlikne mot gammel implementasjon ifm oppgradering
 * til Hibernate 5.x og denne implementasjon.
 *
 * TODO: Når oppgraderingen er gjennomført bør det ryddes opp i denne koden og alle TODO, inkl denne, bør resolves og fjernes.
 */
@SuppressWarnings("Duplicates")
public class HibernateDetachedSupport {
    private static final Logger logger = LoggerFactory.getLogger(HibernateDetachedSupport.class);
    private final HibernateLazySupport lazyInitializer;

    public HibernateDetachedSupport(HibernateLazySupport lazyInitializer) {
        this.lazyInitializer = lazyInitializer;
    }

    private Session session() {
        return lazyInitializer.session();
    }

    /**
     * Brukes til å identifisere entity components som er nye etter at de har fått tildelt id. For å få batching av
     * bobler til å virke er det nødvendig at entities insertes før boblene insertes eller oppdateres.
     * Derved får entity components tildelt id og boblen kan ikke lengre se at den er ny.
     * Bruker IdentityHashSet fordi det er mest logisk å bruke dette en id for sammenlikning
     */
    protected Set<EntityComponent> newlyInsertedComponents = Sets.newIdentityHashSet();

    /**
     * Oppdaterer underliggende hibernate session med verdiene fra {@code bubbleObject}. Dersom {@code bubbleObject} allerede er
     * knyttet til sessionen returneres samme instans. Dersom {@code bubbleObject} ikke er knyttet til sessionen kastes den gamle
     * instansen og den nye knyttes til sessionen. I den forbindelse gjøres det et større rydde arbeide for å sikre at entity
     * componenter som ikke lengre er i bruk blir slettet automatisk. Videre blir alle collections konvertert til
     * PersistentCollections og snapshot fra opprinnelig objekt blir satt slik at Hibernate kan slette referanse som
     * ikke lenger er en del av collectionen.
     * <p>
     * Endringer blir først utført ved senere kall til flush()
     */
    public <T extends BubbleObject> void update(T bubbleObject) {
        List<Multimap<Class<? extends EntityComponent>, EntityComponent>> orphanOneToOneEntityComponents = Lists.newArrayList();
        try {
            Session session = session();
            boolean attachedInstance = isAttachedInstance(session, bubbleObject);
            checkForSubtypeChangedEntityComponentsOnAttachedInstance(bubbleObject);
            assignPersistentCollectionsAndConvertObjectIfTypeChangedAndEvictOtherInstance(bubbleObject, orphanOneToOneEntityComponents);
            session.update(bubbleObject); // Viktig at class-mapping inneholder 'select-before-update="true"'. Dette bør settes automatisk ved konfigurasjon av hibernate session factory.
            for (int i = orphanOneToOneEntityComponents.size() - 1; i >= 0; i--) {
                Multimap<Class<? extends EntityComponent>, EntityComponent> multimap = orphanOneToOneEntityComponents.get(i);
                for (Object orphanEntity : multimap.values()) {
                    session.delete(orphanEntity);
                }
            }
            if (!attachedInstance && session instanceof org.hibernate.internal.SessionImpl) {
                session.flush();
            }
        } catch (HibernateException | SQLException e) {
            throw new ImplementationException("Update failed for " + bubbleObject, e);
        }
    }

    private boolean isAttachedInstance(Session session, BubbleObject bubbleObject) {
        if (!(session instanceof SessionImplementor)) {
            return false;
        }
        if (!(session instanceof org.hibernate.internal.SessionImpl)) {
            return false;
        }
        PersistenceContext persistenceContext = ((SessionImplementor) session).getPersistenceContext();
        return persistenceContext != null && persistenceContext.getEntry(bubbleObject) != null;
    }

    private void checkForSubtypeChangedEntityComponentsOnAttachedInstance(BubbleObject bubbleObject) {
        Session session = session();
        if (!(session instanceof SessionImplementor)) {
            return;
        }
        if (!(session instanceof org.hibernate.internal.SessionImpl)) {
            return;
        }
        PersistenceContext persistenceContext = ((SessionImplementor) session).getPersistenceContext();
        EntityEntry entityEntry = persistenceContext.getEntry(bubbleObject);
        Object[] loadedState = entityEntry != null ? entityEntry.getLoadedState() : null;
        EntityPersister persister = (EntityPersister) HibernateHelper.getClassMetadata(session, bubbleObject);
        Type[] types = persister.getPropertyTypes();
        int loadedLength = loadedState != null ? loadedState.length : 0;
        int length = Math.min(types.length, Math.max(loadedLength, types.length));
        for (int i = 0; i < length; i++) {
            Type type = types[i];
            if (!type.isEntityType()) {
                continue;
            }
            Object current = persister.getPropertyValue(bubbleObject, i);
            Object loaded = loadedState != null && i < loadedState.length ? loadedState[i] : null;
            if (!(current instanceof EntityComponent)) {
                continue;
            }
            EntityComponent currentComponent = (EntityComponent) current;
            if (currentComponent.getId() != null) {
                verifyPersistedEntityComponentType((EntityType) type, session, currentComponent);
            }
            EntityComponent oldComponent = resolveLoadedEntityComponent(loaded, (EntityType) type, session, currentComponent);
            if (oldComponent != null) {
                Class<?> oldClass = resolveEntityComponentClass(oldComponent);
                Class<?> currentClass = resolveEntityComponentClass(currentComponent);
                if (Objects.equals(currentComponent.getId(), oldComponent.getId())
                        && !Objects.equals(oldClass, currentClass)) {
                    throw new ImplementationException("Attempted to change class from " + oldComponent.getClass().getName()
                            + " to " + currentComponent.getClass().getName() + " for id " + currentComponent.getId());
                }
                if (oldComponent == currentComponent && currentComponent.getId() != null) {
                    verifyPersistedEntityComponentType((EntityType) type, session, currentComponent);
                }
            } else if (currentComponent.getId() != null) {
                verifyPersistedEntityComponentType((EntityType) type, session, currentComponent);
            }
        }
    }

    private EntityComponent resolveLoadedEntityComponent(Object loaded, EntityType entityType, Session session, EntityComponent currentComponent) {
        if (loaded instanceof HibernateProxy) {
            Object implementation = ((HibernateProxy) loaded).getHibernateLazyInitializer().getImplementation();
            if (implementation instanceof EntityComponent) {
                return (EntityComponent) implementation;
            }
        } else if (loaded instanceof EntityComponent) {
            return (EntityComponent) loaded;
        } else if (loaded instanceof Serializable) {
            EntityComponent byLoadedId = loadEntityComponentById(entityType, session, (Serializable) loaded, currentComponent);
            if (byLoadedId != null) {
                return byLoadedId;
            }
        }
        Long currentId = currentComponent != null ? currentComponent.getId() : null;
        if (currentId == null) {
            return null;
        }
        return loadEntityComponentById(entityType, session, currentId, currentComponent);
    }

    private EntityComponent loadEntityComponentById(EntityType entityType, Session session, Serializable id, EntityComponent currentComponent) {
        Object oldEntity = session.get(entityType.getAssociatedEntityName(), id);
        if (oldEntity == currentComponent) {
            EntityComponent freshEntity = loadEntityComponentByIdIgnoringCache(entityType, session, id);
            if (freshEntity != null) {
                return freshEntity;
            }
        }
        if (oldEntity instanceof EntityComponent) {
            return (EntityComponent) oldEntity;
        }
        return null;
    }

    private EntityComponent loadEntityComponentByIdIgnoringCache(EntityType entityType, Session session, Serializable id) {
        try (org.hibernate.StatelessSession statelessSession = session.getSessionFactory().openStatelessSession()) {
            Object oldEntity = statelessSession.get(entityType.getAssociatedEntityName(), id);
            if (oldEntity instanceof EntityComponent) {
                return (EntityComponent) oldEntity;
            }
            return null;
        }
    }

    private void verifyPersistedEntityComponentType(EntityType entityType, Session session, EntityComponent currentComponent) {
        if (session instanceof SessionImplementor) {
            SessionImplementor sessionImplementor = (SessionImplementor) session;
            EntityPersister entityPersister = sessionImplementor.getSessionFactory()
                    .getRuntimeMetamodels()
                    .getMappingMetamodel()
                    .getEntityDescriptor(entityType.getAssociatedEntityName());
            if (entityPersister instanceof org.hibernate.persister.entity.SingleTableEntityPersister) {
                org.hibernate.persister.entity.SingleTableEntityPersister singleTablePersister =
                        (org.hibernate.persister.entity.SingleTableEntityPersister) entityPersister;
                String[] idColumns = singleTablePersister.getIdentifierColumnNames();
                if (idColumns.length == 1) {
                    String discriminatorColumn = singleTablePersister.getDiscriminatorColumnName();
                    String tableName = singleTablePersister.getRootTableName();
                    String sql = "select " + discriminatorColumn + " from " + tableName + " where " + idColumns[0] + " = ?";
                    String discriminatorValue = sessionImplementor.doReturningWork(connection -> {
                        try (PreparedStatement statement = connection.prepareStatement(sql)) {
                            statement.setObject(1, currentComponent.getId());
                            try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                                if (!resultSet.next()) {
                                    return null;
                                }
                                String value = resultSet.getString(1);
                                return value != null ? value.trim() : null;
                            }
                        }
                    });
                    if (discriminatorValue != null) {
                        if (!discriminatorValue.equals(currentComponent.getClass().getSimpleName())) {
                            throw new ImplementationException("Attempted to change class from " + discriminatorValue
                                    + " to " + currentComponent.getClass().getName() + " for id " + currentComponent.getId());
                        }
                        return;
                    }
                }
            }
        }
        EntityComponent persistedComponent = loadEntityComponentByIdIgnoringCache(entityType, session, currentComponent.getId());
        if (persistedComponent != null) {
            Class<?> persistedClass = resolveEntityComponentClass(persistedComponent);
            Class<?> currentClass = resolveEntityComponentClass(currentComponent);
            if (!Objects.equals(persistedClass, currentClass)) {
            throw new ImplementationException("Attempted to change class from " + persistedComponent.getClass().getName()
                    + " to " + currentComponent.getClass().getName() + " for id " + currentComponent.getId());
            }
        }
    }

    /**
     * Laster eksisterende objekt fra databasen hvis det ikke allerede er lastet og prosesserer det nye objektet
     * dersom nytt og eksisterende objekt er forskjellige instanser.
     * <p>
     * Prosesseringen består i at det nye objektet får oppdatert alle sine collections slik at de inneholder riktig
     * snapshotverdi av gammel tilstand. Dette er viktig for at hibernate skal kunne oppdatere collections riktig i
     * databasen (se SKIF-214).  Prosesseringen finner også one-to-one objekter som har blitt orphan og som må
     * slettes manuelt.
     * <p>
     * Videre så sjekkes det nye objektet har endret type i forhold til eksisterende objekt. Dersom så har skjedd, så
     * må ikke-felles felter nullstilles og det utføres en spesiell SQL som endrer objekttypen i databasen.
     * <p>
     * Endelig sørges det for at Hibernates cache ikke inneholder et objekt med samme id, med mindre det også er
     * nøyaktig samme objekt (instans) som <i>bubbleObject</i>.
     *
     * @param bubbleObject                   et objekt som kanskje er lastet gjennom hibernate tidligere i denne
     *                                       sesjonen
     * @param orphanOneToOneEntityComponents fylles med én-til-én entity components som ikke lenger blir referert til av sin eier
     * @throws HibernateException    dersom get() eller evict() på hibernate session feiler
     * @throws SQLException dersom SQL feiler ved typeendring
     */
    private void assignPersistentCollectionsAndConvertObjectIfTypeChangedAndEvictOtherInstance(BubbleObject bubbleObject, List<Multimap<Class<? extends EntityComponent>, EntityComponent>> orphanOneToOneEntityComponents) throws HibernateException, SQLException {
        // Hibernate tillater ikke update på et objekt når et annet objekt med samme id finnes i hibernate sin cache
        // Vi må teste på dette og evt. kaste ut det gamle objektet fra hibernate sin cache. Dette kan testes ved å
        // slå opp objektet i hibernates interne cache slik at objektet ikke lastes fra databasen hvis det ikke allerede
        // er lastet.
        //
        // Metoden trenger imidlertid alltid informasjon om eksisterende innhold i collections så objektet må lastes
        // uansett. For å skifte subtype trenges det også at objektet er lastet for å nulle ut ikke-felles-felter.
        // Dersom objektet ikke allerede er lastet lastes eksistrende objekt her. For bulk updates vil det går raksere
        // hvis eksisterende objekter allerede er lastet før man kommer her slik at eksisterende objekter ikke lastes
        // en-etter-en.
        BubbleObject existingBubble = (BubbleObject) session().get(bubbleObject.getId().getBaseType(), bubbleObject.getId(), LockMode.NONE);
        if (existingBubble != bubbleObject) {
            lazyInitializer.ensureFullyLoaded(existingBubble); // TODO: Håndter lazy loaded collections. Må pt kalle ensureFullyLoaded fordi attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntityComponents pt ikke håndtere lazyloaded collections.

            // Typeendring må skje før attachPersistenceCollection
            if (!(bubbleObject.getClass().isInstance(existingBubble))) {
                changeType(bubbleObject, existingBubble);

                // Les inn objektet på nytt med den nye typen. changeType har evictet.
                existingBubble = (BubbleObject) session().get(bubbleObject.getId().getBaseType(), bubbleObject.getId(), LockMode.NONE);
                lazyInitializer.ensureFullyLoaded(existingBubble); // TODO: Håndter lazy loaded collections. Må pt kalle ensureFullyLoaded fordi attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntityComponents pt ikke håndtere lazyloaded collections.
            }

            attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntityComponents(bubbleObject, CopyHelper.copy(existingBubble), orphanOneToOneEntityComponents);

            // Objektet har ikke endret type, bare hiv ut gammel versjon fra Hibernate.
            if (logger.isDebugEnabled()) {
                logger.debug("Evict av annen boble instans assosiert med sessionen : " + bubbleObject.getId());
            }
            // Dersom den gamle instansen har blitt endret vil hibernate kunne gi en "postInsert feil: possible nonthreadsafe access to session"
            // Denne feilen kan unngåes hvis man gjør en session.flush() her slik at alle endringer fra den gamle instansen
            // kommer ned i databasen. Rammeverket skal dog fange opp og hindre at det oppdateres på flere instanser av
            // samme objekt innenfor samme session. Det skal derfor ikke være noen flush her.
            //session.flush();
            // La den nye versjonen erstatte den gamle. Det er tryggt å anta at denne også er fullt initialisert.
            lazyInitializer.markAsFullyInitialized(bubbleObject);

            session().evict(existingBubble);
        } // else {
        // Gjør ingen ting, bubbleObject er det objektet som allerede ligger i hibernate sessionen
        // }
    }


    /**
     * Sørger for at objektets collections alle er av typen PersisteneCollection og inneholder snapshot av gammel tilstand
     * i tillegg til ny tilstand. Finner også orphan one-to-one objekter som må slettes manuelt og legger disse i {@code
     * orphanOneToOneEntityComponents}
     */
    public void attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntityComponents(BubbleObject bubbleObject, BubbleObject existingBubble, List<Multimap<Class<? extends EntityComponent>, EntityComponent>> orphanOneToOneEntityComponents) {
        try {
            IdentityHashMap<Object, Object> processedObjects = new IdentityHashMap<>();
            attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntities(bubbleObject, existingBubble, processedObjects, 0, orphanOneToOneEntityComponents);
        } catch (HibernateException e) {
            throw new ImplementationException("Could not attach PersistenceCollection: " + bubbleObject.getId(), e);
        }
    }

    /**
     * Sørger for at objektets collections alle er av typen PersisteneCollection og inneholder snapshot av gammel tilstand
     * i tillegg til ny tilstand. Hvis en assosiasjon er definert som cascade vil metoden bli kaldt rekursivt på de assosierte
     * objekter dersom de også finnes i existing object
     */
    protected void attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntities(Object object, Object existingObject, IdentityHashMap<Object, Object> processedObjects, int nestingLevel, List<Multimap<Class<? extends EntityComponent>, EntityComponent>> orphanOneToOneEntityComponents) throws HibernateException {
        if (object == null) return;

        if (processedObjects.containsKey(object)) return;
        processedObjects.put(object, null);

        ClassMetadata classMetadata = HibernateHelper.getClassMetadata(session(), object);

        if (erAvTypeSomIkkeSkalInitialiseresVidere(classMetadata)) return;
        EntityPersister persister = (EntityPersister) classMetadata;

        Type[] types = persister.getPropertyTypes();
        Object[] values = persister.getPropertyValues(object);
        CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();

        Class<?> entityClass = existingObject instanceof HibernateProxy ? existingObject.getClass().getSuperclass() : existingObject.getClass();
        EntityPersister persisterExisting = lazyInitializer.getMetamodel().entityPersister(entityClass.getName());
        Type[] typesExisting = persisterExisting.getPropertyTypes();
        Object[] valuesExisting = persisterExisting.getPropertyValues(existingObject);

        int commonLength;
        if (persister != persisterExisting) {
            // Beregn felles felter for object og existingObject
            for (commonLength = 0; commonLength < types.length && commonLength < typesExisting.length; commonLength++) {
                if (!types[commonLength].getName().equals(typesExisting[commonLength].getName())) {
                    break;
                }
            }
        } else {
            commonLength = types.length;
        }

        for (int i = 0; i < commonLength; i++) {
            Type type = types[i];
            Object value = values[i];
            Object valueExisting = valuesExisting[i];
            CascadeStyle cascadeStyle = cascadeStyles != null ? cascadeStyles[i] : CascadeStyles.NONE;
            attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForElement(i, object, processedObjects, nestingLevel, orphanOneToOneEntityComponents, persister, type, value, valueExisting, cascadeStyle);
        }

        // TODO: håndter felter som er forskjellige i subtyper her. Bør sjekke at felter i nytt objekt ikke bruker eksisterende entity components. Bør også finne alle orphan one-to-one i gamle felter
    }

    protected void attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForElement(int i, Object object, IdentityHashMap<Object, Object> processedObjects, int nestingLevel, List<Multimap<Class<? extends EntityComponent>, EntityComponent>> orphanOneToOneEntityComponents, EntityPersister persister, Type type, Object value, Object valueExisting, CascadeStyle cascadeStyle) {
        if (type.isEntityType()) {
            EntityType entityType = (EntityType) type;
            boolean isNew = isNewOrReplacedEntityComponent(entityType, value, valueExisting, processedObjects, nestingLevel, orphanOneToOneEntityComponents, cascadeStyle);
            if (isNew) {
                checkForStolenEntitiesInNewObject(type, value, new IdentityHashMap<>(), newlyInsertedComponents, cascadeStyle);
            } else if (valueExisting != null) {
                checkForSubtypeChangedEntityComponent(type, value, valueExisting);
                // Kan ikke attache collections i entity hvis det ikke var noe fra før
                attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntities(value, valueExisting, processedObjects, nestingLevel + 1, orphanOneToOneEntityComponents);
            }
        } else if (type.isComponentType()) {
            attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForComponent(value, valueExisting, type, processedObjects, nestingLevel, orphanOneToOneEntityComponents);
        } else if (type.isCollectionType()) {
            if (valueExisting instanceof Map) {
                MapType mapType = (MapType) type;
                Map<?, ?> mapWithSnapshot = attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForMap((Map<?, ?>) value, (Map<?, ?>) valueExisting, mapType, true);
                persister.setPropertyValue(object, i, mapWithSnapshot);
            } else {
                CollectionType collectionType = (CollectionType) type;
                Collection<?> collectionWithSnapshot = attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForCollection((Collection<?>) value, (Collection<?>) valueExisting, collectionType, processedObjects, nestingLevel, orphanOneToOneEntityComponents);
                persister.setPropertyValue(object, i, collectionWithSnapshot);
            }
        } else if (!isSingleColumnType(type) // ting som ligger i én kolonne (Primitiver, String, o.l.). Disse kan ikke ha collections.
                && !(type instanceof CustomType)) { // CustomType har nok heller ingen collections i seg.
            throw new NotImplementedException();
            // TODO: Trenger et test eksempel for å skrive denne koden riktig
        }
    }

    /**
     * Sjekker om dette en en entitycomponent som har endret subtype.
     *
     * @param type          typedefinisjon
     * @param value         oppdatert verdi
     * @param valueExisting persistert verdi
     * @throws ImplementationException hvis det er en EntityComponent som har endret subtype
     */
    private void checkForSubtypeChangedEntityComponent(Type type, Object value, Object valueExisting) {
        if (value != null && valueExisting != null) {
            Class<?> typeClass = type.getReturnedClass();
            if (EntityComponent.class.isAssignableFrom(typeClass)) {
                EntityComponent entityComponent = (EntityComponent) value;
                EntityComponent oldComponent = (EntityComponent) valueExisting;
                // Sjekk om id har blitt gjenbrukt i entitycomponent av annen subtype
                Class<?> oldClass = resolveEntityComponentClass(oldComponent);
                Class<?> currentClass = resolveEntityComponentClass(entityComponent);
                if (Objects.equals(entityComponent.getId(), oldComponent.getId()) && !Objects.equals(oldClass, currentClass)) {
                    throw new ImplementationException("Attempted to change class from " + oldComponent.getClass().getName() +" to " + entityComponent.getClass().getName() + " for id " + entityComponent.getId());
                }
            }
        }
    }

    private Class<?> resolveEntityComponentClass(Object component) {
        if (component instanceof HibernateProxy) {
            return ((HibernateProxy) component).getHibernateLazyInitializer().getPersistentClass();
        }
        return component != null ? component.getClass() : null;
    }

    /**
     * Legge inn orphan entity component i {@code orphanOneToOneEntityComponents}.
     */
    protected void addOrphanOneToOneEntityComponent(int nestingLevel, EntityComponent valueExisting, List<Multimap<Class<? extends EntityComponent>, EntityComponent>> orphanOneToOneEntityComponents) {
        while (orphanOneToOneEntityComponents.size() <= nestingLevel) {
            orphanOneToOneEntityComponents.add(HashMultimap.create());
        }
        orphanOneToOneEntityComponents.get(nestingLevel).put(valueExisting.getClass(), valueExisting);
    }

    protected void attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForComponent(Object component, Object componentExisting, Type componentType, IdentityHashMap<Object, Object> processedObjects, int nestingLevel, List<Multimap<Class<? extends EntityComponent>, EntityComponent>> orphanOneToOneEntityComponents) {
        Type[] propertyTypes = getSubtypes(componentType);
        Object[] properties = (component == null) ? new Object[propertyTypes.length] : getPropertyValues(componentType, component);
        Object[] propertiesExisting = (componentExisting == null) ? null : getPropertyValues(componentType, componentExisting);
        boolean wasModified = false;
        for (int j = 0; j < propertyTypes.length; j++) {
            Type propertyType = propertyTypes[j];
            Object property = properties[j];
            Object propertyExisting = propertiesExisting == null ? null : propertiesExisting[j];
            CascadeStyle cascadeStyle = getCascadeStyle(componentType, j);

            // Hver property kan enten være et simple objekt (f.eks Long), complex objekt (f.eks Boundary) eller en collection
            if (propertyType.isEntityType()) {
                EntityType entityPropertyType = (EntityType) propertyType;
                boolean isNew = isNewOrReplacedEntityComponent(entityPropertyType, property, propertyExisting, processedObjects, nestingLevel, orphanOneToOneEntityComponents, cascadeStyle);
                if (isNew) {
                    checkForStolenEntitiesInNewObject(entityPropertyType, property, processedObjects, newlyInsertedComponents, cascadeStyle);
                } else if (propertyExisting != null) {
                    // Kan ikke attache collections i entity hvis det ikke var noe fra før
                    attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntities(property, propertyExisting, processedObjects, nestingLevel + 1, orphanOneToOneEntityComponents);
                }
            } else if (propertyType.isComponentType()) {
                // Trenger ikke å øke nestinLevel for composite components
                attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForComponent(property, propertyExisting, propertyType, processedObjects, nestingLevel, orphanOneToOneEntityComponents);
            } else if (propertyType.isCollectionType()) {
                if (property != null) {
                    // For hvert element
                    if (propertyExisting instanceof Map) {
                        MapType mapType = (MapType) propertyType;
                        properties[j] = attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForMap((Map<?, ?>) property, (Map<?, ?>) propertyExisting, mapType, true);
                    } else {
                        CollectionType collectionType = (CollectionType) propertyType;
                        properties[j] = attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForCollection((Collection<?>) property, (Collection<?>) propertyExisting, collectionType, processedObjects, nestingLevel, orphanOneToOneEntityComponents);
                    }
                    wasModified = true;
                } else {
                    throw new ImplementationException("Component contains a Collection that is null: " + propertyType);
                }
            } else if (!isSingleColumnType(propertyType) // ting som ligger i én kolonne (Primitiver, String, o.l.). Disse kan ikke ha collections.
                    && !(propertyType instanceof CustomType)) { // CustomType har nok heller ingen collections i seg.
                throw new NotImplementedException();
            }
        }

        if (wasModified)

        {
            setPropertyValues(componentType, component, properties);
        }

    }

    /**
     * Typehieraki for component ser forskjelllig i Hibernate 3.2 og 3.6. Må derfor bruke hjelpemetoder til å caste
     * riktig.
     *
     * @since 2.3
     */
    protected CascadeStyle getCascadeStyle(Type componentType, int i) {
        return ((CompositeType) componentType).getCascadeStyle(i);
    }

    /**
     * Typehieraki for component ser forskjelllig i Hibernate 3.2 og 3.6. Må derfor bruke hjelpemetoder til å caste
     * riktig.
     *
     * @since 2.3
     */
    void setPropertyValues(Type componentType, Object component, Object[] properties) {
        ((CompositeType) componentType).setPropertyValues(component, properties);
    }

    /**
     * Typehieraki for component ser forskjelllig i Hibernate 3.2 og 3.6. Må derfor bruke hjelpemetoder til å caste
     * riktig.
     *
     * @since 2.3
     */
    Object[] getPropertyValues(Type componentType, Object component) {
        return ((CompositeType) componentType).getPropertyValues(component);
    }

    /**
     * Typehieraki for component ser forskjelllig i Hibernate 3.2 og 3.6. Må derfor bruke hjelpemetoder til å caste
     * riktig.
     *
     * @since 2.3
     */
    Type[] getSubtypes(Type componentType) {
        return ((CompositeType) componentType).getSubtypes();
    }


    protected Map<?, ?> attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForMap(Map<?, ?> mapInObject, Map<?, ?> mapInExistingObject, MapType mapType, boolean cascade) {
        if (mapInExistingObject instanceof PersistentCollection) {
            PersistentCollection persistentMapInExistingObject = (PersistentCollection) mapInExistingObject;
            if (mapInObject instanceof PersistentCollection && !((PersistentCollection) mapInObject).wasInitialized()) {
                throw new ImplementationException("Uninitialized map in detached object not supported");
            }

            final SessionImplementor sessionImpl = (SessionImplementor) session();
            final AbstractCollectionPersister collectionPersister = (AbstractCollectionPersister) sessionImpl.getSessionFactory().getMetamodel().collectionPersister(mapType.getRole());

            if (!(collectionPersister.getKeyType() instanceof BasicType)) {
                throw new ImplementationException("Key must be BasicType");
            }
            Map<?, ?> map = (Map<?, ?>) mapType.instantiate(mapInObject != null ? mapInObject.size() : 0);
            PersistentMap persistentMap = (PersistentMap) mapType.wrap(sessionImpl, map);
            persistentMap.unsetSession(sessionImpl); // Skal ikke være attached enda, men null er ikke lov over
            persistentMap.setSnapshot(persistentMapInExistingObject.getKey(), persistentMapInExistingObject.getRole(), persistentMapInExistingObject.getStoredSnapshot());
            if (mapInObject != null) {
                persistentMap.putAll(mapInObject);
            }

            if (cascade) {
                // Sjekk at det ikke er noen collections inni her
                Type elementType = collectionPersister.getElementType();
                if (elementType.isCollectionType()) {
                    throw new NotImplementedException("Map value kan ikke være collection");
                } else if (elementType.isAssociationType()) {
                    return persistentMap;
                } else if (elementType.isComponentType()) {
                    throw new NotImplementedException("Map value kan ikke være enkel verdi eller entity");
                }
            }
            return persistentMap;
        } else {
            // TODO: Alternativt returner eksisterende map. Kanskje det er greit?
            throw new ImplementationException("Unable to attach persistent collection, as existing object has map that isn't PersistentCollection");
            //return collectionInOject;
        }
    }

    protected Collection<?> attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForCollection(Collection<?> collectionInObject, Collection<?> collectionInExistingObject, CollectionType collectionType, IdentityHashMap<Object, Object> processedObjects, int nestingLevel, List<Multimap<Class<? extends EntityComponent>, EntityComponent>> orphanOneToOneEntityComponents) throws HibernateException {
        if (collectionInExistingObject instanceof PersistentCollection) {
            PersistentCollection persistentCollectionInExistingObject = (PersistentCollection) collectionInExistingObject;
            if (collectionInObject instanceof PersistentCollection && !((PersistentCollection) collectionInObject).wasInitialized()) {
                throw new ImplementationException("Uninitialized collection in detached object not supported");
            } else {
                final SessionImplementor sessionImpl = (SessionImplementor) session();

                Collection<?> collection = (Collection<?>) collectionType.instantiate(collectionInObject != null ? collectionInObject.size() : 0);
                PersistentCollection persistentCollection = collectionType.wrap(sessionImpl, collection);
                persistentCollection.unsetSession(sessionImpl); // Skal ikke være attached enda, men null er ikke lov over
                if (!persistentCollectionInExistingObject.wasInitialized()) {
                    Hibernate.initialize(persistentCollectionInExistingObject);
                }
                persistentCollection.setSnapshot(persistentCollectionInExistingObject.getKey(), persistentCollectionInExistingObject.getRole(), persistentCollectionInExistingObject.getStoredSnapshot());

                // Det er viktig å gjøre dette etter setSnapshot pga. MultikoblingPersistentSet
                if (collectionInObject != null) {
                    ((Collection) persistentCollection).addAll(collectionInObject);
                }

                Type elementType = collectionType.getElementType(sessionImpl.getSessionFactory());
                checkForStolenEntityComponent(elementType, collectionInObject, collectionInExistingObject);
                attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForCollectionCascade((Collection<?>) persistentCollection, collectionInExistingObject, elementType, processedObjects, nestingLevel, orphanOneToOneEntityComponents);
                return (Collection<?>) persistentCollection;
            }
        } else {
            // TODO: Alternativt returner eksisterende collection. Kanskje det er greit?
            throw new ImplementationException("Unable to attach persistent collection, as existing object has collection that isn't PersistentCollection");
            //return collectionInObject;
        }
    }

    protected void attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForCollectionCascade(Collection<?> collectionInOject, Collection<?> collectionInExistingObject, Type elementType, IdentityHashMap<Object, Object> processedObjects, int nestingLevel, List<Multimap<Class<? extends EntityComponent>, EntityComponent>> orphanOneToOneEntityComponents) throws HibernateException {
        SessionImplementor sessionImpl = (SessionImplementor) session();
        if (elementType.isEntityType()) {
            Map<Serializable, Object> oldElementMap = Maps.newHashMap();
            for (Object o : collectionInExistingObject) {
                final EntityPersister elementPersister = sessionImpl.getEntityPersister(elementType.getName(), o);
                oldElementMap.put((Serializable) elementPersister.getIdentifier(o, sessionImpl), o);
            }
            for (Object object : collectionInOject) {
                final EntityPersister elementPersister = sessionImpl.getEntityPersister(elementType.getName(), object);
                final Serializable identifier = (Serializable) elementPersister.getIdentifier(object, sessionImpl);
                final Object valueExisting = oldElementMap.get(identifier);
                if (valueExisting != null) { // TODO: Dersom objektet ikke fantes før, så kan det vel ikke være noen collections som skal attaches?
                    attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntities(object, valueExisting, processedObjects, nestingLevel, orphanOneToOneEntityComponents);
                }
            }
        } else if (elementType.isComponentType()) {
            ComponentType componentType = (ComponentType) elementType;
            Type[] propertyTypes = componentType.getSubtypes();
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < propertyTypes.length; i++) {
                if (propertyTypes[i].isCollectionType()) {
                    // TODO: Her har vi en collection hvis elementer er av type component som inneholer et felt som er en collection
                    // Utfordringen her er å match gamle og nye komponenter mot hverander i den overliggende
                    // collection. For sett kan man bruke equals, men for lister er det ikke opplagt hva man
                    // man skal bruke. Kanskje indexposisjon. Venter med å implementere støtte for dette til vi
                    // har en konkret case.
                    throw new NotImplementedException();
                }
            }
        } else if (!isSingleColumnType(elementType) // ting som ligger i én kolonne (Primitiver, String, o.l.). Disse kan ikke ha collections.
                && !(elementType instanceof CustomType)) { // CustomType har nok heller ingen collections i seg.
            throw new NotImplementedException();
        }
    }

    /**
     * Knytter {@code bubbleObject} til underliggende hibernate sesison. Ved senere kall til flush() vil endringene
     * bli sendt til databasen.
     */
    public <T extends BubbleObject> void insert(T bubbleObject) {
        try {
            checkEntityComponentsOnInsert(bubbleObject);
            session().save(bubbleObject);
        } catch (HibernateException e) {
            throw new ImplementationException("Insert failed for " + bubbleObject, e);
        }
    }

    /**
     * Sjekker at objektgraften ikke inneholder entity components som har id. Dette er et tegn på at komponenten har
     * blitt stjålet fra en annen boble.
     *
     * @since 2.2.0
     */
    private void checkEntityComponentsOnInsert(BubbleObject bubbleObject) {
        try {
            IdentityHashMap<Object, Object> processedObjects = new IdentityHashMap<>();
            checkEntityComponentsOnInsert(bubbleObject, processedObjects);
        } catch (HibernateException e) {
            throw new ImplementationException("Could not check entity components for " + bubbleObject.getId(), e);
        }
    }

    /**
     * Sjekker at objektet ikke referer til entity components som allerede har fått tilordnet id. Hvis en assosiasjon er
     * definert som cascade vil metoden bli kalt rekursivt på de assosierte objekter.
     *
     * @since 2.2.0
     */
    private void checkEntityComponentsOnInsert(Object object, IdentityHashMap<Object, Object> processedObjects) throws HibernateException {
        if (object == null) return;

        ClassMetadata classMetadata = HibernateHelper.getClassMetadata(session(), object);

        if (erAvTypeSomIkkeSkalInitialiseresVidere(classMetadata)) return;
        EntityPersister persister = (EntityPersister) classMetadata;

        Type[] types = persister.getPropertyTypes();
        Object[] values = persister.getPropertyValues(object);
        CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();

        for (int i = 0; i < types.length; i++) {
            Type type = types[i];
            Object value = values[i];
            CascadeStyle cascadeStyle = cascadeStyles != null ? cascadeStyles[i] : CascadeStyles.NONE;

            if (type.isEntityType()) {
                checkForStolenEntitiesInNewObject(type, value, processedObjects, newlyInsertedComponents, cascadeStyle);
            } else if (type.isComponentType()) {
                checkEntityComponentsOnInsertInComponent(value, type, processedObjects);
            } else if (type.isCollectionType()) {
                if (value instanceof Map) {
                    CollectionType collectionType = (CollectionType) type;
                    checkEntityComponentsInMapOnInsert(collectionType, (Map<?, ?>) value, processedObjects, cascadeStyle);
                } else {
                    CollectionType collectionType = (CollectionType) type;
                    Type elementType = collectionType.getElementType(((SessionImplementor) session()).getSessionFactory());
                    checkForStolenEntitiesInNewObjectForCollection(elementType, (Collection<?>) value, processedObjects, newlyInsertedComponents, cascadeStyle);
                }
            } else if (!isSingleColumnType(type) // ting som ligger i én kolonne (Primitiver, String, o.l.). Disse kan ikke ha collections.
                    && !(type instanceof CustomType)) { // CustomType har nok heller ingen collections i seg.
                throw new NotImplementedException();
                // TODO: Trenger et test eksempel for å skrive denne koden riktig
            }
        }
    }

    protected void checkEntityComponentsOnInsertInComponent(Object component, Type componentType, IdentityHashMap<Object, Object> processedObjects) {
        if (component != null) {
            Type[] propertyTypes = getSubtypes(componentType);
            Object[] properties = getPropertyValues(componentType, component);
            boolean wasModified = false;
            for (int j = 0; j < properties.length; j++) {
                Type propertyType = propertyTypes[j];
                Object property = properties[j];
                CascadeStyle cascadeStyle = getCascadeStyle(componentType, j);

                if (cascadeStyle != null && cascadeStyle.doCascade(CascadingActions.SAVE_UPDATE)) {
                    // Hver property kan enten være et simple objekt (f.eks Long), complex objekt (f.eks Boundary) eller en collection
                    if (propertyType.isEntityType()) {
                        //checkForReplacedOrStolenEntityComponentInV32((EntityType) propertyType, property, null);
                        checkEntityComponentsOnInsert(property, processedObjects);
                    } else if (propertyType.isCollectionType()) {
                        // For hvert element
                        if (property instanceof Map) {
                            CollectionType collectionType = (CollectionType) propertyType;
                            checkEntityComponentsInMapOnInsert(collectionType, (Map<?, ?>) property,  processedObjects, cascadeStyle);
                        } else {
                            CollectionType collectionType = (CollectionType) propertyType;
                            Type elementType = collectionType.getElementType(((SessionImplementor) session()).getSessionFactory());
                            checkForStolenEntitiesInNewObjectForCollection(elementType, (Collection<?>) property, processedObjects, newlyInsertedComponents, cascadeStyle);
                        }
                        wasModified = true;
                    } else if (propertyType.isComponentType()) {
                        checkEntityComponentsOnInsertInComponent(property, propertyType, processedObjects);
                    } else if (!isSingleColumnType(propertyType) // ting som ligger i én kolonne (Primitiver, String, o.l.). Disse kan ikke ha collections.
                            && !(propertyType instanceof CustomType)) { // CustomType har nok heller ingen collections i seg.
                        throw new NotImplementedException();

                    }
                }
            }
            if (wasModified) {
                setPropertyValues(componentType, component, properties);
            }
        }
    }

    private void checkEntityComponentsInMapOnInsert(CollectionType collectionType, Map<?, ?> value, IdentityHashMap<Object, Object> processedObjects, CascadeStyle cascadeStyle) {
        AbstractCollectionPersister collectionPersister = (AbstractCollectionPersister) lazyInitializer.getMetamodel().collectionPersister(collectionType.getRole());

        if (!(collectionPersister.getKeyType() instanceof BasicType)) {
            throw new ImplementationException("Key must be BasicType");
        }

        if (cascadeStyle.doCascade(CascadingActions.SAVE_UPDATE)) {
            // Sjekk at det ikke er noen collections inni her
            Type elementType = collectionPersister.getElementType();
            if (elementType.isCollectionType()) {
                throw new NotImplementedException("Map value kan ikke være collection");
            } else if (elementType.isAssociationType()) {
                for (Object o : value.values()) {
                    checkForStolenEntitiesInNewObject(elementType, o, processedObjects, newlyInsertedComponents, cascadeStyle);
                }
            } else if (elementType.isComponentType()) {
                throw new NotImplementedException("Map value må være enkel verdi eller entity");
            }
        }
    }

    private void checkForStolenEntitiesInNewObjectForCollection(Type elementType, Collection<?> collectionInObject, IdentityHashMap<Object, Object> processedObjects, Set<EntityComponent> newlyInsertedComponents, CascadeStyle cascadeStyle) throws HibernateException {
        if (elementType.isEntityType()) {
            for (Object object : collectionInObject) {
                checkForStolenEntitiesInNewObject(elementType, object, processedObjects, newlyInsertedComponents, cascadeStyle);
            }
        } else if (elementType.isComponentType()) {
            ComponentType componentType = (ComponentType) elementType;
            Type[] propertyTypes = componentType.getSubtypes();
            //noinspection ForLoopReplaceableByForEach
            for (int i = 0; i < propertyTypes.length; i++) {
                // TODO: her mangler det noe. Må sjekke hvis element er en entity eller nested component

                if (propertyTypes[i].isCollectionType()) {
                    // TODO: Her har vi en collection hvis elementer er av type component som inneholer et felt som er en collection
                    // Utfordringen her er å match gamle og nye komponenter mot hverander i den overliggende
                    // collection. For sett kan man bruke equals, men for lister er det ikke opplagt hva man
                    // man skal bruke. Kanskje indexposisjon. Venter med å implementere støtte for dette til vi
                    // har en konkret case.
                    throw new NotImplementedException();
                }
            }
        } else if (!isSingleColumnType(elementType) // ting som ligger i én kolonne (Primitiver, String, o.l.). Disse kan ikke ha collections.
                && !(elementType instanceof CustomType)) { // CustomType har nok heller ingen collections i seg.
            throw new NotImplementedException();
        }
    }


    /**
     * Sjekker om {@code value} er en entity component og om den  er ny i forhold til {@code valueExisiting}. Dersom
     * dette er tilfellet returneres {@code true} og ellers {@code false}. Videre sjekker metoden om
     * {@code valueExisting} blir orphan og legger den inn i {@code orphanOneToOneEntityComponents}
     * hvis det er tilfellet og cascade "delete-orphan" er satt for mappingen.
     *
     * <P>Hibernate 3.2 støtter ikke automatisk sletting av orphan objekter i attached state og etterlader
     * orphan objekter i databasen. Dette er ikke umidelbart mulig å fikse. For å få mest mulig lik oppførsel mellom
     * attached og detached state kaster metoden derfor exception dersom en entity component blir orphan i detached
     * state.
     *
     * <P>I Hibernate 3.6 støttes automatisk sletting av orphan entity components i attached state. For å få dette til
     * har Hibernate 3.6 blitt patchet med 2 bugfixes (se SKIF-326). Oppførslen blir derfor lik for attached og
     * detached state.
     * <p>
     *
     * @param type          typen til feltet
     * @param value         nåværende verdi
     * @param valueExisting forrige verdi
     * @throws ImplementationException hvis eksisterende component blir orphan (gjelder kun Hibernate 3.2)
     * @return              true hvis {@code value} er ny eller erstatter {@code valueExisting}
     * @since 2.4.0
     */
    protected boolean isNewOrReplacedEntityComponent(EntityType type, Object value, Object valueExisting, IdentityHashMap<Object, Object> processedObjects, int nestingLevel, List<Multimap<Class<? extends EntityComponent>, EntityComponent>> orphanOneToOneEntityComponents, CascadeStyle cascadeStyle) {
        Class<?> typeClass = type.getReturnedClass();
        if (EntityComponent.class.isAssignableFrom(typeClass)) {
            EntityComponent componentExisting = (EntityComponent) valueExisting;
            EntityComponent component = (EntityComponent) value;
            if (componentExisting != null) {
                if (component == null || !componentExisting.getId().equals(component.getId())) {
                    if (cascadeStyle.hasOrphanDelete()) {
                        addOrphanOneToOneEntityComponent(nestingLevel, (EntityComponent) valueExisting, orphanOneToOneEntityComponents);
                    }
                    return true;
                } else {
                    // Eksisterende entity og ny entity har samme id
                    return false;
                }
            } else {
                return value != null;
            }
        }
        return false;
    }

    protected void checkIsNewEntityComponent(EntityMetamodel entityMetamodel, EntityComponent component, Set<EntityComponent> newlyInsertedComponents) {
        if (component.getId() != null
                && !newlyInsertedComponents.contains(component)
                && !(entityMetamodel.getIdentifierProperty().getIdentifierGenerator() instanceof Assigned)) {
            throw new ImplementationException("Found entity component that is not new. Class: " + entityMetamodel.getEntityType().getReturnedClass().getName() + " Id:" + component.getId(), logger);
        }
    }

    /**
     * Sjekker om <code>collection</code> inneholder en EntityComponent som ikke er ny, men ikke var der fra før.
     * Sjekker også at ingen EntityComponent har endret subtype, men beholdt id.
     *
     * @param elementType        typen til elementene i collection
     * @param collection         nåværende collection
     * @param collectionExisting forrige collection
     * @throws ImplementationException dersom collection innholder entity component med id som ikke fins i collectionExisting, eller hvis den har annen subtype i collectionExisting
     * @since 2.2.0
     */
    private void checkForStolenEntityComponent(Type elementType, Collection<?> collection, Collection<?> collectionExisting) {
        if (collection == null || collectionExisting == null) return;

        if (elementType.isEntityType()) {
            Class<?> typeClass = elementType.getReturnedClass();
            if (EntityComponent.class.isAssignableFrom(typeClass)) {
                // Dersom komponentid er assigned, så kan vi ikke detektere stjeling av komponenter. Slike id-er finnes i matrikkel historikk. // TODO: jo vi kan siden vi ved hvilke objekter som insertes, ligger i eget set
                EntityPersister persister = lazyInitializer.getMetamodel().entityPersister(elementType.getName());
                boolean kanSjekkeForTyveri = !(persister.getIdentifierGenerator() instanceof Assigned);

                Map<Long, EntityComponent> oldIdMap = collectionExisting.stream().map(EntityComponent.class::cast).collect(Collectors.toMap(
                    EntityComponent::getId,
                    Function.identity()
                ));

                for (Object o : collection) {
                    EntityComponent entityComponent = (EntityComponent) o;

                    if (entityComponent == null || entityComponent.getId() == null) continue;

                    // Sjekk om id har blitt gjenbrukt i entitycomponent av annen subtype
                    EntityComponent oldComponent = oldIdMap.get(entityComponent.getId());
                    if (oldComponent != null && !oldComponent.getClass().equals(entityComponent.getClass())) {
                        throw new ImplementationException("Attempted to change class from " + oldComponent.getClass().getName() + " to " + entityComponent.getClass().getName() + " for id " + entityComponent.getId());
                    }

                    if (kanSjekkeForTyveri) {
                        if (entityComponent.getId() != null && !oldIdMap.containsKey(entityComponent.getId())) {
                            throw new ImplementationException("Found entity component " + typeClass.getName() + " Id:" + entityComponent.getId() + " in collection that didn't contain it previously", logger);
                        }
                    }
                }
            }
        }
    }

    /**
     * Utfører nødvendige Hibernate- og SQL-operasjoner som må til for å endre <i>previousObject</i> til
     * <i>currentObject</i>. <i>previousObject</i> må være siste utgave av objektet i <i>denne</i> sesjonen og ha samme
     * replicaversion.
     *
     * @param currentObject  det oppdaterte objektet av ny type
     * @param previousObject forrige utgave av <i>currentObject</i>
     * @throws SQLException dersom databasen ikke samarbeider
     * @since 2.1
     */
    private void changeType(BubbleObject currentObject, BubbleObject previousObject) throws SQLException {
        final AbstractEntityPersister fromEntityPersister = (AbstractEntityPersister) lazyInitializer.getClassPersister(previousObject.getClass());
        final AbstractEntityPersister toEntityPersister = (AbstractEntityPersister) lazyInitializer.getClassPersister(currentObject.getClass());

        final String dbTable = toEntityPersister.getTableName();

        if (fromEntityPersister.isMultiTable()) {
            throw new ImplementationException("Can not change type from " + previousObject.getClass() + " since it spans multiple tables", logger);
        }
        if (toEntityPersister.isMultiTable()) {
            throw new ImplementationException("Can not change type to " + currentObject.getClass() + " since it spans multiple tables", logger);
        }
        if (!dbTable.equals(fromEntityPersister.getTableName())) {
            throw new ImplementationException("Can not change type from " + previousObject.getClass() + " to " + currentObject.getClass() + " since they are stored in different tables", logger);
        }

        List<Field> oldPrimitiveFields;
        try {
            oldPrimitiveFields = blankUtIkkeFellesFelter(previousObject, currentObject.getClass());
        } catch (IllegalAccessException e) {
            throw new ImplementationException("Could not clear fields in initial object during type change", e, logger);
        }
        List<Field> newPrimitiveFields = findNyePrimitiveFelter(previousObject.getClass(), currentObject.getClass());

        session().flush();
        evict(currentObject.getId());

        final String discriminatorColumn = toEntityPersister.getDiscriminatorColumnName();
        final String discriminatorValue = toEntityPersister.getDiscriminatorSQLValue(); // Inkluderer apostrofer hvis tekst

        StringBuilder sql = new StringBuilder("update ");
        sql.append(dbTable);
        sql.append(" set ").append(discriminatorColumn).append('=').append(discriminatorValue);

        if (!oldPrimitiveFields.isEmpty()) {
            for (int i = 0; i < oldPrimitiveFields.size(); i++) {
                final Field field = oldPrimitiveFields.get(i);
                final int propertyIndex = fromEntityPersister.getPropertyIndex(field.getName());
                final String[] propertyColumnNames = fromEntityPersister.getPropertyColumnNames(propertyIndex);
                if (propertyColumnNames.length != 1) {
                    throw new ImplementationException("Property " + field.getName() + " er mappet til flere kolonner: " + Arrays.toString(propertyColumnNames), logger);
                }
                sql.append(", ").append(propertyColumnNames[0]).append("=null");
            }
        }
        // Må "manuelt" sette primitivfelter til sine nye verdier.
        // Hvis de fortsetter å være null, så feiler initiell lasting av endret type. (SKIF-527)
        // Hvis de initialiseres til f.eks. 0, så kan det være en ulovlig verdi. (SKIF-660)
        List<StatementSetter> newPrimitives = new ArrayList<>();
        int nextSqlParamIndex = 1;
        if (!newPrimitiveFields.isEmpty()) {
            for (int i = 0; i < newPrimitiveFields.size(); i++) {
                final Field field = newPrimitiveFields.get(i);
                final int propertyIndex = toEntityPersister.getPropertyIndex(field.getName());
                final String[] propertyColumnNames = toEntityPersister.getPropertyColumnNames(propertyIndex);
                Type type = toEntityPersister.getPropertyTypes()[propertyIndex];
                if (propertyColumnNames.length != 1) {
                    throw new ImplementationException("Property " + field.getName() + " er mappet til flere kolonner: " + Arrays.toString(propertyColumnNames), logger);
                }
                sql.append(", ").append(propertyColumnNames[0]).append("=?");
                final int sqlParamIndex = nextSqlParamIndex++;
                final Object value = toEntityPersister.getPropertyValue(currentObject, propertyIndex);
                newPrimitives.add(preparedStatement -> type.nullSafeSet(preparedStatement, value, sqlParamIndex, (SessionImplementor) session()));
            }
        }

        sql.append(" where id=").append(currentObject.getId().getValue());

        final String sqlString = sql.toString();
        logger.debug(sqlString);
        session().doWork(connection -> {
            try (PreparedStatement statement = connection.prepareStatement(sqlString)) {
                for (StatementSetter newPrimitive : newPrimitives) {
                    newPrimitive.set(statement);
                }
                int rows = statement.executeUpdate();
                if (rows != 1) {
                    throw new ImplementationException("When changing type, the number of updated rows should be 1, but it turned out to be " + rows, logger);
                }
            }
        });
    }
    @FunctionalInterface
    interface StatementSetter {
        void set(PreparedStatement preparedStatement) throws SQLException;
    }

    /**
     * Når et objekt skal endre type, må alle felter som finnes i fra-typen men ikke i til-typen, blankes ut.
     * Det vil si at kolleksjoner må tømmes og referanser må settes til <code>null</code>.
     * <p>
     * Primitive felter kan ikke nulles ut. Disse blir returnert slik at de kan nulles ut med SQL, om mulig.
     *
     * @param bubbleObject den gamle utgaven av objektet med sin gamle type
     * @param tilClass     typen objektet skal endres til
     * @return liste over primitive felter som må nulles ut med SQL
     * @throws IllegalAccessException dersom det av en eller annen grunn ikke er mulig å få tak i noen av feltene
     * @since 2.1
     */
    private static List<Field> blankUtIkkeFellesFelter(BubbleObject bubbleObject, Class<? extends BubbleObject> tilClass) throws IllegalAccessException {
        ArrayList<Field> primitiveFields = new ArrayList<>();

        for (Class<?> clazz = bubbleObject.getClass(); !clazz.isAssignableFrom(tilClass); clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                // Ikke nullstill statiske felter
                if ((field.getModifiers() & (Modifier.STATIC | Modifier.FINAL)) == 0) {
                    field.setAccessible(true);
                    if (Collection.class.isAssignableFrom(field.getType())) {
                        ((Collection<?>) field.get(bubbleObject)).clear();
                    } else if (!field.getType().isPrimitive()) {
                        field.set(bubbleObject, null);
                    } else {
                        primitiveFields.add(field);
                    }
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Bobletypeendring: Blanker ikke ut felt " + field);
                }
            }
        }

        return primitiveFields;
    }

    /**
     * Når et objekt skal endre type, må alle primitive felter som kun finnes i den nye typen få en lovlig verdi.
     * Denne metoden finner de feltene som er primitiver siden siste felles klasse.
     *
     * @param fraClass     typen objektet endres fra
     * @param tilClass     typen objektet skal endres til
     * @return liste over primitive felter som må initialiseres med SQL
     * @since 2.4.5
     */
    private static List<Field> findNyePrimitiveFelter(Class<? extends BubbleObject> fraClass, Class<? extends BubbleObject> tilClass) {
        ArrayList<Field> primitiveFields = new ArrayList<>();

        for (Class<?> clazz = tilClass; !clazz.isAssignableFrom(fraClass); clazz = clazz.getSuperclass()) {
            for (Field field : clazz.getDeclaredFields()) {
                // Ikke vurderer statiske og transiente felter
                if ((field.getModifiers() & (Modifier.STATIC | Modifier.FINAL | Modifier.TRANSIENT)) == 0) {
                    if (field.getType().isPrimitive()) {
                        primitiveFields.add(field);
                    }
                }
            }
        }

        return primitiveFields;
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId) {
        lazyInitializer.evict(bubbleId);
    }

    protected boolean isSingleColumnType(Type type) {
        return type instanceof BasicType;

    }

    /**
     * Finn entity components for object og sjekk at disse alle er nye. Sjekker også objektet selv hvis det er en entity component
     *
     * @since 2.3
     */
    public void checkForStolenEntitiesInNewObject(Type objectType, Object object, IdentityHashMap<Object, Object> processedObjects, Set<EntityComponent> newlyInsertedComponents, CascadeStyle cascadeStyle) throws HibernateException {
        if (object == null) return;

        if (processedObjects.containsKey(object)) return;
        processedObjects.put(object, null);

        ClassMetadata classMetadata = HibernateHelper.getClassMetadata(session(), object);
        EntityPersister persister = (EntityPersister) classMetadata;

        if (objectType.isEntityType()) {
            if (object instanceof EntityComponent) {
                checkIsNewEntityComponent(persister.getEntityMetamodel(), (EntityComponent) object, newlyInsertedComponents);
            } //else {
                 // TODO: Handle non EntityComponent based entities
            //}
        }

        if (processedObjects.containsKey(object)) return;
        processedObjects.put(object, null);

        if (cascadeStyle.doCascade(CascadingActions.SAVE_UPDATE)) {

            Type[] types = persister.getPropertyTypes();
            Object[] values = persister.getPropertyValues(object);
            CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();

            for (int i = 0; i < types.length; i++) {
                Type type = types[i];
                Object value = values[i];
                CascadeStyle cascadeStyleForElement = cascadeStyles != null ? cascadeStyles[i] : CascadeStyles.NONE;
                if (type.isEntityType()) {
                    checkForStolenEntitiesInNewObject(type, value, processedObjects, newlyInsertedComponents, cascadeStyleForElement);
                } else if (type.isComponentType()) {
                    checkForStolenEntitiesInNewObject(type, value, processedObjects, newlyInsertedComponents, cascadeStyleForElement);
                } else if (type.isCollectionType()) {
                    CollectionType collectionType = (CollectionType) type;
                    if (value instanceof Map) {
                        checkForStolenEntitiesInNewObjectForMap((Map<?, ?>) value, collectionType, processedObjects, cascadeStyleForElement);
                    } else {
                        Type elementType = collectionType.getElementType(((SessionImplementor) session()).getSessionFactory());
                        checkForStolenEntitiesInNewObjectForCollection(elementType, (Collection<?>) value, processedObjects, newlyInsertedComponents, cascadeStyleForElement);
                    }
                }
            }
        }
    }

    // TODO: Denne gjør ikke noe av det den sier den gjør
    private void checkForStolenEntitiesInNewObjectForMap(Map<?, ?> mapInObject, CollectionType collectionType, IdentityHashMap<Object, Object> processedObjects, CascadeStyle cascadeStyleForElement) {
        AbstractCollectionPersister collectionPersister = (AbstractCollectionPersister) lazyInitializer.getMetamodel().collectionPersister(collectionType.getRole());

        if (!(collectionPersister.getKeyType() instanceof BasicType)) {
            throw new ImplementationException("Key must be BasicType");
        }

        // Sjekk at det ikke er noen collections inni her
        Type elementType = collectionPersister.getElementType();
        if (elementType.isCollectionType()) {
            throw new NotImplementedException("Map value kan ikke være collection");
        } else if (elementType.isAssociationType()) {
            EntityPersister entityPersister = collectionPersister.getElementPersister();
            Type[] propertyTypes = entityPersister.getPropertyTypes();
            for (int i = 0; i < propertyTypes.length; i++) {
                Type propertyType = propertyTypes[i];
                if (propertyType.isAssociationType() || propertyType.isComponentType()) {
                    throw new NotImplementedException("Map value må være enkel verdi eller ett-nivå entity");
                }
            }
        }
    }

    /**
     * Finn entity components for object og legg disse inn i {@code groupedEntityComponents} som er sortert på level og klasse
     *
     * @since 2.3
     */
    public void fixBatchingForObjectWithEntityComponents(Object object, IdentityHashMap<Object, Object> processedObjects, int levelKey, List<Multimap<Class<? extends EntityComponent>, EntityComponent>> batchGroupedEntityComponents) throws HibernateException {
        if (object == null) return;

        if (processedObjects.containsKey(object)) return;
        processedObjects.put(object, null);

        ClassMetadata classMetadata = HibernateHelper.getClassMetadata(session(), object.getClass());

        if (erAvTypeSomIkkeSkalInitialiseresVidere(classMetadata)) return;
        EntityPersister persister = (EntityPersister) classMetadata;

        Type[] types = persister.getPropertyTypes();
        Object[] values = persister.getPropertyValues(object);
        CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();

        for (int i = 0; i < types.length; i++) {
            Type type = types[i];
            Object value = values[i];
            CascadeStyle cascadeStyle = cascadeStyles != null ? cascadeStyles[i] : null;

            if (type.isEntityType()) {
                if (cascadeStyle != null && cascadeStyle.doCascade(CascadingActions.SAVE_UPDATE)) {
                    fixForBatchInsertUpdateOfEntityComponent((EntityType) type, value, processedObjects, levelKey, batchGroupedEntityComponents);
                }
//            } else if (type.isComponentType()) {
//                checkEntityComponentsOnInsertInComponent(value, type, processedObjects);
//            } else if (type.isCollectionType()) {
//                boolean cascade = cascadeStyle != null && cascadeStyle.doCascade(CascadingAction.SAVE_UPDATE);
//                if (value instanceof Map) {
//                    CollectionType collectionType = (CollectionType) type;
//                    checkEntityComponentsInMapOnInsert((Map) value, collectionType, processedObjects, cascade);
//                } else {
//                    CollectionType collectionType = (CollectionType) type;
//                    Type elementType = collectionType.getElementType(((SessionImpl) session()).getFactory());
//                    checkForStolenEntitiesInNewObjectForCollection((Collection) value, elementType, processedObjects, cascade);
//                }
            }
        }
    }


    private void fixForBatchInsertUpdateOfEntityComponent(EntityType type, Object value, IdentityHashMap<Object, Object> processedObjects, int levelKey, List<Multimap<Class<? extends EntityComponent>, EntityComponent>> groupedEntityComponents) {
        Class<?> typeClass = type.getReturnedClass();
        // TODO: mangler å opptimalisere på tvers av subklasser
        if (value != null && EntityComponent.class.isAssignableFrom(typeClass)) {
            EntityComponent component = (EntityComponent) value;
            if (groupedEntityComponents.size() == levelKey) {
                groupedEntityComponents.add(HashMultimap.create());
            }
            groupedEntityComponents.get(levelKey).put(component.getClass(), component);
            fixBatchingForObjectWithEntityComponents(value, processedObjects, levelKey + 1, groupedEntityComponents);
        }
    }
}
