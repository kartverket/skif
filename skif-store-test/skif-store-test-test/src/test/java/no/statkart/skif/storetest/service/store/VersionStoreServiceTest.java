package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.Store;
import no.statkart.skif.storetest.domain.Foo;
import no.statkart.skif.storetest.domain.FooId;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@Test
public class VersionStoreServiceTest extends StoreTestTestCase {
    @Inject
    StoreService service;

    @Inject
    Store store;

    SnapshotVersion intervalStart1 = SnapshotVersion.createInstance("2011-10-02 08:00:00.00");
    SnapshotVersion intervalEnd1 = SnapshotVersion.CURRENT;
    SnapshotVersion intervalStart2 = SnapshotVersion.createInstance("2011-10-02 08:01:00.00");
    SnapshotVersion intervalEnd2 = SnapshotVersion.CURRENT;

    public void testFindBubbleIdsForIntervalSomInneholderAlleHeltTilCurrent() {

        Collection<FooId<Foo>> ids1 = service.getVersions(new FooId<Foo>(100L, SnapshotVersion.CURRENT), intervalStart1, intervalEnd1);
        Assert.assertEquals(ids1.size(), 5);
    }


    public void testFindBubbleIdsForIntervalSomInneholderAkkuratAlle() {
        SnapshotVersion intervalEnd = SnapshotVersion.createInstance("2011-10-02 08:04:00.01");
        Collection<FooId<Foo>> ids1 = service.getVersions(new FooId<Foo>(100L, SnapshotVersion.CURRENT), intervalStart1, intervalEnd);
        Assert.assertEquals(ids1.size(), 5);
    }


    public void testFindBubbleIdsForIntervalSomIkkeInneholderFoerste() {
        Collection<FooId<Foo>> ids2 = service.getVersions(new FooId<Foo>(100L, SnapshotVersion.CURRENT), intervalStart2, intervalEnd2);
        Assert.assertEquals(ids2.size(), 4);

    }

    public void testFindBubbleIdsForIntervalSomBareInneholderFoerste() {
        Collection<FooId<Foo>> ids3 = service.getVersions(new FooId<Foo>(100L, SnapshotVersion.CURRENT), intervalStart1, intervalStart2);
        Assert.assertEquals(ids3.size(), 1);
        Assert.assertEquals(ids3.iterator().next().getSnapshotVersion(), intervalStart1);
    }

    public void testFindBubbleIdsForIntervalSomErTomt() {
        Collection<FooId<Foo>> ids4 = service.getVersions(new FooId<Foo>(100L, SnapshotVersion.CURRENT), intervalStart1, intervalStart1);
        Assert.assertEquals(ids4.size(), 0);
    }

    public void testFindBubbleIdsForIntervalSomInneholderAlleHeltTilCurrent_List() {

        List<FooId<Foo>> fooIds = Arrays.asList(new FooId<Foo>(100L), new FooId<Foo>(101L));
        Map<FooId<Foo>,List<FooId<Foo>>> versionsForList = service.getVersionsForList(fooIds, intervalStart1, intervalEnd1);
        Assert.assertEquals(versionsForList.size(), 2);
        Assert.assertNotNull(versionsForList.get(fooIds.get(0)), "Fant ikke versjoner hørende til" + fooIds.get(0));
        Assert.assertNotNull(versionsForList.get(fooIds.get(1)), "Fant ikke versjoner hørende til" + fooIds.get(1));
        Assert.assertEquals(versionsForList.get(fooIds.get(0)).size(), 5);
        Assert.assertEquals(versionsForList.get(fooIds.get(1)).size(), 2);
    }


    public void testGetVersionsViaStore() {
        Collection<FooId<Foo>> ids1 = store.getVersions(new FooId<Foo>(100L, SnapshotVersion.CURRENT), intervalStart1, intervalEnd1);
        Assert.assertEquals(ids1.size(), 5);
    }

    public void testGetVersionsViaStore_List() {

        List<FooId<Foo>> fooIds = Arrays.asList(new FooId<Foo>(100L), new FooId<Foo>(101L));
        Map<FooId<Foo>,List<FooId<Foo>>> versionsForList = store.getVersionsForList(fooIds, intervalStart1, intervalEnd1);
        Assert.assertEquals(versionsForList.size(), 2);
        Assert.assertNotNull(versionsForList.get(fooIds.get(0)), "Fant ikke versjoner hørende til" + fooIds.get(0));
        Assert.assertNotNull(versionsForList.get(fooIds.get(1)), "Fant ikke versjoner hørende til" + fooIds.get(1));
        Assert.assertEquals(versionsForList.get(fooIds.get(0)).size(), 5);
        Assert.assertEquals(versionsForList.get(fooIds.get(1)).size(), 2);
    }
}
