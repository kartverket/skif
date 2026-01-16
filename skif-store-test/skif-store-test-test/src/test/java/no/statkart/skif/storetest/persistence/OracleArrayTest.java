package no.statkart.skif.storetest.persistence;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import no.statkart.skif.persistence.hibernate.type.OracleLongBubbleIdArrayCustomType;
import no.statkart.skif.store.persistence.OracleArrayNumberConverter;
import no.statkart.skif.store.persistence.OracleArrayStringStringConverter;
import no.statkart.skif.store.persistence.OracleArrayType;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import no.statkart.skif.util.OracleUtils;
import org.hibernate.Session;
import org.hibernate.internal.SessionImpl;
import org.hibernate.query.NativeQuery;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.testng.Assert.assertEquals;

/**
 * Tester bruk av Oracle ARRAY i queries via Hibernate user type og jdbc basert på OracleArrayConverter og subtyper av denne
 *
 * @author Henrik Fredholm
 * @since 2.3.0
 */
public class OracleArrayTest extends StoreTestServerTestCase {
    @Inject
    Session session;

    @Inject
    StoreTestMockupFacadeFactory mockupFacadeFactory;

    private SessionImpl session() {
        return (SessionImpl) session;
    }

    private Collection<SimpleId<?>> getSimpleIds() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        return ImmutableList.<SimpleId<?>>of(
                mockupFacade.getSimpleMockupFactory().getSimpleId1()
        );
    }

    public void testOracleLongBubbleIdArrayCustomType() {
        Collection<SimpleId<?>> eierIds = getSimpleIds();
        NativeQuery<Simple> query = session
            .createNativeQuery("select s.* from Simple s where s.id in (select * from table(:idValues))", Simple.class)
            .setParameter("idValues", eierIds, new OracleLongBubbleIdArrayCustomType());

        assertThat(query.list()).hasSize(1);
    }


    public void testOracleNumberArrayType() throws SQLException {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        Collection<Long> simpleIds = ImmutableList.of(
                mockupFacade.getSimpleMockupFactory().getSimpleId1().getValue()
        );

        Connection connection = OracleUtils.getOracleConnection(session().doReturningWork(c -> c));
        try (PreparedStatement statement = connection.prepareStatement("select s.id from Simple s where s.id in (select * from table(:idValues))")) {
            statement.setObject(1, OracleArrayType.getOracleNumberArray(connection, simpleIds));
            ResultSet resultSet = statement.executeQuery();
            int size = 0;
            while (resultSet.next()) {
                size++;
            }
            assertEquals(size, 1);
        }
    }

    public void testOracleArrayNumberConverter() throws SQLException {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        Collection<Long> simpleIds = ImmutableList.of(
                mockupFacade.getSimpleMockupFactory().getSimpleId1().getValue()
        );

        Connection connection = OracleUtils.getOracleConnection(session().doReturningWork(c -> c));
        try (PreparedStatement statement = connection.prepareStatement("select s.id from Simple s where s.id in (select * from table(:idValues))")) {
            statement.setArray(1, new OracleArrayNumberConverter().toArray(connection, simpleIds));
            ResultSet resultSet = statement.executeQuery();
            int size = 0;
            while (resultSet.next()) {
                size++;
            }
            assertEquals(size, 1);
        }
    }


//    public void testOracleArrayAnyBubbleIdConverter() throws SQLException {
//        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
//        Collection<BubbleId<?>> anyIds = ImmutableList.<BubbleId<?>>of(
//                mockupFacade.getSimpleMockupFactory().getSimpleId2(),
//                mockupFacade.getBubbleWithRelationMockupFactory().getBubbleWithRelationId1()
//        );
//
//        Connection connection = OracleUtils.getOracleConnection(session().doReturningWork(c -> c));
//        try (PreparedStatement statement = connection.prepareStatement("select b.id from BubbleWithAnyBubbleRef b where (b.anyId, b.anyIdClass) in (select * from table(:anyBubbleIds))")) {
//            statement.setArray(1, new OracleArrayAnyBubbleIdConverter().toArray(connection, anyIds));
//            ResultSet resultSet = statement.executeQuery();
//            int size = 0;
//            while (resultSet.next()) {
//                size++;
//            }
//            assertEquals(size, 2);
//        }
//    }

//    public void testOracleArrayStringStringConverter() throws SQLException {
//        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
//
//            Object[][] values =
//                    {
//                            {mockupFacade.getSimpleMockupFactory().getSimpleId2().getClass().getName(), "En BubbleWithRelation (nr 1) peker til denne"}
//                    };
//
//        Connection connection = OracleUtils.getOracleConnection(session().doReturningWork(c -> c));
//        try (PreparedStatement statement = connection.prepareStatement("select s.id from BubbleWithAnyBubbleRef b, Simple s where b.anyId=s.id and s.id=:sId and (b.anyIdClass,s.text) in (select * from table(:idValues))")) {
//            statement.setLong(1, mockupFacade.getSimpleMockupFactory().getSimpleId2().getValue());
//            statement.setArray(2, new OracleArrayStringStringConverter().toArray(connection, values));
//            ResultSet resultSet = statement.executeQuery();
//            int size = 0;
//            while (resultSet.next()) {
//                size++;
//            }
//            assertEquals(size, 1);
//        }
//    }

//    public void testOracleArrayConcatenatedFieldsConverter() throws SQLException {
//        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
//
//        BubbleWithAnyBubbleRef bubbleWithAnyBubbleRef = mockupFacade.getStore().get(mockupFacade.getBubbleWithAnyBubbleRefMockupFactory().getBubbleWithAnyBubbleRefId2());
//        ImmutableList<SomeIdent> identValues = ImmutableList.of(
//                new SomeIdent(bubbleWithAnyBubbleRef.getNr(), "null")
//        );
//
//        Connection connection = OracleUtils.getOracleConnection(session().doReturningWork(c -> c));
//        try (PreparedStatement statement = connection.prepareStatement("select b.id from BubbleWithAnyBubbleRef b where (b.someIdentValue, b.someIdentClass) in (select * from table(:identValues))")) {
//            statement.setArray(1, new OracleArrayConcatenatedFieldsConverter().toArray(connection, identValues));
//            ResultSet resultSet = statement.executeQuery();
//            int size = 0;
//            while (resultSet.next()) {
//                size++;
//            }
//            assertEquals(size, 1);
//        }
//    }

}
