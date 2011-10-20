package no.statkart.skif.storetest.persistence.dbutil;

import no.statkart.skif.config.Configuration;
import no.statkart.skif.config.PropertiesConfiguration;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.SnapshotVersionSeed;
import no.statkart.skif.store.persistence.hibernate.HibernateSessionFactoryBuilder;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.kodeliste.TestDbKodeliste;
import no.statkart.skif.storetest.domain.kodeliste.TestDbKodelisteId;
import no.statkart.skif.storetest.domain.demo.koder.TestADbKode;
import no.statkart.skif.storetest.domain.demo.koder.TestADbKodeId;
import no.statkart.skif.storetest.domain.demo.koder.TestBDbKode;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.DataSetException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.FileNotFoundException;

import static org.testng.Assert.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
@Test
public class DbUtilKodeTest {
    private IDatabaseTester databaseTester;
    private Configuration configuration = new PropertiesConfiguration("skif.properties");


    @BeforeClass
    public void testDdUtilSetup() throws Exception, FileNotFoundException, DataSetException {
        /*
        String username = configuration.getString(ConfigurationConstants.DB_USERNAME);
        String password = configuration.getString(ConfigurationConstants.DB_PASSWORD);
        String sid = configuration.getString(ConfigurationConstants.DB_SID);
        String hostname = configuration.getString(ConfigurationConstants.DB_HOSTNAME);
        String port = configuration.getString(ConfigurationConstants.DB_PORT);
        String url = String.format("jdbc:oracle:thin:@%s:%s:%s", hostname, port, sid);


        FlatXmlDataSet flatXmlDataSet = new FlatXmlDataSetBuilder().build(ResourceUtils.locateFromClasspath("no/statkart/skif/storetest/persistence/dbutil/dataset-koder.xml").openStream());
        databaseTester = new JdbcDatabaseTester("oracle.jdbc.OracleDriver",url, username, password, username);
        databaseTester.setSetUpOperation(DatabaseOperation.REFRESH);
        databaseTester.setDataSet(flatXmlDataSet);
        databaseTester.onSetup();
        */
    }

    private SessionFactory setupHibernate() {
        HibernateSessionFactoryBuilder sfbuilder = TestHelper.createHibernateSessionFactoryBuilder();
        sfbuilder.addResourceUsingRelativePath("kodeliste", TestADbKode.class);
        sfbuilder.addResourceUsingRelativePath("kodeliste", TestBDbKode.class);
        sfbuilder.addResourceUsingRelativePath("kodeliste", TestDbKodeliste.class);
        SessionFactory sf = sfbuilder.build(new SnapshotVersionSeed(SnapshotVersion.CURRENT));
        assertNotNull(sf);
        return sf;
    }

    public void testHibernate() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        TestADbKode obj = (TestADbKode) session.load(TestADbKode.class, TestADbKodeId.createInstance(1));
        assertEquals(obj.getId().getValue(), new Long(1));

    }

    public void testUpdate() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        Transaction transaction = session.beginTransaction();

        TestADbKode obj = (TestADbKode) session.load(TestADbKode.class, TestADbKodeId.createInstance(1));
        obj.getLokalisertBeskrivelse().put("f", "test");
        transaction.commit();
        session.clear();
        TestADbKode obj1 = (TestADbKode) session.load(TestADbKode.class, TestADbKodeId.createInstance(1));
        assertEquals(obj1.getLokalisertBeskrivelse().get("f"), "test");

    }

    public void testLastKodeliste() {
        SessionFactory sf = setupHibernate();
        Session session = sf.openSession();
        TestDbKodeliste obj = (TestDbKodeliste) session.load(TestDbKodeliste.class, new TestDbKodelisteId(10001L, SnapshotVersion.CURRENT));
        assertEquals(obj.getKodeClass(), TestADbKode.class);
    }
}
