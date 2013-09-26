package no.statkart.skif.store.localization;

import no.statkart.skif.store.BubbleObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

/**
 * Logikk for at mange felter kan ha mange oversettelser, samlet i én tabell.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class LocalizationMap implements Serializable {
    private static final long serialVersionUID = 1L;

    private final BubbleObject owner;

    /**
     * Map fra locale+feltnavn til tekst. Det er dette Hibernate jobber med.
     */
    private Map<LocalizationKey, String> map = new HashMap<LocalizationKey, String>();

    public LocalizationMap(BubbleObject owner) {
        this.owner = owner;
    }

    /**
     * Henter ut det interne mappet. Brukes for Hibernate og {@link LocalizedString}.
     *
     * @return internt map, ikke en kopi
     */
    public Map<LocalizationKey, String> getMap() {
        return map;
    }

    /**
     * Erstatter det interne mappet med et annet. Brukes for Hibernate.
     *
     * @param map nytt internt map
     */
    public void setMap(Map<LocalizationKey, String> map) {
        this.map = map;
    }

    public LocalizedString localizedStringForField(String fieldName) {
        Map<Locale, String> localizations = new HashMap<Locale, String>();

        for (Map.Entry<LocalizationKey, String> entry : map.entrySet()) {
            if (entry.getKey().getName().equals(fieldName)) {
                localizations.put(entry.getKey().getLocale(), entry.getValue());
            }
        }

        return new LocalizedString(localizations);
    }

    public void updateLocalizations(String fieldName, LocalizedString localizedString) {
        for (Iterator<LocalizationKey> iterator = map.keySet().iterator(); iterator.hasNext(); ) {
            LocalizationKey next = iterator.next();
            if (next.getName().equals(fieldName)) {
                iterator.remove();
            }
        }

        for (Map.Entry<Locale, String> entry : localizedString.getAllTexts().entrySet()) {
            map.put(new LocalizationKey(fieldName, entry.getKey()), entry.getValue());
        }
    }

    public BubbleObject getOwner() {
        return owner;
    }

    public static class LocalizationKey implements Serializable {
        private static final long serialVersionUID = 1L;

        /**
         * Det lokaliserte feltets navn.
         */
        private String name = "";

        /**
         * Locale.
         */
        private Locale locale = null;

        public LocalizationKey() {
        }

        public LocalizationKey(String name, Locale locale) {
            this.name = name;
            this.locale = locale;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Locale getLocale() {
            return locale;
        }

        public void setLocale(Locale locale) {
            this.locale = locale;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            LocalizationKey that = (LocalizationKey) o;

            if (locale != null ? !locale.equals(that.locale) : that.locale != null) return false;
            if (!name.equals(that.name)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + (locale != null ? locale.hashCode() : 0);
            return result;
        }
    }
}
