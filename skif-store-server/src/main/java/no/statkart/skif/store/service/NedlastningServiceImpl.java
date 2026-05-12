package no.statkart.skif.store.service;

import com.google.common.base.Preconditions;
import com.google.inject.Provider;
import jakarta.annotation.Nullable;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import no.statkart.skif.persistence.hibernate.type.OracleArrayLongBubbleIdCustomType;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Bubbles;
import no.statkart.skif.store.Kontroll;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.endringslogg.EndringManagerConfiguration;
import no.statkart.skif.store.persistence.SessionSelector;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static no.statkart.skif.util.HibernateHelper.getTableName;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 * @since 2.5.0
 */
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
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Object> cq = cb.createQuery();
            Root<T> root = cq.from(domainklasse);

            cq.select(root.get("id"));

            if (id != null) {
                Path<? extends Comparable> idPath = root.get("id");
                cq.where(cb.greaterThan(idPath, (Comparable) id));
            }
            cq.orderBy(cb.asc(root.get("id")));

            List<?> result = session.createQuery(cq)
                .setMaxResults(maksAntall)
                .getResultList();

            return (List<I>) result;
        }
    }

    @Override
    public <T extends BubbleObject> List<T> findObjekterEtterId(@Nullable BubbleId<? extends T> id, Class<T> domainklasse, @Nullable String filter, int maksAntall) {
        checkBobbleklasseGyldigForNedlasting(domainklasse);
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(domainklasse);
            Root<T> root = cq.from(domainklasse);

            cq.select(root);

            if (id != null) {
                Path<? extends Comparable> idPath = root.get("id");
                cq.where(cb.greaterThan(idPath, (Comparable) id));
            }
            cq.orderBy(cb.asc(root.get("id")));

            List<T> result = session.createQuery(cq)
                .setMaxResults(maksAntall)
                .getResultList();

            return (List) store.getOrdered(Bubbles.asIds(result));
        }
    }

    @Override
    public <T extends BubbleObject> Kontroll calcObjektkontrollForRange(@Nullable BubbleId<? extends T> fraId, @Nullable BubbleId<? extends T> tilId, Class<T> domainklasse, @Nullable String filter) {
        checkBobbleklasseGyldigForNedlasting(domainklasse);
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> root = cq.from(domainklasse);

            cq.select(cb.count(root));
            List<Predicate> predicates = new ArrayList<>();

            Path<? extends Comparable> idPath = root.get("id");
            if (fraId != null) {
                predicates.add(cb.greaterThan(idPath, (Comparable) fraId));
            }
            if (tilId != null) {
                predicates.add(cb.lessThanOrEqualTo(idPath, (Comparable) tilId));
            }

            if (!predicates.isEmpty()) {
                cq.where(predicates.toArray(new Predicate[0]));
            }

            Long count = session.createQuery(cq).getSingleResult();
            Kontroll result =  new Kontroll();
            result.setAntall(count != null ? count : 0L); // kan ikke caste direkte til Long pga forskjell på datatype her i hibernate 3.2 og 3.6
            return result;
        }
    }

    @Override
    public <I extends BubbleId<? extends T>, T extends BubbleObject> Kontroll calcObjektkontrollForList(Collection<I> ids, Class<T> domainklasse) {
        checkBobbleklasseGyldigForNedlasting(domainklasse);
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            String tableName = getTableName(session, domainklasse);

            String sql = "select count(*) from " + tableName + " where id in (select * from table(?))";

            Number count = (Number) session.createNativeQuery(sql)
                .setParameter(1, OracleArrayLongBubbleIdCustomType.wrap(ids), new OracleArrayLongBubbleIdCustomType())
                .getSingleResult();
            Kontroll result = new Kontroll();
            result.setAntall(count.longValue());
            return result;
        }
    }

    private <T extends BubbleObject> void checkBobbleklasseGyldigForNedlasting(Class<T> domainklasse) {
        Class<?> endringsklasse = endringManagerConfiguration.getEndringsklasse(domainklasse);
        Preconditions.checkArgument(!domainklasse.isInterface() && endringsklasse != null, "Domainklasse %s kan ikke brukes som filter for nedlastning", domainklasse.getSimpleName());
    }
}
