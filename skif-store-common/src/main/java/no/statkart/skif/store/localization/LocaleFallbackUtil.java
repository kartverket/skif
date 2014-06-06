package no.statkart.skif.store.localization;

import com.google.common.collect.ImmutableList;

import java.util.List;
import java.util.Locale;

/**
 * Logikk for hvordan Locale skal falle tilbake på mer generelle.
 *
 * @author Tor Egil R. Strand
 * @since 2.5.0
 */
public class LocaleFallbackUtil {
    private static final Locale bokmaal1 = new Locale("nb", "NO");
    private static final Locale bokmaal2 = new Locale("no", "NO", "B");
    private static final Locale nynorsk1 = new Locale("ny", "NO");
    private static final Locale nynorsk2 = new Locale("no", "NO", "NY");
    private static final Locale norsk = new Locale("no", "NO");

    private static final List<Locale> norskeUnderarter = ImmutableList.of(
            bokmaal1, bokmaal2, nynorsk1, nynorsk2
    );

    public static Locale getFallbackLocale(Locale locale) {
        if (locale == null || locale.equals(Locale.ROOT)) {
            return null;
        } else if (norskeUnderarter.contains(locale)) {
            return norsk;
        } else {
            return Locale.ROOT;
        }
    }
}
