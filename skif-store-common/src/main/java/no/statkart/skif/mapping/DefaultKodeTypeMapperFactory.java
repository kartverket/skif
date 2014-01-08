package no.statkart.skif.mapping;

import com.google.common.reflect.TypeToken;
import no.statkart.skif.mapper.DefaultTypeMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.mapper.TypeMapper;
import no.statkart.skif.mapper.TypeMapperFactory;
import no.statkart.skif.store.kodeliste.Kode;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

/**
 * Håndterer det at {@link no.statkart.skif.store.kodeliste.Kode} har kodelisteId i WS-API, men ikke i internmodell.
 * Dette gjøres ved å filtrere vekk getKodelisteId fra listen med gettere på WS-siden. Factory slår til på forespørsler
 * hvor domenesiden arver fra {@link Kode}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class DefaultKodeTypeMapperFactory implements TypeMapperFactory {
    private static final TypeToken<Kode> kodeTypeToken = TypeToken.of(Kode.class);

    @Override
    public <WsapiT, DomainT> TypeMapper createTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        if (kodeTypeToken.isAssignableFrom(domainTypeToken)) {
            final Class<?> wsapiKodeClass = wsapiTypeToken.getRawType();
            return new DefaultTypeMapper<WsapiT, DomainT, Mapping>(wsapiTypeToken, domainTypeToken, Mapping.class, Collections.<Class<?>>emptySet(), true) {
                @Override
                protected Collection<Method> findGetters(Class<?> c) {
                    Collection<Method> getters = super.findGetters(c);
                    if (c.equals(wsapiKodeClass)) {
                        for (Iterator<Method> iterator = getters.iterator(); iterator.hasNext(); ) {
                            Method method = iterator.next();
                            if (method.getName().equals("getKodelisteId")) {
                                iterator.remove();
                            }
                        }
                    }
                    return getters;
                }
            };
        } else {
            return null;
        }
    }
}
