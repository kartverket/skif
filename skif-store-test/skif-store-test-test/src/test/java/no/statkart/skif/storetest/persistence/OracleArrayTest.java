package no.statkart.skif.storetest.persistence;

import com.google.common.collect.ImmutableList;
import com.google.inject.Inject;
import no.statkart.skif.persistence.hibernate.type.OracleArrayLongBubbleIdCustomType;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.persistence.OracleArrayAnyBubbleIdConverter;
import no.statkart.skif.store.persistence.OracleArrayConcatenatedFieldsConverter;
import no.statkart.skif.store.persistence.OracleArrayNumberConverter;
import no.statkart.skif.store.persistence.OracleArrayStringStringConverter;
import no.statkart.skif.storetest.domain.basic.BubbleWithAnyBubbleRef;
import no.statkart.skif.storetest.domain.basic.Simple;
import no.statkart.skif.storetest.domain.basic.SimpleId;
import no.statkart.skif.storetest.domain.basic.SomeIdent;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacade;
import no.statkart.skif.storetest.mockup.StoreTestMockupFacadeFactory;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import oracle.jdbc.OracleConnection;
import org.hibernate.Session;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.query.NativeQuery;
import org.testng.annotations.Test;

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

    // Testene er avhengig av Oracle JDBC driver
    private Connection getOracleConnection() throws SQLException {
        return ((SharedSessionContractImplementor) session)
            .getJdbcCoordinator()
            .getLogicalConnection()
            .getPhysicalConnection()
            .unwrap(OracleConnection.class); //verifiserer faktisk Oracle connection
    }

    private Collection<SimpleId<?>> getSimpleIds() {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        return ImmutableList.<SimpleId<?>>of(
                mockupFacade.getSimpleMockupFactory().getSimpleId1()
        );
    }

    @Test
    public void testOracleArrayLongBubbleIdCustomType() {
        Collection<SimpleId<?>> eierIds = getSimpleIds();
        NativeQuery<Simple> query = session
            .createNativeQuery("select s.* from Simple s where s.id in (select * from table(:idValues))", Simple.class)
            .setParameter("idValues", eierIds, new OracleArrayLongBubbleIdCustomType());

        assertThat(query.list()).hasSize(1);
    }


    @Test
    public void testOracleArrayNumberConverter_setArray() throws SQLException {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        Collection<Long> simpleIds = ImmutableList.of(
                mockupFacade.getSimpleMockupFactory().getSimpleId1().getValue()
        );

        var connection = getOracleConnection();
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

    @Test
    public void testOracleArrayNumberConverter_setObject() throws SQLException {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        Collection<Long> simpleIds = ImmutableList.of(
                mockupFacade.getSimpleMockupFactory().getSimpleId1().getValue()
        );

        var connection = getOracleConnection();
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

    @Test
    public void testOracleArrayAnyBubbleIdConverter() throws SQLException {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        Collection<BubbleId<?>> anyIds = ImmutableList.<BubbleId<?>>of(
                mockupFacade.getSimpleMockupFactory().getSimpleId2(),
                mockupFacade.getBubbleWithRelationMockupFactory().getBubbleWithRelationId1()
        );

        var connection = getOracleConnection();
        try (PreparedStatement statement = connection.prepareStatement("select b.id from BubbleWithAnyBubbleRef b where (b.anyId, b.anyIdClass) in (select * from table(:anyBubbleIds))")) {
            statement.setArray(1, new OracleArrayAnyBubbleIdConverter().toArray(connection, anyIds));
            ResultSet resultSet = statement.executeQuery();
            int size = 0;
            while (resultSet.next()) {
                size++;
            }
            assertEquals(size, 2);
        }
    }

    @Test
    public void testOracleArrayStringStringConverter() throws SQLException {
        final var mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();
        final var simpleId1 = mockupFacade.getSimpleMockupFactory().getSimpleId1();
        Object[][] values = {{"1", "Ingen BubbleWithRelation peker til denne"}};

        var connection = getOracleConnection();
        try (PreparedStatement statement = connection.prepareStatement("select s.id from Simple s where (s.nr,s.text) in (select * from table(:idValues))")) {
            statement.setArray(1, new OracleArrayStringStringConverter().toArray(connection, values));
            ResultSet resultSet = statement.executeQuery();
            assertThat(resultSet.next()).isTrue();
            assertThat(resultSet.getLong(1)).isEqualTo(simpleId1.getValue());
            assertThat(resultSet.next()).as("Kun èn rad").isFalse();
        }
    }

    @Test
    public void testOracleArrayConcatenatedFieldsConverter() throws SQLException {
        StoreTestMockupFacade mockupFacade = mockupFacadeFactory.getReadMockupFacadeAndSaveData();

        BubbleWithAnyBubbleRef bubbleWithAnyBubbleRef = mockupFacade.getStore().get(mockupFacade.getBubbleWithAnyBubbleRefMockupFactory().getBubbleWithAnyBubbleRefId2());
        ImmutableList<SomeIdent> identValues = ImmutableList.of(
                new SomeIdent(bubbleWithAnyBubbleRef.getNr(), "null")
        );

        var connection = getOracleConnection();
        try (PreparedStatement statement = connection.prepareStatement("select b.id from BubbleWithAnyBubbleRef b where (b.someIdentValue, b.someIdentClass) in (select * from table(:identValues))")) {
            statement.setArray(1, new OracleArrayConcatenatedFieldsConverter().toArray(connection, identValues));
            ResultSet resultSet = statement.executeQuery();
            int size = 0;
            while (resultSet.next()) {
                size++;
            }
            assertEquals(size, 1);
        }
    }

}
