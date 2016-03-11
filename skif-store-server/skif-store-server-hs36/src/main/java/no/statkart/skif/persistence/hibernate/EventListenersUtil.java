package no.statkart.skif.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import org.hibernate.Session;
import org.hibernate.event.EventListeners;
import org.hibernate.impl.SessionImpl;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

/**
 * @author frehen
 */
public class EventListenersUtil {
    public static EventListeners cloneListeners(Session session) {
        EventListeners listeners = ((SessionImpl)session).getListeners();
        EventListeners newListeners = new EventListeners();
        newListeners.setLoadEventListeners(listeners.getLoadEventListeners().clone());
        newListeners.setReplicateEventListeners(listeners.getReplicateEventListeners());
        newListeners.setDeleteEventListeners(listeners.getDeleteEventListeners());
        newListeners.setAutoFlushEventListeners(listeners.getAutoFlushEventListeners());
        newListeners.setDirtyCheckEventListeners(listeners.getDirtyCheckEventListeners());
        newListeners.setFlushEventListeners(listeners.getFlushEventListeners());
        newListeners.setEvictEventListeners(listeners.getEvictEventListeners());
        newListeners.setLockEventListeners(listeners.getLockEventListeners());
        newListeners.setRefreshEventListeners(listeners.getRefreshEventListeners());
        newListeners.setInitializeCollectionEventListeners(listeners.getInitializeCollectionEventListeners());
        newListeners.setFlushEntityEventListeners(listeners.getFlushEntityEventListeners());
        newListeners.setSaveOrUpdateEventListeners(listeners.getSaveOrUpdateEventListeners());
        newListeners.setMergeEventListeners(listeners.getMergeEventListeners());
        newListeners.setPersistEventListeners(listeners.getPersistEventListeners());
        newListeners.setPersistOnFlushEventListeners(listeners.getPersistOnFlushEventListeners());
        newListeners.setSaveOrUpdateCopyEventListeners(listeners.getSaveOrUpdateCopyEventListeners());
        newListeners.setSaveEventListeners(listeners.getSaveEventListeners());
        newListeners.setUpdateEventListeners(listeners.getUpdateEventListeners());
        newListeners.setPostLoadEventListeners(listeners.getPostLoadEventListeners());
        newListeners.setPreLoadEventListeners(listeners.getPreLoadEventListeners());
        newListeners.setPreCollectionRecreateEventListeners(listeners.getPreCollectionRecreateEventListeners());
        newListeners.setPreCollectionRemoveEventListeners(listeners.getPreCollectionRemoveEventListeners());
        newListeners.setPreCollectionUpdateEventListeners(listeners.getPreCollectionUpdateEventListeners());
        newListeners.setPostDeleteEventListeners(listeners.getPostDeleteEventListeners());
        newListeners.setPostInsertEventListeners(listeners.getPostInsertEventListeners());
        newListeners.setPostUpdateEventListeners(listeners.getPostUpdateEventListeners());
        newListeners.setPostCollectionRecreateEventListeners(listeners.getPostCollectionRecreateEventListeners());
        newListeners.setPostCollectionRemoveEventListeners(listeners.getPostCollectionRemoveEventListeners());
        newListeners.setPostCollectionUpdateEventListeners(listeners.getPostCollectionUpdateEventListeners());
        newListeners.setPreDeleteEventListeners(listeners.getPreDeleteEventListeners());
        newListeners.setPreInsertEventListeners(listeners.getPreInsertEventListeners());
        newListeners.setPreUpdateEventListeners(listeners.getPreUpdateEventListeners());
        newListeners.setPostCommitDeleteEventListeners(listeners.getPostCommitDeleteEventListeners());
        newListeners.setPostCommitInsertEventListeners(listeners.getPostCommitInsertEventListeners());
        newListeners.setPostCommitUpdateEventListeners(listeners.getPostCommitUpdateEventListeners());
        return newListeners;
    }

    public static void addCurrentDatabaseEventListener(EventListeners listeners) {
        CurrentDatabaseEventListener currentDatabaseEventListener = new CurrentDatabaseEventListener();
        listeners.setPreInsertEventListeners(addElementToStartOfArray(listeners.getPreInsertEventListeners(),currentDatabaseEventListener));
        listeners.setPreUpdateEventListeners(addElementToStartOfArray(listeners.getPreUpdateEventListeners(),currentDatabaseEventListener));
        listeners.setPreDeleteEventListeners(addElementToStartOfArray(listeners.getPreDeleteEventListeners(),currentDatabaseEventListener));
        listeners.setPostInsertEventListeners(addElementToStartOfArray(listeners.getPostInsertEventListeners(),currentDatabaseEventListener));
        listeners.setPostUpdateEventListeners(addElementToStartOfArray(listeners.getPostUpdateEventListeners(),currentDatabaseEventListener));
        listeners.setPostDeleteEventListeners(addElementToStartOfArray(listeners.getPostDeleteEventListeners(),currentDatabaseEventListener));
    }

    public static <T> T[] addElementToStartOfArray(T[] listeners,T currentDatabaseEventListener) {
        T[] newListeners = (T[]) Array.newInstance(listeners.getClass().getComponentType(), listeners.length+1);
        System.arraycopy(listeners, 0, newListeners, 1, listeners.length);
        newListeners[0]=currentDatabaseEventListener;
        return newListeners;
    }

    public static void setEventListeners(Session session, EventListeners listeners) {
        SessionImpl sessionImpl = (SessionImpl) session;
        try {
            Field field = sessionImpl.getClass().getDeclaredField("listeners");
            field.setAccessible(true);
            field.set(session, listeners);
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        } catch (NoSuchFieldException e) {
            throw new ImplementationException(e);
        }
    }

    public static void installCurentDatabaseEventListener(Session session) {
        EventListeners listeners = cloneListeners(session);
        addCurrentDatabaseEventListener(listeners);
        setEventListeners(session, listeners);
    }

    public static CurrentDatabaseEventListener getCurentDatabaseEventListener(Session session) {
        return CurrentDatabaseEventListener.getForSession(session);
    }


}
