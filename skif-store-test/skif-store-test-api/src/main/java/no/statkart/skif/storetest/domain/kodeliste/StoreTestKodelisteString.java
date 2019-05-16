package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.KodelisteString;
import no.statkart.skif.store.localization.LocalizationMap;
import no.statkart.skif.store.localization.Localized;
import no.statkart.skif.store.localization.LocalizedString;

import java.util.Map;

/**
 * Implementasjonsklasse for Kodelister i StoreTest applikasjon som bruker en String som idValue.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public class StoreTestKodelisteString extends KodelisteString implements StoreTestKodeliste, Localized {
    private static final long serialVersionUID = 1L;

    private final LocalizationMap localizationMap = new LocalizationMap(this);

    @Override
    public StoreTestKodelisteStringId<?> getId() {
        return (StoreTestKodelisteStringId<?>) super.getId();
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
