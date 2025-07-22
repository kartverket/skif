package no.statkart.skif.mapping;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;
import com.google.inject.TypeLiteral;
import no.statkart.skif.mapper.AbstractTypeMapper;
import no.statkart.skif.mapper.DefaultTypeMapper;
import no.statkart.skif.mapper.Mapping;
import no.statkart.skif.mapper.MappingException;
import no.statkart.skif.mapper.TypeMapper;
import no.statkart.skif.mapper.TypeMapperFactory;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.BubbleId;
import no.statkart.skif.store.Transfer;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * TypeMapperFactory for {@link Transfer}.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class TransferTypeMapperFactory implements TypeMapperFactory {
    private final Provider<? extends ServiceContext> serviceContextProvider;

    public TransferTypeMapperFactory() {
        serviceContextProvider = null;
    }

    public TransferTypeMapperFactory(Provider<? extends ServiceContext> serviceContextProvider) {
        this.serviceContextProvider = serviceContextProvider;
    }

    @Override
    public <WsapiT, DomainT> TypeMapper createTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        if (Transfer.class.isAssignableFrom(domainTypeToken.getRawType())) {
            // Sjekk om transfer er inline eller ikke

            TypeToken<?> transferTypeToken = ((TypeToken<? extends Transfer<?>>) domainTypeToken).getSupertype(Transfer.class);
            Type transferType = transferTypeToken.getType();
            final TypeToken<?> resultTypeToken;
            if (transferType instanceof Class) {
                // Isj
                resultTypeToken = TypeToken.of(Object.class);
            } else {
                ParameterizedType parameterizedType = (ParameterizedType) transferType;
                resultTypeToken = TypeToken.of(parameterizedType.getActualTypeArguments()[0]);
            }

            if (BubbleId.class.isAssignableFrom(resultTypeToken.getRawType()) || Collection.class.isAssignableFrom(resultTypeToken.getRawType())) {

                return new ExternalTransferTypeMapper(wsapiTypeToken, domainTypeToken, resultTypeToken);
            } else {
                //noinspection unchecked
                return new InlineTransferTypeMapper(wsapiTypeToken, domainTypeToken, resultTypeToken, serviceContextProvider);
            }
        } else {
            return null;
        }
    }

    public static class InlineTransferTypeMapper<WsapiT, ResultT, DomainT extends Transfer<ResultT>> extends AbstractTypeMapper<WsapiT, DomainT, Mapping> {
        private final DefaultTypeMapper<WsapiT, ResultT, Mapping> resultMapper;
        private final PropertyDescriptor bubbleObjectsProperty;
        private final Constructor<DomainT> transferConstructor;

        public InlineTransferTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken, TypeToken<ResultT> resultTypeToken, Provider<ServiceContext> serviceContextProvider) {
            //noinspection unchecked
            super((Class<WsapiT>) wsapiTypeToken.getRawType(), (Class<DomainT>) domainTypeToken.getRawType(), Mapping.class);

            try {
                // Første parameter er Object pga. type erasure
                transferConstructor = (Constructor<DomainT>) domainTypeToken.getRawType().getConstructor(Object.class, Iterable.class);
            } catch (NoSuchMethodException e) {
                throw new MappingException("No suitable constructor for " + domainTypeToken.getRawType());
            }

            resultMapper = new DefaultTypeMapper<WsapiT, ResultT, Mapping>(wsapiTypeToken, resultTypeToken, Mapping.class, Collections.emptySet(), true, serviceContextProvider) {
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
            } catch (IllegalAccessException | InvocationTargetException e) {
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
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new MappingException("Error during reading from " + bubbleObjectsProperty.getReadMethod().toGenericString(), e);
            }

            LinkedHashSet<?> bubbleObjects = getMapping().w2d(bubbleObjectList, LinkedHashSet.class);

            try {
                return transferConstructor.newInstance(result, bubbleObjects);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new MappingException(e);
            }
        }
    }

    public static class ExternalTransferTypeMapper<WsapiT, ResultT, DomainT extends Transfer<ResultT>> extends AbstractTypeMapper<WsapiT, DomainT, Mapping> {
        private final TypeToken<ResultT> resultTypeToken;
        private final PropertyDescriptor bubbleObjectsProperty;
        private final PropertyDescriptor resultProperty;
        private final Constructor<DomainT> transferConstructor;

        public ExternalTransferTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<Transfer<ResultT>> domainTypeToken, TypeToken<ResultT> resultTypeToken) {
            //noinspection unchecked
            super((Class<WsapiT>) wsapiTypeToken.getRawType(), (Class<DomainT>) domainTypeToken.getRawType(), Mapping.class);
            this.resultTypeToken = resultTypeToken;

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

            PropertyDescriptor bubbleObjectsProperty = null;
            List<PropertyDescriptor> resultPropertyCandidates = new ArrayList<PropertyDescriptor>(1);
            try {
                BeanInfo beanInfo = Introspector.getBeanInfo(getWsapiClass(), getWsapiClass().getSuperclass());
                for (PropertyDescriptor propertyDescriptor : beanInfo.getPropertyDescriptors()) {
                    if (propertyDescriptor.getName().equals("bubbleObjects")) {
                        bubbleObjectsProperty = propertyDescriptor;
                    } else {
                        resultPropertyCandidates.add(propertyDescriptor);
                    }
                }
            } catch (IntrospectionException e) {
                throw new MappingException("Unable to introspect " + getWsapiClass());
            }

            if (bubbleObjectsProperty == null) {
                throw new MappingException("Unable to find bubbleObjects on " + wsapiTypeToken.getRawType().getName());
            }
            this.bubbleObjectsProperty = bubbleObjectsProperty;

            if (resultPropertyCandidates.size() == 1) {
                resultProperty = resultPropertyCandidates.get(0);
            } else {
                throw new MappingException("Unable to find result property on " + wsapiTypeToken.getRawType().getName() + " (" + resultPropertyCandidates.size() + " candidates)");
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
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new MappingException("Error during writing to " + resultProperty.getWriteMethod().toGenericString(), e);
            }

            Object bubbleObjectList = getMapping().d2w(source.getBubbleObjects().values(), bubbleObjectsProperty.getPropertyType());

            try {
                bubbleObjectsProperty.getWriteMethod().invoke(target, bubbleObjectList);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new MappingException("Error during writing to " + bubbleObjectsProperty.getWriteMethod().toGenericString(), e);
            }

            return target;
        }

        @Override
        public DomainT mapWsapiObject(WsapiT source) {
            Object resultSource;
            
            try {
                resultSource = resultProperty.getReadMethod().invoke(source);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new MappingException("Error during reading from " + resultProperty.getReadMethod().toGenericString(), e);
            }

            Object bubbleObjectList;

            try {
                bubbleObjectList = bubbleObjectsProperty.getReadMethod().invoke(source);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new MappingException("Error during reading from " + bubbleObjectsProperty.getReadMethod().toGenericString(), e);
            }

            ResultT result = (ResultT) getMapping().w2d(resultSource, TypeLiteral.get(resultTypeToken.getType()));
            LinkedHashSet<?> bubbleObjects = getMapping().w2d(bubbleObjectList, LinkedHashSet.class);

            try {
                return transferConstructor.newInstance(result, bubbleObjects);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new MappingException(e);
            }
        }
    }
}
