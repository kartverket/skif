package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodelistesupport.DbKodelisteImpl;
import no.statkart.skif.store.kodelistesupport.DbKodelisteImplId;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class StoreTestDbKodelisteLong extends DbKodelisteImpl implements StoreTestKodelisteLong {
    @Override
    public StoreTestDbKodelisteLongId<?> getId() {
        return (StoreTestDbKodelisteLongId<?>) super.getId();
    }
}
