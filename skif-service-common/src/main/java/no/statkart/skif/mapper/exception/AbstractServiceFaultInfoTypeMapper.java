package no.statkart.skif.mapper.exception;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;
import no.statkart.skif.exception.SkifException;
import no.statkart.skif.mapper.DefaultTypeMapper;
import no.statkart.skif.mapper.DontMap;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.service.ServiceContext;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Generell funksjonalitet for å mappe mellom WS-API-ets FaultInfo-klasser og SkifException-hierarkiet.
 *
 * @since 2.6.0
 */
public abstract class AbstractServiceFaultInfoTypeMapper<WsapiT, DomainT extends SkifException> extends DefaultTypeMapper<WsapiT, DomainT, Mapping> {
    private static final Set<String> externallyMappedGetters = ImmutableSet.of(
            "getCategory",
            "getExceptionDetail",
            "getStackTraceText"
    );


    public AbstractServiceFaultInfoTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken, Class<Mapping> mappingInterface, Provider<? extends ServiceContext> serviceContextProvider) {
        this(wsapiTypeToken, domainTypeToken, mappingInterface, Collections.<Class<?>>emptySet(), true, serviceContextProvider);
    }

    public AbstractServiceFaultInfoTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken, Class<Mapping> mappingInterface, Set<Class<?>> doNotMapTheseClasses, boolean failIfMissingDomainProperties, Provider<? extends ServiceContext> serviceContextProvider) {
        super(wsapiTypeToken, domainTypeToken, mappingInterface, doNotMapTheseClasses, failIfMissingDomainProperties, serviceContextProvider);
    }

    public AbstractServiceFaultInfoTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        this(wsapiTypeToken, domainTypeToken, Collections.<Class<?>>emptySet(), true);
    }

    public AbstractServiceFaultInfoTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken, Set<Class<?>> doNotMapTheseClasses, boolean failIfMissingDomainProperties) {
        super(wsapiTypeToken, domainTypeToken, Mapping.class, doNotMapTheseClasses, failIfMissingDomainProperties);
    }

    @Override
    protected Collection<Method> findGetters(Class<?> c) {

        if (c.equals(getWsapiClass())) {
            Collection<Method> getters = super.findGetters(c);

            getters.removeIf(getter -> externallyMappedGetters.contains(getter.getName()));

            return getters;
        } else {
            // Kan ikke bruke like aggressiv reflection på Exception pga. jigsaw

            Map<String, Method> getters = new LinkedHashMap<>();

            for (Class<?> clazz = c; clazz != null && clazz != Exception.class; clazz = clazz.getSuperclass()) {
                Method[] methods = clazz.getDeclaredMethods();
                for (Method method : methods) {
                    if (!method.isBridge() && method.getParameterTypes().length == 0 && (method.getName().startsWith("get") || method.getName().startsWith("is")) && method.getAnnotation(DontMap.class) == null && !getters.containsKey(method.getName())) {
                        method.setAccessible(true);
                        getters.put(method.getName(), method);
                    }
                }
            }

            Method[] methods = Exception.class.getMethods();
            for (Method method : methods) {
                if (!method.isBridge() && method.getParameterTypes().length == 0 && (method.getName().startsWith("get") || method.getName().startsWith("is")) && method.getAnnotation(DontMap.class) == null && !getters.containsKey(method.getName())) {
                    getters.put(method.getName(), method);
                }
            }

            return new ArrayList<>(getters.values());
        }

    }

    /**
     * @param source    faultinfo-instans
     * @return innholdet i faultinfo sin exceptionDetail sitt className-felt
     */
    protected abstract String getExceptionDetailClassName(WsapiT source);

    /**
     * @param source    faultinfo-instans
     * @return innholdet i faultinfo sin exceptionDetail sitt message-felt
     */
    protected abstract String getExceptionDetailMessage(WsapiT source);

    @Override
    public DomainT createDomainT(WsapiT source) {
        try {
            String className = getExceptionDetailClassName(source);
            Class<?> aClass = Class.forName(className);

            Class<? extends DomainT> eClass = aClass.asSubclass(getDomainClass());

            Constructor<? extends DomainT> constructor = eClass.getConstructor(String.class);

            return constructor.newInstance(getExceptionDetailMessage(source));
        } catch (ClassNotFoundException | ClassCastException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new MappingException(e);
        } catch (InvocationTargetException e) {
            throw new MappingException(e.getTargetException());
        }
    }
}
