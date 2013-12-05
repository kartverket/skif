package no.statkart.skif.storetest.wsapi.mapping.testutils;

import com.google.inject.Provider;
import no.statkart.skif.service.DefaultServiceContext;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.localization.LocalizedString;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;

import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapper;
import no.statkart.skif.storetest.wsapi.mapping.StoreTestMapping;
import org.testng.Assert;

/**
 * Context for test fixture for testing av {@link no.statkart.skif.storetest.wsapi.mapping.StoreTestMapping}
 *
 * @author Leif Lislegård
 * @since 2.4.0
 */
public class StoreTestMappingTestContext {

    public final static Locale NORSK = new Locale("no", "NO");

    private final ServiceContext serviceContext = new DefaultServiceContext();

    final StoreTestMapper configuration = new StoreTestMapper(new Provider<SnapshotVersion>() {
        @Override
        public SnapshotVersion get() {
            return serviceContext.getSnapshotVersion();
        }
    });


    public StoreTestMappingTestContext() {
        setLocale(NORSK);
    }


    public StoreTestMapping buildMapping() {
        return configuration.getMapping();
    }


    public void setLocale(Locale locale) {
        serviceContext.setLocale(locale);
    }


    public Locale getLocale() {
        return serviceContext.getLocale();
    }

    public LocalizedString createDomainLocalizedString(String text) {
        Map<Locale, String> map = Collections.singletonMap(getLocale(), text);
        return new LocalizedString(map);
    }

    public no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString createWsapiLocalizedString(String text) {
        no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString.Entry entry = new no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString.Entry();
        entry.setKey(getLocale().toString());
        entry.setValue(text);
        no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString localizedString = new no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString();
        localizedString.getEntry().add(entry);
        return localizedString;
    }

    public void assertEquals(LocalizedString actual, String expected) {
        Assert.assertEquals(actual.getText(getLocale()), expected);
    }
}
