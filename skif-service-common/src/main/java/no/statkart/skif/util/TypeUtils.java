package no.statkart.skif.util;

import com.google.common.primitives.Primitives;
import com.google.common.reflect.TypeToken;

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

    public static TypeToken<?> wrapPrimitives(TypeToken<?> possiblyPrimitive) {
        if (possiblyPrimitive.getRawType().isPrimitive()) {
            return TypeToken.of(Primitives.wrap(possiblyPrimitive.getRawType()));
        }
        return possiblyPrimitive;
    }
}
