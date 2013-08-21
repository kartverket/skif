package no.statkart.skif.persistence.jdbc;

import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.config.SkifServerConfiguration;
import no.statkart.skif.store.SnapshotVersion;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.*;

import static no.statkart.skif.util.JDBCHelper.createConnectionFactoryUsingJDBC;
import static org.testng.Assert.*;

/**
 * Tester for {@link ConnectionManagerUsingFactory}
 *
 * @author Henrik Fredholm
 * @author Jan Holmen
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class ConnectionManagerUsingJDBCTest {
    private ConnectionManagerUsingFactory connectionManager;

    String username;
    String password;
    String url;

    @BeforeClass
    public void setUp() {
        SkifServerConfiguration configuration = new SkifServerConfiguration();
        username = configuration.getString(SkifConfigConstants.DB_USERNAME);
        password = configuration.getString(SkifConfigConstants.DB_PASSWORD);
        String sid = configuration.getString(SkifConfigConstants.DB_SID);
        String hostname = configuration.getString(SkifConfigConstants.DB_HOSTNAME);
        String port = configuration.getString(SkifConfigConstants.DB_PORT);
        url = String.format("jdbc:oracle:thin:@%s:%s:%s", hostname, port, sid);


        connectionManager = new ConnectionManagerUsingFactory(
                createConnectionFactoryUsingJDBC(configuration, SnapshotVersion.CURRENT, false),
                createConnectionFactoryUsingJDBC(configuration, SnapshotVersion.OLD, false)
        );
    }

    @AfterClass
    void tearDown() {
        connectionManager.close();
    }

    public void testAllocateConnection() throws SQLException {

        ConnectionForSnapshotVersion connectionForSnapshotVersion = connectionManager.getForSnapshotVersion(SnapshotVersion.CURRENT);
        assertFalse(connectionForSnapshotVersion.getAutoCommit());

        assertSame(connectionForSnapshotVersion, connectionManager.getForSnapshotVersion(SnapshotVersion.CURRENT));

        try {
            Connection unwrappedConnection = connectionForSnapshotVersion.reserve();
            assertFalse(unwrappedConnection.getAutoCommit());
            assertEquals(unwrappedConnection.getClass().getName(), "oracle.jdbc.driver.T4CConnection");
        } finally {
            connectionForSnapshotVersion.release();
        }

        Connection connectionOld = connectionManager.getForSnapshotVersion(SnapshotVersion.OLD);
        assertFalse(connectionForSnapshotVersion.getAutoCommit());

        assertNotSame(connectionForSnapshotVersion, connectionOld);
        connectionManager.close();
    }

    @Test(invocationCount = 1 /*200*/)
    public void testAllocateConnection_many() throws SQLException, InterruptedException {
        testAllocateConnection();
    }

    @Test(invocationCount = 1 /*200*/)
    public void testAllocateConnection_many2() {
        // Se SKIF 215 hvis denne feiler
        try {
            Connection conn = DriverManager.getConnection(url, username, password);
            conn.close();
        } catch (SQLException e) {
            fail(e.getMessage(), e);
        }
    }


    /**
     * Sjekke en enkel read fra tabell foo_h
     *
     * @throws SQLException ved feil
     */
    @Test(invocationCount = 1)
    public void testSqlRead() throws SQLException {
        ConnectionForSnapshotVersion forSnapshotVersion = connectionManager.getForSnapshotVersion(SnapshotVersion.CURRENT);
        assertFalse(forSnapshotVersion.isClosed());
        PreparedStatement preparedStatement;
        try {
            Connection unwrappedConnection = forSnapshotVersion.reserve();
            preparedStatement = unwrappedConnection.prepareStatement("select * from foo_h where id = ?");
            preparedStatement.setInt(1, 101);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet != null) {
                while (resultSet.next()) {
                    Integer intger = resultSet.getInt(5);
                    String str = resultSet.getString(6);
                    assertNotNull(intger);
                    assertNotNull(str);
//                    System.out.println(intger + " - " + str);
                }
            }
            resultSet.close();

            preparedStatement = unwrappedConnection.prepareStatement("select * from bar_h where id = ?");
            preparedStatement.setInt(1, 1002);
            resultSet = preparedStatement.executeQuery();
            if (resultSet != null) {
                while (resultSet.next()) {
                    Integer intger = resultSet.getInt(5);
                    Integer intger2 = resultSet.getInt(7);
                    assertNotNull(intger);
                    assertNotNull(intger2);
//                    System.out.println(intger + " - " + intger2);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            forSnapshotVersion.release();
        }
    }

    //todo ta inn denne testen når snapshot_time er på plass
//    /**
//     * Sjekke en enkel read fra foo view
//     *
//     * @throws SQLException ved feil
//     */
//    @Test(invocationCount = 1)
//    public void testSqlReadView() throws SQLException {
//        ConnectionForSnapshotVersion forSnapshotVersion = connectionManager.getForSnapshotVersion(SnapshotVersion.CURRENT);
//        assertFalse(forSnapshotVersion.isClosed());
//        PreparedStatement preparedStatement;
//         try {
//            Connection unwrappedConnection = forSnapshotVersion.reserve();
//             preparedStatement = unwrappedConnection.prepareStatement("select * from foo where id = ?");
//             preparedStatement.setInt(1,101);
//             ResultSet resultSet = preparedStatement.executeQuery();
//             if(resultSet != null){
//                 int returned = 0;
//                 while(resultSet.next()){
//                     returned++;
//                     Integer intger = resultSet.getInt(5);
//                     String str = resultSet.getString(6);
//                     assertNotNull(intger);
//                     assertNotNull(str);
//                     System.out.println(intger + " - " + str);
//                 }
//                 assertTrue(returned > 0,"Det skulle ha vært returnert rader i fra foo, mulig set snapshot_time ikke er implementert.......");
//             }
//         } finally {
//            forSnapshotVersion.release();
//        }
//    }


    /**
     * Sjekke en enkel count
     *
     * @throws SQLException ved feil
     */
    @Test(invocationCount = 1)
    public void testSqlCount() throws SQLException {
        Integer itn = count("foo_h", 100);
//        System.out.println("antall foo's " + itn);
        assertNotNull(itn);
        assertTrue(itn > 0);
    }

    private Integer count(String table, Integer id) throws SQLException {
        Integer res = null;
        ConnectionForSnapshotVersion forSnapshotVersion = connectionManager.getForSnapshotVersion(SnapshotVersion.CURRENT);
        assertFalse(forSnapshotVersion.isClosed());
        PreparedStatement preparedStatement;
        try {
            Connection unwrappedConnection = forSnapshotVersion.reserve();
            String statement = "select count(*) from " + table;
            if (id != null) statement += " where id = ?";

            preparedStatement = unwrappedConnection.prepareStatement(statement);
            if (id != null) {
                preparedStatement.setInt(1, 101);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet != null) {
                if (resultSet.next()) {
                    res = resultSet.getInt(1);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            forSnapshotVersion.release();
        }
        return res;
    }


    /**
     * Oppdater foo_h tabell.
     * Sjekk at <code>old != current</code>
     * Rollback current
     * Sjekk at current har gammel verdi.
     *
     * @throws SQLException ved feil
     */
    @Test(invocationCount = 1)
    public void testUpdateFoo_H_rollback_og_old() throws SQLException {
        String unavn = "Old road";
        ConnectionForSnapshotVersion forSnapshotVersion = connectionManager.getForSnapshotVersion(SnapshotVersion.CURRENT);
        PreparedStatement preparedStatement = null;
        Connection unwrappedConnection = null;
        boolean feilet = false;

        try {
            unwrappedConnection = forSnapshotVersion.reserve();
            preparedStatement = unwrappedConnection.prepareStatement("update foo_h set navn = ? where id = ? and versjonId = ?");
            preparedStatement.setString(1, unavn);
            preparedStatement.setInt(2, 101);
            preparedStatement.setLong(3, 2);
            int updated = preparedStatement.executeUpdate();
            assertTrue(updated == 1, "Mer en en rad er oppdatert!!!!!!");

            //les tilbake oppdatert data
            String retNavn_ = getNavn(unwrappedConnection, 101, 2);
            assertTrue(retNavn_.equals(unavn));

            String oldNavn = getOld(101, 2);
            assertFalse(oldNavn.equals(retNavn_), "Old skal ikke vere likt oppdatert navn");

            forSnapshotVersion.rollback();

            //sjekk at vi kan lese tilbake de nye data.....
            String retNavn = getNavn(unwrappedConnection, 101, 2);
            if (unavn.equals(retNavn)) {
                //Hvis navnet forsatt er likt, er ikke rollback kjørt.
                feilet = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            //reset i tilfelle noe ikke virker så andre tester kan kjøre uforstyrret.
            try {
                if (unwrappedConnection != null) {
                    //resett etter testen.
                    preparedStatement = unwrappedConnection.prepareStatement("update foo_h set navn = ? where id = ? and versjonId = ?");
                    preparedStatement.setString(1, "GAMMELVEIEN");
                    preparedStatement.setInt(2, 101);
                    preparedStatement.setLong(3, 2);
                    int updated = preparedStatement.executeUpdate();
                    assertTrue(updated == 1, "Mer en en rad er oppdatert!!!!!!");
                }
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            } finally {
                forSnapshotVersion.release();
                if (feilet) {
                    fail("Oppdatering ikke rullet tilbake");
                }
            }
        }
    }

    private String getOld(int id, int version) {
        ConnectionForSnapshotVersion forOldSnapshotVersion = null;
        String oldNavn = null;
        try {
            //sjekke old
            forOldSnapshotVersion = connectionManager.getForSnapshotVersion(SnapshotVersion.OLD);
            Connection unwrappedOldConnection = forOldSnapshotVersion.reserve();
            oldNavn = getNavn(unwrappedOldConnection, id, version);
            assertNotNull(oldNavn, "Old skal ikke vere null");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            if (forOldSnapshotVersion != null) forOldSnapshotVersion.release();
        }
        return oldNavn;
    }

    private String getNavn(Connection unwrappedConnection, int id, int version) throws SQLException {
        String retNavn = null;
        PreparedStatement preparedStatement;
        preparedStatement = unwrappedConnection.prepareStatement("select * from foo_h where id = ? and versjonId = ?");
        preparedStatement.setInt(1, id);
        preparedStatement.setInt(2, version);
        ResultSet resultSet = preparedStatement.executeQuery();
        int returned = 0;
        if (resultSet != null) {
            while (resultSet.next()) {
                returned++;
                retNavn = resultSet.getString(6);
            }
            resultSet.close();
        }
        assertNotNull(retNavn);
        assertTrue(returned == 1);
        return retNavn;
    }


    /**
     * Sjekke commit.
     *
     * @throws SQLException
     */
    @Test(invocationCount = 1)
    public void testUpdateFoo_H_commit() throws SQLException {
        String unavn = "Old road commit";

        ConnectionForSnapshotVersion forSnapshotVersion = connectionManager.getForSnapshotVersion(SnapshotVersion.CURRENT);
        assertFalse(forSnapshotVersion.isClosed());
        PreparedStatement preparedStatement;
        Connection unwrappedConnection = null;
        try {
            unwrappedConnection = forSnapshotVersion.reserve();
            preparedStatement = unwrappedConnection.prepareStatement("update foo_h set navn = ? where id = ? and versjonId = ?");
            preparedStatement.setString(1, unavn);
            preparedStatement.setInt(2, 101);
            preparedStatement.setLong(3, 2);
            int updated = preparedStatement.executeUpdate();
            assertTrue(updated == 1, "Mer en en rad er oppdatert!!!!!!");
            unwrappedConnection.commit();

            String navn = getOld(101, 2);
            assertTrue(unavn.equals(navn), "Oppdatering ikke komittet");
        } catch (Exception e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            try {
                if (unwrappedConnection != null) {
                    int updated = 0;
                    //resett for andre tester.....
                    preparedStatement = unwrappedConnection.prepareStatement("update foo_h set navn = ? where id = ? and versjonId = ?");
                    preparedStatement.setString(1, "GAMMELVEIEN");
                    preparedStatement.setInt(2, 101);
                    preparedStatement.setInt(3, 2);
                    updated = preparedStatement.executeUpdate();
                    assertTrue(updated == 1, "Mer en en rad er oppdatert!!!!!!");
                    unwrappedConnection.commit();
                }
            } catch (Exception e) {
                e.printStackTrace();
                fail(e.getMessage());
            } finally {
                forSnapshotVersion.release();
            }
        }
    }


}
