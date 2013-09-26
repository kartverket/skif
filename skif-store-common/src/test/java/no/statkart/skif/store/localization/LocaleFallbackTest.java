package no.statkart.skif.store.localization;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Tester at fallback locale virker.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@Test
public class LocaleFallbackTest {
    public void fallbackNorsk() {
        ResourceBundle.Control control = ResourceBundle.Control.getControl(ResourceBundle.Control.FORMAT_PROPERTIES);

        Locale locale = new Locale("no", "NO", "ny");
        Assert.assertEquals(locale.toString(), "no_NO_ny", "locale");

        Locale fallback1 = control.getFallbackLocale("", locale);
        Assert.assertNotNull(fallback1, "fallback 1");
        Assert.assertEquals(fallback1.toString(), "no_NO", "fallback 1");

        Locale fallback2 = control.getFallbackLocale("", fallback1);
        Assert.assertNull(fallback2, "fallback 2");
    }
}
