package no.statkart.skif.store.endringslogg;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.persistence.SessionSelector;
import no.statkart.skif.util.HibernateHelper;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Set;

/**
 * Basisfunksjonalitet for å finne endringer.
 *
 * @param <T>    Representerer rotendringsklassen i Hibernate-mappingen. Denne bindes opp i implementasjones
 *               <code>extends</code>-bit, og via klassen som sendes inn til konstruktøren.
 *
 * @author Tor Egil R. Strand
 * @since 2.2.0
 */
public abstract class AbstractEndringFinder<T extends AbstractEndring> {
    private final Class<T> endringsbaseklasse;

    @Inject
    private Provider<SessionSelector> sessionSelectorProvider;

    /**
     * @param endringsbaseklasse Den klassen som er roten i Hibernate-mappingen for endringer. Implementasjonens
     *                           konstruktør angir denne i sin konstruktør (tar den ikke inn som parameter) tilsvarende
     *                           som <code>T</code>.
     */
    public AbstractEndringFinder(Class<T> endringsbaseklasse) {
        this.endringsbaseklasse = endringsbaseklasse;
    }

    /**
     * Finner siste endringsnummer totalt.
     *
     * @return siste endringenummer
     */
    public long findSisteEndringsnummer(SnapshotVersion snapshotVersion) {
        return findSisteEndringsnummerForClass(endringsbaseklasse, snapshotVersion);
    }

    /**
     * Finner siste endringsnummer for gitt endringsklasse, inkludert subklasser.
     *
     * @param endringsklasse tell kun endringer av denne klassen
     * @return siste endringenummer for endringsklassen
     */
    public long findSisteEndringsnummerForClass(Class<? extends T> endringsklasse, SnapshotVersion snapshotVersion) {
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(snapshotVersion);
            Criteria criteria = session.createCriteria(endringsklasse);
            criteria.setProjection(Projections.max("id"));
            AbstractEndringId<?> endringId = (AbstractEndringId<?>) criteria.uniqueResult();
            return endringId != null ? endringId.getValue() : 0L;
        } finally {
            if (sessionSelector!=null) sessionSelector.close();
        }
    }

    /**
     * Henter alle endringer etter gitt endringsnummer. Endringen med gitt endringsnummer er ikke inkludert.
     *
     * @param endringsnummer endringsnummeret før første endring som skal hentes
     * @param maksAntall     maksimalt antall endringer som skal hentes
     * @return endringene, sortert etter stigende endringsnummer
     */
    public List<T> findEndringerEtterEndringsnummer(long endringsnummer, int maksAntall, SnapshotVersion snapshotVersion) {
        return findEndringerEtterEndringsnummerForClass(endringsnummer, endringsbaseklasse, maksAntall, snapshotVersion);
    }

    /**
     * Henter alle endringer av gitt endringsklasse, inkludert subklasser, etter gitt endringsnummer. Endringen med gitt
     * endringsnummer er ikke inkludert.
     *
     * @param endringsnummer endringsnummeret før første endring som skal hentes
     * @param endringsklasse hent kun endringer av denne klassen
     * @param maksAntall     maksimalt antall endringer som skal hentes
     * @param <E>            tilsvarer <code>endringsklasse</code>
     * @return endringene, sortert etter stigende endringsnummer
     */
    @SuppressWarnings("unchecked")
    public <E extends T> List<E> findEndringerEtterEndringsnummerForClass(long endringsnummer, Class<E> endringsklasse, int maksAntall, SnapshotVersion snapshotVersion) {
        SessionSelector sessionSelector = sessionSelectorProvider.get();
        try {
            Session session = sessionSelector.get(snapshotVersion);
            Criteria criteria = session.createCriteria(endringsklasse);
            criteria.add(Restrictions.gt("id", new AbstractEndringId(endringsnummer)));
            //criteria.addOrder(Order.asc("id")); // trengs ikke da Endring er definert som organization index tabell
            criteria.setMaxResults(maksAntall);
            return criteria.list();
        } finally {
            if (sessionSelector!=null) sessionSelector.close();
        }
    }
}
