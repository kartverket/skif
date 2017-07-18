package no.statkart.skif.persistence.hibernate;

import no.statkart.skif.util.ThreadLocalWithSuspend;
import org.hibernate.Session;
import org.hibernate.event.*;
import org.hibernate.impl.SessionImpl;

/**
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

    public AbstractPreDatabaseOperationEvent getcurrentEvent() {
        return currentEvent.get();
    }

    public static CurrentDatabaseEventListener getForSession(Session session) {
        SessionImpl sessionImpl = (SessionImpl) session;
        PreInsertEventListener[] preInsertEventListeners = sessionImpl.getListeners().getPreInsertEventListeners();
        for (PreInsertEventListener preInsertEventListener : preInsertEventListeners) {
            if (preInsertEventListener instanceof CurrentDatabaseEventListener) {
                return ((CurrentDatabaseEventListener) preInsertEventListener);
            }
        }
        return null;
    }
}
