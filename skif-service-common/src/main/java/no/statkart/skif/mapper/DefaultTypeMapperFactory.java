package no.statkart.skif.mapper;

import com.google.common.reflect.TypeToken;

import java.util.HashSet;
import java.util.Set;

/**
 * Factory for å lage helt generiske {@link DefaultTypeMapper}e.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class DefaultTypeMapperFactory implements TypeMapperFactory {
    private final Set<Class<?>> doNotMapTheseClasses = new HashSet<Class<?>>();

    private boolean failIfMissingDomainProperties = true;

    @Override
    public <WsapiT, DomainT> TypeMapper<WsapiT, DomainT> createTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        return new DefaultTypeMapper<WsapiT, DomainT, Mapping>(wsapiTypeToken, domainTypeToken, Mapping.class, doNotMapTheseClasses, failIfMissingDomainProperties);
    }

    /**
     * Metode for å angi en klasse som skal ignoreres ved mapping.
     *
     * @param className Fully qualified class name.
     */
    public DefaultTypeMapperFactory doNotMapThisClass(String className) {
        try {
            Class c = Class.forName(className);
            doNotMapTheseClasses.add(c);
        } catch (ClassNotFoundException e) {
            throw new MappingException("Tried to ignore unknown class for mapping", e);
        }

        return this;
    }

    public DefaultTypeMapperFactory doNotMapThisClass(Class<?> c) {
        doNotMapTheseClasses.add(c);
        return this;
    }

    public boolean isFailIfMissingDomainProperties() {
        return failIfMissingDomainProperties;
    }

    /**
     * Angir om det skal kastes en exception dersom det finnes en getter på wsapi-siden som ikke har tilsvarende setter
     * på domene-siden. Dette er vanligvis en feil, og standardverdien er <code>true</code>.
     *
     * @param failIfMissingDomainProperties <code>true</code> for å skru på sjekk, <code>false</code> for å skru av.
     */
    public void setFailIfMissingDomainProperties(boolean failIfMissingDomainProperties) {
        this.failIfMissingDomainProperties = failIfMissingDomainProperties;
    }
}
