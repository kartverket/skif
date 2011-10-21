package no.statkart.skif.store;

import no.statkart.skif.exception.ImplementationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BubbleIds {

    public static <I extends BubbleId<?>> I createInstance(Class<I> idClass, long idValue) {
        return createInstance(idClass, new Long(idValue), SnapshotVersion.CURRENT);
    }

    public static <I extends BubbleId<?>> I createInstance(Class<I> idClass, long idValue, SnapshotVersion snapshotVersion) {
        return createInstance(idClass, new Long(idValue), snapshotVersion);
    }

    public static <I extends BubbleId<?>> I createInstance(Class<I> idClass, Object idValue, SnapshotVersion snapshotVersion) {
        I id = null;
        try {
            Constructor<I> ctor = idClass.getDeclaredConstructor(idValue.getClass(), SnapshotVersion.class);
            ctor.setAccessible(true);
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

    public static Class getValueType(Class<? extends BubbleId> clazz) {
        // TODO: bruke reflection på clazz istedet for å gå mot direkte AbstractBubbleId
        return AbstractBubbleId.getValueType(clazz);
    }
}
