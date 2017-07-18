package no.statkart.skif.mapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 */
public class WsapiListTypeMapper<WsapiT, WsapiE, DomainT extends Collection, DomainE> extends AbstractTypeMapper<WsapiT, DomainT, Mapping> {
    private final Class<WsapiE> wsapiElementClass;
    private final Class<DomainE> domainElementClass;
    protected final Method getItemsMethod;

    public WsapiListTypeMapper(Class<WsapiT> wsapiClass, Class<WsapiE> wsapiElementClass, Class<DomainT> domainClass, Class<DomainE> domainElementClass) {
        super(wsapiClass, domainClass, Mapping.class);
        this.wsapiElementClass = wsapiElementClass;
        this.domainElementClass = domainElementClass;
        try {
            getItemsMethod = wsapiClass.getMethod("getItem");
        } catch (NoSuchMethodException e) {
            throw new MappingException("Class does does not implement method getItem() as expected:" + wsapiClass.getName());
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public WsapiT mapDomainObject(DomainT source) {
        WsapiT target = createWsapiT();
        List targetList=getList(target);
        for (Object sourceElement : source) {
            targetList.add(getMapping().d2w(sourceElement, wsapiElementClass));
        }
        return target;
    }

    private List getList(WsapiT target) {
        try {
            return (List) getItemsMethod.invoke(target);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MappingException("Object does does not implement method getItem() as expected:" + target.getClass().getName());
        }
    }


    @SuppressWarnings("unchecked")
    @Override
    public DomainT mapWsapiObject(WsapiT source) {
        DomainT target = createDomainT();
        List sourceList=getList(source);
        for (Object sourceElement : sourceList) {
            target.add(getMapping().w2d(sourceElement, domainElementClass));
        }
        return target;
    }

    protected DomainT createDomainT() {
        Class<DomainT> domainClass = getDomainClass();
        if (Set.class.isAssignableFrom(domainClass)) {
            return domainClass.cast(new HashSet());
        } else if (List.class.isAssignableFrom(domainClass)) {
            return domainClass.cast(new ArrayList());
        } else {
            return super.createDomainT();
        }
    }
}