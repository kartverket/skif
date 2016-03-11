package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.store.kodeliste.KodelisteId;
import no.statkart.skif.storetest.domain.StoreTestBubble;
import no.statkart.skif.storetest.domain.StoreTestBubbleId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodeId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteLongId;
import no.statkart.skif.storetest.domain.kodeliste.StoreTestKodelisteStringId;

import java.sql.Timestamp;

/**
 * @author Henrik Fredholm
 * @since 2.0
 */
public interface StoreTestMapping extends Mapping {

    StoreTestBubbleId<? extends StoreTestBubble> w2d(no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId source);
    no.statkart.skif.storetest.wsapi.domain.StoreTestBubbleId d2w(StoreTestBubbleId<?> source);

    no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId d2w(KodelisteId<?> source);
    KodelisteId<?> w2d(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteId source);

    no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId d2w(StoreTestKodelisteLongId<?> source);
    StoreTestKodelisteLongId<?> w2d(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteLongId source);

    no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteStringId d2w(StoreTestKodelisteStringId<?> source);
    StoreTestKodelisteStringId<?> w2d(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodelisteStringId source);

    no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeId d2w(StoreTestKodeId<?> source);
    StoreTestKodeId<?> w2d(no.statkart.skif.storetest.wsapi.domain.kodeliste.KodeId source);

    no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp d2w(Timestamp timestamp);
    Timestamp w2d(no.statkart.skif.storetest.wsapi.domain.basetyper.Timestamp timestamp);

}