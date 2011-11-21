package no.statkart.skif.mapper;


import no.statkart.skif.exception.ImplementationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * @author Henrik Fredholm
 */
public abstract class AbstractMapper implements InvocationHandler, BaseMapping {
    private static Logger logger = LoggerFactory.getLogger(AbstractMapper.class);

    /**
     * Dersom satt til <b>true</b> vil man ved mangel av registrerte mappere dynamisk søke opp disse og velge ut den nermeste (ved muligt valg mellom flere registrert supertype-mappere)
     */
    private boolean mergeMapping = false;

    //Felles typemapper - hanste
    private TypeMapper defaultMapper = null;

    public boolean isMergeMapping() {
        return mergeMapping;
    }

    enum DIRECTION {
        /**
         * mapping from domain to webserivce classes
         */
        D2W,
        /**
         * mapping from webservice to domain classes
         */
        W2D
    }

    private ObjectFactory domainObjectFactory;
    private ObjectFactory wsapiObjectFactory;

    private Map<Class, TypeMapper<?, ?>> mappersByDomainClass = new HashMap<Class, TypeMapper<?, ?>>();
    private Map<Class, TypeMapper<?, ?>> mappersByWsapiClass = new HashMap<Class, TypeMapper<?, ?>>();

    private Set<Class> useIdentityMapping = new HashSet<Class>();

    private Mapping thisMapping;

    public AbstractMapper(Class<? extends Mapping> mappingClass) {
        this(mappingClass, new DefaultObjectFactory(), new DefaultObjectFactory(), false);
    }

    /**
     * @param mergeMapping bestemmer om en skal søke seg frem til nermeste registrerte mapper for evt supertype eller ikke
     */
    @SuppressWarnings("unchecked")
    public AbstractMapper(Class<? extends Mapping> mappingClass, ObjectFactory wsapiObjectFactory, ObjectFactory domainObjectFactory, boolean mergeMapping) {
        this.wsapiObjectFactory = wsapiObjectFactory;
        this.domainObjectFactory = domainObjectFactory;
        thisMapping = (Mapping) Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{mappingClass}, this);
        this.wsapiObjectFactory.setMapping(thisMapping);
        this.domainObjectFactory.setMapping(thisMapping);
        this.mergeMapping = mergeMapping;
    }

    protected void addMapper(TypeMapper<?, ?> typeMapper) {
        typeMapper.setDomainObjectFactory(domainObjectFactory);
        typeMapper.setWsapiObjectFactory(wsapiObjectFactory);
        typeMapper.setMapping(thisMapping);
        mappersByDomainClass.put(typeMapper.getDomainClass(), typeMapper);
        mappersByWsapiClass.put(typeMapper.getWsapiClass(), typeMapper);
    }

    //Forsøk på å lage en felles typemapper - hanste
    protected void setDefaultMapper(TypeMapper typeMapper) {
        typeMapper.setDomainObjectFactory(domainObjectFactory);
        typeMapper.setWsapiObjectFactory(wsapiObjectFactory);
        typeMapper.setMapping(thisMapping);
        defaultMapper = typeMapper;
    }


    protected void addMapperW2D(TypeMapper<?, ?> typeMapper) {
        typeMapper.setDomainObjectFactory(domainObjectFactory);
        typeMapper.setWsapiObjectFactory(wsapiObjectFactory);
        typeMapper.setMapping(thisMapping);
        mappersByWsapiClass.put(typeMapper.getWsapiClass(), typeMapper);
    }


    protected void useIdentityMapping(Class c) {
        useIdentityMapping.add(c);
    }

    public Mapping getMapping() {
        return thisMapping;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object target;


        if (method.getName().equals("w2d")) {
            target = w2d(args);
        } else if (method.getName().equals("d2w")) {
            target = d2w(args);
        } else {
            target = method.invoke(this, args);
            //throw new SkifImplementationException("Unexpected method call: " + method.toGenericString());
        }

        return target;
    }

    @SuppressWarnings("unchecked")
    protected Object d2w(Object[] args) {

        Object lastArg = args[args.length - 1];
        Class[] parameterTypes = null;
        Class parameterType = null;
        if (lastArg instanceof Class[]) {
            parameterTypes = (Class[]) lastArg;
        } else if (lastArg instanceof Class) {
            parameterType = (Class) lastArg;
        }

        Object source = args[0];
        Object target = null;
        if (source != null) {
            if (source instanceof Object[] && parameterTypes != null) {
                Object[] sourceArray = (Object[]) source;
                Object[] targetArray = new Object[sourceArray.length];
                for (int i = 0; i < sourceArray.length; i++) {
                    targetArray[i] = thisMapping.d2w(sourceArray[i], parameterTypes[i]);
                }
                target = targetArray;
            } else if (useIdentityMapping.contains(source.getClass())) {
                target = source;
            } else if (source instanceof Collection) {
                if (parameterType != null) {
                    target = d2wCollection(args, parameterType);
                } else {
                    target = d2wCollection(args, args[1].getClass());
                }
            } else {
                TypeMapper typeMapper = getMapperByDomainClass(source.getClass());
                target = typeMapper.mapDomainObject(source);
            }
        }
        return target;
    }

    private TypeMapper getMapperByDomainClass(Class sourceClass) {
        return findMapper(sourceClass, mappersByDomainClass, DIRECTION.D2W);
    }

    @SuppressWarnings("unchecked")
    private Object d2wCollection(Object[] args, Class<?> parameterType) {
        Object target = null;

        if (args.length == 2 && args[1] instanceof Class) {
            try {
                target = createNewInstance((Class) args[1]);
            } catch (InstantiationException e) {
                throw new MappingException(e);
            } catch (IllegalAccessException e) {
                throw new MappingException(e);
            }
        } else if (args.length == 2) {
            target = args[1];
        } else {
            try {
                target = createNewInstance(parameterType);
            } catch (InstantiationException e) {
                throw new MappingException(e);
            } catch (IllegalAccessException e) {
                throw new MappingException(e);
            }
        }

        TypeMapper typeMapper = getMapperByWsapiClass(target.getClass());
        Object source = args[0];
        typeMapper.mapDomainObject(source, target);
        return target;
    }

    /**
     * Oppretter ny instans av klassen <code>clazz</code> dersom det er mulig. Vil gi exceptions for abstrakte klasser
     * som mapperen ikke kjenner til
     *
     * @param clazz Klasse vi skal opprette
     * @return Instans av klassen clazz
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    private Object createNewInstance(Class clazz) throws InstantiationException, IllegalAccessException {
        if (Modifier.isAbstract(clazz.getModifiers())) {
            if (clazz.equals(List.class) || clazz.equals(Collection.class)) {
                return new ArrayList();
            } else if (clazz.equals(Set.class)) {
                return new HashSet();
            } else if (clazz.equals(Map.class)) {
                return new HashMap();
            } else {
                throw new MappingException("Mapper har ikke kjenskap til implementerende klasse av den abstrakte typen " + clazz
                        .getName());
            }
        } else if (clazz.isInterface()) {
            throw new MappingException("Mapper har ikke kjenskap til implementerende klasse av den abstrakte typen " + clazz
                    .getName());
        } else {
            return clazz.newInstance();
        }
    }

    @SuppressWarnings("unchecked")
    protected Object w2d(Object[] args) {

        Object lastArg = args[args.length - 1];
        Class[] parameterTypes = null;
        Class parameterType = null;
        if (lastArg instanceof Class[]) {
            parameterTypes = (Class[]) lastArg;
        } else if (lastArg instanceof Class) {
            parameterType = (Class) lastArg;
        }

        Object source = args[0];
        Object target = null;
        if (source != null) {
            if (source instanceof Object[] && parameterTypes != null) {
                Object[] sourceArray = (Object[]) source;
                Object[] targetArray = new Object[sourceArray.length];
                for (int i = 0; i < sourceArray.length; i++) {
                    targetArray[i] = thisMapping.w2d(sourceArray[i], parameterTypes[i]);
                }
                target = targetArray;
            } else if (useIdentityMapping.contains(source.getClass())) {
                target = source;
            } else if (source instanceof Collection) {
                target = w2dCollection(args, parameterType);
            } else {
                TypeMapper typeMapper = getMapperByWsapiClass(source.getClass());
                if (typeMapper instanceof WsapiListTypeMapper) {
                    target = getCollection(args);
                    typeMapper.mapWsapiObject(source, target);
                } else if (typeMapper instanceof WsapiMapTypeMapper) {
                    target = getMap(args);
                    if(args.length == 3 && args[2] instanceof MapperInfo){
                        ((WsapiMapTypeMapper) typeMapper).setValueType(((MapperInfo) args[2]).value()[1]);
                    }
                    typeMapper.mapWsapiObject(source, target);
                } else if (typeMapper instanceof DefaultTypeMapper) {
                    target = getTargetForGenericTypeMapper(args);
                    if (target==null) {
                        target = typeMapper.mapWsapiObject(source);
                    } else {
                        typeMapper.mapWsapiObject(source, target);
                    }
                } else {
                    target = typeMapper.mapWsapiObject(source);
                }
            }
        }
        return target;
    }

    protected Object getTargetForGenericTypeMapper(Object[] args) {
        Object result = null;
        switch (args.length) {
            case 1:
                result = null;
                break;
            case 2:
                if (args[1] instanceof Collection) {
                    result = (Collection) args[1];
                } else if (args[1] instanceof Class) {
                    try {
                        Class t = (Class) args[1];
                        if (t.isAssignableFrom(Collection.class)) {

                            result = createNewInstance((Class) args[1]);
                        } else {
                            result = null;
                        }

                    } catch (InstantiationException e) {
                        throw new MappingException(e);
                    } catch (IllegalAccessException e) {
                        throw new MappingException(e);
                    }
                }
                break;
            default:
                throw new MappingException("Expected a second parameter in mapping class " + args[0].getClass()
                        .getName());
        }
        return result;

    }

    private Collection getCollection(Object[] args) {
        Collection result = null;
        switch (args.length) {
            case 1:
                result = new ArrayList();
                break;
            case 2:
                if (args[1] instanceof Collection) {
                    result = (Collection) args[1];
                } else if (args[1] instanceof Class) {
                    try {
                        Object target = createNewInstance((Class) args[1]);
                        if (target instanceof Collection) {
                            result = (Collection) target;
                        } else {
                            throw new MappingException("Expected instantiable Collection class as second parameter in mapping class " + args[0]
                                    .getClass()
                                    .getName());
                        }
                    } catch (InstantiationException e) {
                        throw new MappingException(e);
                    } catch (IllegalAccessException e) {
                        throw new MappingException(e);
                    }
                }
                break;
            default:
                throw new MappingException("Expected a second parameter in mapping class " + args[0].getClass()
                        .getName());
        }
        return result;
    }


    private Map getMap(Object[] args) {
        Map result = null;
        switch (args.length) {
            case 1:
                result = new HashMap();
                break;
            case 2:
            case 3:
                if (args[1] instanceof Map) {
                    result = (Map) args[1];
                } else if (args[1] instanceof Class) {
                    try {
                        Object target = createNewInstance((Class) args[1]);
                        if (target instanceof Map) {
                            result = (Map) target;
                        } else {
                            throw new MappingException("Expected instantiable Map class as second parameter in mapping class " + args[0]
                                    .getClass()
                                    .getName());
                        }
                    } catch (InstantiationException e) {
                        throw new MappingException(e);
                    } catch (IllegalAccessException e) {
                        throw new MappingException(e);
                    }
                }
                break;
            default:
                throw new MappingException("Expected a second parameter in mapping class " + args[0].getClass()
                        .getName());
        }
        return result;
    }

    private TypeMapper findMapper(Class mappableClass, Map<Class, TypeMapper<?, ?>> mapOfMappers, DIRECTION direction) {
        if (mapOfMappers.containsKey(mappableClass)) {
            return mapOfMappers.get(mappableClass);
        }
        final Collection<TypeMapper<?, ?>> candidates = new HashSet<TypeMapper<?, ?>>();
        for (TypeMapper<?, ?> candidate : mapOfMappers.values()) {
            final Class<?> candidateClass;

            if (DIRECTION.D2W == direction) {
                candidateClass = candidate.getDomainClass();
            } else if (DIRECTION.W2D == direction) {
                candidateClass = candidate.getWsapiClass();
            } else {
                throw new ImplementationException("Unknows direction: " + direction);
            }

            if (candidateClass.isAssignableFrom(mappableClass)) {
                candidates.add(candidate);
            }

        }
//        if (candidates.size() == 0) {
//            throw new MappingException("TypeMapper[" + getClass().getName() +"] has no mapper for for class: " + mappableClass.getName());
//        } else
        if (candidates.size() > 1) {
            if (!isMergeMapping()) {
                throw new MappingException("TypeMapper[" + getClass().getName() + "] found " + candidates.size() + " mapper candidates for class " + mappableClass.getName());
            }
        }
        if (candidates.size() == 1 || (isMergeMapping() && candidates.size() > 1)) {
            final TypeMapper<?, ?> candidate = findClosestTypeMapper(candidates, mappableClass, direction);
            if (logger.isDebugEnabled()) {
                logger.debug("TypeMapper[" + getClass().getName() + "] has assigned mapping of class " + mappableClass + " to " + candidate);
            }
            mapOfMappers.put(mappableClass, candidate);
            return candidate;
        }

        //Felles typemapper - hanste
        if (defaultMapper != null) {
            return defaultMapper;
        } else {
            throw new MappingException("TypeMapper[" + getClass().getName() + "] has no mapper for for class: " + mappableClass.getName());
        }
    }

    private TypeMapper<?, ?> findClosestTypeMapper(Collection<TypeMapper<?, ?>> candidates, Class mappableClass, DIRECTION direction) {
        TypeMapper<?, ?> closest = candidates.iterator().next();
        if (candidates.size() == 1) {
            return closest;
        }

        //map with natural ordering of keys
        TreeMap<Integer, TypeMapper<?, ?>> signedCandidates = new TreeMap<Integer, TypeMapper<?, ?>>();

        for (TypeMapper<?, ?> candidate : candidates) {
            final Class<?> mapperClass;
            Class<?> candidateClass;
            int weight = 0;

            if (DIRECTION.D2W == direction) {
                mapperClass = candidate.getDomainClass();
            } else if (DIRECTION.W2D == direction) {
                mapperClass = candidate.getWsapiClass();
            } else {
                throw new ImplementationException("Unknows direction: " + direction);
            }

            candidateClass = mappableClass;
            while (!mapperClass.equals(candidateClass) && !Object.class.equals(candidateClass)) {
                weight++;
                candidateClass = candidateClass.getSuperclass();
            }

            TypeMapper<?, ?> sibling = signedCandidates.put(weight, candidate);
            if (sibling != null) {
                throw new ImplementationException("Several defined mappers found for mapping of class of type " + mappableClass);
            }
        }

        return signedCandidates.values().iterator().next();
    }

    private TypeMapper getMapperByWsapiClass(Class wsapiClass) {
        return findMapper(wsapiClass, mappersByWsapiClass, DIRECTION.W2D);
    }

    private Object w2dCollection(Object[] args, Class<?> parameterType) {
        throw new UnsupportedOperationException("Mapping currently not implemented");
    }

    public ObjectFactory getDomainObjectFactory() {
        return domainObjectFactory;
    }

    public ObjectFactory getWsapiObjectFactory() {
        return wsapiObjectFactory;
    }
}