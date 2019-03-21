package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.persistence.hibernate.CurrentDatabaseEventListener;
import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;

public class HibernateIntegrator implements Integrator {
    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        CurrentDatabaseEventListener listenerThatRembersCurrentDatabaseEvent = new CurrentDatabaseEventListener();

        final EventListenerRegistry eventListenerRegistry =
                serviceRegistry.getService( EventListenerRegistry.class );
        eventListenerRegistry.appendListeners( EventType.PRE_INSERT, listenerThatRembersCurrentDatabaseEvent );
        eventListenerRegistry.appendListeners( EventType.PRE_UPDATE, listenerThatRembersCurrentDatabaseEvent );
        eventListenerRegistry.appendListeners( EventType.PRE_DELETE, listenerThatRembersCurrentDatabaseEvent );
        eventListenerRegistry.appendListeners( EventType.POST_INSERT, listenerThatRembersCurrentDatabaseEvent );
        eventListenerRegistry.appendListeners( EventType.POST_UPDATE, listenerThatRembersCurrentDatabaseEvent );
        eventListenerRegistry.appendListeners( EventType.POST_DELETE, listenerThatRembersCurrentDatabaseEvent );
    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {

    }
}
