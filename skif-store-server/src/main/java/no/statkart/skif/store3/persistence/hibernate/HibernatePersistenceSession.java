package no.statkart.skif.store3.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store3.persistence.SnapshotSessionEventListener;
import no.statkart.skif.store3.persistence.SnapshotSessionEventSource;
import no.statkart.skif.store3.persistence.PersistenceSession;
import org.hibernate.*;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.criterion.Expression;
import org.hibernate.engine.CascadeStyle;
import org.hibernate.engine.CascadingAction;
import org.hibernate.engine.EntityKey;
import org.hibernate.engine.PersistenceContext;
import org.hibernate.impl.SessionImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.type.AbstractComponentType;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.*;

/**
 * En PersistenceSession som bruker hibernate som underliggende session og som er knyttet til en gitt SnapshotVersion.
 *
 * Klassen håndterer lasting av bobler basert på id og har støtte for lazyloading. Klassen holder styr på hvilke bobler
 * som helt sikkert er fuldt initialisert samt hvilke bobler som har blitt gitt ut og som muligvis ikke er fuldt initialisert.
 * Det er også mulig å angi om bobler alltid skal lastet fuldt ut før de blir gitt ut.
 *
 * Ved endring av SnapshotVersion på den underliggende hibernate session forventer klasse å bli fortalt om dette via
 * en event. Klassen vi da sørge for at alle lastet bobler som har blitt gitt ut blir fuldt initialisert.
 * Deretter nullstilles listene over lastet bobler.
 *
 *
 * @author Henrik Fredholm
 */
public class HibernatePersistenceSession implements PersistenceSession {
    private static int CRITERIA_BATCH_POWER = 9;
    private static final String ID_KOLONNE_NAVN = "id";

    /* Holder referanse til hibernate sesjonen */
    protected final Session session;
    protected final SnapshotSessionEventSource eventSource;
    protected SnapshotVersionSeed snapshotVersionSeed;

    /* Holder referanse til alle bobler lastet i denne sesjonen som allerede er fullt initialisert */
    private Map fullyInitializedBubbles = new HashMap();
    private Map exportedLazyLoadedBubbles = new HashMap();

    /**
     * Bestemmer om Bubbler kan ha lazyloaded assosiasjoner som ikke er initialisert i det bubblen
     * utleveres fra HibernateSessionWrapper
     */
    private boolean lazyLoadedBubblesAllowed;

    public HibernatePersistenceSession(Session session, SnapshotSessionEventSource eventSource, SnapshotVersionSeed snapshotVersionSeed) {
        this.session = session;
        this.eventSource = eventSource;
        this.snapshotVersionSeed = snapshotVersionSeed;

        eventSource.addListener(new SnapshotSessionEventListener(){

            @Override
            public void onChangeSnapshot() {
                ensureBubblesFullyLoaded();
                onClear();
            }

            @Override
            public void onClear() {
                fullyInitializedBubbles.clear();
                exportedLazyLoadedBubbles.clear();
            }

            @Override
            public void onClose() {
                HibernatePersistenceSession.this.eventSource.removeListener(this);
            }
        });
    }

    public Session getWrappedSession() {
        return session;
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
                throw new ObjectNotFoundException(bubbleId, "Objekt finnes ikke: " + bubbleId);
            if (!isLazyLoadedBubblesAllowed()) {
                ensureFullyInitialized(bubble);
            } else {
                exportedLazyLoadedBubbles.put(bubble.getId(), bubble);
            }
            return bubble;
        } catch (HibernateException e) {
            throw new ImplementationException("Load feilet for " + bubbleId, e);
        }
    }

    /**
     * Laster et set av domenebobler basert på boblenes id'er. Dersom alle finnes i minne vil det ikke bli gjort kald til
     * databasen
     *
     * @param bubbleIds ider for bobler som skal lastest.
     * @return fundne objekter; et objekt per id.
     */
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
            FlushMode oldFlushMode = session.getFlushMode();
            try {
                // Disable flush. Det søkes kun etter objekter som allerede finnes i databasen
                session.setFlushMode(FlushMode.MANUAL);

                result = loadAllFromDatabase(idsToLoad);
                if (lazyLoadedBubblesAllowed) {
                    for (T bubble : result) {
                        exportedLazyLoadedBubbles.put(bubble.getId(), bubble);
                    }
                }
                result.addAll(alreadyLoaded);
            } finally {
                session.setFlushMode(oldFlushMode);
            }
        } else {
            result = new HashSet<T>(alreadyLoaded);
        }

        if (!lazyLoadedBubblesAllowed) {
            for (T bubble : result) {
                ensureFullyInitialized(bubble);
            }
        }
        if (bubbleIds.size() != result.size()) {
            //ids.removeAll(Store.convertObjectsToIds(result));
            bubbleIds = null;  //TODO: Fix
            throw new ImplementationException("Ikke alle objekter kunne finnes: " + bubbleIds);
        }
        return result;
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId) {
        T bubble = lookupInHibernateCache(bubbleId);
        if (bubble != null) {
            session.evict(bubbleId);
            fullyInitializedBubbles.remove(bubbleId);
            exportedLazyLoadedBubbles.remove(bubbleId);
        }
    }

    public void ensureBubblesFullyLoaded() {
        for (Object bubble : exportedLazyLoadedBubbles.values()) {
            ensureFullyInitialized((BubbleObject) bubble);
        }
    }

    protected final <T extends BubbleObject, I extends BubbleId<? extends T>> void checkSnapshotVersion(I bubbleId) {
        SnapshotVersion snapshotVersionFromHolder = snapshotVersionSeed.get();
        SnapshotVersion snapshotVersionInId = bubbleId.getSnapshotVersion();

        if (!snapshotVersionFromHolder.equals(snapshotVersionInId)) {
            throw new ImplementationException("Id har feil SnapshotVersion for session:" + bubbleId);
        }
    }

    /**
     * Laster alle fra database via criteria søk per type
     *
     * @param ids
     * @return
     * @throws ObjectNotFoundException
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
                throw new ImplementationException(e);
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
        T bubble;
        final EntityPersister classPersister = getClassPersister(bubbleId.getType());
        final EntityKey key = new EntityKey(bubbleId, classPersister, EntityMode.POJO);
        bubble = (T) ((SessionImpl) session).getPersistenceContext().getEntity(key);
        if (bubble == null) {
            bubble = (T) session.get(bubbleId.getType(), bubbleId);
        }
        return bubble;
    }

    /**
     * Sørger for at objektet er fuldstendig initialisert. Dvs. at objektets  assosiasjoner er lastet
     * fra databasen. Hvis en assosiasjon er definert som cascade vil de assosierte objektene også
     * bli initialisert.
     *
     * @param bubble helt eller delvis initialisert bubble objekt
     */
    private void ensureFullyInitialized(BubbleObject bubble) {
        BubbleObject initializedBubble = (BubbleObject) fullyInitializedBubbles.get(bubble.getId());
        if (initializedBubble == null) {
            try {
                IdentityHashMap initializedObjects = new IdentityHashMap();
                ensureInitialized(bubble, initializedObjects);
                fullyInitializedBubbles.put(bubble.getId(), bubble);
                exportedLazyLoadedBubbles.remove(bubble.getId());
            } catch (HibernateException e) {
                throw new ImplementationException("Kunne ikke initialisere lazy loaded associasjoner for: " + bubble.getId(), e);
            }
        } else {
            // Check that we got the same bubble
            if (initializedBubble != bubble)
                throw new ImplementationException("Hibernate returnerte ny instans av allerede loaded bubble (dette burde ikke kunne skje): " + bubble.getId());
        }
    }

    /**
     * Sørger for at objektets assosiasjoner er blitt initialisert med data fra databasen. Hvis en
     * assosiasjon er definert som cascade vil metoden bli kaldt rekursivt på de assosierte
     * objekter.
     *
     * @param object             a helt eller delvis initialisert objekt.
     * @param initializedObjects set av objekter som methoden allerede har initialisert
     * @throws HibernateException
     */
    private void ensureInitialized(Object object, IdentityHashMap initializedObjects) throws HibernateException {
        if (object == null) return;

        if (initializedObjects.containsKey(object)) return;
        initializedObjects.put(object, null);

        ClassMetadata classMetadata = ((SessionImpl) session).getFactory().getClassMetadata(object.getClass());

        if( erAvTypeSomIkkeSkalInitialiseresVidere(classMetadata)) return;

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
                Criteria criteria = session.createCriteria(type).add(Expression.in(ID_KOLONNE_NAVN, subList));
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
        PersistenceContext context = ((SessionImpl) session).getPersistenceContext();
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
                lastResultForClass = ((SessionImpl) session).getFactory().getEntityPersister(theClass.getName());
                lastClass = theClass;
            }
            return lastResultForClass;
        } catch (MappingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returnere true hvis denne instans av HibernatedSessionWrapper har lov til å returnere bubler
     * som kun er delvis initialisert. Hvis metoden returnerer true bør metoden {@link #ensureFullyInitialized} kalles
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
}
