package no.statkart.skif.persistence;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.OperationalException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *
 * @author Henrik Fredholm
 * @since 2.0
 */
public class JDBCConnectionFactory  implements ConnectionFactory{
    private String url;
    private String username;
    private String password;

    public JDBCConnectionFactory(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public Connection createConnection() throws SQLException {
        try {
            Class.forName ("oracle.jdbc.OracleDriver");
        } catch (ClassNotFoundException e) {
            throw new ImplementationException(e);
        }

        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
}
