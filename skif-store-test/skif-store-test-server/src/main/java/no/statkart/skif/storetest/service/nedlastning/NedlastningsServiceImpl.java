package no.statkart.skif.storetest.service.nedlastning;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.SessionSelector;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.Kontroll;
import no.statkart.skif.storetest.endringslogg.EndringManagerConfiguration;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * @author Henrik Fredholm
 * @since 2.4
 */
public class NedlastningsServiceImpl implements NedlastningsService {
    @Inject
    Provider<SnapshotVersion> snapshotVersionProvider;

    @Inject
    private Provider<SessionSelector> sessionSelectorProvider;

    @Inject
    EndringManagerConfiguration endringManagerConfiguration;

    @Override
    public <I extends BubbleId<? extends T>, T extends BubbleObject> List<I> findIdsEtterId(@Nullable BubbleId<? extends T> id, Class<T> bobleklasse, @Nullable String filter, int maksAntall) {
        checkBobbleklasseGyldigForNedlasting(bobleklasse);
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            Criteria criteria = session.createCriteria(bobleklasse);
            criteria.setMaxResults(maksAntall);
            criteria.setProjection(Projections.id());
            if (id != null) {
                criteria.add(Restrictions.gt("id", id));
            }
            return (List<I>) criteria.list();
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }

    @Override
    public <T extends BubbleObject> List<T> findObjekterEtterId(@Nullable BubbleId<? extends T> id, Class<T> bobleklasse, @Nullable String filter, int maksAntall) {
        checkBobbleklasseGyldigForNedlasting(bobleklasse);
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            Criteria criteria = session.createCriteria(bobleklasse);
            criteria.setMaxResults(maksAntall);
            if (id != null) {
                criteria.add(Restrictions.gt("id", id));
            }
            return (List<T>) criteria.list();
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }

    @Override
    public <T extends StoreTestBubble> Kontroll calcObjektkontrollForRange(@Nullable BubbleId<? extends T> fraId, @Nullable BubbleId<? extends T> tilId, Class<T> bobleklasse, @Nullable String filter) {
        checkBobbleklasseGyldigForNedlasting(bobleklasse);
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            Criteria criteria = session.createCriteria(bobleklasse);
            criteria.setProjection(Projections.rowCount());
            if (fraId != null) {
                criteria.add(Restrictions.gt("id", fraId));
            }
            if (tilId != null) {
                criteria.add(Restrictions.le("id", tilId));
            }
            Kontroll result =  new Kontroll();
            result.setAntall(((Number)criteria.uniqueResult()).longValue()); // kan ikke caste direkte til Long pga forskjell på datatype her i hibernate 3.2 og 3.6
            return result;
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }

    @Override
    public <I extends StoreTestBubbleId<? extends T>, T extends StoreTestBubble> Kontroll calcObjektkontrollForList(Collection<I> ids, Class<T> bobleklasse) {
        checkBobbleklasseGyldigForNedlasting(bobleklasse);
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(snapshotVersionProvider.get());
            Criteria criteria = session.createCriteria(bobleklasse);
            criteria.setProjection(Projections.rowCount());
            criteria.add(Restrictions.sqlRestriction("id in (select * from table(?))", ids, new OracleLongBubbleIdArrayCustomType()));
            Kontroll result = new Kontroll();
            result.setAntall(((Number) criteria.uniqueResult()).longValue()); // kan ikke caste direkte til Long pga forskjell på datatype her i hibernate 3.2 og 3.6
            return result;
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }

    private <T extends BubbleObject> void checkBobbleklasseGyldigForNedlasting(Class<T> bobleklasse) {
        checkArgument(endringManagerConfiguration.getEndringsklasse(bobleklasse)!=Endring.class, "Domainklasse %s kan ikke brukes som filter for nedlastning", bobleklasse.getSimpleName());
    }

}
