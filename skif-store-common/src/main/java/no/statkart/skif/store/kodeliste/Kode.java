package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Localizable;
import no.statkart.skif.store.LocalizedFields;
import org.apache.commons.lang3.LocaleUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Superklasse for Koder.
 *
 * TODO: Generalisere LocalizedFields slik at det er mulig å angi flere felter i subklasser
 * TODO: Lage eget interface for localize metoden
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class Kode extends AbstractBubbleObject implements Localizable {
    private static final long serialVersionUID = 1L;

    private LocalizedFields localizedFields = new LocalizedFields();
    private Map<String, LocalizedFields> localizedFieldsMap = new HashMap<String, LocalizedFields>();
    private KodelisteId<?> kodelisteId;

    public static class LocalizedFields implements no.statkart.skif.store.LocalizedFields {
        private static final long serialVersionUID = 1L;

        public String navn = "";
        public String beskrivelse = "";

        void updateFrom(LocalizedFields l) {
            navn = l.navn;
            beskrivelse = l.beskrivelse;
        }
    }

    @Override
    public KodeId<?> getId() {
        return (KodeId<?>) super.getId();
    }

    @Override
    public void setId(BubbleId<?> id) {
        super.setId(id);
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

    public KodelisteId<?> getKodelisteId() {
        if (kodelisteId==null) {
            KodelisteId kodelisteId = KodeId.class.cast(id).getKodelisteId();
            setKodelisteId(kodelisteId.asSnapshotVersion(id));
        }
        return kodelisteId;
    }

    public void setKodelisteId(KodelisteId<?> kodelisteId) {
        this.kodelisteId = kodelisteId;
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
        Locale locale = LocaleUtils.toLocale(localeString);
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
}
