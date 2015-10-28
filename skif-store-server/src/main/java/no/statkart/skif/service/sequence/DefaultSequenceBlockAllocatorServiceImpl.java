package no.statkart.skif.service.sequence;

import com.google.inject.Provider;
import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.SkifConfigConstants;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.util.JDBCHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Standardimplementasjon av en sekvensblokkallokeringstjeneste. Både denne og interfacet må subklasses, samt at EJB må
 * lages, i hver enkelt applikasjon. Navn på disse bestemmes av applikasjonen, og må samsvare med hverandre på vanlig
 * måte.
 *
 * @author Roar Ingebrigtsen
 * @author Tor Egil R. Strand
 * @since 2.1
 */
public class DefaultSequenceBlockAllocatorServiceImpl implements SequenceBlockAllocatorService {
    protected static final String NEXTFREENUMBER = "NEXTFREENUMBER";
    protected static final String SEQUENCENAME = "TABLENAME";

    protected final Provider<Connection> connectionProvider;
    protected final Configuration configuration;

    public DefaultSequenceBlockAllocatorServiceImpl(Provider<Connection> connectionProvider, Configuration configuration) {
        this.connectionProvider = connectionProvider;
        this.configuration = configuration;
    }

    protected String getTablename() {
        return configuration.getString(SkifConfigConstants.DB_SEQUENCE_TABLENAME);
    }

    @Override
    public long allocateSequenceBlock(String sequenceName, int blockSize) {
        if (blockSize <= 0) throw new ImplementationException("Block size must be positive: " + blockSize);

        Connection con = connectionProvider.get();

        final long prevFreeNumber;
        final long nextFreeNumber;

        PreparedStatement stmt = null;
        try {
            String tablename = getTablename();

            String sqlString = "SELECT " + NEXTFREENUMBER + " FROM " + tablename + " WHERE " + SEQUENCENAME + "=? FOR UPDATE";
            stmt = con.prepareStatement(sqlString);
            stmt.setString(1, sequenceName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                prevFreeNumber = rs.getLong(1);
            } else {
                throw new ImplementationException("Found no sequence named:" + sequenceName);
            }
            JDBCHelper.close(rs, stmt);

            nextFreeNumber = prevFreeNumber + blockSize;
            sqlString = "UPDATE " + tablename + " SET " + NEXTFREENUMBER + "=? WHERE " + SEQUENCENAME + "=?";
            stmt = con.prepareStatement(sqlString);
            stmt.setLong(1, nextFreeNumber);
            stmt.setString(2, sequenceName);
            int result = stmt.executeUpdate();
            if (result != 1) {
                con.rollback();
                throw new ImplementationException("Failed to update sequence named: " + sequenceName + ". Expected one update, got " + result);
            }
            commit(con);
        } catch (SQLException e) {
            throw new OperationalException("Failed to update sequence named: " + sequenceName, e);
        } finally {
            if (stmt != null) try {
                stmt.close();
            } catch (SQLException e) {
                throw new OperationalException(e);
            }
        }
        return nextFreeNumber - 1;
    }

    /**
     * Standardoppførsel er at implementasjonen committer transaksjonen. De som ønsker å benytte sekvensnallokatoren i
     * egen transaksjon overrider denne til å ikke gjøre noe.
     *
     * @param con    databaseforbindelsen
     * @throws SQLException
     */
    protected void commit(Connection con) throws SQLException {
        con.commit();
    }
}
