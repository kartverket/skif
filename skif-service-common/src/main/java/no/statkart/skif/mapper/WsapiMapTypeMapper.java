package no.statkart.skif.mapper;

import no.statkart.skif.SkifUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author Roar Ingebrigtsen
 * @since 2.0
 */
public class WsapiMapTypeMapper<WsapiT, DomainT extends Map<Object, Collection>> extends AbstractTypeMapper<WsapiT, DomainT> {
    protected Mapping map;
    private Method getEntryMethod;
    private Class<?> entryClass;
    private Method getValueMethod;
    private Method setValueMethod;
    private Method setKeyMethod;
    private Method getKeyMethod;

    public WsapiMapTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
        super(wsapiClass, domainClass);
        try {
            getEntryMethod = wsapiClass.getMethod("getEntry");
        } catch (NoSuchMethodException e) {
            throw new MappingException("Class does does not implement method getEntry() as expected:" + wsapiClass.getName());
        }

        try{
            entryClass = Class.forName(wsapiClass.getName() + "$Entry");
        } catch (ClassNotFoundException e) {
            throw new MappingException("Class does not contain subclass with the name Entry as expected: " + wsapiClass.getName());
        }

        try{
            getValueMethod = entryClass.getMethod("getValue");
            setValueMethod = entryClass.getMethod("setValue", getValueMethod.getReturnType());
            getKeyMethod = entryClass.getMethod("getKey");
            setKeyMethod = entryClass.getMethod("setKey", getKeyMethod.getReturnType());
        } catch (NoSuchMethodException e) {
            throw new MappingException("Entryclass does not implement expected method! " + entryClass.getName() + ". " + e.getMessage());
        }
    }


    @Override
    public Mapping getMapping() {
        return map;
    }

    @Override
    public void setMapping(Mapping mapping) {
        this.map = mapping;
    }

    @Override
    public void mapDomainObject(DomainT source, WsapiT target) {
        super.mapDomainObject(source, target);

        List entryList = getEntryList(target);

        Set<Map.Entry<Object,Collection>> entrySet = source.entrySet();
        for (Map.Entry<Object, Collection> entry : entrySet) {
            Object wsapiEntry = createEntry();
            Object key = map.d2w(entry.getKey());
            setKeyForEntry(wsapiEntry, key);
            Object wsapiValue = createAndSetValueForEntry(wsapiEntry);

            map.d2w(entry.getValue(), wsapiValue);

            entryList.add(wsapiEntry);
        }
    }


    @Override
    public void mapWsapiObject(WsapiT source, DomainT target) {
        super.mapWsapiObject(source, target);

        List entryList = getEntryList(source);
        for (Object entry : entryList) {
            Object domainKey = map.w2d(getKeyForEntry(entry));
            List domainValueList = new ArrayList();
            map.w2d(getValueForEntry(entry), domainValueList);

            target.put(domainKey, domainValueList);
        }

    }

    private List getEntryList(WsapiT wsapiT) {
        try {
            return (List) getEntryMethod.invoke(wsapiT);
        } catch (IllegalAccessException e) {
            throw new MappingException("Object does does not implement method getItem() as expected:" + wsapiT.getClass().getName());
        } catch (InvocationTargetException e) {
            throw new MappingException("Object does does not implement method getItem() as expected:" + wsapiT.getClass().getName());
        }
    }

    private void setKeyForEntry(Object wsapiEntry, Object key) {
        try {
            setKeyMethod.invoke(wsapiEntry, key);
        } catch (IllegalAccessException e) {
            throw new MappingException("Call to method setKey fro entryClass failed. Entryclass: " + entryClass.getName(), e);
        } catch (InvocationTargetException e) {
            throw new MappingException("Call to method setKey fro entryClass failed. Entryclass: " + entryClass.getName(), e);
        }
    }

    private Object getKeyForEntry(Object wsapiEntry) {
        try {
            return getKeyMethod.invoke(wsapiEntry);
        } catch (IllegalAccessException e) {
            throw new MappingException("Call to method setKey fro entryClass failed. Entryclass: " + entryClass.getName(), e);
        } catch (InvocationTargetException e) {
            throw new MappingException("Call to method setKey fro entryClass failed. Entryclass: " + entryClass.getName(), e);
        }
    }

    private Object getValueForEntry(Object wsapiEntry) {
        try {
            return getValueMethod.invoke(wsapiEntry);
        } catch (IllegalAccessException e) {
            throw new MappingException("Call to method getValue for entryClass failed. Entryclass: " + entryClass.getName(), e);
        } catch (InvocationTargetException e) {
            throw new MappingException("Call to method getValue for entryClass failed. Entryclass: " + entryClass.getName(), e);
        }
    }

    private Object createAndSetValueForEntry(Object wsapiEntry) {
        try {
            Object wsList = SkifUtil.newInstance(getValueMethod.getReturnType());
            setValueMethod.invoke(wsapiEntry, wsList);
            return wsList;
        } catch (IllegalAccessException e) {
            throw new MappingException("Call to method setValue for entryClass failed. Entryclass: " + entryClass.getName(), e);
        } catch (InvocationTargetException e) {
            throw new MappingException("Call to method getValue for entryClass failed. Entryclass: " + entryClass.getName(), e);
        }
    }

    private Object createEntry() {
        try {
            return entryClass.newInstance();
        } catch (InstantiationException e) {
            throw new MappingException("Could not instantiate entryClass: " + entryClass.getName(), e);
        } catch (IllegalAccessException e) {
            throw new MappingException("Could not instantiate entryClass: " + entryClass.getName(), e);
        }
    }


}
