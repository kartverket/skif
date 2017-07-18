package no.statkart.skif.mapping;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.mapper.TypeMapper;
import no.statkart.skif.mapper.TypeMapperFactory;
import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.SnapshotVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.*;

/**
 * Mapper fra
 * <pre>
 *     &lt;xs:complexType name="...">
 *         &lt;xs:annotation>
 *             &lt;xs:appinfo>
 *                 &lt;IsDictionary xmlns="http://schemas.microsoft.com/2003/10/Serialization/">
 *                     true
 *                 &lt;/IsDictionary>
 *             &lt;/xs:appinfo>
 *         &lt;/xs:annotation>
 *         &lt;xs:sequence>
 *             &lt;xs:element minOccurs="0" maxOccurs="unbounded" name="entry">
 *                 &lt;xs:complexType>
 *                     &lt;xs:sequence>
 *                         &lt;xs:element name="key" type="...:Timestamp"/>
 *                         &lt;xs:element name="value" type="...:...BubbleId"/>
 *                     &lt;/xs:sequence>
 *                 &lt;/xs:complexType>
 *             &lt;/xs:element>
 *         &lt;/xs:sequence>
 *     &lt;/xs:complexType>
 * </pre>
 * til <code>Collection&lt;? extends BubbleId></code> og tilbake.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class SnapshotBubbleIdTypeMapperFactory implements TypeMapperFactory {
    private static final Logger logger = LoggerFactory.getLogger(SnapshotBubbleIdTypeMapperFactory.class);

    @Override
    public <WsapiT, DomainT> TypeMapper createTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        // Sjekk først om domenetypen er en Collection av BubbleId
        if (Collection.class.isAssignableFrom(domainTypeToken.getRawType()) && domainTypeToken.getType() instanceof ParameterizedType) {
            ParameterizedType domainType = (ParameterizedType) domainTypeToken.getType();
            TypeToken<?> domainElementTypeToken = TypeToken.of(domainType.getActualTypeArguments()[0]);
            if (BubbleId.class.isAssignableFrom(domainElementTypeToken.getRawType())) {
                // Sjekk så om wsapitypen ser ut som map/dictionary
                if (checkHasField(wsapiTypeToken.getRawType(), "entry")) {
                    //noinspection unchecked
                    return new SnapshotBubbleIdTypeMapper(wsapiTypeToken, domainTypeToken, domainElementTypeToken);
                }
            }
        }

        return null;
    }

    protected static boolean checkHasField(Class clazz, String fieldname) {
        try {
            clazz.getDeclaredField(fieldname);
            return true;
        } catch (NoSuchFieldException e) {
            return false;
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

    protected static class SnapshotBubbleIdTypeMapper<WsapiT, DomainT extends Collection<? extends AbstractBubbleId>> implements TypeMapper<WsapiT, DomainT> {
        private final TypeToken<WsapiT> wsapiTypeToken;
        private final TypeToken<? extends DomainT> domainTypeToken;
        private final TypeToken<?> domainElementTypeToken;

        protected Constructor<DomainT> collectionConstructor;

        private final Field entryField;
        private final Class<?> entryClass;
        private final Field keyField;
        private final Field valueField;

        private Mapping mapping;

        public SnapshotBubbleIdTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken, TypeToken<?> domainElementTypeToken) {
            this.wsapiTypeToken = wsapiTypeToken;
            this.domainTypeToken = resolveCollection(domainTypeToken);
            this.domainElementTypeToken = domainElementTypeToken;

            try {
                collectionConstructor = (Constructor<DomainT>) this.domainTypeToken.getRawType().getConstructor();
            } catch (NoSuchMethodException e) {
                logger.info("No constructor for {}. Mapping only from it.", domainTypeToken);
                collectionConstructor = null;
            }

            entryField = getField(wsapiTypeToken.getRawType(), "entry");

            ParameterizedType entryCollectionType = (ParameterizedType) entryField.getGenericType();
            entryClass = (Class<?>) entryCollectionType.getActualTypeArguments()[0];

            keyField = getField(entryClass, "key");
            valueField = getField(entryClass, "value");
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
        public Class<WsapiT> getWsapiClass() {
            return (Class<WsapiT>) wsapiTypeToken.getRawType();
        }

        @Override
        public Class<DomainT> getDomainClass() {
            return (Class<DomainT>) domainTypeToken.getRawType();
        }

        @Override
        public WsapiT mapDomainObject(DomainT source) {
            WsapiT target = createDictionary();

            List<Object> entries = new ArrayList<>(source.size());

            for (final AbstractBubbleId bubbleId : source) {
                TypeToken<?> wsBubbleIdTypeToken = getMapping().getMappingResolver().resolveTargetType(bubbleId.getClass(), TypeToken.of(valueField.getGenericType()));
                //noinspection unchecked
                BubbleIdTypeMapperFactory.BubbleIdTypeMapper bubbleIdTypeMapper = new BubbleIdTypeMapperFactory.BubbleIdTypeMapper(wsBubbleIdTypeToken.getRawType(), bubbleId.getClass(), new Provider<SnapshotVersion>() {
                    @Override
                    public SnapshotVersion get() {
                        return bubbleId.getSnapshotVersion();
                    }
                });

                Object wsSnapshotVersion = getMapping().d2w(bubbleId.getSnapshotVersion(), keyField.getType());
                Object wsBubbleId = bubbleIdTypeMapper.mapDomainObject(bubbleId);

                Object entry = createEntry();
                try {
                    keyField.set(entry, wsSnapshotVersion);
                    valueField.set(entry, wsBubbleId);
                } catch (IllegalAccessException e) {
                    throw new MappingException(e);
                }

                entries.add(entry);
            }

            try {
                entryField.set(target, entries);
            } catch (IllegalAccessException e) {
                throw new MappingException(e);
            }

            return target;
        }

        @Override
        public DomainT mapWsapiObject(WsapiT source) {
            DomainT target = createCollection();

            try {
                List entryList = (List) entryField.get(source);
                if (entryList != null) { // Tomme maps ser ut til å føre til dette
                    for (Object entry : entryList) {
                        Object wsSnapshotVersion = keyField.get(entry);
                        Object wsBubbleId = valueField.get(entry);

                        final SnapshotVersion snapshotVersion = getMapping().w2d(wsSnapshotVersion, SnapshotVersion.class);

                        TypeToken<?> bubbleIdTypeToken = getMapping().getMappingResolver().resolveTargetType(wsBubbleId.getClass(), domainElementTypeToken);
                        //noinspection unchecked
                        BubbleIdTypeMapperFactory.BubbleIdTypeMapper bubbleIdTypeMapper = new BubbleIdTypeMapperFactory.BubbleIdTypeMapper(wsBubbleId.getClass(), bubbleIdTypeToken.getRawType(), new Provider<SnapshotVersion>() {
                            @Override
                            public SnapshotVersion get() {
                                return snapshotVersion;
                            }
                        });

                        //noinspection unchecked
                        final AbstractBubbleId bubbleId = bubbleIdTypeMapper.mapWsapiObject(wsBubbleId);

                        //noinspection unchecked
                        ((Collection) target).add(bubbleId);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new MappingException(e);
            }

            return target;
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

        protected WsapiT createDictionary() {
            try {
                return (WsapiT) wsapiTypeToken.getRawType().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new MappingException("Failed to create map/dictionary", e);
            }
        }

        protected Object createEntry() {
            try {
                return entryClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new MappingException("Failed to create entry for map/dictionary", e);
            }
        }

        protected DomainT createCollection() {
            try {
                if (collectionConstructor == null) {
                    throw new MappingException("Mapping between " + wsapiTypeToken + " and " + domainTypeToken + " is only towards the left");
                } else {
                    return collectionConstructor.newInstance();
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new MappingException("Failed to create " + collectionConstructor.getDeclaringClass(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Failed to create " + collectionConstructor.getDeclaringClass(), e.getTargetException());
            }
        }
    }
}
