package no.statkart.skif.mapper;

import com.google.common.reflect.TypeToken;
import no.statkart.skif.exception.ImplementationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


/**
 * <p>Denne klassen forsøker å mappe to typer mellom hverandre ved å basere seg på to antagelser:
 * <ul>
 * <li>Klassenavnene er like, klassene ligger bare i forskjellige pakker</li>
 * <li>Accessormetodenes navn er like i klassene</li>
 * </ul>
 * </p>
 * <p/>
 * <p>For å benytte klassen så setter man inn denne med {@link AbstractMapper#setDefaultMapper(no.statkart.skif.mapper.DefaultTypeMapper)}.
 * For å benytte DefaultTypeMapper må også en packageMapping legges inn. Ved bruk av addPackageMapping er det mulig å
 * mappe alle klasser i en pakke og subpakker til klasser i en annen pakke og subpakkker med samme navn.</p>
 * <p/>
 * <p>F.eks.
 * <blockquote><pre>
 * DefaultTypeMapper typeMapper = new DefaultTypeMapper();
 * typeMapper.addPackageMapping("no.statkart.grunnbok.borett.info.wsapi.domain", "no.statkart.grunnbok.borett.info.domain");
 * setDefaultTypeMapper(typeMapper);
 * </pre></blockquote></p>
 * <p/>
 * <p>Hensikten med defaultmapperen er at den skal benyttes ved "defaulting" som i en switch-statement. Dersom ingen annen typemapping
 * finnes så faller typemappingen tilbake til denne.</p>
 * <p/>
 *
 * @author Steinar Hansen
 * @author Tor Egil R. Strand
 */
public class DefaultTypeMapper {
    private Logger logger = LoggerFactory.getLogger(DefaultTypeMapper.class);

    private static Map<Method, Method> settersForGetters = new HashMap<Method, Method>();

    Mapping mapping;

    Map<String, String> wsapiPkg2domainPkg = new HashMap<String, String>();
    Map<String, String> domainPkg2wsapiPkg = new HashMap<String, String>();
    private Map<Class, Class> classMappings = new HashMap<Class, Class>();
    private Set<Class> doNotMapTheseClasses = new HashSet<Class>();
    private final MappedFieldsTracker mappedFields = new MappedFieldsTracker();
    private Map<Class, Class> overrideClassMappings;

    public DefaultTypeMapper() {
    }

    public void clearMappedFields() {
        mappedFields.clear();
    }

    /**
     * Denne metoden finner klasser i alle subpakker av de angitte pakkene, og mapper de opp mot hverandre gitt at navnene (SimpleName) på
     * klassene er de samme.
     *
     * @param wsapiPackage  pakken til JAXB-klassene
     * @param domainPackage pakken til Hibernate-domene-klassene
     */
    public void addPackageMapping(String wsapiPackage, String domainPackage) {
        addPackageMapping(wsapiPackage, domainPackage, true);
    }

    /**
     * Denne metoden finner klasser i alle subpakker av de angitte pakkene, med mindre recurse er satt til 'false', da leter den bare i den angitte pakken. og mapper de opp mot hverandre gitt at navnene (SimpleName) på
     * klassene er de samme.
     *
     * @param wsapiPackage  pakken til JAXB-klassene
     * @param domainPackage pakken til Hibernate-domene-klassene
     */
    public void addPackageMapping(String wsapiPackage, String domainPackage, boolean recurse) {
        try {
            if (wsapiPackage == null || domainPackage == null) {
                return;
            }

            wsapiPkg2domainPkg.put(wsapiPackage, domainPackage);
            domainPkg2wsapiPkg.put(domainPackage, wsapiPackage);

            List<Class> wsapiClasses = getClasses(wsapiPackage, recurse);
            List<Class> domainClasses = getClasses(domainPackage, recurse);
            for (Class wsapiClass : wsapiClasses) {
                String name = wsapiClass.getSimpleName();
                for (Class domainClass : domainClasses) {
                    if (isEquivalent(domainClass.getSimpleName(), name)) {
                        classMappings.put(wsapiClass, domainClass);
                    }
                }
            }
            for (Class domainClass : domainClasses) {
                String name = domainClass.getSimpleName();
                for (Class wsapiClass : wsapiClasses) {
                    if (isEquivalent(name, wsapiClass.getSimpleName())) {
                        classMappings.put(domainClass, wsapiClass);
                    }
                }
            }

        } catch (ClassNotFoundException e) {
            throw new ImplementationException(e);
        } catch (IOException e) {
            throw new ImplementationException(e);
        }
    }

    protected boolean isEquivalent(String domainName, String wsapiName) {
        return wsapiName.equals(domainName);
    }

    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    public void overrideClassMappings(Map<Class, Class> classMappings) {
        this.overrideClassMappings = classMappings;
    }

    public TypeToken<?> findTargetClass(Class sourceClass, TypeToken<?> targetType) throws ClassNotFoundException, NoSuchFieldException {
        TypeToken<?> retVal = null;
        if (this.overrideClassMappings != null) {
            retVal = TypeToken.of(this.overrideClassMappings.get(sourceClass));
        }
        if (retVal == null) {
            if (classMappings.containsKey(sourceClass)) {
                retVal = TypeToken.of(classMappings.get(sourceClass));
                // Hvis kildeklassen har et felt som heter 'item' så er dette en collection-klasse.
            } else if (checkHasField(sourceClass, "item")) {
                retVal = resolveCollection(targetType);
                // Eller hvis kildeklassen har et felt som heter 'liste' så er dette en collection-klasse.
            } else if (checkHasField(sourceClass, "liste")) {
                retVal = resolveCollection(targetType);
                // Eller hvis kildeklassen har et felt som heter 'entry' så er dette en map-klasse.
            } else if (checkHasField(sourceClass, "entry")) {
                retVal = resolveMap(targetType);
                // Eller hvis det er collection på begge sider.
            } else if (Collection.class.isAssignableFrom(sourceClass) && Collection.class.isAssignableFrom(targetType.getRawType())) {
                retVal = targetType;
                // Eller hvis det er collection på kildesiden, mens måltypen har felt som heter 'item' eller 'liste'.
            } else if (Collection.class.isAssignableFrom(sourceClass) && (checkHasField(targetType.getRawType(), "item") || checkHasField(targetType.getRawType(), "liste"))) {
                retVal = targetType;
                // Eller hvis det er array på kildesiden, mens måltypen har felt som heter 'item' eller 'liste'.
            }  else if (sourceClass.isArray() && (checkHasField(targetType.getRawType(), "item") || checkHasField(targetType.getRawType(), "liste"))) {
                retVal = targetType;
                // Eller hvis det er map på kildesiden, mens måltypen har felt som heter 'entry'.
            } else if (Map.class.isAssignableFrom(sourceClass) && checkHasField(targetType.getRawType(), "entry")) {
                retVal = targetType;
            } else {
                throw new MappingException("Can not map " + sourceClass.toString() + ", could not find corresponding class");
            }
        }

        if (!targetType.isAssignableFrom(retVal)) {
            throw new MappingException("Wanted to map " + sourceClass + " to " + retVal + ", but requested class is " + targetType.getRawType());
        }

        return retVal;
    }

    private TypeToken<?> resolveCollection(TypeToken<?> targetType) {
        TypeToken<?> retVal;
        if (Collection.class.isAssignableFrom(targetType.getRawType())) {
            // Det er angitt en spesiell type collection det skal mappes til
            retVal = targetType;
            if (retVal.getRawType().isInterface()) {
                if (Set.class.isAssignableFrom(retVal.getRawType())) {
                    retVal = retVal.getSubtype(HashSet.class);
                } else if (List.class.isAssignableFrom(retVal.getRawType())) {
                    retVal = retVal.getSubtype(ArrayList.class);
                } else {
                    throw new MappingException("Unknown Collection type: " + retVal);
                }
            }
        } else if (targetType.isArray()) {
            retVal = targetType;
        } else {
            // Antar ArrayList når ikke nærmere spesifisert
            retVal = TypeToken.of(ArrayList.class);
        }
        return retVal;
    }

    private TypeToken<?> resolveMap(TypeToken<?> targetType) {
        TypeToken<?> retVal;
        if (Map.class.isAssignableFrom(targetType.getRawType())) {
            // Det er angitt en spesiell type map det skal mappes til
            retVal = targetType;
            if (retVal.getRawType().isInterface()) {
                retVal = retVal.getSubtype(HashMap.class);
            }
        } else {
            // Antar HashMap når ikke nærmere spesifisert
            retVal = TypeToken.of(HashMap.class);
        }
        return retVal;
    }


    public final Object mapDomainObject(Object source, TypeToken<?> wsapiType) {
        Object target = null;
        if (!doNotMapTheseClasses.contains(source.getClass())) {
            try {
                TypeToken<?> targetType = findTargetClass(source.getClass(), wsapiType);
                Object alreadyMappedValue = mappedFields.getMappedValue(source, targetType.getRawType());
                if (alreadyMappedValue == null) {
                    target = targetType.getRawType().newInstance();

                    if (!doNotMapTheseClasses.contains(source.getClass())) {
                        mapCommonDomainFields(source, target, wsapiType);
                    }
                } else {
                    target = alreadyMappedValue;
                }
            } catch (ClassNotFoundException e) {
                throw new MappingException(e);
            } catch (InstantiationException e) {
                throw new MappingException(e);
            } catch (IllegalAccessException e) {
                throw new MappingException(e);
            } catch (NoSuchFieldException e) {
                throw new MappingException(e);
            }

        }
        return target;
    }


    public final Object mapWsapiObject(Object source, TypeToken<?> domainType) {
        Object target = null;
        if (!doNotMapTheseClasses.contains(source.getClass())) {
            try {
                TypeToken<?> targetType = findTargetClass(source.getClass(), domainType);
                Object alreadyMappedValue = mappedFields.getMappedValue(source, targetType.getRawType());
                if (alreadyMappedValue == null) {
                    if (targetType.isArray()) {
                        Field field;
                        if (checkHasField(source.getClass(), "item")) {
                            field = source.getClass().getDeclaredField("item");
                        } else if (checkHasField(source.getClass(), "liste")) {
                            field = source.getClass().getDeclaredField("liste");
                        } else {
                            throw new MappingException("Assumption that there is a field 'item' or 'liste' corresponding to an Array failed");
                        }
                        field.setAccessible(true);
                        Collection collection = (Collection) field.get(source);
                        //noinspection ConstantConditions
                        target = Array.newInstance(targetType.getComponentType().getRawType(), collection == null ? 0 :collection.size());
                    } else {
                        target = targetType.getRawType().newInstance();
                    }

                    if (!doNotMapTheseClasses.contains(source.getClass())) {
                        mapCommonWsapiFields(source, target, targetType);
                    }
                } else {
                    target = alreadyMappedValue;
                }
            } catch (InstantiationException e) {
                throw new MappingException(e);
            } catch (NoSuchFieldException e) {
                throw new MappingException(e);
            } catch (IllegalAccessException e) {
                throw new MappingException(e);
            } catch (ClassNotFoundException e) {
                throw new MappingException(e);
            }
        }
        return target;
    }

    /**
     * Metode for å angi en klasse som skal ignoreres ved mapping.
     *
     * @param className Fully qualified class name.
     */
    public void doNotMapThisClass(String className) {
        try {
            Class c = Class.forName(className);
            doNotMapTheseClasses.add(c);
        } catch (ClassNotFoundException e) {
            throw new MappingException("Tried to ignore unknown class for mapping", e);
        }

    }

    @SuppressWarnings("unchecked")
    protected void mapCommonDomainFields(Object source, Object target, TypeToken<?> targetType) throws ClassNotFoundException {
        try {
            if (source instanceof Collection) {
                Collection sourceCollection = (Collection) source;
                if (checkHasField(target.getClass(), "item")) {
                    Field targetField = target.getClass().getDeclaredField("item");
                    TypeToken<?> wsapiCollectionType = targetType.resolveType(targetField.getGenericType());
                    ParameterizedType wsapiParametrizedType = (ParameterizedType) wsapiCollectionType.getType();
                    Type wsapiElementType = wsapiParametrizedType.getActualTypeArguments()[0];

                    List<Object> value = new ArrayList<Object>(sourceCollection.size());
                    for (Object o : sourceCollection) {
                        value.add(mapping.d2w(o, wsapiElementType));
                    }

                    targetField.setAccessible(true);
                    targetField.set(target, value);
                } else if (checkHasField(target.getClass(), "liste")) {
                    Field targetField = target.getClass().getDeclaredField("liste");
                    TypeToken<?> wsapiCollectionType = targetType.resolveType(targetField.getGenericType());
                    ParameterizedType wsapiParametrizedType = (ParameterizedType) wsapiCollectionType.getType();
                    Type wsapiElementType = wsapiParametrizedType.getActualTypeArguments()[0];

                    List<Object> value = new ArrayList<Object>(sourceCollection.size());
                    for (Object o : sourceCollection) {
                        value.add(mapping.d2w(o, wsapiElementType));
                    }

                    targetField.setAccessible(true);
                    targetField.set(target, value);
                } else if (target instanceof Collection) {
                    TypeToken<?> wsapiCollectionType = targetType.getSupertype((Class) Collection.class);
                    ParameterizedType wsapiParametrizedType = (ParameterizedType) wsapiCollectionType.getType();
                    Type wsapiElementType = wsapiParametrizedType.getActualTypeArguments()[0];

                    Collection targetCollection = (Collection) target;
                    for (Object next : sourceCollection) {
                        targetCollection.add(mapping.d2w(next, wsapiElementType));
                    }
                } else {
                    throw new MappingException("Assumption that there is a field 'item' or 'liste' corresponding to a Collection failed");
                }
            } else if (source.getClass().isArray()) {
                if (checkHasField(target.getClass(), "item")) {
                    Field targetField = target.getClass().getDeclaredField("item");
                    TypeToken<?> wsapiCollectionType = targetType.resolveType(targetField.getGenericType());
                    ParameterizedType wsapiParametrizedType = (ParameterizedType) wsapiCollectionType.getType();
                    Type wsapiElementType = wsapiParametrizedType.getActualTypeArguments()[0];

                    List<Object> value = new ArrayList<Object>(Array.getLength(source));
                    for (int i = 0; i < Array.getLength(source); i++) {
                        value.add(mapping.d2w(Array.get(source, i), wsapiElementType));
                    }

                    targetField.setAccessible(true);
                    targetField.set(target, value);
                } else if (checkHasField(target.getClass(), "liste")) {
                    Field targetField = target.getClass().getDeclaredField("liste");
                    TypeToken<?> wsapiCollectionType = targetType.resolveType(targetField.getGenericType());
                    ParameterizedType wsapiParametrizedType = (ParameterizedType) wsapiCollectionType.getType();
                    Type wsapiElementType = wsapiParametrizedType.getActualTypeArguments()[0];

                    List<Object> value = new ArrayList<Object>(Array.getLength(source));
                    for (int i = 0; i < Array.getLength(source); i++) {
                        value.add(mapping.d2w(Array.get(source, i), wsapiElementType));
                    }

                    targetField.setAccessible(true);
                    targetField.set(target, value);
                } else {
                    throw new MappingException("Assumption that there is a field 'item' or 'liste' corresponding to an Array failed");
                }
            } else if (source instanceof Map) {
                // Det skal ikke være noe arv som gjør at feltene ikke er umiddelbart tilgjengelig her
                final Field entryField = targetType.getRawType().getDeclaredField("entry");
                entryField.setAccessible(true);
                List entryList = new ArrayList();
                entryField.set(target, entryList);

                ParameterizedType entryListType = (ParameterizedType) entryField.getGenericType(); // Dette er en List<?.Entry>. Vil ha tak i Class for ?.Entry
                Class<?> entryClass = (Class) entryListType.getActualTypeArguments()[0];

                Field keyField = entryClass.getDeclaredField("key");
                keyField.setAccessible(true);
                Type keyType = keyField.getGenericType();
                Field valueField = entryClass.getDeclaredField("value");
                valueField.setAccessible(true);
                Type valueType = valueField.getGenericType();

                Map<?, ?> sourceMap = (Map) source;
                for (Map.Entry<?, ?> sourceEntry : sourceMap.entrySet()) {
                    Object targetKey = mapping.d2w(sourceEntry.getKey(), keyType);
                    Object targetValue = mapping.d2w(sourceEntry.getValue(), valueType);
                    Object targetEntry = entryClass.newInstance();
                    keyField.set(targetEntry, targetKey);
                    valueField.set(targetEntry, targetValue);
                    entryList.add(targetEntry);
                }
            } else {
                Collection<Method> sourceGetters = findGetters(source.getClass());
                for (Method sourceGetter : sourceGetters) {
                    Method targetSetter = findSetterForGetter(target.getClass(), sourceGetter);

                    Method overriddenSetter = overrideSetter(sourceGetter, targetSetter, target.getClass());
                    if (overriddenSetter != null) {
                        targetSetter = overriddenSetter;
                    }

                    if (targetSetter != null) {
                        Object source1 = sourceGetter.invoke(source);
                        TypeToken<?> targetFieldType = targetType.resolveType(targetSetter.getGenericParameterTypes()[0]);
                        if (source1 != null) {
                            Object value = mapping.d2w(source1, targetFieldType.getType());
                            targetSetter.invoke(target, value);
                        } else {
                            //Dersom value = null, så kan vi fremdeles sette den i target.
                            //Med midre typen er primitiv, da lar vi den bare være
                            if (!targetFieldType.getRawType().isPrimitive()) {
                                targetSetter.invoke(target, new Object[]{null});
                            }
                        }
                    } else {
                        //Kan ikke feile dersom vi ikke finner et felt, da vil ikke subklasser kunne fungere.
                        if (logger.isDebugEnabled())
                            logger.debug("Ignorer getter: " + sourceGetter.getName() + ", siden jeg ikke fant en tilsvarende setter i target-klasse");
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new MappingException(e);
        } catch (NoSuchFieldException e) {
            throw new MappingException(e);
        } catch (InvocationTargetException e) {
            throw new MappingException(e);
        } catch (InstantiationException e) {
            throw new MappingException(e);
        }

    }

    /**
     * Denne metoden gir tilgang til sourceGetter og targetSetter før kalling av targetSetter med returverdien fra
     * sourceGetter, slik at man evt. overstyre hvilken verdig target skal få. Det er den returnerte targetSetter som
     * brukes videre i koden. Så dersom man ønsker å bytte ut target-property 'minProperty' med 'minAlternativeProperty',
     * så kan man gjøre det i en subklasse av DefaultTypeMapper se f.eks. {@link no.statkart.skif.mapper.RenamingDefaultTypeMapper}
     *
     * @param sourceGetter getter som brukes for å hente ut property fra source
     * @param targetSetter setter som i utgangspunktet skal brukes for å sette property på target
     * @param targetClass  klassen som setteren skal være på
     * @return setter som benyttes videre istedenfor <code>targetSetter</code> (<code>null</code> for å fortsette å bruke <code>targetSetter</code>
     */
    protected Method overrideSetter(Method sourceGetter, Method targetSetter, Class targetClass) {
        return null;
    }

    @SuppressWarnings("unchecked")
    protected void mapCommonWsapiFields(Object source, Object target, TypeToken<?> targetType) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        try {
            if (checkHasField(source.getClass(), "item")) {
                Field item = source.getClass().getDeclaredField("item");

                if (target instanceof Collection) {
                    Collection targetCollection = (Collection) target;
                    TypeToken<?> domainCollectionType = targetType.getSupertype((Class) Collection.class);
                    ParameterizedType domainParametrizedType = (ParameterizedType) domainCollectionType.getType();
                    Type domainElementType = domainParametrizedType.getActualTypeArguments()[0];
                    if (domainElementType instanceof TypeVariable) {
                        TypeVariable typeVariable = (TypeVariable) domainElementType;
                        // Må pakke ut TypeVariable, ellers feiler mapping på det senere.
                        domainElementType = typeVariable.getBounds()[0];
                    }

                    item.setAccessible(true);
                    Object o = item.get(source);
                    if (o != null) {
                        for (Object next : ((Iterable) o)) {
                            targetCollection.add(mapping.w2d(next, domainElementType));
                        }
                    }
                } else if (targetType.isArray()) {
                    //noinspection ConstantConditions
                    Type domainElementType = targetType.getComponentType().getType();
                    if (domainElementType instanceof TypeVariable) {
                        TypeVariable typeVariable = (TypeVariable) domainElementType;
                        // Må pakke ut TypeVariable, ellers feiler mapping på det senere.
                        domainElementType = typeVariable.getBounds()[0];
                    }

                    item.setAccessible(true);
                    Object o = item.get(source);
                    if (o != null) {
                        List c = (List) o;
                        for (int i = 0; i < c.size(); i++) {
                            Array.set(target, i, mapping.w2d(c.get(i), domainElementType));
                        }
                    }
                } else {
                    throw new MappingException("Assumption that a Collection or array corresponds to wsapi field 'item' failed");
                }
            } else if (checkHasField(source.getClass(), "liste")) {
                Field item = source.getClass().getDeclaredField("liste");

                if (target instanceof Collection) {
                    Collection targetCollection = (Collection) target;
                    TypeToken<?> domainCollectionType = targetType.getSupertype((Class) Collection.class);
                    ParameterizedType domainParametrizedType = (ParameterizedType) domainCollectionType.getType();
                    Type domainElementType = domainParametrizedType.getActualTypeArguments()[0];
                    if (domainElementType instanceof TypeVariable) {
                        TypeVariable typeVariable = (TypeVariable) domainElementType;
                        // Må pakke ut TypeVariable, ellers feiler mapping på det senere.
                        domainElementType = typeVariable.getBounds()[0];
                    }

                    item.setAccessible(true);
                    Object o = item.get(source);
                    if (o != null) {
                        for (Object next : ((Iterable) o)) {
                            targetCollection.add(mapping.w2d(next, domainElementType));
                        }
                    }
                } else if (targetType.isArray()) {
                    //noinspection ConstantConditions
                    Type domainElementType = targetType.getComponentType().getType();
                    if (domainElementType instanceof TypeVariable) {
                        TypeVariable typeVariable = (TypeVariable) domainElementType;
                        // Må pakke ut TypeVariable, ellers feiler mapping på det senere.
                        domainElementType = typeVariable.getBounds()[0];
                    }

                    item.setAccessible(true);
                    Object o = item.get(source);
                    if (o != null) {
                        List c = (List) o;
                        for (int i = 0; i < c.size(); i++) {
                            Array.set(target, i, mapping.w2d(c.get(i), domainElementType));
                        }
                    }
                } else {
                    throw new MappingException("Assumption that a Collection or array corresponds to wsapi field 'liste' failed");
                }
            } else if (checkHasField(source.getClass(), "entry")) {
                ParameterizedType parameterizedType = (ParameterizedType) targetType.getType();
                Type targetKeyType = parameterizedType.getActualTypeArguments()[0];
                Type targetValueType = parameterizedType.getActualTypeArguments()[1];
                if (targetValueType instanceof TypeVariable) {
                    TypeVariable typeVariable = (TypeVariable) targetValueType;
                    // Må pakke ut TypeVariable, ellers feiler mapping på det senere.
                    targetValueType = typeVariable.getBounds()[0];
                }

                // Det skal ikke være noe arv som gjør at feltene ikke er umiddelbart tilgjengelig her
                Class<?> sourceClass = source.getClass();
                final Field entryField;
                try {
                    entryField = sourceClass.getDeclaredField("entry");
                } catch (NoSuchFieldException e) {
                    throw new MappingException("Expected field entry when mapping to map");
                }
                entryField.setAccessible(true);
                final ParameterizedType entryListType = (ParameterizedType) entryField.getGenericType();
                final Class<?> entryClass = (Class<?>) entryListType.getActualTypeArguments()[0];

                final Field keyField;
                try {
                    keyField = entryClass.getDeclaredField("key");
                } catch (NoSuchFieldException e) {
                    throw new MappingException("Expected field key in entry class when mapping to map");
                }
                keyField.setAccessible(true);
                final Field valueField;
                try {
                    valueField = entryClass.getDeclaredField("value");
                } catch (NoSuchFieldException e) {
                    throw new MappingException("Excepted field value i entry class when mapping to map");
                }
                valueField.setAccessible(true);

                List entryList = (List) entryField.get(source);
                Map targetMap = (Map) target;
                for (Object entry : entryList) {
                    final Object key, value;
                    if (targetKeyType != null) {
                        key = mapping.w2d(keyField.get(entry), targetKeyType);
                    } else {
                        logger.warn("Vet ikke generisk type for key ved mapping fra " + sourceClass.getName());
                        key = mapping.w2d(keyField.get(entry), Object.class);
                    }
                    if (targetValueType != null) {
                        value = mapping.w2d(valueField.get(entry), targetValueType);
                    } else {
                        logger.warn("Vet ikke generisk type for value ved mapping fra " + sourceClass.getName());
                        value = mapping.w2d(valueField.get(entry), Object.class);
                    }
                    targetMap.put(key, value);
                }
            } else if (source instanceof Collection && target instanceof Collection) {
                TypeToken<?> domainCollectionType = targetType.getSupertype((Class) Collection.class);
                ParameterizedType domainParametrizedType = (ParameterizedType) domainCollectionType.getType();
                Type domainElementType = domainParametrizedType.getActualTypeArguments()[0];

                Collection sourceCollection = (Collection) source;
                Collection targetCollection = (Collection) target;
                for (Object next : sourceCollection) {
                    targetCollection.add(mapping.w2d(next, domainElementType));
                }
            } else {
                Collection<Method> sourceGetters = findGetters(source.getClass());
                for (Method sourceGetter : sourceGetters) {
                    Method targetSetter = findSetterForGetter(target.getClass(), sourceGetter);

                    Method overriddenSetter = overrideSetter(sourceGetter, targetSetter, target.getClass());
                    if (overriddenSetter != null) {
                        targetSetter = overriddenSetter;
                    }

                    if (targetSetter != null) {
                        Object source1 = sourceGetter.invoke(source);
                        TypeToken<?> targetFieldType = targetType.resolveType(targetSetter.getGenericParameterTypes()[0]);
                        if (source1 != null) {
                            Object value = mapping.w2d(source1, targetFieldType.getType());
                            if (value != null) {
                                targetSetter.invoke(target, value);
                            }
                        } else {
                            //Dersom value = null, så kan vi fremdeles sette den i target.
                            //Med midre typen er primitiv, da lar vi den bare være
                            if (!targetFieldType.getRawType().isPrimitive()) {
                                targetSetter.invoke(target, new Object[]{null});
                            }
                        }
                    } else {
                        //Kan ikke feile dersom vi ikke finner et felt, da vil ikke subklasser kunne fungere.
                        if (logger.isDebugEnabled())
                            logger.debug("Ignorer getter: " + sourceGetter.getName() + ", siden jeg ikke fant en tilsvarende setter i target-klasse");
                    }
                }
            }
        } catch (IllegalAccessException e) {
            throw new MappingException(e);
        } catch (InvocationTargetException e) {
            throw new MappingException(e);
        }
    }


    /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws java.io.IOException
     */
    protected static List<Class> getClasses(String packageName, boolean recurse) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        Set<Class> classes = new HashSet<Class>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();
            if (protocol.equals("file")) {
                String fileName = resource.getFile();
                String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
                final File e = new File(fileNameDecoded);
                if (e.isDirectory())
                    classes.addAll(findClasses(e, packageName));
            } else if (protocol.equals("jar")) {
                String filepath = resource.getPath();
                int idx = filepath.indexOf("!");
                String parsedJarName = filepath.substring(0, idx);
                if (resource != null) {
                    URL resource2 = new URL(parsedJarName);
                    ZipInputStream zip2 = new ZipInputStream(resource2.openStream());
                    try {
                        ZipEntry ze;
                        while ((ze = zip2.getNextEntry()) != null) {
                            String entryName = ze.getName();

                            if (entryName.endsWith(".class") && !entryName.contains("$") && !entryName.endsWith("package-info.class") && !entryName.endsWith("ObjectFactory.class")) {
                                Class _class;
                                String className;
                                try {
                                    className = entryName.replace("/", ".").substring(0, entryName.length() - 6);
                                    String classPackageName = className.substring(0, className.lastIndexOf("."));
                                    // Laster kun klasser som ligger under packageName
                                    if (classPackageName.contains(packageName)) {
                                        _class = Class.forName(className);
                                        classes.add(_class);
                                    }
                                } catch (ExceptionInInitializerError e) {
                                    // happen, for example, in classes, which depend on
                                    // Spring to inject some beans, and which fail,
                                    // if dependency is not fulfilled
//                                    _class = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
                                    throw new MappingException(e);
                                }
                            }
                        }
                    } finally {
                        zip2.close();
                    }
                }
            } else if (protocol.equals("zip")) {
                String filepath = resource.getPath();
                int idx = filepath.indexOf("!");
                String parsedJarName = filepath.substring(0, idx);
                URL resource2 = new File(parsedJarName).toURI().toURL();
                ZipInputStream zip2 = new ZipInputStream(resource2.openStream());
                try {
                    ZipEntry ze;
                    while ((ze = zip2.getNextEntry()) != null) {
                        String entryName = ze.getName();
                        if (entryName.endsWith(".class") && !entryName.contains("$") && !entryName.endsWith("package-info.class") && !entryName.endsWith("ObjectFactory.class")) {
                            try {
                                String className = entryName.replace("/", ".").substring(0, entryName.length() - 6);
                                String classPackageName = className.substring(0, className.lastIndexOf("."));
                                // Laster kun klasser som ligger under packageName
                                if (classPackageName.contains(packageName)) {
                                    Class _class = Class.forName(className);
                                    classes.add(_class);
                                }

                            } catch (ExceptionInInitializerError e) {
                                // happen, for example, in classes, which depend on
                                // Spring to inject some beans, and which fail,
                                // if dependency is not fulfilled
//                                        _class = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
                                throw new MappingException(e);
                            }
                        }
                    }
                } finally {
                    zip2.close();
                }

            } else {
                throw new ImplementationException("Unknown protocol: " + protocol);
            }
        }

        if (!recurse) {
            Set<Class> trimmedClasses = new HashSet<Class>();
            for (Class c : classes) {
                if (c.getPackage().getName().equals(packageName)) {
                    trimmedClasses.add(c);
                }
            }
            classes = trimmedClasses;
        }

        return new ArrayList<Class>(classes);
    }


    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    protected static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<Class>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        //noinspection ConstantConditions
        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                assert !fileName.contains(".");
                classes.addAll(findClasses(file, packageName + "." + fileName));
            } else if (fileName.endsWith(".class") && !fileName.contains("$") && !fileName.endsWith("package-info.class") && !fileName.endsWith("ObjectFactory.class")) {
                Class _class;
                try {
                    _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
                    classes.add(_class);
                } catch (ExceptionInInitializerError e) {
                    throw new MappingException(e);
                    // happen, for example, in classes, which depend on
                    // Spring to inject some beans, and which fail,
                    // if dependency is not fulfilled
//                    _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6), false, Thread.currentThread().getContextClassLoader());

                }

            }
        }
        return classes;
    }

    protected Collection<Method> findGetters(Class<?> c) {
        Method[] methods = c.getMethods();
        List<Method> getters = new ArrayList<Method>(methods.length / 2);
        List<Method> idGetters = new ArrayList<Method>(methods.length / 4);
        for (Method method : methods) {
            if (!method.isBridge() && method.getName().startsWith("get")) {
                getters.add(method);
                if (method.getName().endsWith("Id")) {
                    idGetters.add(method);
                }
            }
        }
        Iterator<Method> iterator = getters.iterator();
        while (iterator.hasNext()) {
            Method getter = iterator.next();
            boolean match = false;
            for (Method idGetter : idGetters) {
                if (!idGetter.equals(getter) && idGetter.getName().startsWith(getter.getName())) {
                    match = true;
                }
            }
            if (match) {
                iterator.remove();
            }
        }
        return getters;
    }

    protected Method findSetterForGetter(Class<?> c, Method getter) {

        Method setter = settersForGetters.get(getter);
        if (setter != null) {
            return setter;
        }

        String expectedSetterName = 's' + getter.getName().substring(1);

        Method[] methods = c.getMethods();
        Method matched = null;
        for (Method method : methods) {
            if (method.getParameterTypes().length == 1) {
                if (method.getName().equals(expectedSetterName)) {
                    matched = method;
                    break;
                }
            }
        }

        if (matched != null) {
            settersForGetters.put(getter, matched);
        }
        return matched;
    }

    protected boolean checkHasField(Class clazz, String fieldname) {
        try {
            clazz.getDeclaredField(fieldname);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

}