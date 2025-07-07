package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import com.google.inject.Inject;
import jakarta.inject.Provider;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.SessionSelector;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.StandardBasicTypes;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyMap;

/**
 * @author Henrik Fredholm
 */
public class X2AAWithEntityComponentFinderServiceImpl implements X2AAWithEntityComponentFinderService {
    @Inject
    private Provider<SessionSelector> sessionSelectorProvider;

    @Override
    public Map<X2BBOneId<?>, Set<X2AAWithEntityComponentId<?>>> findInvSomeBBIds(Collection<? extends X2BBOneId<?>> ids) {
        if (ids.isEmpty()) return emptyMap();

        Map<X2BBOneId<?>, Set<X2AAWithEntityComponentId<?>>> result = SkifUtil.newHashMapWithEmptySetValues(ids);


        SnapshotVersion snapshotVersion = ids.iterator().next().getSnapshotVersion();
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            NativeQuery<?> sqlQuery = sessionSelector.get(snapshotVersion)
                .createNativeQuery("select someBBId, ownerId  from X2EntityComponentOne  where someBBId in (select * from table(:idValues))")
                .addSynchronizedQuerySpace("X2EntityComponentOne")
                .setParameter("idValues", ids, new OracleLongBubbleIdArrayCustomType())
                .setFetchSize(Math.min(1000, ids.size()))
                .addScalar("someBBId", StandardBasicTypes.LONG)
                .addScalar("ownerId", StandardBasicTypes.LONG);
            try (ScrollableResults scroll = sqlQuery.scroll(ScrollMode.FORWARD_ONLY)) {
                while (scroll.next()) {
                    Object[] next = scroll.get();
                    X2BBOneId<?> key = new X2BBOneId<>((Long) next[0], snapshotVersion);
                    Set<X2AAWithEntityComponentId<?>> relatedIds = result.get(key);
                    relatedIds.add(new X2AAWithEntityComponentId<>((Long) next[1], snapshotVersion));
                }
            }
        }
        return result;
    }

    @Override
    public Map<X2CCManyId<?>, X2AAWithEntityComponentId<?>> findInvSomeCCsId(Collection<? extends X2CCManyId<?>> ids) {
        if (ids.isEmpty()) return emptyMap();

        Map<X2CCManyId<?>, X2AAWithEntityComponentId<?>> result = SkifUtil.newHashMapWithNullValues(ids);

        SnapshotVersion snapshotVersion = ids.iterator().next().getSnapshotVersion();
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            NativeQuery<?> sqlQuery = sessionSelector.get(snapshotVersion)
                .createNativeQuery("select t.id, c.ownerId  from X2CCMany t, X2EntityComponentOne c  where t.ownerId = c.id and  t.id in (select * from table(:idValues))")
                .addSynchronizedQuerySpace("X2CCMany")
                .addSynchronizedQuerySpace("X2EntityComponentOne")
                .setParameter("idValues", ids, new OracleLongBubbleIdArrayCustomType())
                .setFetchSize(Math.min(1000, ids.size()))
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("ownerId", StandardBasicTypes.LONG);

            try (ScrollableResults scroll = sqlQuery.scroll(ScrollMode.FORWARD_ONLY)) {
                while (scroll.next()) {
                    Object[] next = scroll.get();
                    if (next[1] != null) {
                        X2CCManyId<?> key = new X2CCManyId<>((Long) next[0], snapshotVersion);
                        result.put(key, new X2AAWithEntityComponentId<>((Long) next[1], snapshotVersion));
                    }
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
            NativeQuery<?> sqlQuery = sessionSelector.get(snapshotVersion)
                .createNativeQuery("select role1BBOneId, ownerId  from X2SetEntityComp  where role1BBOneId in (select * from table(:idValues))")
                .addSynchronizedQuerySpace("X2SetEntityComp")
                .setParameter("idValues", ids, new OracleLongBubbleIdArrayCustomType())
                .setFetchSize(Math.min(1000, ids.size()))
                .addScalar("role1BBOneId", StandardBasicTypes.LONG)
                .addScalar("ownerId", StandardBasicTypes.LONG);

            try (ScrollableResults scroll = sqlQuery.scroll(ScrollMode.FORWARD_ONLY)) {
                while (scroll.next()) {
                    Object[] next = scroll.get();
                    X2BBOneId<?> key = new X2BBOneId<>((Long) next[0], snapshotVersion);
                    Set<X2AAWithEntityComponentId<?>> relatedIds = result.get(key);
                    relatedIds.add(new X2AAWithEntityComponentId<>((Long) next[1], snapshotVersion));
                }
            }
        }
        return result;
    }
}
