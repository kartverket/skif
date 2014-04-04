package no.statkart.skif.store.localization;

import com.google.inject.TypeLiteral;
import no.statkart.skif.service.ServiceContext;

import java.io.Serializable;
import java.util.*;

/**
 * En streng som kan være lokalisert på et vilkårlig antall språk.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class LocalizedString implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final TypeLiteral<Map<Locale, String>> MAP_TYPE = new TypeLiteral<Map<Locale, String>>() {};

    private final Map<Locale, String> localizations;

    public LocalizedString() {
        localizations = new HashMap<Locale, String>();
    }

    public LocalizedString(Map<Locale, String> localizations) {
        this.localizations = localizations;
    }

    /**
     * Finner faktisk locale for teksten for denne locale. Dette vil si en locale som har verdi, og som er nærmeste
     * fallback locale for gitt locale.
     *
     * @param locale locale søket skal starte med
     * @return locale som faktisk har definert verdi for dette feltet (kan være <code>null</code> dersom ingen verdier er definert)
     */
    // LocaleFallbackTest tester denne funksjonaliteten direkte, altså ikke via denne metoden
    public Locale getLocale(Locale locale) {
        ResourceBundle.Control control = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES);
        while (true) {
            boolean funnet = localizations.containsKey(locale);

            if (funnet || locale == null) {
                break;
            }

            locale = control.getFallbackLocale("", locale); // Første parameter kan ikke være null, men det ser ikke ut til at den brukes til noe
        }

        return locale != null ? locale : Locale.ROOT;
    }

    /**
     * Henter ut lokalisert tekst for gitt locale. Dersom det ikke finnes noen tekst for gitt locale, vil metoden søke
     * opp fallback locale rekursivt.
     *
     * @param locale locale det skal hentes tekst for
     * @return lokalisert tekst
     */
    public String getText(Locale locale) {
        return localizations.get(getLocale(locale));
    }

    public String setText(Locale locale, String text) {
        return localizations.put(locale, text);
    }

    public String removeText(Locale locale) {
        return localizations.remove(locale);
    }

    public void clear() {
        localizations.clear();
    }

    public Map<Locale, String> getAllTexts() {
        return Collections.unmodifiableMap(localizations);
    }
}
