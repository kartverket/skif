package no.statkart.skif.mapping;

import com.google.common.reflect.TypeToken;
import com.google.inject.TypeLiteral;
import no.statkart.skif.mapper.*;
import no.statkart.skif.store.Transfer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.*;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * TypeMapperFactory for {@link Transfer}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class TransferTypeMapperFactory implements TypeMapperFactory {
    @Override
    public <WsapiT, DomainT> TypeMapper createTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        if (Transfer.class.isAssignableFrom(domainTypeToken.getRawType())) {
            // Sjekk om result er inline eller ikke
            try {
                PropertyDescriptor resultProperty = new PropertyDescriptor("result", wsapiTypeToken.getRawType());
                //noinspection unchecked
                return new ExternalTransferTypeMapper(wsapiTypeToken, domainTypeToken, resultProperty);
            } catch (IntrospectionException e) {
                //noinspection unchecked
                return new InlineTransferTypeMapper(wsapiTypeToken, domainTypeToken);
            }
        } else {
            return null;
        }
    }

    public static class InlineTransferTypeMapper<WsapiT, ResultT, DomainT extends Transfer<ResultT>> extends AbstractTypeMapper<WsapiT, DomainT, Mapping> {
        private final TypeToken<ResultT> resultTypeToken;
        private final DefaultTypeMapper<WsapiT, ResultT, Mapping> resultMapper;
        private final PropertyDescriptor bubbleObjectsProperty;
        private final Constructor<DomainT> transferConstructor;

        public InlineTransferTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
            //noinspection unchecked
            super((Class<WsapiT>) wsapiTypeToken.getRawType(), (Class<DomainT>) domainTypeToken.getRawType(), Mapping.class);

            TypeToken<? super DomainT> transferTypeToken = domainTypeToken.getSupertype(Transfer.class);
            Type transferType = transferTypeToken.getType();
            if (transferType instanceof Class) {
                // Isj
                resultTypeToken = (TypeToken) TypeToken.of(Object.class);
            } else {
                ParameterizedType parameterizedType = (ParameterizedType) transferType;
                //noinspection unchecked
                resultTypeToken = (TypeToken) TypeToken.of(parameterizedType.getActualTypeArguments()[0]);
            }

            try {
                // Første parameter er Object pga. type erasure
                transferConstructor = (Constructor<DomainT>) domainTypeToken.getRawType().getConstructor(Object.class, Iterable.class);
            } catch (NoSuchMethodException e) {
                throw new MappingException("No suitable constructor for " + domainTypeToken.getRawType());
            }

            resultMapper = new DefaultTypeMapper<WsapiT, ResultT, Mapping>(wsapiTypeToken, resultTypeToken, Mapping.class, Collections.<Class<?>>emptySet(), true) {
                @Override
                protected Collection<Method> findGetters(Class<?> c) {
                    Collection<Method> getters = super.findGetters(c);
                    if (c.equals(getWsapiClass())) {
                        for (Iterator<Method> iterator = getters.iterator(); iterator.hasNext(); ) {
                            Method method = iterator.next();
                            if (method.getName().equals("getBubbleObjects")) {
                                iterator.remove();
                            }
                        }
                    }
                    return getters;
                }
            };

            try {
                bubbleObjectsProperty = new PropertyDescriptor("bubbleObjects", wsapiTypeToken.getRawType());
            } catch (IntrospectionException e) {
                throw new MappingException("Unable to find bubbleObjects on " + wsapiTypeToken.getRawType().getName(), e);
            }
        }

        @Override
        public void setMapping(Mapping mapping) {
            super.setMapping(mapping);
            resultMapper.setMapping(mapping);
        }

        @Override
        public WsapiT mapDomainObject(DomainT source) {
            WsapiT target = resultMapper.mapDomainObject(source.getResult());

            Object bubbleObjectList = getMapping().d2w(source.getBubbleObjects().values(), bubbleObjectsProperty.getPropertyType());

            try {
                bubbleObjectsProperty.getWriteMethod().invoke(target, bubbleObjectList);
            } catch (IllegalAccessException e) {
                throw new MappingException("Error during writing to " + bubbleObjectsProperty.getWriteMethod().toGenericString(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Error during writing to " + bubbleObjectsProperty.getWriteMethod().toGenericString(), e);
            }

            return target;
        }

        @Override
        public DomainT mapWsapiObject(WsapiT source) {
            ResultT result = resultMapper.mapWsapiObject(source);

            Object bubbleObjectList;

            try {
                bubbleObjectList = bubbleObjectsProperty.getReadMethod().invoke(source);
            } catch (IllegalAccessException e) {
                throw new MappingException("Error during reading from " + bubbleObjectsProperty.getReadMethod().toGenericString(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Error during reading from " + bubbleObjectsProperty.getReadMethod().toGenericString(), e);
            }

            LinkedHashSet bubbleObjects = getMapping().w2d(bubbleObjectList, LinkedHashSet.class);

            try {
                return transferConstructor.newInstance(result, bubbleObjects);
            } catch (InstantiationException e) {
                throw new MappingException(e);
            } catch (IllegalAccessException e) {
                throw new MappingException(e);
            } catch (InvocationTargetException e) {
                throw new MappingException(e);
            }
        }
    }

    public static class ExternalTransferTypeMapper<WsapiT, ResultT, DomainT extends Transfer<ResultT>> extends AbstractTypeMapper<WsapiT, DomainT, Mapping> {
        private final TypeToken<ResultT> resultTypeToken;
        private final PropertyDescriptor bubbleObjectsProperty;
        private final PropertyDescriptor resultProperty;
        private final Constructor<DomainT> transferConstructor;

        public ExternalTransferTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<Transfer<ResultT>> domainTypeToken, PropertyDescriptor resultProperty) {
            //noinspection unchecked
            super((Class<WsapiT>) wsapiTypeToken.getRawType(), (Class<DomainT>) domainTypeToken.getRawType(), Mapping.class);
            this.resultProperty = resultProperty;

            TypeToken<? super DomainT> transferTypeToken = domainTypeToken.getSupertype(Transfer.class);
            Type transferType = transferTypeToken.getType();
            if (transferType instanceof Class) {
                // Isj
                resultTypeToken = (TypeToken) TypeToken.of(Object.class);
            } else {
                ParameterizedType parameterizedType = (ParameterizedType) transferType;
                //noinspection unchecked
                resultTypeToken = (TypeToken) TypeToken.of(parameterizedType.getActualTypeArguments()[0]);
            }

            Constructor<DomainT> transferConstructor;
            try {
                transferConstructor = (Constructor<DomainT>) domainTypeToken.getRawType().getConstructor(resultTypeToken.getRawType(), Iterable.class);
            } catch (NoSuchMethodException ignore) {
                try {
                    // Første parameter er sannsynligvis Object pga. type erasure
                    transferConstructor = (Constructor<DomainT>) domainTypeToken.getRawType().getConstructor(Object.class, Iterable.class);
                } catch (NoSuchMethodException e) {
                    throw new MappingException("No suitable constructor for " + domainTypeToken.getRawType());
                }
            }
            this.transferConstructor = transferConstructor;

            try {
                bubbleObjectsProperty = new PropertyDescriptor("bubbleObjects", wsapiTypeToken.getRawType());
            } catch (IntrospectionException e) {
                throw new MappingException("Unable to find bubbleObjects on " + wsapiTypeToken.getRawType().getName(), e);
            }
        }

        @Override
        public void setMapping(Mapping mapping) {
            super.setMapping(mapping);
        }

        @Override
        public WsapiT mapDomainObject(DomainT source) {
            WsapiT target = createWsapiT();
            
            Object targetResult = getMapping().d2w(source.getResult(), resultProperty.getPropertyType());

            try {
                resultProperty.getWriteMethod().invoke(target, targetResult);
            } catch (IllegalAccessException e) {
                throw new MappingException("Error during writing to " + resultProperty.getWriteMethod().toGenericString(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Error during writing to " + resultProperty.getWriteMethod().toGenericString(), e);
            }

            Object bubbleObjectList = getMapping().d2w(source.getBubbleObjects().values(), bubbleObjectsProperty.getPropertyType());

            try {
                bubbleObjectsProperty.getWriteMethod().invoke(target, bubbleObjectList);
            } catch (IllegalAccessException e) {
                throw new MappingException("Error during writing to " + bubbleObjectsProperty.getWriteMethod().toGenericString(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Error during writing to " + bubbleObjectsProperty.getWriteMethod().toGenericString(), e);
            }

            return target;
        }

        @Override
        public DomainT mapWsapiObject(WsapiT source) {
            Object resultSource;
            
            try {
                resultSource = resultProperty.getReadMethod().invoke(source);
            } catch (IllegalAccessException e) {
                throw new MappingException("Error during reading from " + resultProperty.getReadMethod().toGenericString(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Error during reading from " + resultProperty.getReadMethod().toGenericString(), e);
            }

            Object bubbleObjectList;

            try {
                bubbleObjectList = bubbleObjectsProperty.getReadMethod().invoke(source);
            } catch (IllegalAccessException e) {
                throw new MappingException("Error during reading from " + bubbleObjectsProperty.getReadMethod().toGenericString(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Error during reading from " + bubbleObjectsProperty.getReadMethod().toGenericString(), e);
            }

            ResultT result = (ResultT) getMapping().w2d(resultSource, TypeLiteral.get(resultTypeToken.getType()));
            LinkedHashSet bubbleObjects = getMapping().w2d(bubbleObjectList, LinkedHashSet.class);

            try {
                return transferConstructor.newInstance(result, bubbleObjects);
            } catch (InstantiationException e) {
                throw new MappingException(e);
            } catch (IllegalAccessException e) {
                throw new MappingException(e);
            } catch (InvocationTargetException e) {
                throw new MappingException(e);
            }
        }
    }
}
