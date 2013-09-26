package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.KodelisteLong;
import no.statkart.skif.store.localization.LocalizationMap;
import no.statkart.skif.store.localization.Localized;
import no.statkart.skif.store.localization.LocalizedString;

import java.util.Map;

/**
 * Implemenmtasjonsklasse for Kodelister i StoreTest applikasjoen som bruker en Long som idValue.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreTestKodelisteLong extends KodelisteLong implements StoreTestKodeliste, Localized {
    private static final long serialVersionUID = 1L;

    private final LocalizationMap localizationMap = new LocalizationMap(this);

    @Override
    public StoreTestKodelisteLongId<?> getId() {
        return (StoreTestKodelisteLongId<?>) super.getId();
    }

    public LocalizedString getNavn() {
        return localizationMap.localizedStringForField("navn");
    }

    public void setNavn(LocalizedString navn) {
        localizationMap.updateLocalizations("navn", navn);
    }

    public LocalizedString getBeskrivelse() {
        return localizationMap.localizedStringForField("beskrivelse");
    }

    public void setBeskrivelse(LocalizedString beskrivelse) {
        localizationMap.updateLocalizations("beskrivelse", beskrivelse);
    }

    @Override
    public Map<LocalizationMap.LocalizationKey, String> getLocalizationMap() {
        return localizationMap.getMap();
    }

    @Override
    public void setLocalizationMap(Map<LocalizationMap.LocalizationKey, String> map) {
        localizationMap.setMap(map);
    }
}
