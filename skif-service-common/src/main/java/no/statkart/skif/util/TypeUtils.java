package no.statkart.skif.util;

import com.google.common.reflect.TypeToken;

import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;

@SuppressWarnings({"UnstableApiUsage", "unchecked"})
public class TypeUtils {
    /**
     * Workaround for ting {@link TypeToken#getSubtype(Class)} ikke gjør.
     */
    public static <T> TypeToken<? extends T> getSubtype(final TypeToken<T> typeToken, Class<?> subClass) {
        if (typeToken.getRawType().isPrimitive()) {
            return typeToken;
        }

        // Dersom subklassen, og eventuelle eiende klasser, ikke har noen typeparametre, så er det ikke mer å gjøre.
        // Dersom den arver fra typeToken uten å angi typene på veien, så vil det feile senere i metoden hvis vi
        // fortsetter, og det er ikke brukbart. Dette er laget for å håndtere klasser som implementerer Collections
        // uten å angi typeparametrene til Collection (eller List, Set, Map).
        if (isFullyTyped(subClass) && typeToken.getRawType().isAssignableFrom(subClass)) {
            return (TypeToken<? extends T>) TypeToken.of(subClass);
        }

        TypeToken<? super T> tempToken = typeToken;

        while (tempToken.getType() instanceof TypeVariable || tempToken.getType() instanceof WildcardType) {
            if (tempToken.getType() instanceof TypeVariable) {
                // Gjør om T extends Foo til Foo (og T til Object)
                TypeVariable typeVariable = (TypeVariable) tempToken.getType();
                tempToken = (TypeToken<? super T>) TypeToken.of(typeVariable.getBounds()[0]);
            } else {
                // Gjør om ? extends Foo til Foo (og ? til Object). ? super Foo blir vel Object?
                WildcardType wildcardType = (WildcardType) tempToken.getType();
                tempToken = (TypeToken<? super T>) TypeToken.of(wildcardType.getUpperBounds()[0]);
            }
        }

        return (TypeToken<? extends T>) tempToken.getSubtype(subClass);
    }

    /**
     * @return {@code false} dersom klassen har typeparametre eller er ikke-statisk indre klasse i en klasse som har det
     */
    public static boolean isFullyTyped(Class<?> clazz) {
        if (clazz.getTypeParameters().length > 0) {
            return false;
        } else if (clazz.getEnclosingClass() == null) {
            return true;
        } else if (Modifier.isStatic(clazz.getModifiers())) {
            return true;
        } else {
            return isFullyTyped(clazz.getEnclosingClass());
        }
    }

}
