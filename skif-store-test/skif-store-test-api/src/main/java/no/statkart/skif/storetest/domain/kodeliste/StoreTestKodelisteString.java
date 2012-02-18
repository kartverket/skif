package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.KodelisteLong;
import no.statkart.skif.store.kodeliste.KodelisteString;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * Implemenmtasjonsklasse for Kodelister i StoreTest applikasjoen som bruker en String som idValue.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreTestKodelisteString extends KodelisteString implements StoreTestKodeliste {
    @Override
    public StoreTestKodelisteStringId<?> getId() {
        return (StoreTestKodelisteStringId<?>) super.getId();
    }
}
