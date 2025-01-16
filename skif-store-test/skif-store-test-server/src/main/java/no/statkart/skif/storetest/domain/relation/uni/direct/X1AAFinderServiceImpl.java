package no.statkart.skif.storetest.domain.relation.uni.direct;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import no.statkart.skif.persistence.hibernate.type.OracleArrayStringCustomType;
import no.statkart.skif.persistence.hibernate.type.OracleArrayUserType;
import no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;
import no.statkart.skif.store.persistence.OracleArrayConverter;
import no.statkart.skif.store.persistence.SessionSelector;
import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.type.CustomType;
import org.hibernate.type.StandardBasicTypes;

import jakarta.inject.Provider;
import java.util.Collection;
import java.util.HashSet;
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
            result.put(id, new HashSet<X1AAId<?>>());
        }


        SnapshotVersion snapshotVersion = x1BBOneIds.iterator().next().getSnapshotVersion();
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersion);
            SQLQuery query = session.createSQLQuery("select someBBId, id  from X1AA  where someBBId in (select * from table(:idValues))");
            query.addSynchronizedQuerySpace("X1AA");
            query.setParameter("idValues", x1BBOneIds, new OracleLongBubbleIdArrayCustomType());
            query.setFetchSize(Math.min(1000, x1BBOneIds.size()));
            query.addScalar("someBBId", StandardBasicTypes.LONG);
            query.addScalar("id", StandardBasicTypes.LONG);
            ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);

            while (scroll.next()) {
                Object[] next = scroll.get();
                X1BBOneId<?> key = new X1BBOneId<>((Long) next[0], snapshotVersion);
                Set<X1AAId<?>> relatedIds = result.get(key);
                relatedIds.add(new X1AAId<>((Long) next[1], snapshotVersion));
            }
        }
        return result;
    }

//    @Override
    public Map<X1CCManyId<?>, Set<X1AAId<?>>> findInvSomeCCsIds_NotUsed(Collection<? extends X1CCManyId<?>> x1CCManyIds) {
        Map<X1CCManyId<?>, Set<X1AAId<?>>> result = Maps.newHashMapWithExpectedSize(x1CCManyIds.size());
        if (x1CCManyIds.isEmpty()) return result;

        for (X1CCManyId<?> id : x1CCManyIds) {
            result.put(id, new HashSet<X1AAId<?>>());
        }

        SnapshotVersion snapshotVersion = x1CCManyIds.iterator().next().getSnapshotVersion();
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersion);
            SQLQuery query = session.createSQLQuery("select id, ownerId  from X1CCMany  where id in (select * from table(:idValues))");
            query.addSynchronizedQuerySpace("X1CCMany");
            query.setParameter("idValues", x1CCManyIds, new OracleLongBubbleIdArrayCustomType());
            query.setFetchSize(Math.min(1000, x1CCManyIds.size()));
            query.addScalar("id", StandardBasicTypes.LONG);
            query.addScalar("ownerId", StandardBasicTypes.LONG);
            ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);

            while (scroll.next()) {
                Object[] next = scroll.get();
                if (next[1] != null) {
                    X1CCManyId<?> key = new X1CCManyId<>((Long) next[0], snapshotVersion);
                    Set<X1AAId<?>> relatedIds = result.get(key);
                    relatedIds.add(new X1AAId<>((Long) next[1], snapshotVersion));
                }
            }
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
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersion);
            SQLQuery query = session.createSQLQuery("select id, ownerId  from X1CCMany  where id in (select * from table(:idValues))");
            query.addSynchronizedQuerySpace("X1CCMany");
            query.setParameter("idValues", x1CCManyIds, new OracleLongBubbleIdArrayCustomType());
            query.setFetchSize(Math.min(1000, x1CCManyIds.size()));
            query.addScalar("id", StandardBasicTypes.LONG);
            query.addScalar("ownerId", StandardBasicTypes.LONG);
            ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);

            while (scroll.next()) {
                Object[] next = scroll.get();
                if (next[1] != null) {
                    X1CCManyId<?> key = new X1CCManyId<>((Long) next[0], snapshotVersion);
                    result.put(key, new X1AAId<>((Long) next[1], snapshotVersion));
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, X1AAId<?>> findX1AAIdsForUniqueOnX1AA(Collection<String> textValues) {
        Map<String, X1AAId<?>> result = Maps.newHashMapWithExpectedSize(textValues.size());
        if (textValues.isEmpty()) return result;

        for (String value : textValues) {
            result.put(value, null);
        }


        SnapshotVersion snapshotVersion = SnapshotVersion.CURRENT;
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersion);
            SQLQuery query = session.createSQLQuery("select uniqueOnX1AA, id  from X1AA  where uniqueOnX1AA in (select * from table(:textValues))");
            query.addSynchronizedQuerySpace("X1AA");
            query.setParameter("textValues", textValues, new OracleArrayStringCustomType());
            query.setFetchSize(Math.min(1000, textValues.size()));
            query.addScalar("uniqueOnX1AA", StandardBasicTypes.STRING);
            query.addScalar("id", StandardBasicTypes.LONG);
            ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);

            while (scroll.next()) {
                Object[] next = scroll.get();
                if (next[1] != null) {
                    String key = (String) next[0];
                    result.put(key, new X1AAId<>((Long) next[1], snapshotVersion));
                }
            }
        }
        return result;
    }

    public Map<String, Set<X1AAId<?>>> findX1AAIdsForNonUniqueOnX1AA(Collection<String> textValues) {
        Map<String, Set<X1AAId<?>>> result = Maps.newHashMapWithExpectedSize(textValues.size());
        if (textValues.isEmpty()) return result;

        for (String values : textValues) {
            result.put(values, new HashSet<X1AAId<?>>());
        }


        SnapshotVersion snapshotVersion = SnapshotVersion.CURRENT;
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            Session session = sessionSelector.get(snapshotVersion);
            SQLQuery query = session.createSQLQuery("select nonUniqueOnX1AA, id  from X1AA  where nonUniqueOnX1AA in (select * from table(:textValues))");
            query.addSynchronizedQuerySpace("X1AA");
            query.setParameter("textValues", textValues, new OracleArrayStringCustomType());
            query.setFetchSize(Math.min(1000, textValues.size()));
            query.addScalar("nonUniqueOnX1AA", StandardBasicTypes.STRING);
            query.addScalar("id", StandardBasicTypes.LONG);
            ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);

            while (scroll.next()) {
                Object[] next = scroll.get();
                if (next[1] != null) {
                    String key = (String) next[0];
                    Set<X1AAId<?>> relatedIds = result.get(key);
                    relatedIds.add(new X1AAId<>((Long) next[1], snapshotVersion));
                }
            }
        }
        return result;
    }

    @Override
    public Map<X1AAIdent, Set<X1AAId<?>>> findX1AAIdsForIdents(Collection<X1AAIdent> idents) {
        Map<X1AAIdent, Set<X1AAId<?>>> result = Maps.newHashMapWithExpectedSize(idents.size());
        for (X1AAIdent ident : idents) {
            result.put(ident, new HashSet<X1AAId<?>>());
        }

        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            SnapshotVersion snapshotVersion = SnapshotVersionContext.getInstance().getSnapshotVersion();
            Session session = sessionSelector.get(snapshotVersion);
            SQLQuery query = session.createSQLQuery("select b.nr as bnr, a.nr as anr, a.id  from X1AA a join X1BBOne b on a.someBBId=b.id  where (b.nr, a.nr) in (select * from table(:idents))");
            query.addSynchronizedQuerySpace("X1AA");
            query.addSynchronizedQuerySpace("X1BBOne");
            query.setParameter("idents", idents, new CustomType(new OracleX1AAIdentArrayUserType()));
            query.setFetchSize(Math.min(1000, idents.size()));
            query.addScalar("bnr", StandardBasicTypes.INTEGER);
            query.addScalar("anr", StandardBasicTypes.INTEGER);
            query.addScalar("id", StandardBasicTypes.LONG);
            ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);

            while (scroll.next()) {
                Object[] next = scroll.get();
                X1AAIdent ident = new X1AAIdent((Integer) next[0], (Integer) next[1]);
                Set<X1AAId<?>> relatedIds = result.get(ident);
                relatedIds.add(new X1AAId<>((Long) next[2], snapshotVersion));
            }
        }
        return result;
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
        Map<X1BBOneIdent, Set<X1BBOneId<?>>> result = Maps.newHashMapWithExpectedSize(idents.size());
        for (X1BBOneIdent ident : idents) {
            result.put(ident, new HashSet<X1BBOneId<?>>());
        }

        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            SnapshotVersion snapshotVersion = SnapshotVersionContext.getInstance().getSnapshotVersion();
            Session session = sessionSelector.get(snapshotVersion);
            SQLQuery query = session.createSQLQuery("select b.nr as bnr, b.id  from X1BBOne b where (b.nr) in (select * from table(:idents))");
            query.addSynchronizedQuerySpace("X1BBOne");
            query.setParameter("idents", idents, new CustomType(new OracleX1BBOneIdentArrayUserType()));
            query.setFetchSize(Math.min(1000, idents.size()));
            query.addScalar("bnr", StandardBasicTypes.INTEGER);
            query.addScalar("id", StandardBasicTypes.LONG);
            ScrollableResults scroll = query.scroll(ScrollMode.FORWARD_ONLY);

            while (scroll.next()) {
                Object[] next = scroll.get();
                X1BBOneIdent ident = new X1BBOneIdent((Integer) next[0]);
                Set<X1BBOneId<?>> relatedIds = result.get(ident);
                relatedIds.add(new X1BBOneId<>((Long) next[1], snapshotVersion));
            }
        }
        return result;
    }

}
