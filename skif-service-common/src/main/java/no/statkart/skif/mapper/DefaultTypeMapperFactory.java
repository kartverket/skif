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

    @Override
    public <WsapiT, DomainT> TypeMapper<WsapiT, DomainT> createTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        return new DefaultTypeMapper<WsapiT, DomainT, Mapping>(wsapiTypeToken, domainTypeToken, Mapping.class, doNotMapTheseClasses);
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
}
