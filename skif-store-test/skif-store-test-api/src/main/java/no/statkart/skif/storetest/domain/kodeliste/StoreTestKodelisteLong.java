package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.storetest.domain.StoreTestBubble;

import java.util.List;
import java.util.Map;

/**
 * Implemenmtasjonsklasse for Kodelister i StoreTest applikasjoen som bruker en Long som idValue.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreTestKodelisteLong extends KodelisteLong implements StoreTestKodeliste {
    @Override
    public StoreTestKodelisteLongId<?> getId() {
        return (StoreTestKodelisteLongId<?>) super.getId();
    }

}
