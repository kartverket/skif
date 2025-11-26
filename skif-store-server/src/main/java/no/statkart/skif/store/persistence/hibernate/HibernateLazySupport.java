package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.engine.spi.CascadeStyle;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.AssociationType;
import org.hibernate.type.BasicType;
import org.hibernate.type.CollectionType;
import org.hibernate.type.CompositeType;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static no.statkart.skif.store.persistence.hibernate.HibernateHelper.erAvTypeSomIkkeSkalInitialiseresVidere;

/**
 * Support class for fully initializing bubbles that may be lazy (partially) loaded by Hibernate. The class
 * maintains a map of bubbles it knows have been fully initialized and a map of objects that has been given
 * out and which might not have been fully initialized.
 */
public class HibernateLazySupport {

    private final Supplier<Session> sessionSupplier;

    /* Holder referanse til alle bobler lastet i denne sesjonen som allerede er fullt initialisert */
    private final Map<BubbleId, BubbleObject> fullyInitializedBubbles = new HashMap<>();
    private final Map<BubbleId, BubbleObject> exportedLazyLoadedBubbles = new HashMap<>();

    public HibernateLazySupport(Supplier<Session> sessionSupplier) {
        this.sessionSupplier = sessionSupplier;
    }

    private boolean lazyLoadedBubblesAllowedDefault = false; // TODO: Kan vi fjerne denne?

    /**
     * Bestemmer om Bubbler kan ha lazyloaded assosiasjoner som ikke er initialisert i det bubblen
     * utleveres fra HibernateSessionWrapper
     */
    private boolean lazyLoadedBubblesAllowed;

    public Session session() {
        return sessionSupplier.get();
    }

    MetamodelImplementor getMetamodel() {
        return (MetamodelImplementor) session().getSessionFactory().getMetamodel();
    }

    public Map<BubbleId, BubbleObject> getFullyInitializedBubbles() {
        return fullyInitializedBubbles;
    }

    public Map<BubbleId, BubbleObject> getExportedLazyLoadedBubbles() {
        return exportedLazyLoadedBubbles;
    }

    public void clear() {
        session().clear();
        exportedLazyLoadedBubbles.clear();
        fullyInitializedBubbles.clear();
        lazyLoadedBubblesAllowed = lazyLoadedBubblesAllowedDefault;
    }

    public void flushIfDirtyThenFullyInitializeAllExportedBubblesAndClearSession() {
        // Kan vi kreve at flushing må være utført i forkant og kaste en exception i stedet hvis dirty er true?
        Session session = session();
        if (session.isDirty()) {
            session.flush();
        }
        ensureBubblesFullyLoaded();
        boolean oldValue = lazyLoadedBubblesAllowed;
        clear();
        lazyLoadedBubblesAllowed = oldValue;
    }

    public void ensureBubblesFullyLoaded() {
        for (BubbleObject bubble : exportedLazyLoadedBubbles.values()) {
            ensureFullyLoaded(bubble);
        }
    }

    public void markBubbleAsExportedLazyOrFullLoaded(BubbleObject bubble) {
        if (!isLazyLoadedBubblesAllowed()) {
            ensureFullyLoaded(bubble);
        } else {
            exportedLazyLoadedBubbles.put(bubble.getId(), bubble);
        }
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
    private void ensureInitialized(Object object, IdentityHashMap<Object, Object> initializedObjects) throws HibernateException {
        if (object == null) return;

        if (initializedObjects.containsKey(object)) return;
        initializedObjects.put(object, null);

        final SessionImplementor sessionImpl = (SessionImplementor) session();
        final SessionFactoryImplementor sessionFactory = sessionImpl.getFactory();
        ClassMetadata classMetadata = HibernateHelper.getClassMetadata(sessionImpl, object);

        if (classMetadata == null) {
            Hibernate.initialize(object);
            return;
        }
        if (erAvTypeSomIkkeSkalInitialiseresVidere(classMetadata)) return;

        Object persistentObject;
        if (object instanceof HibernateProxy) {
            persistentObject = ((HibernateProxy) object).getHibernateLazyInitializer().getImplementation();
        } else {
            persistentObject = object;
        }

        EntityPersister persister = (EntityPersister) classMetadata;
        Type[] types = persister.getPropertyTypes();
        Object[] values = persister.getPropertyValues(persistentObject);
        CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();

        ensureInitialized(types, values, sessionImpl, sessionFactory, cascadeStyles, initializedObjects);
    }

    private void ensureInitialized(Type[] types, Object[] values, SessionImplementor sessionImpl, SessionFactoryImplementor sessionFactory, CascadeStyle[] cascadeStyles, IdentityHashMap<Object, Object> initializedObjects) {
        for (int i = 0; i < types.length; i++) {
            Type type = types[i];
            if (type.isEntityType()) {
                // TODO: Opptimaliser Many-to-one-bubbleref trenger ikke initialiseres
                Hibernate.initialize(values[i]);

                if (cascadeStyles != null && cascadeStyles[i].doCascade(CascadingActions.SAVE_UPDATE)) {
                    ensureInitialized(values[i], initializedObjects);
                }
            } else if (type.isComponentType()) {
                CompositeType t = (CompositeType) type;
                Object component = values[i];
                if (component != null) {
                    Object[] componentProperties = t.getPropertyValues(component);
                    Type[] componentTypes = t.getSubtypes();
                    CascadeStyle[] componentCascadeStyles = new CascadeStyle[componentTypes.length];
                    for (int j = 0; j < componentCascadeStyles.length; j++) {
                        componentCascadeStyles[j] = t.getCascadeStyle(j);
                    }
                    ensureInitialized(componentTypes, componentProperties, sessionImpl, sessionFactory, componentCascadeStyles, initializedObjects);
                }
            } else if (type.isAssociationType()) {
                Hibernate.initialize(values[i]);
                if (cascadeStyles != null && cascadeStyles[i].doCascade(CascadingActions.SAVE_UPDATE)) {
                    // Initialisert collectionen og hvert element. Element kan være av typen composite eller association.
                    // TODO: Nåværende implementasjon håndtere kun et nivå av composite-elementer. Generaliser ved behov
                    if (values[i] instanceof Collection) {
                        Collection<?> col = (Collection<?>) values[i];
                        if (!col.isEmpty()) {
                            CollectionPersister collectionPersister = sessionFactory.getMappingMetamodel().getCollectionDescriptor((((CollectionType) type).getRole()));
                            if (collectionPersister.getElementType() instanceof CompositeType) {
                                CompositeType compositeType = (CompositeType) collectionPersister.getElementType();
                                for (Object componentObject : col) {
                                    final Object[] propertyValues = compositeType.getPropertyValues(componentObject, sessionImpl);
                                    //noinspection ForLoopReplaceableByForEach
                                    for (int j = 0; j < propertyValues.length; j++) {
                                        ensureInitialized(propertyValues[j], initializedObjects);
                                    }
                                }
                            } else if (collectionPersister.getElementType() instanceof AssociationType) {
                                for (Object o : col) {
                                    ensureInitialized(o, initializedObjects);
                                }
                            } // else {
                            // No-op
                            //}
                        }
                    } else if (values[i] instanceof Map) {
                        Map<?, ?> map = (Map<?, ?>) values[i];
                        if (!map.isEmpty()) {
                            CollectionPersister collectionPersister = sessionFactory.getMappingMetamodel().getCollectionDescriptor(((CollectionType) type).getRole());
                            if (collectionPersister.getElementType() instanceof AssociationType) {
                                for (Object o : map.values()) {
                                    ensureInitialized(o, initializedObjects);
                                }
                            } else if (!(collectionPersister.getElementType() instanceof BasicType)) {
                                throw new NotImplementedException("Map value må være enkel verdi eller ett-nivå entity");
                            }
                        }
                    }
                }
            }
        }
    }

    public void markAsFullyInitialized(BubbleObject bubbleObject) {
        exportedLazyLoadedBubbles.remove(bubbleObject.getId());
        fullyInitializedBubbles.put(bubbleObject.getId(), bubbleObject);
    }

    public void markAsRemoved(BubbleId<?> bubbleId) {
        exportedLazyLoadedBubbles.remove(bubbleId);
        fullyInitializedBubbles.remove(bubbleId);
    }

    public void evict(BubbleId<?> bubbleId) {
        BubbleObject bubble = lookupInHibernateCache(bubbleId);
        if (bubble != null) {
            session().evict(bubble);
        }
        markAsRemoved(bubbleId);
    }

    private BubbleObject lookupInHibernateCache(BubbleId<?> bubbleId) {
        return (BubbleObject)lookupInHibernateCache(bubbleId.getBaseType(), bubbleId);
    }

    /**
     * Metode for å sjekke om objektet allerede er lastet av hibernate uten at hibernate forsøker å laste objeket eller lager
     * en proxy.
     *
     * @param aClass      Persistent Objektklasse for <code>hibernateId</code> Eks. Tedm for TedmPK
     * @param hibernateId identifikator for objekt vi vil sjekke om finnes i cachen
     * @return Objektet dersom det er lastet
     */

    private Object lookupInHibernateCache(Class<?> aClass, Serializable hibernateId) {
        final EntityPersister classPersister = getClassPersister(aClass);
        final EntityKey key = new EntityKey(hibernateId, classPersister);
        PersistenceContext context = ((SessionImplementor) session()).getPersistenceContext();
        return context.getEntity(key);
    }

    // Hjelpe variable for opptimalisering
    private Class<?> lastClass;
    private EntityPersister lastResultForClass;

    /**
     * Lager <code>EntityPersister</code> for <code>theClass</code>. Har støtte for caching av siste hentede persister
     * som en optimalisering.
     *
     * @param theClass Class vi vil hente persister for
     * @return <code>EntityPersister<code> for <code>theClass</code>
     */
    EntityPersister getClassPersister(Class<?> theClass) {
        try {
            if (lastClass != theClass) {
                lastResultForClass = getMetamodel().entityPersister(theClass.getName());
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

    public HibernateLazySupport setLazyLoadedBubblesAllowedDefault(boolean lazyLoadedBubblesAllowedDefault) {
        this.lazyLoadedBubblesAllowedDefault = lazyLoadedBubblesAllowedDefault;
        return this;
    }

    public void flush() {
        // TODO: Å kalle isDirty her er en midlertidig workaround.
        Session session = session();
        if (session.isDirty()) {
            session.flush();
        }
    }

}
