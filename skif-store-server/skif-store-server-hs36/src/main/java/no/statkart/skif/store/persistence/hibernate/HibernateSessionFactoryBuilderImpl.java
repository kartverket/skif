package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.persistence.hibernate.BugFixDeleteEventListener;
import no.statkart.skif.persistence.hibernate.CurrentDatabaseEventListener;
import no.statkart.skif.persistence.hibernate.EmptyCollectionOptimizerPreLoadListener;
import no.statkart.skif.persistence.hibernate.EmptyCollectionsOptimizer;
import no.statkart.skif.store.persistence.hibernate.bubbleref.BubbleRefConfiguration;
import org.hibernate.Interceptor;
import org.hibernate.MappingException;
import org.hibernate.cfg.Configuration;
import org.hibernate.event.DeleteEventListener;
import org.hibernate.event.PostDeleteEventListener;
import org.hibernate.event.PostInsertEventListener;
import org.hibernate.event.PostUpdateEventListener;
import org.hibernate.event.PreDeleteEventListener;
import org.hibernate.event.PreInsertEventListener;
import org.hibernate.event.PreLoadEventListener;
import org.hibernate.event.PreUpdateEventListener;
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
public class HibernateSessionFactoryBuilderImpl extends HibernateSessionFactoryBuilder {
    public HibernateSessionFactoryBuilderImpl(String mappingFilesDirectory) {
        super(mappingFilesDirectory);
    }

    protected Configuration createConfiguration(Properties props, @Nullable Interceptor interceptor) {
        // Log databaseparametre. I singlevm mode brukes JDBCTransactionFactory (dvs url, bruker/password).
        // I servermode brukes JTATransactionFactory (dvs datasource)
        String connectionInfo;
        if (props.get("hibernate.transaction.factory_class").equals("org.hibernate.transaction.JDBCTransactionFactory")) {
            connectionInfo = props.getProperty("hibernate.connection.url") + " - " + props.getProperty("hibernate.connection.username");
        } else {
            // TODO: Dette blir feil for SnapshotVersion.OLD. Må bruke old datasource
            connectionInfo = props.getProperty("hibernate.connection.datasource");
        }
        logger.info("SKIF hibernatekonfigurasjon({}): {}", org.hibernate.Version.getVersionString(), connectionInfo);
        ClassLoader cl = HibernateSessionFactoryBuilder.class.getClassLoader();
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

            // Legg på listeners for ta vare på event med id ifm databaseoperasjoner for insert, update og delete. Dette flagget setter det for alle sessioner.
            // Det er også mulig å sette det for en enkelt session via EventListenersUtil klassen. Dermed er det mulig å unngå listener overheaded som introduseres
            // nå dette flagget settes.
            if ("true".equalsIgnoreCase(props.getProperty(SkifConfigConstants.USE_DATABASE_EVENT_LISTENER))) {
                addCurrentDatabaseEventListener(cfg);
            }

            // Nedenstående gjøres nå via en StoreSessionListener og trens derfor ikke lengre her.
            // Old session skal aldrig forsøke å oppdatere emptycollectionsflagget. Derfor legges listeneren kun på Current session
//          if( SnapshotVersion == SnapshotVersion.CURRENT ) {
//              FlushEntityEventListener[] flushEntityStack = {new EmptyCollectionOptimizerFlushEntityEventListener(optimizers), new DefaultFlushEntityEventListener()};
//              cfg.getEventListeners().setFlushEntityEventListeners(flushEntityStack);
//          }
        } catch (MappingException e) {
            throw new ConfigurationException("Error in Hibernate mapping files: " + e.getMessage(), e, logger);
        }
        return cfg;
    }

    private void addCurrentDatabaseEventListener(Configuration cfg) {
        CurrentDatabaseEventListener rememberCurrentEventListener = new CurrentDatabaseEventListener();
        PreInsertEventListener[] preInsertEventStack = {rememberCurrentEventListener};
        cfg.getEventListeners().setPreInsertEventListeners(preInsertEventStack);
        PreUpdateEventListener[] preUpdateEventStack = {rememberCurrentEventListener};
        cfg.getEventListeners().setPreUpdateEventListeners(preUpdateEventStack);
        PreDeleteEventListener[] preDeleteEventStack = {rememberCurrentEventListener};
        cfg.getEventListeners().setPreDeleteEventListeners(preDeleteEventStack);
        PostInsertEventListener[] PostInsertEventStack = {rememberCurrentEventListener};
        cfg.getEventListeners().setPostInsertEventListeners(PostInsertEventStack);
        PostUpdateEventListener[] PostUpdateEventStack = {rememberCurrentEventListener};
        cfg.getEventListeners().setPostUpdateEventListeners(PostUpdateEventStack);
        PostDeleteEventListener[] PostDeleteEventStack = {rememberCurrentEventListener};
        cfg.getEventListeners().setPostDeleteEventListeners(PostDeleteEventStack);
    }

}
