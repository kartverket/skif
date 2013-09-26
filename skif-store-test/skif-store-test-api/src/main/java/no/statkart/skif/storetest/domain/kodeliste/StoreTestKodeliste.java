package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.*;
import no.statkart.skif.store.localization.LocalizedString;
import no.statkart.skif.storetest.domain.StoreTestBubble;

import java.util.List;

/**
 * Alle Kodelister i StoreTest applikasjonen implementerer dette interface. Det er nødvendig å implementere dette
 * interfacet slik at StoreTest kodelister både blir {@code StoreTestBuble} og {@code Kodeliste}
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public interface StoreTestKodeliste extends StoreTestBubble, Kodeliste {
    // Java tillater ikke overskrivning av return type som kan føre til diamanthieraki
    //@Override
    //StoreTestKodelisteId<?> getId();

    public abstract LocalizedString getNavn();
    public abstract void setNavn(LocalizedString navn);
    public abstract LocalizedString getBeskrivelse();
    public abstract void setBeskrivelse(LocalizedString beskrivelse);
}
