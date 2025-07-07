package no.statkart.skif.storetest.service.storetest1;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.storetest.domain.basic.TestMap;
import org.hibernate.Session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@SuppressWarnings("unused")
public class StoreTest1ServiceImpl implements StoreTest1Service {
    // Alternativ via provider
    @Inject
    Provider<Session> sessionProvider;
//    Session session;

    private Session getSession() {
        return sessionProvider.get();
//        return session;
    }

    Provider<Connection>  connectionProvider;

    @Override
    public String put(String key, String value) {
        String result = null;
        Session s = getSession();
        TestMap entry = (TestMap) s.get(TestMap.class, key);
        if (entry == null) {
            s.save(new TestMap(key, value));
        } else {
            result = entry.getV();
            entry.setV(value);
        }
        return result;
    }

    @Override
    public String get(String key) {
        Session s = getSession();
        TestMap entry = (TestMap) s.get(TestMap.class, key);
        if (entry == null) {
            return null;
        } else {
            return entry.getV();
        }
    }

    @Override
    public String remove(String key) {
        Session s = getSession();
        TestMap entry = (TestMap) s.get(TestMap.class, key);
        s.delete(entry);
        return entry.getV();
    }

    @Override
    public void clear() {
        Session session = getSession();
        session.createNativeQuery("delete from TestMap").executeUpdate();
    }

    @Override
    public String putThatFails(String key, String value) {
        //noinspection unused
        String put = put(key, value);
        getSession().flush();
        throw new ImplementationException("putThatFails - denne metode skal feil. Endringer committes ikke");
    }

    @Override
    public String putViaJDBCConnection(String key, String value) {
        Connection connection = connectionProvider.get();
        boolean updated;
        try (PreparedStatement statement = connection.prepareStatement("update TestMap set v=? where k=?")) {
            statement.setString(1, value);
            statement.setString(2, key);
            updated = statement.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new ImplementationException(e);
        }

        if (!updated) {
            try (PreparedStatement statement = connection.prepareStatement("insert into TestMap values(?,?)")) {
                statement.setString(1, key);
                statement.setString(2, value);
                statement.executeUpdate();
            } catch (SQLException e) {
                throw new ImplementationException(e);
            }
        }

        return null;
    }
}
