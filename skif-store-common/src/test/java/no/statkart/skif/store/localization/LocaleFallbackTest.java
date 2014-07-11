package no.statkart.skif.store.localization;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Locale;

/**
 * Tester at fallback locale virker.
 *
 * @since 2.4.0
 */
@Test
public class LocaleFallbackTest {
    public void fallbackNynorsk1() {
        Locale locale = new Locale("nn", "NO");
        Assert.assertEquals(locale.toString(), "nn_NO", "locale");

        Locale fallback1 = LocaleFallbackUtil.getFallbackLocale(locale);
        Assert.assertNotNull(fallback1, "fallback 1");
        Assert.assertEquals(fallback1.toString(), "no_NO", "fallback 1");

        Locale fallback2 = LocaleFallbackUtil.getFallbackLocale(fallback1);
        Assert.assertNotNull(fallback2, "fallback 2");
        Assert.assertEquals(fallback2.toString(), "", "fallback 2");
    }

    public void fallbackNynorsk2() {
        Locale locale = new Locale("no", "NO", "NY");
        Assert.assertEquals(locale.toString(), "no_NO_NY", "locale");

        Locale fallback1 = LocaleFallbackUtil.getFallbackLocale(locale);
        Assert.assertNotNull(fallback1, "fallback 1");
        Assert.assertEquals(fallback1.toString(), "no_NO", "fallback 1");

        Locale fallback2 = LocaleFallbackUtil.getFallbackLocale(fallback1);
        Assert.assertNotNull(fallback2, "fallback 2");
        Assert.assertEquals(fallback2.toString(), "", "fallback 2");
    }

    public void fallbackBokmaal1() {
        Locale locale = new Locale("nb", "NO");
        Assert.assertEquals(locale.toString(), "nb_NO", "locale");

        Locale fallback1 = LocaleFallbackUtil.getFallbackLocale(locale);
        Assert.assertNotNull(fallback1, "fallback 1");
        Assert.assertEquals(fallback1.toString(), "no_NO", "fallback 1");

        Locale fallback2 = LocaleFallbackUtil.getFallbackLocale(fallback1);
        Assert.assertNotNull(fallback2, "fallback 2");
        Assert.assertEquals(fallback2.toString(), "", "fallback 2");
    }

    public void fallbackBokmaal2() {
        Locale locale = new Locale("no", "NO", "B");
        Assert.assertEquals(locale.toString(), "no_NO_B", "locale");

        Locale fallback1 = LocaleFallbackUtil.getFallbackLocale(locale);
        Assert.assertNotNull(fallback1, "fallback 1");
        Assert.assertEquals(fallback1.toString(), "no_NO", "fallback 1");

        Locale fallback2 = LocaleFallbackUtil.getFallbackLocale(fallback1);
        Assert.assertNotNull(fallback2, "fallback 2");
        Assert.assertEquals(fallback2.toString(), "", "fallback 2");
    }
}
