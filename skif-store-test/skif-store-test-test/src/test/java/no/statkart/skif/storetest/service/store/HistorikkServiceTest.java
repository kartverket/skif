package no.statkart.skif.storetest.service.store;

import com.google.inject.Inject;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.storetest.domain.Foo;
import no.statkart.skif.storetest.domain.FooId;
import no.statkart.skif.storetest.util.testsupport.StoreTestTestCase;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.Collection;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
@Test
public class HistorikkServiceTest extends StoreTestTestCase {
    @Inject
    HistorikkService service;

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
}
