package no.statkart.skif.store;

import no.statkart.skif.exception.ImplementationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Hjelpeklasse for generisk funksjonalitet for BubbleId som er uavhengig av BubbleId implementasjonsklasse
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BubbleIds {
    private static ConcurrentHashMap<Class, Constructor> constructorMap = new ConcurrentHashMap<Class, Constructor>();

    /**
     * Denne metode har funksjonalitet for å opprette BubbleIds av forskjellig type uten å måtte bruke {@code new}
     * operatoren. Dette er nødvendig for enum koder, som garanterer at det kun opprettes en instans per id slik at
     * {@code ==} fungerer.  Skif rammeverket trenger denne klassen for på generisk vis å kunne opprette bobleId'er
     * for alle typer.
     * <p/>
     * Applikasjonskode fra rådes å bruke denne klassen. I versjon 2.1 av Skif vil enum koder ikke lengere støtte {@code ==}
     * og denne funksjonaliteten vil bli tatt bort.
     *
     * @deprecated
     */
    public static <I extends BubbleId<?>> I createInstance(Class<I> idClass, Object idValue, SnapshotVersion snapshotVersion) {
        I id = null;
        try {

            Constructor<I> ctor = constructorMap.get(idClass);
            if (ctor == null) {
                ctor = idClass.getDeclaredConstructor(idValue.getClass(), SnapshotVersion.class);
                ctor.setAccessible(true);
                constructorMap.putIfAbsent(idClass, ctor);
            }
            id = ctor.newInstance(idValue, snapshotVersion);
            return (I) id.resolveInstance();
        } catch (InstantiationException e) {
            throw new ImplementationException(e);
        } catch (IllegalAccessException e) {
            throw new ImplementationException(e);
        } catch (NoSuchMethodException e) {
            throw new ImplementationException(e);
        } catch (InvocationTargetException e) {
            throw new ImplementationException(e);
        }
    }

    /**
     * Returnerer hvilke klasse som BubbleId-klassen bruker som idvalue. Typisk Long eller String.
     */
    public static Class getValueType(Class<? extends BubbleId> clazz) {
        // TODO: bruke reflection på clazz istedet for å gå mot direkte AbstractBubbleId
        return AbstractBubbleId.getValueType(clazz);
    }
}
