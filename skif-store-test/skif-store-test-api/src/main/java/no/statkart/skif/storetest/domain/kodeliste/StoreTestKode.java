package no.statkart.skif.storetest.domain.kodeliste;

import no.statkart.skif.internal.util.InternalLocaleUtils;
import no.statkart.skif.store.Localizable;
import no.statkart.skif.store.kodeliste.Kode;
import no.statkart.skif.storetest.domain.StoreTestBubble;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * @author Henrik Fredholm
 */
public abstract class StoreTestKode extends Kode implements StoreTestBubble, Localizable {
    private String kodeverdi;

    private LocalizedFields localizedFields = new LocalizedFields();
    private Map<String, LocalizedFields> localizedFieldsMap = new HashMap<String, LocalizedFields>();

    public StoreTestKodeId<?> getId() {
        return (StoreTestKodeId<?>) super.getId();
    }

    public String getKodeverdi() {
        return kodeverdi;
    }

    public void setKodeverdi(String kodeverdi) {
        this.kodeverdi = kodeverdi;
    }

    public String getNavn() {
        return localizedFields.navn;
    }

    public void setNavn(String navn) {
        localizedFields.navn = navn;
    }

    public String getBeskrivelse() {
        return localizedFields.beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        this.localizedFields.beskrivelse = beskrivelse;
    }

    public Map<String, LocalizedFields> getLocalizedFieldsMap() {
        return localizedFieldsMap;
    }

    public void setLocalizedFieldsMap(Map<String, LocalizedFields> localizedFieldsMap) {
        this.localizedFieldsMap = localizedFieldsMap;
    }

    // TODO: Denne bør ligge i eget interface
    public void localize(String localeString) {
        localizedFields = null;
        ResourceBundle.Control control = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES);
        Locale locale = InternalLocaleUtils.toLocale(localeString);
        LocalizedFields fields;
        while (true) {
            fields = localizedFieldsMap.get(locale != null ? locale.toString() : "");

            if (fields != null || locale == null) {
                break;
            }

            locale = control.getFallbackLocale("", locale); // Første parameter kan ikke være null, men det ser ikke ut til at den brukes til noe
        }
        localizedFields = fields != null ? fields : new LocalizedFields();
    }

    public void updateLocalized(String localeString) {
        LocalizedFields l = localizedFieldsMap.get(localeString);
        if (l == null) {
            l = new LocalizedFields();
            localizedFieldsMap.put(localeString, l);
        }
        if (l != localizedFields) {
            l.updateFrom(localizedFields);
        }
    }

    public static class LocalizedFields implements no.statkart.skif.store.LocalizedFields {
        private static final long serialVersionUID = 1L;

        public String navn = "";
        public String beskrivelse = "";

        void updateFrom(LocalizedFields l) {
            navn = l.navn;
            beskrivelse = l.beskrivelse;
        }
    }
}
