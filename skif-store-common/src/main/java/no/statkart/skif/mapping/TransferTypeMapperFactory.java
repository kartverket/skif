package no.statkart.skif.mapping;

import com.google.common.reflect.TypeToken;
import no.statkart.skif.mapper.*;
import no.statkart.skif.store.Transfer;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
        if (domainTypeToken.getRawType().equals(Transfer.class)) {
            //noinspection unchecked
            return new TransferTypeMapper(wsapiTypeToken, domainTypeToken);
        } else {
            return null;
        }
    }

    public static class TransferTypeMapper<WsapiT, ResultT> extends AbstractTypeMapper<WsapiT, Transfer<ResultT>, Mapping> {
        private final TypeToken<ResultT> resultTypeToken;
        private final DefaultTypeMapper<WsapiT, ResultT, Mapping> resultMapper;
        private final PropertyDescriptor bubbleObjectsProperty;

        public TransferTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<Transfer<ResultT>> domainTypeToken) {
            //noinspection unchecked
            super((Class<WsapiT>) wsapiTypeToken.getRawType(), (Class<Transfer<ResultT>>) domainTypeToken.getRawType(), Mapping.class);

            Type domainType = domainTypeToken.getType();
            if (domainType instanceof Class) {
                // Isj
                resultTypeToken = (TypeToken) TypeToken.of(Object.class);
            } else {
                ParameterizedType parameterizedType = (ParameterizedType) domainType;
                //noinspection unchecked
                resultTypeToken = (TypeToken) TypeToken.of(parameterizedType.getActualTypeArguments()[0]);
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
        public WsapiT mapDomainObject(Transfer<ResultT> source) {
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
        public Transfer<ResultT> mapWsapiObject(WsapiT source) {
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

            return new Transfer<ResultT>(result, bubbleObjects);
        }
    }
}
