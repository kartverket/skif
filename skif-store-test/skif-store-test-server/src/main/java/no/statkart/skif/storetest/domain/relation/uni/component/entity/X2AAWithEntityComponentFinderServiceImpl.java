package no.statkart.skif.storetest.domain.relation.uni.component.entity;

import com.google.inject.Inject;
import jakarta.inject.Provider;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.OracleArrayLongBubbleIdConverter;
import no.statkart.skif.store.persistence.SessionSelector;
import org.hibernate.Session;

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
            Session session = sessionSelector.get(snapshotVersion);
            return session.doReturningWork(connection -> {
                try (java.sql.PreparedStatement statement = connection.prepareStatement(
                    "select someBBId, ownerId from X2EntityComponentOne where someBBId in (select * from table(?))"
                )) {
                    statement.setArray(1, new OracleArrayLongBubbleIdConverter().toArray(connection, ids));
                    statement.setFetchSize(Math.min(1000, ids.size()));
                    try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            X2BBOneId<?> key = new X2BBOneId<>(resultSet.getLong(1), snapshotVersion);
                            Set<X2AAWithEntityComponentId<?>> relatedIds = result.get(key);
                            relatedIds.add(new X2AAWithEntityComponentId<>(resultSet.getLong(2), snapshotVersion));
                        }
                    }
                }
                return result;
            });
        }
    }

    @Override
    public Map<X2CCManyId<?>, X2AAWithEntityComponentId<?>> findInvSomeCCsId(Collection<? extends X2CCManyId<?>> ids) {
        if (ids.isEmpty()) return emptyMap();

        Map<X2CCManyId<?>, X2AAWithEntityComponentId<?>> result = SkifUtil.newHashMapWithNullValues(ids);

        SnapshotVersion snapshotVersion = ids.iterator().next().getSnapshotVersion();
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersion);
            return session.doReturningWork(connection -> {
                try (java.sql.PreparedStatement statement = connection.prepareStatement(
                    "select t.childId as id, c.ownerId from X2AAForX2CCMany t, X2EntityComponentOne c" +
                        " where t.ownerId = c.id and t.childId in (select * from table(?))"
                )) {
                    statement.setArray(1, new OracleArrayLongBubbleIdConverter().toArray(connection, ids));
                    statement.setFetchSize(Math.min(1000, ids.size()));
                    try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            Object ownerId = resultSet.getObject(2);
                            if (ownerId != null) {
                                X2CCManyId<?> key = new X2CCManyId<>(resultSet.getLong(1), snapshotVersion);
                                result.put(key, new X2AAWithEntityComponentId<>(resultSet.getLong(2), snapshotVersion));
                            }
                        }
                    }
                }
                return result;
            });
        }
    }

    @Override
    public Map<X2BBOneId<?>, Set<X2AAWithEntityComponentId<?>>> findInvRole1BBIds(Collection<? extends X2BBOneId<?>> ids) {
        Map<X2BBOneId<?>, Set<X2AAWithEntityComponentId<?>>> result = SkifUtil.newHashMapWithEmptySetValues(ids);

        SnapshotVersion snapshotVersion = ids.iterator().next().getSnapshotVersion();
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersion);
            return session.doReturningWork(connection -> {
                try (java.sql.PreparedStatement statement = connection.prepareStatement(
                    "select role1BBOneId, ownerId from X2SetEntityComp where role1BBOneId in (select * from table(?))"
                )) {
                    statement.setArray(1, new OracleArrayLongBubbleIdConverter().toArray(connection, ids));
                    statement.setFetchSize(Math.min(1000, ids.size()));
                    try (java.sql.ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            X2BBOneId<?> key = new X2BBOneId<>(resultSet.getLong(1), snapshotVersion);
                            Set<X2AAWithEntityComponentId<?>> relatedIds = result.get(key);
                            relatedIds.add(new X2AAWithEntityComponentId<>(resultSet.getLong(2), snapshotVersion));
                        }
                    }
                }
                return result;
            });
        }
    }
}
