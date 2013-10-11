package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.SessionSelector;
import no.statkart.skif.storetest.domain.relation.uni.direct.*;
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
public class X2AAWithEntityComponentFinderServiceImpl implements X2AAWithEntityComponentFinderService {
    @Inject
    private Provider<SessionSelector> sessionSelectorProvider;

    @Override
    public Map<X2BBOneId<?>, Set<X2AAWithEntityComponentId<?>>> findInvSomeBBIds(Collection<? extends X2BBOneId<?>> ids) {
        Map<X2BBOneId<?>, Set<X2AAWithEntityComponentId<?>>> result = Maps.newHashMapWithExpectedSize(ids.size());
        if (ids.isEmpty()) return result;

        for (X2BBOneId<?> id : ids) {
            result.put(id, Sets.<X2AAWithEntityComponentId<?>>newHashSet());
        }


        SnapshotVersion snapshotVersion = ids.iterator().next().getSnapshotVersion();
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        PreparedStatement preparedStatement = null;
        try {
            Session session = sessionSelector.get(snapshotVersion);
            SQLQuery query = session.createSQLQuery("select someBBId, ownerId  from X2EntityComponentOne  where someBBId in (select * from table(:idValues))");
            query.setParameter("idValues", ids, new OracleLongBubbleIdArrayCustomType());
            query.setFetchSize(Math.min(1000, ids.size()));
            query.addScalar("someBBId", Hibernate.LONG);
            query.addScalar("ownerId", Hibernate.LONG);
            ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);

            while (scroll.next()) {
                Object[] next = scroll.get();
                X2BBOneId<?> key = new X2BBOneId<X2BBOne>((Long) next[0], snapshotVersion);
                Set<X2AAWithEntityComponentId<?>> relatedIds = result.get(key);
                relatedIds.add(new X2AAWithEntityComponentId<X2AAWithEntityComponent>((Long) next[1], snapshotVersion));
            }
        } finally {
            HibernateHelper.close(preparedStatement, sessionSelector);
        }
        return result;
    }

    @Override
    public Map<X2CCManyId<?>, X2AAWithEntityComponentId<?>> findInvSomeCCsIds(Collection<? extends X2CCManyId<?>> ids) {
        Map<X2CCManyId<?>, X2AAWithEntityComponentId<?>> result = Maps.newHashMapWithExpectedSize(ids.size());
        if (ids.isEmpty()) return result;

        for (X2CCManyId<?> id : ids) {
            result.put(id, null);
        }


        SnapshotVersion snapshotVersion = ids.iterator().next().getSnapshotVersion();
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        PreparedStatement preparedStatement = null;
        try {
            Session session = sessionSelector.get(snapshotVersion);
            SQLQuery query = session.createSQLQuery("select t.id, c.ownerId  from X2CCMany t, X2EntityComponentOne c  where t.ownerId = c.id and  t.id in (select * from table(:idValues))");
            query.setParameter("idValues", ids, new OracleLongBubbleIdArrayCustomType());
            query.setFetchSize(Math.min(1000, ids.size()));
            query.addScalar("id", Hibernate.LONG);
            query.addScalar("ownerId", Hibernate.LONG);
            ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);

            while (scroll.next()) {
                Object[] next = scroll.get();
                if (next[1] != null) {
                    X2CCManyId<?> key = new X2CCManyId<X2CCMany>((Long) next[0], snapshotVersion);
                    result.put(key, new X2AAWithEntityComponentId<X2AAWithEntityComponent>((Long) next[1], snapshotVersion));
                }
            }
        } finally {
            HibernateHelper.close(preparedStatement, sessionSelector);
        }
        return result;
    }

}
