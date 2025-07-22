package no.statkart.skif.service.sequence;

import com.google.common.base.Preconditions;
import jakarta.inject.Provider;
import no.statkart.skif.store.BubbleId;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.engine.config.spi.ConfigurationService;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;

import java.io.Serializable;
import java.util.Properties;

/**
 * Hibernate specifik sekvens generator klasse som virker for JTA transaksjoner. Brukes av Hibernate
 * ved automatisk generering av id'er ved insert. Denne klasser er implementert ved bruk av
 * {@link IdService}
 *
 * @author Henrik Fredholm
 */
public class HighLowGenerator implements IdentifierGenerator, Configurable {
    private Provider<IdService> idServiceProvider;

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
        return Preconditions.checkNotNull(idServiceProvider, "No Provider<IdService> was registered for SessionFactory: %s", sessionFactory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
        idServiceProvider = (Provider<IdService>) serviceRegistry.requireService(ConfigurationService.class)
                .getSetting("no.statkart.skif.IdServiceProvider", Provider.class, null);
    }
}
