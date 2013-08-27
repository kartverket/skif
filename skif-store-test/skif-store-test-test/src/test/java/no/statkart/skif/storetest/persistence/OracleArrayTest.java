package no.statkart.skif.storetest.persistence;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType;
import no.statkart.skif.store.persistence.OracleArrayType;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import no.statkart.skif.util.JDBCHelper;
import no.statkart.skif.util.OracleUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import java.sql.*;
import java.util.Collection;
import java.util.List;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

/**
 * Tester bruk av Oracle ARRAY i quiries via Hibernate user type og jdbc
 *
 * @author Henrik Fredholm
 * @since 2.3.0
 */
public class OracleArrayTest extends StoreTestServerTestCase {
    @Inject
    Session session;

    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;

    private Collection<SimpleId<?>> getSimpleIds() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        return ImmutableList.<SimpleId<?>>of(
                mockupFacade.getSimpleMockupFactory().getSimpleId1()
        );
    }

    public void testOracleLongBubbleIdArrayUserType() {
        Collection<SimpleId<?>> eierIds = getSimpleIds();
        SQLQuery query = session.
                createSQLQuery("select {s.*} from Simple {s} where s.id in (select * from table(:idValues))");
        query.addEntity("s", Simple.class);
        query.setParameter("idValues", eierIds, new OracleLongBubbleIdArrayCustomType());
        List<Simple> eiers = query.list();
        assertThat(eiers).hasSize(1);
    }


    public void testOracleNumberArrayUserType() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        Collection<Long> simpleIds = ImmutableList.of(
                mockupFacade.getSimpleMockupFactory().getSimpleId1().getValue()
        );

        PreparedStatement statement=null;
        try {
            Connection connection = OracleUtils.getOracleConnection(session.connection());
            statement = connection.prepareStatement("select s.id from Simple s where s.id in (select * from table(:idValues))");
            statement.setObject(1, OracleArrayType.getOracleNumberArray(connection, simpleIds));
            ResultSet resultSet = statement.executeQuery();
            int size =0;
            while (resultSet.next()) {
                size++;
            }
            assertEquals(size, 1);
        } catch (SQLException e) {
            JDBCHelper.close(statement);
        }
    }
}
