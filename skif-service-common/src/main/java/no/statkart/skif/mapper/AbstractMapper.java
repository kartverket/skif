package no.statkart.skif.mapper;


import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.reflect.TypeToken;
import no.statkart.skif.exception.ImplementationException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.*;
import java.util.*;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 */
public abstract class AbstractMapper<M extends Mapping> implements InvocationHandler, DefaultTypeMapped {
//    private static Logger logger = LoggerFactory.getLogger(AbstractMapper.class);

    private DefaultTypeMapper defaultMapper = null;

    private static enum Direction {
        /**
         * mapping from domain to webserivce classes
         */
        D2W,
        /**
         * mapping from webservice to domain classes
         */
        W2D
    }

    private final ListMultimap<Class<?>, TypeMapper<?, ?>> mappersByDomainClass = ArrayListMultimap.create();
    private final ListMultimap<Class<?>, TypeMapper<?, ?>> mappersByWsapiClass = ArrayListMultimap.create();

    private final Set<Class<?>> useIdentityMapping = new HashSet<Class<?>>();

    private final M thisMapping;

    private final ThreadLocal<Integer> recurseLevel_w2d = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };
    private final ThreadLocal<Integer> recurseLevel_d2w = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };

    public AbstractMapper(Class<? extends M> mappingClass) {
        thisMapping = mappingClass.cast(Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{mappingClass}, this));
    }

    protected void addMapper(TypeMapper<?, ?> typeMapper) {
        typeMapper.setMapping(thisMapping);
        mappersByDomainClass.put(typeMapper.getDomainClass(), typeMapper);
        mappersByWsapiClass.put(typeMapper.getWsapiClass(), typeMapper);
    }

    protected void setDefaultMapper(DefaultTypeMapper typeMapper) {
        typeMapper.setMapping(thisMapping);
        defaultMapper = typeMapper;
    }

    public DefaultTypeMapper getDefaultMapper() {
        return defaultMapper;
    }

    protected void useIdentityMapping(Class<?> c) {
        useIdentityMapping.add(c);
    }

    public M getMapping() {
        return thisMapping;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object target;

        if (method.getName().equals("w2d")) {
            //try/finally for å vedlikeholde en teller for hvor dypt i rekursjonsgrafen vi er.
            //Dersom vi er på toppen så kan vi clearMappedFields fra DefaultTypeMapper.
            try {
                int i = recurseLevel_w2d.get();
                if (i == 0) {
                    if (defaultMapper != null) {
                        defaultMapper.clearMappedFields();
                    }
                }
                recurseLevel_w2d.set(++i);
                target = w2d(method, args);
            } finally {
                int i = recurseLevel_w2d.get();
                recurseLevel_w2d.set(--i);
            }
        } else if (method.getName().equals("d2w")) {
            //try/finally for å vedlikeholde en teller for hvor dypt i rekursjonsgrafen vi er.
            //Dersom vi er på toppen så kan vi clearMappedFields fra DefaultTypeMapper.
            try {
                int i = recurseLevel_d2w.get();
                if (i == 0) {
                    if (defaultMapper != null) {
                        ((DefaultTypeMapper) defaultMapper).clearMappedFields();
                    }
                }
                recurseLevel_d2w.set(++i);
                target = d2w(method, args);
            } finally {
                int i = recurseLevel_d2w.get();
                recurseLevel_d2w.set(--i);
            }
        } else {
            target = method.invoke(this, args);
            //throw new SkifImplementationException("Unexpected method call: " + method.toGenericString());
        }

        return target;
    }

    @Override
    public DefaultTypeMapping getDefaultTypeMapping() {
        return getDefaultMapper();
    }

    protected Object d2w(Method method, Object[] args) {
        if (args.length == 1) {
            return d2w(args[0], method.getGenericReturnType());
        } else if (args.length == 2) {
            return d2w(args[0], (Type) args[1]);
        } else {
            throw new ImplementationException("No such method: " + method.toString());
        }
    }

    protected Object d2w(Object source, Type targetType) {
        Object target = null;
        if (source != null) {
            TypeToken<?> targetTypeToken = TypeToken.of(targetType);
            TypeToken<?> sourceTypeToken = TypeToken.of(source.getClass());
            if (useIdentityMapping.contains(source.getClass())) {
                target = source;
            } else if (sourceTypeToken.isArray() && targetTypeToken.isArray()) {
                int length = Array.getLength(source);
                target = Array.newInstance(targetTypeToken.getComponentType().getRawType(), length);
                for (int i = 0; i < length; ++i) {
                    Array.set(target, i, thisMapping.d2w(Array.get(source, i), targetTypeToken.getComponentType().getType()));
                }
            } else {
                TypeMapper typeMapper = findMapper(sourceTypeToken.getRawType(), targetTypeToken.getRawType(), Direction.D2W);
                if (typeMapper != null) {
                    target = typeMapper.mapDomainObject(source);
                } else if (defaultMapper != null) {
                    target = defaultMapper.mapDomainObject(source, targetTypeToken);
                } else {
                    throw new MappingException(String.format("Mapper[%s] could not map from %s to %s", this.getClass().getName(), sourceTypeToken, targetTypeToken));
                }
            }
        }
        return target;
    }

    protected Object w2d(Method method, Object[] args) {
        if (args.length == 1) {
            return w2d(args[0], method.getGenericReturnType());
        } else if (args.length == 2) {
            return w2d(args[0], (Type) args[1]);
        } else {
            throw new ImplementationException("No such method: " + method.toString());
        }
    }

    protected Object w2d(Object source, Type targetType) {
        Object target = null;
        if (source != null) {
            TypeToken<?> targetTypeToken = TypeToken.of(targetType);
            TypeToken<?> sourceTypeToken = TypeToken.of(source.getClass());
            if (useIdentityMapping.contains(source.getClass())) {
                target = source;
            } else if (sourceTypeToken.isArray() && targetTypeToken.isArray()) {
                int length = Array.getLength(source);
                target = Array.newInstance(targetTypeToken.getComponentType().getRawType(), length);
                for (int i = 0; i < length; ++i) {
                    Array.set(target, i, thisMapping.w2d(Array.get(source, i), targetTypeToken.getComponentType().getType()));
                }
            } else {
                TypeMapper typeMapper = findMapper(sourceTypeToken.getRawType(), targetTypeToken.getRawType(), Direction.W2D);
                if (typeMapper != null) {
                    target = typeMapper.mapWsapiObject(source);
                } else if (defaultMapper != null) {
                    target = defaultMapper.mapWsapiObject(source, targetTypeToken);
                } else {
                    throw new MappingException(String.format("Mapper[%s] could not map from %s to %s", this.getClass().getName(), sourceTypeToken, targetTypeToken));
                }
            }
        }
        return target;
    }

    private TypeMapper findMapper(Class<?> sourceClass, Class<?> targetClass, @Nonnull Direction direction) {
        ListMultimap<Class<?>, TypeMapper<?, ?>> mapOfMappers;
        switch (direction) {
            case D2W:
                mapOfMappers = mappersByDomainClass;
                break;
            case W2D:
                mapOfMappers = mappersByWsapiClass;
                break;
            default:
                throw new MappingException("Invalid direction: " + direction);
        }

        // Finn mappere som kan gå fra sourceClass
        List<TypeMapper<?, ?>> candidates = mapOfMappers.get(sourceClass);
        if (candidates == null || candidates.isEmpty()) {
            // Ingen som kan gå fra sourceClass, så finn de som kan mapper superklasser av sourceClass
            candidates = new ArrayList<TypeMapper<?, ?>>();

            for (Map.Entry<Class<?>, TypeMapper<?, ?>> entry : mapOfMappers.entries()) {
                if (entry.getKey().isAssignableFrom(sourceClass)) {
                    candidates.add(entry.getValue());
                }
            }
        }

        // Fjern mappere som ikke kan lage targetClass
        for (Iterator<TypeMapper<?, ?>> iterator = candidates.iterator(); iterator.hasNext(); ) {
            TypeMapper<?, ?> mapper = iterator.next();
            Class<?> toClass;
            switch (direction) {
                case D2W:
                    toClass = mapper.getWsapiClass();
                    break;
                case W2D:
                    toClass = mapper.getDomainClass();
                    break;
                default:
                    throw new MappingException("Invalid direction: " + direction);
            }
            if (!targetClass.isAssignableFrom(toClass)) {
                iterator.remove();
            }
        }

        if (candidates.isEmpty()) {
            return null;
        } else if (candidates.size() == 1) {
            return candidates.get(0);
        } else {
            return findClosestTypeMapper(candidates, sourceClass, direction);
        }
    }

    private TypeMapper<?, ?> findClosestTypeMapper(Collection<TypeMapper<?, ?>> candidates, Class mappableClass, Direction direction) {
        //map with natural ordering of keys
        TreeMap<Integer, TypeMapper<?, ?>> signedCandidates = new TreeMap<Integer, TypeMapper<?, ?>>();

        for (TypeMapper<?, ?> candidate : candidates) {
            final Class<?> mapperClass;
            Class<?> candidateClass;
            int weight = 0;

            if (Direction.D2W == direction) {
                mapperClass = candidate.getDomainClass();
            } else if (Direction.W2D == direction) {
                mapperClass = candidate.getWsapiClass();
            } else {
                throw new ImplementationException("Invalid direction: " + direction);
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

}