package no.statkart.skif.store2;

import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ReflectionException;
import no.statkart.skif.store.BubbleIdInterface;
import no.statkart.skif.store.ReplicaVersion;
import no.statkart.skif.util.Reflection;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class AbstractBubbleId2<T extends BubbleObject2> implements BubbleId2<T>, Serializable {
    private Object value;

    /**
     * Allows the object to exist in multiple versions in Store.
     */
    private ReplicaVersion replicaVersion = ReplicaVersion.CURRENT;

    /**
     * Helper class that holds meta info for each subtype of this class. The meta info takes
     * time to calculate and is therefore shared by all instances of a particular class.
     * <p/>
     * Thisclass should not be Serializable
     */
    private static class TypeInfo {
        String typeName;
        Class type;
        Class baseType;

        public TypeInfo(String typeName, Class type, Class baseType) {
            this.typeName = typeName;
            this.type = type;
            this.baseType = baseType;
        }
    }

    // static Map of meta info for each BubbleId class
    transient private static Map<Class, TypeInfo> typeInfoMap = new ConcurrentHashMap(100);

    // Cached meta info for this instance.
    transient private TypeInfo typeInfo;

    // Cache the class for faster access. This actually matters
    protected Class clazz = getClass();

    public static <I extends BubbleIdInterface<?>> I createInstance(Class<I> idClass, long idValue) {
        return createInstance(idClass, new Long(idValue), ReplicaVersion.CURRENT);
    }

    public static <I extends BubbleIdInterface<?>> I createInstance(Class<I> idClass, long idValue, ReplicaVersion replicaVersion) {
        return createInstance(idClass, new Long(idValue), replicaVersion);
    }

    public static <I extends BubbleIdInterface<?>> I createInstance(Class<I> idClass, Object idValue, ReplicaVersion replicaVersion) {
         I id = null;
        try {
            Constructor<I> ctor = idClass.getDeclaredConstructor(idValue.getClass(), ReplicaVersion.class);
            ctor.setAccessible(true);
            id = ctor.newInstance(idValue, replicaVersion);
            return (I) id.resolveInstance();
        } catch (InstantiationException e) {
            throw new ImplementationException(e);
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        } catch (NoSuchMethodException e) {
            throw new ImplementationException(e);
        } catch (InvocationTargetException e) {
            throw new ImplementationException(e);
        }
    }

    public BubbleId2<T> resolveInstance() {
        return this;
    }

    protected AbstractBubbleId2() {
    }

    protected AbstractBubbleId2(Object value) {
        this.value = value;
    }

    protected AbstractBubbleId2(Object value, ReplicaVersion version) {
        this.value = value;
        this.replicaVersion = version;
    }


    public T getObject(Object session) {
        return null;
    }

    public Object getValue() {
        return value;
    }

    public ReplicaVersion getReplicaVersion() {
        return replicaVersion;
    }

    public AbstractBubbleId2<T> fromIdValue(Object value, ReplicaVersion replicaVersion) {
        try {
            AbstractBubbleId2 newId = getClass().newInstance();
            newId.value = value;
            newId.replicaVersion=replicaVersion;
            return newId;
        } catch (InstantiationException e) {
            throw new ImplementationException(e);
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        }
    }

    public AbstractBubbleId2<T> asReplicaVersion(ReplicaVersion replicaVersion) {
        if (this.replicaVersion == replicaVersion) return this;
        return fromIdValue(getValue(), replicaVersion);
    }

    public AbstractBubbleId2<T> asReplicaVersionOld() {
        return asReplicaVersion(ReplicaVersion.OLD);
    }

    public AbstractBubbleId2<T> asReplicaVersionCurrent() {
        return asReplicaVersion(ReplicaVersion.CURRENT);
    }


    private TypeInfo getTypeInfo() {
        TypeInfo typeInfo = typeInfoMap.get(clazz);
        if (typeInfo == null) {
            typeInfo = new TypeInfo(calcTypeName(), calcType(), calcBaseType());
            typeInfoMap.put(clazz, typeInfo);
        }
        return typeInfo;
    }

    @Override
    public final int hashCode() {
        return value.hashCode();
    }

    public boolean equals(Object id) {
        return id instanceof AbstractBubbleId2 && equals((AbstractBubbleId2) id);
    }

    /**
     * Må ha spesiell implementasjon. Årsaken til dette er at vi må håndtere id'er for hierarkier i modellen
     * spesielt. I vår modell kan vi ha hierarkier i domenemodellen der super-klassen er en abstract klasse.
     * Men hibernate gjør at når vi laster et objekt av klasse <code>Sub</code> som er subklasse av
     * <code>Super</code>, vil objektet kunne få et id-objekt som er av id-typen til 'Super'.
     * <p/>
     * <p>Eksempel: en grunneiendom får id av type MatrikkelenhetId i stedet for GrunneiendomId. I praksis er en id
     * av type MatrikkelenhetId lik en med type GrunneiendomId, dersom <code>value</code> er lik. I motsetning til en
     * GateadresseId og en GrunneiendomId som har samme <code>value</code> (gitt at id-verdier evt. kun er unike
     * innenfor hver klasse/hierarki).
     *
     * @param id en annen Id
     * @return true dersom dette objektet er av samme type og har samme <code>value</code> som parameteren <code>id</code>
     *         Objektene er av samme type også dersom dette objektet er en sub-type av <code>id</code> eller omvendt.
     */
    protected boolean equals(AbstractBubbleId2 id) {
        // Denne implementason håndter subtyper: eg. AdresseId er lik GateAdresseId og MatrikkelAdresseId dersom
        // value og replicaVersion er lik, men en GateAdresseId kan aldrig være lik MatrikkeladresseId
        if (id == null) return false;
        return value.equals(id.value) && replicaVersion == id.replicaVersion && compatible(id);
    }


    final public boolean equalsIgnoreReplicaVersion(AbstractBubbleId2 id) {
        // Denne implementason håndter subtyper: eg. AdresseId er lik GateAdresseId og MatrikkelAdresseId dersom
        // value og replicaVersion er lik, men en GateAdresseId kan aldrig være lik MatrikkeladresseId
        if (id == null) return false;
        return value.equals(id.value) && compatible(id);
    }

    final private boolean compatible(AbstractBubbleId2 id) {
        return (clazz == id.clazz || clazz.isAssignableFrom(id.clazz) || id.clazz.isAssignableFrom(clazz));
    }

    /**
     * Comparable is implemented to support consistent ordering of id collections.
     * This is useful even if most id's are not instended for humans.
     */
    public int compareTo(Object object) {
        return getStringValue().compareTo(((AbstractBubbleId2) object).getStringValue());
    }

    /**
     * <p>Returns the type of this id.
     * The type is by definition the short class
     * name of this class preceeding "Id".
     * So if you have an id class org.foo.BarId,
     * the type name returned is "Bar".
     * The type of a non-subclassed id is the empty string.</p>
     * <p/>
     * <p>A different naming scheme can be facilitated by
     * overriding this method (and probably createTypeInstance
     * and getType as well).</p>
     */
    public String getTypeName() {
        if (typeInfo == null) {
            typeInfo = getTypeInfo();
        }
        return typeInfo.typeName;
    }

    public String calcTypeName() {
        String cachedTypeName;
        String className = clazz.getName();
        int lastDotIndex = className.lastIndexOf(".");
        int dollarIndex = className.lastIndexOf("$"); // For inner classes
        int cutIndex = Math.max(lastDotIndex, dollarIndex);
        if (cutIndex < 0) cachedTypeName = className.substring(0, className.length() - 2);
        else cachedTypeName = className.substring(cutIndex + 1, className.length() - 2);
        return cachedTypeName;
    }

    /**
     * Returns the type of this id as a class.
     * This is not the class of this id, but of the type it is an id of.
     *
     * @return the class
     */
    public Class getType() {
        if (typeInfo == null) {
            typeInfo = getTypeInfo();
        }
        return typeInfo.type;
    }

    public Class calcType() {
        Class type;
        String className = null;
        try {
            className = clazz.getName();
            // TODO: temp fix XIdImpl2 -> XImpl2, i stedet for XId -> X
            if (className.endsWith("IdImpl2")) {
                int cutIndex = className.length() - 7;
                int dollarIndex = className.lastIndexOf("$"); // For inner classes
                if (dollarIndex > 0) cutIndex = dollarIndex;
                className = className.substring(0, cutIndex) + "Impl2";

            } else {
                int cutIndex = className.length() - 3;
                int dollarIndex = className.lastIndexOf("$"); // For inner classes
                if (dollarIndex > 0) cutIndex = dollarIndex;
                className = className.substring(0, cutIndex) + "2";
            }

/*
            int cutIndex = className.length() - 2;
            int dollarIndex = className.lastIndexOf("$"); // For inner classes
            if (dollarIndex > 0) cutIndex = dollarIndex;
            className = className.substring(0, cutIndex);
*/
            type = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
            return type;
        } catch (ClassNotFoundException e) {
            throw new ReflectionException("Could not load class " + className + " derived from " + this, e);
        }
    }

    /**
     * Creates an instance of the type this is an id for.
     * The type name must be the same as the id name minus the
     * "Id", and it must have a constructor taking an id as the only
     * argument
     *
     * @return the new type instance or null if no such contructor exists
     * @throws no.statkart.skif.exception.ReflectionException if the type could not be created
     */
    public T createTypeInstance() {
        T instance = (T) Reflection.newInstance(getType());
        if (instance == null)
            //throw new ReflectionException("No workable public constructor with argument " + clazz + " in " + getType());
            throw new ReflectionException("No workable public constructor with no arguments in " + getType());
        return instance;
    }


    /**
     * Returns the class of the base id type. The base id type is last non abstract super class of the id class.
     *
     * @return the base type class of the id
     */
    public Class getBaseIdType() {
        Class c = clazz;
        while (!Modifier.isAbstract(c.getSuperclass().getModifiers())) c = c.getSuperclass();
        return c;
    }

    /**
     * Returns the classname without package prefix for the base id type.
     *
     * @return the classname without package prefix for the base id type.
     */
    public String getBaseIdTypeName() {
        Class c = getBaseIdType();
        String classname = c.getName();
        return classname.substring(classname.lastIndexOf(".") + 1); // strip the package name
    }

    /**
     * Returns the class of the base type for this id. This class may be abstract.
     *
     * @return the base type class of the id
     */
    public Class getBaseType() {
        if (typeInfo == null) {
            typeInfo = getTypeInfo();
        }
        return typeInfo.baseType;
    }

    protected Class calcBaseType() {
        Class baseType;
        Class c = getBaseIdType();
        String className = null;
        try {
            className = c.getName();

            // TODO: temp fix XIdImpl2 -> XImpl2, i stedet for XId -> X
            if (className.endsWith("IdImpl2")) {
                int cutIndex = className.length() - 7;
                int dollarIndex = className.lastIndexOf("$"); // For inner classes
                if (dollarIndex > 0) cutIndex = dollarIndex;
                className = className.substring(0, cutIndex) + "Impl2";

            } else {
                int cutIndex = className.length() - 3;
                int dollarIndex = className.lastIndexOf("$"); // For inner classes
                if (dollarIndex > 0) cutIndex = dollarIndex;
                className = className.substring(0, cutIndex) + "2";
            }
/*
            int cutIndex = className.length() - 2;
            int dollarIndex = className.lastIndexOf("$"); // For inner classes
            if (dollarIndex > 0) cutIndex = dollarIndex;
            className = className.substring(0, cutIndex);
  */
            baseType = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("Could not load base class " + className + " derived from " + this, e);
        }
        return baseType;
    }


    /**
     * Returns the classname without prefix of the base type for this id
     *
     * @return the base type class of the id
     */
    public String getBaseTypeName() {
        Class c = getBaseType();
        String classname = c.getName();
        return classname.substring(classname.lastIndexOf(".") + 1); // strip the package name
    }

    /**
     * Returns the string value of this id
     */
    public String getStringValue() {
        return value.toString();
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "value=" + value +
                ", replicaVersion=" + replicaVersion +
                '}';
    }
}
