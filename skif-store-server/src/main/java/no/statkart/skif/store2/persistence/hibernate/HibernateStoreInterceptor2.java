package no.statkart.skif.store2.persistence.hibernate;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store2.BubbleId2;
import no.statkart.skif.store2.BubbleObject2;
import no.statkart.skif.store2.ReplicaVersion2;
import no.statkart.skif.store2.module.common.BubbleIdFactory2;
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
public class HibernateStoreInterceptor2 extends EmptyInterceptor {
    protected Logger logger = LoggerFactory.getLogger(HibernateStoreInterceptor2.class);

    protected ReplicaVersion2 replicaVersion = ReplicaVersion2.CURRENT;

    /**
     * Denne metoden retter opp id'en for entiteter hvor hibernate har brukt supertypens idklasse
     * @return false fordi vi ikke endrer på <code>state</code> til <code>entity</code>
     * @see {@link org.hibernate.Interceptor#onLoad(Object, java.io.Serializable, Object[], String[], org.hibernate.type.Type[])}
     */
    public boolean onLoad(Object entity, Serializable hibernateId, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        if (entity instanceof BubbleObject2) {
            BubbleObject2 bubbleEntity = (BubbleObject2) entity;
            String classname = bubbleEntity.getClass().getName();
            BubbleId2 bubbleId = (BubbleId2) bubbleEntity.getId();
            try {
                String idString;
                if(classname.contains("Impl")) {
                    idString = classname.substring(0, classname.indexOf("Impl")) + "IdImpl2";
                } else if(classname.endsWith("2")) {
                    idString = classname.substring(0, classname.length() - 1) + "Id2";
                } else {
                    idString = classname + "Id2";
                }
                Class classid = Class.forName(idString);
                if (classid != bubbleId.getClass()) {
                    Object value = bubbleId.getValue();
                    BubbleId2<?> newBubbleId = (BubbleId2<?>) BubbleIdFactory2.createInstance(classid, value, bubbleId.getReplicaVersion());
                    bubbleEntity.setId(newBubbleId);
                    if (logger.isDebugEnabled()) {
                        logger.debug("Endret id for " + classname + " fra: " + bubbleId + " til: " + newBubbleId);
                    }
                    return false;
                }
            } catch (ClassNotFoundException e) {
                throw new ImplementationException("Klassen " + classname + "Id finnes ikke i classpath");
            }
        }
        return false;
    }

    /**
     * Gjør ingenting
     */
    public Object instantiate(Class entitetClazz, Serializable id) throws CallbackException {
        sjekkReplicaVersjon(id);
        //Retur av null gjør at Hibernate bruker default oppførsel
        return null;
    }

    /**
     * Gjør ingenting
     */
    public boolean onFlushDirty(Object entity, Serializable id, Object[] currentState, Object[] previousState, String[] propertyNames, Type[] types) throws CallbackException {
        sjekkReplicaVersjon(id);
        return false;
    }

    /**
     * Gjør ingenting
     */
    public boolean onSave(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        sjekkReplicaVersjon(id);
        return false;
    }

    /**
     * Gjør ingenting
     */
    public void onDelete(Object entity, Serializable id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        sjekkReplicaVersjon(id);
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

    private void sjekkReplicaVersjon(Serializable id) {
        if (id instanceof BubbleId2) {
            if (((BubbleId2) id).getReplicaVersion() != replicaVersion) {
                throw new ImplementationException("id for instans har feil replicaVersjon", logger);
            }
        }
    }
}
