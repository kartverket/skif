package no.statkart.skif.storetest.wsapi.mapping2;

import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.store2.kodelistesupport2.Kode2;
import no.statkart.skif.store2.kodelistesupport2.KodeId2;
import no.statkart.skif.storetest.domain2.StoreTestBubble2;
import no.statkart.skif.storetest.domain2.StoreTestBubbleId2;
import no.statkart.skif.storetest.wsapi.domain2.A2List;
import no.statkart.skif.storetest.wsapi.domain2.StoreTestBubble2List;
import no.statkart.skif.storetest.wsapi.domain2.StoreTestBubbleId2List;
import no.statkart.skif.storetest.wsapi.domain2.kodeliste.KodeId2List;
import no.statkart.skif.storetest.wsapi.domain2.kodeliste.KodelisteId2List;

import java.util.Collection;

/**
 * @author Henrik Fredholm
 * @since 0.3
 */
public interface StoreTestMapping2 extends Mapping {
    public StoreTestBubbleId2<? extends StoreTestBubble2> w2d(no.statkart.skif.storetest.wsapi.domain2.StoreTestBubbleId2 source);
    public no.statkart.skif.storetest.wsapi.domain2.StoreTestBubbleId2 d2w(StoreTestBubbleId2<? extends StoreTestBubble2> source);

    public StoreTestBubbleId2List d2w(Collection source, StoreTestBubbleId2List target);
    public <T extends Collection> T w2d(StoreTestBubbleId2List source, T target);

//    public no.statkart.skif.storetest.wsapi.domain2.StoreTestBubble2 d2w(StoreTestBubble2 source);
//    public TestBubble w2d(no.statkart.skif.storetest.wsapi.domain2.StoreTestBubble2 source);

    public StoreTestBubble2List d2w(Collection source, StoreTestBubble2List target);
    public <T extends Collection> T w2d(StoreTestBubble2List source, T target);

    public A2List d2w(Collection source, A2List target);
    public <T extends Collection> T w2d(A2List source, T target);

    public no.statkart.skif.storetest.wsapi.domain2.kodeliste.KodeId2 d2w(KodeId2<?> source);
    public KodeId2<?> w2d(no.statkart.skif.storetest.wsapi.domain2.kodeliste.KodeId2 source);

    public KodeId2List d2w(Collection source, KodeId2List target);
    public <T extends Collection> T w2d(KodeId2List source, T target);

    public KodeId2List d2w(Collection source, KodelisteId2List target);
    public <T extends Collection> T w2d(KodelisteId2List source, T target);

    public no.statkart.skif.storetest.wsapi.domain2.kodeliste.Kode2 d2w(Kode2 source);
    public Kode2 w2d(no.statkart.skif.storetest.wsapi.domain2.kodeliste.Kode2 source);
}