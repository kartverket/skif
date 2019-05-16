package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.Kodeliste;
import no.statkart.skif.store.localization.LocalizedString;
import no.statkart.skif.storetest.domain.StoreTestBubble;

/**
 * Alle Kodelister i StoreTest applikasjonen implementerer dette interface. Det er nødvendig å implementere dette
 * interfacet slik at StoreTest kodelister både blir {@code StoreTestBubble} og {@code Kodeliste}
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface StoreTestKodeliste extends StoreTestBubble, Kodeliste {
    // Java tillater ikke overskrivning av return type som kan føre til diamanthieraki
    //@Override
    //StoreTestKodelisteId<?> getId();

    LocalizedString getNavn();

    void setNavn(LocalizedString navn);

    LocalizedString getBeskrivelse();

    void setBeskrivelse(LocalizedString beskrivelse);

}
