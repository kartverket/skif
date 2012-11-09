package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.util.JDBCHelper;
import org.hibernate.*;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.criterion.Expression;
import org.hibernate.engine.CascadeStyle;
import org.hibernate.engine.CascadingAction;
import org.hibernate.engine.EntityKey;
import org.hibernate.engine.PersistenceContext;
import org.hibernate.impl.SessionImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.type.AbstractComponentType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * @author Henrik Fredholm
 */
public class HibernatePersistenceSessionMasterImpl implements HibernatePersistenceSessionMaster {
    private static Logger logger = LoggerFactory.getLogger(HibernatePersistenceSessionMasterImpl.class);
    private static int CRITERIA_BATCH_POWER = 9;
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


    @Override
    public SnapshotVersion getSnapshot() {
        return sessionFactoryDescriptor.getSnapshotVersion();
    }

    @Override
    public SnapshotVersion setSnapshot(SnapshotVersion snapshotVersion) {
        SnapshotVersion previous = sessionFactoryDescriptor.getSnapshotVersion();
        if (snapshotVersion != previous) {
            if (reserveCount > 0) {
                throw new ImplementationException("Sessionen er reservert. Kan ikke endre SnapshotVersion fra " + previous + " til " + snapshotVersion);
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
                throw new ImplementationException("Session er ikke reservert");
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
            //ids.removeAll(Store.convertObjectsToIds(result));
            bubbleIds = null;  //TODO: Fix
            throw new ImplementationException("Ikke alle objekter kunne finnes: " + bubbleIds);
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
            session().save(bubbleObject);
        } catch (HibernateException e) {
            throw new ImplementationException("Update feilet for " + bubbleObject, e);
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
            convertObjectIfTypeChangedAndEvictOtherInstance(bubbleObject);
            session().update(bubbleObject); // Viktig at class-mapping inneholder 'select-before-update="true"'. Dette bør settes automatisk ved konfigurasjon av hibernate session factory.
        } catch (HibernateException e) {
            throw new ImplementationException("Update feilet for " + bubbleObject, e);
        } catch (SQLException e) {
            throw new ImplementationException("Update feilet for " + bubbleObject, e);
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
            throw new ImplementationException("Delete feilet for " + bubbleObject, e);
        }
    }

    /**
     * Sjekk om objektet har endret type i forhold til det som ligger i Hibernate/databasen. Dersom så har skjedd, så
     * må ikke-felles felter nullstilles og det utføres en spesiell SQL som endrer objekttypen i databasen.
     * <p/>
     * Uansett sørges det for at Hibernates cache ikke inneholder et objekt med samme id, med mindre det også er
     * nøyaktig samme objekt (instans) som <i>bubbleObject</i>.
     *
     * @param bubbleObject et objekt som kanskje er lastet gjennom hibernate tidligere i denne
     *                     sesjonen
     * @throws HibernateException    dersom get() eller evict() på hibernate session feiler
     * @throws java.sql.SQLException dersom SQL feiler ved typeendring
     */
    private void convertObjectIfTypeChangedAndEvictOtherInstance(BubbleObject bubbleObject) throws HibernateException, SQLException {
        // Hibernate tillater ikke update på et objekt når et annet objekt med samme id finnes i hibernate sin cache
        // Vi må teste på dette og evt. kaste ut det gamle objektet fra hibernate sin cache.
        // Dette kan kun testes ved å forsøke å loaded objektet og se om man får det samme objket som man
        // har fra før. Dersom objektet ikke var loadet vil Hibernate retunerer en proxy. Dette kallet gir
        // derfor ingen database aksess.
        Object obj = session().get(bubbleObject.getBubbleId().getBaseType(), bubbleObject.getBubbleId(), LockMode.NONE);
        if (obj != bubbleObject) {
            if (!(bubbleObject.getClass().isInstance(obj))) {
                changeType(bubbleObject, (BubbleObject) obj);

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

                session().evict(obj);
            }
        } else {
            // Gjør ingen ting, bubbleObject er det objektet som allerede ligger i hibernate sessionen
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
            throw new ImplementationException("Kunne ikke blanke ut felter i fra-objekt som endrer type", e, logger);
        }

        flush();
        evict(currentObject.getBubbleId());

        Statement statement = session().connection().createStatement();
        try {
            final AbstractEntityPersister fromEntityPersister = (AbstractEntityPersister) getClassPersister(previousObject.getClass());
            final AbstractEntityPersister toEntityPersister = (AbstractEntityPersister) getClassPersister(currentObject.getClass());
            final String dbTable = toEntityPersister.getTableName();

            if (fromEntityPersister.isMultiTable()) {
                throw new ImplementationException("Kan ikke endre type fra " + previousObject.getClass() + " da denne spenner flere tabeller", logger);
            }
            if (toEntityPersister.isMultiTable()) {
                throw new ImplementationException("Kan ikke endre type til " + currentObject.getClass() + " da denne spenner flere tabeller", logger);
            }
            if (!dbTable.equals(fromEntityPersister.getTableName())) {
                throw new ImplementationException("Kan ikke endre type fra " + previousObject.getClass() + " til " + currentObject.getClass() + " da de ligger i forskjellige tabeller", logger);
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
                throw new ImplementationException("Ved endring av bobletype skulle antall oppdaterte rader vært 1, var " + rows, logger);
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
            throw new ImplementationException("Id har feil SnapshotVersion for session:" + bubbleId);
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
            throw new RuntimeException(e);
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
            throw new ImplementationException("Lokal transaksjon har allerede blitt startet");
        }
        localTransaction = session().beginTransaction();
    }

    @Override
    public boolean hasLocalTrasaction() {
        return localTransaction != null;
    }

    public void rollback() {
        if (localTransaction == null) {
            throw new ImplementationException("Lokal transaksjon har ikke blitt startet");
        }
        localTransaction.rollback();
        localTransaction = null;
        clear();
    }

    @Override
    public void commit() {
        if (localTransaction == null) {
            throw new ImplementationException("Lokal transaksjon har ikke blitt startet");
        }
        localTransaction.commit();
        localTransaction = null;
    }
}
