package no.statkart.skif.store;

import no.statkart.skif.SkifUtil;
import no.statkart.skif.exception.ImplementationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsLast;

/**
 * Hjelpeklasse for generisk funksjonalitet for BubbleId som er uavhengig av BubbleId implementasjonsklasse.
 *
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BubbleIds {
    private static ConcurrentHashMap<Class, Constructor> constructorMap = new ConcurrentHashMap<Class, Constructor>();

    /**
     * Oppretter en bubbleId instans av gitt type
     */
    public static <I extends BubbleId<?>> I createInstance(Class<I> idClass, Object idValue, SnapshotVersion snapshotVersion) {
        try {
            Constructor<I> ctor = constructorMap.get(idClass);
            if (ctor == null) {
                ctor = idClass.getDeclaredConstructor(idValue.getClass(), SnapshotVersion.class);
                ctor.setAccessible(true);
                constructorMap.putIfAbsent(idClass, ctor);
            }
            //noinspection UnnecessaryLocalVariable
            I id = ctor.newInstance(idValue, snapshotVersion);
            return id;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
            throw new ImplementationException(e);
        } catch (InvocationTargetException e) {
            throw new ImplementationException(e.getTargetException());
        }
    }

    /**
     * Returnerer hvilke klasse som BubbleId-klassen bruker som idvalue. Typisk Long eller String.
     */
    public static Class getValueType(Class<? extends BubbleId> clazz) {
        // TODO: bruke reflection på clazz istedet for å gå mot direkte AbstractBubbleId
        return AbstractBubbleId.getValueType(clazz);
    }

    //public static <T extends BubbleObject, I extends BubbleId<? extends T>> Class<? extends T> getBaseType(Class<I> clazz) {
    public static Class<? extends BubbleObject> getBaseType(Class<? extends BubbleId<?>> clazz) {
        // TODO: bruke reflection på clazz istedet for å gå mot direkte AbstractBubbleId
        return AbstractBubbleId.getTypeInfo(clazz).baseType;
    }

    public static <T extends BubbleObject> Class<? extends BubbleId<T>> getBubbleIdClass(Class<T> bubbleClass) {
        return SkifUtil.classForName(bubbleClass.getName() + "Id");
    }

    public static boolean equalsIgnoreSnapshotVersion(BubbleId<?> id1, BubbleId<?> id2) {
        return (id1 == id2) || (id1 != null && id1.equalsIgnoreSnapshotVersion(id2));
    }

    /**
     * Optimalisert sammenligning av id-verdier i tilfeller hvor man vet at man opererer på samme type (homogene typer) og snapshot version.
     * <p>
     * Sammenlikningen forutsetter like {@link no.statkart.skif.store.SnapshotVersion snapshotVersion} og {@link BubbleId id-type} for alle parameterisert ids.
     * </p>
     *
     * @return {@code true} kun dersom id med value er like og not null
     */
    public static boolean valueEquals(BubbleId<?> id1, BubbleId<?> id2) {
        if (id1 == null || id2 == null) {
            return false;
        }
        if (id1.getValue() == null) {
            return false;
        } else {
            return id1.getValue().equals(id2.getValue());
        }
    }


    private static final Comparator<BubbleId<?>> bubbleIdValueComparator = BubbleIds::comparingBubbleIdValue;
    public static Comparator<BubbleId<?>> comparingBubbleIdValue() {
        return bubbleIdValueComparator;
    }

    /**
     * Sammenlikner to bubbleId, mhp id-verdi.
     *
     * Null-verdier håndteres på tilsvarende måte som: <br>
     * {@code Objects.compare(bubbleId1, bubbleId2, nullsLast(naturalOrder()))}
     *
     * @param bubbleId1 kan være null
     * @param bubbleId2 kan være null
     * @return -1 om bubbleId1 er før, 0 på samme plass eller 1 hvis den er etter bubbleId2 ihht kriterier.
     */
    public static int comparingBubbleIdValue(final BubbleId<?> bubbleId1, final BubbleId<?> bubbleId2) {
        final Object value1 = bubbleId1 == null ? null : bubbleId1.getValue();
        final Object value2 = bubbleId2 == null ? null : bubbleId2.getValue();

        // Optimization of common cases...
        if (value1 instanceof Long && value2 instanceof Long) {
            return Long.compare((long) value1, (long) value2);
        }

        if (value1 == null ^ value2 == null) { //xor
            return value1 != null ? -1 : 1; //nulls last
        }

        return value1 == value2 ? 0 : (String.valueOf(value1)).compareTo(String.valueOf(value2));
    }

}
