package no.statkart.skif.persistence.jdbc;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import no.statkart.skif.persistence.SingleVmTransaction;
import no.statkart.skif.persistence.SingleVmTransactionManager;

import javax.sql.DataSource;
import javax.transaction.SystemException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Enkelt datasource som returnerer connection knyttet til transaksjon hvis en transaksjon er aktiv.
 */
@Singleton
public class SingleVmTransactionAwareDataSource implements DataSource {
    private final SingleVmTransactionManager transactionManager;

    @Inject
    public SingleVmTransactionAwareDataSource(SingleVmTransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    private DataSource getDelegate() {
        return transactionManager.getDataSource();
    }

    @Override
    public Connection getConnection() throws SQLException {
        SingleVmTransaction transaction;
        try {
            transaction = transactionManager.getTransaction();
        } catch (SystemException e) {
            throw new SQLException("Could not get transaction", e);
        }

        if (transaction == null) {
            return getDelegate().getConnection();
        } else {
            ProxyHandler proxyHandler = new ProxyHandler(transaction.getConnection());
            return (Connection) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{Connection.class}, proxyHandler);
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public PrintWriter getLogWriter() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public int getLoginTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return iface.cast(this);
        } else {
            return getDelegate().unwrap(iface);
        }
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this) || getDelegate().isWrapperFor(iface);
    }

    private static class ProxyHandler implements InvocationHandler {
        private static final Set<String> DENIED_METHODS = ImmutableSet.of(
                "commit",
                "rollback",
                "abort",
                "setAutoCommit",
                "releaseSavepoint",
                "setSavepoint"
        );

        private Connection delegate;

        private ProxyHandler(Connection delegate) {
            this.delegate = delegate;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                if (method.getDeclaringClass().equals(Object.class)) {
                    return method.invoke(this, args);
                }
                if (method.getName().equals("close")) {
                    delegate = null;
                    return null;
                }
                if (method.getName().equals("isClosed")) {
                    return delegate == null || delegate.isClosed();
                }
                if (DENIED_METHODS.contains(method.getName())) {
                    throw new SQLException("Method is denied in JTA transaction");
                }
                if (delegate == null) {
                    throw new SQLException("Connection is closed");
                }
                return method.invoke(delegate, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }
}
