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

        try {
            oracleConnection = con.unwrap(OracleConnection.class);
        } catch (SQLException e) {
            throw new OperationalException("An error occured getting Oracle connection from Weblogic JTSConnection", e);
        }

        return oracleConnection;
    }

}
