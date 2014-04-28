package no.statkart.skif.storetest.service.endringslogg;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.NotImplementedException;
import no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType;
import no.statkart.skif.store.*;
import no.statkart.skif.store.endringslogg.AbstractEndring;
import no.statkart.skif.store.endringslogg.AbstractEndringId;
import no.statkart.skif.store.endringslogg.ReturnerBobler;
import no.statkart.skif.store.persistence.SessionSelector;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.EndringId;
import no.statkart.skif.storetest.domain.endringslogg.Endringer;
import no.statkart.skif.storetest.endringslogg.EndringFinder;
import no.statkart.skif.storetest.endringslogg.EndringManagerConfiguration;
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

import static com.google.common.base.Preconditions.checkArgument;
import static no.statkart.skif.util.HibernateHelper.*;

/**
 * Implementasjon av {@link no.statkart.skif.storetest.service.endringslogg.EndringsloggService}.
 *
 * @author Tor Egil R. Strand
 * @author Henrik Fredholm
 * @since 2.2.0
 */
public class EndringsloggServiceImpl implements EndringsloggService {
    private static int LIMIT = 1000;
    @Inject
    private EndringFinder endringFinder;

    @Inject
    Provider<SnapshotVersion> snapshotVersionProvider;

    @Inject
    private Provider<SessionSelector> sessionSelectorProvider;

    @Inject
    EndringManagerConfiguration endringManagerConfiguration;

    @Inject
    Store store;

    @Nullable
    @Override
    public <I extends AbstractEndringId<?>> I findSisteEndringId() {
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            return findSisteEndringId(session);
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }

    private <I extends AbstractEndringId<?>> I findSisteEndringId(Session session) {
        Criteria criteria = session.createCriteria(Endring.class);
        criteria.setProjection(Projections.max("id"));
        I endringId = (I) criteria.uniqueResult();
        return endringId;
    }

    public <E extends Endringer<?>> E findEndringer(@Nullable EndringId<?> id, Class<? extends BubbleObject> bobleklasse, @Nullable String filter, ReturnerBobler returnerBobler, int maksAntall) {
        Endringer endringer = new Endringer();

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
                return (E) endringer;
            } else if (returnerBobler == ReturnerBobler.Aldri) {
                // Finn endringer, objekter skal ikke returneres
                Criteria criteria = session.createCriteria(endringClass);
                if (id != null) {
                    criteria.add(Restrictions.gt("id", id));
                }
                //criteria.addOrder(Order.asc("id")); // trengs ikke da Endring er definert som organization index tabell
                criteria.setMaxResults(maksAntall);
                List<Endring<?, ?>> endringList = criteria.list();
                boolean alleEndringerFunnet = endringList.size() < maksAntall;
                endringer.setAlleEndringerFunnet(alleEndringerFunnet);
                endringer.setEndringList(endringList);
                endringer.setSisteEndringIdProsessert(alleEndringerFunnet ? sisteEndringId : endringList.get(maksAntall - 1).getId());
            } else if (returnerBobler == ReturnerBobler.Alltid) {
                int oensketAntallEndringer = Math.min(LIMIT, maksAntall);
                EndringId<?> fromId = id;
                List<Endring<?, ?>> accumulatedEndringer = null;
                LinkedHashSet<BubbleId<?>> accumulatedEndretIds = Sets.newLinkedHashSet();
                while (oensketAntallEndringer > 0 && !endringer.isAlleEndringerFunnet()) {
                    // Finn endringer
                    Criteria criteria = session.createCriteria(endringClass);
                    if (fromId != null) {
                        criteria.add(Restrictions.gt("id", fromId));
                    }
                    //criteria.addOrder(Order.asc("id")); // trengs ikke da Endring er definert som organization index tabell
                    criteria.setMaxResults(oensketAntallEndringer);

                    List<Endring<?, ?>> endringList = criteria.list();
                    if (accumulatedEndringer == null) {
                        accumulatedEndringer = endringList;
                    } else {
                        accumulatedEndringer.addAll(endringList);
                    }

                    // Hent objekter
                    List<BubbleId<?>> endretIds = Lists.newArrayListWithCapacity(endringList.size());
                    for (Endring<?, ?> endring : endringList) {
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
        return (E) endringer;
    }


    @Override
    public <T extends BubbleObject> Kontroll calcEndringskontroll(@Nullable EndringId<?> id, Class<T> bobleklasse, @Nullable String filter, int antall) {
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
    public <T extends StoreTestBubble> Kontroll calcObjektkontrollForList(Collection<? extends BubbleId<?>> ids, Class<T> bobleklasse) {
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
