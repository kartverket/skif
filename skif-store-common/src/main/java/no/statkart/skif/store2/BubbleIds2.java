package no.statkart.skif.store2;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BubbleIds2 {
    public static <I extends BubbleId2<?>> I createInstance(Class<I> idClass, long idValue) {
        return createInstance(idClass, new Long(idValue), SnapshotVersion.CURRENT);
    }

    public static <I extends BubbleId2<?>> I createInstance(Class<I> idClass, long idValue, SnapshotVersion replicaVersion) {
        return createInstance(idClass, new Long(idValue), replicaVersion);
    }

    public static <I extends BubbleId2<?>> I createInstance(Class<I> idClass, Object idValue, SnapshotVersion replicaVersion) {
        I id = null;
        try {
            Constructor<I> ctor = idClass.getDeclaredConstructor(idValue.getClass(), SnapshotVersion.class);
            ctor.setAccessible(true);
            id = ctor.newInstance(idValue, replicaVersion);
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
}
