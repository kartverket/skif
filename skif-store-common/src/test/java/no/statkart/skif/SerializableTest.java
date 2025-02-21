package no.statkart.skif;

import com.google.common.collect.ImmutableSet;
import no.statkart.skif.exception.ObjectNotFoundException;
import no.statkart.skif.locker.LockInfo;
import no.statkart.skif.store.AbstractBubbleId;
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

    static final List<Package> packages = Arrays.asList(
            ObjectNotFoundException.class.getPackage(),
            LockInfo.class.getPackage(),
            AbstractBubbleId.class.getPackage()
    );


    static final Set<Class> excludeClasses = ImmutableSet.of(
            Enum.class
    );

    /**
     * Verifiserer alle kjente klasser som er serialiserbare har fått
     * satt serialVersionUID ihht standard
     */
    public void testSerialVersionUID() {
        LinkedHashSet<String> feilliste = new LinkedHashSet<String>();


        for (Package aPackage : packages) {
            Reflections reflections = new Reflections(aPackage.getName());
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
