package no.statkart.skif.storetest.domain.relation.uni.direct;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.SessionSelector;
import no.statkart.skif.util.HibernateHelper;
import org.hibernate.*;

import javax.inject.Provider;
import java.sql.PreparedStatement;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author Henrik Fredholm
 */
@SuppressWarnings("UnusedDeclaration")   // Reflection
public class X1AAFinderServiceImpl implements X1AAFinderService {
    @Inject
    private Provider<SessionSelector> sessionSelectorProvider;

    @Override
    public Map<X1BBOneId<?>, Set<X1AAId<?>>> findInvSomeBBIds(Collection<? extends X1BBOneId<?>> x1BBOneIds) {
        Map<X1BBOneId<?>, Set<X1AAId<?>>> result = Maps.newHashMapWithExpectedSize(x1BBOneIds.size());
        if (x1BBOneIds.isEmpty()) return result;

        for (X1BBOneId<?> id : x1BBOneIds) {
            result.put(id, Sets.<X1AAId<?>>newHashSet());
        }


        SnapshotVersion snapshotVersion = x1BBOneIds.iterator().next().getSnapshotVersion();
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        PreparedStatement preparedStatement = null;
        try {
            Session session = sessionSelector.get(snapshotVersion);
            SQLQuery query = session.createSQLQuery("select someBBId, id  from X1AA  where someBBId in (select * from table(:idValues))");
            query.setParameter("idValues", x1BBOneIds, new OracleLongBubbleIdArrayCustomType());
            query.setFetchSize(Math.min(1000, x1BBOneIds.size()));
            query.addScalar("someBBId", Hibernate.LONG);
            query.addScalar("id", Hibernate.LONG);
            ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);

            while (scroll.next()) {
                Object[] next = scroll.get();
                X1BBOneId<?> key = new X1BBOneId<X1BBOne>((Long) next[0], snapshotVersion);
                Set<X1AAId<?>> relatedIds = result.get(key);
                relatedIds.add(new X1AAId<X1AA>((Long) next[1], snapshotVersion));
            }
        } finally {
            HibernateHelper.close(preparedStatement, sessionSelector);
        }
        return result;
    }

//    @Override
    public Map<X1CCManyId<?>, Set<X1AAId<?>>> findInvSomeCCsIds_NotUsed(Collection<? extends X1CCManyId<?>> x1CCManyIds) {
        Map<X1CCManyId<?>, Set<X1AAId<?>>> result = Maps.newHashMapWithExpectedSize(x1CCManyIds.size());
        if (x1CCManyIds.isEmpty()) return result;

        for (X1CCManyId<?> id : x1CCManyIds) {
            result.put(id, Sets.<X1AAId<?>>newHashSet());
        }


        SnapshotVersion snapshotVersion = x1CCManyIds.iterator().next().getSnapshotVersion();
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        PreparedStatement preparedStatement = null;
        try {
            Session session = sessionSelector.get(snapshotVersion);
            SQLQuery query = session.createSQLQuery("select id, ownerId  from X1CCMany  where id in (select * from table(:idValues))");
            query.setParameter("idValues", x1CCManyIds, new OracleLongBubbleIdArrayCustomType());
            query.setFetchSize(Math.min(1000, x1CCManyIds.size()));
            query.addScalar("id", Hibernate.LONG);
            query.addScalar("ownerId", Hibernate.LONG);
            ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);

            while (scroll.next()) {
                Object[] next = scroll.get();
                if (next[1] != null) {
                    X1CCManyId<?> key = new X1CCManyId<X1CCMany>((Long) next[0], snapshotVersion);
                    Set<X1AAId<?>> relatedIds = result.get(key);
                    relatedIds.add(new X1AAId<X1AA>((Long) next[1], snapshotVersion));
                }
            }
        } finally {
            HibernateHelper.close(preparedStatement, sessionSelector);
        }
        return result;
    }

    @Override
    public Map<X1CCManyId<?>, X1AAId<?>> findInvSomeCCsId(Collection<? extends X1CCManyId<?>> x1CCManyIds) {
        Map<X1CCManyId<?>, X1AAId<?>> result = Maps.newHashMapWithExpectedSize(x1CCManyIds.size());
        if (x1CCManyIds.isEmpty()) return result;

        for (X1CCManyId<?> id : x1CCManyIds) {
            result.put(id, null);
        }


        SnapshotVersion snapshotVersion = x1CCManyIds.iterator().next().getSnapshotVersion();
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        PreparedStatement preparedStatement = null;
        try {
            Session session = sessionSelector.get(snapshotVersion);
            SQLQuery query = session.createSQLQuery("select id, ownerId  from X1CCMany  where id in (select * from table(:idValues))");
            query.setParameter("idValues", x1CCManyIds, new OracleLongBubbleIdArrayCustomType());
            query.setFetchSize(Math.min(1000, x1CCManyIds.size()));
            query.addScalar("id", Hibernate.LONG);
            query.addScalar("ownerId", Hibernate.LONG);
            ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);

            while (scroll.next()) {
                Object[] next = scroll.get();
                if (next[1] != null) {
                    X1CCManyId<?> key = new X1CCManyId<X1CCMany>((Long) next[0], snapshotVersion);
                    result.put(key, new X1AAId<X1AA>((Long) next[1], snapshotVersion));
                }
            }
        } finally {
            HibernateHelper.close(preparedStatement, sessionSelector);
        }
        return result;
    }

}
