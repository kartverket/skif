package no.statkart.skif.storetest.domain.relation.uni.direct;

import com.google.inject.Inject;
import jakarta.inject.Provider;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.persistence.hibernate.type.OracleArrayUserType;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;
import no.statkart.skif.store.persistence.OracleArrayConverter;
import no.statkart.skif.store.persistence.OracleArrayLongBubbleIdConverter;
import no.statkart.skif.store.persistence.OracleArrayStringConverter;
import no.statkart.skif.store.persistence.SessionSelector;
import org.hibernate.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptyMap;

/**
 * @author Henrik Fredholm
 */
public class X1AAFinderServiceImpl implements X1AAFinderService {
    @Inject
    private Provider<SessionSelector> sessionSelectorProvider;

    @Override
    public Map<X1BBOneId<?>, Set<X1AAId<?>>> findInvSomeBBIds(Collection<? extends X1BBOneId<?>> x1BBOneIds) {
        if (x1BBOneIds.isEmpty()) return emptyMap();

        Map<X1BBOneId<?>, Set<X1AAId<?>>> result = SkifUtil.newHashMapWithEmptySetValues(x1BBOneIds);

        SnapshotVersion snapshotVersion = x1BBOneIds.iterator().next().getSnapshotVersion();
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersion);
            session.flush();
            return session.doReturningWork(connection -> {
                try (PreparedStatement statement = connection.prepareStatement(
                    "select someBBId, id from X1AA where someBBId in (select * from table(?))"
                )) {
                    statement.setArray(1, new OracleArrayLongBubbleIdConverter().toArray(connection, x1BBOneIds));
                    statement.setFetchSize(Math.min(1000, x1BBOneIds.size()));
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            X1BBOneId<?> key = new X1BBOneId<>(resultSet.getLong(1), snapshotVersion);
                            Set<X1AAId<?>> relatedIds = result.get(key);
                            relatedIds.add(new X1AAId<>(resultSet.getLong(2), snapshotVersion));
                        }
                    }
                }
                return result;
            });
        }
    }

    @Override
    public Map<X1CCManyId<?>, X1AAId<?>> findInvSomeCCsId(Collection<? extends X1CCManyId<?>> x1CCManyIds) {
        if (x1CCManyIds.isEmpty()) return emptyMap();

        Map<X1CCManyId<?>, X1AAId<?>> result = SkifUtil.newHashMapWithNullValues(x1CCManyIds);

        SnapshotVersion snapshotVersion = x1CCManyIds.iterator().next().getSnapshotVersion();
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersion);
            session.flush();
            return session.doReturningWork(connection -> {
                try (PreparedStatement statement = connection.prepareStatement(
                    "select childId as id, ownerId from X1AAForX1CCMany where childId in (select * from table(?))"
                )) {
                    statement.setArray(1, new OracleArrayLongBubbleIdConverter().toArray(connection, x1CCManyIds));
                    statement.setFetchSize(Math.min(1000, x1CCManyIds.size()));
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            Object ownerId = resultSet.getObject(2);
                            if (ownerId != null) {
                                X1CCManyId<?> key = new X1CCManyId<>(resultSet.getLong(1), snapshotVersion);
                                result.put(key, new X1AAId<>(resultSet.getLong(2), snapshotVersion));
                            }
                        }
                    }
                }
                return result;
            });
        }
    }

    @Override
    public Map<String, X1AAId<?>> findX1AAIdsForUniqueOnX1AA(Collection<String> textValues) {
        if (textValues.isEmpty()) return emptyMap();

        Map<String, X1AAId<?>> result = SkifUtil.newHashMapWithNullValues(textValues);

        SnapshotVersion snapshotVersion = SnapshotVersion.CURRENT;
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersion);
            session.flush();
            return session.doReturningWork(connection -> {
                try (PreparedStatement statement = connection.prepareStatement(
                    "select uniqueOnX1AA, id from X1AA where uniqueOnX1AA in (select * from table(?))"
                )) {
                    statement.setArray(1, new OracleArrayStringConverter().toArray(connection, textValues));
                    statement.setFetchSize(Math.min(1000, textValues.size()));
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            Object idValue = resultSet.getObject(2);
                            if (idValue != null) {
                                String key = resultSet.getString(1);
                                result.put(key, new X1AAId<>(resultSet.getLong(2), snapshotVersion));
                            }
                        }
                    }
                }
                return result;
            });
        }
    }

    public Map<String, Set<X1AAId<?>>> findX1AAIdsForNonUniqueOnX1AA(Collection<String> textValues) {
        if (textValues.isEmpty()) return emptyMap();

        Map<String, Set<X1AAId<?>>> result = SkifUtil.newHashMapWithEmptySetValues(textValues);

        SnapshotVersion snapshotVersion = SnapshotVersion.CURRENT;
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersion);
            session.flush();
            return session.doReturningWork(connection -> {
                try (PreparedStatement statement = connection.prepareStatement(
                    "select nonUniqueOnX1AA, id from X1AA where nonUniqueOnX1AA in (select * from table(?))"
                )) {
                    statement.setArray(1, new OracleArrayStringConverter().toArray(connection, textValues));
                    statement.setFetchSize(Math.min(1000, textValues.size()));
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            Object idValue = resultSet.getObject(2);
                            if (idValue != null) {
                                String key = resultSet.getString(1);
                                Set<X1AAId<?>> relatedIds = result.get(key);
                                relatedIds.add(new X1AAId<>(resultSet.getLong(2), snapshotVersion));
                            }
                        }
                    }
                }
                return result;
            });
        }
    }

    @Override
    public Map<X1AAIdent, Set<X1AAId<?>>> findX1AAIdsForIdents(Collection<X1AAIdent> idents) {
        Map<X1AAIdent, Set<X1AAId<?>>> result = SkifUtil.newHashMapWithEmptySetValues(idents);

        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            SnapshotVersion snapshotVersion = SnapshotVersionContext.getInstance().getSnapshotVersion();
            Session session = sessionSelector.get(snapshotVersion);
            session.flush();
            return session.doReturningWork(connection -> {
                try (PreparedStatement statement = connection.prepareStatement(
                    "select b.nr as bnr, a.nr as anr, a.id" +
                        " from X1AA a join X1BBOne b on (a.someBBId=b.id)" +
                        " where (b.nr, a.nr) in (select * from table(?))"
                )) {
                    statement.setArray(1, new OracleX1AAIdentArrayConverter().toArray(connection, idents));
                    statement.setFetchSize(Math.min(1000, idents.size()));
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            X1AAIdent ident = new X1AAIdent(resultSet.getInt(1), resultSet.getInt(2));
                            Set<X1AAId<?>> relatedIds = result.get(ident);
                            relatedIds.add(new X1AAId<>(resultSet.getLong(3), snapshotVersion));
                        }
                    }
                }
                return result;
            });
        }
    }

    private static class OracleX1AAIdentArrayConverter extends OracleArrayConverter<X1AAIdent> {
        public OracleX1AAIdentArrayConverter() {
            super("NUMBER_NUMBER_LIST_TYPE");
        }

        @Override
        protected Object toValue(X1AAIdent object) {
            return new Object[]{object.getBNr(), object.getANr()};
        }
    }

    private static class OracleX1AAIdentArrayUserType extends OracleArrayUserType<OracleX1AAIdentArrayConverter, X1AAIdent> {
        public OracleX1AAIdentArrayUserType() {
            super(new OracleX1AAIdentArrayConverter());
        }
    }


    private static class OracleX1BBOneIdentArrayConverter extends OracleArrayConverter<X1BBOneIdent> {
        public OracleX1BBOneIdentArrayConverter() {
            super("NUMBER_LIST_TYPE");
        }

        @Override
        protected Object toValue(X1BBOneIdent object) {
            return object.getBNr();
        }
    }

    private static class OracleX1BBOneIdentArrayUserType extends OracleArrayUserType<OracleX1BBOneIdentArrayConverter, X1BBOneIdent> {
        public OracleX1BBOneIdentArrayUserType() {
            super(new OracleX1BBOneIdentArrayConverter());
        }
    }

    @Override
    public Map<X1BBOneIdent, Set<X1BBOneId<?>>> findX1BBOneIdsForIdents(Collection<X1BBOneIdent> idents) {
        Map<X1BBOneIdent, Set<X1BBOneId<?>>> result = SkifUtil.newHashMapWithEmptySetValues(idents);

        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            SnapshotVersion snapshotVersion = SnapshotVersionContext.getInstance().getSnapshotVersion();
            Session session = sessionSelector.get(snapshotVersion);
            return session.doReturningWork(connection -> {
                try (PreparedStatement statement = connection.prepareStatement(
                    "select b.nr as bnr, b.id from X1BBOne b where (b.nr) in (select * from table(?))"
                )) {
                    statement.setArray(1, new OracleX1BBOneIdentArrayConverter().toArray(connection, idents));
                    statement.setFetchSize(Math.min(1000, idents.size()));
                    try (ResultSet resultSet = statement.executeQuery()) {
                        while (resultSet.next()) {
                            X1BBOneIdent ident = new X1BBOneIdent(resultSet.getInt(1));
                            Set<X1BBOneId<?>> relatedIds = result.get(ident);
                            relatedIds.add(new X1BBOneId<>(resultSet.getLong(2), snapshotVersion));
                        }
                    }
                }
                return result;
            });
        }
    }

}
