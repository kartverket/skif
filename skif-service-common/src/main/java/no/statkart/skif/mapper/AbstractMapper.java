package no.statkart.skif.mapper;


import com.google.common.base.Supplier;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import no.statkart.skif.exception.ImplementationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Henrik Fredholm
 * @author Tor Egil R. Strand
 */
public abstract class AbstractMapper<M extends Mapping> implements InvocationHandler, MappingBase {
    private static final Logger logger = LoggerFactory.getLogger(AbstractMapper.class);

    private MappingResolver mappingResolver = null;

    static enum Direction {
        /**
         * mapping from domain to webserivce classes
         */
        D2W,
        /**
         * mapping from webservice to domain classes
         */
        W2D
    }

    protected static class MapperKey {
        private final TypeToken<?> wsapiType, domainType;

        public MapperKey(TypeToken<?> wsapiType, TypeToken<?> domainType) {
            this.wsapiType = wsapiType;
            this.domainType = domainType;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MapperKey mapperKey = (MapperKey) o;

            return wsapiType.equals(mapperKey.wsapiType) && domainType.equals(mapperKey.domainType);
        }

        @Override
        public int hashCode() {
            int result = wsapiType.hashCode();
            result = 31 * result + domainType.hashCode();
            return result;
        }

        @Override
        public String toString() {
            return "MapperKey{" +
                    "wsapiType=" + wsapiType +
                    ", domainType=" + domainType +
                    '}';
        }
    }

    private final ListMultimap<Class<?>, TypeMapper<?, ?>> mappersByDomainClass = ArrayListMultimap.create();
    private final ListMultimap<Class<?>, TypeMapper<?, ?>> mappersByWsapiClass = ArrayListMultimap.create();

    private final List<TypeMapperFactory> typeMapperFactories = new ArrayList<TypeMapperFactory>();

    protected final Map<MapperKey, TypeMapper<?, ?>> mapperCache = new ConcurrentHashMap<MapperKey, TypeMapper<?, ?>>();

    private final M thisMapping;

    private final ThreadLocal<Integer> recurseLevel = new ThreadLocal<Integer>() {
        @Override
        protected Integer initialValue() {
            return 0;
        }
    };
    private final ThreadLocal<MappedFieldsTracker> mappedFieldsTracker = new ThreadLocal<MappedFieldsTracker>() {
        @Override
        protected MappedFieldsTracker initialValue() {
            return new MappedFieldsTracker();
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

    protected void addMapperFactory(TypeMapperFactory typeMapperFactory) {
        typeMapperFactories.add(typeMapperFactory);
    }

    protected void setMappingResolver(MappingResolver mappingResolver) {
        this.mappingResolver = mappingResolver;
    }

    @Override
    public MappingResolver getMappingResolver() {
        return mappingResolver;
    }

    public M getMapping() {
        return thisMapping;
    }

    @Override
    public void registerTarget(Object source, Object target) {
        mappedFieldsTracker.get().put(source, target);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Object target;

        if (method.getName().equals("w2d")) {
            //try/finally for å vedlikeholde en teller for hvor dypt i rekursjonsgrafen vi er.
            //Dersom vi er på toppen så kan vi clearMappedFields fra DefaultTypeMapper.
            try {
                int i = recurseLevel.get();
                recurseLevel.set(++i);
                target = w2d(method, args);
            } finally {
                int i = recurseLevel.get() - 1;
                if (i == 0) {
                    // Å holde på ikke-primitiver i ThreadLocal kan lett medføre minnelekasje
                    mappedFieldsTracker.remove();
                    // Tar denne også, for å gjøre det likt
                    recurseLevel.remove();
                } else {
                    recurseLevel.set(i);
                }
            }
        } else if (method.getName().equals("d2w")) {
            //try/finally for å vedlikeholde en teller for hvor dypt i rekursjonsgrafen vi er.
            //Dersom vi er på toppen så kan vi clearMappedFields fra DefaultTypeMapper.
            try {
                int i = recurseLevel.get();
                recurseLevel.set(++i);
                target = d2w(method, args);
            } finally {
                int i = recurseLevel.get() - 1;
                if (i == 0) {
                    // Å holde på ikke-primitiver i ThreadLocal kan lett medføre minnelekasje
                    mappedFieldsTracker.remove();
                    // Tar denne også, for å gjøre det likt
                    recurseLevel.remove();
                } else {
                    recurseLevel.set(i);
                }
            }
        } else {
            target = method.invoke(this, args);
            //throw new SkifImplementationException("Unexpected method call: " + method.toGenericString());
        }

        return target;
    }

    protected Object d2w(Method method, Object[] args) {
        if (args.length == 1) {
            return d2w(args[0], method.getGenericParameterTypes()[0], method.getGenericReturnType());
        } else if (args.length == 2) {
            final Type toType;
            if (args[1] instanceof TypeLiteral) {
                toType = ((TypeLiteral) args[1]).getType();
            } else {
                toType = (Type) args[1];
            }
            return d2w(args[0], args[0] == null ? Object.class : args[0].getClass(), toType);
        } else if (args.length == 3) {
            final Type fromType, toType;
            if (args[1] instanceof TypeLiteral) {
                fromType = ((TypeLiteral) args[1]).getType();
            } else {
                fromType = (Type) args[1];
            }
            if (args[2] instanceof TypeLiteral) {
                toType = ((TypeLiteral) args[2]).getType();
            } else {
                toType = (Type) args[2];
            }
            return d2w(args[0], fromType, toType);
        } else {
            throw new ImplementationException("No such method: " + method.toString());
        }
    }

    protected Object d2w(Object source, Type sourceType, Type targetType) {
        Object target = null;
        if (source != null) {
            TypeToken<?> targetTypeToken = resolveTarget(targetType);
            TypeToken<?> sourceTypeToken = resolveSubClass(source, sourceType);
            MappedFieldsTracker tracker = mappedFieldsTracker.get();

            target = tracker.getMappedValue(source, targetTypeToken.getRawType());
            if (target == null) {
                if (sourceTypeToken.isArray() && targetTypeToken.isArray()) {
                    int length = Array.getLength(source);
                    //noinspection ConstantConditions
                    target = Array.newInstance(targetTypeToken.getComponentType().getRawType(), length);
                    for (int i = 0; i < length; ++i) {
                        //noinspection ConstantConditions
                        Array.set(target, i, thisMapping.d2w(Array.get(source, i), sourceTypeToken.getComponentType().getType(), targetTypeToken.getComponentType().getType()));
                    }
                } else {
                    final TypeToken<?> resolvedTargetTypeToken;
                    final MapperKey mapperKey;

                    if (mappingResolver != null) {
                        // Prøv å finne ut mer nøyaktig hva måltypen er
                        resolvedTargetTypeToken = mappingResolver.resolveTargetType(sourceTypeToken.getRawType(), targetTypeToken);
                        logger.debug("Resolving {} for {} to {}", new Object[]{targetTypeToken, sourceTypeToken, resolvedTargetTypeToken});
                        mapperKey = new MapperKey(resolvedTargetTypeToken, sourceTypeToken);
                    } else {
                        resolvedTargetTypeToken = targetTypeToken;
                        mapperKey = new MapperKey(targetTypeToken, sourceTypeToken);
                    }

                    TypeMapper typeMapper;
                    if (mapperCache.containsKey(mapperKey)) {
                        typeMapper = mapperCache.get(mapperKey);
                    } else {
                        try {
                            // Bruker ikke resolvedTargetTypeToken her grunnet bakoverkompatibilitet
                            typeMapper = findMapper(sourceTypeToken.getRawType(), targetTypeToken.getRawType(), Direction.D2W);
                            if (typeMapper == null) {
                                for (TypeMapperFactory typeMapperFactory : typeMapperFactories) {
                                    typeMapper = typeMapperFactory.createTypeMapper(resolvedTargetTypeToken, sourceTypeToken);
                                    if (typeMapper != null) {
                                        logger.debug("{} provided {} for mapping between {} and {}", new Object[]{typeMapperFactory, typeMapper, mapperKey.wsapiType, mapperKey.domainType});
                                        typeMapper.setMapping(thisMapping);
                                        break;
                                    }
                                }
                            } else {
                                logger.debug("Using TypeMapper<{}, {}> for mapping between {} and {}", new Object[]{typeMapper.getWsapiClass(), typeMapper.getDomainClass(), mapperKey.wsapiType, mapperKey.domainType});
                            }

                            if (typeMapper != null) {
                                mapperCache.put(mapperKey, typeMapper);
                            }
                        } catch (RuntimeException e) {
                            throw new MappingException("Error mapping from " + sourceTypeToken + " to " + targetTypeToken, e);
                        }
                    }
                    if (typeMapper == null) {
                        throw new MappingException(String.format("Mapper[%s] could not map from %s to %s", this.getClass().getName(), sourceTypeToken, targetTypeToken));
                    }

                    try {
                        //noinspection unchecked
                        target = typeMapper.mapDomainObject(source);
                    } catch (RuntimeException e) {
                        throw new MappingException("Error mapping from " + sourceTypeToken + " to " + targetTypeToken, e);
                    }
                }
            }
        }
        return target;
    }

    /**
     * Workaround for manglende funksjonalitet i Guava. Den takler ikke at man går fra KodeId&lt;?&gt; til AKodeId.
     */
    private static TypeToken<?> resolveSubClass(Object source, Type sourceType) {
        if (sourceType instanceof TypeVariable) {
            return TypeToken.of(source.getClass());
        }
        if (sourceType instanceof Class && ((Class) sourceType).isPrimitive()) {
            return TypeToken.of(sourceType);
        }
        TypeVariable[] sourceClassParameters = source.getClass().getTypeParameters();
        if (sourceClassParameters.length == 0) {
            return TypeToken.of(source.getClass());
        }
        if (sourceType instanceof ParameterizedType) {
            ParameterizedType parameterizedSourceType = (ParameterizedType) sourceType;
            Type[] actualTypeArguments = parameterizedSourceType.getActualTypeArguments();
            if (actualTypeArguments.length == 1 && actualTypeArguments[0] instanceof WildcardType && sourceClassParameters.length == 1) {
                // Antar at sourceType er en BubbleId<?>, mens
                return TypeToken.of(Types.newParameterizedTypeWithOwner(source.getClass().getEnclosingClass(), source.getClass(), actualTypeArguments[0]));
            }
        }

        return TypeToken.of(sourceType).getSubtype(source.getClass());
    }

    /**
     * Workaround for hvis target type er en type variable.
     */
    private static TypeToken<?> resolveTarget(Type targetType) {
        if (targetType instanceof TypeVariable) {
            TypeVariable typeVariable = (TypeVariable) targetType;
            return TypeToken.of(typeVariable.getBounds()[0]);
        } else {
            return TypeToken.of(targetType);
        }
    }

    protected Object w2d(Method method, Object[] args) {
        if (args.length == 1) {
            return w2d(args[0], method.getGenericParameterTypes()[0], method.getGenericReturnType());
        } else if (args.length == 2) {
            final Type toType;
            if (args[1] instanceof TypeLiteral) {
                toType = ((TypeLiteral) args[1]).getType();
            } else {
                toType = (Type) args[1];
            }
            return w2d(args[0], args[0] == null ? Object.class : args[0].getClass(), toType);
        } else if (args.length == 3) {
            final Type fromType, toType;
            if (args[1] instanceof TypeLiteral) {
                fromType = ((TypeLiteral) args[1]).getType();
            } else {
                fromType = (Type) args[1];
            }
            if (args[2] instanceof TypeLiteral) {
                toType = ((TypeLiteral) args[2]).getType();
            } else {
                toType = (Type) args[2];
            }
            return w2d(args[0], fromType, toType);
        } else {
            throw new ImplementationException("No such method: " + method.toString());
        }
    }

    protected Object w2d(Object source, Type sourceType, Type targetType) {
        Object target = null;
        if (source != null) {
            TypeToken<?> targetTypeToken = resolveTarget(targetType);
            TypeToken<?> sourceTypeToken = resolveSubClass(source, sourceType);
            MappedFieldsTracker tracker = mappedFieldsTracker.get();

            target = tracker.getMappedValue(source, targetTypeToken.getRawType());
            if (target == null) {
                if (sourceTypeToken.isArray() && targetTypeToken.isArray()) {
                    int length = Array.getLength(source);
                    //noinspection ConstantConditions
                    target = Array.newInstance(targetTypeToken.getComponentType().getRawType(), length);
                    for (int i = 0; i < length; ++i) {
                        //noinspection ConstantConditions
                        Array.set(target, i, thisMapping.w2d(Array.get(source, i), sourceTypeToken.getComponentType().getType(), targetTypeToken.getComponentType().getType()));
                    }
                } else {
                    final TypeToken<?> resolvedTargetTypeToken;
                    final MapperKey mapperKey;

                    if (mappingResolver != null) {
                        // Prøv å finne ut mer nøyaktig hva måltypen er
                        resolvedTargetTypeToken = mappingResolver.resolveTargetType(sourceTypeToken.getRawType(), targetTypeToken);
                        logger.debug("Resolving {} for {} to {}", new Object[]{targetTypeToken, sourceTypeToken, resolvedTargetTypeToken});
                        mapperKey = new MapperKey(sourceTypeToken, resolvedTargetTypeToken);
                    } else {
                        resolvedTargetTypeToken = targetTypeToken;
                        mapperKey = new MapperKey(sourceTypeToken, targetTypeToken);
                    }

                    TypeMapper typeMapper;
                    if (mapperCache.containsKey(mapperKey)) {
                        typeMapper = mapperCache.get(mapperKey);
                    } else {
                        try {
                            // Bruker ikke resolvedTargetTypeToken her grunnet bakoverkompatibilitet
                            typeMapper = findMapper(sourceTypeToken.getRawType(), targetTypeToken.getRawType(), Direction.W2D);
                            if (typeMapper == null) {
                                for (TypeMapperFactory typeMapperFactory : typeMapperFactories) {
                                    typeMapper = typeMapperFactory.createTypeMapper(sourceTypeToken, resolvedTargetTypeToken);
                                    if (typeMapper != null) {
                                        logger.debug("{} provided {} for mapping between {} and {}", new Object[]{typeMapperFactory, typeMapper, mapperKey.wsapiType, mapperKey.domainType});
                                        typeMapper.setMapping(thisMapping);
                                        break;
                                    }
                                }
                            } else {
                                logger.debug("Using TypeMapper<{}, {}> for mapping between {} and {}", new Object[]{typeMapper.getWsapiClass(), typeMapper.getDomainClass(), mapperKey.wsapiType, mapperKey.domainType});
                            }

                            if (typeMapper != null) {
                                mapperCache.put(mapperKey, typeMapper);
                            }
                        } catch (RuntimeException e) {
                            throw new MappingException("Error mapping from " + sourceTypeToken + " to " + targetTypeToken, e);
                        }
                    }
                    if (typeMapper == null) {
                        throw new MappingException(String.format("Mapper[%s] could not map from %s to %s", this.getClass().getName(), sourceTypeToken, targetTypeToken));
                    }

                    try {
                        //noinspection unchecked
                        target = typeMapper.mapWsapiObject(source);
                    } catch (RuntimeException e) {
                        throw new MappingException("Error mapping from " + sourceTypeToken + " to " + targetTypeToken, e);
                    }
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

        // Finn mappere som kan gå fra sourceClass eller en superklasse av dette
        List<TypeMapper<?, ?>> candidates = new ArrayList<TypeMapper<?, ?>>();

        for (Map.Entry<Class<?>, TypeMapper<?, ?>> entry : mapOfMappers.entries()) {
            if (entry.getKey().isAssignableFrom(sourceClass)) {
                candidates.add(entry.getValue());
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
            return findClosestTypeMapper(candidates, sourceClass, targetClass, direction);
        }
    }

    static TypeMapper<?, ?> findClosestTypeMapper(Collection<TypeMapper<?, ?>> candidates, Class mappableClass, Class requestedClass, Direction direction) {
        //map with natural ordering of keys
        ListMultimap<TypeMapperMatch, TypeMapper<?, ?>> signedCandidates = Multimaps.newListMultimap(new TreeMap<TypeMapperMatch, Collection<TypeMapper<?, ?>>>(), new Supplier<List<TypeMapper<?, ?>>>() {
            @Override
            public List<TypeMapper<?, ?>> get() {
                return new ArrayList<TypeMapper<?, ?>>();
            }
        });

        for (TypeMapper<?, ?> candidate : candidates) {
            final Class<?> fromClass, toClass;
            Class<?> candidateClass;
            int fromDistance = 0;
            int toDistance = 0;

            if (Direction.D2W == direction) {
                fromClass = candidate.getDomainClass();
                toClass = candidate.getWsapiClass();
            } else if (Direction.W2D == direction) {
                fromClass = candidate.getWsapiClass();
                toClass = candidate.getDomainClass();
            } else {
                throw new ImplementationException("Invalid direction: " + direction);
            }

            candidateClass = mappableClass;
            while (!fromClass.equals(candidateClass) && !Object.class.equals(candidateClass)) {
                fromDistance++;
                candidateClass = candidateClass.getSuperclass();
            }

            candidateClass = requestedClass;
            while (!toClass.equals(candidateClass) && !Object.class.equals(candidateClass)) {
                toDistance++;
                candidateClass = candidateClass.getSuperclass();
            }

            signedCandidates.put(new TypeMapperMatch(fromDistance, toDistance), candidate);
        }

        Collection<TypeMapper<?, ?>> best = signedCandidates.asMap().values().iterator().next();


        if (best.size() > 1) {
            TypeMapper<?, ?> prev = null;
            for (TypeMapper<?, ?> typeMapper : best) {
                if (prev != null && prev.getClass() != typeMapper.getClass()) {
                    throw new ImplementationException("Several defined mappers of different types found for mapping of class of type " + mappableClass);
                }
                prev = typeMapper;
            }
        }
        // Returner den første, da der er den per definisjon vi ønsker når de er like.
        return best.iterator().next();
    }

    /**
     * Typemappere rangeres etter hvor fra match de er. Først og fremst foretrekkes den typemapper som er nærmest
     * klassen det mappes fra. Dersom det her blir uavgjort mellom to typemappere på dette punktet, så velges den
     * typemapper som mapper til det som er nærmest ønsket klasse (SKIF-383). Det er allrede på forhånd sikret at
     * typemapper mapper fra (en superklasse av) klassen som skal mappes, til (en subtype av) klassen det skal mappes
     * til.
     */
    private static class TypeMapperMatch implements Comparable<TypeMapperMatch> {
        private final int fromDistance;
        private final int toDistance;

        private TypeMapperMatch(int fromDistance, int toDistance) {
            this.fromDistance = fromDistance;
            this.toDistance = toDistance;
        }

        @Override
        public int compareTo(TypeMapperMatch o) {
            int relasjon = new Integer(fromDistance).compareTo(o.fromDistance);
            if (relasjon == 0) {
                relasjon = new Integer(toDistance).compareTo(o.toDistance);
            }
            return relasjon;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TypeMapperMatch that = (TypeMapperMatch) o;

            return fromDistance == that.fromDistance && toDistance == that.toDistance;
        }

        @Override
        public int hashCode() {
            int result = fromDistance;
            result = 31 * result + toDistance;
            return result;
        }
    }
}
