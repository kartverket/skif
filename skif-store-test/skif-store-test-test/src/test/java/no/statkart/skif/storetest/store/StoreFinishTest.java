package no.statkart.skif.storetest.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.OperationalException;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.basic.BubbleWithFilter;
import no.statkart.skif.storetest.domain.basic.BubbleWithFilterId;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import no.statkart.skif.util.JDBCHelper;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.testng.Assert.assertNotSame;

/**
 * Tester at finish blir kalt av service-kjeden.
 *
 * @author Tor Egil R. Strand
 * @since 2.1
 */
@Test(groups = "singlevm-required", enabled=false)
@Deprecated // TODO : lage bedre testcase for filtered properties, ala person med skjermet id hvor noen bruker kan se felt.
public class StoreFinishTest extends StoreTestMixedTestCase {
    private static final String str = "Skal byttes ut i finish";

    private static final BubbleWithFilterId<BubbleWithFilter> BubbleWithFilterId_101 = new BubbleWithFilterId<BubbleWithFilter>(101);

    @BeforeMethod
    public void beforeMethod() {
        server.runInTxRequiresNew(new RunOnServerMethod() {
            @Inject
            private Connection connection;

            @Override
            public Object run() {
                Statement statement = null;
                try {
                    statement = connection.createStatement();
                    statement.executeUpdate("delete from BubbleWithFilter where id>100");
                } catch (SQLException e) {
                    throw new OperationalException(e);
                } finally {
                    JDBCHelper.close(statement);
                }
                return null;
            }
        });
    }

    public void testFinishFilter() {
        server.runInTxRequiresNew(new SaveFiltered());

        Store store = injector.getInstance(Store.class);
        BubbleWithFilter lest = store.get(BubbleWithFilterId_101);
        assertNotSame(str, lest.getFilterText());
    }

    private static class SaveFiltered extends RunOnServerMethod {
        @Inject
        private Store storeServer;

        @Override
        public Object run() {
            BubbleWithFilter BubbleWithFilter = new BubbleWithFilter(BubbleWithFilterId_101, "Finish 101", false, str);
            storeServer.insert(BubbleWithFilter);

            return null;
        }
    }
}
