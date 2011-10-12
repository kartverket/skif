package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.store.*;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.Kode;
import no.statkart.skif.store.kodelistesupport.KodeId;
import no.statkart.skif.storetest.domain.*;
import no.statkart.skif.storetest.domain.Bar;
import no.statkart.skif.storetest.domain.BarFoos;
import no.statkart.skif.storetest.domain.BarFoosId;
import no.statkart.skif.storetest.domain.BarId;
import no.statkart.skif.storetest.domain.Foo;
import no.statkart.skif.storetest.domain.FooId;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.*;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeIdList;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteIdList;

import java.sql.Timestamp;
import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public interface StoreTestMapping extends Mapping {
    public StoreTestBubbleId<? extends StoreTestBubble> w2d(no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId source);
    public no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId d2w(StoreTestBubbleId<? extends StoreTestBubble> source);

    public StoreTestBubbleIdList d2w(Collection source, StoreTestBubbleIdList target);
    public <T extends Collection> T w2d(StoreTestBubbleIdList source, T target);

//    public no.statkart.skif.storetest.wsapi.domain.StoreTestBubble d2w(StoreTestBubble source);
//    public TestBubble w2d(no.statkart.skif.storetest.wsapi.domain.StoreTestBubble source);

    public no.statkart.skif.storetest.wsapi.domain.Timestamp d2w(Timestamp source);
    public Timestamp w2d(no.statkart.skif.storetest.wsapi.domain.Timestamp source);

    public no.statkart.skif.storetest.wsapi.domain.SnapshotVersion d2w(SnapshotVersion source);
    public SnapshotVersion w2d(no.statkart.skif.storetest.wsapi.domain.SnapshotVersion source);

    public StoreTestBubbleList d2w(Collection source, StoreTestBubbleList target);
    public <T extends Collection> T w2d(StoreTestBubbleList source, T target);

    public AList d2w(Collection source, AList target);
    public <T extends Collection> T w2d(AList source, T target);

    public no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeId d2w(KodeId<?> source);
    public KodeId<?> w2d(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeId source);

    public KodeIdList d2w(Collection source, KodeIdList target);
    public <T extends Collection> T w2d(KodeIdList source, T target);

    public KodeIdList d2w(Collection source, KodelisteIdList target);
    public <T extends Collection> T w2d(KodelisteIdList source, T target);

    public no.statkart.skif.storetest.wsapi.domain.kodeliste.Kode d2w(Kode source);
    public Kode w2d(no.statkart.skif.storetest.wsapi.domain.kodeliste.Kode source);

    public BarFoosId<BarFoos> w2d(no.statkart.skif.storetest.wsapi.domain.BarFoosId source);
    public no.statkart.skif.storetest.wsapi.domain.BarFoosId d2w(BarFoosId<BarFoos> source);

    public BarFoosIdList d2w(Collection source, BarFoosIdList target);
    public <T extends Collection> T w2d(BarFoosIdList source, T target);

    public BarId<Bar> w2d(no.statkart.skif.storetest.wsapi.domain.BarId source);
    public no.statkart.skif.storetest.wsapi.domain.BarId d2w(BarId<Bar> source);

    public BarIdList d2w(Collection source, BarIdList target);
    public <T extends Collection> T w2d(BarIdList source, T target);

    public FooId<Foo> w2d(no.statkart.skif.storetest.wsapi.domain.FooId source);
    public no.statkart.skif.storetest.wsapi.domain.FooId d2w(FooId<Foo> source);

    public FooIdList d2w(Collection source, FooIdList target);
    public <T extends Collection> T w2d(FooIdList source, T target);

}