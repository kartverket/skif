package no.statkart.skif.util.testsupport;

import no.statkart.skif.mapper.MappingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
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
    protected Logger logger = LoggerFactory.getLogger(AutomagicTest.class);
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
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String fileName = resource.getFile();
            String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
            dirs.add(new File(fileNameDecoded));
        }
        ArrayList<Class> classes = new ArrayList<Class>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
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
    @SuppressWarnings("unchecked")
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
                } catch (ExceptionInInitializerError e) {
                    // happen, for example, in classes, which depend on
                    // Spring to inject some beans, and which fail,
                    // if dependency is not fulfilled
                    _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6), false, Thread.currentThread().getContextClassLoader());
                }
                classes.add(_class);
            }
        }
        return classes;
    }

    protected Map<String, List<Class>> discoverClassHierarchy(List<Class> classes, Map<String, List<Class>> className2ListOfSubclasses) throws ClassNotFoundException {
        for (Iterator<Class> iterator = classes.iterator(); iterator.hasNext(); ) {
            Class next = iterator.next();
            if (!getSkipTestingForTheseClasses().contains(next.getName())) {
                if (next != null && next.getSuperclass() != null) {

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

    protected <T extends Object> T generateDummyData(T o, String fieldPath) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        T retVal = null;
        if (o != null) {
            retVal = o;
            List<Field> fields = new ArrayList<Field>();
            addDeclaredAndInheritedFields(retVal.getClass(), fields);
            for (Iterator<Field> iterator = fields.iterator(); iterator.hasNext(); ) {
                Field field = iterator.next();
                field.setAccessible(true);
                if (Modifier.isFinal(field.getModifiers()) && Modifier.isStatic(field.getModifiers())) {
                    //final static felter er virkelig konstante
                    //Ingen vits å forsøke å sette et felt som er virkelig konstant
                    continue;
                } else {
                    //Vi må 'kappe' referansegrafen et sted, og det gjøres enkelt (kanskje for enkelt) ved å si at når
                    //pakkestien blir lengre enn 10 pakker så traverserer vi ikke referansene lenger.
                    if (!(fieldPath.split("\\.").length > 10)) {
                        if (field.getType().isPrimitive()) {
                            if (field.getType().equals(Integer.TYPE)) {
                                if(o.getClass().toString().endsWith("SnapshotVersion") || o.getClass().toString().endsWith("Timestamp")){
                                    //Må bruke SnapshotVersion.CURRENT.getNanos() (som er 0) pga EnumKodeId som kun kan være current.
                                    field.set(retVal, 0);
                                } else {
                                    field.set(retVal, randomGenerator.nextInt());
                                }
                            } else if (field.getType().equals(Float.TYPE)) {
                                field.set(retVal, randomGenerator.nextFloat());
                            } else if (field.getType().equals(Double.TYPE)) {
                                field.set(retVal, randomGenerator.nextDouble());
                            } else if (field.getType().equals(Long.TYPE)) {
                                if(o.getClass().toString().endsWith("SnapshotVersion") || o.getClass().toString().endsWith("Timestamp")){
                                    //på grun av enumKodeId må vi bruke SnapshotVersion.CURRENT
                                   field.set(retVal, 253370761200000L );
                                } else {
                                    field.set(retVal, randomGenerator.nextLong());
                                }
                            } else if (field.getType().equals(Boolean.TYPE)) {
                                field.set(retVal, true);
                            }
                        } else if (field.getType().getSimpleName().equals("String")) {
                            if (o.getClass().toString().endsWith("KodeId")) {
                                //ikke så mange teseelementer i kodelisten. Begrenser antallet mulig verdier til [1,2]
                                field.set(retVal, ""+(randomGenerator.nextInt(1)+1));
                            } else if (o.getClass().toString().endsWith("Id")) {
                                //Id må settes til string men kun nummeric verdier
                                field.set(retVal, ""+randomGenerator.nextInt());
                            } else {
                                field.set(retVal, field.getName() + "_testdata_rnd_" + randomGenerator.nextInt(100));
                            }
                        } else if (field.getType().isArray()) {
                            throw new IllegalArgumentException("Støtte for array-felter er ikke implementert enda.");
                        } else if (field.getType().equals(XMLGregorianCalendar.class)) {
                            field.set(retVal, createXMLGregorianCalendar(null));
                        } else if (field.getType().equals(BigInteger.class)) {
                            field.set(retVal, BigInteger.ONE);
                        } else if (field.getType().equals(Boolean.class)) {
                            field.set(retVal, Boolean.TRUE);
                        } else if (field.getType().equals(Integer.class)) {
                            field.set(retVal, randomGenerator.nextInt());
                        } else if (field.getType().equals(Long.class)) {
                            field.set(retVal, randomGenerator.nextLong());
                        } else if (field.getType().equals(Float.class)) {
                            field.set(retVal, randomGenerator.nextFloat());
                        } else if (field.getType().equals(Double.class)) {
                            field.set(retVal, randomGenerator.nextDouble());
                        } else if (field.getType().equals(List.class)) {
                            ArrayList list = new ArrayList();
                            ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                            Class<?> genericClass = (Class<?>) genericType.getActualTypeArguments()[0];
                            if (isClassAbstract(genericClass)) {
                                list.add(generateDummyData(generateConcreteSubclass(genericClass), fieldPath + "." + field.getName()));
                            } else {
                                list.add(generateDummyData(createNewInstance(genericClass), fieldPath + "." + field.getName()));
                            }
                            field.set(retVal, list);
                        } else if (isClassAbstract(field.getType())) {
                            if (field.getType().getName().startsWith("no.")) {
                                if("id".equalsIgnoreCase(field.getName())){
                                    //generere for abstrakt id
                                    field.set(retVal, generateDummyData(generateConcreteSubclassId(field.getType(),o.getClass().getName()), fieldPath + "." + field.getName()));
                                } else {
                                    field.set(retVal, generateDummyData(generateConcreteSubclass(field.getType()), fieldPath + "." + field.getName()));
                                }
                            } else {
                                logger.debug("Hopper over: " + field.getName() + ", som er av type: " + field.getType() + ", og abstrakt, i klasse " + o.getClass().getName());
                            }
                        } else {
                            //recurse
                            Object o2 = createNewInstance(field.getType());
                            generateDummyData(o2, fieldPath + "." + field.getName());
                            field.set(retVal, o2);
                        }
                    }
                }
            }
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
                throw new MappingException("Fant ikke match mellom pakkene for klassen: " + clazz.getName());
            }

            Class<?> aClass;
            try {
                aClass = Class.forName(reroutedPackageName + "." + clazz.getSimpleName());
                if (Modifier.isAbstract(aClass.getModifiers())) {
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
        Iterator<String> domainIter = getDomainPkg().iterator();
        for (Iterator<String> iterator = domainIter; iterator.hasNext(); ) {
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
            if (dennesScore > maxScore) {
                maxScore = dennesScore;
                besteMatch = next;
            }
        }

        return besteMatch;
    }

    private Object generateConcreteSubclassId(Class clazz, String inClazzName) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        if(inClazzName == null){
            throw new MappingException("Kan ikke generere id for NULL kasse");
        }
        if(!isClassAbstract(clazz)){
            return generateConcreteSubclass(clazz);
        }

        //finn id for inClazzName
        String idName = inClazzName+"Id";
        Class idClazz = Class.forName(idName);
        return idClazz.newInstance();
    }

    private Object generateConcreteSubclass(Class clazz) throws IllegalAccessException, InstantiationException, ClassNotFoundException {
        List<Class> subclasses = className2ListOfSubclasses.get(clazz.getName());
        if (subclasses == null) {
            throw new MappingException("Fant ingen konkrete subklasser for klassen: " + clazz.getName());
        }
        Class subclass = subclasses.get(randomGenerator.nextInt(subclasses.size()));
        if (isClassAbstract(subclass)) {
            return generateConcreteSubclass(subclass);
        } else {
            return subclass.newInstance();
        }
    }

    protected <T> T createNewInstance(Class clazz) throws IllegalAccessException, InstantiationException {
        T t = null;
        if (clazz.equals(Long.class)) {
            t = (T) new Long(randomGenerator.nextLong());
        } else if (clazz.equals(BigInteger.class)) {
            t = (T) BigInteger.ONE;
        } else {
            t = (T) clazz.newInstance();
        }
        return t;
    }

    private Field getFieldWithInheritedFields(Class<?> c, String fieldname) {
        Collection<Field> fields = new ArrayList<Field>();
        addDeclaredAndInheritedFields(c, fields);
        for (Iterator<Field> iterator = fields.iterator(); iterator.hasNext(); ) {
            Field next = iterator.next();
            if (next.getName().equals(fieldname)) {
                return next;
            }
        }
        return null;
    }

    private GregorianCalendar createPureGregorianCalendar(Date date) {
        GregorianCalendar calendar = new GregorianCalendar();
        calendar.clear();
        calendar.setTime(date);
        calendar.setGregorianChange(new Date(Long.MIN_VALUE));
        return calendar;
    }

    private XMLGregorianCalendar createXMLGregorianCalendar(Date date) {

        final GregorianCalendar gregorianCalendar = createPureGregorianCalendar(date == null ? new Date() : date);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
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
        for (Iterator<String> iterator = getWsapiPkg().iterator(); iterator.hasNext(); ) {
            wsapiClasses.addAll(getClasses(iterator.next()));
        }
        discoverClassHierarchy(wsapiClasses, className2ListOfSubclasses);

        for (Iterator<String> iterator = getDomainPkg().iterator(); iterator.hasNext(); ) {
            domainClasses.addAll(getClasses(iterator.next()));
        }
        discoverClassHierarchy(domainClasses, className2ListOfSubclasses);
    }
}
