package no.statkart.skif.mapper;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.util.Since;
import no.statkart.skif.util.SystemVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
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

    protected final Provider<? extends ServiceContext> serviceContextProvider;

    public DefaultTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass, Class<M> mappingInterface) {
        this(TypeToken.of(wsapiClass), TypeToken.of(domainClass), mappingInterface, null);
    }

    public DefaultTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass, Class<M> mappingInterface, Provider<? extends ServiceContext> serviceContextProvider) {
        this(TypeToken.of(wsapiClass), TypeToken.of(domainClass), mappingInterface, serviceContextProvider);
    }

    public DefaultTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken, Class<M> mappingInterface) {
        this(wsapiTypeToken, domainTypeToken, mappingInterface, Collections.emptySet(), false, null);
    }

    public DefaultTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken, Class<M> mappingInterface, Provider<? extends ServiceContext> serviceContextProvider) {
        this(wsapiTypeToken, domainTypeToken, mappingInterface, Collections.emptySet(), false, serviceContextProvider);
    }

    public DefaultTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken, Class<M> mappingInterface, Set<Class<?>> doNotMapTheseClasses, boolean failIfMissingDomainProperties) {
        this(wsapiTypeToken, domainTypeToken, mappingInterface, doNotMapTheseClasses, failIfMissingDomainProperties, null);
    }

    public DefaultTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken, Class<M> mappingInterface, Set<Class<?>> doNotMapTheseClasses, boolean failIfMissingDomainProperties, Provider<? extends ServiceContext> serviceContextProvider) {
        //noinspection unchecked
        super((Class<WsapiT>) wsapiTypeToken.getRawType(), (Class<DomainT>) domainTypeToken.getRawType(), mappingInterface);
        this.serviceContextProvider = serviceContextProvider;

        Logger logger = LoggerFactory.getLogger(DefaultTypeMapper.class);

        Collection<Method> wsapiGetters = findGetters(wsapiTypeToken.getRawType());
        ImmutableMap.Builder<Method, PropertyMappingInfo> builder = ImmutableMap.builder();
        for (Method wsapiGetter : wsapiGetters) {
            Method domainSetter = findSetterForGetter(domainTypeToken.getRawType(), wsapiGetter);
            if (domainSetter != null) {


                PropertyMappingInfo pmi = new PropertyMappingInfo(
                        domainSetter,
                        wsapiTypeToken.resolveType(wsapiGetter.getGenericReturnType()),
                        domainTypeToken.resolveType(domainSetter.getGenericParameterTypes()[0]),
                        null);

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

                SystemVersion sinceVersion = findSinceVersionForDomainField(domainGetter);

                PropertyMappingInfo pmi = new PropertyMappingInfo(
                        wsapiSetter,
                        domainTypeToken.resolveType(domainGetter.getGenericReturnType()),
                        wsapiTypeToken.resolveType(wsapiSetter.getGenericParameterTypes()[0]),
                        sinceVersion);

                if (!doNotMapTheseClasses.contains(pmi.getFromType().getRawType()) && !doNotMapTheseClasses.contains(pmi.getToType().getRawType())) {
                    logger.debug("Mapping {} {}.{}() to void {}.{}({})", new Object[]{pmi.fromType, domainTypeToken.getRawType(), domainGetter.getName(), wsapiTypeToken.getRawType(), wsapiSetter.getName(), pmi.toType});

                    builder.put(domainGetter, pmi);
                }
            }
        }
        domainToWsapi = builder.build();
    }

    /**
     * Finner Since-annotasjon på feltet som ligger bak metoden sendt inn som parameter.
     *
     * @param domainGetter Metode vi ønsker å finne since-versjon for
     * @return Strengverdi av since-annotasjonen hvis denne finnes, null ellers.
     * @throws MappingException dersom det ikke finnes et felt med samme navn som metode
     */
    private SystemVersion findSinceVersionForDomainField(Method domainGetter) {
        Field domainField = findFieldForDomainGetter(domainGetter);

        Since sinceAnnotation = domainField != null ? domainField.getAnnotation(Since.class) : null;
        if (sinceAnnotation != null) {
            return new SystemVersion(sinceAnnotation.value());
        }

        return null;
    }

    /**
     * Finner felt for get-metode på en klasse
     *
     * @param domainGetter Metode vi ønsker å finne felt for
     * @return Field-element for feltet.
     */
    private Field findFieldForDomainGetter(Method domainGetter) {

        String methodName = domainGetter.getName();

        //Fjerner "get" eller "is" fra navnet og gjør første bokstav til liten bokstav istedenfor stor
        String fieldName = null;
        if (methodName.startsWith("get")) {
            fieldName = methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
        } else if (methodName.startsWith("is")) {
            fieldName = methodName.substring(2, 3).toLowerCase() + methodName.substring(3);
        }

        if (fieldName != null) {
            Class<?> domainClass = domainGetter.getDeclaringClass();
            TypeToken<?> typeToken = TypeToken.of(domainClass);

            for (Class<?> clazz : typeToken.getTypes().classes().rawTypes()) {
                try {
                    return clazz.getDeclaredField(fieldName);
                } catch (NoSuchFieldException e) {
                    //OK, fant ikke feltet
                }
            }
        }

        //Det finnes ikke noe felt for getteren. Getteren er da enten feilskrevet eller er ikke en getter i det hele tatt.
        //Kan ikke kaste exception da dette blant annet gjeldet "getBubbleId" på AbstractBubbleObject
        return null;
    }

    /**
     * Override denne dersom målklassen avhenger av hva kildeklassen er.
     *
     * @param source kildeklassen
     * @return initielt opprettet målklasse
     */
    protected WsapiT createWsapiT(DomainT source) {
        return createWsapiT();
    }

    /**
     * Override denne dersom målklassen avhenger av hva kildeklassen er.
     *
     * @param source kildeklassen
     * @return initielt opprettet målklasse
     */
    protected DomainT createDomainT(WsapiT source) {
        return createDomainT();
    }

    @Override
    public WsapiT mapDomainObject(DomainT source) {
        WsapiT target = createWsapiT(source);

        SystemVersion contextVersion = serviceContextProvider != null ? new SystemVersion(serviceContextProvider.get().getSystemVersion()) : null;

        for (Map.Entry<Method, PropertyMappingInfo> entry : domainToWsapi.entrySet()) {
            Method getter = entry.getKey();
            PropertyMappingInfo pmi = entry.getValue();
            Method setter = pmi.getSetter();

            if (contextVersion == null || pmi.getSinceVersion() == null || contextVersion.newerThanOrEqualTo(pmi.getSinceVersion())) {

                final Object fromValue;
                try {
                    fromValue = getter.invoke(source);
                } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                    throw new MappingException("Error during reading of " + getter.toGenericString(), e);
                }

                final Object toValue = getMapping().d2w(fromValue, pmi.getFromType().getType(), pmi.getToType().getType());

                try {
                    setter.invoke(target, toValue);
                } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                    throw new MappingException("Error during writing to " + setter.toGenericString(), e);
                }
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
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
                throw new MappingException("Error during reading of " + getter.toGenericString(), e);
            }

            final Object toValue = getMapping().w2d(fromValue, pmi.getFromType().getType(), pmi.getToType().getType());

            try {
                setter.invoke(target, toValue);
            } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
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

        return Lists.newArrayList(getters.values()); // Kan ikke returnere values() direkte, for den støtter ikke add()
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
        private final SystemVersion sinceVersion;

        private PropertyMappingInfo(Method setter, TypeToken<?> fromType, TypeToken<?> toType, SystemVersion sinceVersion) {
            this.setter = setter;
            this.fromType = fromType;
            this.toType = toType;
            this.sinceVersion = sinceVersion;
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

        public SystemVersion getSinceVersion() {
            return sinceVersion;
        }
    }
}
