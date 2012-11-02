package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.StoreServer;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.demo.Foo;
import no.statkart.skif.storetest.domain.demo.FooId;
import no.statkart.skif.storetest.domain.demo.TestBubble;
import no.statkart.skif.storetest.domain.demo.TestBubbleId;
import no.statkart.skif.storetest.util.testsupport.StoreTestMixedTestCase;
import no.statkart.skif.util.CopyHelper;
import org.testng.annotations.Test;

import static no.statkart.skif.storetest.TestHelper.assertNotFound;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * Tester bruk av UnitOfWork på server. Alle tester kjøres via bean managed transaction slik at ingen ting blir
 * committet til databasen.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class StoreUnitOfWorkTest extends StoreTestMixedTestCase {
    static String T1 = "2011-10-02 08:01:00.00";
    static String T2 = "2011-10-02 08:02:00.00";
    static String T3 = "2011-10-02 08:03:00.00";
    static String T4 = "2011-10-02 08:04:00.00";
    static SnapshotVersion CURRENT = SnapshotVersion.CURRENT;
    static SnapshotVersion OLD = SnapshotVersion.OLD;
    static SnapshotVersion S1 = SnapshotVersion.createInstance(T1);
    static SnapshotVersion S2 = SnapshotVersion.createInstance(T2);
    static SnapshotVersion S3 = SnapshotVersion.createInstance(T3);
    static SnapshotVersion S4 = SnapshotVersion.createInstance(T4);

    FooId<Foo> FooId_100_CURRENT = new FooId<Foo>(100L, SnapshotVersion.CURRENT);
    FooId<Foo> FooId_100_OLD = new FooId<Foo>(100L, SnapshotVersion.OLD);
    FooId<Foo> FooId_100_S1 = new FooId<Foo>(100L, S1);
    FooId<Foo> FooId_100_S2 = new FooId<Foo>(100L, S2);
    FooId<Foo> FooId_100_S3 = new FooId<Foo>(100L, S3);
    FooId<Foo> FooId_100_S4 = new FooId<Foo>(100L, S4);

    FooId<Foo> FooId_101_CURRENT = new FooId<Foo>(101L, SnapshotVersion.CURRENT);
    FooId<Foo> FooId_101_S3 = new FooId<Foo>(101L, S3);
    FooId<Foo> FooId_101_S4 = new FooId<Foo>(101L, S4);
    FooId<Foo> FooId_101_OLD = new FooId<Foo>(101L, SnapshotVersion.OLD);

    FooId<Foo> FooId_10001_CURRENT = new FooId<Foo>(10001L, SnapshotVersion.CURRENT);

    public void testBeginEndEmptyUnitOfWork() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            Store store;

            public Object run() {
                store.beginUnitOfWork();
                store.commitUnitOfWork();
                return null;
            }
        });
    }


    public void testInsertObjectInUnitOfWork() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            StoreServer store;
            @Inject
            PersistenceSessionForSnapshot persistenceSessionForSnapshot;

            public Object run() {
                TestHelper.deletePriviouslyWritenTestBubbles(persistenceSessionForSnapshot);
                store.beginUnitOfWork();
                TestBubbleId<TestBubble> TestBubbleId_101_CURRENT = new TestBubbleId<TestBubble>(101L);
                TestBubble testBubble1 = new TestBubble(TestBubbleId_101_CURRENT, "TestBubble 101");
                store.insert(testBubble1);
                store.commitUnitOfWork();
                assertSame(store.get(TestBubbleId_101_CURRENT), testBubble1);
                store.flush();

                assertEquals(TestHelper.countInDatabase(persistenceSessionForSnapshot, TestBubbleId_101_CURRENT), 1);
                return null;
            }
        });
    }



    public void testInsertDeleteObjectInSameUnitOfWork() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            Store store;
            @Inject
            PersistenceSessionForSnapshot persistenceSessionForSnapshot;

            public Object run() {
                TestHelper.deletePriviouslyWritenTestBubbles(persistenceSessionForSnapshot);
                store.beginUnitOfWork();
                TestBubbleId<TestBubble> TestBubbleId_101_CURRENT = new TestBubbleId<TestBubble>(101L);
                TestBubble testBubble1 = new TestBubble(TestBubbleId_101_CURRENT, "TestBubble 101");
                store.insert(testBubble1);
                store.delete(testBubble1);
                store.commitUnitOfWork();
                assertNotFound(store, TestBubbleId_101_CURRENT);

                return null;
            }
        });
    }


    public void testInsertDeleteObjectViaNestedUnitOfWork() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            Store store;
            @Inject
            PersistenceSessionForSnapshot persistenceSessionForSnapshot;

            public Object run() {
                TestHelper.deletePriviouslyWritenTestBubbles(persistenceSessionForSnapshot);
                store.beginUnitOfWork();
                TestBubbleId<TestBubble> TestBubbleId_101_CURRENT = new TestBubbleId<TestBubble>(101L);
                TestBubble testBubble1 = new TestBubble(TestBubbleId_101_CURRENT, "TestBubble 101");
                store.insert(testBubble1);
                store.beginUnitOfWork();
                TestBubble copy = CopyHelper.copy(testBubble1);
                store.delete(copy);
                store.commitUnitOfWork();
                assertSame(store.get(TestBubbleId_101_CURRENT), copy);
                store.commitUnitOfWork();
                assertNotFound(store, TestBubbleId_101_CURRENT);

                return null;
            }
        });
    }

    @Test(expectedExceptions = ImplementationException.class)
    public void testInsertDeleteObjectViaNestedUnitOfWork_Fail() {

        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            Store store;
            @Inject
            PersistenceSessionForSnapshot persistenceSessionForSnapshot;

            public Object run() {
                TestHelper.deletePriviouslyWritenTestBubbles(persistenceSessionForSnapshot);
                store.beginUnitOfWork();
                TestBubbleId<TestBubble> TestBubbleId_101_CURRENT = new TestBubbleId<TestBubble>(101L);
                TestBubble testBubble1 = new TestBubble(TestBubbleId_101_CURRENT, "TestBubble 101");
                store.insert(testBubble1);
                store.beginUnitOfWork();
                TestBubble copy = testBubble1; //CopyHelper.copy(testBubble1); // Bruker feil instans her
                store.delete(copy);
                return null;
            }
        });
    }


}
