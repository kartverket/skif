package no.statkart.skif.mapper;

import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;
import com.google.common.reflect.TypeToken;

import java.util.HashSet;
import java.util.Set;

/**
 * Factory for typemappere som bare sender objektet tvert igjennom. Bør kun brukes for "primitiver" (Integer, String).
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@SuppressWarnings("unchecked")
public class IdentityTypeMapperFactory implements TypeMapperFactory {
    private final Set<Class<?>> useIdentityMapping = new HashSet<Class<?>>();

    public IdentityTypeMapperFactory useIdentityMapping(Class<?>... c) {
        useIdentityMapping.addAll(Lists.newArrayList(c));
        return this;
    }

    /**
     * Legger til grunnleggende Java-typer.
     * <li>
     * <ul>Boolean og bool</ul>
     * <ul>Byte og byte</ul>
     * <ul>Short og short</ul>
     * <ul>Integer og int</ul>
     * <ul>Long og long</ul>
     * <ul>Float og float</ul>
     * <ul>Double og double</ul>
     * <ul>Character og char</ul>
     * <ul>String</ul>
     * </li>
     *
     * @return <code>this</code>
     */
    public IdentityTypeMapperFactory useIdentityMappingForBasicTypes() {
        return useIdentityMapping(
                Boolean.class, Boolean.TYPE,
                Byte.class, Byte.TYPE,
                Short.class, Short.TYPE,
                Integer.class, Integer.TYPE,
                Long.class, Long.TYPE,
                Float.class, Float.TYPE,
                Double.class, Double.TYPE,
                Character.class, Character.TYPE,
                String.class
        );
    }

    @Override
    public <WsapiT, DomainT> TypeMapper<WsapiT, DomainT> createTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        if (useIdentityMapping.contains(wsapiTypeToken.getRawType()) && useIdentityMapping.contains(domainTypeToken.getRawType()) && wrappingEquals(wsapiTypeToken, domainTypeToken)) {
            final Class<?> clazz = wsapiTypeToken.getRawType();

            return new TypeMapper<WsapiT, DomainT>() {
                private Mapping mapping = null;

                @Override
                public Mapping getMapping() {
                    return mapping;
                }

                @Override
                public void setMapping(Mapping mapping) {
                    this.mapping = mapping;
                }

                @Override
                public WsapiT mapDomainObject(DomainT source) {
                    return (WsapiT) source;
                }

                @Override
                public DomainT mapWsapiObject(WsapiT source) {
                    return (DomainT) source;
                }

                @Override
                public Class<WsapiT> getWsapiClass() {
                    return (Class<WsapiT>) clazz;
                }

                @Override
                public Class<DomainT> getDomainClass() {
                    return (Class<DomainT>) clazz;
                }
            };
        } else {
            return null;
        }
    }

    /**
     * Sjekker om to typer er like. Denne godtar vil si at <code>Integer == int</code>.
     */
    private <WsapiT, DomainT> boolean wrappingEquals(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        if (wsapiTypeToken.equals(domainTypeToken)) {
            return true;
        } else {
            // Det er viktig her å ikke gå ned på equals av rawType med mindre den ene eller andre siden er en primitiv type.
            if (wsapiTypeToken.getRawType().isPrimitive()) {
                return wsapiTypeToken.getRawType().equals(Primitives.unwrap(domainTypeToken.getRawType()));
            }
            if (domainTypeToken.getRawType().isPrimitive()) {
                return domainTypeToken.getRawType().equals(Primitives.unwrap(wsapiTypeToken.getRawType()));
            }
        }
        return false;
    }
}
