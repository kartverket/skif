package no.statkart.skif.store.service;

import com.google.common.collect.Lists;
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
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import static no.statkart.skif.util.HibernateHelper.*;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.5.0
 */
// OBS! Originalen ligger i hs 3.6
public class EndringsloggServiceImpl<E extends AbstractEndring<?, ?>, EI extends AbstractEndringId<?>> implements EndringsloggService<E, EI> {
    private static final int LIMIT = 1000;

    private final Provider<SnapshotVersion> snapshotVersionProvider;

    private final EndringManagerConfiguration<?> endringManagerConfiguration;

    private final Store store;

    private final Provider<SessionSelector> sessionSelectorProvider;

    protected EndringsloggServiceImpl(Provider<SnapshotVersion> snapshotVersionProvider, EndringManagerConfiguration<?> endringManagerConfiguration, Store store, Provider<SessionSelector> sessionSelectorProvider) {
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

    private <I extends AbstractEndringId<?>> I findSisteEndringId(Session session) {
        Criteria criteria = session.createCriteria(AbstractEndring.class);
        criteria.setProjection(Projections.max("id"));
        I endringId = (I) criteria.uniqueResult();
        return endringId;
    }

    @Override
    public Endringer<E> findEndringer(@Nullable AbstractEndringId<?> id, Class<? extends BubbleObject> bobleklasse, @Nullable String filter, ReturnerBobler returnerBobler, int maksAntall) {
        Endringer<E> endringer = new Endringer<E>();

        Class<? extends AbstractEndring> endringClass = endringManagerConfiguration.getEndringsklasseNullSafe(bobleklasse);
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        maksAntall = Math.min(100000, maksAntall);

        try {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            AbstractEndringId<?> sisteEndringId = findSisteEndringId(session);
            if (sisteEndringId == null || sisteEndringId.equals(id)) {
                // Ingen nye endringer
                endringer.setAlleEndringerFunnet(true);
                endringer.setSisteEndringIdProsessert(sisteEndringId);
                return endringer;
            } else if (returnerBobler == ReturnerBobler.Aldri) {
                // Finn endringer, objekter skal ikke returneres
                Criteria criteria = session.createCriteria(endringClass);
                if (id != null) {
                    criteria.add(Restrictions.gt("id", id));
                }
                //criteria.addOrder(Order.asc("id")); // trengs ikke da Endring er definert som organization index tabell
                criteria.setMaxResults(maksAntall);
                List<E> endringList = criteria.list();
                boolean alleEndringerFunnet = endringList.size() < maksAntall;
                endringer.setAlleEndringerFunnet(alleEndringerFunnet);
                endringer.setEndringList(endringList);
                endringer.setSisteEndringIdProsessert(alleEndringerFunnet ? sisteEndringId : endringList.get(maksAntall - 1).getId());
            } else if (returnerBobler == ReturnerBobler.Alltid) {
                int oensketAntallEndringer = Math.min(LIMIT, maksAntall);
                AbstractEndringId<?> fromId = id;
                List<E> accumulatedEndringer = null;
                LinkedHashSet<BubbleId<?>> accumulatedEndretIds = Sets.newLinkedHashSet();
                while (oensketAntallEndringer > 0 && !endringer.isAlleEndringerFunnet()) {
                    // Finn endringer
                    Criteria criteria = session.createCriteria(endringClass);
                    if (fromId != null) {
                        criteria.add(Restrictions.gt("id", fromId));
                    }
                    //criteria.addOrder(Order.asc("id")); // trengs ikke da Endring er definert som organization index tabell
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
                    if (!accumulatedEndretIds.isEmpty()) {
                        store.evict(endretIds);
                    }
                    store.get(endretIds);
                    accumulatedEndretIds.addAll(endretIds);

                    // Fikk vi alle endringer
                    if (endringList.size() < oensketAntallEndringer) {
                        // Kanskje, må sjekke at endringsnummeret ikke har endret seg
                        AbstractEndringId<?> nytSisteEndringId = findSisteEndringId(session);
                        if (nytSisteEndringId.equals(sisteEndringId)) {
                            // Det har ikke kommet nye endringer. Stopper her.
                            endringer.setAlleEndringerFunnet(true);
                            endringer.setEndringList(accumulatedEndringer);
                            ArrayList endretObjects = new ArrayList(accumulatedEndretIds.size());
                            store.getOrdered(accumulatedEndretIds, endretObjects);
                            endringer.setObjects(endretObjects);
                        } else {
                            // Det har kommet flere endringer mens vi leste objekter. Forsetter.
                            oensketAntallEndringer = oensketAntallEndringer - endringList.size();
                        }
                    } else {
                        // Har ikke plass til flere endringer
                        endringer.setAlleEndringerFunnet(false);
                        endringer.setEndringList(accumulatedEndringer);
                        ArrayList endretObjects = new ArrayList(accumulatedEndretIds.size());
                        store.getOrdered(accumulatedEndretIds, endretObjects);
                        endringer.setObjects(endretObjects);
                        oensketAntallEndringer = 0;
                    }
                }
            } else {
                throw new NotImplementedException("Kommer senere");
            }
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
        return endringer;
    }

    @Override
    public <T extends BubbleObject> Kontroll calcEndringskontroll(@Nullable AbstractEndringId<?> id, Class<T> bobleklasse, @Nullable String filter, int antall) {
        Class<? extends AbstractEndring> endringClass = endringManagerConfiguration.getEndringsklasseNullSafe(bobleklasse);
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        // TODO: Legge inn filter
        try {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            ClassMetadata classMetadata = getClassMetadata(session, endringClass);
            String tableName = getTableName(classMetadata);
            String discriminatorSql = getDiscriminatorSql(classMetadata, "t");

            String sql = "select count(id) from (select * from (select t.id from " + tableName + " t where t.id>:id" + discriminatorSql + ") where rownum <=:antall)";

            Query query = session.createSQLQuery(sql)
                    .setParameter("antall", antall);
            if (id == null) {
                query.setParameter("id", 0L);
            } else {
                query.setParameter("id", id);
            }
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

    private <T extends BubbleObject> void checkEndringsklasseFinnes(Class<T> bobleklasse) {
        endringManagerConfiguration.getEndringsklasseNullSafe(bobleklasse);
    }
}
