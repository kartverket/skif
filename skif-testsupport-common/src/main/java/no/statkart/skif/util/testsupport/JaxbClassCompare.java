package no.statkart.skif.util.testsupport;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.ws.WebFault;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Et lite program for å sammenlikne to jar-filer som i teorien skal inneholde de samme klassene basert på like
 * XSD-filer, typisk jar-ene fra wsschema og wsclient.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class JaxbClassCompare {
    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("Requires two jar files as arguments");
            System.exit(-1);
        }
        File jar1 = new File(args[0]);
        File jar2 = new File(args[1]);

        LinkedHashSet<String> classNames = new LinkedHashSet<String>();

        JarFile jarFile = new JarFile(jar1);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            if (name.endsWith(".class")) {
                String className = name.substring(0, name.length() - 6).replace('/', '.');
                classNames.add(className);
            }
        }
        jarFile.close();

        ClassLoader classLoader1 = new URLClassLoader(new URL[]{jar1.toURI().toURL()});
        ClassLoader classLoader2 = new URLClassLoader(new URL[]{jar2.toURI().toURL()});

        for (String className : classNames) {
            final Class class1, class2;
            try {
                class1 = Class.forName(className, false, classLoader1);
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found in " + jar1 + ": " + className);
                continue;
            }
            try {
                class2 = Class.forName(className, false, classLoader2);
            } catch (ClassNotFoundException e) {
                System.err.println("Class not found in " + jar2 + ": " + className);
                continue;
            }

            System.out.println(className);

            compareClasses(className, class1, class2);
        }
    }

    private static void compareClasses(String className, Class class1, Class class2) {
        Annotation[] annotations1 = class1.getAnnotations();
        Annotation[] annotations2 = class2.getAnnotations();

        compareAnnotations(className, annotations1, annotations2);

        Field[] fields1 = class1.getDeclaredFields();
        for (Field field1 : fields1) {
            try {
                final Field field2 = class2.getDeclaredField(field1.getName());

                compareAnnotations(field1.toString(), field1.getAnnotations(), field2.getAnnotations());
            } catch (NoSuchFieldException e) {
                System.err.println("No field " + field1.getName() + " for copy 2 of " + className);
            }
        }
    }

    private static void compareAnnotations(String elementName, Annotation[] annotationArray1, Annotation[] annotationArray2) {
        Map<Class<? extends Annotation>, Annotation> annotationMap1 = new LinkedHashMap<Class<? extends Annotation>, Annotation>(annotationArray1.length);
        Map<Class<? extends Annotation>, Annotation> annotationMap2 = new LinkedHashMap<Class<? extends Annotation>, Annotation>(annotationArray2.length);

        for (Annotation annotation : annotationArray1) {
            annotationMap1.put(annotation.annotationType(), annotation);
        }
        for (Annotation annotation : annotationArray2) {
            annotationMap2.put(annotation.annotationType(), annotation);
        }

        for (Map.Entry<Class<? extends Annotation>, Annotation> entry : annotationMap1.entrySet()) {
            Annotation annotation1 = entry.getValue();
            Annotation annotation2 = annotationMap2.remove(entry.getKey());

            if (annotation2 == null) {
                System.err.println(entry.getKey() + " only found on copy 1 of " + elementName);
            }

            compareAnnotation(elementName, annotation1, annotation2);
        }

        for (Class<? extends Annotation> key : annotationMap2.keySet()) {
            System.err.println(key + " only found on copy 2 of " + elementName);
        }
    }

    private static void compareAnnotation(String elementName, Annotation annotation1, Annotation annotation2) {
        if (annotation1 instanceof XmlType) {
            XmlType xmlType1 = (XmlType) annotation1;
            XmlType xmlType2 = (XmlType) annotation2;
            if (!xmlType1.name().equals(xmlType2.name())) {
                System.err.println(elementName + ": XmlType.name not equals, " + xmlType1.name() + " / " + xmlType2.name());
            }
            if (!xmlType1.factoryClass().equals(xmlType2.factoryClass())) {
                System.err.println(elementName + ": XmlType.factoryClas not equals, " + xmlType1.factoryClass() + " / " + xmlType2.factoryClass());
            }
            if (!xmlType1.factoryMethod().equals(xmlType2.factoryMethod())) {
                System.err.println(elementName + ": XmlType.factoryMethod not equals, " + xmlType1.factoryMethod() + " / " + xmlType2.factoryMethod());
            }
            if (!xmlType1.namespace().equals(xmlType2.namespace())) {
                System.err.println(elementName + ": XmlType.namespace not equals, " + xmlType1.namespace() + " / " + xmlType2.namespace());
            }
            if (!Arrays.equals(xmlType1.propOrder(), xmlType2.propOrder())) {
                System.err.println(elementName + ": XmlType.propOrder not equals, " + Arrays.toString(xmlType1.propOrder()) + " / " + Arrays.toString(xmlType2.propOrder()));
            }
        } else if (annotation1 instanceof XmlAccessorType) {
            XmlAccessorType xmlAccessorType1 = (XmlAccessorType) annotation1;
            XmlAccessorType xmlAccessorType2 = (XmlAccessorType) annotation2;
            if (!xmlAccessorType1.value().equals(xmlAccessorType2.value())) {
                System.err.println(elementName + ": XmlAccessorType.value not equals, " + xmlAccessorType1.value() + " / " + xmlAccessorType2.value());
            }
        } else if (annotation1 instanceof XmlSeeAlso) {
            XmlSeeAlso xmlSeeAlso1 = (XmlSeeAlso) annotation1;
            XmlSeeAlso xmlSeeAlso2 = (XmlSeeAlso) annotation2;
            Class[] classes1 = xmlSeeAlso1.value().clone();
            Class[] classes2 = xmlSeeAlso2.value().clone();

            Arrays.sort(classes1, new ClassComparator());
            Arrays.sort(classes2, new ClassComparator());

            String classNames1 = Arrays.toString(classes1);
            String classNames2 = Arrays.toString(classes2);

            if (!classNames1.equals(classNames2)) {
                System.err.println(elementName + ": XmlSeeAlso.value not equals, " + classNames1 + " / " + classNames2);
            }
        } else if (annotation1 instanceof XmlSchema) {
            XmlSchema xmlSchema1 = (XmlSchema) annotation1;
            XmlSchema xmlSchema2 = (XmlSchema) annotation2;
            if (!xmlSchema1.namespace().equals(xmlSchema2.namespace())) {
                System.err.println(elementName + ": XmlSchema.namespace not equals, " + xmlSchema1.namespace() + " / " + xmlSchema2.namespace());
            }
            if (!xmlSchema1.location().equals(xmlSchema2.location())) {
                System.err.println(elementName + ": XmlSchema.location not equals, " + xmlSchema1.location() + " / " + xmlSchema2.location());
            }
            if (!xmlSchema1.elementFormDefault().equals(xmlSchema2.elementFormDefault())) {
                System.err.println(elementName + ": XmlSchema.elementFormDefault not equals, " + xmlSchema1.elementFormDefault() + " / " + xmlSchema2.elementFormDefault());
            }
            if (xmlSchema1.elementFormDefault() != XmlNsForm.QUALIFIED) {
                System.err.println(elementName + ": XmlSchema.elementFormDefault for copy 1 is not QUALIFIED, but " + xmlSchema1.elementFormDefault());
            }
        } else //noinspection StatementWithEmptyBody
            if (annotation1 instanceof XmlRegistry) {
        } else if (annotation1 instanceof WebFault) {
            WebFault webFault1 = (WebFault) annotation1;
            WebFault webFault2 = (WebFault) annotation2;
            if (!webFault1.name().equals(webFault2.name())) {
                System.err.println(elementName + ": WebFault.name not equals, " + webFault1.name() + " / " + webFault2.name());
            }
            if (!webFault1.targetNamespace().equals(webFault2.targetNamespace())) {
                System.err.println(elementName + ": WebFault.targetNamespace not equals, " + webFault1.targetNamespace() + " / " + webFault2.targetNamespace());
            }
            if (!webFault1.faultBean().equals(webFault2.faultBean())) {
                System.err.println(elementName + ": WebFault.faultBean not equals, " + webFault1.faultBean() + " / " + webFault2.faultBean());
            }
        } else if (annotation1 instanceof XmlElement) {
            XmlElement xmlElement1 = (XmlElement) annotation1;
            XmlElement xmlElement2 = (XmlElement) annotation2;
            if (!xmlElement1.name().equals(xmlElement2.name())) {
                System.err.println(elementName + ": XmlElement.name not equals, " + xmlElement1.name() + " / " + xmlElement2.name());
            }
            if (!xmlElement1.namespace().equals(xmlElement2.namespace())) {
                System.err.println(elementName + ": XmlElement.namespace not equals, " + xmlElement1.namespace() + " / " + xmlElement2.namespace());
            }
            if (xmlElement1.required() != xmlElement2.required()) {
                System.err.println(elementName + ": XmlElement.required not equals, " + xmlElement1.required() + " / " + xmlElement2.required());
            }
            if (xmlElement1.nillable() != xmlElement2.nillable()) {
                System.err.println(elementName + ": XmlElement.nillable not equals, " + xmlElement1.nillable() + " / " + xmlElement2.nillable());
            }
        } else {
            System.out.println("Unhandled annotation: " + annotation1.annotationType());
        }
    }

    private static class ClassComparator implements Comparator<Class> {
        @Override
        public int compare(Class o1, Class o2) {
            return o1.getName().compareTo(o2.getName());
        }
    }
}
