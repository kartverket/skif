package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.SessionSelector;
import org.hibernate.*;
import org.hibernate.type.StandardBasicTypes;

import javax.inject.Provider;
import java.util.Collection;
import java.util.HashSet;
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
            result.put(id, new HashSet<X2AAWithEntityComponentId<?>>());
        }


        SnapshotVersion snapshotVersion = ids.iterator().next().getSnapshotVersion();
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersion);
            SQLQuery query = session.createSQLQuery("select someBBId, ownerId  from X2EntityComponentOne  where someBBId in (select * from table(:idValues))");
            query.addSynchronizedQuerySpace("X2EntityComponentOne");
            query.setParameter("idValues", ids, new OracleLongBubbleIdArrayCustomType());
            query.setFetchSize(Math.min(1000, ids.size()));
            query.addScalar("someBBId", StandardBasicTypes.LONG);
            query.addScalar("ownerId", StandardBasicTypes.LONG);
            ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);

            while (scroll.next()) {
                Object[] next = scroll.get();
                X2BBOneId<?> key = new X2BBOneId<>((Long) next[0], snapshotVersion);
                Set<X2AAWithEntityComponentId<?>> relatedIds = result.get(key);
                relatedIds.add(new X2AAWithEntityComponentId<>((Long) next[1], snapshotVersion));
            }
        }
        return result;
    }

    @Override
    public Map<X2CCManyId<?>, X2AAWithEntityComponentId<?>> findInvSomeCCsId(Collection<? extends X2CCManyId<?>> ids) {
        Map<X2CCManyId<?>, X2AAWithEntityComponentId<?>> result = Maps.newHashMapWithExpectedSize(ids.size());
        if (ids.isEmpty()) return result;

        for (X2CCManyId<?> id : ids) {
            result.put(id, null);
        }


        SnapshotVersion snapshotVersion = ids.iterator().next().getSnapshotVersion();
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersion);
            SQLQuery query = session.createSQLQuery("select t.id, c.ownerId  from X2CCMany t, X2EntityComponentOne c  where t.ownerId = c.id and  t.id in (select * from table(:idValues))");
            query.addSynchronizedQuerySpace("X2CCMany");
            query.addSynchronizedQuerySpace("X2EntityComponentOne");
            query.setParameter("idValues", ids, new OracleLongBubbleIdArrayCustomType());
            query.setFetchSize(Math.min(1000, ids.size()));
            query.addScalar("id", StandardBasicTypes.LONG);
            query.addScalar("ownerId", StandardBasicTypes.LONG);
            ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);

            while (scroll.next()) {
                Object[] next = scroll.get();
                if (next[1] != null) {
                    X2CCManyId<?> key = new X2CCManyId<>((Long) next[0], snapshotVersion);
                    result.put(key, new X2AAWithEntityComponentId<>((Long) next[1], snapshotVersion));
                }
            }
        }
        return result;
    }

    @Override
    public Map<X2BBOneId<?>, Set<X2AAWithEntityComponentId<?>>> findInvRole1BBIds(Collection<? extends X2BBOneId<?>> ids) {
        Map<X2BBOneId<?>, Set<X2AAWithEntityComponentId<?>>> result = SkifUtil.newHashMapWithEmptySetValues(ids);

        SnapshotVersion snapshotVersion = ids.iterator().next().getSnapshotVersion();
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersion);
            SQLQuery query = session.createSQLQuery("select role1BBOneId, ownerId  from X2SetEntityComp  where role1BBOneId in (select * from table(:idValues))");
            query.addSynchronizedQuerySpace("X2SetEntityComp");
            query.setParameter("idValues", ids, new OracleLongBubbleIdArrayCustomType());
            query.setFetchSize(Math.min(1000, ids.size()));
            query.addScalar("role1BBOneId", StandardBasicTypes.LONG);
            query.addScalar("ownerId", StandardBasicTypes.LONG);
            ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);

            while (scroll.next()) {
                Object[] next = scroll.get();
                X2BBOneId<?> key = new X2BBOneId<>((Long) next[0], snapshotVersion);
                Set<X2AAWithEntityComponentId<?>> relatedIds = result.get(key);
                relatedIds.add(new X2AAWithEntityComponentId<>((Long) next[1], snapshotVersion));
            }
        }
        return result;

    }
}
