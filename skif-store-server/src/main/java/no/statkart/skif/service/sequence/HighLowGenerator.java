package no.statkart.skif.service.sequence;

import com.google.inject.Provider;
import no.statkart.skif.store.BubbleId;
import org.hibernate.HibernateException;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;

/**
 * Hibernate specifik sekvens generator klasse som virker for JTA transaksjoner. Brukes av Hibernate
 * ved automatisk generering av id'er ved insert. Denne klasser er implementert ved bruk av
 * {@link IdService}
 *
 * @author Henrik Fredholm
 */
public class HighLowGenerator implements IdentifierGenerator {
    private static Provider<IdService> idServiceProvider;

    public static void setIdServiceProvider(Provider<IdService> idServiceProvider) {
        HighLowGenerator.idServiceProvider = idServiceProvider;
    }

    public synchronized Serializable generate(SessionImplementor session, Object object) throws HibernateException {
        return (Serializable) idServiceProvider.get().getNextIdValue((Class <BubbleId>)object.getClass());
    }
}
