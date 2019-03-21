package no.statkart.skif.service.sequence;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import no.statkart.skif.store.BubbleId;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import javax.inject.Provider;
import java.io.Serializable;
import java.util.Map;

/**
 * Hibernate specifik sekvens generator klasse som virker for JTA transaksjoner. Brukes av Hibernate
 * ved automatisk generering av id'er ved insert. Denne klasser er implementert ved bruk av
 * {@link IdService}
 *
 * @author Henrik Fredholm
 */
public class HighLowGenerator implements IdentifierGenerator {
    private static Map<SessionFactory, Provider<IdService>> map = Maps.newConcurrentMap();

    /**
     * @since 2.3
     */
    public static void registerIdServiceForSessionFactory(SessionFactory sessionFactory, Provider<IdService> provider) {
        Preconditions.checkState(map.put(sessionFactory, provider) == null, "A Provider<IdService> is already registered for SessionFactory: %s", sessionFactory);
    }

    /**
     * @since 2.3
     */
    public static void unregisterIdServiceForSessionFactory(SessionFactory sessionFactory) {
        map.remove(sessionFactory);
    }

    /**
     * Denne metode er synchronized da {@code IdService} objektet som metoden anvender allokerer id-er fra en id-blok
     * som er felles for alle tråder. Man kunne ha separate blokker per tråd ved å gjøre {@code IdService} om til å være
     * request scoped i stedet for å være en singleton, men det vil føre til at man får flere ubrukte id-er per restart.
     * <p>
     * Man kan vurdere å flytte synchronized til implementasjonen av {@code IdService} slik at det er konfigurasjonen
     * som om avgjør om kallet er synchronized eller ikke.
     */
    @Override
    public synchronized Serializable generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
        final Provider<IdService> idServiceProvider = getIdServiceProvider(session.getFactory());
        return (Serializable) idServiceProvider.get().getNextIdValue((Class<BubbleId>) object.getClass());
    }

    private Provider<IdService> getIdServiceProvider(SessionFactory sessionFactory) {
        return Preconditions.checkNotNull(map.get(sessionFactory), "No Provider<IdService> was registered for SessionFactory: %s", sessionFactory);
    }
}
