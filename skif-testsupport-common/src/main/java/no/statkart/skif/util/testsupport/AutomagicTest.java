package no.statkart.skif.util.testsupport;

import com.google.common.reflect.TypeToken;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mapper.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Denne testklassen går gjennom alle klasser som ligger i den angitte wsapiPkg eller under og tester mappingen av de, ved å:
 * <ul>
 * <li>Opprette et testobjekt ved å instansiere klassen og fylle inn vilkårlige verdier i alle felter</li>
 * <li>Dersom feltet er av en abstrakt type så settes det inn en verdi med en vilkårlig konkret subklasse av den abstrakte typen</li>
 * <li>Klasser som er angitt i skipTestingForTheseClasses testes ikke</li>
 * <li>Klasser som er abstrakte, enten i wsapi modellen, eller den motstående klassen i domenemodellen, testes ikke.</li>
 * <li>Klasser som er lister, basert på at klassenavnet i wsapi-modellen slutter på *List, testes ikke, fordi disse uansett vil bli testet av </li>
 * <li>For de resterende mappes klassen fra wsapi-modellen, til domenemodellen, og tilbake. Så sjekkes det at det opprinnelige og remappede objektet er like.</li>
 * </ul>
 *
 * @author Steinar Hansen
 */
public class AutomagicTest {
    protected static Logger logger = LoggerFactory.getLogger(AutomagicTest.class);
    private Set<String> wsapiPkg = new HashSet<String>();
    private Set<String> domainPkg = new HashSet<String>();
    private Set<String> skipTestingForTheseClasses = new HashSet<String>();
    protected Map<String, List<Class>> className2ListOfSubclasses = new HashMap<String, List<Class>>();
    private Random randomGenerator = new Random();
    protected List<Class> wsapiClasses = new ArrayList<Class>();
    protected List<Class> domainClasses = new ArrayList<Class>();

    private static void addDeclaredAndInheritedFields(Class<?> c, Collection<Field> fields) {
        fields.addAll(Arrays.asList(c.getDeclaredFields()));
        Class<?> superClass = c.getSuperclass();
        if (superClass != null) {
            addDeclaredAndInheritedFields(superClass, fields);
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
    @SuppressWarnings("unchecked")
    protected static List<Class> getClasses(String packageName) throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        ArrayList<Class> classes = new ArrayList<Class>();
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
                    ZipEntry ze;
                    while ((ze = zip2.getNextEntry()) != null) {
                        String entryName = ze.getName();

                        logger.info(entryName);
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
                                throw new MappingException(e);
                                // happen, for example, in classes, which depend on
                                // Spring to inject some beans, and which fail,
                                // if dependency is not fulfilled
                                //_class = Class.forName(className, false, Thread.currentThread().getContextClassLoader());
                            }
                        }
                    }
                }
            } else {
                throw new ImplementationException("Unknown protocol: " + protocol);
            }
        }

        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
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
                    //_class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6), false, Thread.currentThread().getContextClassLoader());
                }
            }
        }
        return classes;
    }

    protected Map<String, List<Class>> discoverClassHierarchy(List<Class> classes, Map<String, List<Class>> className2ListOfSubclasses) throws ClassNotFoundException {
        for (Class next : classes) {
            String nextName = next.getName();
            String packageName = nextName.substring(0, nextName.lastIndexOf("."));
            boolean bpackage = getSkipTestingForTheseClasses().contains(packageName);
            boolean exact = getSkipTestingForTheseClasses().contains(nextName);
            boolean testClass = !(exact || bpackage);

            if (testClass) {
                if (next.getSuperclass() != null) {

                    List<Class> subclasses = className2ListOfSubclasses.get(next.getSuperclass().getName());
                    if (subclasses == null) {
                        subclasses = new ArrayList<Class>();
                        className2ListOfSubclasses.put(next.getSuperclass().getName(), subclasses);
                    }
                    subclasses.add(next);
                }
            }
        }
        return className2ListOfSubclasses;
    }

    protected <T> T generateDummyData(T o, String fieldPath) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        if (o != null) {
            List<Field> fields = new ArrayList<Field>();
            addDeclaredAndInheritedFields(o.getClass(), fields);
            for (Iterator<Field> iterator = fields.iterator(); iterator.hasNext(); ) {
                Field field = iterator.next();
                field.setAccessible(true);
                //static felter har ingenting med mapping å gjøre
                if (!Modifier.isStatic(field.getModifiers())) {
                    //Vi må 'kappe' referansegrafen et sted, og det gjøres enkelt (kanskje for enkelt) ved å si at når
                    //pakkestien blir lengre enn 10 pakker så traverserer vi ikke referansene lenger.
                    if (!(fieldPath.split("\\.").length > 10)) {
                        Object instance = generateInstanceForClass(field.getType());
                        if (instance != null) {
                            field.set(o, instance);
                        } else {
                            field.set(o, generateDummyValue(o.getClass(), TypeToken.of(field.getGenericType()), field, fieldPath));
                        }
                    }
                }
            }
        }
        return o;
    }

    protected Object generateDummyValue(Class<?> clazz, TypeToken<?> type, Field field, String fieldPath) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        final Object retVal;
        if (type.getRawType().isPrimitive()) {
            if (type.getRawType().equals(Integer.TYPE)) {
                if (clazz.toString().endsWith("SnapshotVersion") || clazz.toString().endsWith("Timestamp")) {
                    //Må bruke SnapshotVersion.CURRENT.getNanos() (som er 0) pga EnumKodeId som kun kan være current.
                    retVal = 0;
                } else {
                    retVal = randomGenerator.nextInt();
                }
            } else if (type.getRawType().equals(Float.TYPE)) {
                retVal = randomGenerator.nextFloat();
            } else if (type.getRawType().equals(Double.TYPE)) {
                retVal = randomGenerator.nextDouble();
            } else if (type.getRawType().equals(Long.TYPE)) {
                if (clazz.toString().endsWith("SnapshotVersion") || clazz.toString().endsWith("Timestamp")) {
                    //på grun av enumKodeId må vi bruke SnapshotVersion.CURRENT
                    retVal = 253370761200000L;
                } else if (clazz.toString().endsWith("KodeId") && field.getName().equals("value")) {
                    retVal = randomGenerator.nextInt(2);
                } else if (clazz.toString().endsWith("SprakformId") && field.getName().equals("value")) {
                    retVal = randomGenerator.nextInt(3);
                } else {
                    retVal = randomGenerator.nextLong();
                }
            } else if (type.getRawType().equals(Boolean.TYPE)) {
                retVal = true;
            } else {
                throw new ImplementationException("Primitive type " + type.getRawType() + " not supported");
            }
        } else if (type.getRawType().getSimpleName().equals("String")) {
            if (clazz.toString().endsWith("KodeId")) {
                //ikke så mange teseelementer i kodelisten. Begrenser antallet mulig verdier til [1,2]
                retVal = "" + (randomGenerator.nextInt(1) + 1);
            } else if (clazz.toString().endsWith("Id")) {
                //Id må settes til string men kun nummeric verdier
                retVal = "" + randomGenerator.nextInt();
            } else if (field.getName().equals("kodeIdClass")) {
                retVal = "no.statkart.skif.storetest.wsapi.domain.demo.koder.TestAEnumKodeId";
            } else {
                retVal = field.getName() + "_testdata_rnd_" + randomGenerator.nextInt(100);
            }
        } else if (type.getRawType().isArray()) {
            throw new IllegalArgumentException("Støtte for array-felter er ikke implementert enda.");
        } else if (type.getRawType().equals(XMLGregorianCalendar.class)) {
            retVal = createXMLGregorianCalendar(field);
        } else if (type.getRawType().equals(BigInteger.class)) {
            retVal = BigInteger.ONE;
        } else if (type.getRawType().equals(Boolean.class)) {
            retVal = Boolean.TRUE;
        } else if (type.getRawType().equals(Integer.class)) {
            retVal = randomGenerator.nextInt();
        } else if (type.getRawType().equals(Long.class)) {
            if (clazz.toString().endsWith("KodeId")) {
                retVal = Long.parseLong("" + randomGenerator.nextInt(1) + 1);
            } else if (clazz.toString().endsWith("SnapshotVersion") || clazz.toString().endsWith("Timestamp")) {
                retVal = 253370761200000L;
            } else if (clazz.toString().endsWith("formId")) {
                //ikke så mange teseelementer i kodelisten. Begrenser antallet mulig verdier til [1,2]
                retVal = "" + (randomGenerator.nextInt(1) + 1);
            } else {
                retVal = randomGenerator.nextLong();
            }
        } else if (type.getRawType().equals(Float.class)) {
            retVal = randomGenerator.nextFloat();
        } else if (type.getRawType().equals(Double.class)) {
            retVal = randomGenerator.nextDouble();
        } else if (type.getRawType().equals(List.class)) {
            ArrayList<Object> list = new ArrayList<Object>();
            ParameterizedType genericType = (ParameterizedType) type.getType();
            TypeToken<?> genericElementType = TypeToken.of(genericType.getActualTypeArguments()[0]);
            Object instanceForList = generateInstanceForClass(genericElementType.getRawType());
            if (instanceForList != null) {
                list.add(instanceForList);
            } else if (isClassAbstract(genericElementType.getRawType())) {
                list.add(generateDummyData(generateConcreteSubclass(genericElementType.getRawType()), fieldPath + "." + field.getName()));
            } else {
                if (genericElementType.getRawType().equals(String.class)) {
                    list.add(field.getName() + "_testdata_rnd_" + randomGenerator.nextInt(100));
                } else {
                    list.add(generateDummyValue(type.getRawType(), genericElementType, null, fieldPath + "." + field.getName()));
                }
            }
            retVal = list;
        } else if (isClassAbstract(type.getRawType())) {
            if (type.getRawType().getName().startsWith("no.")) {
                if ("id".equalsIgnoreCase(field.getName())) {
                    //generere for abstrakt id
                    retVal = generateDummyData(generateConcreteSubclassId(type.getRawType(), clazz.getName()), fieldPath + "." + field.getName());
                } else {
                    retVal = generateDummyData(generateConcreteSubclass(type.getRawType()), fieldPath + "." + field.getName());
                }
            } else {
                logger.debug("Hopper over: " + field.getName() + ", som er av type: " + type.getRawType() + ", og abstrakt, i klasse " + clazz.getName());
                retVal = null;
            }
        } else if (field != null && field.getName().equalsIgnoreCase("id") && field.getType().getName().endsWith("MatrikkelBubbleId")) {
            Object o2 = generateConcreteSubclassId(clazz.getName());
            generateDummyData(o2, fieldPath + "." + field.getName());
            retVal = o2;
        } else if (field != null && field.getName().equalsIgnoreCase("id") && type.getRawType().getName().endsWith("BubbleId")) {
            Object o2 = generateConcreteSubclassId(clazz.getName());
            generateDummyData(o2, fieldPath + "." + field.getName());
            retVal = o2;
        } else if (field != null && field.getName().endsWith("KodeId") && type.getRawType().getName().startsWith("no.")) {
            Object o2 = generateConcreteKodeId(type.getRawType());
            generateDummyData(o2, fieldPath + "." + field.getName());
            retVal = o2;
        } else {
            //recurse
            Object o2 = createNewInstance(type.getRawType());
            generateDummyData(o2, fieldPath + "." + (field != null ? field.getName() : "()"));
            retVal = o2;
        }
        return retVal;
    }

    protected boolean isClassAbstract(Class clazz) throws ClassNotFoundException {
        boolean retVal;
        retVal = Modifier.isAbstract(clazz.getModifiers());
        if (retVal) {
            return retVal;
        } else {
            String packageName = clazz.getPackage().getName();
            if (!packageName.startsWith("no.")) {
                return false;
            }
            String reroutedPackageName = packageName.replace(packageName, finnBesteMatch(packageName));
            if (reroutedPackageName == null) {
                throw new MappingException("Found not match between packages for class: " + clazz.getName());
            }

            Class<?> aClass;
            try {
                aClass = Class.forName(reroutedPackageName + "." + clazz.getSimpleName());
                if (Modifier.isAbstract(aClass.getModifiers()) || aClass.isInterface()) {
                    //Den "andre sidens" klasse er abstrakt. Håndter det som at denne sidens klasse er abstrakt
                    return true;
                }
            } catch (ClassNotFoundException e) {
                //Fant ikke denne klassen på den andre siden,
                //Dette skjer f.eks. for lister, som har navn som slutter på List i wsapi, men ikke i domain
                if (!clazz.getSimpleName().endsWith("List")) {
                    //Men ellers så er det sannsynligvis feil, men vil uansett bli håndtert av testen på mappingen, så vi bare ignorerer dette her
                    logger.debug("Fant ikke noen klasse: " + reroutedPackageName + "." + clazz.getSimpleName());
                }
            }

            return false;
        }

    }


    /**
     * Denne finner antatt beste match, ved å se på hvor mange av pakkenavn-elementene, tekst mellom punktum,
     * som er lik i domenemodellen. Den antar at den beste matchen vil få flest treff.
     * <p/>
     * Den antar også at man alltid søker fra wsapi-pakke til domene-pakke.
     *
     * @param finnDenne
     * @return
     */
    private CharSequence finnBesteMatch(String finnDenne) {
        String[] elementer = finnDenne.split("\\.");
        int maxScore = 0;
        String besteMatch = null;
        for (Iterator<String> iterator = getDomainPkg().iterator(); iterator.hasNext(); ) {
            int dennesScore = 0;
            String next = iterator.next();
            if (next.equals(finnDenne)) {
                break;
            }
            for (int i = 0; i < elementer.length; i++) {
                String s = elementer[i];
                if (next.contains(s)) {
                    dennesScore++;
                }
            }
            if (dennesScore >= maxScore) {
                maxScore = dennesScore;
                besteMatch = next;
            }
        }

        return besteMatch;
    }

    private Object generateConcreteKodeId(Class clazz) {
        if (clazz == null) {
            throw new MappingException("Can not generate id for null class");
        }

        Object o;
        try {
            o = clazz.newInstance();
        } catch (InstantiationException e) {
            throw new MappingException("Can not instantiate class " + clazz.getName());
        } catch (IllegalAccessException e) {
            throw new MappingException("Can not access constructor for " + clazz.getName());
        }
        return o;
    }

    private Object generateConcreteSubclassId(Class clazz, String inClazzName) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        if (inClazzName == null) {
            throw new MappingException("Can not generate id for null class");
        }
        if (!isClassAbstract(clazz)) {
            return generateConcreteSubclass(clazz);
        }

        //finn id for inClazzName
        String idName = inClazzName + "Id";
        Class idClazz = Class.forName(idName);
        return idClazz.newInstance();
    }

    private Object generateConcreteSubclassId(String inClazzName) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        if (inClazzName == null) {
            throw new MappingException("Can not generate id for null class");
        }

        //finn id for inClazzName
        String idName = inClazzName + "Id";
        Class idClazz = Class.forName(idName);

        return idClazz.newInstance();
    }

    private Object generateConcreteSubclass(Class clazz) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        List<Class> subclasses = className2ListOfSubclasses.get(clazz.getName());
        if (subclasses == null) {
            throw new MappingException("Found no concrete subclass for class: " + clazz.getName());
        }
        Class subclass = subclasses.get(randomGenerator.nextInt(subclasses.size()));
        if (isClassAbstract(subclass)) {
            return generateConcreteSubclass(subclass);
        } else {
            return subclass.newInstance();
        }
    }

    protected Object generateInstanceForClass(Class clazz) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        return null;
    }

    protected <T> T createNewInstance(Class<T> clazz) throws IllegalAccessException, InstantiationException {
        T t = null;
        if (clazz.equals(Long.class)) {
            t = clazz.cast(new Long(randomGenerator.nextLong()));
        } else if (clazz.equals(BigInteger.class)) {
            t = clazz.cast(BigInteger.ONE); // Caster for å unngå warning
        } else {
            t = clazz.newInstance();
        }
        return t;
    }

    protected GregorianCalendar createPureGregorianCalendar(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.clear();
        calendar.setTime(date);
        calendar.setGregorianChange(new Date(Long.MIN_VALUE));
        return calendar;
    }

    protected XMLGregorianCalendar createXMLGregorianCalendar(Field field) {
        final GregorianCalendar gregorianCalendar = createPureGregorianCalendar(new Date());

        try {
            XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);

            XmlSchemaType xmlSchemaType = field.getAnnotation(XmlSchemaType.class);
            if (xmlSchemaType != null) {
                String type = xmlSchemaType.name();
                if (type.equals("date")) {
                    xmlGregorianCalendar.setHour(DatatypeConstants.FIELD_UNDEFINED);
                    xmlGregorianCalendar.setMinute(DatatypeConstants.FIELD_UNDEFINED);
                    xmlGregorianCalendar.setSecond(DatatypeConstants.FIELD_UNDEFINED);
                    xmlGregorianCalendar.setFractionalSecond(null);
                } else if (type.equals("time")) {
                    xmlGregorianCalendar.setYear(DatatypeConstants.FIELD_UNDEFINED);
                    xmlGregorianCalendar.setMonth(DatatypeConstants.FIELD_UNDEFINED);
                    xmlGregorianCalendar.setDay(DatatypeConstants.FIELD_UNDEFINED);
                }
            }

            return xmlGregorianCalendar;
        } catch (DatatypeConfigurationException e) {
            throw new ImplementationException(e);
        }

    }

    public Set<String> getWsapiPkg() {
        return wsapiPkg;
    }

    public void setWsapiPkg(Set<String> wsapiPkg) {
        this.wsapiPkg = wsapiPkg;
    }

    public Set<String> getDomainPkg() {
        return domainPkg;
    }

    public void setDomainPkg(Set<String> domainPkg) {
        this.domainPkg = domainPkg;
    }

    public Set<String> getSkipTestingForTheseClasses() {
        return skipTestingForTheseClasses;
    }

    public void setSkipTestingForTheseClasses(Set<String> skipTestingForTheseClasses) {
        this.skipTestingForTheseClasses = skipTestingForTheseClasses;
    }

    /**
     * initierer testen, her må 'toppen' av pakkestien for wsapi- og domene-klasser legges inn. Alle klasser under disse
     * vil (stort sett) oppdages automatisk.
     *
     * @throws ClassNotFoundException
     * @throws java.io.IOException
     */
    protected void discoverClassHierarchy() throws ClassNotFoundException, IOException {
        for (String s : getWsapiPkg()) {
            wsapiClasses.addAll(getClasses(s));
        }
        discoverClassHierarchy(wsapiClasses, className2ListOfSubclasses);

        for (String s : getDomainPkg()) {
            domainClasses.addAll(getClasses(s));
        }
        discoverClassHierarchy(domainClasses, className2ListOfSubclasses);
    }
}
