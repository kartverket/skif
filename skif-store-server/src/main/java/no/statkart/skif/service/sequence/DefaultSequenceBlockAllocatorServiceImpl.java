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
    protected final Provider<Connection> connectionProvider;
    protected final Configuration configuration;

    public DefaultSequenceBlockAllocatorServiceImpl(Provider<Connection> connectionProvider, Configuration configuration) {
        this.connectionProvider = connectionProvider;
        this.configuration = configuration;
    }

    //TODO: Skal denne bare redirecte til den andre implementasjonen?
    @Override
    public long allocateSequenceBlock(String sequenceName, int blockSize) {
        if (blockSize <= 0) throw new RuntimeException("Blocksize må være positiv: " + blockSize);

        Connection con = connectionProvider.get();

        long prevFreeNumber = 0;
        long nextFreeNumber = 0;

        PreparedStatement stmt = null;
        try {
            String sqlString = "SELECT NEXTFREENUMBER FROM " + configuration.getString(SkifConfigConstants.DB_SEQUENCE_TABLENAME) + " WHERE TABLENAME=? FOR UPDATE";
            stmt = con.prepareStatement(sqlString);
            stmt.setString(1, sequenceName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                prevFreeNumber = rs.getLong(1);
            } else {
                throw new ImplementationException("Fant ingen sekvens med sekvensnavn:" + sequenceName);
            }
            JDBCHelper.close(rs, stmt);

            nextFreeNumber = prevFreeNumber + blockSize;
            sqlString = "UPDATE TABLESEQUENCE SET NEXTFREENUMBER=? WHERE TABLENAME=?";
            stmt = con.prepareStatement(sqlString);
            stmt.setLong(1, nextFreeNumber);
            stmt.setString(2, sequenceName);
            int result = stmt.executeUpdate();
            if (result != 1) {
                con.rollback();
                throw new ImplementationException("Oppdatering av sekvens med sekvensnavn: " + sequenceName + "feilet. Forventet 1 oppdatering. Fikk: " + result);
            }
            con.commit();
        } catch (SQLException e) {
            throw new OperationalException("Oppdatering av sekvens feilet: " + sequenceName, e);
        } finally {
            if (stmt != null) try {
                stmt.close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return nextFreeNumber - 1;
    }
}
