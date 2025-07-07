package no.statkart.skif.storetest.domain.relation.uni.direct;

import com.google.inject.Inject;
import jakarta.inject.Provider;
import no.statkart.skif.SkifUtil;
import no.statkart.skif.persistence.hibernate.type.OracleArrayStringCustomType;
import no.statkart.skif.persistence.hibernate.type.OracleArrayUserType;
import no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionContext;
import no.statkart.skif.store.persistence.OracleArrayConverter;
import no.statkart.skif.store.persistence.SessionSelector;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.query.NativeQuery;
import org.hibernate.type.CustomType;
import org.hibernate.type.StandardBasicTypes;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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
            NativeQuery<?> sqlQuery = sessionSelector.get(snapshotVersion)
                .createNativeQuery("select someBBId, id  from X1AA  where someBBId in (select * from table(:idValues))")
                .addSynchronizedQuerySpace("X1AA")
                .setParameter("idValues", x1BBOneIds, new OracleLongBubbleIdArrayCustomType())
                .setFetchSize(Math.min(1000, x1BBOneIds.size()))
                .addScalar("someBBId", StandardBasicTypes.LONG)
                .addScalar("id", StandardBasicTypes.LONG);
            
            try (ScrollableResults scroll = sqlQuery.scroll(ScrollMode.FORWARD_ONLY)) {
                while (scroll.next()) {
                    Object[] next = scroll.get();
                    X1BBOneId<?> key = new X1BBOneId<>((Long) next[0], snapshotVersion);
                    Set<X1AAId<?>> relatedIds = result.get(key);
                    relatedIds.add(new X1AAId<>((Long) next[1], snapshotVersion));
                }
            }
        }
        return result;
    }

    @Override
    public Map<X1CCManyId<?>, X1AAId<?>> findInvSomeCCsId(Collection<? extends X1CCManyId<?>> x1CCManyIds) {
        if (x1CCManyIds.isEmpty()) return emptyMap();

        Map<X1CCManyId<?>, X1AAId<?>> result = SkifUtil.newHashMapWithNullValues(x1CCManyIds);

        SnapshotVersion snapshotVersion = x1CCManyIds.iterator().next().getSnapshotVersion();
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            NativeQuery<?> sqlQuery = sessionSelector.get(snapshotVersion)
                .createNativeQuery("select id, ownerId  from X1CCMany  where id in (select * from table(:idValues))")
                .addSynchronizedQuerySpace("X1CCMany")
                .setParameter("idValues", x1CCManyIds, new OracleLongBubbleIdArrayCustomType())
                .setFetchSize(Math.min(1000, x1CCManyIds.size()))
                .addScalar("id", StandardBasicTypes.LONG)
                .addScalar("ownerId", StandardBasicTypes.LONG);

            try (ScrollableResults scroll = sqlQuery.scroll(ScrollMode.FORWARD_ONLY)) {
                while (scroll.next()) {
                    Object[] next = scroll.get();
                    if (next[1] != null) {
                        X1CCManyId<?> key = new X1CCManyId<>((Long) next[0], snapshotVersion);
                        result.put(key, new X1AAId<>((Long) next[1], snapshotVersion));
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Map<String, X1AAId<?>> findX1AAIdsForUniqueOnX1AA(Collection<String> textValues) {
        if (textValues.isEmpty()) return emptyMap();

        Map<String, X1AAId<?>> result = SkifUtil.newHashMapWithNullValues(textValues);

        SnapshotVersion snapshotVersion = SnapshotVersion.CURRENT;
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            NativeQuery<?> sqlQuery = sessionSelector.get(snapshotVersion)
                .createNativeQuery("select uniqueOnX1AA, id  from X1AA  where uniqueOnX1AA in (select * from table(:textValues))")
                .addSynchronizedQuerySpace("X1AA")
                .setParameter("textValues", textValues, new OracleArrayStringCustomType())
                .setFetchSize(Math.min(1000, textValues.size()))
                .addScalar("uniqueOnX1AA", StandardBasicTypes.STRING)
                .addScalar("id", StandardBasicTypes.LONG);

            try (ScrollableResults scroll = sqlQuery.scroll(ScrollMode.FORWARD_ONLY)) {
                while (scroll.next()) {
                    Object[] next = scroll.get();
                    if (next[1] != null) {
                        String key = (String) next[0];
                        result.put(key, new X1AAId<>((Long) next[1], snapshotVersion));
                    }
                }
            }
        }
        return result;
    }

    public Map<String, Set<X1AAId<?>>> findX1AAIdsForNonUniqueOnX1AA(Collection<String> textValues) {
        if (textValues.isEmpty()) return emptyMap();

        Map<String, Set<X1AAId<?>>> result = SkifUtil.newHashMapWithNullValues(textValues);

        SnapshotVersion snapshotVersion = SnapshotVersion.CURRENT;
        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            NativeQuery<?> sqlQuery = sessionSelector.get(snapshotVersion)
                .createNativeQuery("select nonUniqueOnX1AA, id  from X1AA  where nonUniqueOnX1AA in (select * from table(:textValues))")
                .addSynchronizedQuerySpace("X1AA")
                .setParameter("textValues", textValues, new OracleArrayStringCustomType())
                .setFetchSize(Math.min(1000, textValues.size()))
                .addScalar("nonUniqueOnX1AA", StandardBasicTypes.STRING)
                .addScalar("id", StandardBasicTypes.LONG);

            try (ScrollableResults scroll = sqlQuery.scroll(ScrollMode.FORWARD_ONLY)) {
                while (scroll.next()) {
                    Object[] next = scroll.get();
                    if (next[1] != null) {
                        String key = (String) next[0];
                        Set<X1AAId<?>> relatedIds = result.get(key);
                        relatedIds.add(new X1AAId<>((Long) next[1], snapshotVersion));
                    }
                }
            }
        }
        return result;
    }

    @Override
    public Map<X1AAIdent, Set<X1AAId<?>>> findX1AAIdsForIdents(Collection<X1AAIdent> idents) {
        Map<X1AAIdent, Set<X1AAId<?>>> result = SkifUtil.newHashMapWithEmptySetValues(idents);

        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            SnapshotVersion snapshotVersion = SnapshotVersionContext.getInstance().getSnapshotVersion();
            NativeQuery<?> sqlQuery = sessionSelector.get(snapshotVersion)
                .createNativeQuery("select b.nr as bnr, a.nr as anr, a.id" +
                    " from X1AA a join X1BBOne b on (a.someBBId=b.id)" +
                    " where (b.nr, a.nr) in (select * from table(:idents))")
                .addSynchronizedQuerySpace("X1AA")
                .addSynchronizedQuerySpace("X1BBOne")
                .setParameter("idents", idents, new CustomType(new OracleX1AAIdentArrayUserType()))
                .setFetchSize(Math.min(1000, idents.size()))
                .addScalar("bnr", StandardBasicTypes.INTEGER)
                .addScalar("anr", StandardBasicTypes.INTEGER)
                .addScalar("id", StandardBasicTypes.LONG);

            try (ScrollableResults scroll = sqlQuery.scroll(ScrollMode.FORWARD_ONLY)) {
                while (scroll.next()) {
                    Object[] next = scroll.get();
                    X1AAIdent ident = new X1AAIdent((Integer) next[0], (Integer) next[1]);
                    Set<X1AAId<?>> relatedIds = result.get(ident);
                    relatedIds.add(new X1AAId<>((Long) next[2], snapshotVersion));
                }
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
        Map<X1BBOneIdent, Set<X1BBOneId<?>>> result = SkifUtil.newHashMapWithEmptySetValues(idents);

        try (SessionSelector sessionSelector = sessionSelectorProvider.get()) {
            SnapshotVersion snapshotVersion = SnapshotVersionContext.getInstance().getSnapshotVersion();
            NativeQuery<?> sqlQuery = sessionSelector.get(snapshotVersion)
                .createNativeQuery("select b.nr as bnr, b.id  from X1BBOne b where (b.nr) in (select * from table(:idents))")
                .addSynchronizedQuerySpace("X1BBOne")
                .setParameter("idents", idents, new CustomType(new OracleX1BBOneIdentArrayUserType()))
                .setFetchSize(Math.min(1000, idents.size()))
                .addScalar("bnr", StandardBasicTypes.INTEGER)
                .addScalar("id", StandardBasicTypes.LONG);

            try (ScrollableResults scroll = sqlQuery.scroll(ScrollMode.FORWARD_ONLY)) {
                while (scroll.next()) {
                    Object[] next = scroll.get();
                    X1BBOneIdent ident = new X1BBOneIdent((Integer) next[0]);
                    Set<X1BBOneId<?>> relatedIds = result.get(ident);
                    relatedIds.add(new X1BBOneId<>((Long) next[1], snapshotVersion));
                }
            }
        }
        return result;
    }

}
