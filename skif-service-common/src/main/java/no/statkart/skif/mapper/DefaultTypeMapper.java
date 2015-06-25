package no.statkart.skif.mapper;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import no.statkart.skif.exception.ImplementationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;


/**
 * Denne klassen forsøker å mappe alle felter som er felles mellom to klasser.
 *
 * @author Steinar Hansen
 * @author Tor Egil R. Strand
 */
public class DefaultTypeMapper<WsapiT, DomainT, M extends Mapping> extends AbstractTypeMapper<WsapiT, DomainT, M> {
    private final Map<Method, PropertyMappingInfo> wsapiToDomain;
    private final Map<Method, PropertyMappingInfo> domainToWsapi;

    public DefaultTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass, Class<M> mappingInterface) {
        this(TypeToken.of(wsapiClass), TypeToken.of(domainClass), mappingInterface);
    }

    public DefaultTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken, Class<M> mappingInterface) {
        this(wsapiTypeToken, domainTypeToken, mappingInterface, Collections.<Class<?>>emptySet(), false);
    }

    public DefaultTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken, Class<M> mappingInterface, Set<Class<?>> doNotMapTheseClasses, boolean failIfMissingDomainProperties) {
        //noinspection unchecked
        super((Class<WsapiT>) wsapiTypeToken.getRawType(), (Class<DomainT>) domainTypeToken.getRawType(), mappingInterface);

        Logger logger = LoggerFactory.getLogger(DefaultTypeMapper.class);

        Collection<Method> wsapiGetters = findGetters(wsapiTypeToken.getRawType());
        ImmutableMap.Builder<Method, PropertyMappingInfo> builder = ImmutableMap.builder();
        for (Method wsapiGetter : wsapiGetters) {
            Method domainSetter = findSetterForGetter(domainTypeToken.getRawType(), wsapiGetter);
            if (domainSetter != null) {
                PropertyMappingInfo pmi = new PropertyMappingInfo(
                        domainSetter,
                        wsapiTypeToken.resolveType(wsapiGetter.getGenericReturnType()),
                        domainTypeToken.resolveType(domainSetter.getGenericParameterTypes()[0])
                );

                if (!doNotMapTheseClasses.contains(pmi.getFromType().getRawType()) && !doNotMapTheseClasses.contains(pmi.getToType().getRawType())) {
                    logger.debug("Mapping {} {}.{}() to void {}.{}({})", new Object[]{pmi.fromType, wsapiTypeToken.getRawType(), wsapiGetter.getName(), domainTypeToken.getRawType(), domainSetter.getName(), pmi.toType});

                    builder.put(wsapiGetter, pmi);
                }
            } else if (failIfMissingDomainProperties) {
                throw new ImplementationException("No corresponding domain setter for wsapi getter " + wsapiGetter.getName());
            }
        }
        wsapiToDomain = builder.build();

        Collection<Method> domainGetters = findGetters(domainTypeToken.getRawType());
        builder = ImmutableMap.builder();
        for (Method domainGetter : domainGetters) {
            Method wsapiSetter = findSetterForGetter(wsapiTypeToken.getRawType(), domainGetter);
            if (wsapiSetter != null) {
                PropertyMappingInfo pmi = new PropertyMappingInfo(
                        wsapiSetter,
                        domainTypeToken.resolveType(domainGetter.getGenericReturnType()),
                        wsapiTypeToken.resolveType(wsapiSetter.getGenericParameterTypes()[0])
                );

                if (!doNotMapTheseClasses.contains(pmi.getFromType().getRawType()) && !doNotMapTheseClasses.contains(pmi.getToType().getRawType())) {
                    logger.debug("Mapping {} {}.{}() to void {}.{}({})", new Object[]{pmi.fromType, domainTypeToken.getRawType(), domainGetter.getName(), wsapiTypeToken.getRawType(), wsapiSetter.getName(), pmi.toType});

                    builder.put(domainGetter, pmi);
                }
            }
        }
        domainToWsapi = builder.build();
    }

    /**
     * Override denne dersom målklassen avhenger av hva kildeklassen er.
     *
     * @param source    kildeklassen
     * @return initielt opprettet målklasse
     */
    protected WsapiT createWsapiT(DomainT source) {
        return createWsapiT();
    }

    /**
     * Override denne dersom målklassen avhenger av hva kildeklassen er.
     *
     * @param source    kildeklassen
     * @return initielt opprettet målklasse
     */
    protected DomainT createDomainT(WsapiT source) {
        return createDomainT();
    }

    @Override
    public WsapiT mapDomainObject(DomainT source) {
        WsapiT target = createWsapiT(source);

        for (Map.Entry<Method, PropertyMappingInfo> entry : domainToWsapi.entrySet()) {
            Method getter = entry.getKey();
            PropertyMappingInfo pmi = entry.getValue();
            Method setter = pmi.getSetter();

            final Object fromValue;
            try {
                fromValue = getter.invoke(source);
            } catch (IllegalAccessException e) {
                throw new MappingException("Error during reading of " + getter.toGenericString(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Error during reading of " + getter.toGenericString(), e);
            } catch (IllegalArgumentException e) {
                throw new MappingException("Error during reading of " + getter.toGenericString(), e);
            }

            final Object toValue = getMapping().d2w(fromValue, pmi.getFromType().getType(), pmi.getToType().getType());

            try {
                setter.invoke(target, toValue);
            } catch (IllegalAccessException e) {
                throw new MappingException("Error during writing to " + setter.toGenericString(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Error during writing to " + setter.toGenericString(), e);
            } catch (IllegalArgumentException e) {
                throw new MappingException("Error during writing to " + setter.toGenericString(), e);
            }
        }

        return target;
    }


    @Override
    public DomainT mapWsapiObject(WsapiT source) {
        DomainT target = createDomainT(source);

        for (Map.Entry<Method, PropertyMappingInfo> entry : wsapiToDomain.entrySet()) {
            Method getter = entry.getKey();
            PropertyMappingInfo pmi = entry.getValue();
            Method setter = pmi.getSetter();

            final Object fromValue;
            try {
                fromValue = getter.invoke(source);
            } catch (IllegalAccessException e) {
                throw new MappingException("Error during reading of " + getter.toGenericString(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Error during reading of " + getter.toGenericString(), e);
            } catch (IllegalArgumentException e) {
                throw new MappingException("Error during reading of " + getter.toGenericString(), e);
            }

            final Object toValue = getMapping().w2d(fromValue, pmi.getFromType().getType(), pmi.getToType().getType());

            try {
                setter.invoke(target, toValue);
            } catch (IllegalAccessException e) {
                throw new MappingException("Error during writing to " + setter.toGenericString(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Error during writing to " + setter.toGenericString(), e);
            } catch (IllegalArgumentException e) {
                throw new MappingException("Error during writing to " + setter.toGenericString(), e);
            }
        }

        return target;
    }


    protected Collection<Method> findGetters(Class<?> c) {
        Map<String, Method> getters = Maps.newLinkedHashMap();

        for (Class<?> clazz = c; clazz != null && clazz != Object.class; clazz = clazz.getSuperclass()) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (!method.isBridge() && method.getParameterTypes().length == 0 && (method.getName().startsWith("get") || method.getName().startsWith("is")) && method.getAnnotation(DontMap.class) == null && !getters.containsKey(method.getName())) {
                    method.setAccessible(true);
                    getters.put(method.getName(), method);
                }
            }
        }

        return getters.values();
    }

    protected Method findSetterForGetter(Class<?> targetClass, Method getter) {
        String getterName = getter.getName();
        String expectedSetterName;
        if (getterName.startsWith("is")) {
            expectedSetterName = "set" + getterName.substring(2);
        } else {
            expectedSetterName = "set" + getterName.substring(3);
        }

        Method matched = null;
        for (Class<?> clazz = targetClass; clazz != null; clazz = clazz.getSuperclass()) {
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                if (method.getParameterTypes().length == 1 && method.getName().equals(expectedSetterName) && method.getAnnotation(DontMap.class) == null) {
                    matched = method;
                    matched.setAccessible(true);
                    break;
                }
            }
        }

        return matched;
    }

    private static class PropertyMappingInfo {
        private final Method setter;
        private final TypeToken<?> fromType;
        private final TypeToken<?> toType;

        private PropertyMappingInfo(Method setter, TypeToken<?> fromType, TypeToken<?> toType) {
            this.setter = setter;
            this.fromType = fromType;
            this.toType = toType;
        }

        private Method getSetter() {
            return setter;
        }

        private TypeToken<?> getFromType() {
            return fromType;
        }

        private TypeToken<?> getToType() {
            return toType;
        }
    }
}
