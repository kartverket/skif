package no.statkart.skif.store.persistence.jdbc;

import no.statkart.skif.persistence.jdbc.ConnectionForSnapshotVersion;
import no.statkart.skif.persistence.jdbc.ConnectionReservationForSnapshot;
import no.statkart.skif.store.persistence.hibernate.HibernatePersistenceSessionMaster;

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
public class ConnectionProxyUsingHibernate implements InvocationHandler, ConnectionReservationForSnapshot {
    private final HibernatePersistenceSessionMaster persistenceSessionMaster;
    private final static Set<Method> methodsImplementedByProxy;
    private ConnectionForSnapshotVersion proxy;

    static {
        methodsImplementedByProxy = new HashSet<Method>();
        methodsImplementedByProxy.addAll(Arrays.asList(Object.class.getMethods()));
        methodsImplementedByProxy.addAll(Arrays.asList(ConnectionReservationForSnapshot.class.getMethods()));
    }

    public ConnectionProxyUsingHibernate(HibernatePersistenceSessionMaster persistenceSessionMaster) {
        this.persistenceSessionMaster = persistenceSessionMaster;

        this.proxy = (ConnectionForSnapshotVersion) Proxy.newProxyInstance(persistenceSessionMaster.getClass().getClassLoader(), new Class[] {ConnectionForSnapshotVersion.class}, this);

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
            Connection connection = persistenceSessionMaster.reserveSession().connection();

            try {
                //noinspection UnnecessaryLocalVariable
                Object result = method.invoke(connection, args);
                return result;
            } finally {
                persistenceSessionMaster.releaseSession();
            }
        }
    }

    @Override
    public Connection reserve() {
        //noinspection UnnecessaryLocalVariable
        Connection connection = persistenceSessionMaster.reserveSession().connection();
        return connection;
    }

    @Override
    public void  release() {
        persistenceSessionMaster.releaseSession();
    }
}
