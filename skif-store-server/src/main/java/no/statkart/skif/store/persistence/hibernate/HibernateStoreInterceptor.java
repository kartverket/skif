package no.statkart.skif.store.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.module.common.BubbleIdFactory;
import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.Iterator;


/**
 * Interceptor for hibernate. Dvs. at metoder på denne klassen alltid blir kalt når objekter blir lastet, lagret og
 * fjernet gjennom en hibernate <code>Session</code>.
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class HibernateStoreInterceptor extends EmptyInterceptor {
    protected Logger logger = LoggerFactory.getLogger(HibernateStoreInterceptor.class);

    protected final SnapshotVersionSeed snapshotVersionSeed;

    public HibernateStoreInterceptor(SnapshotVersionSeed snapshotVersionSeed) {
        this.snapshotVersionSeed = snapshotVersionSeed;
    }

    /**
     * Denne metoden retter opp id'en for entiteter hvor hibernate har brukt supertypens idklasse
     * @return false fordi vi ikke endrer på <code>state</code> til <code>entity</code>
     * @see {@link org.hibernate.Interceptor#onLoad(Object, java.io.Serializable, Object[], String[], org.hibernate.type.Type[])}
     */
    public boolean onLoad(Object entity, Serializable hibernateId, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        if (entity instanceof BubbleObject) {
            BubbleObject bubbleEntity = (BubbleObject) entity;
            String classname = bubbleEntity.getClass().getName();
            BubbleId<?> bubbleId = bubbleEntity.getBubbleId();
            try {
                String idString;
                if(classname.contains("Impl")) {
                    idString = classname.substring(0, classname.indexOf("Impl")) + "IdImpl";
                } else {
                    idString = classname + "Id";
                }
                Class classid = Class.forName(idString);
                if (classid != bubbleId.getClass()) {
                    Object value = bubbleId.getValue();
                    BubbleId<?> newBubbleId = (BubbleId<?>) BubbleIdFactory.createInstance(classid, value, bubbleId.getSnapshotVersion());
                    bubbleEntity.setId(newBubbleId);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Endret id for " + classname + " fra: " + bubbleId + " til: " + newBubbleId);
                    }
                    return false;
                }
            } catch (ClassNotFoundException e) {
                throw new ImplementationException("Class " + classname + "Id was not found in classpath");
            }
        }
        return false;
    }

    /**
     * Gjør ingenting
     */
    public Object instantiate(Class entitetClazz, Serializable id) throws CallbackException {
        sjekkSnapshotVersjon(id);
        //Retur av null gjør at Hibernate bruker default oppførsel
        return null;
    }

    /**
     * Gjør ingenting
     */
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
        sjekkSnapshotVersjon(id);
        return false;
    }

    /**
     * Gjør ingenting
     */
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        sjekkSnapshotVersjon(id);
        return false;
    }

    /**
     * Gjør ingenting
     */
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        sjekkSnapshotVersjon(id);
    }

    /**
     * Gjør ingenting
     */
    public void preFlush(Iterator iterator) throws CallbackException {
    }

    /**
     * Gjør ingenting
     */
    public void postFlush(Iterator iterator) throws CallbackException {
    }

    /**
     * Gjør ingenting
     */
    public Boolean isUnsaved(Object o) {
        //Retur av null gjør at Hibernate bruker default oppførsel
        return null;
    }

    /**
     * Gjør ingenting
     */
    public int[] findDirty(Object o, Serializable serializable, Object[] objects, Object[] objects1, String[] strings, Type[] types) {
        //Retur av null gjør at Hibernate bruker default oppførsel
        return null;
    }

    private void sjekkSnapshotVersjon(Serializable id) {
        if (id instanceof BubbleId) {
            if (((BubbleId) id).getSnapshotVersion() != snapshotVersionSeed.get()) {
                throw new ImplementationException("Id for instance has wrong SnapshotVersion", logger);
            }
        }
    }
}
