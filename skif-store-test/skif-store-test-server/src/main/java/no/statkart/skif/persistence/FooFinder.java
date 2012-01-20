package no.statkart.skif.persistence;

import com.google.inject.Inject;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.demo.Baz;
import no.statkart.skif.storetest.domain.demo.BazId;
import no.statkart.skif.storetest.domain.demo.Foo;
import no.statkart.skif.storetest.domain.demo.FooId;
import no.statkart.skif.util.JDBCHelper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class FooFinder {

    @Inject
    private no.statkart.skif.persistence5.jdbc.ConnectionManager connectionManager;

    public Set<FooId<Foo>> findFooIdsForNr(long nr) {
        Set<FooId<Foo>> fooIds = new HashSet<FooId<Foo>>();
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = connectionManager.getForSnapshotVersion(SnapshotVersion.CURRENT).prepareStatement("select id from foo where nr = ?");
            preparedStatement.setLong(1, nr);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                fooIds.add(new FooId<Foo>(resultSet.getLong(1)));
            }
        } catch (SQLException e) {
            throw new ImplementationException(e);
        } finally {
            JDBCHelper.close(resultSet, preparedStatement);
        }

        return fooIds;
    }

}
