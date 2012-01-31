package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.RunOnServerMethod;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.store.UnitOfWorkTransfer;
import no.statkart.skif.store.persistence.PersistenceSessionForSnapshot;
import no.statkart.skif.storetest.TestHelper;
import no.statkart.skif.storetest.domain.demo.*;
import no.statkart.skif.storetest.util.testsupport.StoreTestServerTestCase;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import weblogic.ejb20.persistence.spi.PersistenceRuntimeException;

import static no.statkart.skif.storetest.TestHelper.countInDatabase;
import static org.fest.assertions.Assertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertSame;

/**
 * Tester for {@link Store} på klient. Testene krever SingleVm mode Fordi de bruker @{link TestHelper} til å slette gamle
 * gamle bobler i databsen.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
@Test(groups = "singlevm-required")
public class StoreTestSVM extends StoreTestServerTestCase {
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

    TestBubbleId<TestBubble> TestBubbleId_1 = new TestBubbleId<TestBubble>(1);
    TestBubbleId<TestBubble> TestBubbleId_101 = new TestBubbleId<TestBubble>(101);
    ParrentBubbleId<ParrentBubble> ParrentBubbleId_101 = new ParrentBubbleId<ParrentBubble>(101);
    ChildBubbleId<ChildBubble> ChildBubbleId_101 = new ChildBubbleId<ChildBubble>(101);
    ChildBubbleId<ChildBubble> ChildBubbleId_102 = new ChildBubbleId<ChildBubble>(102);
    Long childForParrentId_102 = (long) 102;


    @Inject
    Store store;

    @BeforeMethod
    public void deletePriviouslyWritenTestBubbles() {
        server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            PersistenceSessionForSnapshot persistenceSessionForSnapshot;

            public Object run() {
                TestHelper.deletePriviouslyWritenTestBubbles(persistenceSessionForSnapshot);
                return null;
            }
        });

    }

    public int countInDatabase(final TestBubbleId bubbleId) {
        Object result = server.runInBeanManagedTransaction(new RunOnServerMethod() {
            @Inject
            PersistenceSessionForSnapshot persistenceSessionForSnapshot;

            public Object run() {
                return TestHelper.countInDatabase(persistenceSessionForSnapshot, bubbleId);
            }
        });
        return Integer.class.cast(result).intValue();
    }


    /**
     * Tester insert object
     */
    @Test(expectedExceptions = ImplementationException.class)
    public void testInsert() {
        TestBubble testBubble_100 = new TestBubble(TestBubbleId_101);
        testBubble_100.setText("Insert 1");
        store.insert(testBubble_100);
        assertEquals(countInDatabase(TestBubbleId_101), 1);
    }

    public void testLockObject() {
        TestBubble testBubble_1 = store.lock(TestBubbleId_1);
        assertSame(store.lock(TestBubbleId_1), testBubble_1);
    }


    /**
     * Tester insert object
     */
    private void insert() {
        TestBubble testBubble_100 = new TestBubble(TestBubbleId_101);
        testBubble_100.setText("Insert 1");
        store.insert(testBubble_100);
    }

    /**
     * Tester update object etter insert
     */
    public void testUpdate() {
        store.beginUnitOfWork();
        insert();
        TestBubble testBubble_101 = store.lock(TestBubbleId_101);
        assertEquals(testBubble_101.getText(), "Insert 1");

        testBubble_101.setText("Update 1");

        store.update(testBubble_101);
        UnitOfWorkTransfer unitOfWorkTransfer = store.getUnitOfWorkTransfer();
        store.endUnitOfWork();
        assertThat(unitOfWorkTransfer.getNewIds()).contains(TestBubbleId_101);
    }

    // TODO: flere tester


}
