package no.statkart.skif.store.persistence;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.BubbleObject;
import no.statkart.skif.store.SnapshotVersion;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * @author Henrik Fredholm
 */
public class PersistenceSessionProxy implements InvocationHandler, PersistenceSessionForSnapshot {
    private final PersistenceSessionForSnapshot delegate;
    private final SnapshotVersion snapshotVersion;
    private final PersistenceSessionProxyCache proxyCache;
    private PersistenceSessionForSnapshot proxy;
    private final static Set<Method> methodsImplementedByProxy;

    static {
        methodsImplementedByProxy = new HashSet<Method>();
        methodsImplementedByProxy.addAll(Arrays.asList(Object.class.getMethods()));
        methodsImplementedByProxy.addAll(Arrays.asList(PersistenceSessionForSnapshot.class.getMethods()));
    }

    public PersistenceSessionProxy(PersistenceSessionForSnapshot persistenceSessionForSnapshot, SnapshotVersion snapshotVersion, PersistenceSessionProxyCache proxyCache) {
        this.delegate = persistenceSessionForSnapshot;
        this.snapshotVersion = snapshotVersion;
        this.proxyCache = proxyCache;
        Class[] interfaces = getDerivedInterfaces(delegate.getClass());
        this.proxy = (PersistenceSessionForSnapshot) Proxy.newProxyInstance(delegate.getClass().getClassLoader(), interfaces, this);

    }

    private final Class[] getDerivedInterfaces(Class<?> clazz) {
        List<Class> interfaces = new ArrayList<Class>(5);
        do {
            interfaces.addAll(Arrays.asList(clazz.getInterfaces()));
            clazz = clazz.getSuperclass();
        } while (clazz!=null);
        return interfaces.toArray(new Class[0]);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (methodsImplementedByProxy.contains(method)) {
            try {
                return method.invoke(this, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        } else {
            SnapshotVersion previousVersion = delegate.setSnapshot(snapshotVersion);

            try {
                Object result = method.invoke(delegate, args);
                if (result instanceof PersistenceSessionForSnapshot) {
                    result = proxyCache.getOrCreateProxy(PersistenceSessionForSnapshot.class.cast(result), snapshotVersion);
                }
                return result;
            } finally {
                verifySnapshotVersion(delegate, snapshotVersion);
            }
        }
    }

    private void verifySnapshotVersion(PersistenceSessionForSnapshot sessionForSnapshot, SnapshotVersion snapshotVersion2) {
        if (sessionForSnapshot.getSnapshot() != snapshotVersion2) {
            throw new ImplementationException("Uventet endring av SnapshotVersion under kall til PersistenceSessionForSnapshot. Forventet: " + snapshotVersion2 + ". Faktisk:" + sessionForSnapshot);
        }
    }

    public PersistenceSessionForSnapshot getProxy() {
        return proxy;
    }

    @Override
    public SnapshotVersion getSnapshot() {
        return snapshotVersion;
    }

    @Override
    public SnapshotVersion setSnapshot(SnapshotVersion snapshotVersion) {
        throw new ImplementationException("Kan ikke endre snapshot");
    }

    @Override
    public boolean isSnapshotChangable() {
        return false;
    }

    @Override
    public boolean acceptsSnapshot(SnapshotVersion snapshotVersion) {
        return this.snapshotVersion.equals(snapshotVersion);
    }

    @Override
    public PersistenceSessionForSnapshot getForBubbleId(Class<? extends BubbleId> type) {
        PersistenceSessionForSnapshot sessionForBubbleId = delegate.getForBubbleId(type);
        return proxyCache.getOrCreateProxy(sessionForBubbleId, snapshotVersion);
    }

    @Override
    public <T extends PersistenceSessionForSnapshot> T getImplementation(Class<T> interfaceType) {
        T sessionForBubbleId = delegate.getImplementation(interfaceType);
        return interfaceType.cast(proxyCache.getOrCreateProxy(sessionForBubbleId, snapshotVersion));
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T get(I bubbleId) {
        SnapshotVersion previousVersion = delegate.setSnapshot(snapshotVersion);
        try {
            return delegate.get(bubbleId);
        } finally {
            verifySnapshotVersion(delegate, snapshotVersion);
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> get(Collection<I> bubbleIds) {
        SnapshotVersion previousVersion = delegate.setSnapshot(snapshotVersion);
        try {
            return delegate.get(bubbleIds);
        } finally {
            verifySnapshotVersion(delegate, snapshotVersion);
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void insert(T bubble) {
        SnapshotVersion previousVersion = delegate.setSnapshot(snapshotVersion);
        try {
            delegate.insert(bubble);
        } finally {
            verifySnapshotVersion(delegate, snapshotVersion);
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void update(T bubble) {
        SnapshotVersion previousVersion = delegate.setSnapshot(snapshotVersion);
        try {
            delegate.update(bubble);
        } finally {
            verifySnapshotVersion(delegate, snapshotVersion);
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void delete(T bubble) {
        SnapshotVersion previousVersion = delegate.setSnapshot(snapshotVersion);
        try {
            delegate.delete(bubble);
        } finally {
            verifySnapshotVersion(delegate, snapshotVersion);
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> void evict(I bubbleId) {
        SnapshotVersion previousVersion = delegate.setSnapshot(snapshotVersion);
        try {
            delegate.evict(bubbleId);
        } finally {
            verifySnapshotVersion(delegate, snapshotVersion);
        }
    }

    @Override
    public <T extends BubbleObject> void ensureFullyLoaded(T bubble) {
        SnapshotVersion previousVersion = delegate.setSnapshot(snapshotVersion);
        try {
            delegate.ensureFullyLoaded(bubble);
        } finally {
            verifySnapshotVersion(delegate, snapshotVersion);
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> T refresh(I bubbleId) {
        SnapshotVersion previousVersion = delegate.setSnapshot(snapshotVersion);
        try {
            return delegate.refresh(bubbleId);
        } finally {
            verifySnapshotVersion(delegate, snapshotVersion);
        }
    }

    @Override
    public <T extends BubbleObject, I extends BubbleId<? extends T>> Collection<? extends T> refresh(Collection<I> bubbleIds) {
        SnapshotVersion previousVersion = delegate.setSnapshot(snapshotVersion);
        try {
            return delegate.refresh(bubbleIds);
        } finally {
            verifySnapshotVersion(delegate, snapshotVersion);
        }
    }
    @Override
    public <T extends BubbleObject> void refresh(T bubble) {
        SnapshotVersion previousVersion = delegate.setSnapshot(snapshotVersion);
        try {
            delegate.refresh(bubble);
        } finally {
            verifySnapshotVersion(delegate, snapshotVersion);
        }
    }
}
