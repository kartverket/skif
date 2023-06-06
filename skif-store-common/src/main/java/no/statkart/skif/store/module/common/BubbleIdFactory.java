package no.statkart.skif.store.module.common;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class BubbleIdFactory {
    public static <I extends BubbleId<?>> I createInstance(Class<I> idClass, long idValue, SnapshotVersion snapshotVersion) {
        return createInstance(idClass, Long.valueOf(idValue), snapshotVersion);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    public static <I extends BubbleId<?>> I createInstance(Class<I> idClass, Object idValue, SnapshotVersion snapshotVersion) {
        try {
            Constructor<I> ctor = idClass.getDeclaredConstructor(idValue.getClass(), SnapshotVersion.class);
            ctor.setAccessible(true);
            @SuppressWarnings("UnnecessaryLocalVariable")
            I id = ctor.newInstance(idValue, snapshotVersion);
            return id;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new ImplementationException(e);
        }
    }

}
