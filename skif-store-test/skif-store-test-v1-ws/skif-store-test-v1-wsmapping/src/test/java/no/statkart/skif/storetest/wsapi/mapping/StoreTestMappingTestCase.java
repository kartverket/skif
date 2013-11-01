package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.store.localization.LocalizedString;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import org.testng.Assert;

/**
 * Ikke dokumentert
 *
 * @author Leif Lislegård
 * @since 1.0 - sprint 28
 */
public class StoreTestMappingTestCase {

    public final static Locale NORSK = new Locale("no", "NO");

    final StoreTestMapper configuration = new StoreTestMapper();
    Locale locale;


    public StoreTestMappingTestCase() {
        setLocale(NORSK);
    }


    public StoreTestMapping buildMapping() {
        return configuration.getMapping();
    }


    public void setLocale(Locale locale) {
        this.locale = locale;
    }


    public Locale getLocale() {
        return locale;
    }

    public LocalizedString createDomainLocalizedString(String text) {
        Map<Locale, String> map = Collections.singletonMap(locale, text);
        return new LocalizedString(map);
    }

    public no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString createWsapiLocalizedString(String text) {
        no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString.Entry entry = new no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString.Entry();
        entry.setKey(locale.toString());
        entry.setValue(text);
        no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString localizedString = new no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString();
        localizedString.getEntry().add(entry);
        return localizedString;
    }

    public void assertEquals(LocalizedString actual, String expected) {
        Assert.assertEquals(actual.getText(locale), expected);
    }
}
