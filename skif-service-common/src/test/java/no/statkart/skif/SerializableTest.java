package no.statkart.skif;

import com.google.common.collect.ImmutableSet;
import org.reflections.Reflections;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Tester at serialiserbare klasser i kjente pakker er korrekt implementert.
 */
@Test
public class SerializableTest {

    static final List<String> packages = Arrays.asList(
            SerializableTest.class.getPackage().getName()
    );

    static final Set<Class> excludeClasses = ImmutableSet.of(
            Enum.class,
            no.statkart.skif.config.ConfigurationRuntimeException.class,
            no.statkart.skif.config.ConversionException.class
    );


    /**
     * Verifiserer alle kjente klasser som er serialiserbare har fått
     * satt serialVersionUID ihht standard
     */
    public void testSerialVersionUID() {
        LinkedHashSet<String> feilliste = new LinkedHashSet<String>();


        for (String packageName : packages) {

            Assert.assertTrue(packageName.startsWith("no.statkart.skif"));

            Reflections reflections = new Reflections(packageName);
            Set<Class<? extends Serializable>> classes = reflections.getSubTypesOf(Serializable.class);
            for (Class<?> clazz : classes) {
                if (clazz.isEnum() || excludeClasses.contains(clazz)) continue; // Enumer serialiseres ikke feltene for

                //sjekker mot feltet serialVersionUID
                while( clazz != null && Serializable.class.isAssignableFrom(clazz) ) {
                    if (clazz.isInterface()) break;
                    try {
                        Field field = clazz.getDeclaredField("serialVersionUID");
                    } catch( NoSuchFieldException e ) {
                        feilliste.add(String.format("\nForventet at klasse %s deklarerer feltet 'serialVersionUID'", clazz.getName()));
                    }

                    clazz = clazz.getSuperclass();
                }
            }
        }

        if( !feilliste.isEmpty() ) {
            for( String msg : feilliste ) {
                System.out.print(msg);
            }
            feilliste.add("");
            Assert.fail(feilliste.toString());
        }

    }

}
