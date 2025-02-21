package no.statkart.skif.util;

import no.statkart.skif.exception.ImplementationException;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Hjelpeklasse for å liste alle filer i en gitt pakke.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class ResourceLister implements Iterable<String> {
    private final List<String> resources = new ArrayList<String>();

    /**
     * @param packageName pakken det skal letes i
     */
    public ResourceLister(String packageName) throws IOException {
        final String packagePath = packageName.replace('.', '/');

        Enumeration<URL> resources = getClass().getClassLoader().getResources(packagePath);

        while (resources.hasMoreElements()) {
            URL classpathUrl = resources.nextElement();

            if (classpathUrl.getProtocol().equals("file")) {
                listDirectory(new File(classpathUrl.getPath()));
            } else if (classpathUrl.getProtocol().equals("jar")) {
                listJar(classpathUrl, packagePath);
            } else if (classpathUrl.getProtocol().equals("zip")) {
                listZip(classpathUrl, packagePath);
            } else {
                throw new ImplementationException("Unknown protocol: " + classpathUrl.getProtocol());
            }
        }
    }

    private void listZip(URL classpathUrl, String packagePath) throws IOException {
        String filepath = classpathUrl.getPath();
        int idx = filepath.indexOf("!");
        String parsedJarName = filepath.substring(0, idx);
        FileInputStream inputStream = new FileInputStream(parsedJarName);
        try {
            ZipInputStream zipStream = new ZipInputStream(inputStream);
            try {
                ZipEntry zipEntry = zipStream.getNextEntry();
                while (zipEntry != null) {
                    String entryName = zipEntry.getName();
                    if (entryName.startsWith(packagePath)) {
                        resources.add(entryName.substring(packagePath.length() + 1));
                    }

                    zipStream.closeEntry();
                    zipEntry = zipStream.getNextEntry();
                }
            } finally {
                try {
                    zipStream.close();
                } catch (IOException ignored) {
                }
            }
        } finally {
            try {
                inputStream.close();
            } catch (IOException ignored) {
            }
        }
    }

    private void listJar(URL classpathUrl, String packagePath) throws IOException {
        JarURLConnection jarURLConnection = (JarURLConnection) classpathUrl.openConnection();
        JarFile jarFile = jarURLConnection.getJarFile();
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String entryName = jarEntry.getName();
            if (entryName.startsWith(packagePath)) {
                resources.add(entryName.substring(packagePath.length() + 1));
            }
        }
    }

    private void listDirectory(File directory) {
        for (File file : directory.listFiles(new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isFile();
            }
        })) {
            resources.add(file.getName());
        }
    }

    @Override
    public Iterator<String> iterator() {
        return Collections.unmodifiableList(resources).iterator();
    }
}
