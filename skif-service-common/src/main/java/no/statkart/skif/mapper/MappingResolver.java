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
                JarURLConnection jarURLConnection = (JarURLConnection) resource.openConnection();
                JarFile jarFile = jarURLConnection.getJarFile();
                Enumeration<JarEntry> jarEntries = jarFile.entries();
                while (jarEntries.hasMoreElements()) {
                    JarEntry ze = jarEntries.nextElement();
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
}