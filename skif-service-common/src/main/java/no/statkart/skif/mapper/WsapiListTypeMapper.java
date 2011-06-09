package no.statkart.skif.mapper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * 
 * @author Henrik Fredholm
 */
public class  WsapiListTypeMapper<WsapiT, DomainT extends Collection> extends AbstractTypeMapper<WsapiT, DomainT> {
    protected Method getItemsMethod;
    protected Mapping map;

    public WsapiListTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
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
        super.mapDomainObject(source, target);
        List targetList=getList(target);
        for (Object sourceElement : source) {
            targetList.add(map.d2w(sourceElement));
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
        super.mapWsapiObject(source, target);
        List sourceList=getList(source);
        target.clear();
        for (Object sourceElement : sourceList) {
            target.add(map.w2d(sourceElement));
        }
    }
}