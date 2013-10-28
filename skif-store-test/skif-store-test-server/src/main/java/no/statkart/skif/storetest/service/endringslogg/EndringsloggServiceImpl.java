package no.statkart.skif.storetest.service.endringslogg;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.endringslogg.AbstractEndringId;
import no.statkart.skif.store.persistence.SessionSelector;
import no.statkart.skif.storetest.domain.AbstractStoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.domain.endringslogg.Endring;
import no.statkart.skif.storetest.domain.endringslogg.Kontroll;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestDbKode;
import no.statkart.skif.storetest.endringslogg.EndringFinder;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import javax.annotation.Nullable;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Implementasjon av {@link EndringsloggService}.
 *
 * @author Tor Egil R. Strand
 * @author Henrik Fredholm
 * @since 2.2.0
 */
public class EndringsloggServiceImpl implements EndringsloggService {
    @Inject
    private EndringFinder endringFinder;

    @Inject
    private Provider<SessionSelector> sessionSelectorProvider;

    @Override
    public long findSisteEndringsnummer(SnapshotVersion snapshotVersion) {
        return endringFinder.findSisteEndringsnummer(snapshotVersion);
    }

    @Override
    public <E extends Endring> List<E> findEndringerEtterEndringsnummer(long endringsnummer, Class<E> endringsklasse, int maksAntall, SnapshotVersion snapshotVersion) {
        return endringFinder.findEndringerEtterEndringsnummerForClass(endringsnummer, endringsklasse, maksAntall, snapshotVersion);
    }

    // Kan vurdere om denne metode bør forwarde til en finder
    @Override
    @SuppressWarnings("unchecked")
    public <I extends BubbleId<? extends T>, T extends BubbleObject> List<I> findIdsEtterId(@Nullable BubbleId<? extends T> id, Class<T> klassefilter, int maksAntall, SnapshotVersion snapshotVersion) {
        checkArgument(!Modifier.isAbstract(klassefilter.getModifiers()), "Klassefilter kan ikke være en abstrakt klasse");
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(snapshotVersion);
            Criteria criteria = session.createCriteria(klassefilter);
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

    // Kan vurdere om denne metode bør forwarde til en finder
    @Override
    public <K extends Kontroll, I extends StoreTestBubbleId<T>, T extends StoreTestBubble> K calcKontrollForRange(@Nullable BubbleId<? extends T> fraId, @Nullable BubbleId<? extends T> tilId, Class<T> klassefilter, SnapshotVersion snapshotVersion) {
        checkArgument(!Modifier.isAbstract(klassefilter.getModifiers()), "Klassefilter kan ikke være en abstrakt klasse");
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(snapshotVersion);
            Criteria criteria = session.createCriteria(klassefilter);
            criteria.setProjection(Projections.rowCount());
            if (fraId != null) {
                criteria.add(Restrictions.gt("id", fraId));
            }
            if (tilId != null) {
                criteria.add(Restrictions.le("id", fraId));
            }
            K result = (K) new Kontroll<I>();
            result.setAntall((Long) criteria.uniqueResult());
            return result;
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }

    // Kan vurdere om denne metode bør forwarde til en finder
    @Override
    public <K extends Kontroll, I extends StoreTestBubbleId<? extends T>, T extends StoreTestBubble> K calcKontrollForList(Collection<I> ids, Class<T> klassefilter, SnapshotVersion snapshotVersion) {
        checkArgument(!Modifier.isAbstract(klassefilter.getModifiers()), "Klassefilter kan ikke være en abstrakt klasse");
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(snapshotVersion);
            Criteria criteria = session.createCriteria(klassefilter);
            criteria.setProjection(Projections.rowCount());
            criteria.add(Restrictions.sqlRestriction("id in (select * from table(?))",ids, new OracleLongBubbleIdArrayCustomType()));
            K result = (K) new Kontroll<I>();
            result.setAntall((Long) criteria.uniqueResult());
            return result;
        } finally {
            if (sessionSelector != null) sessionSelector.close();
        }
    }

}
