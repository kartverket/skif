package no.statkart.skif.mapper;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.internal.util.InternalClassUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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
 * <p>For å benytte klassen så setter man inn denne med {@link AbstractMapper#setDefaultMapper(TypeMapper)}.
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
 * <p>Det er også mulig å benytte mapperen som en superklasse for implementasjon av
 * type-mappere på lik linje med f.eks. GrunnbokBorettInfoTypeMapper. Da vil default type mapperen håndtere felter med samme
 * navn, slik at man bare trenger å håndtere de spesielle feltene i subklassen. I disse tilfellen så legger man til den nye
 * type-mapper subklassen ved bruk av AbstractMapper sin addMapper-metode, som vanlig.</p>
 * <p/>
 * <p/>
 * <p>F.eks.
 * <blockquote><pre>
 * public class SpesiellTypeMapper extends DefaultTypeMapper<Beloep, no.statkart.grunnbok.borett.info.domain.Beloep> {
 * <p/>
 *   public SpesiellTypeMapper() {
 *       super(Beloep.class, no.statkart.grunnbok.borett.info.domain.Beloep.class);
 *   }
 * <p/>
 *   public void mapDomainObject(no.statkart.grunnbok.borett.info.domain.Beloep source, Beloep target) {
 *       super.mapDomainObject(source, target);//setter f.eks. verdiene i 3 felter som har samme feltnavn i begge klassene
 *       target.setAnnenVerdi(mapping.w2d(source.getVerdi());
 *   }
 * <p/>
 *   public void mapWsapiObject(Beloep source, no.statkart.grunnbok.borett.info.domain.Beloep target) {
 *       super.mapWsapiObject(source, target);//ditto
 *       target.setVerdi(mapping.d2w(source.getAnnenVerdi());
 *   }
 * }
 * </pre></blockquote></p>
 *
 * @author Steinar Hansen
 * @author Tor Egil R. Strand
 */
public class DefaultTypeMapper<WsapiT, DomainT> implements AutomaticTypeMapper<WsapiT, DomainT> {
    private Logger logger = LoggerFactory.getLogger(DefaultTypeMapper.class);

    private static Map<Method, Method> settersForGetters = new HashMap<Method, Method>();

    private ObjectFactory domainObjectFactory;
    private ObjectFactory wsapiObjectFactory;

    Mapping mapping;

    Map<String, String> wsapiPkg2domainPkg = new HashMap<String, String>();
    Map<String, String> domainPkg2wsapiPkg = new HashMap<String, String>();
    private Class<WsapiT> wsapiClass;
    private Class<DomainT> domainClass;
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
     * @param wsapiPackage     pakken til JAXB-klassene
     * @param domainPackage    pakken til Hibernate-domene-klassene
     */
    public void addPackageMapping(String wsapiPackage, String domainPackage) {
        addPackageMapping(wsapiPackage, domainPackage, true);
    }

    /**
     * Denne metoden finner klasser i alle subpakker av de angitte pakkene, med mindre recurse er satt til 'false', da leter den bare i den angitte pakken. og mapper de opp mot hverandre gitt at navnene (SimpleName) på
     * klassene er de samme.
     *
     * @param wsapiPackage     pakken til JAXB-klassene
     * @param domainPackage    pakken til Hibernate-domene-klassene
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
            for (int i = 0; i < wsapiClasses.size(); i++) {
                Class wsapiClass = wsapiClasses.get(i);
                String name = wsapiClass.getSimpleName();
                for (int j = 0; j < domainClasses.size(); j++) {
                    Class domainClass = domainClasses.get(j);
                    if (isEquivalent(domainClass.getSimpleName(), name, domainClass, wsapiClass)) {
                        classMappings.put(wsapiClass, domainClass);
                    }
                }
            }
            for (int i = 0; i < domainClasses.size(); i++) {
                Class domainClass = domainClasses.get(i);
                String name = domainClass.getSimpleName();
                for (int j = 0; j < wsapiClasses.size(); j++) {
                    Class wsapiClass = wsapiClasses.get(j);
                    if (isEquivalent(name, wsapiClass.getSimpleName(), domainClass, wsapiClass)) {
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

    protected boolean isEquivalent(String domainName, String wsapiName, Class myDomainClass, Class myWsapiClass) {
        //Sjekk om wsapi-navn slutter på "Kode", da skal domainName slutte på "KodeId"
        if (wsapiName.endsWith("Kode")) {
            try {
                Class enumClass = Class.forName("no.statkart.matrikkel.domene.Enum");
                if (InternalClassUtils.isAssignable(myWsapiClass, enumClass)) {
                    //Dette er en enum, enumer skal mappes til klasse som ender på KodeId.
                    if (domainName.endsWith("KodeId")) {
                        if (domainName.substring(0, domainName.length() - 2).equals(wsapiName)) {
                            //Hvis klassenavnene med unntakt av Id er like så er disse klassene ekvivalente
                            return true;
                        }
                    }
                } else if (wsapiName.equals(domainName)) {
                    //Dette er ikke en enum, og klassenavnene er like, så da er de ekvivalente.
                    return true;
                }
            } catch (ClassNotFoundException e) {
                //Dette kan skje, ikke gjør noe, vi vil ende opp med å returnere true eller false i bunn av metoden uansett.
            }
        } else if (wsapiName.equals(domainName)) {
            return true;
        } else {
            return false;
        }
        return false;
    }


    protected DefaultTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        this.wsapiClass = wsapiClass;
        this.domainClass = domainClass;
    }


    public Mapping getMapping() {
        return mapping;
    }

    public void setMapping(Mapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public Class<WsapiT> getWsapiClass() {
        return wsapiClass;
    }

    @Override
    public Class<DomainT> getDomainClass() {
        return domainClass;
    }

    @Override
    public ObjectFactory getWsapiObjectFactory() {
        return wsapiObjectFactory;
    }

    @Override
    public void setWsapiObjectFactory(ObjectFactory factory) {
        this.wsapiObjectFactory = factory;
    }


    @Override
    public ObjectFactory getDomainObjectFactory() {
        return domainObjectFactory;
    }

    @Override
    public void setDomainObjectFactory(ObjectFactory factory) {
        this.domainObjectFactory = factory;
    }

    public void overrideClassMappings(Map<Class, Class> classMappings) {
        this.overrideClassMappings = classMappings;
    }

    public Class findTargetClassFromSourceClass(Class sourceClass) throws ClassNotFoundException, NoSuchFieldException {
        Class retVal = null;
        if (this.overrideClassMappings != null) {
            retVal = this.overrideClassMappings.get(sourceClass);
        }
        if (retVal == null) {
            //Hvis kildeklassen har et felt som heter 'item' så er dette en collection-klasse.
            if (checkHasField(sourceClass, "item")) {
                return Class.forName("java.util.ArrayList");
                //Eller hvis kildeklassen har et felt som heter 'liste' så er dette en collection-klasse.
            } else if (checkHasField(sourceClass, "liste")) {
                return Class.forName("java.util.ArrayList");
            } else if (classMappings.containsKey(sourceClass)) {
                retVal = classMappings.get(sourceClass);
            } else {
                throw new MappingException("Can not map " + sourceClass.toString() + ", could not find corresponding class");
            }
        }

        return retVal;
    }


    @Override
    public final WsapiT mapDomainObject(DomainT source) {
        WsapiT target = null;
        if (!doNotMapTheseClasses.contains(source.getClass())) {
            try {
                Class targetClass;
                if (getWsapiClass() != null) {
                    targetClass = getWsapiClass();
                } else {
                    targetClass = findTargetClassFromSourceClass(source.getClass());
                }
                Object alreadyMappedValue = mappedFields.getMappedValue(source, targetClass);
                if (alreadyMappedValue == null) {

                    target = getInitialWsapiObject(source);

                    mapDomainObject(source, target);
                } else {
                    target = (WsapiT) alreadyMappedValue;
                }
            } catch (Exception e) {
//                logger.error("Feilet under oppretting av target objekt med kildetype: " + source.getClass().getName(), e);
                throw new MappingException(e);
            }

        }
        return target;
    }


    @Override
    public final DomainT mapWsapiObject(WsapiT source) {
        DomainT target = null;
        if (!doNotMapTheseClasses.contains(source.getClass())) {
            try {
                Class targetClass;
                if (getDomainClass() != null) {
                    targetClass = getDomainClass();
                } else {
                    targetClass = findTargetClassFromSourceClass(source.getClass());
                }
                Object alreadyMappedValue = mappedFields.getMappedValue(source, targetClass);
                if (alreadyMappedValue == null) {

                    target = getInitialDomainObject(source);

                    mapWsapiObject(source, target);
                } else {
                    target = (DomainT) alreadyMappedValue;
                }


            } catch (NoSuchMethodException e) {
                throw new MappingException(e);
            } catch (InstantiationException e) {
                throw new MappingException(e);
            } catch (NoSuchFieldException e) {
                throw new MappingException(e);
            } catch (IllegalAccessException e) {
                throw new MappingException(e);
            } catch (InvocationTargetException e) {
                throw new MappingException(e);
            } catch (ClassNotFoundException e) {
                throw new MappingException(e);
            }
        }
        return target;
    }

    protected WsapiT getInitialWsapiObject(DomainT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
        WsapiT target = null;
        if (getWsapiClass() != null) {
            target = wsapiObjectFactory.getInitialObject(source, getWsapiClass());
        }
        if (target == null) {
            target = (WsapiT) wsapiObjectFactory.getInitialObject(source, findTargetClassFromSourceClass(source.getClass()));
        }

        if (target != null) {
            mappedFields.put(source, target);
        }

        return target;
    }

    protected DomainT getInitialDomainObject(WsapiT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, ClassNotFoundException, NoSuchFieldException {
        DomainT target = null;

        if (getDomainClass() != null) {
            target = domainObjectFactory.getInitialObject(source, getDomainClass());
        }
        if (target == null) {
            target = (DomainT) domainObjectFactory.getInitialObject(source, findTargetClassFromSourceClass(source.getClass()));
        }

        if (target != null) {
            mappedFields.put(source, target);
        }

        return target;
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        try {
            if (!doNotMapTheseClasses.contains(source.getClass())) {
                mapCommonDomainFields(source, target);
            }
        } catch (ClassNotFoundException e) {
            throw new MappingException(e);
        }
    }

    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        try {
            if (!doNotMapTheseClasses.contains(source.getClass())) {
                mapCommonWsapiFields(source, target);
            }
        } catch (ClassNotFoundException e) {
            throw new MappingException(e);
        } catch (NoSuchFieldException e) {
            throw new MappingException(e);
        } catch (IllegalAccessException e) {
            throw new MappingException(e);
        }
    }

    /**
     * Metode for å angi en klasse som skal ignoreres ved mapping.
     *
     * @param className    Fully qualified class name.
     */
    public void doNotMapThisClass(String className) {
        try {
            Class c = Class.forName(className);
            doNotMapTheseClasses.add(c);
        } catch (ClassNotFoundException e) {
            throw new MappingException("Tried to ignore unknown class for mapping", e);
        }

    }

    protected void mapCommonDomainFields(DomainT source, WsapiT target) throws ClassNotFoundException {
        try {
            if (source instanceof Collection) {
                if (checkHasField(target.getClass(), "item")) {
                    Field targetField = target.getClass().getDeclaredField("item");
                    ArrayList value = null;
                    for (Object o : ((Collection) source)) {
                        if (value == null) {
                            value = new ArrayList();
                        }
                        value.add(mapping.d2w(o));
                    }
                    targetField.setAccessible(true);
                    targetField.set(target, value);
                } else if (checkHasField(target.getClass(), "liste")) {
                    Field targetField = target.getClass().getDeclaredField("liste");
                    ArrayList value = null;
                    for (Object o : ((Collection) source)) {
                        if (value == null) {
                            value = new ArrayList();
                        }
                        value.add(mapping.d2w(o));
                    }
                    targetField.setAccessible(true);
                    targetField.set(target, value);
                } else if (target instanceof Collection) {
                    Collection sourceCollection = (Collection) source;
                    Collection targetCollection = (Collection) target;
                    for (Object next : sourceCollection) {
                        targetCollection.add(mapping.d2w(next));
                    }
                } else {
                    throw new MappingException("Assumption that there is a field 'item' or 'liste' corresponding to a Collection failed");
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
                        Class<?> targetType = targetSetter.getParameterTypes()[0];
                        if (source1 != null) {
                            Object value = mapping.d2w(source1, targetSetter.getGenericParameterTypes()[0]);
                            if (value != null) {
                                //Sjekk om source = Set og target = List, fordi da håndterer vi settingen spesielt
                                if (List.class.isAssignableFrom(targetType) && value instanceof Set) {
                                    List replaceSetWithThisList = new ArrayList((Set) value);
                                    targetSetter.invoke(target, replaceSetWithThisList);
                                } else {
                                    //Dersom dette ikke er en spesialsituasjon, så prøver vi den vanlige måten, så får vi evt. en feil
                                    targetSetter.invoke(target, value);
                                }
                            }
                        } else {
                            //Dersom value = null, så kan vi fremdeles sette den i target.
                            //Med midre typen er primitiv, da lar vi den bare være
                            if (!targetType.isPrimitive()) {
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
        }

    }

    /**
     * Denne metoden gir tilgang til sourceGetter og targetSetter før kalling av targetSetter med returverdien fra
     * sourceGetter, slik at man evt. overstyre hvilken verdig target skal få. Det er den returnerte targetSetter som
     * brukes videre i koden. Så dersom man ønsker å bytte ut target-property 'minProperty' med 'minAlternativeProperty',
     * så kan man gjøre det i en subklasse av DefaultTypeMapper se f.eks. {@link no.statkart.skif.mapper.RenamingDefaultTypeMapper}
     *
     * @param sourceGetter    getter som brukes for å hente ut property fra source
     * @param targetSetter    setter som i utgangspunktet skal brukes for å sette property på target
     * @param targetClass     klassen som setteren skal være på
     * @return setter som benyttes videre istedenfor <code>targetSetter</code> (<code>null</code> for å fortsette å bruke <code>targetSetter</code>
     */
    protected Method overrideSetter(Method sourceGetter, Method targetSetter, Class targetClass) {
        return null;
    }

    protected void mapCommonWsapiFields(WsapiT source, DomainT target) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        try {
            if (checkHasField(source.getClass(), "item")) {
                if (target instanceof Collection) {
                    Collection targetCollection = (Collection) target;

                    Field item = source.getClass().getDeclaredField("item");
                    item.setAccessible(true);
                    Object o = item.get(source);
                    if (o != null) {
                        for (Object next : ((Iterable) o)) {
                            targetCollection.add(mapping.w2d(next));
                        }
                    }
                } else {
                    throw new MappingException("Assumption that a List corresponds to wsapi field 'item' failed");
                }
            } else if (checkHasField(source.getClass(), "liste")) {
                if (target instanceof Collection) {
                    Collection targetCollection = (Collection) target;

                    Field item = source.getClass().getDeclaredField("liste");
                    item.setAccessible(true);
                    Object o = item.get(source);
                    if (o != null) {
                        for (Object next : ((Iterable) o)) {
                            targetCollection.add(mapping.w2d(next));
                        }
                    }
                } else {
                    throw new MappingException("Assumption that a List corresponds to wsapi field 'liste' failed");
                }
            } else if (source instanceof Collection && target instanceof Collection) {
                Collection sourceCollection = (Collection) source;
                Collection targetCollection = (Collection) target;
                for (Object next : sourceCollection) {
                    targetCollection.add(mapping.w2d(next));
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
                        Class<?> targetType = targetSetter.getParameterTypes()[0];
                        if (source1 != null) {
                            Object value = mapping.w2d(source1, targetSetter.getGenericParameterTypes()[0]);
                            if (value != null) {
                                //Sjekk om source = List og target = Set, fordi da håndterer vi settingen spesielt
                                if (Set.class.isAssignableFrom(targetType) && value instanceof List) {
                                    Set replaceListWithThisSet = new HashSet((List) value);
                                    targetSetter.invoke(target, replaceListWithThisSet);
                                } else {
                                    //Dersom dette ikke er en spesialsituasjon, så prøver vi den vanlige måten, så får vi evt. en feil
                                    targetSetter.invoke(target, value);
                                }
                            }
                        } else {
                            //Dersom value = null, så kan vi fremdeles sette den i target.
                            //Med midre typen er primitiv, da lar vi den bare være
                            if (!targetType.isPrimitive()) {
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
//        List<File> dirs = new ArrayList<File>();
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
                            Class _class;
                            String className = null;
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

    /**
     * Spesiell håndtering av at feltet heter ett eller annet "feltnavn" på den ene siden og "feltnavnId" på den andre.
     * I disse tilfellene så skal feltet som heter 'nesten' det samme returneres.
     * TODO: Denne spesialhåndteringen bør flyttes til matrikkelen
     */
    protected Method findSetterForGetter(Class<?> c, Method getter) {

        Method setter = settersForGetters.get(getter);
        if (setter != null) {
            return setter;
        }

        String expectedSetterName = 's' + getter.getName().substring(1);

        Method[] methods = c.getMethods();
        Method matched = null;
        boolean nameMatch = false;
        for (Method method : methods) {
            if (method.getParameterTypes().length == 1) {
                if (method.getName().equals(expectedSetterName)) {
                    nameMatch = true;
                    matched = method;
                } else if (!nameMatch && ((method.getName() + "Id").equals(expectedSetterName) || method.getName().equals(expectedSetterName + "Id"))) {
                    matched = method;
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