package no.statkart.skif.store.persistence.hibernate;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import no.statkart.skif.exception.*;
import no.statkart.skif.store.*;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.util.CopyHelper;
import no.statkart.skif.util.JDBCHelper;
import org.hibernate.*;
import org.hibernate.LockMode;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.criterion.Expression;
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
import org.hibernate.util.EqualsHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.Collections;

/**
 * @author Henrik Fredholm
 */
public abstract class HibernatePersistenceSessionMasterImpl implements HibernatePersistenceSessionMaster {
    private static Logger logger = LoggerFactory.getLogger(HibernatePersistenceSessionMasterImpl.class);
    private static final int CRITERIA_BATCH_POWER = 9;
    private static final String ID_KOLONNE_NAVN = "id";

    protected final HibernateSessionFactoryManager sessionFactoryManager;
    protected final HibernateSessionFactoryDescriptor sessionFactoryDescriptor;

    /* Holder referanse til hibernate sesjonen */
    protected Session lazySession;

    /* Holder referanse til alle bobler lastet i denne sesjonen som allerede er fullt initialisert */
    private Map<BubbleId, BubbleObject> fullyInitializedBubbles = new HashMap<BubbleId, BubbleObject>();
    private Map exportedLazyLoadedBubbles = new HashMap();

    private int reserveCount = 0;

    protected Transaction localTransaction;


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

    public Map getExportedLazyLoadedBubbles() {
        return exportedLazyLoadedBubbles;
    }

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

    //@Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T getUpdatable(I bubbleId) {
        T bubble;

        checkSnapshotVersion(bubbleId);
        try {
            bubble = getFromHibernateSessionOrLoadEnsureLatest(bubbleId);
            if (bubble == null)
                throw new ObjectNotFoundException(bubbleId);
            if (!isLazyLoadedBubblesAllowed()) {
                ensureFullyLoaded(bubble);
            } else {
                exportedLazyLoadedBubbles.put(bubble.getId(), bubble);
            }
            return bubble;
        } catch (HibernateException e) {
            throw new ObjectNotFoundException(bubbleId, e);
        }
    }


    /**
     * Laster en boble basert på boblens id.
     *
     * @param bubbleId id for boblen som skal hentes
     * @return en domeneboble av typen <code>MatrikkelBubbleObject</code>
     */
    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId) {
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
    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> get(Collection<I> bubbleIds) {
        List<T> alreadyLoaded = new ArrayList<T>(bubbleIds.size());
        Set<I> idsToLoad = new HashSet<I>(bubbleIds.size());

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
        Set<T> result = null;
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
            result = new HashSet<T>(alreadyLoaded);
        }

        if (!lazyLoadedBubblesAllowed) {
            for (T bubble : result) {
                ensureFullyLoaded(bubble);
            }
        }
        if (bubbleIds.size() != result.size()) {
            Set<BubbleId<?>> ids = new HashSet<BubbleId<?>>(bubbleIds);
            ids.removeAll(BubbleIds.asIds(result));
            throw new ObjectsNotFoundException(ids);
        }
        return result;
    }

    /**
     * Knytter {@code bubbleObject} til underliggende hibernate sesison. Ved senere kall til flush() vil endringene
     * bli sendt til databasen.
     *
     * @param bubbleObject
     */
    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void insert(T bubbleObject) {
        try {
            checkEntityComponentsOnInsert(bubbleObject);
            session().save(bubbleObject);
        } catch (HibernateException e) {
            throw new ImplementationException("Insert failed for " + bubbleObject, e);
        }
    }

    /**
     * Oppdaterer underliggende hibernate session med verdiene fra {@code bubbleObject}. Dersom {@code bubbleObject} allerede er
     * knyttet til sessionen returneres samme instans. Dersom {@code bubbleObject} ikke er knyttet til sessionen er det implementasjon
     * avhengig om er {@code bubbleObject} eller en ny instans som vil bli knyttet til sessionen og returnert. Ved senere kall til flush() vil endringene
     * bli sendt til databasen.
     *
     * @param bubbleObject
     * @return oppdatert instans som er knyttet til underliggende hibernate session
     */
    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void update(T bubbleObject) {
        try {
            assignPersistentCollectionsAndConvertObjectIfTypeChangedAndEvictOtherInstance(bubbleObject);
            session().update(bubbleObject); // Viktig at class-mapping inneholder 'select-before-update="true"'. Dette bør settes automatisk ved konfigurasjon av hibernate session factory.
        } catch (HibernateException e) {
            throw new ImplementationException("Update failed for " + bubbleObject, e);
        } catch (SQLException e) {
            throw new ImplementationException("Update failed for " + bubbleObject, e);
        }
    }

    /**
     * Sletter objekt som har samme id som {@code bubbeObject} fra underliggende hibernate session. Ved senere kall til
     * flush() vil endringene bli sendt til databasen. Dersom objektet inneholder endringer i forhold til de som ligger
     * i databasen så vil disse endringene bli lagret først.
     *
     * @param bubbleObject objekt som inneholder id for det objekt som skal slettes
     * @return objekt som ble slettet. Hvis bubbleObject ikke er knyttet til sessionen vi dette være en annen instans
     */
    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void delete(T bubbleObject) {
        try {
            evictOtherInstanceFromHibernateSession(bubbleObject);
            fullyInitializedBubbles.remove(bubbleObject.getId());
            exportedLazyLoadedBubbles.remove(bubbleObject.getId());
            session().delete(bubbleObject);
        } catch (HibernateException e) {
            throw new ImplementationException("Delete failed for " + bubbleObject, e);
        }
    }

    /**
     * Laster eksisterende objekt fra databasen hvis det ikke allerede er lastet og prosesserer det nye objektet
     * dersom nytt og eksisterende objekt er forskjellige instanser.
     * <p/>
     * Prosesseringen består i at det nye objektet får oppdatert alle sine collections slik at de inneholder riktig
     * snapshotverdi av gammel tilstand. Dette er viktig for at hibernate skal kunne oppdatere collections riktig i
     * databasen (se SKIF-214).
     * <p/>
     * Videre så sjekkes det nye objektet har endret type i forhold til eksisterende objekt. Dersom så har skjedd, så
     * må ikke-felles felter nullstilles og det utføres en spesiell SQL som endrer objekttypen i databasen.
     * <p/>
     * Endelig sørges det for at Hibernates cache ikke inneholder et objekt med samme id, med mindre det også er
     * nøyaktig samme objekt (instans) som <i>bubbleObject</i>.
     *
     * @param bubbleObject et objekt som kanskje er lastet gjennom hibernate tidligere i denne
     *                     sesjonen
     * @throws HibernateException    dersom get() eller evict() på hibernate session feiler
     * @throws java.sql.SQLException dersom SQL feiler ved typeendring
     */
    private void assignPersistentCollectionsAndConvertObjectIfTypeChangedAndEvictOtherInstance(BubbleObject bubbleObject) throws HibernateException, SQLException {
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
            // TOOD: Denne kan antageligvis tas bort
            ensureFullyLoaded(existingBubble);
            attachPersistenceCollectionWithSnapshotOfOldState(bubbleObject, existingBubble);

            if (!(bubbleObject.getClass().isInstance(existingBubble))) {
                changeType(bubbleObject, (BubbleObject) existingBubble);

                fullyInitializedBubbles.put(bubbleObject.getBubbleId(), bubbleObject);
            } else {
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
            }
        } else {
            // Gjør ingen ting, bubbleObject er det objektet som allerede ligger i hibernate sessionen
        }
    }


    /**
     * Sørger for at objektets collections alle er av typen PersisteneCollection og inneholder snapshot av gammel tilstand
     * i tillegg til ny tilstand.
     */
    public void attachPersistenceCollectionWithSnapshotOfOldState(BubbleObject bubbleObject, BubbleObject existingBubble) {
        try {
            IdentityHashMap processedObjects = new IdentityHashMap();
            attachPersistenceCollectionWithSnapshotOfOldState(bubbleObject, existingBubble, processedObjects);
        } catch (HibernateException e) {
            throw new ImplementationException("Could not attach PersistenceCollection: " + bubbleObject.getId(), e);
        }
    }

    /**
     * Sørger for at objektets collections alle er av typen PersisteneCollection og inneholder snapshot av gammel tilstand
     * i tillegg til ny tilstand. Hvis en assosiasjon er definert som cascade vil metoden bli kaldt rekursivt på de assosierte
     * objekter dersom de også finnes i existing object
     */
    protected void attachPersistenceCollectionWithSnapshotOfOldState(Object object, Object existingObject, IdentityHashMap processedObjects) throws HibernateException {
        if (object == null) return;

        if (processedObjects.containsKey(object)) return;
        processedObjects.put(object, null);

        ClassMetadata classMetadata = ((SessionImpl) session()).getFactory().getClassMetadata(object.getClass());

        if (erAvTypeSomIkkeSkalInitialiseresVidere(classMetadata)) return;
        EntityPersister persister = (EntityPersister) classMetadata;
        if (!persister.hasCollections()) return;

        Type[] types = persister.getPropertyTypes();
        Object[] values = persister.getPropertyValues(object, EntityMode.POJO);
        CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();

        EntityPersister persisterExisting = (EntityPersister) ((SessionImpl) session()).getFactory().getClassMetadata(existingObject.getClass());
        Type[] typesExisting = persisterExisting.getPropertyTypes();
        Object[] valuesExisting = persisterExisting.getPropertyValues(existingObject, EntityMode.POJO);

        int commonLength = 0;
        if (persister != persisterExisting) {
            // Beregn felles felter for object og existingObject
            for (int i = 0; i < types.length && i < typesExisting.length; i++) {
                if (!types[i].getName().equals(typesExisting[i].getName())) {
                    commonLength = i;
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
            CascadeStyle cascadeStyle = cascadeStyles != null ? cascadeStyles[i] : null;

            if (type.isEntityType()) {
                if (cascadeStyle != null && cascadeStyle.doCascade(CascadingAction.SAVE_UPDATE)) {
                    checkForReplacedOrStolenEntityComponent((EntityType) type, value, valueExisting);
                    attachPersistenceCollectionWithSnapshotOfOldState(value, valueExisting, processedObjects);
                }
            } else if (type.isComponentType()) {
                attachComponent(value, valueExisting, (AbstractComponentType) type, processedObjects);
            } else if (type.isCollectionType()) {
                boolean cascade = cascadeStyle != null && cascadeStyle.doCascade(CascadingAction.SAVE_UPDATE);
                if (valueExisting instanceof Map) {
                    Map mapWithSnapshot = attachPersitentMap((Map) value, (Map) valueExisting, processedObjects, cascade);
                    persister.setPropertyValue(object, i, mapWithSnapshot, EntityMode.POJO);
                } else {
                    CollectionType collectionType = (CollectionType) type;
                    Type elementType = collectionType.getElementType(((SessionImpl) session()).getFactory());
                    Collection collectionWithSnapshot = attachPersistentCollection((Collection) value, (Collection) valueExisting, elementType, processedObjects, cascade);
                    persister.setPropertyValue(object, i, collectionWithSnapshot, EntityMode.POJO);
                }
            } else if (!isSingleColumnType(type) // ting som ligger i én kolonne (Primitiver, String, o.l.). Disse kan ikke ha collections.
                    && !(type instanceof CustomType)) { // CustomType har nok heller ingen collections i seg.
                throw new NotImplementedException();
                // TODO: Trenger et test eksempel for å skrive denne koden riktig
            }
        }
    }

    protected void attachComponent(Object component, Object componentExisting, AbstractComponentType componentType, IdentityHashMap processedObjects) {
        if (component != null && componentExisting != null) {
            Type[] propertyTypes = componentType.getSubtypes();
            Object[] properties = componentType.getPropertyValues(component, EntityMode.POJO);
            Object[] propertiesExisting = componentType.getPropertyValues(componentExisting, EntityMode.POJO);
            boolean wasModified = false;
            for (int j = 0; j < properties.length; j++) {
                Type propertyType = propertyTypes[j];
                Object property = properties[j];
                Object propertyExisting = propertiesExisting[j];
                CascadeStyle cascadeStyle = componentType.getCascadeStyle(j);

                // Hver property kan enten være et simple objekt (f.eks Long), complex objekt (f.eks Boundary) eller en collection
                if (propertyType.isEntityType()) {
                    if (cascadeStyle != null && cascadeStyle.doCascade(CascadingAction.SAVE_UPDATE)) {
                        checkForReplacedOrStolenEntityComponent((EntityType) propertyType, property, propertyExisting);
                        attachPersistenceCollectionWithSnapshotOfOldState(property, propertyExisting, processedObjects);
                    }
                } else if (propertyType.isCollectionType()) {
                    // For hvert element
                    boolean cascade = cascadeStyle != null && cascadeStyle.doCascade(CascadingAction.SAVE_UPDATE);
                    if (propertyExisting instanceof Map) {
                        properties[j] = attachPersitentMap((Map) property, (Map) propertyExisting, processedObjects, cascade);
                    } else {
                        CollectionType collectionType = (CollectionType) propertyType;
                        Type elementType = collectionType.getElementType(((SessionImpl) session()).getFactory());
                        properties[j] = attachPersistentCollection((Collection) property, (Collection) propertyExisting, elementType, processedObjects, cascade);
                    }
                    wasModified = true;
                } else if (propertyType.isComponentType()) {
                    attachComponent(property, propertyExisting, (AbstractComponentType) propertyType, processedObjects);
                } else if (!isSingleColumnType(propertyType) // ting som ligger i én kolonne (Primitiver, String, o.l.). Disse kan ikke ha collections.
                        && !(propertyType instanceof CustomType)) { // CustomType har nok heller ingen collections i seg.
                    throw new NotImplementedException();
                }
            }
            if (wasModified) {
                componentType.setPropertyValues(component, properties, EntityMode.POJO);
            }
        }
    }

    protected Map attachPersitentMap(Map mapInObject, Map mapInExistingObject, IdentityHashMap processedObjects, boolean cascade) {
        if (mapInExistingObject instanceof PersistentCollection) {
            final CollectionEntry entry = ((SessionImpl) session()).getPersistenceContext().getCollectionEntry((PersistentCollection) mapInExistingObject);
            final AbstractCollectionPersister collectionPersister = (AbstractCollectionPersister) entry.getLoadedPersister();

            if (!(collectionPersister.getKeyType() instanceof LiteralType)) {
                throw new ImplementationException("Key must be LiteralType");
            }

            Map persistentCollection = CopyHelper.copy(mapInExistingObject);
            persistentCollection.clear();
            if (mapInObject != null) {
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

    protected Collection attachPersistentCollection(Collection collectionInObject, Collection collectionInExistingObject, Type elementType, IdentityHashMap processedObjects, boolean cascade) throws HibernateException {
        if (collectionInExistingObject instanceof PersistentCollection) {
            Collection persistentCollection = CopyHelper.copy(collectionInExistingObject);
            persistentCollection.clear();
            if (collectionInObject != null) {
                persistentCollection.addAll(collectionInObject);
            }
            if (cascade) {
                checkForStolenEntityComponent(elementType, collectionInObject, collectionInExistingObject);
                cascadeAttachPersistenceCollections(persistentCollection, collectionInExistingObject, processedObjects);
            }
            return persistentCollection;
        } else {
            // TODO: Alternativt returner eksisterende collection. Kanskje det er greit?
            throw new ImplementationException("Unable to attach persistent collection, as existing object has collection that isn't PersistentCollection");
            //return collectionInObject;
        }
    }

    protected void cascadeAttachPersistenceCollections(Collection collectionInOject, Collection collectionInExistingObject, IdentityHashMap processedObjects) throws HibernateException {
        CollectionEntry entry = ((SessionImpl) session()).getPersistenceContext().getCollectionEntry((PersistentCollection) collectionInExistingObject);
        final CollectionPersister collectionPersister = entry.getLoadedPersister();
        Type elementType = collectionPersister.getElementType();
        if (elementType.isEntityType()) {
            final EntityPersister elementPersister = ((AbstractCollectionPersister) collectionPersister).getElementPersister();
            if (elementPersister.hasCollections()) {
                Map<Serializable, Object> oldElementMap = Maps.newHashMap();
                for (Object o : collectionInExistingObject) {
                    oldElementMap.put(elementPersister.getIdentifier(o, EntityMode.POJO), o);
                }
                for (Object object : collectionInOject) {
                    final Serializable identifier = elementPersister.getIdentifier(object, EntityMode.POJO);
                    final Object valueExisting = oldElementMap.get(identifier);
                    if (valueExisting != null) { // TODO: Dersom objektet ikke fantes før, så kan det vel ikke være noen collections som skal attaches?
                        attachPersistenceCollectionWithSnapshotOfOldState(object, valueExisting, processedObjects);
                    }
                }
            }
        } else if (elementType.isComponentType()) {
            ComponentType componentType = (ComponentType) elementType;
            Type[] propertyTypes = componentType.getSubtypes();
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
            IdentityHashMap processedObjects = new IdentityHashMap();
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
    private void checkEntityComponentsOnInsert(Object object, IdentityHashMap processedObjects) throws HibernateException {
        if (object == null) return;

        if (processedObjects.containsKey(object)) return;
        processedObjects.put(object, null);

        ClassMetadata classMetadata = ((SessionImpl) session()).getFactory().getClassMetadata(object.getClass());

        if (erAvTypeSomIkkeSkalInitialiseresVidere(classMetadata)) return;
        EntityPersister persister = (EntityPersister) classMetadata;
        if (!persister.hasCollections()) return;

        Type[] types = persister.getPropertyTypes();
        Object[] values = persister.getPropertyValues(object, EntityMode.POJO);
        CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();

        for (int i = 0; i < types.length; i++) {
            Type type = types[i];
            Object value = values[i];
            CascadeStyle cascadeStyle = cascadeStyles != null ? cascadeStyles[i] : null;

            if (type.isEntityType()) {
                if (cascadeStyle != null && cascadeStyle.doCascade(CascadingAction.SAVE_UPDATE)) {
                    checkForReplacedOrStolenEntityComponent((EntityType) type, value, null);
                }
            } else if (type.isComponentType()) {
                checkEntityComponentsOnInsertInComponent(value, (AbstractComponentType) type, processedObjects);
            } else if (type.isCollectionType()) {
                boolean cascade = cascadeStyle != null && cascadeStyle.doCascade(CascadingAction.SAVE_UPDATE);
                if (value instanceof Map) {
                    CollectionType collectionType = (CollectionType) type;
                    checkEntityComponentsInMapOnInsert((Map) value, collectionType, processedObjects, cascade);
                } else {
                    CollectionType collectionType = (CollectionType) type;
                    Type elementType = collectionType.getElementType(((SessionImpl) session()).getFactory());
                    checkEntityComponentsInCollectionOnInsert((Collection) value, elementType, processedObjects, cascade);
                }
            } else if (!isSingleColumnType(type) // ting som ligger i én kolonne (Primitiver, String, o.l.). Disse kan ikke ha collections.
                    && !(type instanceof CustomType)) { // CustomType har nok heller ingen collections i seg.
                throw new NotImplementedException();
                // TODO: Trenger et test eksempel for å skrive denne koden riktig
            }
        }
    }

    protected void checkEntityComponentsOnInsertInComponent(Object component, AbstractComponentType componentType, IdentityHashMap processedObjects) {
        if (component != null) {
            Type[] propertyTypes = componentType.getSubtypes();
            Object[] properties = componentType.getPropertyValues(component, EntityMode.POJO);
            boolean wasModified = false;
            for (int j = 0; j < properties.length; j++) {
                Type propertyType = propertyTypes[j];
                Object property = properties[j];
                CascadeStyle cascadeStyle = componentType.getCascadeStyle(j);

                // Hver property kan enten være et simple objekt (f.eks Long), complex objekt (f.eks Boundary) eller en collection
                if (propertyType.isEntityType()) {
                    if (cascadeStyle != null && cascadeStyle.doCascade(CascadingAction.SAVE_UPDATE)) {
                        checkForReplacedOrStolenEntityComponent((EntityType) propertyType, property, null);
                        checkEntityComponentsOnInsert(property, processedObjects);
                    }
                } else if (propertyType.isCollectionType()) {
                    // For hvert element
                    boolean cascade = cascadeStyle != null && cascadeStyle.doCascade(CascadingAction.SAVE_UPDATE);
                    if (property instanceof Map) {
                        CollectionType collectionType = (CollectionType) propertyType;
                        checkEntityComponentsInMapOnInsert((Map) property, collectionType, processedObjects, cascade);
                    } else {
                        CollectionType collectionType = (CollectionType) propertyType;
                        Type elementType = collectionType.getElementType(((SessionImpl) session()).getFactory());
                        checkEntityComponentsInCollectionOnInsert((Collection) property, elementType, processedObjects, cascade);
                    }
                    wasModified = true;
                } else if (propertyType.isComponentType()) {
                    checkEntityComponentsOnInsertInComponent(property, (AbstractComponentType) propertyType, processedObjects);
                } else if (!isSingleColumnType(propertyType) // ting som ligger i én kolonne (Primitiver, String, o.l.). Disse kan ikke ha collections.
                        && !(propertyType instanceof CustomType)) { // CustomType har nok heller ingen collections i seg.
                    throw new NotImplementedException();
                }
            }
            if (wasModified) {
                componentType.setPropertyValues(component, properties, EntityMode.POJO);
            }
        }
    }

    private void checkEntityComponentsInMapOnInsert(Map mapInObject, CollectionType collectionType, IdentityHashMap processedObjects, boolean cascade) {
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
                for (int i = 0; i < propertyTypes.length; i++) {
                    Type propertyType = propertyTypes[i];
                    if (propertyType.isAssociationType() || propertyType.isComponentType()) {
                        throw new NotImplementedException("Map value må være enkel verdi eller ett-nivå entity");
                    }
                }
            }
        }
    }

    private void checkEntityComponentsInCollectionOnInsert(Collection collectionInObject, Type elementType, IdentityHashMap processedObjects, boolean cascade) throws HibernateException {
        if (cascade) {
            checkForStolenEntityComponent(elementType, collectionInObject, Collections.emptyList());

            if (elementType.isEntityType()) {
                for (Object object : collectionInObject) {
                    checkEntityComponentsOnInsert(object, processedObjects);
                }
            } else if (elementType.isComponentType()) {
                ComponentType componentType = (ComponentType) elementType;
                Type[] propertyTypes = componentType.getSubtypes();
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
    }

    /**
     * Sjekker om <code>value</code> er en entity component har blitt erstattet med en annen eller <code>null</code>,
     * eller om entity component ser ut til å ha blitt stjålet.
     *
     * @param type          typen til feltet
     * @param value         nåværende verdi
     * @param valueExisting forrige verdi
     * @throws ImplementationException dersom entity component har blitt byttet ut med en annen eller <code>null</code>
     *                                 eller hvis entity component allerede har id
     * @since 2.2.0
     */
    private void checkForReplacedOrStolenEntityComponent(EntityType type, Object value, Object valueExisting) {
        Class typeClass = type.getReturnedClass();
        if (AbstractEntityComponent.class.isAssignableFrom(typeClass)) {
            AbstractEntityPersister persister = (AbstractEntityPersister) ((SessionImpl) session()).getFactory().getClassMetadata(type.getName());
            EntityMetamodel entityMetamodel = persister.getEntityMetamodel();
            if (valueExisting != null && value == null) {
                AbstractEntityComponent oldEntityComponent = (AbstractEntityComponent) valueExisting;
                throw new ImplementationException("Attempt at setting entity component to null. Entity class: " + typeClass.getName() + " Id:" + oldEntityComponent.getId(), logger);
            }

            // Dersom komponentid er assigned, så kan vi ikke detektere stjeling av komponenter. Slike id-er finnes i matrikkel historikk.
            if (!(entityMetamodel.getIdentifierProperty().getIdentifierGenerator() instanceof Assigned) && value != null) {
                final Long oldId;
                if (valueExisting != null) {
                    AbstractEntityComponent oldEntityComponent = (AbstractEntityComponent) valueExisting;
                    oldId = oldEntityComponent.getId();
                } else {
                    oldId = null;
                }
                AbstractEntityComponent entityComponent = (AbstractEntityComponent) value;
                final Long newId = entityComponent.getId();

                if (!EqualsHelper.equals(oldId, newId)) {
                    throw new ImplementationException("Attempt at replacing entity component. Entity class: " + typeClass.getName() + " New id:" + newId + ", Old id:" + oldId, logger);
                }
            }
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
            if (AbstractEntityComponent.class.isAssignableFrom(typeClass)) {
                AbstractEntityPersister persister = (AbstractEntityPersister) ((SessionImpl) session()).getFactory().getClassMetadata(elementType.getName());
                EntityMetamodel entityMetamodel = persister.getEntityMetamodel();

                // Dersom komponentid er assigned, så kan vi ikke detektere stjeling av komponenter. Slike id-er finnes i matrikkel historikk.
                if (!(entityMetamodel.getIdentifierProperty().getIdentifierGenerator() instanceof Assigned)) {
                    Set<Object> existingIds = new HashSet<Object>();
                    for (Object o : collectionExisting) {
                        existingIds.add(((AbstractEntityComponent) o).getId());
                    }

                    for (Object o : collection) {
                        AbstractEntityComponent entityComponent = (AbstractEntityComponent) o;
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
        List<Field> primitiveFields;
        try {
            primitiveFields = blankUtIkkeFellesFelter(previousObject, currentObject.getClass());
        } catch (IllegalAccessException e) {
            throw new ImplementationException("Could not clear fields in initial object during type change", e, logger);
        }

        flush();
        evict(currentObject.getBubbleId());

        Statement statement = session().connection().createStatement();
        try {
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

            final String discriminatorColumn = toEntityPersister.getDiscriminatorColumnName();
            final String discriminatorValue = toEntityPersister.getDiscriminatorSQLValue(); // Inkluderer apostrofer hvis tekst

            StringBuilder sql = new StringBuilder("update ");
            sql.append(dbTable);
            sql.append(" set ").append(discriminatorColumn).append('=').append(discriminatorValue);

            if (!primitiveFields.isEmpty()) {
                final boolean[] propertyNullability = fromEntityPersister.getPropertyNullability();

                for (int i = 0; i < primitiveFields.size(); i++) {
                    final Field field = primitiveFields.get(i);
                    final int propertyIndex = fromEntityPersister.getPropertyIndex(field.getName());
                    if (propertyNullability[propertyIndex]) {
                        final String[] propertyColumnNames = fromEntityPersister.getPropertyColumnNames(propertyIndex);
                        if (propertyColumnNames.length != 1) {
                            throw new ImplementationException("Property " + field.getName() + " er mappet til flere kolonner: " + Arrays.toString(propertyColumnNames), logger);
                        }
                        sql.append(", ").append(propertyColumnNames[0]).append("=null");
                    } else {
                        logger.warn("Property " + field.getName() + " er ikke nullable, men unik for fra-klasse " + previousObject.getClass());
                    }
                }
            }

            sql.append(" where id=").append(currentObject.getBubbleId().getValue());

            final String sqlString = sql.toString();
            logger.debug(sqlString);
            int rows = statement.executeUpdate(sqlString);
            if (rows != 1) {
                throw new ImplementationException("When changing type, the number of updated rows should be 1, but it turned out to be " + rows, logger);
            }
        } finally {
            JDBCHelper.close(statement);
        }
    }

    /**
     * Når et objekt skal endre type, må alle felter som finnes i fra-typen men ikke i til-typen, blankes ut.
     * Det vil si at kolleksjoner må tømmes og referanser må settes til <code>null</code>.
     * <p/>
     * Primitive felter kan ikke nulles ut. Disse blir returnert slik at de kan nulles ut med SQL, om mulig.
     *
     * @param bubbleObject den gamle utgaven av objektet med sin gamle type
     * @param tilClass     typen objektet skal endres til
     * @return liste over primitive felter som må nulles ut med SQL
     * @throws IllegalAccessException dersom det av en eller annen grunn ikke er mulig å få tak i noen av feltene
     * @since 2.1
     */
    private static List<Field> blankUtIkkeFellesFelter(BubbleObject bubbleObject, Class<? extends BubbleObject> tilClass) throws IllegalAccessException {
        ArrayList<Field> primitiveFields = new ArrayList<Field>();

        for (Class clazz = bubbleObject.getClass(); !clazz.isAssignableFrom(tilClass); clazz = clazz.getSuperclass()) {
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
     * Sjekk om hibernate har et objekt med samme id som <code>bubbleObject</code>, kast hibernate's
     * objekt ut av hibernate cache dersom objektet hibernate innehar ikke er identisk med
     * <code>bubbleObject</code>. Identisk betyr samme objekt-instans ikke equals-likhet.
     *
     * @param bubbleObject et objekt som kanskje er lastet gjennom hibernate tidligere i denne
     *                     sesjonen
     * @throws org.hibernate.HibernateException
     *          dersom evict(..) på hibernate session feiler
     */
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
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T refresh(I bubbleId) {
        //TODO: På grunn av feilen beskrevet i SKIF-231 har vi valgt å ikke bruke refresh på session her inntil videre.
        evict(bubbleId);
        return get(bubbleId);
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> refresh(Collection<I> bubbleIds) {
        for (I bubbleId : bubbleIds) {
            evict(bubbleId);
        }
        return get(bubbleIds);
    }

    @Override
    public <T extends BubbleObject> void refresh(T bubble) {
        session().refresh(bubble);
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
     *
     * @param ids
     * @return
     * @throws org.hibernate.ObjectNotFoundException
     *
     */
    private <T extends BubbleObject, I extends BubbleId<? extends T>> Set<T> loadAllFromDatabase(Set<I> ids) throws ObjectNotFoundException {
        Set<T> allEntities = new HashSet<T>(ids.size());

        List criterias = buildCriterias(ids);
        for (Iterator it = criterias.iterator(); it.hasNext(); ) {
            Criteria criteria = (Criteria) it.next();
            try {
                List objects = criteria.list();
                for (Iterator iterator = objects.iterator(); iterator.hasNext(); ) {
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
    private List buildCriterias(Set ids) {
        List criterias = new ArrayList();

        Map idsByType = getIdsByType(ids);
        Iterator it = idsByType.keySet().iterator();
        while (it.hasNext()) {
            Class type = (Class) it.next();
            List criteriasForType = buildCriteriaForType(type, (Set) idsByType.get(type));
            criterias.addAll(criteriasForType);
        }
        return criterias;
    }


    /**
     * Create a map with a ids keyed on the clazz of entity that they defines the id for
     *
     * @param ids a set of ids
     * @return a map of ids keyed by the clazz og the entity
     */
    private <T extends BubbleObject, I extends BubbleId<? extends T>> Map getIdsByType(Set<I> ids) {
        HashMap idsByType = new HashMap();
        Iterator<I> it = ids.iterator();
        while (it.hasNext()) {
            I id = it.next();
            Class entityClazz = id.getBaseType(); //Class name for the entity owning the id
            // Endringer ligger i mange forskjellige tabeller. Hvis vi gjør query via basetype må
            // Hibernate gjøre en masse joins.
            // TODO: Bruke Hibernate metadata til å finne ut av dette. Pt er Endring den eneste klasse som er slik.
            //if (entityClazz == Endring.class) {
            //    entityClazz = id.getType();
            //}
            if (!idsByType.containsKey(entityClazz)) {
                //Add an entry in the map for holding all ids of this typename
                idsByType.put(entityClazz, new HashSet());
            }

            ((Set) idsByType.get(entityClazz)).add(id);
        }
        return idsByType;
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> T getFromHibernateSessionOrLoad(I bubbleId) {
        T bubble = getFromHibernatePersistenceContext(bubbleId);
        if (bubble == null) {
            bubble = (T) session().get(bubbleId.getType(), bubbleId, LockMode.NONE);
        }
        return bubble;
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> T getFromHibernatePersistenceContext(I bubbleId) {
        T bubble;
        final EntityPersister classPersister = getClassPersister(bubbleId.getType());
        final EntityKey key = new EntityKey(bubbleId, classPersister, EntityMode.POJO);
        bubble = (T) ((SessionImpl) session()).getPersistenceContext().getEntity(key);
        return bubble;
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> T getFromHibernateSessionOrLoadEnsureLatest(I bubbleId) {
        T bubble;
        final EntityPersister classPersister = getClassPersister(bubbleId.getType());
        final EntityKey key = new EntityKey(bubbleId, classPersister, EntityMode.POJO);
        boolean bubbleWasAlreadyLoaded = ((SessionImpl) session()).getPersistenceContext().getEntity(key) != null;
        bubble = (T) session().get(bubbleId.getType(), bubbleId, LockMode.READ);
        if (bubbleWasAlreadyLoaded) {
            // TODO: check that alrady loaded entities of bubble are uptodate
        }
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
        BubbleObject initializedBubble = (BubbleObject) fullyInitializedBubbles.get(bubble.getId());
        if (initializedBubble == null) {
            try {
                IdentityHashMap initializedObjects = new IdentityHashMap();
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
     * @throws org.hibernate.HibernateException
     *
     */
    // TODO: må gjøres abstract og flyttes til 3.2 implementasjon
    protected void ensureInitialized(Object object, IdentityHashMap initializedObjects) throws HibernateException {
        if (object == null) return;

        if (initializedObjects.containsKey(object)) return;
        initializedObjects.put(object, null);

        ClassMetadata classMetadata = ((SessionImpl) session()).getFactory().getClassMetadata(object.getClass());

        if (erAvTypeSomIkkeSkalInitialiseresVidere(classMetadata)) return;

        EntityPersister persister = (EntityPersister) classMetadata;
        Type[] types = persister.getPropertyTypes();
        Object[] values = persister.getPropertyValues(object, EntityMode.POJO);
        CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();

        for (int i = 0; i < types.length; i++) {
            Type type = types[i];
            if (type.isEntityType()) {
                Hibernate.initialize(values[i]);

                if (cascadeStyles != null && cascadeStyles[i].doCascade(CascadingAction.SAVE_UPDATE)) {
                    ensureInitialized(values[i], initializedObjects);
                }
            } else if (type.isComponentType()) {
                AbstractComponentType t = (AbstractComponentType) type;
                Object component = values[i];
                if (component != null) {
                    Object[] componentProperties = t.getPropertyValues(component, EntityMode.POJO);
                    for (int j = 0; j < componentProperties.length; j++) {
                        // Hver property kan enten være et simple objekt (f.eks Long), complex objekt (f.eks Boundary) eller en collection
                        Object componentProperty = componentProperties[j];
                        Hibernate.initialize(componentProperty);
                        if (componentProperty instanceof PersistentCollection) {
                            // Initialiser hvert element
                            for (Iterator iterator = ((Collection) componentProperty).iterator(); iterator.hasNext(); ) {
                                Object o = iterator.next();
                                ensureInitialized(o, initializedObjects);
                            }
                        } else {
                            ensureInitialized(componentProperty, initializedObjects);
                        }
                    }
                }
            } else if (type.isAssociationType()) {
                Hibernate.initialize(values[i]);
                if (cascadeStyles != null && cascadeStyles[i].doCascade(CascadingAction.SAVE_UPDATE)) {
                    Collection col = (Collection) values[i];
                    for (Iterator iterator = col.iterator(); iterator.hasNext(); ) {
                        Object o = (Object) iterator.next();
                        ensureInitialized(o, initializedObjects);
                    }
                }
            }
        }
    }

    /**
     * SingleColumnType finnes ikke i 3.2.6 så her brukes test mot NullableType istedet
     *
     * @param type
     * @return
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
     * <p/>
     *
     * @param type klassen til domeneboblene som skal lastes
     * @param ids  et sett med id'er for domeneboblene
     * @return en liste med <code>Criteria</code>-objekter for uthenting av domeneboblene fra
     *         databasen
     */
    protected List<Criteria> buildCriteriaForType(Class type, Collection<? extends Serializable> ids) {
        List<Criteria> criterias = new ArrayList<Criteria>();
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
                List<Serializable> subList = new ArrayList<Serializable>(length);
                for (int j = 0; j < length; j++) {
                    subList.add(idIterator.next());
                }
                Criteria criteria = session().createCriteria(type).add(Expression.in(ID_KOLONNE_NAVN, subList));
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
     * <p/>
     * TODO: Vurder om dette kan gjøres smartere. Evt vedlikeholde en egen map av objekter som helt sikkert er lastet via en listener
     *
     * @param bubbleId
     * @return true hvis objektet er lastet
     */
    private <T extends BubbleObject, I extends BubbleId<? extends T>> T lookupInHibernateCache(I bubbleId) {
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
     *
     * @param lazyLoadedBubblesAllowed
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
}
