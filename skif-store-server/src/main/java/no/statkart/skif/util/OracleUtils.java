package no.statkart.skif.util;

import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.persistence.jdbc.ConnectionForSnapshotVersion;
import oracle.jdbc.OracleConnection;
import org.hibernate.jdbc.ConnectionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Roar Ingebrigtsen
 * @author Tor Egil R. Strand
 * @author Erik Romson
 * @since 2.0
 */
public class OracleUtils {
    private static final Logger logger = LoggerFactory.getLogger(OracleUtils.class);

    /**
     * Initialized to unwrap c3p0 connection. Is <code>null</code> if the class is not in the classpath.
     */
    private static Class<?> c3p0NewProxyConnectionClass;
    /**
     * The wanted field in newProxyConnectionClass.
     */
    private static Field c3p0InnerField;

    static {
        try {
            // Siden c3p0 ikke kan forventes å være på classpath, må den letes opp dynamisk.
            c3p0NewProxyConnectionClass = Class.forName("com.mchange.v2.c3p0.impl.NewProxyConnection");
            c3p0InnerField = c3p0NewProxyConnectionClass.getDeclaredField("inner");
        } catch (Throwable t) {
            logger.debug("Unable to enable c3p0 unwrapping", t);
        }
    }

    /**
     * Henter ut OracleConnection fra en JDBC Connection. Sjekker på om JDBC connection er fra
     * Weblogic connection pool (weblogic.jdbc.wrapper.JTSConnection) og henter i så fall ut
     * underliggende connection, ellers castes JDBC connection til OracleConnection.
     *
     * @param con en JDBC connection som er eller inneholder en OracleConnection
     * @return OracleConnection for en JDBC connection
     */
    public static OracleConnection getOracleConnection(Connection con) {
        OracleConnection oracleConnection;

        if (con instanceof ConnectionForSnapshotVersion) {
            ConnectionForSnapshotVersion connectionForSnapshotVersion = ConnectionForSnapshotVersion.class.cast(con);
            con = connectionForSnapshotVersion.reserve();
            // TODO: Dette er ikke så pent å låse opp før vi bruker connection. I teorien kan vi komme til endre den før vi bruker den. Men det går sikkert bra. Burde vurdere annet design som koden som bruker dette.
            connectionForSnapshotVersion.release();
        }

        if (con instanceof ConnectionWrapper) {
            con = ((ConnectionWrapper) con).getWrappedConnection();
        }

        con = unwrapc3p0Connection(con);

        try {
            oracleConnection = con.unwrap(OracleConnection.class);
        } catch (SQLException e) {
            throw new OperationalException("An error occured getting Oracle connection from Weblogic JTSConnection", e);
        }

        return oracleConnection;
    }

    /**
     * Unwrapper c3p0 connections, noe som ikke er like lett som for øvrige connection-wrappere vi forholder oss til.
     *
     * @param connection connection som muligens er en c3p0-connection
     * @return den connection c3p0 wrapper, hva nå enn det måtte være
     * @since 2.3.0
     */
    public static Connection unwrapc3p0Connection(Connection connection) {
        if (c3p0NewProxyConnectionClass == null || !c3p0NewProxyConnectionClass.isInstance(connection)) {
            //nothing to unwrap
            return connection;
        }

        if (c3p0InnerField == null) {
            //here we have the class but 'inner' field doesn't exist, probably some change after a new version
            throw new ImplementationException("Problem unwrapping " + connection.getClass().getName() + " to the native oracle one. The class " +
                    c3p0NewProxyConnectionClass.getName() + " doesn't seem to have a field called inner. Check your C3P0 version");
        }


        Connection innerconn;
        try {
            c3p0InnerField.setAccessible(true);
            innerconn = (Connection) c3p0InnerField.get(connection);
        } catch (IllegalAccessException e) {
            throw new ImplementationException("Problem unwrapping " + connection.getClass().getName() + " to the native oracle one. The class " +
                    c3p0NewProxyConnectionClass.getName() + " doesn't seem to be able to access the field inner", e);
        }
        return innerconn;
    }


}
