package no.statkart.skif.store.persistence.hibernate;

import com.google.common.base.Preconditions;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.exception.ObjectsNotFoundException;
import no.statkart.skif.persistence.hibernate.EmptyCollectionsFlagUpdater;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Bubbles;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import org.hibernate.FlushMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.internal.SessionImpl;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * @author Henrik Fredholm
 */
@SuppressWarnings({"WeakerAccess"})
public class HibernatePersistenceSessionMasterImpl implements HibernatePersistenceSessionMaster {
    protected static Logger logger = LoggerFactory.getLogger(HibernatePersistenceSessionMasterImpl.class);
    private static final int CRITERIA_BATCH_POWER = 9;
    private static final String ID_KOLONNE_NAVN = "id";

    protected final HibernateSessionFactoryManager sessionFactoryManager;
    protected final HibernateSessionFactoryDescriptor sessionFactoryDescriptor;
    protected final EmptyCollectionsFlagUpdater emptyCollectionsFlagUpdater = new EmptyCollectionsFlagUpdater();

    /* Holder referanse til hibernate sesjonen */
    protected Session lazySession;

    private int reserveCount = 0;

    protected Transaction localTransaction;

    protected HibernateLazySupport hibernateLazySupport = new HibernateLazySupport(this::session);
    protected HibernateDetachedSupport hibernateDetachedSupport = new HibernateDetachedSupport(hibernateLazySupport);

    public HibernatePersistenceSessionMasterImpl(HibernateSessionFactoryManager sessionFactoryManager) {
        this.sessionFactoryManager = sessionFactoryManager;
        sessionFactoryDescriptor = sessionFactoryManager.getDescriptor();
    }

    public Map<BubbleId, BubbleObject> getFullyInitializedBubbles() {
        return hibernateLazySupport.getFullyInitializedBubbles();
    }

    @SuppressWarnings("UnusedDeclaration") // Public API
    public Map getExportedLazyLoadedBubbles() {
        return hibernateLazySupport.getExportedLazyLoadedBubbles();
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
        if (!Objects.equals(snapshotVersion,previous)) {
            if (reserveCount > 0) {
                throw new ImplementationException("Session is reserved and can not change SnapshotVersion from " + previous + " to " + snapshotVersion);
            }
            hibernateLazySupport.flushIfDirtyThenFullyInitializeAllExportedBubblesAndClearSession();
            sessionFactoryDescriptor.setSnapshotVersion(session(), snapshotVersion);
        }
        return previous;
    }

    @Override
    public void clear() {
        // Ikke start en session bare for å si clear. Hvis det ikke er en session, så bør det ikke være noe å clear-e.
        if (lazySession != null) {
            hibernateLazySupport.clear();
        }
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
        if (logger.isTraceEnabled()) {
            logger.trace( "Using session " + System.identityHashCode( lazySession ) + " with " + sessionFactoryDescriptor.getSnapshotVersion());
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
            hibernateLazySupport.markBubbleAsExportedLazyOrFullLoaded(bubble);
            return bubble;
        } catch (HibernateException e) {
            throw new no.statkart.skif.exception.ObjectNotFoundException(bubbleId, e);
        }
    }

    /**
     * Laster et set av domenebobler basert på boblenes id'er. Dersom alle finnes i minne vil det ikke bli gjort kald til
     * databasen
     *
     * @param bubbleIds ider for bobler som skal lastes.
     * @return funnede objekter; et objekt per id.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> get(Collection<I> bubbleIds) {
        List<T> alreadyLoaded = new ArrayList<>(bubbleIds.size());
        Set<I> idsToLoad = new HashSet<>(bubbleIds.size());

        for (I bubbleId : bubbleIds) {
            checkSnapshotVersion(bubbleId);
            T bubble;
            if ((bubble = (T) hibernateLazySupport.getFullyInitializedBubbles().get(bubbleId)) != null) {
                alreadyLoaded.add(bubble);
            } else if ((bubble = (T) hibernateLazySupport.getExportedLazyLoadedBubbles().get(bubbleId)) != null) {
                alreadyLoaded.add(bubble);
            } else if ((bubble = lookupInHibernateCache(bubbleId)) != null) {
                hibernateLazySupport.getExportedLazyLoadedBubbles().put(bubbleId, bubble);
                alreadyLoaded.add(bubble);
            } else if ((bubble = lookupInHibernateCache(bubbleId)) != null) {
                alreadyLoaded.add(bubble);
            } else {
                idsToLoad.add(bubbleId);
            }
        }
        Set<T> result;
        if (idsToLoad.size() > 0) {
            FlushMode oldFlushMode = session().getHibernateFlushMode();
            try {
                // Disable flush. Det søkes kun etter objekter som allerede finnes i databasen
                session().setHibernateFlushMode(FlushMode.MANUAL);

                result = loadAllFromDatabase(idsToLoad);
                if (hibernateLazySupport.isLazyLoadedBubblesAllowed()) {
                    for (T bubble : result) {
                        hibernateLazySupport.getExportedLazyLoadedBubbles().put(bubble.getId(), bubble);
                    }
                }
                result.addAll(alreadyLoaded);
            } finally {
                session().setHibernateFlushMode(oldFlushMode);
            }
        } else {
            result = new HashSet<>(alreadyLoaded);
        }

        if (!hibernateLazySupport.isLazyLoadedBubblesAllowed()) {
            for (T bubble : result) {
                hibernateLazySupport.ensureFullyLoaded(bubble);
            }
        }
        if (bubbleIds.size() != result.size()) {
            Set<BubbleId<?>> ids = new HashSet<>(bubbleIds);
            ids.removeAll(Bubbles.asIds(result));
            throw new ObjectsNotFoundException(ids);
        }
        return result;
    }

    /**
     * Knytter {@code bubbleObject} til underliggende hibernate sessison. Ved senere kall til flush() vil endringene
     * bli sendt til databasen.
     */
    @Override
    public <T extends BubbleObject> void insert(T bubbleObject) {
        hibernateDetachedSupport.insert(bubbleObject);
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
        hibernateDetachedSupport.update(bubbleObject);
        // Hvis alle collections er tomme i original og ny detached boble, så genererer Hibernate ikke en
        // onPreUpdateCollectionEvent som trigger beregning av emptycollectionsflagget. Tilsvarende, ved update av
        // en attached boble hvor flagget har blitt nullstilt, for eksempel i en ekstern prosess før boblen ble
        // lest, så vil flagget ikke bli beregnet på nytt hvis alle collections er uendret. Ved alltid å beregne
        // flagget her så sikrer vi at disse spesialtilfeller håndteres. Der er mulig å utelate denne beregningen,
        // men da vil flagget forbli uendret for disse tilfeller. Overhead ved kallet er lavt så det er enklere at
        // disse tilfeller også håndteres.
       emptyCollectionsFlagUpdater.updateEmptyCollectionsFlag(session(), bubbleObject);
    }

    /**
     * Sletter objekt som har samme id som {@code bubbleObject} fra underliggende hibernate session. Ved senere kall til
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
            hibernateLazySupport.markAsRemoved(bubbleObject.getId());
            session().delete(obj);
        } catch (HibernateException e) {
            throw new ImplementationException("Delete failed for " + bubbleObject, e);
        }
    }




    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId) {
        hibernateLazySupport.evict(bubbleId);
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
        hibernateLazySupport.getFullyInitializedBubbles().remove(bubble.getId());
        session().refresh(bubble);
        if (!hibernateLazySupport.isLazyLoadedBubblesAllowed()) {
            hibernateLazySupport.ensureFullyLoaded(bubble);
        } else {
            hibernateLazySupport.getExportedLazyLoadedBubbles().put(bubble.getId(), bubble);
        }
    }

    @Override
    public void ensureBubblesFullyLoaded() {
        hibernateLazySupport.ensureBubblesFullyLoaded();
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

        List<CriteriaQuery<T>> criterias = buildCriterias(ids);
        for (CriteriaQuery<T> criteria : criterias) {
            try {
                List<T> objects = session().createQuery(criteria).getResultList();
                // noinspection
                allEntities.addAll(objects);
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
    public <T extends BubbleObject, I extends BubbleId<? extends T>> List<CriteriaQuery<T>> buildCriterias(Set<I> ids) {
        List<CriteriaQuery<T>> criterias = new ArrayList<>();
        LinkedHashMap<Class<T>, ? extends Set<I>> idsByType = getIdsByType(ids);
        for (Class<T> type : idsByType.keySet()) {
            List<CriteriaQuery<T>> criteriasForType = buildCriteriaForType(type, idsByType.get(type));
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
    private <T extends BubbleObject, I extends BubbleId<? extends T>> LinkedHashMap<Class<T>, Set<I>> getIdsByType(Set<I> ids) {
        LinkedHashMap<Class<T>, Set<I>> idsByType = new LinkedHashMap<>();
        for (I id : ids) {
            @SuppressWarnings("unchecked")
            Class<T> entityClazz = id.getBaseType(); //Class name for the entity owning the id
            // Endringer ligger i mange forskjellige tabeller. Hvis vi gjør query via basetype må
            // Hibernate gjøre en masse joins.
            // TODO: Bruke Hibernate metadata til å finne ut av dette. Pt er Endring den eneste klasse som er slik.
            //if (entityClazz == Endring.class) {
            //    entityClazz = id.getType();
            //}
            if (!idsByType.containsKey(entityClazz)) {
                //Add an entry in the map for holding all ids of this typename
                idsByType.put(entityClazz, new HashSet<>());
            }

            idsByType.get(entityClazz).add(id);
        }
        return idsByType;
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> T getFromHibernateSessionOrLoad(I bubbleId) {
        T bubble = getFromHibernatePersistenceContext(bubbleId);
        if (bubble == null) {
            //noinspection
            bubble = session().get(bubbleId.getType(), bubbleId, LockMode.NONE);
        }
        return bubble;
    }

    private <T extends BubbleObject, I extends BubbleId<? extends T>> T getFromHibernatePersistenceContext(I bubbleId) {
        final EntityPersister classPersister = getClassPersister(bubbleId.getType());
        final EntityKey key = new EntityKey(bubbleId, classPersister);
        //noinspection unchecked,UnnecessaryLocalVariable
        T bubble = (T) ((SessionImpl) session()).getPersistenceContext().getEntity(key);
        return bubble;
    }

    /**
     * Sørger for at objektet er fullstendig lastet. Dvs. at objektets  assosiasjoner er lastet
     * fra databasen. Hvis en assosiasjon er definert som cascade vil de assosierte objektene også
     * bli initialisert.
     *
     * @param bubble helt eller delvis initialisert bubble objekt
     */
    public <T extends BubbleObject> void ensureFullyLoaded(T bubble) {
        hibernateLazySupport.ensureFullyLoaded(bubble);
    }

    /**
     * Lager <code>Criteria</code>-objekter for lasting av objekter av en angitt type. Pga.
     * begrensninger i Oracle for hvor mange uttrykk det kan være i en WHERE-claus så deles
     * spørringen opp i flere søk/criteria-objekter dersom listen over id'er er over størrelsen
     * definert i <code>OracleUtils.SQL_EXPRESSION_MAX_SIZE</code>.
     * <p>
     *
     * @param type klassen til bobler som skal lastes
     * @param ids  et sett med id'er for boblene
     * @return en liste med <code>Criteria</code>-objekter for uthenting av boblene fra databasen
     */
    protected <T extends BubbleObject, I extends BubbleId<? extends T>> List<CriteriaQuery<T>> buildCriteriaForType(Class<T> type, Collection<I> ids) {
        List<CriteriaQuery<T>> criterias = new ArrayList<>();
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
                CriteriaBuilder cb = session().getCriteriaBuilder();
                CriteriaQuery<T> cq = cb.createQuery(type);
                Root<T> root = cq.from(type);
                cq.where(root.get(ID_KOLONNE_NAVN).in(subList));
                criterias.add(cq);
            }
        }
        return criterias;
    }

    /**
     * Metode for å sjekke om objektet allerede er lastet av hibernate uten at hibernate forsøker å laste objektet eller lager
     * en proxy.
     *
     * @param aClass      Persistent Objektklasse for <code>hibernateId</code> Eks. Tedm for TedmPK
     * @param hibernateId identifikator for objekt vi vil sjekke om finnes i cachen
     * @return Objektet dersom det er lastet
     */

    protected final Object lookupInHibernateCache(Class aClass, Serializable hibernateId) {
        final EntityPersister classPersister = getClassPersister(aClass);
        final EntityKey key = new EntityKey(hibernateId, classPersister);
        PersistenceContext context = ((SessionImpl) session()).getPersistenceContext();
        return context.getEntity(key);
    }

    /**
     * Metode for å sjekke om objektet allerede er lastet av hibernate uten at hibernate forsøker å laste objektet eller lager
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


    // Hjelpe variable for optimalisering
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
     * Returnere true hvis denne instans av HibernatedSessionWrapper har lov til å returnere bobler
     * som kun er delvis initialisert. Hvis metoden returnerer true bør metoden {@link #ensureFullyLoaded} kalles
     * manuelt for bobler som skal returneres til klienten slik at deres verdier blir satt før sessionen lukkes
     *
     * @return true hvis HibernatedSessionWrapper har lov til å returnere bobler som kun er delvis
     *         initialisert
     */
    public boolean isLazyLoadedBubblesAllowed() {
        return hibernateLazySupport.isLazyLoadedBubblesAllowed();
    }


    /**
     * Setter om denne instance av HibernateSessionWrapper har lov til å retunere bobler som kun er
     * delvis initialisert.
     */
    public void setLazyLoadedBubblesAllowed(boolean lazyLoadedBubblesAllowed) {
        this.hibernateLazySupport.setLazyLoadedBubblesAllowed(lazyLoadedBubblesAllowed);
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
        Preconditions.checkState(hibernateLazySupport.getFullyInitializedBubbles().size() == 0, "FullyInitializedBubbles er ikke tom");
        Preconditions.checkState(hibernateLazySupport.getExportedLazyLoadedBubbles().size() == 0, "ExportedLazyLoadedBubbles er ikke tom");
        PersistenceContext persistenceContext = ((SessionImpl) session()).getPersistenceContext();
        Preconditions.checkState(persistenceContext.getEntitiesByKey().size() == 0, "EntitiesByKey er ikke tom");
        Preconditions.checkState(persistenceContext.reentrantSafeEntityEntries().length == 0, "EntityEntries er ikke tom");
        Preconditions.checkState(persistenceContext.getCollectionEntries().size() == 0, "CollectionEnties er ikke tom");
        Preconditions.checkState(persistenceContext.getCollectionsByKey().size() == 0, "CollectionEntiesByKey er ikke tom");
        Preconditions.checkState(persistenceContext.getNullifiableEntityKeys().size() == 0, "NullifiableEntityKeys er ikke tom");
    }
}
