package no.statkart.skif.persistence.hibernate;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

/**
 * Hibernate listeners that must be added to Hibernate in order for empty collection optimization to work. Only
 * Bubble that are using the flag in their mapping files will be affected. These listeners should not
 * be disabled. Remove the flag from the mappings files instead. Note that these listeners alone is
 * not enough to handle all cases for keeping the flag up-to-date. See usage of EmptyCollectionsFlagUpdater
 * in HibernatePersistenceSessionMasterImpl.
 *
 * @author Henrik Fredholm
 */
public class EmptyCollectionOptimizerIntegrator implements Integrator {
   @Override
   public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
      final EventListenerRegistry eventListenerRegistry = serviceRegistry.getService( EventListenerRegistry.class );
      eventListenerRegistry.appendListeners(EventType.PRE_LOAD, EmptyCollectionsOptimizerListener.class);
      eventListenerRegistry.appendListeners(EventType.PRE_COLLECTION_UPDATE, EmptyCollectionsOptimizerListener.class);
      eventListenerRegistry.appendListeners(EventType.SAVE, EmptyCollectionsOptimizerListener.class);
   }

   @Override
   public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
   }
}


