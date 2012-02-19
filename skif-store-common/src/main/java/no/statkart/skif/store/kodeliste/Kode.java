package no.statkart.skif.store.kodeliste;

import no.statkart.skif.store.AbstractBubbleObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Superklasse for Koder.
 *
 * TODO: Fjerne kodeverdi slik at denne ikke er påkrevet.
 * TODO: Generalisere LocalizedFields slik at det er mulig å angi flere felter i subklasser
 * TODO: Lage eget interface for localize metoden
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class Kode extends AbstractBubbleObject {
    private String kodeverdi;

    private LocalizedFields localizedFields = new LocalizedFields();
    private Map<String, LocalizedFields> localizedFieldsMap = new HashMap<String, LocalizedFields>();
    private KodelisteId<?> kodelisteId;

    public static class LocalizedFields implements Serializable {
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

    public KodelisteId<?> getKodelisteId() {
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
        if (localeString != null) {
            localizedFields = localizedFieldsMap.get(localeString);
            if (localizedFields == null) {
                localizedFields = new LocalizedFields();
            }
        } else {
            localizedFields = new LocalizedFields();
        }
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
