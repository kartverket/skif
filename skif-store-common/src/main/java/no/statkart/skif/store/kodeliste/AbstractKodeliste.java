package no.statkart.skif.store.kodeliste;

import no.statkart.skif.SkifUtil;
import no.statkart.skif.store.AbstractBubbleObject;
import no.statkart.skif.internal.util.InternalLocaleUtils;

import java.util.*;

/**
 * Abstrakt implementasjon av kodeliste. Klasse holder på en liste av {@code KodeId}s og implementere
 * lokaliseringsstøtte.
 * <p/>
 * Klassen er knyttet mot {@link AbstractKodelisteId} som bruker {@code Object} som idValue type. Det finnes
 * konkrete subtyper som bruker {@code Long} og {@code String} som idValue type.
 *
 * @author Henrik Fredholm
 * @since 2.1
 */
public abstract class AbstractKodeliste extends AbstractBubbleObject implements Kodeliste {
    private static final long serialVersionUID = 1L;

    private String kodeTypeNavn;
    private Class<? extends KodeId<?>> kodeIdClass;
    private LocalizedFields localizedFields = new LocalizedFields();
    private Map<String, LocalizedFields> localizedFieldsMap;
    private List<KodeId<?>> kodeIds = new ArrayList<KodeId<?>>();
    private boolean editerbar;

    // Avledet felt
    private Class<? extends Kode> kodeClass;


    public static class LocalizedFields implements no.statkart.skif.store.LocalizedFields {
        private static final long serialVersionUID = 1L;

        public String navn = "";
        public String beskrivelse = "";

        void updateFrom(LocalizedFields l) {
            navn = l.navn;
            beskrivelse = l.beskrivelse;
        }
    }

    public AbstractKodeliste() {
        this.localizedFieldsMap = new HashMap<String, LocalizedFields>(3);
    }


    public String getKodeTypeNavn() {
        return kodeTypeNavn;
    }

    public void setKodeTypeNavn(String kodeTypeNavn) {
        this.kodeTypeNavn = kodeTypeNavn;
    }

    public String getNavn() {
        return localizedFields.navn;
    }

    public void setNavn(String navn) {
        localizedFields.navn = navn;
    }

    @Override
    public AbstractKodelisteId<?> getId() {
        return (AbstractKodelisteId<?>) super.getId();
    }

    @Override
    public Class<? extends Kode> getKodeClass() {
        if (kodeClass==null) {
            String kodeIdClassName = kodeIdClass.getName();
            String kodeClassName = kodeIdClassName.substring(0, kodeIdClassName.length()-2);
            kodeClass = SkifUtil.classForName(kodeClassName);
        }
        return kodeClass;
    }

    @Override
    public Class<? extends KodeId<?>> getKodeIdClass() {
        return kodeIdClass;
    }

    @Override
    public void setKodeIdClass(Class<? extends KodeId<?>> kodeIdClass) {
        this.kodeIdClass = kodeIdClass;
    }

    @Override
    public List<KodeId<?>> getKodeIds() {
        return kodeIds;
    }

    @Override
    public void setKodeIds(List<? extends KodeId<?>> kodeIds) {
        this.kodeIds = (List) kodeIds;
    }

    public List<Kode> getKoder() {
        return store.get(kodeIds);
    }

    public String getBeskrivelse() {
        return localizedFields.beskrivelse;
    }

    public void setBeskrivelse(String beskrivelse) {
        localizedFields.beskrivelse = beskrivelse;
    }

    public Map<String, LocalizedFields> getLocalizedFieldsMap() {
        return localizedFieldsMap;
    }


    @Override
    public boolean isEditerbar() {
        return editerbar;
    }

    @Override
    public void setEditerbar(boolean editerbar) {
        this.editerbar = editerbar;
    }

    public void setLocalizedFieldsMap(Map<String, LocalizedFields> localizedFieldsMap) {
        this.localizedFieldsMap = localizedFieldsMap;
    }

    @Override
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

    @Override
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

