package no.statkart.skif.mapper;

import java.lang.reflect.InvocationTargetException;


/**
 * @author Henrik Fredholm
 */
public abstract class AbstractTypeMapper<WsapiT, DomainT> implements TypeMapper<WsapiT, DomainT> {
    private ObjectFactory domainObjectFactory;
    private ObjectFactory wsapiObjectFactory;
    final Class<WsapiT> wsapiClass;
    final Class<DomainT> domainClass;

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
    public ObjectFactory getWsapiObjectFactory() {
        return wsapiObjectFactory;
    }

    @Override
    public void setWsapiObjectFactory(ObjectFactory factory) {
        this.wsapiObjectFactory = factory;
    }


    @Override
    public ObjectFactory getDomainObjectFactory() {
        return domainObjectFactory;
    }

    @Override
    public void setDomainObjectFactory(ObjectFactory factory) {
        this.domainObjectFactory = factory;
    }

    @Override
    public final WsapiT mapDomainObject(DomainT source) {
        WsapiT target = null;
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
        DomainT target = null;
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
        WsapiT target = wsapiObjectFactory.getInitialObject(source, getWsapiClass());
        return target;
    }

    protected DomainT getInitialDomainObject(WsapiT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        return domainObjectFactory.getInitialObject(source, getDomainClass());
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        // Dette er roten. Alle mappinger bør komme igjennom her

    }

    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        // Dette er roten. Alle mappinger bør komme igjennom her
    }

    /**
     * @return debug streng på formen <TypeMapperklasse>{<domeneklasse> <-> <apiklasse>}
     */
    public String toString() {
        return super.toString() + "{" + domainClass + " <-> " + wsapiClass + "}";
    }
}