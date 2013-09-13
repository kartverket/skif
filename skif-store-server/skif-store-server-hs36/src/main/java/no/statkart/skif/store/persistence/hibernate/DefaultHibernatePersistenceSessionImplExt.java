package no.statkart.skif.store.persistence.hibernate;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import no.statkart.matrikkel.persistens.hibernate.bubbleref.BubbleRefIdPersister;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.EntityComponent;
import org.hibernate.EntityMode;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.collection.PersistentCollection;
import org.hibernate.engine.CascadeStyle;
import org.hibernate.engine.CascadingAction;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.id.Assigned;
import org.hibernate.impl.SessionImpl;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.*;

import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;

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

        final SessionImpl sessionImpl = (SessionImpl) session();
        final SessionFactoryImplementor sessionFactory = sessionImpl.getFactory();
        ClassMetadata classMetadata = sessionFactory.getClassMetadata(object.getClass());

        if (classMetadata==null) {
            Hibernate.initialize(object);
            return;
        }
        if (erAvTypeSomIkkeSkalInitialiseresVidere(classMetadata)) return;

        EntityPersister persister = (EntityPersister) classMetadata;
        Type[] types = persister.getPropertyTypes();
        Object[] values = persister.getPropertyValues(object, EntityMode.POJO);
        CascadeStyle[] cascadeStyles = persister.getPropertyCascadeStyles();

        for (int i = 0; i < types.length; i++) {
            Type type = types[i];
            if (type.isEntityType()) {
             // TODO: Opptimaliser Many-to-one-bubbleref trenger ikke initialiseres
                Hibernate.initialize(values[i]);

                if (cascadeStyles != null && cascadeStyles[i].doCascade(CascadingAction.SAVE_UPDATE)) {
                    ensureInitialized(values[i], initializedObjects);
                }
            } else if (type.isComponentType()) {
                ComponentType t = (ComponentType) type;
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
                    // Initialisert collectionen og hvert element. Element kan være av typen composite eller association.
                    // TODO: Nåværende implementasjon håndtere kun et nivå av composite-elementer. Generaliser ved behov
                    Collection col = (Collection) values[i];
                    if (!col.isEmpty()) {
                        CollectionPersister collectionPersister = sessionFactory.getCollectionPersister(((CollectionType)type).getRole());
                        if (collectionPersister.getElementType() instanceof CompositeType) {
                            CompositeType compositeType = (CompositeType) collectionPersister.getElementType();
                            for (Iterator iterator = col.iterator(); iterator.hasNext(); ) {
                                Object componentObject = iterator.next();
                                final Object[] propertyValues = compositeType.getPropertyValues(componentObject, sessionImpl);
                                for (int j = 0; j < propertyValues.length; j++) {
                                    ensureInitialized(propertyValues[j], initializedObjects);
                                }
                            }
                        } else if (collectionPersister.getElementType() instanceof  AssociationType) {
                            for (Iterator iterator = col.iterator(); iterator.hasNext(); ) {
                                Object o = (Object) iterator.next();
                                ensureInitialized(o, initializedObjects);
                            }
                        } else {
                            // No-op
                        }
                    }
                }
            }
        }
    }

    @Override
    protected CascadeStyle getCascadeStyle(Type componentType, int i){
        return ((CompositeType)componentType).getCascadeStyle(i);
    }

    @Override
    protected void setPropertyValues(Type componentType, Object component, Object[] properties) {
        ((CompositeType)componentType).setPropertyValues(component, properties, EntityMode.POJO);
    }


    @Override
    protected Object[] getPropertyValues(Type componentType, Object component) {
        return ((CompositeType)componentType).getPropertyValues(component, EntityMode.POJO);
    }

    @Override
    protected Type[] getSubtypes(Type componentType) {
        return ((CompositeType)componentType).getSubtypes();
    }

    protected boolean isSingleColumnType(Type type) {
        return type instanceof SingleColumnType;

    }

    @Override
    protected boolean collectOrphanEntityComponent(EntityType type, Object value, Object valueExisting, IdentityHashMap processedObjects, int nestingLevel, List<Multimap<Class<? extends EntityComponent>, EntityComponent>> orphanOneToOneEntityComponents) {
        Class typeClass = type.getReturnedClass();
        if (EntityComponent.class.isAssignableFrom(typeClass)) {
            EntityComponent componentExisting = (EntityComponent) valueExisting;
            EntityComponent component = (EntityComponent) value;
            if (componentExisting != null) {
                if (component==null || !componentExisting.getId().equals(component.getId())) {
                    // Eksisterende entity component har blitt satt til null eller byttet helt ut. Opprett entry i orphanOneToOneEntityComponents.
                    while(orphanOneToOneEntityComponents.size()<=nestingLevel) {
                        orphanOneToOneEntityComponents.add(HashMultimap.<Class<? extends EntityComponent>, EntityComponent>create());
                    }
                    orphanOneToOneEntityComponents.get(nestingLevel).put(componentExisting.getClass(), componentExisting);
                    return true;
                } else {
                   // Eksisterende entity og ny entity har samme id
                    return false;
                }
            } else {
                return value!=null;
            }
        }
        return false;
    }


    private boolean isNewEntityComponent(EntityMetamodel entityMetamodel, EntityComponent component) {
        // Dersom komponentid er assigned, så kan vi ikke detektere nye komponenter på denne måte . Slike id-er finnes i matrikkel historikk.
        // TODO: Bruke samme metodikk som Hibernate til å bestemme om en entity er ny. Hibernate kan bruke andre felter enn id, f.eks et version felt to å avgjøre om en entity er ny.
        // For å støtte batching kan det være at den nye komponenten allerede er inserted og derfor ikke lengre er ny
        return !(entityMetamodel.getIdentifierProperty().getIdentifierGenerator() instanceof Assigned) && component.getId()==null;
    }
}
