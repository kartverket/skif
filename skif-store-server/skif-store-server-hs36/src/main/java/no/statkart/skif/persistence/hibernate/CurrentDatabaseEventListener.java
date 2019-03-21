package no.statkart.skif.persistence.hibernate;

import no.statkart.skif.util.ThreadLocalWithSuspend;
import org.hibernate.Session;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.*;
import org.hibernate.internal.SessionImpl;
import org.hibernate.persister.entity.EntityPersister;

/**
 * A listener that remembers the current database event in an internal thread local object.
 * @author frehen
 */
public class CurrentDatabaseEventListener implements PreInsertEventListener, PreUpdateEventListener, PreDeleteEventListener, PostInsertEventListener, PostUpdateEventListener, PostDeleteEventListener{
    private final ThreadLocal<AbstractPreDatabaseOperationEvent> currentEvent = new ThreadLocalWithSuspend<>();
    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        this.currentEvent.set(event);
        return false;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        this.currentEvent.set(event);
        return false;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        this.currentEvent.set(event);
        return false;
    }

    @Override
    public void onPostDelete(PostDeleteEvent event) {
        this.currentEvent.set(null);
    }

    @Override
    public void onPostInsert(PostInsertEvent event) {
        this.currentEvent.set(null);
    }

    @Override
    public void onPostUpdate(PostUpdateEvent event) {
        this.currentEvent.set(null);
    }

    @Override
    public boolean requiresPostCommitHandling(EntityPersister persister) {
        return false;
    }

    @Override
    public boolean requiresPostCommitHanding(EntityPersister persister) {
        return false;
    }

    public AbstractPreDatabaseOperationEvent getcurrentEvent() {
        return currentEvent.get();
    }

    public static CurrentDatabaseEventListener getForSession(Session session) {
        final EventListenerGroup<PreInsertEventListener> listenerGroup = listenerGroup((SessionImpl)session,  EventType.PRE_INSERT );
        for (PreInsertEventListener preInsertEventListener : listenerGroup.listeners()) {
            if (preInsertEventListener instanceof CurrentDatabaseEventListener) {
                return ((CurrentDatabaseEventListener) preInsertEventListener);
            }
        }
        return null;
    }

    private  static <T> EventListenerGroup<T> listenerGroup(SessionImpl session, EventType<T> eventType) {
        return session
                .getFactory()
                .getServiceRegistry()
                .getService( EventListenerRegistry.class )
                .getEventListenerGroup( eventType );
    }

}
