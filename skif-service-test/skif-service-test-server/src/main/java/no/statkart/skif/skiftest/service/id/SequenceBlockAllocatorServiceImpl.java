package no.statkart.skif.skiftest.service.id;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.util.JDBCHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class SequenceBlockAllocatorServiceImpl implements SequenceBlockAllocatorService {
    final Provider<Connection> connectionProvider;

    @Inject
    public SequenceBlockAllocatorServiceImpl(Provider<Connection> connectionProvider) {
        this.connectionProvider = connectionProvider;
    }

    @Override
    public long allocateSequenceBlock(String sequenceName, int blockSize) {
        if( blockSize <= 0 ) throw new RuntimeException("Blocksize må være positiv: " + blockSize);

        Connection con = connectionProvider.get();

        long prevFreeNumber=0;
        long nextFreeNumber=0;

        PreparedStatement stmt = null;
        try {
           String sqlString = "SELECT NEXTFREENUMBER FROM TABLESEQUENCE WHERE TABLENAME=? FOR UPDATE";
           stmt = con.prepareStatement(sqlString);
           stmt.setString(1, sequenceName);
           ResultSet rs = stmt.executeQuery();
           if( rs.next() ) {
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
           if (result!=1) {
              throw new ImplementationException("Oppdatering av sekvens med sekvensnavn: " + sequenceName + "feilet. Forventet 1 oppdatering. Fikk: " + result);
           }
        } catch( SQLException e ) {
           throw new OperationalException("Oppdatering av sekvens feilet: " + sequenceName, e);
        } finally {
           if( stmt != null ) try {
              stmt.close();
           } catch( SQLException e ) {
              throw new RuntimeException(e);
           }
        }
        return nextFreeNumber - 1;
    }
}
