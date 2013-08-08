package no.statkart.skif.service.sequence;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import no.statkart.skif.store.BubbleId;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.engine.SessionImplementor;
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

    public synchronized Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        final Provider<IdService> idServiceProvider = getIdServiceProvider(session.getFactory());
        return (Serializable) idServiceProvider.get().getNextIdValue((Class<BubbleId>) object.getClass());
    }

    private Provider<IdService> getIdServiceProvider(SessionFactory sessionFactory) {
        return Preconditions.checkNotNull(map.get(sessionFactory), "No Provider<IdService> was registered for SessionFactory: %s", sessionFactory);
    }
}
