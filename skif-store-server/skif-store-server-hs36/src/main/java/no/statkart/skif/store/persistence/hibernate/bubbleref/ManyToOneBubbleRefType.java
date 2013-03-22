package no.statkart.skif.store.persistence.hibernate.bubbleref;

import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.module.common.BubbleIdFactory;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.engine.Mapping;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.Joinable;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.type.ManyToOneType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeFactory;
import org.hibernate.util.EqualsHelper;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.SQLException;


/**
 * A type that represents a many-to-one association mapping that references an entity via a bubble
 * reference. That is, uses the id of the entity instead of a reference to the entity itself.
 */
public class ManyToOneBubbleRefType extends ManyToOneType {
    private final Class returnedIdClass;
    private final String bubbleEntityName;

    public ManyToOneBubbleRefType(TypeFactory.TypeScope scope,String referencedEntityName, String uniqueKeyPropertyName, boolean lazy, boolean unwrapProxy, boolean isEmbeddedInXML, boolean ignoreNotFound, boolean isLogicalOneToOne) {
        super(scope,referencedEntityName + "Id", uniqueKeyPropertyName, lazy, unwrapProxy, isEmbeddedInXML, ignoreNotFound, isLogicalOneToOne);
        bubbleEntityName = referencedEntityName;
        String idclassName = referencedEntityName + "Id";
        try {
            returnedIdClass = Class.forName(idclassName);
            //returnedIdClassConstructor = returnedIdClass.getConstructor(new Class[]{long.class});
        } catch (ClassNotFoundException e) {
            throw new MappingException("Could not find Id class " + idclassName + " for class " + referencedEntityName, e);
        }
    }

    /**
     * Returns the identifier instead of the actual object
     *
     * NB: Denne metode krever BubbleRef endringer til hibernate for å kompilere. Opprinnelig metode har final keyword
     */
    protected Object resolveIdentifier(Serializable id, SessionImplementor session) throws HibernateException {
        //super.resolveIdentifier(id, session);  // must not call as this would create unwanted proxy for object

        BubbleId genericId = (BubbleId) id;
        BubbleId newId = BubbleIdFactory.createInstance(returnedIdClass, genericId.getValue(), genericId.getSnapshotVersion());
        return newId;
    }

    /**
     * Returns false if old does not equals new.
     *
     * @param old     identifier of the original assocated object
     * @param current identifier of current assocated object
     * @param session
     * @return false if old does not equals new.
     */
    public boolean isDirty(Object old, Object current, SessionImplementor session) throws HibernateException {
        return !EqualsHelper.equals(old, current);
    }

    /**
     * Writes the value of the assocated objects identifier to the prepared statement
     *
     * @param st
     * @param value   identifier of the assocated object
     * @param index
     * @param session
     * @throws org.hibernate.HibernateException
     *
     * @throws java.sql.SQLException
     */
    public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session) throws HibernateException, SQLException {
        Type type = getIdentifierOrUniqueKeyType(session.getFactory());
        type.nullSafeSet(st, value, index, session);
    }

    /**
     * Returns the IdClass of the assocated class defined by this mapping.
     */
    public Class getReturnedIdClass() {
        return returnedIdClass;
    }

    Type getIdentifierType(Mapping factory) {
        return factory.getIdentifierType(bubbleEntityName);
    }

    public Joinable getAssociatedJoinable(SessionFactoryImplementor factory) throws MappingException {
        return (Joinable) factory.getEntityPersister(bubbleEntityName);
    }


    public String toLoggableString(Object value, SessionFactoryImplementor factory) {
        if (value == null) {
            return "null";
        }

        EntityPersister persister = factory.getEntityPersister(getAssociatedEntityName());
        StringBuffer result = new StringBuffer().append(getAssociatedEntityName());

        if (persister.hasIdentifierProperty()) {
            final EntityMode entityMode = persister.guessEntityMode(value);
            final Serializable id;
            if (entityMode == null) {
                if (isEmbeddedInXML) {
                    throw new ClassCastException(value.getClass().getName());
                }
                id = (Serializable) value;
            } else {
                id = getIdentifier(value, persister, entityMode);
            }

            result.append('#')
                    .append(persister.getIdentifierType().toLoggableString(id, factory));
        }

        return result.toString();
    }

    private static Serializable getIdentifier(Object object, EntityPersister persister, EntityMode entityMode) {
        if (object instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy) object;
            LazyInitializer li = proxy.getHibernateLazyInitializer();
            return li.getIdentifier();
        } else {
            return persister.getIdentifier(object, entityMode);
        }
    }


    /**
     * Returns a string representation of <code>value</code> in the format "classname#value".
     *
     * The ManyToOneBubbleRefType must have it own implementation, because the default implemnetation
     * in EntityType will generate an exception. This is because the ManyToOneBubbleRefType fools
     * hibernate into beliving that the assocated type is a of a ManyToOneBubbelRefType is a MatrikkelBubbleObject and not at
     * a MatrikkelBubbleObjectId. However, in truth <code>value</code> is a MatrikkelBubbleObjectId.
     */
/*
   public String toString(Object value, SessionFactoryImplementor factory) throws HibernateException {
      ClassPersister persister = factory.getPersister(getAssociatedClass());
		if (value==null) return "null";
		StringBuffer result = new StringBuffer()
			.append( StringHelper.unqualify( HibernateProxyHelper.getClass(value).getName() ) );
		if ( persister.hasIdentifierProperty() ) {
         MatrikkelBubbleId id = (MatrikkelBubbleId) value;

			result.append('#')
				.append( id.getValue() );
		}
		return result.toString();
   }
*/

}