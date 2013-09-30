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

    public Map<X1BBOneId<?>, Set<X1AAId<?>>> findInvSomeBBIds(Collection<X1BBOneId<?>> x1BBOneIds) {
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
                X1BBOneId<?> key = new X1BBOneId<X1BBOne>((Long)next[0], snapshotVersion);
                Set<X1AAId<?>> relatedIds = result.get(key);
                relatedIds.add(new X1AAId<X1AA>((Long)next[1], snapshotVersion));
            }
        } finally {
            HibernateHelper.close(preparedStatement, sessionSelector);
        }
        return result;
    }
}
