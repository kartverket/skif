package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.persistence.hibernate.BugFixDeleteEventListener;
import no.statkart.skif.persistence.hibernate.EmptyCollectionOptimizerPreLoadListener;
import no.statkart.skif.persistence.hibernate.EmptyCollectionsOptimizer;
import no.statkart.skif.store.persistence.hibernate.bubbleref.BubbleRefConfiguration;
import no.statkart.skif.store5.persistence.hibernate.*;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.DeleteEventListener;
import org.hibernate.event.PreLoadEventListener;
import org.hibernate.event.def.DefaultPreLoadEventListener;
import org.hibernate.persister.entity.EntityPersister;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Denne klasse inneholder Hibernate 3.2.6 specifikk kode. Den skal integreres i superklassen
 * når SKIF støtter bubbleref for seneste versjon av hibernate
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class HibernateSessionFactoryBuilderImpl extends no.statkart.skif.store5.persistence.hibernate.HibernateSessionFactoryBuilder {
    public HibernateSessionFactoryBuilderImpl(String mappingFilesDirectory) {
        super(mappingFilesDirectory);
    }

    protected Configuration createConfiguration(Properties props, @Nullable Interceptor interceptor) {
        // Log databaseparametre. I singlevm mode brukes JDBCTransactionFactory (dvs url, bruker/password).
        // I servermode brukes JTATransactionFactory (dvs datasource)
        if (props.get("hibernate.transaction.factory_class").equals("org.hibernate.transaction.JDBCTransactionFactory")) {
            logger.info("SKIF hibernatekonfigurasjon(3.2): " + props.get("hibernate.connection.url") + " - " + props.get("hibernate.connection.username"));
        } else {
            // TODO: Dette blir feil for SnapshotVersion.OLD. Må bruke old datasource
            logger.info("SKIF hibernatekonfigurasjon(3.2): " + props.get("hibernate.connection.datasource"));
        }
        ClassLoader cl = no.statkart.skif.store5.persistence.hibernate.HibernateSessionFactoryBuilder.class.getClassLoader();
        Configuration cfg = null;
        try {
            // NB: getBubbleClassDeleteOrder() definerer slette rekkefølgen for alle {@code BubbleObject} typer.
            // Metoden {@link #addResourceUsingAbsolutePath} legger automatisk {@code BubbleObject} klasser inn i listen i den rekkefølge
            // metoden blir kallt.
            cfg = new BubbleRefConfiguration().setProperties(props);
            for (String hbm : hbmResource) {
                cfg.addResource(hbm, cl);
            }
            if (interceptor!=null) {
                cfg.setInterceptor(interceptor);
            }


            // Legg in patch for Hibernate 3.2.6
            DeleteEventListener[] deleteEventStack = {new BugFixDeleteEventListener()};
            cfg.getEventListeners().setDeleteEventListeners(deleteEventStack);

            // Configure Listners for fast initialization of empty collections. Listeners are active on load events. The flag is update via a StoreSessionListener
            Map<EntityPersister, EmptyCollectionsOptimizer> optimizers = new HashMap<EntityPersister, EmptyCollectionsOptimizer>();
            PreLoadEventListener[] preLoadStack = {new EmptyCollectionOptimizerPreLoadListener(optimizers), new DefaultPreLoadEventListener()};
            cfg.getEventListeners().setPreLoadEventListeners(preLoadStack);

            // Nedenstående gjøres nå via en StoreSessionListener og trens derfor ikke lengre her.
            // Old session skal aldrig forsøke å oppdatere emptycollectionsflagget. Derfor legges listeneren kun på Current session
//          if( SnapshotVersion == SnapshotVersion.CURRENT ) {
//              FlushEntityEventListener[] flushEntityStack = {new EmptyCollectionOptimizerFlushEntityEventListener(optimizers), new DefaultFlushEntityEventListener()};
//              cfg.getEventListeners().setFlushEntityEventListeners(flushEntityStack);
//          }
        } catch (MappingException e) {
            throw new ImplementationException("Feil i hibernate mapping-filer: " + e.getMessage(), e, logger);
        }
        return cfg;
    }

}
