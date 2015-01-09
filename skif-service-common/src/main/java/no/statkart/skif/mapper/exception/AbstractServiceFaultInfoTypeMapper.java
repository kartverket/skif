package no.statkart.skif.mapper.exception;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import no.statkart.skif.exception.SkifException;
import no.statkart.skif.mapper.DefaultTypeMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.mapper.MappingException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

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

    public AbstractServiceFaultInfoTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        this(wsapiTypeToken, domainTypeToken, Collections.<Class<?>>emptySet(), true);
    }

    public AbstractServiceFaultInfoTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken, Set<Class<?>> doNotMapTheseClasses, boolean failIfMissingDomainProperties) {
        super(wsapiTypeToken, domainTypeToken, Mapping.class, doNotMapTheseClasses, failIfMissingDomainProperties);
    }

    @Override
    protected Collection<Method> findGetters(Class<?> c) {
        Collection<Method> getters = super.findGetters(c);

        if (c.equals(getWsapiClass())) {
            Iterator<Method> iterator = getters.iterator();
            while (iterator.hasNext()) {
                Method getter = iterator.next();
                if (externallyMappedGetters.contains(getter.getName())) {
                    iterator.remove();
                }
            }
        }

        return getters;
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
        } catch (ClassNotFoundException e) {
            throw new MappingException(e);
        } catch (ClassCastException e) {
            throw new MappingException(e);
        } catch (NoSuchMethodException e) {
            throw new MappingException(e);
        } catch (InvocationTargetException e) {
            throw new MappingException(e.getTargetException());
        } catch (InstantiationException e) {
            throw new MappingException(e);
        } catch (IllegalAccessException e) {
            throw new MappingException(e);
        }
    }
}
