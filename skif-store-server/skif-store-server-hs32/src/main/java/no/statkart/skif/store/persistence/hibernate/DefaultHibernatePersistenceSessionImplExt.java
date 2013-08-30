package no.statkart.skif.store.persistence.hibernate;

import no.statkart.matrikkel.persistens.hibernate.bubbleref.BubbleRefIdPersister;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.EntityComponent;
import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.CascadeStyle;
import org.hibernate.engine.CascadingAction;
import org.hibernate.id.Assigned;
import org.hibernate.impl.SessionImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.AbstractComponentType;
import org.hibernate.type.EntityType;
import org.hibernate.type.NullableType;
import org.hibernate.type.Type;
import org.hibernate.util.EqualsHelper;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;

/**
 *
 * Denne klasse inneholder Hibernate 3.2.6 specifikk kode. Den skal integreres i superklassen
 * når SKIF støtter bubbleref for seneste versjon av hibernate
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class DefaultHibernatePersistenceSessionImplExt extends HibernatePersistenceSessionMasterImpl {

    public DefaultHibernatePersistenceSessionImplExt(HibernateSessionFactoryManager sessionFactoryManager) {
        super(sessionFactoryManager);
    }

    @Override
    protected boolean erAvTypeSomIkkeSkalInitialiseresVidere(ClassMetadata classMetadata) {
        return super.erAvTypeSomIkkeSkalInitialiseresVidere(classMetadata) || classMetadata instanceof BubbleRefIdPersister;
    }

    /**
     * Sørger for at objektets assosiasjoner er blitt initialisert med data fra databasen. Hvis en
     * assosiasjon er definert som cascade vil metoden bli kaldt rekursivt på de assosierte
     * objekter.
     *
     * @param object             a helt eller delvis initialisert objekt.
     * @param initializedObjects set av objekter som methoden allerede har initialisert
     * @throws org.hibernate.HibernateException
     *
     */
    protected void ensureInitialized(Object object, IdentityHashMap initializedObjects) throws HibernateException {
        if (object == null) return;

        if (initializedObjects.containsKey(object)) return;
        initializedObjects.put(object, null);

        ClassMetadata classMetadata = ((SessionImpl) session()).getFactory().getClassMetadata(object.getClass());

        if (erAvTypeSomIkkeSkalInitialiseresVidere(classMetadata)) return;

        EntityPersister persister = (EntityPersister) classMetadata;
        Type[] types = persister.getPropertyTypes();
        Object[] values = persister.getPropertyValues(object, EntityMode.POJO);
        CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();

        for (int i = 0; i < types.length; i++) {
            Type type = types[i];
            if (type.isEntityType()) {
                Hibernate.initialize(values[i]);

                if (cascadeStyles != null && cascadeStyles[i].doCascade(CascadingAction.SAVE_UPDATE)) {
                    ensureInitialized(values[i], initializedObjects);
                }
            } else if (type.isComponentType()) {
                AbstractComponentType t = (AbstractComponentType) type;
                Object component = values[i];
                if (component != null) {
                    Object[] componentProperties = t.getPropertyValues(component, EntityMode.POJO);
                    for (int j = 0; j < componentProperties.length; j++) {
                        // Hver property kan enten være et simple objekt (f.eks Long), complex objekt (f.eks Boundary) eller en collection
                        Object componentProperty = componentProperties[j];
                        Hibernate.initialize(componentProperty);
                        if (componentProperty instanceof PersistentCollection) {
                            // Initialiser hvert element
                            for (Iterator iterator = ((Collection) componentProperty).iterator(); iterator.hasNext(); ) {
                                Object o = iterator.next();
                                ensureInitialized(o, initializedObjects);
                            }
                        } else {
                            ensureInitialized(componentProperty, initializedObjects);
                        }
                    }
                }
            } else if (type.isAssociationType()) {
                Hibernate.initialize(values[i]);
                if (cascadeStyles != null && cascadeStyles[i].doCascade(CascadingAction.SAVE_UPDATE)) {
                    Collection col = (Collection) values[i];
                    for (Iterator iterator = col.iterator(); iterator.hasNext(); ) {
                        Object o = (Object) iterator.next();
                        ensureInitialized(o, initializedObjects);
                    }
                }
            }
        }
    }

    @Override
    protected CascadeStyle getCascadeStyle(Type componentType, int i){
        return ((AbstractComponentType)componentType).getCascadeStyle(i);
    }

    @Override
    protected void setPropertyValues(Type componentType, Object component, Object[] properties) {
        ((AbstractComponentType)componentType).setPropertyValues(component, properties, EntityMode.POJO);
    }


    @Override
    protected Object[] getPropertyValues(Type componentType, Object component) {
        return ((AbstractComponentType)componentType).getPropertyValues(component, EntityMode.POJO);
    }

    @Override
    protected Type[] getSubtypes(Type componentType) {
        return ((AbstractComponentType)componentType).getSubtypes();
    }

    @Override
    protected boolean isSingleColumnType(Type type) {
        return type instanceof NullableType;
    }

    protected void checkForReplacedOrStolenEntityComponentInV32(EntityType type, Object value, Object valueExisting) {
        Class typeClass = type.getReturnedClass();
        if (EntityComponent.class.isAssignableFrom(typeClass)) {
            AbstractEntityPersister persister = (AbstractEntityPersister) ((SessionImpl) session()).getFactory().getClassMetadata(type.getName());
            EntityMetamodel entityMetamodel = persister.getEntityMetamodel();
            if (valueExisting != null && value == null) {
                EntityComponent oldEntityComponent = (EntityComponent) valueExisting;
                throw new ImplementationException("Attempt at setting entity component to null. Entity class: " + typeClass.getName() + " Id:" + oldEntityComponent.getId(), logger);
            }

            // Dersom komponentid er assigned, så kan vi ikke detektere stjeling av komponenter. Slike id-er finnes i matrikkel historikk.
            if (!(entityMetamodel.getIdentifierProperty().getIdentifierGenerator() instanceof Assigned) && value != null) {
                final Long oldId;
                if (valueExisting != null) {
                    EntityComponent oldEntityComponent = (EntityComponent) valueExisting;
                    oldId = oldEntityComponent.getId();
                } else {
                    oldId = null;
                }
                EntityComponent entityComponent = (EntityComponent) value;
                final Object newId = entityComponent.getId();

                if (!EqualsHelper.equals(oldId, newId)) {
                    // TODO: Har midlertidig lagt til et ekstra sjekk som håndtere at komponenten nettopp har fått id i fixBatchingForObjectWithEntityComponents() og derfor ikke er null
                    if (!(oldId==null && newlyInsertedComponents.remove(entityComponent))) {
                        throw new ImplementationException("Attempt at replacing entity component. Entity class: " + typeClass.getName() + " New id:" + newId + ", Old id:" + oldId, logger);
                    }
                }
            }
        }
    }
}
