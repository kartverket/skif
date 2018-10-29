package no.statkart.skif.store.persistence.hibernate;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import no.statkart.skif.exception.*;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.store.*;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.util.CopyHelper;
import no.statkart.skif.util.HibernateHelper;
import org.hibernate.*;
import org.hibernate.LockMode;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.*;
import org.hibernate.id.Assigned;
import org.hibernate.impl.SessionImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.collection.AbstractCollectionPersister;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * @author Henrik Fredholm
 */
@SuppressWarnings({"ForLoopReplaceableByForEach", "WeakerAccess"})
public abstract class HibernatePersistenceSessionMasterImpl implements HibernatePersistenceSessionMaster {
    protected static Logger logger = LoggerFactory.getLogger(HibernatePersistenceSessionMasterImpl.class);
    private static final int CRITERIA_BATCH_POWER = 9;
    private static final String ID_KOLONNE_NAVN = "id";

    protected final HibernateSessionFactoryManager sessionFactoryManager;
    protected final HibernateSessionFactoryDescriptor sessionFactoryDescriptor;

    /* Holder referanse til hibernate sesjonen */
    protected Session lazySession;

    /* Holder referanse til alle bobler lastet i denne sesjonen som allerede er fullt initialisert */
    private Map<BubbleId, BubbleObject> fullyInitializedBubbles = new HashMap<>();
    private Map<BubbleId, BubbleObject> exportedLazyLoadedBubbles = new HashMap<>();

    private int reserveCount = 0;

    protected Transaction localTransaction;

    /**
     * Brukes til å identifisere entity components som er nye etter at de har fått tildelt id. For å få batching av
     * bobler til å virke er det nødvendig at entities insertes før boblene insertes eller oppdateres.
     * Derved får entity components tildelt id og boblen kan ikke lengre se at den er ny.
     * Bruker IdentityHashSet fordi det er mest logisk å bruke dette en id for sammenlikning
     */
    protected Set<EntityComponent> newlyInsertedComponents = Sets.newIdentityHashSet();

    /**
     * Bestemmer om Bubbler kan ha lazyloaded assosiasjoner som ikke er initialisert i det bubblen
     * utleveres fra HibernateSessionWrapper
     */
    private boolean lazyLoadedBubblesAllowedDefault = false;
    private boolean lazyLoadedBubblesAllowed = lazyLoadedBubblesAllowedDefault;

    public HibernatePersistenceSessionMasterImpl(HibernateSessionFactoryManager sessionFactoryManager) {
        this.sessionFactoryManager = sessionFactoryManager;
        sessionFactoryDescriptor = sessionFactoryManager.getDescriptor();
    }

    public Map<BubbleId, BubbleObject> getFullyInitializedBubbles() {
        return fullyInitializedBubbles;
    }

    @SuppressWarnings("UnusedDeclaration") // Public API
    public Map getExportedLazyLoadedBubbles() {
        return exportedLazyLoadedBubbles;
    }

    @SuppressWarnings("UnusedDeclaration") // Public API
    public int getReserveCount() {
        return reserveCount;
    }

    @Override
    public SnapshotVersion getSnapshot() {
        return sessionFactoryDescriptor.getSnapshotVersion();
    }

    @Override
    public SnapshotVersion setSnapshot(SnapshotVersion snapshotVersion) {
        SnapshotVersion previous = sessionFactoryDescriptor.getSnapshotVersion();
        if (snapshotVersion != previous) {
            if (reserveCount > 0) {
                throw new ImplementationException("Session is reserved and can not change SnapshotVersion from " + previous + " to " + snapshotVersion);
            }
            flushAndClearLoadedObjects();
            sessionFactoryDescriptor.setSnapshotVersion(session(), snapshotVersion);
        }
        return previous;
    }

    protected void flushAndClearLoadedObjects() {
        flush();
        ensureBubblesFullyLoaded();
        session().clear();
        exportedLazyLoadedBubbles.clear();
        fullyInitializedBubbles.clear();
    }

    @Override
    public void clear() {
        session().clear();
        exportedLazyLoadedBubbles.clear();
        fullyInitializedBubbles.clear();
        lazyLoadedBubblesAllowed = lazyLoadedBubblesAllowedDefault;
    }

    @Override
    public boolean isSnapshotChangable() {
        return sessionFactoryDescriptor.isSnapshotChangable();
    }

    @Override
    public PersistenceSessionForSnapshot getForBubbleId(Class<? extends BubbleId> type) {
        return this;
    }

    @Override
    public <T extends PersistenceSessionForSnapshot> T getImplementation(Class<T> interfaceType) {
        return interfaceType.cast(this);
    }

    @Override
    public boolean acceptsSnapshot(SnapshotVersion snapshotVersion) {
        return sessionFactoryDescriptor.accepts(snapshotVersion);
    }

    protected synchronized Session session() {
        //ToDo: MAT-9784 Har lagt inn '|| !lazySession.isOpen()' fordi vi har hatt situasjonen at lazySession ikke er null, men er lukket.
        if (lazySession == null || !lazySession.isOpen()) {
            lazySession = sessionFactoryManager.createSession();
            sessionFactoryDescriptor.setSnapshotVersion(lazySession, sessionFactoryDescriptor.getSnapshotVersion());
        }
        return lazySession;
    }

    @Override
    public void close() {
        clear();
        try {
            if (lazySession != null) {
                lazySession.close();
                lazySession = null;
            }
        } finally {
            sessionFactoryDescriptor.resetSeed();
        }
        localTransaction = null;
        reserveCount = 0;
    }

    @Override
    public Session reserveSession() {
        if (sessionFactoryDescriptor.isSnapshotChangable()) {
            reserveCount++;
        }
        return session();
    }

    @Override
    public void releaseSession() {
        if (sessionFactoryDescriptor.isSnapshotChangable()) {
            if (reserveCount > 0) {
                reserveCount--;
            } else {
                throw new ImplementationException("Session is not reserved");
            }
        }
    }


    /**
     * Laster en boble basert på boblens id.
     *
     * @param bubbleId id for boblen som skal hentes
     * @return en domeneboble av typen <code>MatrikkelBubbleObject</code>
     */
    @Override
    public <T extends BubbleObject> T get(BubbleId<? extends T> bubbleId) {
        T bubble;

        checkSnapshotVersion(bubbleId);
        try {
            bubble = getFromHibernateSessionOrLoad(bubbleId);
            if (bubble == null)
                throw new ObjectNotFoundException(bubbleId);
            if (!isLazyLoadedBubblesAllowed()) {
                ensureFullyLoaded(bubble);
            } else {
                exportedLazyLoadedBubbles.put(bubble.getId(), bubble);
            }
            return bubble;
        } catch (HibernateException e) {
            throw new no.statkart.skif.exception.ObjectNotFoundException(bubbleId, e);
        }
    }

    /**
     * Laster et set av domenebobler basert på boblenes id'er. Dersom alle finnes i minne vil det ikke bli gjort kald til
     * databasen
     *
     * @param bubbleIds ider for bobler som skal lastest.
     * @return fundne objekter; et objekt per id.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> get(Collection<I> bubbleIds) {
        List<T> alreadyLoaded = new ArrayList<>(bubbleIds.size());
        Set<I> idsToLoad = new HashSet<>(bubbleIds.size());

        for (I bubbleId : bubbleIds) {
            checkSnapshotVersion(bubbleId);
            T bubble;
            if ((bubble = (T) fullyInitializedBubbles.get(bubbleId)) != null) {
                alreadyLoaded.add(bubble);
            } else if ((bubble = (T) exportedLazyLoadedBubbles.get(bubbleId)) != null) {
                alreadyLoaded.add(bubble);
            } else if ((bubble = lookupInHibernateCache(bubbleId)) != null) {
                exportedLazyLoadedBubbles.put(bubbleId, bubble);
                alreadyLoaded.add(bubble);
            } else if ((bubble = lookupInHibernateCache(bubbleId)) != null) {
                alreadyLoaded.add(bubble);
            } else {
                idsToLoad.add(bubbleId);
            }
        }
        Set<T> result;
        if (idsToLoad.size() > 0) {
            FlushMode oldFlushMode = session().getFlushMode();
            try {
                // Disable flush. Det søkes kun etter objekter som allerede finnes i databasen
                session().setFlushMode(FlushMode.MANUAL);

                result = loadAllFromDatabase(idsToLoad);
                if (lazyLoadedBubblesAllowed) {
                    for (T bubble : result) {
                        exportedLazyLoadedBubbles.put(bubble.getId(), bubble);
                    }
                }
                result.addAll(alreadyLoaded);
            } finally {
                session().setFlushMode(oldFlushMode);
            }
        } else {
            result = new HashSet<>(alreadyLoaded);
        }

        if (!lazyLoadedBubblesAllowed) {
            for (T bubble : result) {
                ensureFullyLoaded(bubble);
            }
        }
        if (bubbleIds.size() != result.size()) {
            Set<BubbleId<?>> ids = new HashSet<BubbleId<?>>(bubbleIds);
            ids.removeAll(Bubbles.asIds(result));
            throw new ObjectsNotFoundException(ids);
        }
        return result;
    }

    /**
     * Knytter {@code bubbleObject} til underliggende hibernate sesison. Ved senere kall til flush() vil endringene
     * bli sendt til databasen.
     */
    @Override
    public <T extends BubbleObject> void insert(T bubbleObject) {
        try {
            checkEntityComponentsOnInsert(bubbleObject);
            session().save(bubbleObject);
        } catch (HibernateException e) {
            throw new ImplementationException("Insert failed for " + bubbleObject, e);
        }
    }

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
    @Override
    public <T extends BubbleObject> void update(T bubbleObject) {
        List<Multimap<Class<? extends EntityComponent>, EntityComponent>> orphanOneToOneEntityComponents = Lists.newArrayList();
        try {
            assignPersistentCollectionsAndConvertObjectIfTypeChangedAndEvictOtherInstance(bubbleObject, orphanOneToOneEntityComponents);
            Session session = session();
            session.update(bubbleObject); // Viktig at class-mapping inneholder 'select-before-update="true"'. Dette bør settes automatisk ved konfigurasjon av hibernate session factory.
            for (int i = orphanOneToOneEntityComponents.size() - 1; i >= 0; i--) {
                Multimap<Class<? extends EntityComponent>, EntityComponent> multimap = orphanOneToOneEntityComponents.get(i);
                for (EntityComponent orphanEntity : multimap.values()) {
                    session.delete(orphanEntity);
                }
            }
        } catch (HibernateException | SQLException e) {
            throw new ImplementationException("Update failed for " + bubbleObject, e);
        }
    }

    /**
     * Sletter objekt som har samme id som {@code bubbeObject} fra underliggende hibernate session. Ved senere kall til
     * flush() vil endringene bli sendt til databasen.
     * <p>
     * Slettingen utføres med det objektet som ligger i Hibernate (det lastes eventuelt inn hvis det ikke allerede er
     * lastet), ikke med det objektet som kommer inn, som kan være en annen detached instans.
     *
     * @param bubbleObject objekt som inneholder id for det objekt som skal slettes
     */
    @Override
    public <T extends BubbleObject> void delete(T bubbleObject) {
        try {
            Object obj = getFromHibernateSessionOrLoad(bubbleObject.getId());
            fullyInitializedBubbles.remove(bubbleObject.getId());
            exportedLazyLoadedBubbles.remove(bubbleObject.getId());
            session().delete(obj);
        } catch (HibernateException e) {
            throw new ImplementationException("Delete failed for " + bubbleObject, e);
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
     * @throws java.sql.SQLException dersom SQL feiler ved typeendring
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
        BubbleObject existingBubble = (BubbleObject) session().get(bubbleObject.getBubbleId().getBaseType(), bubbleObject.getBubbleId(), LockMode.NONE);
        if (existingBubble != bubbleObject) {
            ensureFullyLoaded(existingBubble); // TODO: Håndter lazy loaded collections. Må pt kalle ensureFullyLoaded fordi attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntityComponents pt ikke håndtere lazyloaded collections.

            // Typeendring må skje før attachPersistenceCollection
            if (!(bubbleObject.getClass().isInstance(existingBubble))) {
                changeType(bubbleObject, existingBubble);

                // Les inn objektet på nytt med den nye typen. changeType har evictet.
                existingBubble = (BubbleObject) session().get(bubbleObject.getBubbleId().getBaseType(), bubbleObject.getBubbleId(), LockMode.NONE);
                ensureFullyLoaded(existingBubble); // TODO: Håndter lazy loaded collections. Må pt kalle ensureFullyLoaded fordi attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntityComponents pt ikke håndtere lazyloaded collections.
            }

            attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntityComponents(bubbleObject, existingBubble, orphanOneToOneEntityComponents);

            // Objektet har ikke endret type, bare hiv ut gammel versjon fra Hibernate.
            if (logger.isDebugEnabled()) {
                logger.debug("Evict av annen boble instans assosiert med sessionen : " + bubbleObject.getBubbleId());
            }
            // Dersom den gamle instansen har blitt endret vil hibernate kunne gi en "postInsert feil: possible nonthreadsafe access to session"
            // Denne feilen kan unngåes hvis man gjør en session.flush() her slik at alle endringer fra den gamle instansen
            // kommer ned i databasen. Rammeverket skal dog fange opp og hindre at det oppdateres på flere instanser av
            // samme objekt innenfor samme session. Det skal derfor ikke være noen flush her.
            //session.flush();
            // La den nye versjonen erstatte den gamle. Det er tryggt å anta at denne også er fullt initialisert.
            fullyInitializedBubbles.put(bubbleObject.getBubbleId(), bubbleObject);

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
        Object[] values = persister.getPropertyValues(object, EntityMode.POJO);
        CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();

        EntityPersister persisterExisting = (EntityPersister) ((SessionImpl) session()).getFactory().getClassMetadata(existingObject instanceof HibernateProxy ? existingObject.getClass().getSuperclass() : existingObject.getClass());
        Type[] typesExisting = persisterExisting.getPropertyTypes();
        Object[] valuesExisting = persisterExisting.getPropertyValues(existingObject, EntityMode.POJO);

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
            CascadeStyle cascadeStyle = cascadeStyles != null ? cascadeStyles[i] : CascadeStyle.NONE;
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
                // Kan ikke attache collections i entity hvis det ikke var noe fra før
                attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntities(value, valueExisting, processedObjects, nestingLevel + 1, orphanOneToOneEntityComponents);
            }
        } else if (type.isComponentType()) {
            attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForComponent(value, valueExisting, type, processedObjects, nestingLevel, orphanOneToOneEntityComponents);
        } else if (type.isCollectionType()) {
            if (valueExisting instanceof Map) {
                Map mapWithSnapshot = attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForMap((Map) value, (Map) valueExisting, true);
                persister.setPropertyValue(object, i, mapWithSnapshot, EntityMode.POJO);
            } else {
                CollectionType collectionType = (CollectionType) type;
                Type elementType = collectionType.getElementType(((SessionImpl) session()).getFactory());
                Collection collectionWithSnapshot = attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForCollection((Collection) value, (Collection) valueExisting, elementType, processedObjects, nestingLevel, orphanOneToOneEntityComponents);
                persister.setPropertyValue(object, i, collectionWithSnapshot, EntityMode.POJO);
            }
        } else if (!isSingleColumnType(type) // ting som ligger i én kolonne (Primitiver, String, o.l.). Disse kan ikke ha collections.
                && !(type instanceof CustomType)) { // CustomType har nok heller ingen collections i seg.
            throw new NotImplementedException();
            // TODO: Trenger et test eksempel for å skrive denne koden riktig
        }
    }

    /**
     * Legge inn orphan entity component i {@code orphanOneToOneEntityComponents}.
     */
    protected void addOrphanOneToOneEntityComponent(int nestingLevel, EntityComponent valueExisting, List<Multimap<Class<? extends EntityComponent>, EntityComponent>> orphanOneToOneEntityComponents) {
        while (orphanOneToOneEntityComponents.size() <= nestingLevel) {
            orphanOneToOneEntityComponents.add(HashMultimap.<Class<? extends EntityComponent>, EntityComponent>create());
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
                        properties[j] = attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForMap((Map) property, (Map) propertyExisting, true);
                    } else {
                        CollectionType collectionType = (CollectionType) propertyType;
                        Type elementType = collectionType.getElementType(((SessionImpl) session()).getFactory());
                        properties[j] = attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForCollection((Collection) property, (Collection) propertyExisting, elementType, processedObjects, nestingLevel, orphanOneToOneEntityComponents);
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
    abstract protected CascadeStyle getCascadeStyle(Type componentType, int i);

    /**
     * Typehieraki for component ser forskjelllig i Hibernate 3.2 og 3.6. Må derfor bruke hjelpemetoder til å caste
     * riktig.
     *
     * @since 2.3
     */
    abstract protected void setPropertyValues(Type componentType, Object component, Object[] properties);

    /**
     * Typehieraki for component ser forskjelllig i Hibernate 3.2 og 3.6. Må derfor bruke hjelpemetoder til å caste
     * riktig.
     *
     * @since 2.3
     */
    abstract protected Object[] getPropertyValues(Type componentType, Object component);

    /**
     * Typehieraki for component ser forskjelllig i Hibernate 3.2 og 3.6. Må derfor bruke hjelpemetoder til å caste
     * riktig.
     *
     * @since 2.3
     */
    abstract protected Type[] getSubtypes(Type componentType);

    protected Map attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForMap(Map mapInObject, Map mapInExistingObject, boolean cascade) {
        if (mapInExistingObject instanceof PersistentCollection) {
            if (mapInObject instanceof PersistentCollection && !((PersistentCollection) mapInObject).wasInitialized()) {
                // Hvis map ikke er initialisert, er det heller ikke gjort endringer på den
                return CopyHelper.copy(mapInExistingObject);
            }
            final CollectionEntry entry = ((SessionImpl) session()).getPersistenceContext().getCollectionEntry((PersistentCollection) mapInExistingObject);
            final AbstractCollectionPersister collectionPersister = (AbstractCollectionPersister) entry.getLoadedPersister();

            if (!(collectionPersister.getKeyType() instanceof LiteralType)) {
                throw new ImplementationException("Key must be LiteralType");
            }

            Map persistentCollection = CopyHelper.copy(mapInExistingObject);
            persistentCollection.clear();
            if (mapInObject != null) {
                //noinspection unchecked
                persistentCollection.putAll(mapInObject);
            }
            if (cascade) {
                // Sjekk at det ikke er noen collections inni her
                Type elementType = collectionPersister.getElementType();
                if (elementType.isCollectionType()) {
                    throw new NotImplementedException("Map value kan ikke være collection");
                } else if (elementType.isAssociationType()) {
                    EntityPersister entityPersister = collectionPersister.getElementPersister();
                    Type[] propertyTypes = entityPersister.getPropertyTypes();
                    //noinspection ForLoopReplaceableByForEach
                    for (int i = 0; i < propertyTypes.length; i++) {
                        Type propertyType = propertyTypes[i];
                        if (propertyType.isAssociationType() || propertyType.isComponentType()) {
                            throw new NotImplementedException("Map value må være enkel verdi eller ett-nivå entity");
                        }
                    }
                }
            }
            return persistentCollection;
        } else {
            // TODO: Alternativt returner eksisterende map. Kanskje det er greit?
            throw new ImplementationException("Unable to attach persistent collection, as existing object has map that isn't PersistentCollection");
            //return collectionInOject;
        }
    }

    protected Collection attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForCollection(Collection collectionInObject, Collection collectionInExistingObject, Type elementType, IdentityHashMap<Object, Object> processedObjects, int nestingLevel, List<Multimap<Class<? extends EntityComponent>, EntityComponent>> orphanOneToOneEntityComponents) throws HibernateException {
        if (collectionInExistingObject instanceof PersistentCollection) {
            if (collectionInObject instanceof PersistentCollection && !((PersistentCollection) collectionInObject).wasInitialized()) {
                // Hvis collection ikke er initialisert, er det heller ikke gjort endringer på den
                return CopyHelper.copy(collectionInExistingObject);
            } else {
                Collection persistentCollection = CopyHelper.copy(collectionInExistingObject);
                persistentCollection.clear();
                if (collectionInObject != null) {
                    //noinspection unchecked
                    persistentCollection.addAll(collectionInObject);
                }
                checkForStolenEntityComponent(elementType, collectionInObject, collectionInExistingObject);
                attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForCollectionCascade(persistentCollection, collectionInExistingObject, processedObjects, nestingLevel, orphanOneToOneEntityComponents);
                return persistentCollection;
            }
        } else {
            // TODO: Alternativt returner eksisterende collection. Kanskje det er greit?
            throw new ImplementationException("Unable to attach persistent collection, as existing object has collection that isn't PersistentCollection");
            //return collectionInObject;
        }
    }

    protected void attachPersistenceCollectionWithSnapshotOfOldStateAndCollectOrphanEntitiesForCollectionCascade(Collection collectionInOject, Collection collectionInExistingObject, IdentityHashMap<Object, Object> processedObjects, int nestingLevel, List<Multimap<Class<? extends EntityComponent>, EntityComponent>> orphanOneToOneEntityComponents) throws HibernateException {
        SessionImpl sessionImpl = (SessionImpl) session();
        CollectionEntry entry = sessionImpl.getPersistenceContext().getCollectionEntry((PersistentCollection) collectionInExistingObject);
        final CollectionPersister collectionPersister = entry.getLoadedPersister();
        Type elementType = collectionPersister.getElementType();
        if (elementType.isEntityType()) {
            final EntityPersister elementPersister = ((AbstractCollectionPersister) collectionPersister).getElementPersister();
            Map<Serializable, Object> oldElementMap = Maps.newHashMap();
            for (Object o : collectionInExistingObject) {
                oldElementMap.put(elementPersister.getIdentifier(o, sessionImpl), o);
            }
            for (Object object : collectionInOject) {
                final Serializable identifier = elementPersister.getIdentifier(object, sessionImpl);
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
        Object[] values = persister.getPropertyValues(object, EntityMode.POJO);
        CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();

        for (int i = 0; i < types.length; i++) {
            Type type = types[i];
            Object value = values[i];
            CascadeStyle cascadeStyle = cascadeStyles != null ? cascadeStyles[i] : CascadeStyle.NONE;

            if (type.isEntityType()) {
                checkForStolenEntitiesInNewObject(type, value, processedObjects, newlyInsertedComponents, cascadeStyle);
            } else if (type.isComponentType()) {
                checkEntityComponentsOnInsertInComponent(value, type, processedObjects);
            } else if (type.isCollectionType()) {
                if (value instanceof Map) {
                    CollectionType collectionType = (CollectionType) type;
                    checkEntityComponentsInMapOnInsert(collectionType, true);
                } else {
                    CollectionType collectionType = (CollectionType) type;
                    Type elementType = collectionType.getElementType(((SessionImpl) session()).getFactory());
                    checkForStolenEntitiesInNewObjectForCollection(elementType, (Collection) value, processedObjects, newlyInsertedComponents, cascadeStyle);
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

                if (cascadeStyle != null && cascadeStyle.doCascade(CascadingAction.SAVE_UPDATE)) {
                    // Hver property kan enten være et simple objekt (f.eks Long), complex objekt (f.eks Boundary) eller en collection
                    if (propertyType.isEntityType()) {
                        //checkForReplacedOrStolenEntityComponentInV32((EntityType) propertyType, property, null);
                        checkEntityComponentsOnInsert(property, processedObjects);
                    } else if (propertyType.isCollectionType()) {
                        // For hvert element
                        if (property instanceof Map) {
                            CollectionType collectionType = (CollectionType) propertyType;
                            checkEntityComponentsInMapOnInsert(collectionType, true);
                        } else {
                            CollectionType collectionType = (CollectionType) propertyType;
                            Type elementType = collectionType.getElementType(((SessionImpl) session()).getFactory());
                            checkForStolenEntitiesInNewObjectForCollection(elementType, (Collection) property, processedObjects, newlyInsertedComponents, cascadeStyle);
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

    private void checkEntityComponentsInMapOnInsert(CollectionType collectionType, boolean cascade) {
        AbstractCollectionPersister collectionPersister = (AbstractCollectionPersister) session().getSessionFactory().getCollectionMetadata(collectionType.getRole());

        if (!(collectionPersister.getKeyType() instanceof LiteralType)) {
            throw new ImplementationException("Key must be LiteralType");
        }

        if (cascade) {
            // Sjekk at det ikke er noen collections inni her
            Type elementType = collectionPersister.getElementType();
            if (elementType.isCollectionType()) {
                throw new NotImplementedException("Map value kan ikke være collection");
            } else if (elementType.isAssociationType()) {
                EntityPersister entityPersister = collectionPersister.getElementPersister();
                Type[] propertyTypes = entityPersister.getPropertyTypes();
                //noinspection ForLoopReplaceableByForEach
                for (int i = 0; i < propertyTypes.length; i++) {
                    Type propertyType = propertyTypes[i];
                    if (propertyType.isAssociationType() || propertyType.isComponentType()) {
                        throw new NotImplementedException("Map value må være enkel verdi eller ett-nivå entity");
                    }
                }
            }
        }
    }

    private void checkForStolenEntitiesInNewObjectForCollection(Type elementType, Collection collectionInObject, IdentityHashMap<Object, Object> processedObjects, Set<EntityComponent> newlyInsertedComponents, CascadeStyle cascadeStyle) throws HibernateException {
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
    protected abstract boolean isNewOrReplacedEntityComponent(EntityType type, Object value, Object valueExisting, IdentityHashMap<Object, Object> processedObjects, int nestingLevel, List<Multimap<Class<? extends EntityComponent>, EntityComponent>> orphanOneToOneEntityComponents, CascadeStyle cascadeStyle);

    protected void checkIsNewEntityComponent(EntityMetamodel entityMetamodel, EntityComponent component, Set<EntityComponent> newlyInsertedComponents) {
        if (component.getId() != null
                && !newlyInsertedComponents.contains(component)
                && !(entityMetamodel.getIdentifierProperty().getIdentifierGenerator() instanceof Assigned)) {
            throw new ImplementationException("Found entity component that is not new. Class: " + entityMetamodel.getEntityType().getReturnedClass().getName() + " Id:" + component.getId(), logger);
        }
    }

    /**
     * Sjekker om <code>value</code> er en EntityComponent har blitt erstattet med en annen eller <code>null</code>.
     *
     * @param elementType        typen til elementene i collection
     * @param collection         nåværende collection
     * @param collectionExisting forrige collection
     * @throws ImplementationException dersom collection innholder entity component med id som ikke fins i collectionExisting
     * @since 2.2.0
     */
    private void checkForStolenEntityComponent(Type elementType, Collection<?> collection, Collection<?> collectionExisting) {
        if (elementType.isEntityType()) {
            Class typeClass = elementType.getReturnedClass();
            if (EntityComponent.class.isAssignableFrom(typeClass)) {
                AbstractEntityPersister persister = (AbstractEntityPersister) ((SessionImpl) session()).getFactory().getClassMetadata(elementType.getName());
                EntityMetamodel entityMetamodel = persister.getEntityMetamodel();

                // Dersom komponentid er assigned, så kan vi ikke detektere stjeling av komponenter. Slike id-er finnes i matrikkel historikk. // TODO: jo vi kan siden vi ved hvilke objekter som insertes, ligger i eget set
                if (!(entityMetamodel.getIdentifierProperty().getIdentifierGenerator() instanceof Assigned)) {
                    Set<Object> existingIds = new HashSet<>();
                    for (Object o : collectionExisting) {
                        existingIds.add(((EntityComponent) o).getId());
                    }

                    for (Object o : collection) {
                        EntityComponent entityComponent = (EntityComponent) o;
                        if (entityComponent.getId() != null && !existingIds.contains(entityComponent.getId())) {
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
        final AbstractEntityPersister fromEntityPersister = (AbstractEntityPersister) getClassPersister(previousObject.getClass());
        final AbstractEntityPersister toEntityPersister = (AbstractEntityPersister) getClassPersister(currentObject.getClass());

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

        flush();
        evict(currentObject.getBubbleId());

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
                final Object value = toEntityPersister.getPropertyValue(currentObject, propertyIndex, EntityMode.POJO);
                newPrimitives.add(preparedStatement -> {
                    type.nullSafeSet(preparedStatement, value, sqlParamIndex, (SessionImplementor) session());
                });
            }
        }

        sql.append(" where id=").append(currentObject.getBubbleId().getValue());

        final String sqlString = sql.toString();
        logger.debug(sqlString);
        try (PreparedStatement statement = session().connection().prepareStatement(sqlString)) {
            for (StatementSetter newPrimitive : newPrimitives) {
                newPrimitive.set(statement);
            }
            int rows = statement.executeUpdate();
            if (rows != 1) {
                throw new ImplementationException("When changing type, the number of updated rows should be 1, but it turned out to be " + rows, logger);
            }
        }
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
                        ((Collection) field.get(bubbleObject)).clear();
                    } else if (!field.getType().isPrimitive()) {
                        field.set(bubbleObject, null);
                    } else {
                        primitiveFields.add(field);
                    }
                } else if (logger.isDebugEnabled()) {
                    logger.debug("Bobletypeendring: Blanker ikke ut felt " + field.toString());
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

    /*
     * Sjekk om hibernate har et objekt med samme id som <code>bubbleObject</code>, kast hibernate's
     * objekt ut av hibernate cache dersom objektet hibernate innehar ikke er identisk med
     * <code>bubbleObject</code>. Identisk betyr samme objekt-instans ikke equals-likhet.
     *
     * @param bubbleObject et objekt som kanskje er lastet gjennom hibernate tidligere i denne
     *                     sesjonen
     * @throws org.hibernate.HibernateException
     *          dersom evict(..) på hibernate session feiler
     /
    private void evictOtherInstanceFromHibernateSession(BubbleObject bubbleObject) throws HibernateException {
        // Hibernate tillater ikke update på et objekt når et annet objekt med samme id finnes i hibernate sin cache
        // Vi må teste på dette og evt. kaste ut det gamle objektet fra hibernate sin cache.
        // Dette kan kun testes ved å forsøke å loaded objektet og se om man får det samme objket som man
        // har fra før. Dersom objektet ikke var loadet vil Hibernate retunerer en proxy hvis objektet støtter
        // lazyloading. Dette kallet gir derfor ingen database aksess for objekter som støtter lazyloading. For objekter
        // som ikke støtter lazyloading vil dette gi en ekstra db access.
        Object obj = getFromHibernatePersistenceContext(bubbleObject.getId());
        if (obj != bubbleObject) {
            if (obj instanceof HibernateProxy) {
                // Hibernate hadde ikke noen anden boble instans, men vi må kaste ut den proxyen som ble
                // skapt av loaden ovenfor
            } else {
                if (logger.isDebugEnabled()) {
                    logger.debug("Evict av annen boble instans assosiert med sessionen : " + bubbleObject.getId());
                }
                // Dersom den gamle instansen har blitt endret vil hibernate kunne gi en "postInsert feil: possible nonthreadsafe access to session"
                // Denne feilen kan unngåes hvis man gjør en session.flush() her slik at alle endringer fra den gamle instansen
                // kommer ned i databasen. Rammeverket skal dog fange opp og hindre at det oppdateres på flere instanser av
                // samme objekt innenfor samme session. Det skal derfor ikke være noen flush her.
                //session.flush();
                // La den nye versjonen erstatte den gamle. Det er tryggt å anta at denne også er fullt initialisert.
                fullyInitializedBubbles.put(bubbleObject.getId(), bubbleObject);
            }
            session().evict(obj);
        } else {
            // Gjør ingen ting, bubbleObject er det objektet som allerede ligger i hibernate sessionen
        }
    }
    */

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId) {
        T bubble = lookupInHibernateCache(bubbleId);
        if (bubble != null) {
            session().evict(bubble);
            fullyInitializedBubbles.remove(bubbleId);
            exportedLazyLoadedBubbles.remove(bubbleId);
        }
    }

    @Override
    public <T extends BubbleObject> T refresh(BubbleId<? extends T> bubbleId) {
        //TODO: På grunn av feilen beskrevet i SKIF-231 har vi valgt å ikke bruke refresh på session her inntil videre.
        evict(bubbleId);
        return get(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> refresh(Collection<I> bubbleIds) {
        // TODO: Det er raskere å gjøre en evict etterfulgt av en get, men dette er egentlig problematisk da objektreferansen til
        // boblen blir skiftet ut. Da er det bedre å iterere over allerede lastede bobler å gjøre en refresh. Men hvorfor ta denne metode
        // en collection av id-er når refresh tar en boble som argument?. Per i dag er denne metode ikke i bruk så det har ikke
        // som mye å si.

        for (I bubbleId : bubbleIds) {
            evict(bubbleId);
        }
        return get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject> void refresh(T bubble) {
        fullyInitializedBubbles.remove(bubble.getId());
        session().refresh(bubble);
        if (!isLazyLoadedBubblesAllowed()) {
            ensureFullyLoaded(bubble);
        } else {
            exportedLazyLoadedBubbles.put(bubble.getId(), bubble);
        }
    }

    @Override
    public void ensureBubblesFullyLoaded() {
        for (Object bubble : exportedLazyLoadedBubbles.values()) {
            ensureFullyLoaded((BubbleObject) bubble);
        }
    }

    protected final <T extends BubbleObject, I extends BubbleId<? extends T>> void checkSnapshotVersion(I bubbleId) {
        SnapshotVersion snapshotVersionFromHolder = sessionFactoryDescriptor.getSnapshotVersion();
        SnapshotVersion snapshotVersionInId = bubbleId.getSnapshotVersion();

        if (!snapshotVersionFromHolder.equals(snapshotVersionInId)) {
            throw new ImplementationException("Id has wrong SnapshotVersion for session:" + bubbleId);
        }
    }

    /**
     * Laster alle fra database via criteria søk per type
     */
    private <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> loadAllFromDatabase(Set<I> ids) throws ObjectNotFoundException {
        Set<T> allEntities = new HashSet<>(ids.size());

        List<Criteria> criterias = buildCriterias(ids);
        for (Criteria criteria : criterias) {
            try {
                List objects = criteria.list();
                for (Iterator iterator = objects.iterator(); iterator.hasNext(); ) {
                    //noinspection unchecked
                    T bubbleObject = (T) iterator.next();
                    allEntities.add(bubbleObject);
                }
            } catch (HibernateException e) {
                throw new ImplementationException("Failed to load bubbles", e);
            }
        }
        return allEntities;
    }


    /**
     * Lager <code>Criteria</code>-objekter for lasting av domenebobler basert på deres id'er.
     *
     * @param ids et sett av id'er som det skal lages spørringer for
     * @return en liste av <code>Criteria</code>-objekter der hver criteria laster domenebobler av en
     *         bestemt type.
     */
    public List<Criteria> buildCriterias(Set<? extends BubbleId> ids) {
        List<Criteria> criterias = new ArrayList<>();

        LinkedHashMap<Class<?>, ? extends Set<? extends Serializable>> idsByType = getIdsByType(ids);
        for (Object o : idsByType.keySet()) {
            Class type = (Class) o;
            List<Criteria> criteriasForType = buildCriteriaForType(type, idsByType.get(type));
            criterias.addAll(criteriasForType);
        }
        return criterias;
    }


    /**
     * Create a map with a ids keyed on the clazz of entity that they defines the id for.
     *
     * @param ids a set of ids
     * @return a map of ids keyed by the clazz og the entity. The map has at predictable iteration order defined by
     * the iteration order of parameter {@code ids}
     */
    private <T extends BubbleObject, I extends BubbleId<? extends T>> LinkedHashMap<Class<?>, Set<I>> getIdsByType(Set<I> ids) {
        LinkedHashMap<Class<?>, Set<I>> idsByType = new LinkedHashMap<>();
        for (I id : ids) {
            Class entityClazz = id.getBaseType(); //Class name for the entity owning the id
            // Endringer ligger i mange forskjellige tabeller. Hvis vi gjør query via basetype må
            // Hibernate gjøre en masse joins.
            // TODO: Bruke Hibernate metadata til å finne ut av dette. Pt er Endring den eneste klasse som er slik.
            //if (entityClazz == Endring.class) {
            //    entityClazz = id.getType();
            //}
            if (!idsByType.containsKey(entityClazz)) {
                //Add an entry in the map for holding all ids of this typename
                idsByType.put(entityClazz, new HashSet<I>());
            }

            idsByType.get(entityClazz).add(id);
        }
        return idsByType;
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> T getFromHibernateSessionOrLoad(I bubbleId) {
        T bubble = getFromHibernatePersistenceContext(bubbleId);
        if (bubble == null) {
            //noinspection unchecked
            bubble = (T) session().get(bubbleId.getType(), bubbleId, LockMode.NONE);
        }
        return bubble;
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> T getFromHibernatePersistenceContext(I bubbleId) {
        final EntityPersister classPersister = getClassPersister(bubbleId.getType());
        final EntityKey key = new EntityKey(bubbleId, classPersister, EntityMode.POJO);
        //noinspection unchecked,UnnecessaryLocalVariable
        T bubble = (T) ((SessionImpl) session()).getPersistenceContext().getEntity(key);
        return bubble;
    }

    /**
     * Sørger for at objektet er fuldstendig lasteet. Dvs. at objektets  assosiasjoner er lastet
     * fra databasen. Hvis en assosiasjon er definert som cascade vil de assosierte objektene også
     * bli initialisert.
     *
     * @param bubble helt eller delvis initialisert bubble objekt
     */
    public <T extends BubbleObject> void ensureFullyLoaded(T bubble) {
        BubbleObject initializedBubble = fullyInitializedBubbles.get(bubble.getId());
        if (initializedBubble == null) {
            try {
                IdentityHashMap<Object, Object> initializedObjects = new IdentityHashMap<>();
                ensureInitialized(bubble, initializedObjects);
                fullyInitializedBubbles.put(bubble.getId(), bubble);
                exportedLazyLoadedBubbles.remove(bubble.getId());
            } catch (HibernateException e) {
                throw new ImplementationException("Could not initialize lazy loaded association for: " + bubble.getId(), e);
            }
        } else {
            // Check that we got the same bubble
            if (initializedBubble != bubble)
                throw new ImplementationException("Hibernate returned new instance of already loaded bubble: " + bubble.getId());
        }
    }

    /**
     * Sørger for at objektets assosiasjoner er blitt initialisert med data fra databasen. Hvis en
     * assosiasjon er definert som cascade vil metoden bli kaldt rekursivt på de assosierte
     * objekter.
     *
     * @param object             a helt eller delvis initialisert objekt.
     * @param initializedObjects set av objekter som methoden allerede har initialisert
     *
     */
    protected abstract void ensureInitialized(Object object, IdentityHashMap<Object, Object> initializedObjects) throws HibernateException;

    /**
     * SingleColumnType finnes ikke i 3.2.6 så her brukes test mot NullableType istedet
     */
    protected abstract boolean isSingleColumnType(Type type);

    protected boolean erAvTypeSomIkkeSkalInitialiseresVidere(ClassMetadata classMetadata) {
        return !(classMetadata instanceof EntityPersister);
    }


    /**
     * Lager <code>Criteria</code>-objekter for lasting av objekter av en angitt type. Pga.
     * begrensninger i Oracle for hvor mange uttrykk det kan være i en WHERE-claus så deles
     * spørringen opp i flere søk/criteria-objekter dersom listen over id'er er over størrelsen
     * definert i <code>OracleUtils.SQL_EXPRESSION_MAX_SIZE</code>.
     * <p>
     *
     * @param type klassen til domeneboblene som skal lastes
     * @param ids  et sett med id'er for domeneboblene
     * @return en liste med <code>Criteria</code>-objekter for uthenting av domeneboblene fra
     *         databasen
     */
    protected List<Criteria> buildCriteriaForType(Class type, Collection<? extends Serializable> ids) {
        List<Criteria> criterias = new ArrayList<>();
        Iterator<? extends Serializable> idIterator = ids.iterator();
        int size = ids.size();

        // Ved collection størrelse på 128, 256 eller mer sliter oracle. Deler derfor query opp i biter på 64 eller mindre.
        // For å reduserer antall prepared statements brukes størrelse på 64,32,16,15,...
        // Prøver med 512 likevel
        for (int i = CRITERIA_BATCH_POWER; i >= 0; i--) {
            // Hvis collection har mindre enn 16 elementer tilbake hentes ut alle ved en spørring istedet for å dele opp i mindre biter
            final int length = (size > 0 && size < 16) ? size : (1 << i);

            while (size >= length) {
                size -= length;
                List<Serializable> subList = new ArrayList<>(length);
                for (int j = 0; j < length; j++) {
                    subList.add(idIterator.next());
                }
                Criteria criteria = session().createCriteria(type).add(Restrictions.in(ID_KOLONNE_NAVN, subList));
                criterias.add(criteria);
            }
        }
        return criterias;
    }

    /**
     * Metode for å sjekke om objektet allerede er lastet av hibernate uten at hibernate forsøker å laste objeket eller lager
     * en proxy.
     *
     * @param aClass      Persistent Objektklasse for <code>hibernateId</code> Eks. Tedm for TedmPK
     * @param hibernateId identifikator for objekt vi vil sjekke om finnes i cachen
     * @return Objektet dersom det er lastet
     */

    protected final Object lookupInHibernateCache(Class aClass, Serializable hibernateId) {
        final EntityPersister classPersister = getClassPersister(aClass);
        final EntityKey key = new EntityKey(hibernateId, classPersister, EntityMode.POJO);
        PersistenceContext context = ((SessionImpl) session()).getPersistenceContext();
        return context.getEntity(key);
    }

    /**
     * Metode for å sjekke om objektet allerede er lastet av hibernate uten at hibernate forsøker å laste objeket eller lager
     * en proxy.
     * <p>
     * TODO: Vurder om dette kan gjøres smartere. Evt vedlikeholde en egen map av objekter som helt sikkert er lastet via en listener
     *
     * @return true hvis objektet er lastet
     */
    private <T extends BubbleObject, I extends BubbleId<? extends T>> T lookupInHibernateCache(I bubbleId) {
        //noinspection unchecked
        return (T) lookupInHibernateCache(bubbleId.getType(), bubbleId);
    }


    // Hjelpe variable for opptimalisering
    private Class lastClass;
    private EntityPersister lastResultForClass;

    /**
     * Lager <code>EntityPersister</code> for <code>theClass</code>. Har støtte for caching av siste hentede persister
     * som en optimalisering.
     *
     * @param theClass Class vi vil hente persister for
     * @return <code>EntityPersister<code> for <code>theClass</code>
     */
    private EntityPersister getClassPersister(Class theClass) {
        try {
            if (lastClass != theClass) {
                lastResultForClass = ((SessionImpl) session()).getFactory().getEntityPersister(theClass.getName());
                lastClass = theClass;
            }
            return lastResultForClass;
        } catch (MappingException e) {
            throw new ConfigurationException(e);
        }
    }

    /**
     * Returnere true hvis denne instans av HibernatedSessionWrapper har lov til å returnere bubler
     * som kun er delvis initialisert. Hvis metoden returnerer true bør metoden {@link #ensureFullyLoaded} kalles
     * manuelt for bobler som skal returneres til klienten slik at deres verdier blir satt før sessionen lukkes
     *
     * @return true hvis HibernatedSessionWrapper har lov til å returnere bubler som kun er delvis
     *         initialisert
     */
    public boolean isLazyLoadedBubblesAllowed() {
        return lazyLoadedBubblesAllowed;
    }


    /**
     * Setter om denne instance av HibernateSessionWrapper har lov til å retunere bobler som kun er
     * delvist initializert.
     */
    public void setLazyLoadedBubblesAllowed(boolean lazyLoadedBubblesAllowed) {
        this.lazyLoadedBubblesAllowed = lazyLoadedBubblesAllowed;
    }


    public void flush() {
        session().flush();
    }

    public void beginTransaction() {
        if (localTransaction != null) {
            throw new ImplementationException("Local transaction already started");
        }
        localTransaction = session().beginTransaction();
    }

    @Override
    public boolean hasLocalTrasaction() {
        return localTransaction != null;
    }

    public void rollback() {
        if (localTransaction == null) {
            throw new ImplementationException("Local transaction not started");
        }
        localTransaction.rollback();
        localTransaction = null;
        clear();
    }

    @Override
    public void commit() {
        if (localTransaction == null) {
            throw new ImplementationException("Local transaction not started");
        }
        localTransaction.commit();
        localTransaction = null;
    }

    /**
     * Hjelpemetode for å sjekke tilstand etter evict. Er egentlig en testmetode, men kan være nyttig å ha som
     * et sanity check.
     */
    @Override
    public void verifySessionIsEmpty() {
        Preconditions.checkState(fullyInitializedBubbles.size() == 0, "FullyInitializedBubbles er ikke tom");
        Preconditions.checkState(exportedLazyLoadedBubbles.size() == 0, "ExportedLazyLoadedBubbles er ikke tom");
        PersistenceContext persistenceContext = ((SessionImpl) session()).getPersistenceContext();
        Preconditions.checkState(persistenceContext.getEntitiesByKey().size() == 0, "EntitiesByKey er ikke tom");
        Preconditions.checkState(persistenceContext.getEntityEntries().size() == 0, "EntitiesByKey er ikke tom");
        Preconditions.checkState(persistenceContext.getCollectionEntries().size() == 0, "CollectionEnties er ikke tom");
        Preconditions.checkState(persistenceContext.getCollectionsByKey().size() == 0, "CollectionEnties er ikke tom");
        Preconditions.checkState(persistenceContext.getNullifiableEntityKeys().size() == 0, "NullifiableEntityKeys er ikke tom");
    }

    public void saveOrUpdateEntityComponentsInBubbles(List<Multimap<Class<? extends EntityComponent>, EntityComponent>> entityMap) {
        newlyInsertedComponents.clear();
        Session session = session();
        // Nødvendig å legge inn med høyest nesting level først
        for (int nestingLevel = entityMap.size() - 1; nestingLevel >= 0; nestingLevel--) {
            Multimap<Class<? extends EntityComponent>, EntityComponent> classEntityComponentMultimap = entityMap.get(nestingLevel);
            for (EntityComponent entityComponent : classEntityComponentMultimap.values()) {
                if (entityComponent.getId() == null) {
                    newlyInsertedComponents.add(entityComponent);
                }
                session.saveOrUpdate(entityComponent);

            }
        }
    }

    public void checkForStolenEntityComponent(Type type, Object object, Set<EntityComponent> newlyInsertedComponents) throws HibernateException {
        if (object == null) return;
        if (object instanceof EntityComponent) {
            if (!(((EntityComponent) object).getId() == null || newlyInsertedComponents.remove(object))) {
                throw new ImplementationException("Attempt to reuse an entity component from another object. Entity class: " + object.getClass().getName() + " id:" + ((EntityComponent) object).getId());
            }
        }
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

        if (cascadeStyle.doCascade(CascadingAction.SAVE_UPDATE)) {

            Type[] types = persister.getPropertyTypes();
            Object[] values = persister.getPropertyValues(object, EntityMode.POJO);
            CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();

            for (int i = 0; i < types.length; i++) {
                Type type = types[i];
                Object value = values[i];
                CascadeStyle cascadeStyleForElement = cascadeStyles != null ? cascadeStyles[i] : CascadeStyle.NONE;
                if (type.isEntityType()) {
                    checkForStolenEntitiesInNewObject(type, value, processedObjects, newlyInsertedComponents, cascadeStyleForElement);
                } else if (type.isComponentType()) {
                    checkForStolenEntitiesInNewObject(type, value, processedObjects, newlyInsertedComponents, cascadeStyleForElement);
                } else if (type.isCollectionType()) {
                    CollectionType collectionType = (CollectionType) type;
                    if (value instanceof Map) {
                        checkForStolenEntitiesInNewObjectForMap((Map) value, collectionType, processedObjects, cascadeStyleForElement);
                    } else {
                        Type elementType = collectionType.getElementType(((SessionImpl) session()).getFactory());
                        checkForStolenEntitiesInNewObjectForCollection(elementType, (Collection) value, processedObjects, newlyInsertedComponents, cascadeStyleForElement);
                    }
                }
            }
        }
    }

    // TODO: Denne gjør ikke noe av det den sier den gjør
    private void checkForStolenEntitiesInNewObjectForMap(Map mapInObject, CollectionType collectionType, IdentityHashMap processedObjects, CascadeStyle cascadeStyleForElement) {
        AbstractCollectionPersister collectionPersister = (AbstractCollectionPersister) session().getSessionFactory().getCollectionMetadata(collectionType.getRole());

        if (!(collectionPersister.getKeyType() instanceof LiteralType)) {
            throw new ImplementationException("Key must be LiteralType");
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
        Object[] values = persister.getPropertyValues(object, EntityMode.POJO);
        CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();

        for (int i = 0; i < types.length; i++) {
            Type type = types[i];
            Object value = values[i];
            CascadeStyle cascadeStyle = cascadeStyles != null ? cascadeStyles[i] : null;

            if (type.isEntityType()) {
                if (cascadeStyle != null && cascadeStyle.doCascade(CascadingAction.SAVE_UPDATE)) {
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
        Class typeClass = type.getReturnedClass();
        // TODO: mangler å opptimalisere på tvers av subklasser
        if (value != null && EntityComponent.class.isAssignableFrom(typeClass)) {
            EntityComponent component = (EntityComponent) value;
            if (groupedEntityComponents.size() == levelKey) {
                groupedEntityComponents.add(HashMultimap.<Class<? extends EntityComponent>, EntityComponent>create());
            }
            groupedEntityComponents.get(levelKey).put(component.getClass(), component);
            fixBatchingForObjectWithEntityComponents(value, processedObjects, levelKey + 1, groupedEntityComponents);
        }
    }
}
