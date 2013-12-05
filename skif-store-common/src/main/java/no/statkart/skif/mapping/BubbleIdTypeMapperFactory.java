package no.statkart.skif.mapping;

import com.google.common.reflect.TypeToken;
import com.google.inject.Provider;
import no.statkart.skif.exception.ImplementationException;
import no.statkart.skif.mapper.*;
import no.statkart.skif.service.ServiceContext;
import no.statkart.skif.store.AbstractBubbleId;
import no.statkart.skif.store.BubbleIds;
import no.statkart.skif.store.SnapshotVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * Dynamisk oppretting av BubbleIdTypeMapper.
 *
 * @author Tor Egil R. Strand
 * @since 2.4.0
 */
public class BubbleIdTypeMapperFactory implements TypeMapperFactory {
    private final Class<?> wsapiBaseClass;
    private final Provider<SnapshotVersion> snapshotVersionProvider;

    public BubbleIdTypeMapperFactory(Class<?> wsapiBaseClass, Provider<SnapshotVersion> snapshotVersionProvider) {
        this.wsapiBaseClass = wsapiBaseClass;
        this.snapshotVersionProvider = snapshotVersionProvider;
    }

    @Override
    public <WsapiT, DomainT> TypeMapper createTypeMapper(TypeToken<WsapiT> wsapiTypeToken, TypeToken<DomainT> domainTypeToken) {
        if (wsapiBaseClass.isAssignableFrom(wsapiTypeToken.getRawType()) && AbstractBubbleId.class.isAssignableFrom(domainTypeToken.getRawType())) {
            //noinspection unchecked
            return new BubbleIdTypeMapper(wsapiTypeToken.getRawType(), domainTypeToken.getRawType(), snapshotVersionProvider);
        }
        return null;
    }

    /**
     * @author Henrik Fredholm
     * @author Tor Egil R. Strand
     * @since 2.4.0
     */
    public static class BubbleIdTypeMapper<WsapiT, DomainT extends AbstractBubbleId> extends AbstractTypeMapper<WsapiT, DomainT, Mapping> {
        protected static final Logger logger = LoggerFactory.getLogger(BubbleIdTypeMapper.class);

        private final PropertyDescriptor valueProperty;
        private final Provider<SnapshotVersion> snapshotVersionProvider;

        public BubbleIdTypeMapper(Class<WsapiT> wsapiClass, Class<DomainT> domainClass, Provider<SnapshotVersion> snapshotVersionProvider) {
            super(wsapiClass, domainClass, Mapping.class);
            this.snapshotVersionProvider = snapshotVersionProvider;

            try {
                valueProperty = new PropertyDescriptor("value", wsapiClass);
            } catch (IntrospectionException e) {
                throw new ImplementationException(wsapiClass + " is missing essential properties", e);
            }
        }

        @Override
        public WsapiT mapDomainObject(DomainT source) {
            if (!source.getSnapshotVersion().equals(snapshotVersionProvider.get())) {
                throw new ImplementationException("Illegal request to map id with snapshot version (" + source.getSnapshotVersion() + ") different from context (" + snapshotVersionProvider.get() + ")");
            }

            WsapiT target = createWsapiT();
            try {
                valueProperty.getWriteMethod().invoke(target, source.getStringValue());
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
            try {
                idValue = (String) valueProperty.getReadMethod().invoke(source);
            } catch (IllegalAccessException e) {
                throw new MappingException("Unable to read properties on " + getWsapiClass(), e);
            } catch (InvocationTargetException e) {
                throw new MappingException("Unable to read properties on " + getWsapiClass(), e.getTargetException());
            }

            Class valueType = BubbleIds.getValueType(getDomainClass());
            SnapshotVersion snapshotVersion = snapshotVersionProvider.get();
            if (valueType == Long.class) {
                target = BubbleIds.createInstance(getDomainClass(), Long.valueOf(idValue), snapshotVersion);
            } else {
                target = BubbleIds.createInstance(getDomainClass(), idValue, snapshotVersion);
            }
            return target;
        }
    }

}