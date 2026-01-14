package no.statkart.skif.store.service;

import com.google.common.base.Preconditions;
import com.google.inject.Provider;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import no.statkart.skif.store.persistence.OracleArrayLongBubbleIdConverter;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.Bubbles;
import no.statkart.skif.store.Kontroll;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.endringslogg.EndringManagerConfiguration;
import no.statkart.skif.store.persistence.SessionSelector;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static no.statkart.skif.util.HibernateHelper.getClassMetadata;
import static no.statkart.skif.util.HibernateHelper.getDiscriminatorSql;
import static no.statkart.skif.util.HibernateHelper.getTableName;

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
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<BubbleId> cq = cb.createQuery(BubbleId.class);
            Root<T> root = cq.from(domainklasse);
            Path<BubbleId> idPath = root.get("id");
            cq.select(idPath);
            if (id != null) {
                cq.where(cb.greaterThan(idPath, id));
            }
            cq.orderBy(cb.asc(idPath));
            @SuppressWarnings("unchecked")
            List<I> results = (List<I>) (List<?>) session.createQuery(cq).setMaxResults(maksAntall).getResultList();
            return results;
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
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<T> cq = cb.createQuery(domainklasse);
            Root<T> root = cq.from(domainklasse);
            Path<BubbleId<?>> idPath = root.get("id");
            if (id != null) {
                cq.where(cb.greaterThan(idPath, id));
            }
            cq.orderBy(cb.asc(idPath));
            return (List) store.getOrdered(Bubbles.asIds(session.createQuery(cq).setMaxResults(maksAntall).getResultList()));
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
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<T> root = cq.from(domainklasse);
            Path<BubbleId<?>> idPath = root.get("id");
            cq.select(cb.count(root));
            if (fraId != null || tilId != null) {
                if (fraId != null && tilId != null) {
                    cq.where(cb.and(
                            cb.greaterThan(idPath, fraId),
                            cb.lessThanOrEqualTo(idPath, tilId)
                    ));
                } else if (fraId != null) {
                    cq.where(cb.greaterThan(idPath, fraId));
                } else {
                    cq.where(cb.lessThanOrEqualTo(idPath, tilId));
                }
            }
            Kontroll result =  new Kontroll();
            result.setAntall(session.createQuery(cq).uniqueResult()); // kan ikke caste direkte til Long pga forskjell på datatype her i hibernate 3.2 og 3.6
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
            ClassMetadata classMetadata = getClassMetadata(session, domainklasse);
            String tableName = getTableName(classMetadata);
            String discriminatorSql = getDiscriminatorSql(classMetadata, "t");
            String sql = "select count(t.id) from " + tableName + " t where t.id in (select * from table(:ids))" + discriminatorSql;

            Kontroll result = new Kontroll();
            if (ids.isEmpty()) {
                result.setAntall(0L);
                return result;
            }
            return session.doReturningWork(connection -> {
                try (java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
                    statement.setArray(1, new OracleArrayLongBubbleIdConverter().toArray(connection, ids));
                    try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            result.setAntall(resultSet.getLong(1));
                        } else {
                            result.setAntall(0L);
                        }
                    }
                }
                return result;
            });
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }

    private <T extends BubbleObject> void checkBobbleklasseGyldigForNedlasting(Class<T> domainklasse) {
        Class<?> endringsklasse = endringManagerConfiguration.getEndringsklasse(domainklasse);
        Preconditions.checkArgument(!domainklasse.isInterface() && endringsklasse != null, "Domainklasse %s kan ikke brukes som filter for nedlastning", domainklasse.getSimpleName());
    }
}
