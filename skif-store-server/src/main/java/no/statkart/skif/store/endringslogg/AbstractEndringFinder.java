package no.statkart.skif.store.endringslogg;

import com.google.inject.Provider;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.List;

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

    protected final Provider<Session> sessionProvider;

    /**
     * @param endringsbaseklasse Den klassen som er roten i Hibernate-mappingen for endringer. Implementasjonens
     *                           konstruktør angir denne i sin konstruktør (tar den ikke inn som parameter) tilsvarende
     *                           som <code>T</code>.
     * @param sessionProvider    provider av gjeldende Hibernate-session
     */
    public AbstractEndringFinder(Class<T> endringsbaseklasse, Provider<Session> sessionProvider) {
        this.endringsbaseklasse = endringsbaseklasse;
        this.sessionProvider = sessionProvider;
    }

    /**
     * Finner siste endringsnummer totalt.
     *
     * @return siste endringenummer
     */
    public long findSisteEndringsnummer() {
        return findSisteEndringsnummerForClass(endringsbaseklasse);
    }

    /**
     * Finner siste endringsnummer for gitt endringsklasse, inkludert subklasser.
     *
     * @param endringsklasse tell kun endringer av denne klassen
     * @return siste endringenummer for endringsklassen
     */
    public long findSisteEndringsnummerForClass(Class<? extends T> endringsklasse) {
        Session session = sessionProvider.get();
        Criteria criteria = session.createCriteria(endringsklasse);
        criteria.setProjection(Projections.max("id"));
        AbstractEndringId<?> endringId = (AbstractEndringId<?>) criteria.uniqueResult();
        return endringId != null ? endringId.getValue() : 0L;
    }

    /**
     * Henter alle endringer etter gitt endringsnummer. Endringen med gitt endringsnummer er ikke inkludert.
     *
     * @param endringsnummer endringsnummeret før første endring som skal hentes
     * @param maksAntall     maksimalt antall endringer som skal hentes
     * @return endringene, sortert etter stigende endringsnummer
     */
    public List<T> findEndringerEtterEndringsnummer(long endringsnummer, int maksAntall) {
        return findEndringerEtterEndringsnummerForClass(endringsnummer, endringsbaseklasse, maksAntall);
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
    public <E extends T> List<E> findEndringerEtterEndringsnummerForClass(long endringsnummer, Class<E> endringsklasse, int maksAntall) {
        Session session = sessionProvider.get();
        Criteria criteria = session.createCriteria(endringsklasse);
        criteria.add(Restrictions.gt("id", new AbstractEndringId(endringsnummer)));
        criteria.addOrder(Order.asc("id"));
        criteria.setMaxResults(maksAntall);
        return criteria.list();
    }
}
