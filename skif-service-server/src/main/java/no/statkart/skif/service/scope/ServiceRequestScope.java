package no.statkart.skif.service.scope;

import com.google.inject.Key;
import com.google.inject.OutOfScopeException;
import com.google.inject.Provider;
import com.google.inject.Scope;
import no.statkart.skif.util.ThreadLocalWithSuspend;

import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

//import com.google.inject.internal.Maps;

/**
 * Scopes a single execution of a block of code. Apply this scope with a
 * try/finally block: <pre>   {@code
 * <p>
 *   scope.enter();
 *   try {
 *     // explicitly seed some seed objects...
 *     scope.seed(Key.getStrategy(SomeObject.class), someObject);
 *     // createStrategy and access scoped objects
 *   } finally {
 *     scope.exit();
 *   }
 * }</pre>
 * <p>
 * The scope can be initialized with one or more seed values by calling
 * <code>seed(key, value)</code> before the injector will be called upon to
 * provide for this key. A typical use is for a servlet filter to enter/exit the
 * scope, representing a Request Scope, and seed HttpServletRequest and
 * HttpServletResponse.  For each key inserted with seed(), it's good practice
 * (since you have to provide <i>some</i> binding anyhow) to include a
 * corresponding binding that will throw an exception if Guice is asked to
 * provide for that key if it was not yet seeded: <pre>   {@code
 * <p>
 *   bind(key)
 *       .toProvider(SimpleScope.<KeyClass>seededKeyProvider())
 *       .in(ScopeAnnotation.class);
 * }</pre>
 *
 * @author Henrik Fredholm
 * @author Fedor Karpelevitch
 */
public class ServiceRequestScope implements Scope {
    public ServiceRequestScope() {
    }

    private static final Provider<Object> SEEDED_KEY_PROVIDER =
            new Provider<Object>() {
                public Object get() {
                    throw new IllegalStateException("If you got here then it means that" +
                            " your code asked for scoped object which should have been" +
                            " explicitly seeded in this scope by calling" +
                            " ServiceRequestScope.seed(), but was not.");
                }
            };
    private final ThreadLocalWithSuspend<Map<Key<?>, Object>> values
            = new ThreadLocalWithSuspend<Map<Key<?>, Object>>();

    public void enter() {
        checkState(values.get() == null, "A scoping block is already in progress");
        values.set(new HashMap<Key<?>, Object>());
    }


    public void exit() {
        checkState(values.get() != null, "No scoping block in progress");
        for (Object o : values.get().values()) {
            if (o instanceof Closeable) {
                ((Closeable) o).close();
            }
        }
        values.remove();
    }

    public void suspend() {
//        checkState(values.getStrategy() != null, "No scoping block in progress, cannot suspend");
        values.suspend();
    }

    public void resumeSuspended() {
//        checkState(values.getStrategy() == null, "Scoping block in progress, cannot resume to previously suspended block");
        values.resumeSuspended();
    }


    public <T> void seed(Key<T> key, T value) {
        Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);
        checkState(!scopedObjects.containsKey(key), "A value for the key %s was " +
                "already seeded in this scope. Old value: %s New value: %s", key,
                scopedObjects.get(key), value);
        scopedObjects.put(key, value);
    }

    public <T> void seed(Class<T> clazz, T value) {
        seed(Key.get(clazz), value);
    }

    public <T> Provider<T> scope(final Key<T> key, final Provider<T> unscoped) {
        return new Provider<T>() {
            public T get() {
                Map<Key<?>, Object> scopedObjects = getScopedObjectMap(key);

                @SuppressWarnings("unchecked")
                T current = (T) scopedObjects.get(key);
                if (current == null && !scopedObjects.containsKey(key)) {
                    current = unscoped.get();
                    scopedObjects.put(key, current);
                }
                return current;
            }
        };
    }

    private <T> Map<Key<?>, Object> getScopedObjectMap(Key<T> key) {
        Map<Key<?>, Object> scopedObjects = values.get();
        if (scopedObjects == null) {
            throw new OutOfScopeException("Cannot access " + key
                    + " outside of a scoping block");
        }
        return scopedObjects;
    }

    /**
     * Returns a provider that always throws exception complaining that the object
     * in question must be seeded before it can be injected.
     *
     * @return typed provider
     */
    @SuppressWarnings({"unchecked"})
    public static <T> Provider<T> seededKeyProvider() {
        return (Provider<T>) SEEDED_KEY_PROVIDER;
    }

}