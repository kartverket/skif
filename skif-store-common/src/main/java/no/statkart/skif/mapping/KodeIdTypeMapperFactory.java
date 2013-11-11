package no.statkart.skif.mapping;

import com.google.common.reflect.TypeToken;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mapper.*;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import no.statkart.skif.store.kodeliste.KodeId;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * Dynamisk oppretting av KodeIdTypeMapper.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class KodeIdTypeMapperFactory implements TypeMapperFactory {
    private final Class<?> wsapiBaseClass;

    public KodeIdTypeMapperFactory(Class<?> wsapiBaseClass) {
        this.wsapiBaseClass = wsapiBaseClass;
    }

    @Override
    public <WsapiT, DomainT> TypeMapper createTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        if (wsapiBaseClass.isAssignableFrom(wsapiTypeToken.getRawType()) && KodeId.class.isAssignableFrom(domainTypeToken.getRawType())) {
            //noinspection unchecked
            return new KodeIdTypeMapper(wsapiTypeToken.getRawType(), domainTypeToken.getRawType());
        }
        return null;
    }

    /**
     * @author Henrik Fredholm
     * @author Tor Egil R. Strand
     * @since 2.4.0
     */
    public static class KodeIdTypeMapper<WsapiT, DomainT extends KodeId> extends AbstractTypeMapper<WsapiT, DomainT, Mapping> {
        private final PropertyDescriptor valueProperty;
        private final PropertyDescriptor snapshotVersionProperty;

        public KodeIdTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass) {
            super(wsapiClass, domainClass, Mapping.class);

            try {
                valueProperty = new PropertyDescriptor("value", wsapiClass);
                snapshotVersionProperty = new PropertyDescriptor("snapshotVersion", wsapiClass);
            } catch (IntrospectionException e) {
                throw new ImplementationException(wsapiClass + " is missing essential properties", e);
            }
        }

        @Override
        public WsapiT mapDomainObject(DomainT source) {
            WsapiT target = createWsapiT();
            try {
                valueProperty.getWriteMethod().invoke(target, source.getStringValue());
                snapshotVersionProperty.getWriteMethod().invoke(target, getMapping().d2w(source.getSnapshotVersion(), snapshotVersionProperty.getPropertyType()));
            } catch (IllegalAccessException e) {
                throw new MappingException("Unable to set properties on " + getWsapiClass(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Unable to set properties on " + getWsapiClass(), e.getTargetException());
            }
            return target;
        }

        @Override
        public DomainT mapWsapiObject(WsapiT source) {
            DomainT target;

            final String idValue;
            final SnapshotVersion snapshotVersion;
            try {
                idValue = (String) valueProperty.getReadMethod().invoke(source);
                snapshotVersion = getMapping().w2d(snapshotVersionProperty.getReadMethod().invoke(source), SnapshotVersion.class);
            } catch (IllegalAccessException e) {
                throw new MappingException("Unable to read properties on " + getWsapiClass(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Unable to read properties on " + getWsapiClass(), e.getTargetException());
            }

            Class valueType = BubbleIds.getValueType(getDomainClass());
            if (valueType == Long.class) {
                target = BubbleIds.createInstance(getDomainClass(), Long.valueOf(idValue), snapshotVersion);
            } else {
                target = BubbleIds.createInstance(getDomainClass(), idValue, snapshotVersion);
            }
            return target;
        }
    }

}