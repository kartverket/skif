package no.statkart.skif.storetest2.persistence;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType;
import no.statkart.skif.store.persistence.OracleArrayType;
import no.statkart.skif.storetest2.domain.eierskap.Eier;
import no.statkart.skif.storetest2.domain.eierskap.EierId;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacade;
import no.statkart.skif.storetest2.mockup.StoreTest2MockupFacadeFactory;
import no.statkart.skif.storetest2.util.testsupport.StoreTest2ServerTestCase;
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
 */
public class OracleArrayTest extends StoreTest2ServerTestCase {
    @Inject
    Session session;

    @Inject
    StoreTest2MockupFacadeFactory mockupFacadeFactory;

    private Collection<EierId<?>> getEierIds() {
        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        return ImmutableList.<EierId<?>>of(
                mockupFacade.getEierMockupFactory().getEier1Id()
        );
    }

    public void testOracleLongBubbleIdArrayUserType() {
        Collection<EierId<?>> eierIds = getEierIds();
        SQLQuery query = session.
                createSQLQuery("select {e.*} from Eier {e} where e.id in (select * from table(:idValues))");
        query.addEntity("e", Eier.class);
        query.setParameter("idValues", eierIds, new OracleLongBubbleIdArrayCustomType());
        List<Eier> eiers = query.list();
        assertThat(eiers).hasSize(1);
    }


    public void testOracleNumberArrayUserType() {
        StoreTest2MockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        Collection<Long> eierIds = ImmutableList.of(
                mockupFacade.getEierMockupFactory().getEier1Id().getValue()
        );

        PreparedStatement statement=null;
        try {
            Connection connection = OracleUtils.getOracleConnection(session.connection());
            statement = connection.prepareStatement("select e.id from Eier e where e.id in (select * from table(:idValues))");
            statement.setObject(1, OracleArrayType.getOracleNumberArray(connection, eierIds));
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
