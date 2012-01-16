package no.statkart.skif.persistence5.jdbc;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Henrik Fredholm
 * @since 2.1
 */
public class ConnectionFactoryUsingJDBC extends AbstractConnectionFactory {
    private final String url;
    private final String username;
    private final String password;


    public ConnectionFactoryUsingJDBC(String url, String username, String password, boolean snapshotChangable, SnapshotVersion snapshotVersion, boolean setSnapshotOnSession) {
        super(snapshotChangable, snapshotVersion, setSnapshotOnSession);
        this.url = url;
        this.username = username;
        this.password = password;
    }


    public Connection createConnection() throws SQLException {
        try {
            Class.forName("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new ImplementationException(e);
        }
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}
