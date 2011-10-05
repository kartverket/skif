package no.statkart.skif.storetest.service.store;

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

    public void testFindBubbleIdsForInterval() {
        HistorikkService service = injector.getInstance(HistorikkService.class);

        Collection<FooId<Foo>> ids = service.getVersions(new FooId<Foo>(100L, SnapshotVersion.CURRENT), SnapshotVersion.createInstance("2011-10-02 08:01:01.00"), SnapshotVersion.CURRENT);
        Assert.assertEquals(ids.size(), 4);

        Timestamp startTime = Timestamp.valueOf("2011-10-02 08:01:01.00");
        for (FooId<Foo> id : ids) {
            if (!id.getSnapshotVersion().equals(SnapshotVersion.CURRENT)) {
                Timestamp timestamp = Timestamp.valueOf(id.getSnapshotVersion().getTimestamp());
                Assert.assertTrue(startTime.before(timestamp));
            }
        }
    }

    /**
     * Tester at nyeste element får sin verdi på snapshot erstattet med det brukeren ba om. Dette for å sørge for at brukeren
     * får riktig versjon ut ved senere kall dersom id-en har fått ny versjon i etterkant.
     */
    public void testFindBubbleIdsForInterval_Latest() {
        HistorikkService service = injector.getInstance(HistorikkService.class);

        SnapshotVersion endSnapshotVersion = SnapshotVersion.createInstance("2011-10-02 10:05:01.00");
        Collection<FooId<Foo>> ids = service.getVersions(new FooId<Foo>(100L, SnapshotVersion.CURRENT), SnapshotVersion.createInstance("2011-10-02 08:05:01.00"), endSnapshotVersion);
        Assert.assertEquals(ids.size(), 1);

        Assert.assertEquals(ids.iterator().next().getSnapshotVersion(), endSnapshotVersion);

    }

}
