package no.statkart.skif.store.persistence.hibernate.bubbleref;

import no.statkart.skif.store.BubbleObject;
import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Mappings;
import org.hibernate.engine.Mapping;
import org.hibernate.id.factory.IdentifierGeneratorFactory;
import org.hibernate.mapping.Collection;
import org.hibernate.mapping.Component;
import org.hibernate.mapping.ManyToOne;
import org.hibernate.mapping.MetaAttribute;
import org.hibernate.mapping.OneToMany;
import org.hibernate.mapping.PersistentClass;
import org.hibernate.mapping.Property;
import org.hibernate.mapping.Value;
import org.hibernate.persister.entity.JoinedSubclassEntityPersister;
import org.hibernate.type.ComponentType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * A specialized version of the Hibernate {@link org.hibernate.cfg.Configuration} class that parses Hibernate mapping
 * documents and maps Hibernate associations annotated with the <tt>&lt;meta
 * attribute="bubble-ref"/&gt;</tt> meta tag into bubble references. By using meta tags to specify
 * bubble references it is not necessary to extend the Hibernate mapping syntax or change the
 * Hibernate classes that parser the Hiberante mapping documents.
 * <p/>
 * Bubble references solves the problem of partitioning a domain model into disconnected object
 * graphs (bubbles) that can be transferred accross the network independently of each other. Bubble
 * references differ from ordinary Java references, in that a bubble reference points to the id of
 * the object instead of the object itself. Bubble references are implemented in Hibernate in such a
 * way that their usage is transparent to the Hibernate Query Language. That is, bubble reference
 * associations can be traversed and eager loaded using Hibernate Query Language dot notation. For
 * for instance: "from Child c where c.parent.age=25", "from Child c where c.parent=:someparent",
 * and "from Child c join fecth c.parent where c.age=3".
 * <p/>
 * Hibernate associations are usually mapped to Java objects via Java references. For instance, if a
 * class Child defines an association "parent" to a class Parent, then Child would have a member
 * variable of type Parent that holds the assocated object and a getter and setter method pair
 * called <tt>getParent()</tt> and <tt>setParent()</tt> that Hibernate uses for maintaining the
 * association (unless direct field access is used). Similarly, if the association represents a
 * collection of objects then the object holding the association will have a {@link java.util.Set}
 * member variable and getter and setter method pair that uses a {@link java.util.Set} as argument
 * and return value. The elements of the set will be references to the assocated Java objects.
 * <p/>
 * When a Hibernate association is defined as a bubble reference then Hibernate will pass the id of
 * the associated object to the holder of the association instead of the object itself. That is, the
 * holder of the association must define a getter and setter pair that uses the id class of the
 * associated object. Furthermore (by convension), the postfix "<tt>Id</tt>" or "<tt>Ids</tt>" must
 * be added to the names of the getter and setter methods depending on whether the association
 * defines a reference to a single object or a collection of objects.
 * <p/>
 * Thus, if the association between Parent and Child in the example above is defined as a bubble
 * reference, then class Parent must define a member variable of type ParentId and a getter and
 * setter pair <tt>ParentId getParentId()</tt> and <tt>void setParentId(ParentId id)</tt>
 * <p/>
 * Hibernate supports lazy loading via a proxy framework that makes the proxies appear as if they
 * are the actual objects. Initally a proxy will only hold the id of the object it represents, but
 * when a proxy is accessed for data it will lookup or load the actual object and forward all calls
 * to it. Proxies comes with a number of disadvantages with respect to casting of subclasses and
 * uniqueness (see Hibernate documentation for details).
 * <p/>
 * In the implementation of bubble references it is vital that all bubble should defined as lazy
 * loaded, because otherwise the whole object will be loaded whenever it is referenced. However,
 * since proxies in general are undesirable,  great care has been taken to ensure that bubbl
 * references do not create proxies such that when a bubble reference is created for a lazy loaded
 * object, only the Id class will be instantiated. This is different from how Hibernate normally
 * handles lazy loading which always cause Hibernate to create a proxy fro reference objects. Bubble
 * references can avoid creating proxies since only the id of the object is needed.
 * <p/>
 * It might be that the bubble refereces could work without requiring them to be lazy loaded,
 * however then the following problem must also be addressed. During updates and deletes we must be
 * able to figure out if a different object instance of the same object is already loaded by the
 * hibernate session and evict it if this is the case. This can occur if the object to delete/update
 * has been read in an earlier session and sent to the client. Since all bubbles are lazy loaded
 * this problem is solved by asking hibernate to load the object. Since the object is lazy loaded
 * this will create a proxy if the object is not already loaded (hence no database access). If
 * instance returned is different from the one we want to delete/update then we simply evict the undesired
 * instance from the hibernate session before delete/update is called.
 * <p/>
 * <p/>
 * <strong>Example</strong>
 * <p/>
 * This example defines a bi-directional association between between Parent and Child.
 * <pre>
 * ...
 * &lt;hibernate-mapping&gt;
 *   &lt;class name="somepackage.Child lazy="true"&gt;
 *       &lt;id name="bubbleId" column="id" type="no.statkart.matrikkel.persistens.bok.type.BubbleIdType"&gt;
 *           &lt;generator class="assigned"/&gt;
 *       &lt;/id&gt;
 *       ...
 *       &lt;many-to-one
 *           name="parent"
 *           column="parentId"
 *           class="somepackage.Parent"
 *           foreign-key="FK_CHILD_PARENTID"&gt;
 *           &lt;meta attribute="bubble-ref"/&gt;   &lt;!-- This makes it a bubble-ref --&gt;
 *       &lt;/many-to-one&gt;
 *   &lt;/class&gt;
 *   &lt;class name="somepackage.Parent lazy="true"&gt;
 *       &lt;id name="bubbleId" column="id" type="no.statkart.matrikkel.persistens.bok.type.BubbleIdType"&gt;
 *           &lt;generator class="assigned"/&gt;
 *       &lt;/id&gt;
 *       &lt;set name="children" inverse="true"&gt;
 *          &lt;meta attribute="bubble-ref"/&gt;
 *          &lt;key column="parentId" foreign-key="FK_CHILD_PARENTID"/&gt;
 *          &lt;one-to-many class="somepackage.Child"/&gt;
 *       &lt;/set&gt;
 *       ...
 *   &lt;/class&gt;
 * &lt;/hibernate-mapping&gt;
 * </pre>
 *
 * @author Henrik Fredholm
 */
public class BubbleRefConfiguration extends Configuration {
    private static Logger log = LoggerFactory.getLogger(BubbleRefConfiguration.class);

    public SessionFactory buildSessionFactory() throws HibernateException {
//        configureManyToOneBubbleMappings();
        return super.buildSessionFactory();
    }

    protected void reset() {
        super.reset();
        classes = new LinkedHashMap<>();
    }

    public Mapping buildMapping() {
        return new BubbleRefMapping() {
            @Override
            public IdentifierGeneratorFactory getIdentifierGeneratorFactory() {
                return BubbleRefConfiguration.this.getIdentifierGeneratorFactory();
            }

            /**
             * Returns the identifier type of a mapped class
             */
            public Type getIdentifierType(String persistentClass) throws MappingException {
                PersistentClass pc = (classes.get(persistentClass));
                if (pc == null) {
                    throw new MappingException("persistent class not known: " + persistentClass);
                }
                return pc.getIdentifier().getType();
            }

            public String getIdentifierPropertyName(String persistentClass) throws MappingException {
                final PersistentClass pc = classes.get(persistentClass);
                if (pc == null) {
                    throw new MappingException("persistent class not known: " + persistentClass);
                }
                if (!pc.hasIdentifierProperty()) {
                    return null;
                }
                return pc.getIdentifierProperty().getName();
            }

            public Type getReferencedPropertyType(String persistentClass, String propertyName) throws MappingException {
                final PersistentClass pc = classes.get(persistentClass);
                if (pc == null) {
                    throw new MappingException("persistent class not known: " + persistentClass);
                }
                Property prop = pc.getReferencedProperty(propertyName);
                if (prop == null) {
                    throw new MappingException(
                            "property not known: " +
                                    persistentClass + '.' + propertyName
                    );
                }
                return prop.getType();
            }

            public PersistentClass getClassMapping(String entityName) {
                return BubbleRefConfiguration.this.getClassMapping(entityName);
            }
        };
    }

    // This method may be called many times!!
    protected void secondPassCompile() throws MappingException {
        super.secondPassCompile();
        configureSecondPassBubbleMappings();
    }

    /**
     * Processes all mappings and change all ManyToOne properties that has a meta attribute "bubble-ref" into
     * ManyToOneBubbleRef properties.
     */
    public void configureManyToOneBubbleMappings() throws MappingException {
        Mappings mappings = createMappings();
        for (Iterator iterator = getClassMappings(); iterator.hasNext(); ) {
            PersistentClass rootClass = (PersistentClass) iterator.next();
            configureManyToOneBubbleMappings(rootClass, mappings);
        }
    }

    /**
     * Prosesserer alle ManyToOne properties i klassen
     */
    private void configureManyToOneBubbleMappings(PersistentClass persistentClass, Mappings mapping) throws MappingException {
        if (log.isDebugEnabled()) {
            log.debug("Processing bubble-refs for class: " + persistentClass.getEntityName());
        }

        //Hvis identifier er composite blir det en component som vi må spesialbehandle i tilfelle det finnes many-to-one boblekoblinger der
        if(persistentClass.getIdentifier() instanceof Component) {
            configureManyToOneBubbleMappingsForComponent((Component) persistentClass.getIdentifier(), persistentClass, mapping);
        }

        for (Iterator iterator = persistentClass.getPropertyClosureIterator(); iterator.hasNext(); ) {
            Property prop = (Property) iterator.next();
            MetaAttribute attrib = prop.getMetaAttribute("bubble-ref");
            if (attrib != null && prop.getValue() instanceof ManyToOne) {
                configureManyToOneBubbleMapping(prop, persistentClass, mapping);
            } else if (prop.getValue() instanceof Component) {
                configureManyToOneBubbleMappingsForComponent((Component) prop.getValue(), persistentClass, mapping);
            } else if (prop.getValue() instanceof Collection) {
                configureManyToOneBubbleMappingsForCollection((Collection) prop.getValue(), persistentClass, mapping);
            }
        }
    }

    /**
     * Ser etter ManyToOne som skal gjøres om til ManyToOneBubbleRef inni collections (composite-element)
     */
    private void configureManyToOneBubbleMappingsForCollection(Collection collection, PersistentClass persistentClass, Mappings mappings) {
        Value element = collection.getElement();
        if (element instanceof Component) {
            configureManyToOneBubbleMappingsForComponent((Component) element, persistentClass, mappings);
        }
    }

    /**
     * Gjør om en ManyToOne property til en ManyToOneBubbleRef property  mapping
     */
    private void configureManyToOneBubbleMapping(Property prop, PersistentClass persistentClass, Mappings mappings) throws MappingException {
        ManyToOne value = (ManyToOne) prop.getValue();
        if (log.isDebugEnabled()) {
            log.debug("Remapped property: " + prop.getName() + " as bubble-ref for class " + persistentClass.getMappedClass().getName());
        }

        try {
            value = new ManyToOneBubbleRef(mappings, value);
            prop.setValue(value);
        } catch (MappingException e) {
            throw new RuntimeException(e);
        }

        boolean isPropertyAccessor = prop.getPropertyAccessorName().equals("property");
        boolean isFieldAccessor = prop.getPropertyAccessorName().equals("field");
        boolean isBubbleRefAccessor = prop.getPropertyAccessorName().equals(BubbleRefAccessor.class.getName());
        boolean isBubbleRefFieldAccessor = prop.getPropertyAccessorName().equals(BubbleRefFieldAccessor.class.getName());
        if (isFieldAccessor) {
            prop.setPropertyAccessorName(BubbleRefFieldAccessor.class.getName());
        } else if (isPropertyAccessor) {
            prop.setPropertyAccessorName(BubbleRefAccessor.class.getName());
        } else {
            if (!isBubbleRefAccessor && !isBubbleRefFieldAccessor) {
                throw new MappingException("Cannot use the specified custom accessor for ManyToOne bubble mapping \"" + prop.getName() + "\" for class " + persistentClass.getMappedClass().getName());
            }
        }

        if (!prop.getCascade().equals("none"))
            throw new MappingException("Cannot specify cascade different from \"none\" for ManyToOne bubble mapping \"" + prop.getName() + "\" for class " + persistentClass.getMappedClass().getName());

    }

    /**
     * Gjør om en ManyToOne property til en ManyToOneBubbleRef property mapping
     */
    private void configureManyToOneBubbleMappingsForComponent(Component component, PersistentClass persistentClass, Mappings mapping) throws MappingException {
        // Components har et eget array for property type som også må oppdateres når en property endres.
        // Dvs. det holder ikke blot å endre på propertien og dens type. Property type arrayet må også
        // oppdateres.

        ComponentType type = (ComponentType) component.getType();
        int i = 0;
        for (Iterator iterator = component.getPropertyIterator(); iterator.hasNext(); i++) {
            Property prop = (Property) iterator.next();
            MetaAttribute attrib = prop.getMetaAttribute("bubble-ref");
            if (attrib != null && prop.getValue() instanceof ManyToOne) {
                configureManyToOneBubbleMapping(prop, persistentClass, mapping);
                type.getSubtypes()[i] = prop.getValue().getType(); // oppdater property array med ny type
            } else if (prop.getValue() instanceof Component) {
                configureManyToOneBubbleMappingsForComponent((Component) prop.getValue(), persistentClass, mapping);
            }
        }
    }

    /**
     * Prosesserer alle mappings og gjør om  på collection og component properties som har satt <tt>&lt;meta
     * attribute="bubble-ref"/&gt;</tt> slik at mappingen blir riktig. Dette må gjøre etter at Hibernate har
     * generert opp initiell mapping fordi Hibernate ikke nok plugg-in punkter til å gjøre det under den
     * initielle mappingen.
     * <p/>
     * Oppretter deretter egne mapping klasser for BubbleId'er.
     */
    protected void configureSecondPassBubbleMappings() throws MappingException {
        Mappings mappings = createMappings();
        for (Iterator<PersistentClass> iterator = getClassMappings(); iterator.hasNext(); ) {
            PersistentClass persistentClass = iterator.next();
            configureSecondPassBubbleMappings(persistentClass, mappings);
        }

        // We need to add new persistent classes to the mapping while we iterate.
        // Hence we need a copy of the collection we are iterating over.
        java.util.List<PersistentClass> persistentClasses = new ArrayList<>();
        for (Iterator<PersistentClass> iterator = getClassMappings(); iterator.hasNext(); ) {
            persistentClasses.add(iterator.next());
        }
        for (PersistentClass persistenceClass : persistentClasses) {
            defineIdClassMapping(persistenceClass, mappings);
        }
    }

    /**
     * Går igjennom alle property mappings for en persistent klasse og fixer mappingen for properties som bruker bubble-ref
     * hvor det er nødvendig. Dvs hvor det er brukt collections eller components.
     *
     * @param persistentClass class
     * @param mappings mappings
     * @throws org.hibernate.MappingException
     */
    private void configureSecondPassBubbleMappings(PersistentClass persistentClass, Mappings mappings) throws MappingException {
        for (Iterator iterator = persistentClass.getPropertyClosureIterator(); iterator.hasNext(); ) {
            // Prosesser hver property i mapping
            Property prop = (Property) iterator.next();
            configurePropertyBubbleMappings(persistentClass, mappings, prop);
        }

    }

    /**
     * Går igjennom mappings for en enkelt property i persistent klasse og fixer mappinger som bruker bubble-ref
     * hvor det er nødvendig. Siden en property kan være en komponent, som igjen inneholder properties er denne
     * metode rekursiv.
     *
     * @param persistentClass class
     * @param mappings mappings
     * @param prop property
     */
    private void configurePropertyBubbleMappings(PersistentClass persistentClass, Mappings mappings, Property prop) {
        if (prop.getValue() instanceof Collection) {
            MetaAttribute attrib = prop.getMetaAttribute("bubble-ref");
            if (attrib != null) {
                // Collection består av bubble-ref referanser
                configureCollectionBubbleMapping(prop, persistentClass, mappings);
            } else {
                // Sjekk om collection inneholder component mapping
                Collection c = (Collection) prop.getValue();
                if (c.getElement() instanceof Component) {
                    // Prosesser hver property i component mapping
                    Component component = (Component) c.getElement();
                    configureManyToOneBubbleMappingsForComponent(component, persistentClass, mappings);
                }
            }
        } else if (prop.getValue() instanceof Component) {
            // Prosesser hver property i component mapping
            Component component = (Component) prop.getValue();
            for (Iterator componentIterator = component.getPropertyIterator(); componentIterator.hasNext(); ) {
                Property componentProperty = (Property) componentIterator.next();
                configurePropertyBubbleMappings(persistentClass, mappings, componentProperty);
            }
        }
    }

    private void configureCollectionBubbleMapping(Property prop, PersistentClass persistentClass, Mappings mappings) throws MappingException {
        Collection value = (Collection) prop.getValue();
        try {
            if (value.getElement() instanceof ManyToOne)
                value.setElement(new ManyToOneBubbleRef(mappings, (ManyToOne) value.getElement()));
            else if (value.getElement() instanceof OneToMany)
                value.setElement(new OneToManyBubbleRef(mappings, (OneToMany) value.getElement()));

        } catch (MappingException e) {
            throw new RuntimeException(e);
        }
        boolean isPropertyAccessor = prop.getPropertyAccessorName().equals("property");
        boolean isFieldAccessor = prop.getPropertyAccessorName().equals("field");
        boolean isBubbleRefCollectionAccessor = prop.getPropertyAccessorName().equals(BubbleRefCollectionAccessor.class.getName());
        boolean isBubbleRefCollectionFieldAccessor = prop.getPropertyAccessorName().equals(BubbleRefCollectionFieldAccessor.class.getName());

        if (isPropertyAccessor) {
            prop.setPropertyAccessorName(BubbleRefCollectionAccessor.class.getName());
        } else if (isFieldAccessor) {
            prop.setPropertyAccessorName(BubbleRefCollectionFieldAccessor.class.getName());
        } else {
            if (!isBubbleRefCollectionAccessor && !isBubbleRefCollectionFieldAccessor)
                throw new MappingException("Cannot specify custom accessor " + prop.getPropertyAccessorName() + " for bubble-ref mapping \"" + prop.getName() + "\" for class " + persistentClass.getMappedClass().getName());
        }

        if (!prop.getCascade().equals("none"))
            throw new MappingException("Cannot specify cascade different from \"none\" for bubble-ref mapping \"" + prop.getName() + "\" for class " + persistentClass.getMappedClass().getName());
    }

    /**
     * Definere en mapping for id klassen dersom den ikke allerede finnes. Dette er nødvendig fordi
     * id klassen må ha en egen persister. Normalt er det kun entities som trenger en persister, men
     * siden id klassen opptreder som plassholder for den egentlige klassen må vi også ha en for
     * denne. Dette gjøre ved å definere en mapping. Når {@link #buildSessionFactory()} kjøres vil
     * det da bli opprettet en mapping for klassen.
     */
    private void defineIdClassMapping(PersistentClass c, Mappings mappings) throws MappingException {
        Class mappedClass = c.getMappedClass();
        if (mappedClass == null) return;

// Is the mapped class a BubbleObject?
        if (BubbleObject.class.isAssignableFrom(mappedClass)) {
            Class idClass = null;
            String name = c.getMappedClass().getName() + "Id";
            try {
                idClass = Class.forName(name);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }

            // Define a mapping class for the Id class, unless it has already been define in the mapping file
            if (mappings.getClass(name) == null) {
                if (c.getEntityPersisterClass() == JoinedSubclassEntityPersister.class) {
                    // BubbleRefs til klasser som er mappet som joined-subclass er pt ikke støttet. Trenger derfor ikke å opprette persister
                } else {
                    PersistentClass p = new BubbleRefIdClass(c);
                    mappings.addClass(p);
                }
            }
        }
    }
}