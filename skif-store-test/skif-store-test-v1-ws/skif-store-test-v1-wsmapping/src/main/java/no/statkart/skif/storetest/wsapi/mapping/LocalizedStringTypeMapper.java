package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.internal.util.InternalLocaleUtils;
import no.statkart.skif.store.localization.LocalizedString;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Mapper {@link java.util.Locale}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class LocalizedStringTypeMapper extends AbstractStoreTestTypeMapper<no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString, LocalizedString> {
    public LocalizedStringTypeMapper() {
        super(no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString.class, LocalizedString.class);
    }

    @Override
    public no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString mapDomainObject(LocalizedString source) {
        no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString wsapiT = createWsapiT();
        List<no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString.Entry> list = wsapiT.getEntry();

        for (Map.Entry<Locale, String> entry : source.getAllTexts().entrySet()) {
            no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString.Entry e = new no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString.Entry();
            e.setKey(entry.getKey().toString());
            e.setValue(entry.getValue());
            list.add(e);
        }
        return wsapiT;
    }

    @Override
    public LocalizedString mapWsapiObject(no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString source) {
        LocalizedString domainT = createDomainT();
        for (no.statkart.skif.storetest.wsapi.domain.basetyper.LocalizedString.Entry entry : source.getEntry()) {
            domainT.setText(InternalLocaleUtils.toLocale(entry.getKey()), entry.getValue());
        }
        return domainT;
    }
}
