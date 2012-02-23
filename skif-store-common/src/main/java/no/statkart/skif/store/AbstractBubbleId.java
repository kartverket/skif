package no.statkart.skif.store;

import no.statkart.skif.exception.ConfigurationException;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ReflectionException;
import no.statkart.skif.util.Reflection;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public abstract class AbstractBubbleId<T extends BubbleObject> implements BubbleId<T>, Serializable {
    private Object value;

    /**
     * Allows the object to exist in multiple versions in Store.
     */
    private SnapshotVersion snapshotVersion = SnapshotVersion.CURRENT;

    /**
     * Helper class that holds meta info for each subtype of this class. The meta info takes
     * time to calculate and is therefore shared by all instances of a particular class.
     * <p/>
     * This class should not be Serializable
     */
    static class TypeInfo {
        String typeName;
        Class type;
        Class baseType;
        Class valueType;

        public TypeInfo(String typeName, Class type, Class baseType, Class idClass) {
            this.typeName = typeName;
            this.type = type;
            this.baseType = baseType;
            this.valueType = calcIdValueType(idClass);
//            Class<?> t = calcIdValueType(type);
//            if (valueType != t) {
//                throw new ImplementationException(String.format("Id-klasse og base id-klasse har forskjellig idValue type. %s->%s og %s->%s", type.getName(), t.getName(), baseType.getName(), valueType.getName()));
//            }
        }

        private Class calcIdValueType(Class type) {
            try {
                Class<?> valueType = type.getMethod("getValue", (Class[]) null).getReturnType();

                if (valueType==Object.class) {
                    throw new ImplementationException("Klassens getValue() metode returnerer Object. Hadde forventet Long, String eller tilsvarende: " + type);
                }
                return valueType;
            } catch (NoSuchMethodException e) {
                throw new ImplementationException("Klassen har ingen getValue() metode. Dette burde egentlig ikke kunne skje: " + type);
            }
        }
    }

    // static Map of meta info for each BubbleId class
    transient private static Map<Class, TypeInfo> typeInfoMap = new ConcurrentHashMap(100);

    // Cached meta info for this instance.
    transient private TypeInfo typeInfo;

    // Cache the class for faster access. This actually matters
    protected Class clazz = getClass();

    public BubbleId<T> resolveInstance() {
        return this;
    }

    protected AbstractBubbleId() {
    }

    protected AbstractBubbleId(Object value) {
        this.value = value;
    }

    protected AbstractBubbleId(Object value, SnapshotVersion version) {
        this.value = value;
        this.snapshotVersion = version;
    }


    public T getObject(Object session) {
        return null;
    }

    public Object getValue() {
        return value;
    }

    public SnapshotVersion getSnapshotVersion() {
        return snapshotVersion;
    }


    public AbstractBubbleId<T> asSnapshotVersion(SnapshotVersion snapshotVersion) {
        if (this.snapshotVersion.equals(snapshotVersion)) return this;
        return BubbleIds.createInstance(this.getClass(), getValue(), snapshotVersion);
    }

    public AbstractBubbleId<T> asSnapshotVersion(BubbleId<?>  bubbleId) {
        SnapshotVersion snapshotVersion = bubbleId.getSnapshotVersion();
        if (this.snapshotVersion.equals(snapshotVersion)) return this;
        return BubbleIds.createInstance(this.getClass(), getValue(), snapshotVersion);
    }

    public AbstractBubbleId<T> asSnapshotVersionOld() {
        return asSnapshotVersion(SnapshotVersion.OLD);
    }

    public AbstractBubbleId<T> asSnapshotVersionCurrent() {
        return asSnapshotVersion(SnapshotVersion.CURRENT);
    }

    static TypeInfo getTypeInfo(Class clazz) {
        TypeInfo typeInfo = typeInfoMap.get(clazz);
        if (typeInfo == null) {
            typeInfo = new TypeInfo(calcTypeName(clazz), calcType(clazz), calcBaseType(clazz), clazz);
            synchronized (typeInfoMap) {
                typeInfoMap.put(clazz, typeInfo);
            }
        }
        return typeInfo;
    }

    @Override
    public Class getValueType() {
        if (typeInfo == null) {
            typeInfo = getTypeInfo(clazz);
        }
        return typeInfo.valueType;
    }

    public static Class getValueType(Class<? extends BubbleId> type) {
        return getTypeInfo(type).valueType;
    }

    @Override
    public final int hashCode() {
        return value.hashCode();
    }

    public boolean equals(Object id) {
        return id instanceof AbstractBubbleId && equals((AbstractBubbleId) id);
    }

    /**
     * Må ha spesiell implementasjon. Årsaken til dette er at vi må håndtere id'er for hierarkier i modellen
     * spesielt. I vår modell kan vi ha hierarkier i domenemodellen der super-klassen er en abstract klasse.
     * Men hibernate gjør at når vi laster et objekt av klasse <code>Sub</code> som er subklasse av
     * <code>Super</code>, vil objektet kunne få et id-objekt som er av id-typen til 'Super'.
     * <p/>
     * <p>Eksempel: I matrikkelen vil en grunneiendom få id av type MatrikkelenhetId i stedet for GrunneiendomId. I praksis er en id
     * av type MatrikkelenhetId lik en med type GrunneiendomId, dersom id'ens <code>value</code> er lik. I motsetning
     * vil en VegadresseId og en GrunneiendomId være forskjellige selvom id'ene har samme value (gitt at id-verdier evt.
     * kun er unike innenfor hver klasse/hierarki).
     *
     * @param id en annen Id
     * @return true dersom dette objektet er av samme type og har samme <code>value</code> som parameteren <code>id</code>
     *         Objektene er av samme type også dersom dette objektet er en sub-type av <code>id</code> eller omvendt.
     */
    final public boolean equals(AbstractBubbleId id) {
        if (id == null) return false;
        return value.equals(id.value) && snapshotVersion.equals(id.snapshotVersion) && compatible(id);
    }

    final public boolean equalsIgnoreSnapshotVersion(Object id) {
        if (id == null) return false;
        return id instanceof AbstractBubbleId && equalsIgnoreSnapshotVersion((AbstractBubbleId) id);
    }

    final public boolean equalsIgnoreSnapshotVersion(AbstractBubbleId id) {
        if (id == null) return false;
        return value.equals(id.value) && compatible(id);
    }

    /**
     * To {@code BubbleId}'er er i utgangspunktet compatible hvis base typen for id'ene er den samme. Det er det samme
     * som at base typen for id'ens {@code BubbleObject}'er er like.
     * <p/>
     * I noen tilfeller kan det være nødvendig å overskrive denne metode, se {@link no.statkart.skif.store.kodeliste.Kodeliste}
     *
     * @param id
     * @return
     */
    protected boolean compatible(AbstractBubbleId id) {
        // Bruker getBaseType istedet for getBaseTypeId da denne er mye raskere pga caching.
        return clazz == id.clazz || this.getBaseType() == id.getBaseType();
    }

    /**
     * Comparable is implemented to support consistent ordering of id collections.
     * This is useful even if most id's are not instended for humans.
     */
    public int compareTo(Object object) {
        return getStringValue().compareTo(((AbstractBubbleId) object).getStringValue());
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
            typeInfo = getTypeInfo(clazz);
        }
        return typeInfo.typeName;
    }

    private static String calcTypeName(Class clazz) {
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
    public Class<T> getType() {
        if (typeInfo == null) {
            typeInfo = getTypeInfo(clazz);
        }
        return typeInfo.type;
    }

    private static Class calcType(Class clazz) {
        Class type;
        String className = null;
        try {
            className = clazz.getName();
            type = calcClassFromIdClassName(className);
            return type;
        } catch (ClassNotFoundException e) {
            throw new ReflectionException("Could not load class " + className + " derived from " + clazz, e);
        }
    }

    private static Class calcClassFromIdClassName(String className) throws ClassNotFoundException {
        Class type;
        if (className.endsWith("IdImpl")) {
            int cutIndex = className.length() - 6;
            int dollarIndex = className.lastIndexOf("$"); // For inner classes
            if (dollarIndex > 0) cutIndex = dollarIndex;
            className = className.substring(0, cutIndex) + "Impl";

        } else {
            int cutIndex = className.length() - 2;
            int dollarIndex = className.lastIndexOf("$"); // For inner classes
            if (dollarIndex > 0) cutIndex = dollarIndex;
            className = className.substring(0, cutIndex);
        }

        type = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
        return type;
    }

    /**
     * Creates an instance of the type this is an id for.
     * The type name must be the same as the id name minus the
     * "Id", and it must have a constructor taking an id as the only
     * argument
     *
     * @return the new type instance or null if no such contructor exists
     * @throws no.statkart.skif.exception.ReflectionException
     *          if the type could not be created
     */
    public T createTypeInstance() {
        T instance = (T) Reflection.newInstance(getType());
        if (instance == null)
            throw new ReflectionException("No workable public constructor with no arguments in " + getType());
        return instance;
    }


    /**
     * Returns the class of the base id type. The base id type is last non abstract super class of the id class.
     * <p/>
     * Note: This method is not fast as the result is not cached. Use {@link #getBaseType()} if possible.
     *
     * @return the base type class of the id
     */
    public Class getBaseIdType() {
        return calcBaseIdType(clazz);
    }

    private static Class calcBaseIdType(Class c) {
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
            typeInfo = getTypeInfo(clazz);
        }
        return typeInfo.baseType;
    }

    private static Class calcBaseType(Class clazz) {
        Class baseType;
        Class c = calcBaseIdType(clazz);
        String className = null;
        try {
            className = c.getName();

            baseType = calcClassFromIdClassName(className);
        } catch (ClassNotFoundException e) {
            throw new ConfigurationException("Could not load base class " + className + " derived from " + clazz, e);
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
                ", snapshotVersion=" + snapshotVersion +
                '}';
    }
}
