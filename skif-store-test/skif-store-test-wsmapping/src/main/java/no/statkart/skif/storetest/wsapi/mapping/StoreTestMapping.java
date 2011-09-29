package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.store.kodelistesupport.Kode;
import no.statkart.skif.store.kodelistesupport.KodeId;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.wsapi.domain.AList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleList;
import no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleIdList;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeIdList;
import no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteIdList;

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
}