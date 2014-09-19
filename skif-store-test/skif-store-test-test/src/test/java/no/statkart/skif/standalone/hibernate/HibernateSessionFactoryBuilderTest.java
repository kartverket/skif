package no.statkart.skif.standalone.hibernate;

import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilderImpl;
import no.statkart.skif.standalone.util.testsupport.StandAloneTestHelper;
import no.statkart.skif.storetest.domain.mockup.Foo;
import no.statkart.skif.storetest.domain.standalone.TestEntity;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

import static org.testng.Assert.*;

/**
 * Tester opprettelse og frigivelse av Hibernate SessionFactory og Session via HibernateSessionFactoryBuilder.
 * <p>
 * I Oracle 10.2.0.3 har det tidligere vært et problem at  databasen løpe tør for database connections fordi databasen
 * muligvis ikke frigir connections rask nok når sessions opprettes rask etter hverandre. Løsningen her var å
 * legge inn en forsinkelse i Oracle's listener "(RATE_LIMIT=25)":
 * <pre>
 *    LISTENER =
 *     (DESCRIPTION_LIST =
 *       (DESCRIPTION =
 *          (ADDRESS = (PROTOCOL = TCP)(HOST = WSFREHEN2)(PORT = 1521)(RATE_LIMIT=25))
 *       )
 *     )
 * </pre>
 * <p>
 * Dette er en stand-alone-test som går direkte mot databasen uten å bruke StoreTestServer modulen. Mest naturlig at testene
 * kjøres i singleVM mode.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class HibernateSessionFactoryBuilderTest {

    private HibernateSessionFactoryBuilder createHibernateSessionFactoryBuilder() {
        return new HibernateSessionFactoryBuilderImpl("no/statkart/skif/storetest/persistence/hibernate36");
    }

    public void testCreateFactorySessionAndConnection() throws SQLException, InterruptedException {
        HibernateSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(Foo.class);
        Properties hibernateProperties = StandAloneTestHelper.createHibernatePropertiesSingleVm() ;
        SessionFactory sf = sfbuilder.build(new SnapshotVersionSeed(SnapshotVersion.CURRENT), hibernateProperties, null);
        assertNotNull(sf);
        Session s = sf.openSession();
        Connection c = s.connection();
        Statement statement = c.createStatement();
        ResultSet rs = statement.executeQuery("select 1 from dual");
        rs.next();
        assertEquals(rs.getInt(1), 1);
        statement.close();
        c.close();
        s.close();
        sf.close();
    }

    @Test(invocationCount = 1 /*200*/, groups="slow")
    public void testCreateFactorySessionAndConnectionMulti() throws SQLException, InterruptedException {
        testCreateFactorySessionAndConnection();
    }

    public void testCreateFactoryWithEntity() throws SQLException {
        HibernateSessionFactoryBuilder sfbuilder = createHibernateSessionFactoryBuilder();
        sfbuilder.addResource(TestEntity.class);
        Properties hibernateProperties = StandAloneTestHelper.createHibernatePropertiesSingleVm() ;
        SessionFactory sf = sfbuilder.build(new SnapshotVersionSeed(SnapshotVersion.CURRENT), hibernateProperties, null);
        assertNotNull(sf);
        Session s = sf.openSession();
        Query query = s.createQuery("from TestEntity");
        List list = query.list();
        assertNotNull(list);
        s.close();
        sf.close();
    }

    @Test(invocationCount = 200, groups="slow")
    public void testCreateFactoryWithEntityMulti() throws SQLException {
        testCreateFactoryWithEntity();
    }

}
