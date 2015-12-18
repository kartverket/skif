package no.statkart.skif.persistence.jdbc;

import no.statkart.skif.store.SnapshotVersion;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class ConnectionProxyUsingJDBC implements InvocationHandler, ConnectionReservationForSnapshot {
    private final SnapshotVersion snapshotVersion;
    private final ConnectionFactory factory;
    private final Connection delegate;
    private final static Set<Method> methodsImplementedByProxy;
    private ConnectionForSnapshotVersion proxy;

    static {
        methodsImplementedByProxy = new HashSet<Method>();
        methodsImplementedByProxy.addAll(Arrays.asList(Object.class.getMethods()));
        methodsImplementedByProxy.addAll(Arrays.asList(ConnectionReservationForSnapshot.class.getMethods()));
    }

    public ConnectionProxyUsingJDBC(Connection delegate, SnapshotVersion snapshotVersion, ConnectionFactory factory) {
        this.snapshotVersion = snapshotVersion;
        this.factory = factory;
        this.delegate = delegate;
        this.proxy = (ConnectionForSnapshotVersion) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{ConnectionForSnapshotVersion.class}, this);
    }

    public ConnectionForSnapshotVersion getProxy() {
        return proxy;
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
            factory.setSnapshotVersion(delegate, snapshotVersion);
            Object result = null;
            try {
                result = method.invoke(delegate, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
            return result;
        }
    }

    @Override
    public Connection reserve() {
        return delegate;
    }

    @Override
    public void release() {
    }

}
