package no.statkart.skif.mapper;

import com.google.common.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Factory for mappere av {@link Set}, {@link List} og {@link Map}, samt {@link Array}. For Set og Map benyttes henholdsvis {@link HashSet}
 * og {@link HashMap}, mens for List benyttes {@link ArrayList}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class CollectionMapperFactory implements TypeMapperFactory {
    private static final Logger logger = LoggerFactory.getLogger(CollectionMapperFactory.class);

    @Override
    public <WsapiT, DomainT> TypeMapper<WsapiT, DomainT> createTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        if (Collection.class.isAssignableFrom(domainTypeToken.getRawType())) {
            if (Collection.class.isAssignableFrom(wsapiTypeToken.getRawType())) {
                //noinspection unchecked
                return (TypeMapper) collectionToCollection((TypeToken<? extends Collection>) wsapiTypeToken, (TypeToken<? extends Collection>) domainTypeToken);
            } else if (checkHasField(wsapiTypeToken.getRawType(), "item")) {
                //noinspection unchecked
                return (TypeMapper) collectionToXmlItems(wsapiTypeToken, (TypeToken<? extends Collection>) domainTypeToken);
            }
        } else if (Map.class.isAssignableFrom(domainTypeToken.getRawType())) {
            if (checkHasField(wsapiTypeToken.getRawType(), "entry")) {
                //noinspection unchecked
                return (TypeMapper) mapToXmlEntries(wsapiTypeToken, (TypeToken<? extends Map>) domainTypeToken);
            }
        } else if (domainTypeToken.getRawType().isArray()) {
            if (checkHasField(wsapiTypeToken.getRawType(), "item")) {
                return arrayToXmlItems(wsapiTypeToken, domainTypeToken);
            }
        }

        return null;
    }

    protected <WsapiT extends Collection<?>, DomainT extends Collection<?>> TypeMapper<WsapiT, DomainT> collectionToCollection(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        return new CollectionToCollectionTypeMapper<WsapiT, DomainT>(wsapiTypeToken, domainTypeToken);
    }

    protected <WsapiT, DomainT extends Collection<?>> TypeMapper<WsapiT, DomainT> collectionToXmlItems(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        return new CollectionToXmlTypeMapper<WsapiT, DomainT>(wsapiTypeToken, domainTypeToken);
    }

    protected <WsapiT, DomainT extends Map<?, ?>> TypeMapper<WsapiT, DomainT> mapToXmlEntries(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        return new MapToXmlTypeMapper<WsapiT, DomainT>(wsapiTypeToken, domainTypeToken);
    }

    protected <WsapiT, DomainT> TypeMapper<WsapiT, DomainT> arrayToXmlItems(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        return new ArrayToXmlTypeMapper<WsapiT, DomainT>(wsapiTypeToken, domainTypeToken);
    }

    protected boolean checkHasField(Class clazz, String fieldname) {
        try {
            clazz.getDeclaredField(fieldname);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
        }
    }

    protected static class ArrayToXmlTypeMapper<WsapiT, DomainT> implements TypeMapper<WsapiT, DomainT> {
        protected final TypeToken<WsapiT> wsapiTypeToken;
        protected final TypeToken<DomainT> domainTypeToken;

        protected final Field itemField;
        protected final Class<?> itemClass;

        private Mapping mapping;

        protected ArrayToXmlTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
            this.wsapiTypeToken = wsapiTypeToken;
            this.domainTypeToken = domainTypeToken;

            itemField = getField(wsapiTypeToken.getRawType(), "item");

            ParameterizedType itemCollectionType = (ParameterizedType) itemField.getGenericType();
            itemClass = (Class<?>) itemCollectionType.getActualTypeArguments()[0];
        }

        protected WsapiT createXmlList() {
            try {
                return (WsapiT) wsapiTypeToken.getRawType().newInstance();
            } catch (InstantiationException e) {
                throw new MappingException("Failed to create list", e);
            } catch (IllegalAccessException e) {
                throw new MappingException("Failed to create list", e);
            }
        }

        @Override
        public Mapping getMapping() {
            return mapping;
        }

        @Override
        public void setMapping(Mapping mapping) {
            this.mapping = mapping;
        }

        @Override
        public WsapiT mapDomainObject(DomainT source) {
            WsapiT target = createXmlList();

            final int length = Array.getLength(source);
            List<Object> items = new ArrayList<Object>(length);

            for (int i = 0; i < length; ++i) {
                items.add(getMapping().d2w(Array.get(source, i), domainTypeToken.getComponentType().getType(), itemClass));
            }

            try {
                itemField.set(target, items);
            } catch (IllegalAccessException e) {
                throw new MappingException(e);
            }

            return target;
        }

        @Override
        public DomainT mapWsapiObject(WsapiT source) {
            try {
                List items = (List) itemField.get(source);

                if (items != null) {
                    DomainT target = (DomainT) Array.newInstance(domainTypeToken.getComponentType().getRawType(), items.size());

                    for (int i = 0; i < items.size(); i++) {
                        Object item = items.get(i);
                        Array.set(target, i, mapping.w2d(item, itemClass, domainTypeToken.getComponentType().getType()));
                    }

                    return target;
                } else {
                    return (DomainT) Array.newInstance(domainTypeToken.getComponentType().getRawType(), 0);
                }
            } catch (IllegalAccessException e) {
                throw new MappingException(e);
            }
        }

        @Override
        public Class<WsapiT> getWsapiClass() {
            return (Class<WsapiT>) wsapiTypeToken.getRawType();
        }

        @Override
        public Class<DomainT> getDomainClass() {
            return (Class<DomainT>) domainTypeToken.getRawType();
        }
    }

    protected static class CollectionToCollectionTypeMapper<WsapiT extends Collection<?>, DomainT extends Collection<?>> implements TypeMapper<WsapiT, DomainT> {
        protected final TypeToken<? extends WsapiT> wsapiTypeToken;
        protected final TypeToken<? extends DomainT> domainTypeToken;

        protected final Type wsapiElementType;
        protected final Type domainElementType;

        protected Constructor<WsapiT> wsapiCollectionConstructor;
        protected Constructor<DomainT> domainCollectionConstructor;
        private Mapping mapping;

        protected CollectionToCollectionTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
            this.wsapiTypeToken = resolveCollection(wsapiTypeToken);
            this.domainTypeToken = resolveCollection(domainTypeToken);

            try {
                wsapiCollectionConstructor = (Constructor<WsapiT>) this.wsapiTypeToken.getRawType().getConstructor();
            } catch (NoSuchMethodException e) {
                logger.info("No constructor for {}. Mapping only from it.", wsapiTypeToken);
                wsapiCollectionConstructor = null;
            }
            try {
                domainCollectionConstructor = (Constructor<DomainT>) this.domainTypeToken.getRawType().getConstructor();
            } catch (NoSuchMethodException e) {
                logger.info("No constructor for {}. Mapping only from it.", domainTypeToken);
                domainCollectionConstructor = null;
            }

            ParameterizedType wsapiCollectionType = (ParameterizedType) wsapiTypeToken.getSupertype(Collection.class).getType();
            wsapiElementType = wsapiCollectionType.getActualTypeArguments()[0];

            ParameterizedType domainCollectionType = (ParameterizedType) domainTypeToken.getSupertype(Collection.class).getType();
            domainElementType = domainCollectionType.getActualTypeArguments()[0];
        }

        protected <T extends Collection<?>> TypeToken<? extends T> resolveCollection(TypeToken<T> typeToken) {
            TypeToken<? extends T> retVal = typeToken;

            if (retVal.getRawType().isInterface()) {
                if (retVal.getRawType().equals(Collection.class) || List.class.isAssignableFrom(retVal.getRawType())) {
                    retVal = retVal.getSubtype(ArrayList.class);
                } else if (Set.class.isAssignableFrom(retVal.getRawType())) {
                    retVal = retVal.getSubtype(HashSet.class);
                } else {
                    throw new MappingException("Unknown Collection type: " + retVal);
                }
            }

            return retVal;
        }

        protected WsapiT createWsapiCollection() {
            try {
                if (wsapiCollectionConstructor == null) {
                    throw new MappingException("Mapping between " + wsapiTypeToken + " and " + domainTypeToken + " is only towards the right");
                } else {
                    return wsapiCollectionConstructor.newInstance();
                }
            } catch (InstantiationException e) {
                throw new MappingException("Failed to create " + wsapiCollectionConstructor.getDeclaringClass(), e);
            } catch (IllegalAccessException e) {
                throw new MappingException("Failed to create " + wsapiCollectionConstructor.getDeclaringClass(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Failed to create " + wsapiCollectionConstructor.getDeclaringClass(), e);
            }
        }

        protected DomainT createDomainCollection() {
            try {
                if (domainCollectionConstructor == null) {
                    throw new MappingException("Mapping between " + wsapiTypeToken + " and " + domainTypeToken + " is only towards the left");
                } else {
                    return domainCollectionConstructor.newInstance();
                }
            } catch (InstantiationException e) {
                throw new MappingException("Failed to create " + domainCollectionConstructor.getDeclaringClass(), e);
            } catch (IllegalAccessException e) {
                throw new MappingException("Failed to create " + domainCollectionConstructor.getDeclaringClass(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Failed to create " + domainCollectionConstructor.getDeclaringClass(), e);
            }
        }

        @Override
        public Mapping getMapping() {
            return mapping;
        }

        @Override
        public void setMapping(Mapping mapping) {
            this.mapping = mapping;
        }

        @Override
        public WsapiT mapDomainObject(DomainT source) {
            WsapiT target = null;

            if (source != null) {
                target = createWsapiCollection();

                for (Object item : source) {
                    ((Collection) target).add(getMapping().d2w(item, domainElementType, wsapiElementType));
                }
            }

            return target;
        }

        @Override
        public DomainT mapWsapiObject(WsapiT source) {
            DomainT target = null;

            if (source != null) {
                target = createDomainCollection();

                for (Object item : source) {
                    ((Collection) target).add(getMapping().w2d(item, wsapiElementType, domainElementType));
                }
            }

            return target;
        }

        @Override
        public Class<WsapiT> getWsapiClass() {
            return (Class<WsapiT>) wsapiTypeToken.getRawType();
        }

        @Override
        public Class<DomainT> getDomainClass() {
            return (Class<DomainT>) domainTypeToken.getRawType();
        }
    }

    protected static class CollectionToXmlTypeMapper<WsapiT, DomainT extends Collection<?>> implements TypeMapper<WsapiT, DomainT> {
        protected final TypeToken<?> wsapiTypeToken;
        protected final TypeToken<? extends DomainT> domainTypeToken;

        protected final Type domainElementType;

        protected final Field itemField;
        protected final Class<?> itemClass;

        protected Constructor<DomainT> collectionConstructor;

        private Mapping mapping = null;

        protected CollectionToXmlTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
            this.wsapiTypeToken = wsapiTypeToken;
            this.domainTypeToken = resolveCollection(domainTypeToken);

            try {
                collectionConstructor = (Constructor<DomainT>) this.domainTypeToken.getRawType().getConstructor();
            } catch (NoSuchMethodException e) {
                logger.info("No constructor for {}. Mapping only from it.", domainTypeToken);
                collectionConstructor = null;
            }

            ParameterizedType collectionType = (ParameterizedType) domainTypeToken.getSupertype(Collection.class).getType();
            domainElementType = collectionType.getActualTypeArguments()[0];

            itemField = getField(wsapiTypeToken.getRawType(), "item");

            ParameterizedType itemCollectionType = (ParameterizedType) itemField.getGenericType();
            itemClass = (Class<?>) itemCollectionType.getActualTypeArguments()[0];
        }

        protected <T extends Collection<?>> TypeToken<? extends T> resolveCollection(TypeToken<T> typeToken) {
            TypeToken<? extends T> retVal = typeToken;

            if (retVal.getRawType().isInterface()) {
                if (retVal.getRawType().equals(Collection.class) || List.class.isAssignableFrom(retVal.getRawType())) {
                    retVal = retVal.getSubtype(ArrayList.class);
                } else if (Set.class.isAssignableFrom(retVal.getRawType())) {
                    retVal = retVal.getSubtype(HashSet.class);
                } else {
                    throw new MappingException("Unknown Collection type: " + retVal);
                }
            }

            return retVal;
        }

        protected WsapiT createXmlList() {
            try {
                return (WsapiT) wsapiTypeToken.getRawType().newInstance();
            } catch (InstantiationException e) {
                throw new MappingException("Failed to create list", e);
            } catch (IllegalAccessException e) {
                throw new MappingException("Failed to create list", e);
            }
        }

        protected DomainT createCollection() {
            try {
                if (collectionConstructor == null) {
                    throw new MappingException("Mapping between " + wsapiTypeToken + " and " + domainTypeToken + " is only towards the left");
                } else {
                    return collectionConstructor.newInstance();
                }
            } catch (InstantiationException e) {
                throw new MappingException("Failed to create " + collectionConstructor.getDeclaringClass(), e);
            } catch (IllegalAccessException e) {
                throw new MappingException("Failed to create " + collectionConstructor.getDeclaringClass(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Failed to create " + collectionConstructor.getDeclaringClass(), e);
            }
        }

        @Override
        public Mapping getMapping() {
            return mapping;
        }

        @Override
        public void setMapping(Mapping mapping) {
            this.mapping = mapping;
        }

        @Override
        public WsapiT mapDomainObject(DomainT source) {
            WsapiT target = createXmlList();

            List<Object> items = new ArrayList<Object>(source.size());
            for (Object o : source) {
                items.add(mapping.d2w(o, domainElementType, itemClass));
            }

            try {
                itemField.set(target, items);
            } catch (IllegalAccessException e) {
                throw new MappingException(e);
            }

            return target;
        }

        @Override
        public DomainT mapWsapiObject(WsapiT source) {
            try {
                List items = (List) itemField.get(source);

                if (items != null) {
                    DomainT target = createCollection();

                    for (Object item : items) {
                        ((Collection) target).add(mapping.w2d(item, itemClass, domainElementType));
                    }

                    return target;
                } else {
                    return createCollection();
                }
            } catch (IllegalAccessException e) {
                throw new MappingException(e);
            }
        }

        @Override
        public Class<WsapiT> getWsapiClass() {
            return (Class<WsapiT>) wsapiTypeToken.getRawType();
        }

        @Override
        public Class<DomainT> getDomainClass() {
            return (Class<DomainT>) domainTypeToken.getRawType();
        }
    }

    protected static class MapToXmlTypeMapper<WsapiT, DomainT extends Map<?, ?>> implements TypeMapper<WsapiT, DomainT> {
        private final TypeToken<?> wsapiTypeToken;
        private final TypeToken<? extends DomainT> domainTypeToken;

        private final Type domainKeyType;
        private final Type domainValueType;

        private final Field entryField;
        private final Class<?> entryClass;
        private final Field keyField;
        private final Field valueField;

        private Mapping mapping = null;

        protected MapToXmlTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
            this.wsapiTypeToken = wsapiTypeToken;
            this.domainTypeToken = resolveMap(domainTypeToken);

            ParameterizedType mapType = (ParameterizedType) domainTypeToken.getSupertype(Map.class).getType();
            domainKeyType = mapType.getActualTypeArguments()[0];
            domainValueType = mapType.getActualTypeArguments()[1];

            entryField = getField(wsapiTypeToken.getRawType(), "entry");

            ParameterizedType entryCollectionType = (ParameterizedType) entryField.getGenericType();
            entryClass = (Class<?>) entryCollectionType.getActualTypeArguments()[0];

            keyField = getField(entryClass, "key");
            valueField = getField(entryClass, "value");
        }

        protected WsapiT createDictionary() {
            try {
                return (WsapiT) wsapiTypeToken.getRawType().newInstance();
            } catch (InstantiationException e) {
                throw new MappingException("Failed to create map/dictionary", e);
            } catch (IllegalAccessException e) {
                throw new MappingException("Failed to create map/dictionary", e);
            }
        }

        protected Object createEntry() {
            try {
                return entryClass.newInstance();
            } catch (InstantiationException e) {
                throw new MappingException("Failed to create entry for map/dictionary", e);
            } catch (IllegalAccessException e) {
                throw new MappingException("Failed to create entry for map/dictionary", e);
            }
        }

        private DomainT createMap() {
            try {
                return (DomainT) domainTypeToken.getRawType().newInstance();
            } catch (InstantiationException e) {
                throw new MappingException("Failed to create map", e);
            } catch (IllegalAccessException e) {
                throw new MappingException("Failed to create map", e);
            }
        }

        protected <T extends Map<?, ?>> TypeToken<? extends T> resolveMap(TypeToken<T> targetType) {
            TypeToken<? extends T> retVal;
            final Class<?> targetRawType = targetType.getRawType();

            retVal = targetType;
            if (targetRawType.isInterface()) {
                if (SortedMap.class.isAssignableFrom(targetRawType)) {
                    retVal = retVal.getSubtype(TreeMap.class);
                } else {
                    // Antar HashMap
                    retVal = retVal.getSubtype(HashMap.class);
                }
            }

            return retVal;
        }

        @Override
        public Mapping getMapping() {
            return mapping;
        }

        @Override
        public void setMapping(Mapping mapping) {
            this.mapping = mapping;
        }

        @Override
        public WsapiT mapDomainObject(DomainT source) {
            WsapiT target = createDictionary();

            try {
                List<Object> entryList = new ArrayList<Object>();
                entryField.set(target, entryList);

                for (Map.Entry<?, ?> sourceEntry : source.entrySet()) {
                    Object targetKey = mapping.d2w(sourceEntry.getKey(), domainKeyType, keyField.getGenericType());
                    Object targetValue = mapping.d2w(sourceEntry.getValue(), domainValueType, valueField.getGenericType());
                    Object targetEntry = createEntry();
                    keyField.set(targetEntry, targetKey);
                    valueField.set(targetEntry, targetValue);
                    entryList.add(targetEntry);
                }
            } catch (IllegalAccessException e) {
                throw new MappingException(e);
            }

            return target;
        }

        @Override
        public DomainT mapWsapiObject(WsapiT source) {
            DomainT target = createMap();

            try {
                List entryList = (List) entryField.get(source);
                if (entryList != null) { // Tomme maps ser ut til å føre til dette
                    for (Object entry : entryList) {
                        final Object key, value;
                        key = mapping.w2d(keyField.get(entry), keyField.getGenericType(), domainKeyType);
                        value = mapping.w2d(valueField.get(entry), valueField.getGenericType(), domainValueType);
                        ((Map) target).put(key, value);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new MappingException(e);
            }

            return target;
        }

        @Override
        public Class<WsapiT> getWsapiClass() {
            return (Class<WsapiT>) wsapiTypeToken.getRawType();
        }

        @Override
        public Class<DomainT> getDomainClass() {
            return (Class<DomainT>) domainTypeToken.getRawType();
        }
    }

    protected static Field getField(Class<?> c, String name) {
        try {
            Field declaredField = c.getDeclaredField(name);
            declaredField.setAccessible(true);
            return declaredField;
        } catch (NoSuchFieldException e) {
            throw new MappingException("No field called " + name + " in " + c);
        }
    }
}
