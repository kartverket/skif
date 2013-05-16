package no.statkart.skif.mapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 */
public class  WsapiListTypeMapper<WsapiT, WsapiE, DomainT extends Collection, DomainE> extends AbstractTypeMapper<WsapiT, DomainT> {
    private final Class<WsapiE> wsapiElementClass;
    private final Class<DomainE> domainElementClass;
    protected final Method getItemsMethod;

    protected Mapping map;

    public WsapiListTypeMapper(Class<WsapiT> wsapiClass, Class<WsapiE> wsapiElementClass, Class<DomainT> domainClass, Class<DomainE> domainElementClass) {
        super(wsapiClass, domainClass);
        this.wsapiElementClass = wsapiElementClass;
        this.domainElementClass = domainElementClass;
        try {
            getItemsMethod = wsapiClass.getMethod("getItem");
        } catch (NoSuchMethodException e) {
            throw new MappingException("Class does does not implement method getItem() as expected:" + wsapiClass.getName());
        }
    }

    @Override
    public Mapping getMapping() {
        return map;
    }

    @Override
    public void setMapping(Mapping mapping) {
        map = mapping;
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        List targetList=getList(target);
        for (Object sourceElement : source) {
            targetList.add(map.d2w(sourceElement, wsapiElementClass));
        }
    }

    private List getList(WsapiT target) {
        try {
            return (List) getItemsMethod.invoke(target);
        } catch (IllegalAccessException e) {
            throw new MappingException("Object does does not implement method getItem() as expected:" + target.getClass().getName());
        } catch (InvocationTargetException e) {
            throw new MappingException("Object does does not implement method getItem() as expected:" + target.getClass().getName());
        }
    }


    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        List sourceList=getList(source);
        target.clear();
        for (Object sourceElement : sourceList) {
            target.add(map.w2d(sourceElement, domainElementClass));
        }
    }

    @Override
    protected DomainT getInitialDomainObject(WsapiT source) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<DomainT> domainClass = getDomainClass();
        if (Set.class.isAssignableFrom(domainClass)) {
            return domainClass.cast(new HashSet());
        } else if (List.class.isAssignableFrom(domainClass)) {
            return domainClass.cast(new ArrayList());
        } else {
            return super.getInitialDomainObject(source);
        }
    }
}