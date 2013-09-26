package no.statkart.skif.storetest.wsapi.mapping;

import no.statkart.skif.internal.util.InternalLocaleUtils;
import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.mapper.Mapping;

import java.util.Locale;

/**
 * Mapper {@link Locale}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class LocaleMapper extends AbstractTypeMapper<String, Locale, Mapping> {
    public LocaleMapper() {
        super(String.class, Locale.class, Mapping.class);
    }

    @Override
    public String mapDomainObject(Locale source) {
        return source.toString();
    }

    @Override
    public Locale mapWsapiObject(String source) {
        return InternalLocaleUtils.toLocale(source);
    }
}
