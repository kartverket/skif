package no.statkart.skif.util;

import net.sf.ehcache.constructs.asynchronous.Command;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.persistence5.jdbc.ConnectionForSnapshotVersion;
import oracle.jdbc.OracleConnection;
import org.hibernate.jdbc.ConnectionWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import weblogic.jdbc.wrapper.JTSConnection;
import weblogic.jdbc.wrapper.PoolConnection;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class OracleUtils {
    private static Logger logger = LoggerFactory.getLogger(OracleUtils.class);

    /**
     * Henter ut OracleConnection fra en JDBC Connection. Sjekker på om JDBC connection er fra
     * Weblogic connection pool (weblogic.jdbc.wrapper.JTSConnection) og henter i så fall ut
     * underliggende connection, ellers castes JDBC connection til OracleConnection.
     *
     * @param con en JDBC connection som er eller inneholder en OracleConnection
     * @return OracleConnection for en JDBC connection
     */
    public static OracleConnection getOracleConnection(Connection con) {
        OracleConnection oracleConnection = null;

        if (con instanceof ConnectionForSnapshotVersion) {
            ConnectionForSnapshotVersion connectionForSnapshotVersion = ConnectionForSnapshotVersion.class.cast(con);
            con = connectionForSnapshotVersion.reserve();
            // TODO: Dette er ikke så pent å låse opp før vi bruker connection. I teorien kan vi komme til endre den før vi bruker den. Men det går sikkert bra. Burde vurdere annet design som koden som bruker dette.
            connectionForSnapshotVersion.release();

        }

        if (con instanceof ConnectionWrapper) {
            con = ((ConnectionWrapper) con).getWrappedConnection();
        }

        if (con instanceof weblogic.jdbc.wrapper.JTSConnection) {
            //Når vi kjører på tjeneren vil vår oracle connection være wrappet i en weblogic-connection fra connection pool
            try {
                oracleConnection = (OracleConnection) ((JTSConnection) con).getConnection();
            } catch (SQLException e) {
                throw new OperationalException("Feil oppstod ved uthenting av Oracle connection fra Weblogic JTSConnection", e);
            } catch (RuntimeException e) {
                throw new OperationalException("Feil oppstod ved uthenting av Oracle connection fra Weblogic JTSConnection. Check at Weblogic Connection Pool bruker korrekt driver (oracle.jdbc.OracleDriver)", e);
            }
        } else if (con instanceof PoolConnection) {
            try {
                oracleConnection = (OracleConnection) ((PoolConnection) con).getVendorConnection();
            } catch (SQLException e) {
                throw new OperationalException("Feil oppstod ved uthenting av Oracle connection fra Weblogic PoolConnection. Check at Weblogic Connection Pool bruker korrekt driver (oracle.jdbc.OracleDriver)", e);
            }
        } else if (con instanceof OracleConnection) {
            oracleConnection = (OracleConnection) con;
        }
/*
      // Kommenteres inn dersom du bruker Jdbmonitor verktøyet.
      else if ( con instanceof com.jdbmonitor.driver.jdbc.v3.Connection3) {
         com.jdbmonitor.driver.jdbc.v3.Connection3 connection3 = ((com.jdbmonitor.driver.jdbc.v3.Connection3) con);
         try {
            Field f = connection3.getClass().getDeclaredField("realConnection");
            f.setAccessible(true);
            oracleConnection = (OracleConnection)f.get(connection3);
         } catch( NoSuchFieldException e ) {
            throw new RuntimeException(e);
         } catch( IllegalAccessException e ) {
            throw new RuntimeException(e);
         }
      }
*/
        else {
            throw new ImplementationException("Kan ikke hente OracleConnection fra connection {" + con + "}");
        }

        return oracleConnection;
    }

    /**
     * Denne henter ut SRID-koden som brukes når vi lagrer geometri i databasen. Siden Spatial index metadata definerer koordinatsystemet som NULL så returneres -1.
     *
     * @return
     */
    public static int getOracleIntSRID() {
        return -1;
    }


}
