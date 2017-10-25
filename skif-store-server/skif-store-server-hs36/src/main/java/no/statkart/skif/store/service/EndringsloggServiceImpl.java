package no.statkart.skif.store.service;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Provider;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType;
import no.statkart.skif.store.*;
import no.statkart.skif.store.endringslogg.*;
import no.statkart.skif.store.persistence.SessionSelector;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;

import javax.annotation.Nullable;
import java.util.*;

import static no.statkart.skif.util.HibernateHelper.*;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.5.0
 */
// OBS! Det ligger en kopi i hs 3.2
public class EndringsloggServiceImpl<E extends AbstractEndring<?, ?>, EI extends AbstractEndringId> implements EndringsloggService<E, EI> {
    private static final int LIMIT = 1000;

    private final Class<EI> endringIdClass;

    private final Provider<SnapshotVersion> snapshotVersionProvider;

    private final EndringManagerConfiguration<?> endringManagerConfiguration;

    private final Store store;

    private final Provider<SessionSelector> sessionSelectorProvider;

    protected EndringsloggServiceImpl(Class<EI> endringIdClass, Provider<SnapshotVersion> snapshotVersionProvider, EndringManagerConfiguration<?> endringManagerConfiguration, Store store, Provider<SessionSelector> sessionSelectorProvider) {
        this.endringIdClass = endringIdClass;
        this.snapshotVersionProvider = snapshotVersionProvider;
        this.endringManagerConfiguration = endringManagerConfiguration;
        this.store = store;
        this.sessionSelectorProvider = sessionSelectorProvider;
    }

    @Nullable
    @Override
    public EI findSisteEndringId() {
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            return findSisteEndringId(session);
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }

    @Override
    public Endringer<E> findEndringer(@Nullable EI id, Class<? extends BubbleObject> bobleklasse, @Nullable String filter, ReturnerBobler returnerBobler, int maksAntall) {
        Preconditions.checkNotNull(bobleklasse, "domainKlasse er obligatorisk");
        Preconditions.checkNotNull(returnerBobler, "returnerBobler er obligatorisk");
        Preconditions.checkArgument(maksAntall >= 0, "maksAntall er negativ");

        Endringer<E> endringer = new Endringer<>();

        Class<? extends AbstractEndring> endringClass = endringManagerConfiguration.getEndringsklasseNullSafe(bobleklasse);
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        maksAntall = Math.min(100000, maksAntall);
        id = setIfNull(id);

        try {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            EI sisteEndringId = findSisteEndringId(session);
            if (sisteEndringId.equals(id)) {
                // Ingen nye endringer
                endringer.setAlleEndringerFunnet(true);
                endringer.setSisteEndringIdProsessert(sisteEndringId);
                return endringer;
            } else if (maksAntall == 0) {
                endringer.setAlleEndringerFunnet(false); // Siden vi er i en else
                endringer.setSisteEndringIdProsessert(id);
            } else if (returnerBobler == ReturnerBobler.Aldri) {
                // Finn endringer, objekter skal ikke returneres
                Criteria criteria = session.createCriteria(endringClass);
                criteria.add(Restrictions.gt("id", id));
                criteria.addOrder(Order.asc("id"));
                criteria.setMaxResults(maksAntall);
                List<E> endringList = criteria.list();
                boolean alleEndringerFunnet = endringList.size() < maksAntall;
                endringer.setAlleEndringerFunnet(alleEndringerFunnet);
                endringer.setEndringList(endringList);
                endringer.setSisteEndringIdProsessert(alleEndringerFunnet ? sisteEndringId : endringList.get(maksAntall - 1).getId());
            } else if (returnerBobler == ReturnerBobler.Alltid) {
                int oensketAntallEndringer = Math.min(LIMIT, maksAntall);
                List<E> accumulatedEndringer = null;
                Map<BubbleId<?>, BubbleObject> accumulatedBubbleObjects = Maps.newHashMap();
                while (oensketAntallEndringer > 0 && !endringer.isAlleEndringerFunnet()) {
                    // Finn endringer
                    Criteria criteria = session.createCriteria(endringClass);
                    if(endringer.getSisteEndringIdProsessert() == null){
                        criteria.add(Restrictions.gt("id", id));
                    }   else {
                        criteria.add(Restrictions.gt("id",endringer.getSisteEndringIdProsessert()));
                    }

                    criteria.addOrder(Order.asc("id"));
                    criteria.setMaxResults(oensketAntallEndringer);

                    List<E> endringList = criteria.list();
                    if (accumulatedEndringer == null) {
                        accumulatedEndringer = endringList;
                    } else {
                        accumulatedEndringer.addAll(endringList);
                    }

                    // Hent objekter
                    List<BubbleId<?>> endretIds = Lists.newArrayListWithCapacity(endringList.size());
                    for (AbstractEndring<?, ?> endring : endringList) {
                        switch (endring.getEndringstype()) {
                            case Nyoppretting:
                            case Oppdatering:
                                endretIds.add(endring.getEndretBubbleId());
                        }
                    }
                    if (!accumulatedBubbleObjects.isEmpty()) {
                        store.evict(accumulatedBubbleObjects.keySet());   //Det er greit å evicte ids som ikke finnes i store
                    }
                    //Noen objekter kan ha blitt fjernet på et senere tidspunkt, må derfor bruke getIgnoreMissing
                    List<BubbleObject> bubbleObjects = store.getIgnoreMissing(endretIds);

                    for (BubbleObject object : bubbleObjects) {
                        accumulatedBubbleObjects.put(object.getId(), object);
                    }

                    // Fikk vi alle endringer?
                    if (endringList.size() < oensketAntallEndringer) {
                        if (!accumulatedEndringer.isEmpty()) {
                            endringer.setSisteEndringIdProsessert(accumulatedEndringer.get(accumulatedEndringer.size() - 1).getId());
                        }
                        // Kanskje, må sjekke at endringsnummeret ikke har endret seg
                        EI nytSisteEndringId = findSisteEndringId(session);
                        if (nytSisteEndringId.equals(sisteEndringId) || endringList.isEmpty()) {
                            // Det har ikke kommet nye endringer. Stopper her.
                            endringer.setAlleEndringerFunnet(true);
                            endringer.setEndringList(accumulatedEndringer);
                            endringer.setSisteEndringIdProsessert(nytSisteEndringId);
                            endringer.setObjects(sorterBobler(accumulatedEndringer, accumulatedBubbleObjects));
                        } else {
                            // Det har kommet flere endringer mens vi leste objekter. Forsetter.
                            oensketAntallEndringer = oensketAntallEndringer - endringList.size();
                        }
                    } else {
                        // Har ikke plass til flere endringer, kanskje det finnes flere
                        endringer.setAlleEndringerFunnet(false);
                        endringer.setEndringList(accumulatedEndringer);
                        endringer.setSisteEndringIdProsessert(accumulatedEndringer.get(accumulatedEndringer.size() - 1).getId());
                        endringer.setObjects(sorterBobler(accumulatedEndringer, accumulatedBubbleObjects));
                        oensketAntallEndringer = 0;
                    }
                    sisteEndringId = (EI) endringer.getSisteEndringIdProsessert();
                }
            } else {
                throw new NotImplementedException("Denne opsjon er ikke implementert. Kommer senere");
            }
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
        return endringer;
    }

    private List<BubbleObject> sorterBobler(List<E> accumulatedEndringer, Map<BubbleId<?>, BubbleObject> accumulatedBubbleObjects) {
        Set<BubbleObject> objects = Sets.newLinkedHashSetWithExpectedSize(accumulatedEndringer.size());

        for (E endring : accumulatedEndringer) {
            BubbleObject object = accumulatedBubbleObjects.get(endring.getEndretBubbleId());
            if (object != null) {
                objects.add(object);
            }
        }

        return Lists.newArrayList(objects);
    }

    @Override
    public <T extends BubbleObject> Kontroll calcEndringskontroll(@Nullable EI id, Class<T> bobleklasse, @Nullable String filter, int antall) {
        Class<? extends AbstractEndring> endringClass = endringManagerConfiguration.getEndringsklasseNullSafe(bobleklasse);
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        id = setIfNull(id);
        // TODO: Legge inn filter
        try {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            ClassMetadata classMetadata = getClassMetadata(session, endringClass);
            String tableName = getTableName(classMetadata);
            String discriminatorSql = getDiscriminatorSql(classMetadata, "t");

            String sql = "select count(id) from (select * from (select t.id from " + tableName + " t where t.id>:id" + discriminatorSql + ") where rownum <=:antall)";

            Query query = session.createSQLQuery(sql)
                    .setParameter("id", 0L)
                    .setParameter("antall", antall);

            Kontroll result = new Kontroll();
            result.setAntall(((Number) query.uniqueResult()).longValue()); // kan ikke caste direkte til Long pga forskjell på datatype her i hibernate 3.2 og 3.6
            return result;
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }

    @Override
    public <T extends BubbleObject> Kontroll calcObjektkontrollForList(Collection<? extends BubbleId<?>> ids, Class<T> bobleklasse) {
        checkEndringsklasseFinnes(bobleklasse);
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            ClassMetadata classMetadata = getClassMetadata(session, bobleklasse);
            String tableName = getTableName(classMetadata);
            String discriminatorSql = getDiscriminatorSql(classMetadata, "t");

            String sql = "select count(t.id) from " + tableName + " t where t.id in (select * from table(:ids))" + discriminatorSql;

            Query query = session.createSQLQuery(sql)
                    .setParameter("ids", ids, new OracleLongBubbleIdArrayCustomType());

            Kontroll result = new Kontroll();
            result.setAntall(((Number) query.uniqueResult()).longValue()); // kan ikke caste direkte til Long pga forskjell på datatype her i hibernate 3.2 og 3.6
            return result;
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }

    private EI findSisteEndringId(Session session) {
        Criteria criteria = session.createCriteria(AbstractEndring.class);
        criteria.setProjection(Projections.max("id"));
        EI endringId = (EI) criteria.uniqueResult();
        endringId = setIfNull(endringId);
        return endringId;
    }

    private EI setIfNull(EI id) {
        if (id == null) {
            id = BubbleIds.createInstance(endringIdClass, 0L, SnapshotVersionContext.getInstance().getSnapshotVersion());
        }
        return id;
    }

    private <T extends BubbleObject> void checkEndringsklasseFinnes(Class<T> bobleklasse) {
        endringManagerConfiguration.getEndringsklasseNullSafe(bobleklasse);
    }
}
