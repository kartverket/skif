package no.statkart.skif.mapper;

import com.google.common.reflect.TypeToken;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.util.TypeUtils;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Denne klassen forsøker å finne ut hvilke klasser som tilsvarer hverandre basert på antagelsen at klassenavnene er
 * like, klassene ligger bare i forskjellige pakker.
 * <p>
 * For å benytte klassen så setter man inn denne med {@link AbstractMapper#setMappingResolver(MappingResolver)}.
 * For å benytte MappingResolver må også en packageMapping legges inn. Ved bruk av addPackageMapping er det mulig å
 * mappe alle klasser i en pakke og subpakker til klasser i en annen pakke og subpakkker med samme navn.</p>
 * <p>
 * F.eks.
 * <pre><code>
 * MappingResolver resolver = new MappingResolver();
 * resolver.addPackageMapping("no.statkart.grunnbok.borett.info.wsapi.domain", "no.statkart.grunnbok.borett.info.domain");
 * setMappingResolver(resolver);
 * </code></pre>
 * <p>
 * Hensikten med defaultmapperen er at den skal benyttes ved "defaulting" som i en switch-statement. Dersom ingen annen typemapping
 * finnes så faller typemappingen tilbake til denne.</p>
 *
 * @author Steinar Hansen
 * @author Tor Egil R. Strand
 */
public class MappingResolver {
    protected Map<String, String> wsapiPkg2domainPkg = new HashMap<String, String>();
    protected Map<String, String> domainPkg2wsapiPkg = new HashMap<String, String>();
    protected Map<Class, Class> classMappings = new HashMap<Class, Class>();
    protected Map<? extends Class<?>, ? extends Class<?>> overrideClassMappings;

    public MappingResolver() {
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
                for (Class domainClass : domainClasses) {
                    if (isEquivalent(domainClass, wsapiClass)) {
                        classMappings.put(wsapiClass, domainClass);
                    }
                }
            }
            for (Class domainClass : domainClasses) {
                for (Class wsapiClass : wsapiClasses) {
                    if (isEquivalent(domainClass, wsapiClass)) {
                        classMappings.put(domainClass, wsapiClass);
                    }
                }
            }

        } catch (ClassNotFoundException | IOException e) {
            throw new ImplementationException(e);
        }
    }

    protected boolean isEquivalent(Class<?> domainClass, Class<?> wsapiClass) {
        String wsapiName = wsapiClass.getSimpleName();
        String domainName = domainClass.getSimpleName();

        return wsapiName.equals(domainName);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void overrideClassMappings(Map<? extends Class<?>, ? extends Class<?>> classMappings) {
        this.overrideClassMappings = classMappings;
    }

    public TypeToken<?> resolveTargetType(Class sourceClass, TypeToken<?> targetType) {
        TypeToken<?> retVal = null;
        if (this.overrideClassMappings != null) {
            Class<?> clazz = this.overrideClassMappings.get(sourceClass);
            if (clazz != null) {
                retVal = TypeUtils.getSubtype(targetType, clazz).wrap();
            }
        }
        if (retVal == null) {
            if (classMappings.containsKey(sourceClass)) {
                retVal = TypeUtils.getSubtype(targetType, classMappings.get(sourceClass)).wrap();
            }
        }

        // Finner ikke noe bedre
        if (retVal == null) {
            retVal = targetType;
        }

        return retVal;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException If a file found in the directory appears to be a class for Class.forName(...) fails
     */
    protected static List<Class> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        //noinspection ConstantConditions
        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                assert fileName.indexOf('.') < 0;
                classes.addAll(findClasses(file, packageName + '.' + fileName));
            } else if (fileName.endsWith(".class") && fileName.indexOf('$') < 0 && !fileName.endsWith("package-info.class") && !fileName.endsWith("ObjectFactory.class")) {
                try {
                    Class<?> _class = Class.forName(packageName + '.' + fileName.substring(0, fileName.length() - 6));
                    classes.add(_class);
                } catch (ExceptionInInitializerError e) {
                    throw new MappingException(e);
                    // happen, for example, in classes, which depend on
                    // Spring to inject some beans, and which fail,
                    // if dependency is not fulfilled
                }

            }
        }
        return classes;
    }

    private static void tryCollectClassFromEntry(Set<Class> classes, String entryName) throws ClassNotFoundException {
        if (entryName.endsWith(".class")
                && entryName.indexOf('$') < 0
                && !entryName.endsWith("package-info.class")
                && !entryName.endsWith("ObjectFactory.class")) {

            try {
                String className = entryName.substring(0, entryName.length() - 6) //6 = length of ".class"
                        .replace('/', '.');
                Class<?> _class = Class.forName(className);
                classes.add(_class);
            } catch (ExceptionInInitializerError e) {
                // happen, for example, in classes, which depend on
                // Spring to inject some beans, and which fail,
                // if dependency is not fulfilled
                throw new MappingException(e);
            }
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
        final String pathForPackageName = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(pathForPackageName);
        Set<Class> classes = new HashSet<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String protocol = resource.getProtocol();
            if (protocol.equals("file")) {
                String fileName = resource.getFile();
                String fileNameDecoded = URLDecoder.decode(fileName, "UTF-8");
                final File e = new File(fileNameDecoded);
                if (e.isDirectory()) {
                    classes.addAll(findClasses(e, packageName));
                }
            } else if (protocol.equals("jar")) {
                //PS: Don't close jar-resources as they are being managed by the classloader
                JarFile jarFile = ((JarURLConnection) resource.openConnection()).getJarFile();
                Enumeration<JarEntry> jarEntries = jarFile.entries();
                while (jarEntries.hasMoreElements()) {
                    JarEntry ze = jarEntries.nextElement();
                    String entryName = ze.getName();

                    // Laster kun klasser som ligger under packageName (inkl. underpakker)
                    if (entryName.startsWith(pathForPackageName)) {
                        tryCollectClassFromEntry(classes, entryName);
                    }
                }
            } else if (protocol.equals("zip")) {
                String filepath = resource.getPath();
                int idx = filepath.indexOf('!');
                String parsedJarName = filepath.substring(0, idx);
                URL resource2 = new File(parsedJarName).toURI().toURL();
                try (ZipInputStream zip2 = new ZipInputStream(resource2.openStream())) {
                    ZipEntry ze;
                    while ((ze = zip2.getNextEntry()) != null) {
                        String entryName = ze.getName();

                        // Laster kun klasser som ligger under packageName (inkl. underpakker)
                        if (entryName.startsWith(pathForPackageName)) {
                            tryCollectClassFromEntry(classes, entryName);
                        }
                    }
                }

            } else {
                throw new ImplementationException("Unknown protocol: " + protocol);
            }
        }

        if (!recurse) {
            List<Class> trimmedClasses = new ArrayList<>();
            for (Class<?> c : classes) {
                if (c.getPackage().getName().equals(packageName)) {
                    trimmedClasses.add(c);
                }
            }
            return trimmedClasses;
        }

        return new ArrayList<>(classes);
    }
}