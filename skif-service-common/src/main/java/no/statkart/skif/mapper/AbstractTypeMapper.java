package no.statkart.skif.mapper;

import java.lang.reflect.InvocationTargetException;


/**
 * @author Henrik Fredholm
 */
public abstract class AbstractTypeMapper<WsapiT, DomainT> implements TypeMapper<WsapiT, DomainT> {
    private final Class<WsapiT> wsapiClass;
    private final Class<DomainT> domainClass;

    protected AbstractTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        this.wsapiClass = wsapiClass;
        this.domainClass = domainClass;
    }

    @Override
    public abstract Mapping getMapping();

    @Override
    public abstract void setMapping(Mapping mapping);

    @Override
    public Class<WsapiT> getWsapiClass() {
        return wsapiClass;
    }

    @Override
    public Class<DomainT> getDomainClass() {
        return domainClass;
    }

    @Override
    public final WsapiT mapDomainObject(DomainT source) {
        WsapiT target;
        try {
            target = getInitialWsapiObject(source);
        } catch (InstantiationException e) {
            throw new MappingException(e);
        } catch (IllegalAccessException e) {
            throw new MappingException(e);
        } catch (InvocationTargetException e) {
            throw new MappingException(e);
        } catch (NoSuchMethodException e) {
            throw new MappingException(e);
        }
        mapDomainObject(source, target);
        return target;
    }


    @Override
    public final DomainT mapWsapiObject(WsapiT source) {
        DomainT target;
        try {
            target = getInitialDomainObject(source);
        } catch (InstantiationException e) {
            throw new MappingException(e);
        } catch (IllegalAccessException e) {
            throw new MappingException(e);
        } catch (InvocationTargetException e) {
            throw new MappingException(e);
        } catch (NoSuchMethodException e) {
            throw new MappingException(e);
        }
        mapWsapiObject(source, target);
        return target;
    }

    protected WsapiT getInitialWsapiObject(DomainT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return wsapiClass.newInstance();
    }

    protected DomainT getInitialDomainObject(WsapiT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return domainClass.newInstance();
    }

    public abstract void mapDomainObject(DomainT source, WsapiT target);

    public abstract void mapWsapiObject(WsapiT source, DomainT target);

    /**
     * @return debug streng på formen <TypeMapperklasse>{<domeneklasse> <-> <apiklasse>}
     */
    public String toString() {
        return super.toString() + "{" + domainClass + " <-> " + wsapiClass + "}";
    }
}