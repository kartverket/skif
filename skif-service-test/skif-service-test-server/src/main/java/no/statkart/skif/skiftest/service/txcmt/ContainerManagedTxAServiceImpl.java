package no.statkart.skif.skiftest.service.txcmt;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.ValidationException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public class ContainerManagedTxAServiceImpl implements ContainerManagedTxAService {
    final Provider<Connection> connectionProvider;

    @Inject
    public ContainerManagedTxAServiceImpl(Provider<Connection> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public void clear() {
        Connection c = connectionProvider.get();
        try (Statement statement = c.createStatement()) {
            statement.execute("delete from TestMap");
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
    }


    @Override
    public String get(String key) {
        String result;
        Connection c = connectionProvider.get();
        try (PreparedStatement ps = c.prepareStatement("select v from TestMap where k=?")) {
            ps.setString(1, key);
            ResultSet resultSet = ps.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getString(1);
                if (resultSet.next()) {
                    throw new ImplementationException("ResultSet returnerte mer enn en rad");
                }
            } else {
                return null;
            }
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }
        return result;
    }

    @Override
    public String put(String key, String value) {
        if (key==null) {
            throw new ValidationException("key = null er ikke tilladt");
        }

        String oldValue = get(key);
        Connection connection = connectionProvider.get();
        if (oldValue == null) {
            try (PreparedStatement ps = connection.prepareStatement("insert into TestMap values(?,?)")) {
                ps.setString(1, key);
                ps.setString(2, value);
                int result = ps.executeUpdate();
                if (result != 1) {
                    throw new ImplementationException("Fikk feil update count: " + result);
                }
            } catch (SQLException e) {
                throw new ImplementationException(e);
            }
        } else {
            try (PreparedStatement ps = connection.prepareStatement("update TestMap set v=? where k=?")) {
                ps.setString(1, value);
                ps.setString(2, key);
                int result = ps.executeUpdate();
                if (result != 1) {
                    throw new ImplementationException("Fikk feil update count: " + result);
                }
            } catch (SQLException e) {
                throw new ImplementationException(e);
            }
        }
        return oldValue;
    }

    @Override
    public void multiPut(String key1, String value1, String key2, String value2) {
        put(key1, value1);
        put(key2, value2);
    }
}
