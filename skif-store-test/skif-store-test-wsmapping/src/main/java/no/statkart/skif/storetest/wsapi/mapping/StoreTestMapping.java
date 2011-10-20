package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodelistesupport.Kode;
import no.statkart.skif.store.kodelistesupport.KodeId;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.demo.*;
import no.statkart.skif.storetest.wsapi.domain.kode.KodeIdList;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteIdList;

import java.sql.Timestamp;
import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public interface StoreTestMapping extends Mapping {
    public StoreTestBubbleId<? extends StoreTestBubble> w2d(no.statkart.skif.storetest.wsapi.domain.demo.StoreTestBubbleId source);
    public no.statkart.skif.storetest.wsapi.domain.demo.StoreTestBubbleId d2w(StoreTestBubbleId<? extends StoreTestBubble> source);

    public StoreTestBubbleIdList d2w(Collection source, StoreTestBubbleIdList target);
    public <T extends Collection> T w2d(StoreTestBubbleIdList source, T target);

//    public no.statkart.skif.storetest.wsapi.domain.demo.StoreTestBubble d2w(StoreTestBubble source);
//    public TestBubble w2d(no.statkart.skif.storetest.wsapi.domain.demo.StoreTestBubble source);

    public no.statkart.skif.storetest.wsapi.domain.demo.Timestamp d2w(Timestamp source);
    public Timestamp w2d(no.statkart.skif.storetest.wsapi.domain.demo.Timestamp source);

    public no.statkart.skif.storetest.wsapi.domain.demo.SnapshotVersion d2w(SnapshotVersion source);
    public SnapshotVersion w2d(no.statkart.skif.storetest.wsapi.domain.demo.SnapshotVersion source);

    public StoreTestBubbleList d2w(Collection source, StoreTestBubbleList target);
    public <T extends Collection> T w2d(StoreTestBubbleList source, T target);

    public no.statkart.skif.storetest.wsapi.domain.kode.KodeId d2w(KodeId<?> source);
    public KodeId<?> w2d(no.statkart.skif.storetest.wsapi.domain.kode.KodeId source);

    public KodeIdList d2w(Collection source, KodeIdList target);
    public <T extends Collection> T w2d(KodeIdList source, T target);

    public KodeIdList d2w(Collection source, KodelisteIdList target);
    public <T extends Collection> T w2d(KodelisteIdList source, T target);

    public no.statkart.skif.storetest.wsapi.domain.kode.Kode d2w(Kode source);
    public Kode w2d(no.statkart.skif.storetest.wsapi.domain.kode.Kode source);

    public no.statkart.skif.storetest.domain.demo.BarFoosId<no.statkart.skif.storetest.domain.demo.BarFoos> w2d(no.statkart.skif.storetest.wsapi.domain.demo.BarFoosId source);
    public no.statkart.skif.storetest.wsapi.domain.demo.BarFoosId d2w(no.statkart.skif.storetest.domain.demo.BarFoosId<no.statkart.skif.storetest.domain.demo.BarFoos> source);

    public BarFoosIdList d2w(Collection source, BarFoosIdList target);
    public <T extends Collection> T w2d(BarFoosIdList source, T target);

    public no.statkart.skif.storetest.domain.demo.BarId<no.statkart.skif.storetest.domain.demo.Bar> w2d(no.statkart.skif.storetest.wsapi.domain.demo.BarId source);
    public no.statkart.skif.storetest.wsapi.domain.demo.BarId d2w(no.statkart.skif.storetest.domain.demo.BarId<no.statkart.skif.storetest.domain.demo.Bar> source);

    public BarIdList d2w(Collection source, BarIdList target);
    public <T extends Collection> T w2d(BarIdList source, T target);

    public no.statkart.skif.storetest.domain.demo.FooId<no.statkart.skif.storetest.domain.demo.Foo> w2d(no.statkart.skif.storetest.wsapi.domain.demo.FooId source);
    public no.statkart.skif.storetest.wsapi.domain.demo.FooId d2w(no.statkart.skif.storetest.domain.demo.FooId<no.statkart.skif.storetest.domain.demo.Foo> source);

    public FooIdList d2w(Collection source, FooIdList target);
    public <T extends Collection> T w2d(FooIdList source, T target);

}