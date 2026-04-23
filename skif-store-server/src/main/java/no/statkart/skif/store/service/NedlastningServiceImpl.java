package no.statkart.skif.store.service;

import com.google.common.base.Preconditions;
import com.google.inject.Provider;
import no.statkart.skif.persistence.hibernate.type.OracleArrayLongBubbleIdCustomType;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Bubbles;
import no.statkart.skif.store.Kontroll;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.endringslogg.EndringManagerConfiguration;
import no.statkart.skif.store.persistence.SessionSelector;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import jakarta.annotation.Nullable;
import java.util.Collection;
import java.util.List;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.5.0
 */
// OBS! Det er en kopi av denne i hs3.2
public abstract class NedlastningServiceImpl implements NedlastningService {
    protected final Provider<SnapshotVersion> snapshotVersionProvider;

    protected final Provider<SessionSelector> sessionSelectorProvider;

    protected final EndringManagerConfiguration<?> endringManagerConfiguration;

    protected final Store store;

    protected NedlastningServiceImpl(Provider<SnapshotVersion> snapshotVersionProvider, Provider<SessionSelector> sessionSelectorProvider, EndringManagerConfiguration<?> endringManagerConfiguration, Store store) {
        this.snapshotVersionProvider = snapshotVersionProvider;
        this.sessionSelectorProvider = sessionSelectorProvider;
        this.endringManagerConfiguration = endringManagerConfiguration;
        this.store = store;
    }

    @Override
    public <I extends BubbleId<? extends T>, T extends BubbleObject> List<I> findIdsEtterId(@Nullable BubbleId<? extends T> id, Class<T> domainklasse, @Nullable String filter, int maksAntall) {
        checkBobbleklasseGyldigForNedlasting(domainklasse);
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            Criteria criteria = session.createCriteria(domainklasse);
            criteria.setMaxResults(maksAntall);
            criteria.setProjection(Projections.id());
            if (id != null) {
                criteria.add(Restrictions.gt("id", id));
            }
            criteria.addOrder(Order.asc("id"));
            return (List<I>) criteria.list();
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }

    @Override
    public <T extends BubbleObject> List<T> findObjekterEtterId(@Nullable BubbleId<? extends T> id, Class<T> domainklasse, @Nullable String filter, int maksAntall) {
        checkBobbleklasseGyldigForNedlasting(domainklasse);
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            Criteria criteria = session.createCriteria(domainklasse);
            criteria.setMaxResults(maksAntall);
            if (id != null) {
                criteria.add(Restrictions.gt("id", id));
            }
            criteria.addOrder(Order.asc("id"));
            return (List) store.getOrdered(Bubbles.asIds(criteria.list()));
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }

    @Override
    public <T extends BubbleObject> Kontroll calcObjektkontrollForRange(@Nullable BubbleId<? extends T> fraId, @Nullable BubbleId<? extends T> tilId, Class<T> domainklasse, @Nullable String filter) {
        checkBobbleklasseGyldigForNedlasting(domainklasse);
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            Criteria criteria = session.createCriteria(domainklasse);
            criteria.setProjection(Projections.rowCount());
            if (fraId != null) {
                criteria.add(Restrictions.gt("id", fraId));
            }
            if (tilId != null) {
                criteria.add(Restrictions.le("id", tilId));
            }
            Kontroll result =  new Kontroll();
            result.setAntall(((Number)criteria.uniqueResult()).longValue()); // kan ikke caste direkte til Long pga forskjell på datatype her i hibernate 3.2 og 3.6
            return result;
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }

    @Override
    public <I extends BubbleId<? extends T>, T extends BubbleObject> Kontroll calcObjektkontrollForList(Collection<I> ids, Class<T> domainklasse) {
        checkBobbleklasseGyldigForNedlasting(domainklasse);
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            Criteria criteria = session.createCriteria(domainklasse);
            criteria.setProjection(Projections.rowCount());
            criteria.add(Restrictions.sqlRestriction("id in (select * from table(?))", ids, new OracleArrayLongBubbleIdCustomType()));
            Kontroll result = new Kontroll();
            result.setAntall(((Number) criteria.uniqueResult()).longValue()); // kan ikke caste direkte til Long pga forskjell på datatype her i hibernate 3.2 og 3.6
            return result;
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }

    private <T extends BubbleObject> void checkBobbleklasseGyldigForNedlasting(Class<T> domainklasse) {
        Class<?> endringsklasse = endringManagerConfiguration.getEndringsklasse(domainklasse);
        Preconditions.checkArgument(!domainklasse.isInterface() && endringsklasse != null, "Domainklasse %s kan ikke brukes som filter for nedlastning", domainklasse.getSimpleName());
    }
}
