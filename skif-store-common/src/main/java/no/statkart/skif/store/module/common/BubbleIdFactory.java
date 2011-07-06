package no.statkart.skif.store.module.common;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.ReplicaVersion;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BubbleIdFactory {
    public static <I extends BubbleId<?>> I createInstance(Class<I> idClass, long idValue, ReplicaVersion replicaVersion) {
        return createInstance(idClass, new Long(idValue), replicaVersion);
    }

    public static <I extends BubbleId<?>> I createInstance(Class<I> idClass, Object idValue, ReplicaVersion replicaVersion) {
         I id = null;
        try {
            Constructor<I> ctor = idClass.getDeclaredConstructor(idValue.getClass(), ReplicaVersion.class);
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
