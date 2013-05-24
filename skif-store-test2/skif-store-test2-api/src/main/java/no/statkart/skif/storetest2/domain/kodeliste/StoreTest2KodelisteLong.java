package no.statkart.skif.storetest2.domain.kodeliste;

import no.statkart.skif.store.kodeliste.KodelisteLong;

/**
 * Implemenmtasjonsklasse for Kodelister i StoreTest applikasjoen som bruker en Long som idValue.
 *
 * @author Henrik Fredholm
 * @since 2.2.1
 */
public class StoreTest2KodelisteLong extends KodelisteLong implements StoreTest2Kodeliste {
    @Override
    public StoreTest2KodelisteLongId<?> getId() {
        return (StoreTest2KodelisteLongId<?>) super.getId();
    }

}
