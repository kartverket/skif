package no.statkart.skif.mapper;

import com.google.common.collect.Lists;
import com.google.common.primitives.Primitives;
import com.google.common.reflect.TypeToken;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Factory for typemappere som bare sender objektet tvert igjennom. Bør kun brukes for "primitiver" (Integer, String).
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
@SuppressWarnings("unchecked")
public class IdentityTypeMapperFactory implements TypeMapperFactory {
    private final Set<Class<?>> useIdentityMapping = new HashSet<>();

    public IdentityTypeMapperFactory useIdentityMapping(Class<?>... c) {
        useIdentityMapping.addAll(Lists.newArrayList(c));
        return this;
    }

    /**
     * Returnerer et map som kan brukes som mapping override i {@link MappingResolver}.
     * @return et map som kan brukes fritt, siden det lages en ny ved hvert kall, og dermed heller ikke er "live"
     */
    public Map<Class<?>, Class<?>> getOverrideMappings() {
        Map<Class<?>, Class<?>> overrides = new HashMap<>(useIdentityMapping.size());
        for (Class<?> c : useIdentityMapping) {
            if (!c.isPrimitive()) {
                overrides.put(c, c);
            }
        }
        return overrides;
    }

    /**
     * Legger til grunnleggende Java-typer.
     * <ul>
     * <li>Boolean og bool</li>
     * <li>Byte og byte</li>
     * <li>Short og short</li>
     * <li>Integer og int</li>
     * <li>Long og long</li>
     * <li>Float og float</li>
     * <li>Double og double</li>
     * <li>Character og char</li>
     * <li>String</li>
     * <li>BigInteger</li>
     * <li>BigDecimal</li>
     * </ul>
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
                String.class,
                BigInteger.class,
                BigDecimal.class
        );
    }

    @Override
    public <WsapiT, DomainT> TypeMapper<WsapiT, DomainT> createTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        if (useIdentityMapping.contains(wsapiTypeToken.getRawType()) && useIdentityMapping.contains(domainTypeToken.getRawType()) && isRelated(wsapiTypeToken, domainTypeToken)) {
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
    private <WsapiT, DomainT> boolean isRelated(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        if (wsapiTypeToken.equals(domainTypeToken)) {
            return true;
        } else if (wsapiTypeToken.isSupertypeOf(domainTypeToken) || domainTypeToken.isSupertypeOf(wsapiTypeToken)) { // Antar her at subklassen er en subklasse bare fordi instansen er det, ikke fordi feltet er slik
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
