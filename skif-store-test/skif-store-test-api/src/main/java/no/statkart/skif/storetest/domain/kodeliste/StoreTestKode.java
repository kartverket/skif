package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.store.localization.LocalizationMap;
import no.statkart.skif.store.localization.Localized;
import no.statkart.skif.store.localization.LocalizedString;
import no.statkart.skif.storetest.domain.StoreTestBubble;

import java.util.Map;

/**
 * @author Henrik Fredholm
 */
public abstract class StoreTestKode extends Kode implements StoreTestBubble, Localized {
    private static final long serialVersionUID = 1L;

    private String kodeverdi;

    private final LocalizationMap localizationMap = new LocalizationMap(this);

    public StoreTestKodeId<?> getId() {
        return (StoreTestKodeId<?>) super.getId();
    }

    public String getKodeverdi() {
        return kodeverdi;
    }

    public void setKodeverdi(String kodeverdi) {
        this.kodeverdi = kodeverdi;
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
