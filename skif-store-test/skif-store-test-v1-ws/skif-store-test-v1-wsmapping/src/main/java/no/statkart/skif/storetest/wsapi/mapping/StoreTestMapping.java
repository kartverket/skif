package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteStringId;
import no.statkart.skif.storetest.domain.mockup.Bar;
import no.statkart.skif.storetest.domain.mockup.BarId;
import no.statkart.skif.storetest.domain.mockup.Foo;
import no.statkart.skif.storetest.domain.mockup.FooId;

import java.sql.Timestamp;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreTestMapping extends Mapping {
    public StoreTestBubbleId<? extends StoreTestBubble> w2d(no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId source);
    public no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId d2w(StoreTestBubbleId<?> source);

    public no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp d2w(Timestamp source);
    public Timestamp w2d(no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp source);

    public no.statkart.skif.storetest.wsapi.domain.SnapshotVersion d2w(SnapshotVersion source);
    public SnapshotVersion w2d(no.statkart.skif.storetest.wsapi.domain.SnapshotVersion source);

    public no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId d2w(KodelisteId<?> source);
    public KodelisteId<?> w2d(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId source);

    public no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId d2w(StoreTestKodelisteLongId<?> source);
    public StoreTestKodelisteLongId<?> w2d(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId source);

    public no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteStringId d2w(StoreTestKodelisteStringId<?> source);
    public StoreTestKodelisteStringId<?> w2d(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteStringId source);

    public no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeId d2w(StoreTestKodeId<?> source);
    public StoreTestKodeId<?> w2d(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeId source);
}